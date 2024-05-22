package com.boranget.oexsd;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * @author boranget
 * @date 2023/12/2
 */
public class Oexsder {
    static final Logger logger = LogManager.getLogger(Oexsder.class);

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        String frameFileName = null;
        if (args.length == 0) {
            // 如果没有参数，则初始化
            logger.info("未检测到文件名，进行初始化...");
            init();
            return;
        }
        if (args.length == 1){
            // 参数为文件名
            frameFileName = args[0];
        }else if(args.length==2){
            // 第一个参数为指定当前模式
            if("-m".equalsIgnoreCase(args[0])){
                GlobalStatus.CURRENT_MODE = GlobalStatus.MESSAGE_TYPE;
            }else {
                logger.error("参数错误");
                return;
            }
            // 第二个参数为文件名
            frameFileName = args[1];
        }
        // 获取当前程序执行目录
        String currentDirectory = System.getProperty("user.dir");
        // 拼接路径
        File frameFile = new File(currentDirectory, frameFileName);
        if (!frameFile.exists()) {
            logger.error("文件不存在");
            return;
        }
        // 调用解析文件方法
        logger.info("开始解析文件");
        final List<OexsdElement> oexsdElements = OexsdElementFactory.readFromExcel(frameFile);
        logger.info("解析完成");
        // 处理获取到的OexsdElement对象
        logger.info("开始转换为xsd内容");
        final Map<String, Document> oriMap = OexsdElementParser.parseOexsdElements(oexsdElements);
        logger.info("转换完成");
        // 输出到文件
        logger.info("开始写入文件");
        OexsdWriter.writeOexsdsToFile(currentDirectory, frameFileName, oriMap);
        logger.info("写入完成");
    }

    private static void init() {
        // 将init文件夹下的内容输出到执行目录
        // 获取当前程序执行目录
        String currentDirectory = System.getProperty("user.dir");
        FileCopyUtil.copyFileFromJar("/init/frame.xlsx",currentDirectory);
        FileCopyUtil.copyFileFromJar("/init/Readme.md",currentDirectory);

    }
}
