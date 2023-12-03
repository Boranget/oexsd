package com.boranget.oexsd;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * @author boranget
 * @date 2023/12/2
 */
public class Oexsder {
    static final Logger logger = LogManager.getLogger(Oexsder.class);
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
     *
     * @param args
     */
    public static void main(String[] args) {

        String frameFileName = FILE_NAME;
        if (args.length > 0) {
            final String arg = args[0];
            // 判断是否有参数init
            if (INIT_ARG.equals(arg)) {
                // init则将例子文件输出到运行目录
                logger.info("正在进行初始化.....");
                return;
            }
            // 否则认为该参数为文件名
            logger.info("检测到指定文件名.....");
            frameFileName = arg;
        }
        // 如果没有参数，则读取工作目录下的frame.xlsx文件
        logger.info("未检测到指定文件名，使用默认文件名："+FILE_NAME);
        // 获取当前程序执行目录
        String currentDirectory = System.getProperty("user.dir");
        // 拼接路径
        File frameFile = new File(currentDirectory, frameFileName);
        if (!frameFile.exists()) {
            logger.error("文件不存在");
            return;
        }
        // 调用解析文件方法
        final List<OexsdElement> oexsdElements = OexsdElementFactory.readFromExcel(frameFile);
        // 解析获取到的OexsdElement对象
        final Map<String, Document> oriMap = OexsdElementParser.parseOexsdElements(oexsdElements);
        // 输出到文件
        OexsdWriter.writeOexsdsToFile(currentDirectory, frameFileName, oriMap);
    }
}
