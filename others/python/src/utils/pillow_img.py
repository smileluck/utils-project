from PIL import Image

def png_to_jpg(png_path, jpg_path):
    # 打开PNG图像
    png_image = Image.open(png_path)

    # 将PNG图像转换为JPEG格式pip
    jpg_image = png_image.convert("RGB")

    # 保存JPEG图像
    jpg_image.save(jpg_path)