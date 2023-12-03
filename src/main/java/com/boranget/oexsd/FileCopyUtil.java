package com.boranget.oexsd;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

/**
 * @author boranget
 * @date 2023/12/3
 */
public class FileCopyUtil {
    static final Logger logger = LogManager.getLogger(FileCopyUtil.class);

    public static void copyFileFromJar(String sourceFileName, String targetPath) {
        File targetFolder = new File(targetPath);
        if (!targetFolder.exists()) {
            targetFolder.mkdir();
        }
        // 使用源文件名构建目标文件名
        File targetFile = new File(targetFolder, new File(sourceFileName).getName());
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // jar包中的文件需要使用这种方式直接获取输入流，无法直接通过路径获取文件输入流
            inputStream = Oexsder.class.getResourceAsStream(sourceFileName);
            fileOutputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int readByte = 0;
            while ((readByte = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readByte);
            }
        } catch (IOException e) {
            logger.error("文件复制过程出错");
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
