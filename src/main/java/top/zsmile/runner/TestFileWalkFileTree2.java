package top.zsmile.runner;


import org.apache.commons.io.FileUtils;
import top.zsmile.utils.uuid.IdUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFileWalkFileTree2 {


    public static void main(String[] args) throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("C:\\Users\\drenc\\Desktop\\test\\资料\\展示背景"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                System.out.println("文件夹："+dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

//                System.out.println("文件" + file);
//                if (file.getFileName().toString().endsWith(".md") || file.getFileName().toString().endsWith(".MD")) {
//                    System.out.println(file.getFileName().toString());
//                }
                fileCount.incrementAndGet();

                BufferedImage image = ImageIO.read(file.toFile());

                // 获取图片分辨率
                int width = image.getWidth();
                int height = image.getHeight();

                System.out.println(IdUtils.getSnowId().toString() + "-{\"bucket\":\"ai-material-1317824441\",\"key\":\"/bg-img/" + file.getFileName().toString() + "\",\"width\":" + width + ",\"height\":" + height + "}");
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count:" + dirCount);
        System.out.println("file count:" + fileCount);
    }
}
