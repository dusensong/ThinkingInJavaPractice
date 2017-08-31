package com.example.test;

import java.io.File;
import java.io.IOException;

/**
 * Created by BG241996 on 2017/8/22.
 */
public class FileTest {
    public static void main(String[] args) {
        File file = new File("C:\\Data\\Test");
        System.out.println(createDir(file));

        file = new File("C:\\1.txt");
        System.out.println(createFile(file));

        file = new File("C:\\1\\1.txt");
        System.out.println(createFile(file));
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     * @return 如果目录已存在或创建成功，返回true
     * @modify 2017/8/22 方法重命名，新增注释
     */
    public static boolean createDir(final String dirPath) {
        return createDir(getFileByPath(dirPath));
    }

    /**
     * 创建目录
     *
     * @param file 文件
     * @return 如果目录已存在或创建成功，返回true
     * @modify 2017/8/22 方法重命名，新增注释
     */
    public static boolean createDir(final File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 如果文件已存在或创建成功，返回true
     * @modify 2017/8/22 方法重命名，新增注释
     */
    public static boolean createFile(final String filePath) {
        return createFile(getFileByPath(filePath));
    }

    /**
     * 创建文件
     *
     * @param file 文件
     * @return 如果文件已存在或创建成功，返回true
     * @modify 2017/8/22 方法重命名，新增注释
     */
    public static boolean createFile(final File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        // 如果不存在，尝试创建其父目录
        if (!createDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
