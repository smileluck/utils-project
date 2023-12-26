package top.zsmile.runner;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class MdBookApplication {

    private final static String path = "D:\\project\\B.Smile\\SmileX-Note\\src";

    public static void main(String[] args) throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("文件夹：" + dir);
                String fileName = dir.getFileName().toString();
                if (!fileName.endsWith("assets")) {
                    String absolutePath = dir.toAbsolutePath().toString();

                    String replace = absolutePath.replace(path, "");
                    String[] split = replace.split("\\\\");

                    int len = split.length - 1;
                    for (int i = 0; i < len; i++) {
                        sb.append("\t");
                    }
                    sb.append("- [" + fileName + "]()\n");


                    dirCount.incrementAndGet();
                }
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("文件" + file);
                String fileName = file.getFileName().toString();
                String absolutePath = file.toAbsolutePath().toString();
                if (fileName.equalsIgnoreCase("README.md") || fileName.equalsIgnoreCase("SUMMARY.md")) {

                } else if (fileName.endsWith(".md") || fileName.endsWith(".md")) {
                    String replace = absolutePath.replace(path + "\\", "");
                    absolutePath.replace("\\\\", "/");
                    String[] split = replace.split("\\\\");

                    int len = split.length - 1;
                    for (int i = 0; i <= len; i++) {
                        sb.append("\t");
                    }
                    replace = replace.replace("\\", "/");
                    replace = replace.replace(" ", "%20");
                    sb.append("- [" + fileName.substring(0, fileName.length() - 3) + "](" + replace + ")\n");

                    fileCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
//        System.out.println("dir count:" + dirCount);
//        System.out.println("file count:" + fileCount);
        System.out.println(sb.toString());
    }
}
