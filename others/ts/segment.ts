
import * as ort from 'onnxruntime-web';
import * as tf from '@tensorflow/tfjs'
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { captureFrame, toBase64 } from '@/utils/videoFrame'
import { BlockItem, ModelMapType, ConfigType, UploadType, FrameItem, BlockItemType, PointOperaType, SegmentOperaType } from "./types/segment.d.js"
import dayjs from 'dayjs';
import ColorMaker from '@/utils/colorUtils.js';
import { createModelMap } from '~/env.config';

const MODEL_WIDTH = 1024;
const MODEL_HEIGHT = 684;

const globalModelMap = createModelMap(import.meta.env)
const MODEL_MAP: ModelMapType = globalModelMap.onnx
const modelSess: ort.InferenceSession[] = []

function getConfig(): ConfigType {
    const data: ConfigType = {
        model: "mobile_sam",
        provider: { name: "wasm" },//wasm，webgpu,webnn
        device: "cpu",
        threads: 4,
        clear_cache: false
    };
    ort.env.wasm.numThreads = data.threads;
    return data;
}


ort.env.wasm.simd = true;
// ort.env.wasm.proxy = true;
ort.env.wasm.wasmPaths = globalModelMap.wasm as ort.Env.WasmPrefixOrFilePaths;

const config = getConfig();

/**
 * fetch and cache url
 */
async function fetchAndCache(url: string) {
    try {
        const cache = await caches.open("onnx");
        if (config.clear_cache) {
            cache.delete(url);
        }
        let cachedResponse = await cache.match(url);
        if (cachedResponse == undefined) {
            await cache.add(url);
            cachedResponse = await cache.match(url);
            // console.log(`${url} (from network)`);
        } else {
            // console.log(`${url} (from cache)`);
        }
        const data = await cachedResponse?.arrayBuffer();
        return data
    } catch (error) {
        // console.log(`${url} (from network)`);
        return await fetch(url).then(response => {
            //   {
            //   mode: "cors",
            //   headers: {
            //     "Cross-Origin-Embedder-Policy": "require-corp",
            //     "Cross-Origin-Opener-Policy": "same-origin"
            //   },
            //   referrerPolicy: "same-origin",
            //   credentials: "same-origin"
            // }
            // response.headers.set("Cross-Origin-Embedder-Policy", "require-corp");
            // response.headers.set("Cross-Origin-Opener-Policy", "same-origin");
            return response.arrayBuffer()
        });
    }
}

function loadModel(model: string[], idx: number) {
    const provider = config.provider

    switch (config.provider.name) {
        case "webnn":
            if (!("ml" in navigator)) {
                throw new Error("webnn is NOT supported");
            }
            provider.deviceType = config.device;
            provider.powerPreference = 'default'
            break;
        case "webgpu":
            if (!navigator.gpu) {
                throw new Error("webgpu is NOT supported");
            }
            break;
    }

    const opt = { executionProviders: [provider] };

    fetchAndCache(model[idx]).then(async (data: any) => {
        // sess[idx] = ort.InferenceSession.create('./models/sam_vit_b_01ec64.encoder.onnx');
        // const u8data = new Uint8Array(data);
        modelSess[idx] = await ort.InferenceSession.create(data, opt);
        // console.log(`${model[idx]} loaded.`);
        // sess[idx].then(() => {
        //   console.log(`${model[idx]} loaded.`);
        if (idx == 0) {
            loadModel(model, 1);
        }
        // }, (e) => {
        //   console.log(`${model[idx]} failed with ${e}.`);
        //   throw e;

        // });
    })
}


