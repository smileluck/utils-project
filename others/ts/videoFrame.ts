export interface videoInfo {
  blob: Blob | null;
  url: string;
}

// 画视频
const drawVideo = (video: HTMLVideoElement) => {
  return new Promise<videoInfo>(success => {
    const cvs = document.createElement('canvas');
    const ctx = cvs.getContext('2d');
    cvs.width = video.videoWidth;
    cvs.height = video.videoHeight;
    ctx?.drawImage(video, 0, 0, cvs.width, cvs.height);
    cvs.toBlob(blob => {
      success({
        blob,
        url: URL.createObjectURL(blob as Blob)
      });
    });
  });
};

// 视频截取工具
export function captureFrame(videoFile: File, time: number = 0) {
  return new Promise<videoInfo>(succeed => {
    const video = document.createElement('video');
    video.currentTime = time;
    video.muted = true;
    video.preload = 'auto';
    video.autoplay = true;
    video.setAttribute('crossOrigin', 'Anonymous'); // 处理跨域
    video.setAttribute('preload', 'auto'); // auto|metadata|none
    video.src = URL.createObjectURL(videoFile);
    video.oncanplay = async () => {
      // 延时500ms防止白屏问题
      setTimeout(async () => {
        const res = await drawVideo(video);
        succeed(res);
      }, 500);
    };
  });
}
// blob\file转base64
export function toBase64(blob: Blob | File) {
  return new Promise<string>(success => {
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onload = (e: any) => {
      success(e.target.result);
    };
  });
}
