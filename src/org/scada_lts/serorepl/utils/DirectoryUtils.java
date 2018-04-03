package org.scada_lts.serorepl.utils;

        import java.io.File;
        import java.util.Stack;

public class DirectoryUtils {

    private static final String BYTE = " B";
    private static final String KILOBYTE = " KB";
    private static final String MEGABYTE = " MB";
    private static final String GYGABYTE = " GB";
    private static final String TERABYTE = " TB";

    public static DirectoryInfo getDirectorySize(File directory) {

        DirectoryInfo info = new DirectoryInfo();

        Stack<File> stack = new Stack<File>();
        stack.push(directory);
        while(!stack.isEmpty()) {
            File child = stack.pop();
            if (child.isDirectory()) {
                for(File f : child.listFiles()) stack.push(f);
            } else if (child.isFile()) {
                info.setSize(info.getSize() + child.length());
                info.setCount(info.getCount() + 1);
            }
        }
        return info;
    }

    public static String bytesDescription(long size){

        String fileSize;
        if (size < 1028L) {
            fileSize = size + BYTE;
        } else {
            size /= 1028L;
            if (size < 1000L) {
                fileSize = size + KILOBYTE;
            } else {
                size /= 1000L;
                if (size < 1000L) {
                    fileSize = size + MEGABYTE;
                } else {
                    size /= 1000L;
                    if (size < 1000L) {
                        fileSize = size + GYGABYTE;
                    } else {
                        size /= 1000L;
                        fileSize = size + TERABYTE;
                    }
                }
            }
        }
        return fileSize;
    }
}
