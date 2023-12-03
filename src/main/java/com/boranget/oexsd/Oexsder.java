package com.boranget.oexsd;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultNamespace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author boranget
 * @date 2023/12/2
 */
public class Oexsder {
    /**
     * 初始化参数
     */
    public static final String INIT_ARG = "init";
    /**
     * 默认模板文件
     */
    public static final String FILE_NAME = "frame.xlsx";

    /**
     * 程序入口
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            final String arg = args[0];
            // 判断是否有参数init
            if (INIT_ARG.equals(arg)) {
                // init则将例子文件输出到运行目录
                return;
            }
        }
        // 如果没有参数，则读取工作目录下的frame.xlsx文件
        // 获取当前程序执行目录
        String currentDirectory = System.getProperty("user.dir");
        // 拼接路径
        File frameFile = new File(currentDirectory ,FILE_NAME);
        if (!frameFile.exists()) {
            System.out.println("文件不存在");
            return;
        }
        // 调用解析文件方法
        final List<OexsdElement> oexsdElements = OexsdElementFactory.readFromExcel(frameFile);
        // 解析获取到的OexsdElement对象
        final Map<String, Document> oriMap = OexsdElementParser.parseOexsdElements(oexsdElements);
        // 输出到文件
        OexsdWriter.writeOexsdsToFile(currentDirectory,FILE_NAME,oriMap);
    }
}
