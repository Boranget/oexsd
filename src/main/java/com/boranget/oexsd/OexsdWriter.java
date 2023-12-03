package com.boranget.oexsd;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author boranget
 * @date 2023/12/3
 */
public class OexsdWriter {
    static final Logger logger = LogManager.getLogger(OexsdWriter.class);
    public static void writeOexsdsToFile(String location, String excelFileName, Map<String, Document> ori) {

        ori.forEach((fileName, document) -> {
            // 格式化输出
            OutputFormat format = new OutputFormat().createPrettyPrint();
            // 紧凑输出
            // OutputFormat format = new OutputFormat().createCompactFormat();
            // 设置编码格式
            format.setEncoding("utf-8");
            // 获取文件夹
            // 这里不直接用excelFileName.split()是避免输入参数为相对路径（./file.xlsx）的情况下会被第一个.干扰
            File newFolder = new File(location, new File(excelFileName).getName().split("\\.")[0]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            // 创建目标文件
            File targetXsdFile = new File(newFolder, fileName + ".xsd");
            // 这里不能使用FileWriter，否则会导致format中设置的编码无效
            // FileWriter fileWriter = null;
            FileOutputStream fileOutputStream = null;
            XMLWriter xmlWriter = null;
            try {
                fileOutputStream = new FileOutputStream(targetXsdFile);
                xmlWriter = new XMLWriter(fileOutputStream,format);
                xmlWriter.write(document);
                logger.info("xsd [ "+targetXsdFile.getAbsolutePath()+" ]写入成功");
            } catch (IOException e) {
                logger.error("xsd [ "+targetXsdFile.getAbsolutePath()+" ]写入到文件时出现异常");
                e.printStackTrace();
            } finally {
                if (xmlWriter != null) {
                    try {
                        xmlWriter.close();
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
        });
    }
}
