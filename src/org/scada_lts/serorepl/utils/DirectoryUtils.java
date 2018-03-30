package org.scada_lts.serorepl.utils;

import org.scada_lts.serorepl.utils.DirectoryInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DirectoryUtils {

    public static final String BYTE = " B";
    public static final String KILOBYTE = " KB";
    public static final String MEGABYTE = " MB";
    public static final String GYGABYTE = " GB";
    public static final String TERABYTE = " TB";


    public static DirectoryInfo getDirectorySize(File directory) {

        DirectoryInfo info = new DirectoryInfo();

        List<File> allFileList = null;

        Stack<File> stack = new Stack<File>();
        stack.push(directory);
        while(!stack.isEmpty()) {
            File child = stack.pop();
            if (child.isDirectory()) {
                for(File f : child.listFiles()) stack.push(f);
            } else if (child.isFile()) {
             //   System.out.println(child.getPath());
             //   allFileList.add(child);
                ++info.count;
                info.size += child.length();
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
    // użyte w nieużywanej metodzioe
 //   private static void _listDirectories(List<String> list, File currentPath, String namePattern) throws IOException {}

}
