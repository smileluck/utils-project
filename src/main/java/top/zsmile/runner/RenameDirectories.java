package top.zsmile.runner;


import top.zsmile.utils.uuid.IdUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class RenameDirectories {
    static Integer num = 1;

    public static void main(String[] args) {
        // 指定要读取的目录
        String sourceDirectory = "C:\\Users\\drenc\\Desktop\\test\\资料\\new\\Archive 3\\新水壶";

//        try {
        // 获取目录下的所有文件和文件夹
//            Path sourcePath = Paths.get(sourceDirectory);
//            Files.list(sourcePath)
//                    .filter(Files::isRegularFile) // 过滤出文件夹
//                    .forEach((pat) -> {
//                        renameFile(pat);
//                    }); // 对每个文件夹进行重命名操作


        // 创建 File 对象
        File directory = new File(sourceDirectory);
        File[] files = directory.listFiles();
        for (File file : files) {
            renameFile(file);
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static String paddingleft(int num, String fixNum) {
        String str = "" + num;
        while (str.length() < 5){
            str = "0" + str;
    }
        return str;
}

    private static void renameFile(File file) {
//        try {
        // 获取文件的原始名称
        String originalName = file.getName().toString();

        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalName.length() - 1) {
            extension = originalName.substring(dotIndex);
        }

        // 按指定格式生成新名称，例如 "file_1.txt", "file_2.txt" 等

        String paddingleft = paddingleft(num++, "0");

        String newName = "frame_" + paddingleft + extension;

        String newPath = file.getParentFile().getAbsolutePath() + "\\" + newName;
        File newFile = new File(newPath);

        // 重命名文件或目录
        boolean isRenamed = file.renameTo(newFile);

        if (isRenamed) {
            System.out.println("文件或目录已成功重命名");
        } else {
            System.out.println("文件或目录重命名失败");
        }
        System.out.println("Renamed file: " + file + " -> " + newPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void renameDirectory(Path directory) {
        try {
            // 获取文件夹的原始名称
            String originalName = directory.getFileName().toString();

            // 按指定格式生成新名称，例如 "dir_1", "dir_2" 等
            String newName = "dir_" + (directory.getParent().resolve(originalName).iterator().next().getFileName().toString());

            // 重命名文件夹
            Path newPath = directory.resolveSibling(newName);
            Files.move(directory, newPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Renamed directory: " + directory + " -> " + newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
