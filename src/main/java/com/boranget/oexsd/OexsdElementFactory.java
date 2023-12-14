package com.boranget.oexsd;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author boranget
 * @date 2023/12/3
 * 用于将文件中的模板内容转为OexsdElement对象
 */
public class OexsdElementFactory {
    static final Logger logger = LogManager.getLogger(OexsdElementFactory.class);

    /**
     * 解析excel文件为OexsdElement列表
     *
     * @param frameFile
     * @return
     */
    public static List<OexsdElement> readFromExcel(File frameFile) {

        List<OexsdElement> res = new ArrayList<>();
        FileInputStream fileInputStream = null;
        try {
            logger.info("读入文件 [ " + frameFile + " ]");
            fileInputStream = new FileInputStream(frameFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            // 获取sheet数用于循环
            final int numberOfSheets = xssfWorkbook.getNumberOfSheets();
            // 循环处理sheet
            for (int i = 0; i < numberOfSheets; i++) {
                final XSSFSheet currentSheet = xssfWorkbook.getSheetAt(i);
                OexsdElement currentOexsdElementRoot = readFromSheet(currentSheet);
                if (currentOexsdElementRoot != null) {
                    res.add(currentOexsdElementRoot);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("出现了异常");
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 将每个sheet解析为OexsdElement
     *
     * @param currentSheet
     * @return
     */
    private static OexsdElement readFromSheet(XSSFSheet currentSheet) {
        // 获取总行数，总行数为最后一个行num+1
        int rowCount = currentSheet.getLastRowNum() + 1;
        if (rowCount <= 0) {
            logger.warn("sheet为空");
            return null;
        }
        // 读取第一行
        XSSFRow currentRow = currentSheet.getRow(0);
        // 读取根元素名
        String rootName = currentRow.getCell(0).toString();
        // 读取命名空间
        String namespace = currentRow.getCell(1).toString();
        // 读取根元素描述
        String rootDesc = null;
        final XSSFCell rootDescCell = currentRow.getCell(2);
        if (rootDescCell != null && !"".equals(rootDescCell.toString().trim())) {
            rootDesc = rootDescCell.toString();
        }
        logger.info("解析模板 [ " + rootName + " ]");
        OexsdElement oexsdRoot = new OexsdElement();
        oexsdRoot.setElementName(rootName);
        oexsdRoot.setNamespace(namespace);
        oexsdRoot.setElementDesc(rootDesc);
        oexsdRoot.setChildrenList(new ArrayList<>());
        // 存储sheet名作为文件名
        oexsdRoot.setFileName(currentSheet.getSheetName());
        // 初始化层列表
        List<List<OexsdElement>> layerList = new ArrayList<>();
        layerList.add(oexsdRoot.getChildrenList());
        // 循环处理剩余行
        everyRow:
        for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
            currentRow = currentSheet.getRow(rowIndex);
            // 这里不用加1，奇奇怪怪的逻辑
            final int cellCount = currentRow.getLastCellNum();
            for (int j = 0; j < cellCount; j++) {
                // 获取单元格值
                final XSSFCell cell = currentRow.getCell(j);
                if (cell != null) {
                    final String elementName = cell.toString();
                    // 解析主逻辑
                    // 创建一个新的OexsdElement
                    OexsdElement oexsdElement = new OexsdElement();
                    oexsdElement.setElementName(elementName);
                    // 判断是否有注释
                    final XSSFCell descCell = currentRow.getCell(j + 1);
                    if (descCell != null && !"".equals(descCell.toString().trim())) {
                        final String elementDesc = descCell.toString();
                        oexsdElement.setElementDesc(elementDesc);
                    }
                    // 获取当前层
                    List<OexsdElement> currentLayer = null;
                    // 如果当前层为第一层，则直接存到第一层，因为第一层只有一种情况就是根元素的childList
                    if (j == 0) {
                        currentLayer = layerList.get(0);
                    } else {
                        // 否则取前一层最后一个元素的childList作为当前层
                        final List<OexsdElement> preOexsdElements = layerList.get(j - 1);
                        final OexsdElement preLayerLastElement = preOexsdElements.get(preOexsdElements.size() - 1);
                        // 如果前一层最后一个元素不存在childList则创建childList
                        if (preLayerLastElement.getChildrenList() == null) {
                            preLayerLastElement.setChildrenList(new ArrayList<>());
                        }
                        // 取前一层最后一个元素的childList作为当前层
                        currentLayer = preLayerLastElement.getChildrenList();
                        // 层列表更新，不更新的话当前子元素会存入parent的上一个元素的childList中的最后一个子元素的childlist......（水很深，把握不住）
                        if (layerList.size() <= j) {
                            layerList.add(currentLayer);
                        } else {
                            // 如果层列表当前层所在位置已有列表则更新指针
                            layerList.set(j, currentLayer);
                        }

                    }
                    // 将当前元素加入当前层
                    currentLayer.add(oexsdElement);
                    // 既然当前行已经找到值了，就没有必要往下走了，因为一行只有一个元素
                    continue everyRow;
                }
            }
        }
        return oexsdRoot;
    }
}