export default function useSegment() {

    onMounted(() => {
        const model: string[] = MODEL_MAP[config.model as keyof ModelMapType];
        loadModel(model, 0);
    })
    onUnmounted(() => {
        modelSess.forEach((item) => {
            if (item != null) {
                item.release();
            }
        })
    })
    const frameList = reactive<FrameItem[]>([]);
    const frameIndex = ref(0);
    const blockList = reactive<BlockItem[]>([]);
    const blockIndex = ref(0);
    const videoUploadRef = ref();
    const videoUploadLoading = ref(false);
    const fileBase64 = ref('');
    const frameBase64 = ref('');
    const canvasRef = ref();
    const previewRef = ref();
    let colorIdx = 1, MAX_WIDTH: number = 1000, MAX_HEIGHT: number = 360
    const _useBlocks = useBlocks()
    const _useModel = useModel()
    const _useMediaUpload = useMediaUpload()
    const _useFrames = useFrames();

    // 当前帧的图像数据
    let imageImageData: ImageData | undefined, imageEmbeddings: ort.Tensor;

    function useFrames() {
        function frameAddHandle(addIndex: boolean = true, obj: { sec: number, type: number, base64: string, imageData?: ImageData, imageEmbeddings?: ort.Tensor }) {
            frameList.push({
                id: String(dayjs().valueOf()) + Math.random() * (100 - 1) + 1,
                type: obj.type,
                sec: obj.sec,
                base64: obj.base64,
                imageData: obj.imageData,
                imageEmbeddings: obj.imageEmbeddings
            });
            if (addIndex) { frameIndex.value += 1; }
        }
        function frameDelHandle() {

        }
        return {
            frameAddHandle,
            frameDelHandle
        }
    }

    function useBlocks() {
        const blockShowAll = ref(false); // true 展示全部，false 展示一个
        function blockAddHandle(obj: { type?: BlockItemType }, addIndex: boolean = true) {
            blockShowAll.value = false;
            blockList.push({
                id: String(dayjs().valueOf()) + Math.random() * (100 - 1) + 1,
                type: obj.type || 'mask',
                hasPoint: false,
                color: ColorMaker.getRgbColorArr(colorIdx),
                pointModel: [],
                pointXY: [],
                resource: '',
                resourceType: '',
                imgBase64: '',
                imgData: undefined,
                result: null,
                zIndex: 100 - blockIndex.value,
                segmentImage: '',
                segmentImageBase64: '',
                segmentImageBitmap: null
            });
            colorIdx++;
            if (addIndex) { blockIndex.value += 1; blockShowHandle(blockIndex.value); }

        }
        function blockDelHandle(index: number) {
            if (blockList.length === 1) {
                window.$message?.warning('请保留一层蒙版');
            } else {
                blockList.splice(index, 1);
                if (blockIndex.value >= blockList.length) {
                    blockIndex.value = blockList.length - 1;
                }
            }
            blockShowHandle();
        }

        function blockShowHandle(index: number = -1) {
            const canvas = canvasRef.value
            const ctx = canvas.getContext('2d');
            ctx.globalAlpha = 0.5;
            ctx.clearRect(0, 0, canvas.width, canvas.height);


            // todo: 等后面换成多帧后，需要从对应获取
            ctx.putImageData(imageImageData, 0, 0, 0, 0, canvas.width, canvas.height);
            if (index >= 0) {
                blockIndex.value = index;
                blockShowAll.value = false;
                const block = blockList[index];
                ctx.drawImage(block.segmentImageBitmap, 0, 0);
            } else {
                blockShowAll.value = true;
                for (let i = blockList.length - 1; i >= 0; i--) {
                    const block = blockList[i];
                    ctx.drawImage(block.segmentImageBitmap, 0, 0);
                }
            }
        }

        return {
            blockList,
            blockIndex,
            blockShowAll,
            blockAddHandle,
            blockDelHandle,
            blockShowHandle,
        }
    }

    function useMediaUpload() {
        const uploadType: UploadType = {
            type: 1,
            blockIndex: 0,
        };

        async function uploadHandle(type: number, bIndex: number = 0) {
            const vur = videoUploadRef.value;
            uploadType.type = type;
            uploadType.blockIndex = bIndex;
            switch (type) {
                case 1:
                    vur.accept = 'video/*'
                    break;
                case 2:
                    vur.accept = 'video/*,image/*'
                    break;
                case 3:
                    vur.accept = 'video/*'
            }
            vur.onchange = uploadVideo;
            vur.click();
        }

        async function uploadVideo() {
            if (uploadType.type === 1) {
                uploadMedia((img) => {
                    _useBlocks.blockAddHandle({ type: "mask" }, false);
                    _useModel.imageHandle(img, 'frame')
                })
            } else if (uploadType.type === 2) {
                uploadResource(uploadType.blockIndex)
            } else if (uploadType.type === 3) {
                uploadMedia((img) => {
                    // _useBlocks.blockAddHandle(false);
                    // _useModel.imageHandle(img)
                    _useModel.imageRenderHandle(img);
                })
            }
        }
        async function uploadMedia(callback: (img: HTMLImageElement) => void) {
            const vur = videoUploadRef.value;
            const file: File = vur.files[0];
            if (file) {
                frameList.length = 0;
                frameIndex.value = 0
                blockList.length = 0;
                blockIndex.value = 0;
                videoUploadLoading.value = true
                const img = new Image()
                if (file.type.startsWith("image/")) {
                    const base64 = await toBase64(file)
                    img.src = base64
                    fileBase64.value = img.src
                    frameBase64.value = img.src
                } else {
                    const info = await captureFrame(file, 1);
                    img.src = await toBase64(info.blob as Blob)
                    fileBase64.value = await toBase64(file);
                    frameBase64.value = img.src
                }
                _useFrames.frameAddHandle(false, {
                    sec: 1,
                    type: uploadType.type,
                    base64: frameBase64.value,
                })
                videoUploadLoading.value = false
                if (img.complete) {
                    if (callback != null) {
                        callback(img)
                    }
                } else {
                    img.onload = () => {
                        if (callback != null) {
                            callback(img)
                        }
                    }
                }
                vur.value = '';
            }
        }

        async function uploadResource(index: number) {
            if (index > blockList.length || index < 0) {
                return
            }
            const vur = videoUploadRef.value;
            const file: File = vur.files[0];
            if (file) {
                const item = blockList[index];
                item.resource = await toBase64(file);
                if (file.type.startsWith('image/')) {
                    item.resourceType = 'image';
                } else if (file.type.startsWith('video/')) {
                    item.resourceType = 'video';
                }
                vur.value = '';
            }
        }
        return {
            videoUploadRef,
            videoUploadLoading,
            fileBase64,
            frameBase64,
            uploadMedia,
            uploadResource,
            uploadHandle
        }
    }

    function useModel() {
        let pointOpera: PointOperaType = 'plus';
        let segmentOpera: SegmentOperaType = 'mask'

        async function imageRenderHandle(img: HTMLImageElement) {
            let renderLoading = videoUploadLoading.value ? 1 : 0;
            if (renderLoading === 0) {
                videoUploadLoading.value = true
            }
            let width = img.width;
            let height = img.height;

            if (width > MAX_WIDTH) {
                height = height * (MAX_WIDTH / width);
                width = MAX_WIDTH;
            }
            // } else {
            if (height > MAX_HEIGHT) {
                width = width * (MAX_HEIGHT / height);
                height = MAX_HEIGHT;
            }
            // }

            width = Math.round(width);
            height = Math.round(height);


            const canvas: HTMLCanvasElement = canvasRef.value;
            canvas.width = width;
            canvas.height = height;
            let context = canvas.getContext('2d');
            context?.drawImage(img, 0, 0, width, height);
            if (renderLoading === 0) {
                videoUploadLoading.value = false
            }
        }

        async function imageHandle(img: HTMLImageElement, type: "block" | "frame") {
            imageRenderHandle(img);
            videoUploadLoading.value = true;

            const frame = frameList[frameIndex.value]

            const block = blockList[blockIndex.value];
            if (block.imgBase64 === '') {
                block.imgBase64 = frameBase64.value;
            }

            const canvas: HTMLCanvasElement = canvasRef.value;
            let width = canvas.width
            let height = canvas.height
            let context = canvas.getContext('2d');

            setTimeout(async () => {
                imageImageData = context?.getImageData(0, 0, width, height);
                if (!frame.imageData) {
                    frame.imageData = imageImageData
                }

                let resizeTensor = await ort.Tensor.fromImage(imageImageData as unknown as ImageBitmap, { resizedWidth: MODEL_WIDTH, resizedHeight: MODEL_HEIGHT })
                let tfTensor = tf.tensor(resizeTensor.data, [...resizeTensor.dims]);
                tfTensor = tfTensor.reshape([3, MODEL_HEIGHT, MODEL_WIDTH]);
                tfTensor = tfTensor.transpose([1, 2, 0]).mul(255)
                const imageDataTensor = new ort.Tensor(tfTensor.dataSync(), tfTensor.shape);
                let start = Date.now();
                const feed = { "input_image": imageDataTensor };
                const res = await modelSess[0].run(feed);
                let end = Date.now();
                let time_taken = (end - start) / 1000;
                console.log(`Computing image embedding took ${time_taken} seconds`);

                imageEmbeddings = res.image_embeddings;
                if (type === "block") {
                    block.imageEmbeddings = res.image_embeddings;
                } else if (type === 'frame') {
                    frame.imageEmbeddings = res.image_embeddings;
                }
                videoUploadLoading.value = false;
            }, 500)
        }


        async function clickHandle(e: MouseEvent, label: number, fn?: (imgB64: string) => void) {
            // const originWidth = imageFrameRef.value.naturalWidth;
            // const offseWidth = imageFrameRef.value.offsetWidth;
            // const xscale = offseWidth / originWidth;

            // const originHeight = imageFrameRef.value.naturalHeight;
            // const offsetHeight = imageFrameRef.value.offsetHeight;
            // const yscale = offsetHeight / originHeight;

            // 获取x,y坐标
            // const x = Math.floor(e.offsetX / xscale);
            // const y = Math.floor(e.offsetY / yscale);

            // console.log(image_embeddings)
            // if (image_embeddings === undefined) {
            //   await sess[0];
            // }
            // const emb = await image_embeddings;

            const canvas = canvasRef.value
            const ctx = canvas.getContext('2d');

            const rect = canvas.getBoundingClientRect();
            const x = Math.floor(e.clientX - rect.left);
            const y = Math.floor(e.clientY - rect.top);
            console.log(x, y)

            const frame = frameList[frameIndex.value]
            const block = blockList[blockIndex.value];

            if (pointOpera === 'plus') {
                block.pointModel.push(label);
                block.pointXY.push(x, y);
            } else {
                block.pointModel = [label];
                block.pointXY = [x, y];
            }
            // canvas.width = imageImageData.width;
            // canvas.height = imageImageData.height;
            // console.log(canvas.width, canvas.height);

            ctx.clearRect(0, 0, canvas.width, canvas.height);

            ctx.putImageData(imageImageData, 0, 0, 0, 0, canvas.width, canvas.height);
            // ctx.fillStyle = 'blue';
            // ctx.fillRect(x, y, 10, 10);

            // const labels = [label]
            const labels = block.pointModel;
            const points = block.pointXY;

            const pointCoords = new ort.Tensor(new Float32Array(points), [1, points.length / 2, 2]);
            const pointLabels = new ort.Tensor(new Float32Array(labels), [1, labels.length]);
            const maskInput = new ort.Tensor(new Float32Array(256 * 256), [1, 1, 256, 256]);
            const hasMask = new ort.Tensor(new Float32Array([0]), [1,]);
            const origianlImageSize = new ort.Tensor(new Float32Array([MODEL_HEIGHT, MODEL_WIDTH]), [2,]);

            // const t = new ort.Tensor(emb.image_embeddings.type, Float32Array.from(emb.image_embeddings.data), emb.image_embeddings.dims);
            // console.log("t", t)
            // const t = new ort.Tensor(image_embeddings.type, Float32Array.from(image_embeddings.data), image_embeddings.dims);

            const feed = {
                "image_embeddings": imageEmbeddings,
                "point_coords": pointCoords,
                "point_labels": pointLabels,
                "mask_input": maskInput,
                "has_mask_input": hasMask,
                "orig_im_size": origianlImageSize
            }
            console.log("feed", feed)
            // const start = Date.now();
            try {
                const results = await modelSess[1].run(feed);
                console.log("Generated mask:", results);
                const mask = results.masks;

                const maskImageData = mask.toImageData();

                segmentTypeHandle(maskImageData, block)

                block.hasPoint = true;
                if (fn) {
                    fn(block.segmentImageBase64);
                }
            } catch (error) {
                console.log(`caught error: ${error}`)
            }
            // const end = Date.now();
            // console.log(`generating masks took ${(end - start) / 1000} seconds`);
        }

        function cropImage(imagedata: ImageData) {
            let left = -1, top = -1, right = -1, bottom = -1;
            const idata = imagedata.data
            const count = idata.length;
            const countWidth = imagedata.width * 4;
            console.log(count)
            for (let i = 0, y = 0; i < count; i = i + 4) {
                if (i != 0 && i % countWidth == 0) {
                    y++;
                }
                // console.log([idata[i], idata[i + 1], idata[i + 2], idata[i + 3]])
                if (
                    idata[i] !== 255
                    || idata[i + 1] !== 255
                    || idata[i + 2] !== 255
                    || idata[i + 3] !== 255
                ) {
                    const x = (i - countWidth * y) / 4;
                    if (left === -1) {
                        left = x
                    } else {
                        if (left > x) {
                            left = x;
                        }
                        if (right === -1) {
                            right = x
                        } else if (right < x) {
                            right = x
                        }
                    }
                    if (top === -1) {
                        top = y;
                    } else {
                        if (bottom === -1) {
                            bottom = y
                        } else if (bottom < y) {
                            bottom = y
                        }
                    }
                }
            }
            return [left, top, right - left, bottom - top]
        }

        async function segmentTypeHandle(maskImageData: ImageData, block: BlockItem) {
            const idata = maskImageData.data
            const count = idata.length;
            if (segmentOpera === 'mask') {
                for (let i = 0; i < count; i = i + 4) {
                    // console.log([idata[i], idata[i + 1], idata[i + 2], idata[i + 3]])
                    if (idata[i] > 0) {
                        idata[i] = block.color[0];
                        idata[i + 1] = block.color[1]
                        idata[i + 2] = block.color[2]
                        idata[i + 3] = 178; // 透明度
                    } else {
                        idata[i + 3] = 0
                    }
                }
            } else {
                for (let i = 0; i < count; i = i + 4) {
                    // console.log([idata[i], idata[i + 1], idata[i + 2], idata[i + 3]])
                    if (idata[i] > 0) {
                        idata[i + 3] = 0; // 透明度
                    } else {
                        idata[i] = 255
                        idata[i + 1] = 255
                        idata[i + 2] = 255
                        idata[i + 3] = 255
                    }
                }
            }

            const canvas = canvasRef.value
            const ctx = canvas.getContext('2d');

            // ctx.globalAlpha = 0.5;
            // convert image data to image bitmap
            // let imageBitmap = await createImageBitmap(maskImageData, { resizeWidth: canvas.width, resizeHeight: canvas.height, resizeQuality: "medium" });
            let imageBitmap = await createImageBitmap(maskImageData);
            // canvas.width = maskImageData.width;
            // canvas.height = maskImageData.height;
            ctx.drawImage(imageBitmap, 0, 0);

            if (segmentOpera === 'crop') {
                const locate = cropImage(ctx.getImageData(0, 0, canvas.width, canvas.height));
                const imagedata = ctx.getImageData(locate[0], locate[1], locate[2], locate[3])
                imageBitmap = await createImageBitmap(imagedata);

                const tmpCanvas = document.createElement('canvas');
                const tmpCtx = tmpCanvas.getContext('2d');
                // 设置canvas的尺寸并将复制的ImageData绘制到canvas上
                tmpCanvas.width = locate[2]
                tmpCanvas.height = locate[3]
                tmpCtx?.drawImage(imageBitmap, 0, 0);
                block.segmentImageBase64 = tmpCanvas.toDataURL()

                ctx.clearRect(0, 0, canvas.width, canvas.height);
                ctx.putImageData(imageImageData, 0, 0, 0, 0, canvas.width, canvas.height);
                previewHandle(imagedata)
            } else {
                const tmpCanvas = document.createElement('canvas');
                const tmpCtx = tmpCanvas.getContext('2d');
                // 设置canvas的尺寸并将复制的ImageData绘制到canvas上
                tmpCanvas.width = canvas.width;
                tmpCanvas.height = canvas.height;
                tmpCtx?.drawImage(imageBitmap, 0, 0);
                block.segmentImageBase64 = tmpCanvas.toDataURL()
            }
            block.segmentImage = canvas.toDataURL();
            block.segmentImageBitmap = imageBitmap;
        }

        function previewHandle(imagedata: ImageData) {
            if (previewRef.value) {
                const tagName = previewRef.value.tagName
                if (tagName === 'CANVAS') {
                    const canvas = previewRef.value
                    const ctx = canvas.getContext('2d')
                    canvas.width = imagedata.width;
                    canvas.height = imagedata.height;
                    ctx.putImageData(imagedata, 0, 0, 0, 0, imagedata.width, imagedata.height);
                } else if (tagName === 'IMG') {
                    const tmpCanvas = document.createElement('canvas');
                    const tmpCtx = tmpCanvas.getContext('2d');
                    // 设置canvas的尺寸并将复制的ImageData绘制到canvas上
                    tmpCanvas.width = imagedata.width;
                    tmpCanvas.height = imagedata.height;
                    tmpCtx?.putImageData(imagedata, 0, 0, 0, 0, imagedata.width, imagedata.height);
                    previewRef.value.src = tmpCanvas.toDataURL()
                }
            }
        }

        function setMaxSize(width: number, height: number) {
            MAX_WIDTH = width;
            MAX_HEIGHT = height
        }

        function setSegmentOpera(tempSegmentOpera: SegmentOperaType) {
            segmentOpera = tempSegmentOpera
        }

        function setPointOpera(type: PointOperaType) {
            pointOpera = type
        }

        return {
            canvasRef,
            previewRef,
            imageHandle,
            imageRenderHandle,
            clickHandle,
            setPointOpera,
            setSegmentOpera,
            setMaxSize,
        }
    }

    return {
        _useBlocks,
        _useModel,
        _useMediaUpload,
        _useFrames,
    }
}
