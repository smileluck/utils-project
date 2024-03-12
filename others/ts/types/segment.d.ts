
import * as ort from 'onnxruntime-web';
type xy = {
    x: number;
    y: number;
};

export type BlockItemType = "mask" | "panel"

export interface BlockItem {
    id: string;
    type: BlockItemType,// mask遮罩，panel切图
    hasPoint: boolean; // 是否操作
    color: number[]; // 颜色 rgba
    pointModel: number[]; // 操作模式
    pointXY: number[]; // 操作点
    resource: string;
    resourceType: string;
    imgBase64: string; // 图片base64
    imgData: ImageData | undefined;
    result: Api.AiTools.SegmentFrame | null;
    zIndex: number;
    segmentImage: string;// 图片遮罩
    segmentImageBase64: string;// 图片遮罩base64
    segmentImageBitmap: ImageBitmap | null;// 图片遮罩位图
    imageEmbeddings?: ort.Tensor | undefined;
}
export interface ModelMapType {
    sam_b: string[];
    mobile_sam: string[];
}

export interface ProviderType extends ort.InferenceSession.ExecutionProviderOption {
    deviceType?: string;
    powerPreference?: string;
}

export interface ConfigType {
    model: string;
    provider: ProviderType;
    device: string;
    threads: number;
    clear_cache: boolean;
}

export interface UploadType {
    type: number; // 1是主视频，2是每一帧的蒙版视频，3是普通图片
    blockIndex: number;
}

export interface FrameItem {
    id: string;
    type: number; // UploadType.type
    sec: number;// 视频第几秒
    base64: string;// 图片BASE64
    imageData: ImageData | undefined;
    imageEmbeddings: ort.Tensor | undefined;
}

export type PointOperaType = 'plus' | 'cover';// 追加/覆盖

export type SegmentOperaType = 'mask' | 'crop';// 遮罩/截取