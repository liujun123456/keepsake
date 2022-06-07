package com.shunlai.net.util;

import java.io.File;
import java.io.IOException;

/**
 * @author Liu
 * @Date 2021/7/14
 * @mobile 18711832023
 */
public class FileUtils {

    public static boolean mkdirs(File directory) {
        try {
            forceMkdir(directory);
            return true;
        } catch (IOException e){
        }
        return false;
    }

    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message =
                    "File "
                        + directory
                        + " exists and is "
                        + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message =
                        "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

}
