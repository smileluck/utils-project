from utils.pillow_img import png_to_jpg

import os


def list_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            tempFile = str(file)
            tempStr = tempFile.lower()
            if tempStr.endswith(".png"):
                originPath = os.path.join(root, file)
                targetPath = os.path.join(root, tempFile.replace(".png", ".jpg"))
                png_to_jpg(originPath, targetPath)


if __name__ == "__main__":
    directory = "C:\\Users\\drenc\\Desktop\\test\\资料\\old\\背景01-18"
    list_files(directory)
