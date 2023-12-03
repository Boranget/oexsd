package com.boranget.oexsd;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author boranget
 * @date 2023/12/3
 */
public class OexsdWriter {

    public static void writeOexsdsToFile(String location, String excelFileName, Map<String, Document> ori) {

        ori.forEach((fileName, document) -> {
            // 格式化输出
            OutputFormat format = new OutputFormat().createPrettyPrint();
            // 紧凑输出
            // OutputFormat format = new OutputFormat().createCompactFormat();
            // 设置编码格式
            format.setEncoding("utf-8");
            // 获取文件夹
            File newFolder = new File(location, excelFileName.split("\\.")[0]);
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            // 创建目标文件
            File targetXsdFile = new File(newFolder, fileName + ".xsd");
            FileWriter fileWriter = null;
            XMLWriter xmlWriter = null;
            try {
                fileWriter = new FileWriter(targetXsdFile);
                xmlWriter = new XMLWriter(fileWriter,format);
                xmlWriter.write(document);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (xmlWriter != null) {
                    try {
                        xmlWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
