//: net/mindview/util/ProcessFiles.java
package net.mindview.util;

import java.io.File;
import java.io.IOException;

/**
 * 使用指定策略处理指定后缀文件（或目录树中所有匹配后缀的文件）
 */
public class ProcessFiles {
    public interface Strategy {
        void process(File file);
    }

    private Strategy strategy;
    private String ext;

    public ProcessFiles(Strategy strategy, String ext) {
        this.strategy = strategy;
        this.ext = ext;
    }

    /**
     * 使用指定策略处理指定后缀文件，
     * 如果命令行参数为空，扫描当前目录，处理匹配文件
     * 如果命令行不为空，对所有目录或文件进行处理
     *
     * @param args 命令行参数，可以为空，可以指定若干目录或文件
     */
    public void start(String[] args) {
        try {
            if (args.length == 0)
                processDirectoryTree(new File("."));
            else
                for (String arg : args) {
                    File fileArg = new File(arg);
                    if (fileArg.isDirectory())
                        processDirectoryTree(fileArg);
                    else {
                        // 处理文件
                        // Allow user to leave off extension:
                        if (!arg.endsWith("." + ext))
                            arg += "." + ext;
                        strategy.process(
                                new File(arg).getCanonicalFile());
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理目录
     *
     * @param root
     * @throws IOException
     */
    public void
    processDirectoryTree(File root) throws IOException {
        for (File file : Directory.walk(
                root.getAbsolutePath(), ".*\\." + ext))
            strategy.process(file.getCanonicalFile());
    }

    // Demonstration of how to use it:
    public static void main(String[] args) {
        new ProcessFiles(new Strategy() {
            public void process(File file) {
                System.out.println(file);
            }
        }, "java").start(args);
    }
} /* (Execute to see output) *///:~
