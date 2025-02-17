import { ref } from 'vue'
/**
 * 使用canvas转video
 */
export default function useCanvasVideo() {
    const recorder = ref<MediaRecorder>();
    const videoTypes: string[] = ['mp4', 'webm', 'ogg', 'x-matroska'];
    const codecs: string[] = ['should-not-be-supported', 'vp9', 'vp9.0', 'vp8', 'vp8.0', 'avc1', 'av1', 'h265', 'h.265', 'h264', 'h.264', 'opus', 'pcm', 'aac', 'mpeg', 'mp4a'];
    let _supportMime: string = '';
    let _videoType = 'mp4';
    const data: any[] = [];
    const startTime = ref<number>(0);
    const spendTime = ref<string>('0');
    let timeOut: any = null;
    const recordStatus = ref<string>('未初始化')

    supportMime();

    //导出当前浏览器支持的格式
    function supportMime() {
        const isSupported = MediaRecorder.isTypeSupported;
        videoTypes.some((type) => {
            const mimeType = `video/${type}`;
            codecs.some((codec) => [
                `${mimeType};codecs=${codec}`,
                `${mimeType};codecs=${codec.toUpperCase()}`
            ].some(variation => {
                if (isSupported(variation)) {
                    _supportMime = variation
                    _videoType = type
                    console.log(_supportMime, _videoType)
                    return true;
                }
                return false;
            }));
            if (_supportMime !== '') {
                return true;
            }
            else if (isSupported(mimeType)) {
                _supportMime = mimeType
                _videoType = type
                console.log(_supportMime, _videoType)
                return true;
            }

            return false;
        });
    }

    // 创建录像
    function createRecord(canvas: HTMLCanvasElement) {
        const stream = canvas.captureStream();
        recorder.value = new MediaRecorder(stream, { mimeType: _supportMime });
        recorder.value.ondataavailable = function (event) {
            if (event.data && event.data.size) {
                data.push(event.data);
            }
        };
        recorder.value.onstop = () => {
        };
        recordStatus.value = '初始化'
    }


    // 启动录像
    async function startRecord() {
        recordStatus.value = '录制中'
        data.length = 0;
        startTime.value = new Date().getTime() / 1000;
        recorder.value?.start();
        timeOut = setInterval(() => {
            const num = ((new Date().getTime() / 1000) - startTime.value);
            spendTime.value = num.toFixed(0);
        }, 1000)
    }

    // 停止录像
    async function stopRecord() {
        recordStatus.value = '录制完成'
        recorder.value?.stop();
        if (timeOut != null) {
            clearInterval(timeOut)
            spendTime.value = "0";
        }
    }

    // 下载视频
    async function downloadVideo() {
        if (data.length === 0) return;
        const url = URL.createObjectURL(new Blob(data, { type: _supportMime }));
        const link = document.createElement('a'); // 创建下载链接
        link.href = url;
        link.download = 'video.' + _videoType; // 设置文件名
        link.click(); // 触发下载

        URL.revokeObjectURL(url); // 释放Blob URL
    }

    return {
        createRecord,
        startRecord,
        stopRecord,
        downloadVideo,
        recordStatus,
        spendTime
    }

}