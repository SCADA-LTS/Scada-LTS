package org.scada_lts.serorepl.utils;

        import java.io.File;
        import java.util.Stack;

public class DirectoryUtils {

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

}
