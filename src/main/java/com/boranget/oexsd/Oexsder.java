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

/**
 * @author boranget
 * @date 2023/12/2
 */
public class Oexsder {
    public static final String INIT_ARG = "init";
    public static final String FILE_NAME = "frame.xlsx";
    // 创建命名空间用于全局使用
    public static final DefaultNamespace DEFAULT_NAMESPACE = new DefaultNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

    /**
     * 解析excel文件为OexsdElement列表
     * @param frameFile
     * @return
     */
    public static List<OexsdElement> readFromExcel(File frameFile) {
        List<OexsdElement> res = new ArrayList<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(frameFile);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            // 获取sheet数用于循环
            final int numberOfSheets = xssfWorkbook.getNumberOfSheets();
            System.out.println("即将处理共[" + numberOfSheets + "]个sheet");
            // 循环处理sheet
            for (int i = 0; i < numberOfSheets; i++) {
                System.out.println("正在处理第[" + (i + 1) + "]个sheet");
                final XSSFSheet currentSheet = xssfWorkbook.getSheetAt(i);
                OexsdElement currentOexsdElementRoot = readFromSheet(currentSheet);
                if (currentOexsdElementRoot != null) {
                    res.add(currentOexsdElementRoot);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("出现了异常");
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
     * @param currentSheet
     * @return
     */
    private static OexsdElement readFromSheet(XSSFSheet currentSheet) {
        // 获取总行数，总行数为最后一个行num+1
        int rowCount = currentSheet.getLastRowNum() + 1;
        if (rowCount <= 0) {
            System.out.println("sheet为空");
            return null;
        }
        // 读取第一行
        XSSFRow currentRow = currentSheet.getRow(0);
        String rootName = currentRow.getCell(0).toString();
        String namespace = currentRow.getCell(1).toString();
        OexsdElement oexsdRoot = new OexsdElement();
        oexsdRoot.setElementName(rootName);
        oexsdRoot.setNamespace(namespace);
        oexsdRoot.setChildrenList(new ArrayList<>());
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
                    if(j == 0){
                        currentLayer = layerList.get(0);
                    }else{
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
                        if(layerList.size()<=j){
                            layerList.add(currentLayer);
                        }else {
                            // 如果层列表当前层所在位置已有列表则更新指针
                            layerList.set(j,currentLayer);
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

    /**
     * 解析OexsdElement为xsd内容
     * @param oexsdElement
     */
    public static String transformXsdFromOexsdElement(OexsdElement oexsdElement){
        StringBuilder res = new StringBuilder("");
        final String elementName = oexsdElement.getElementName();
        final String namespace = oexsdElement.getNamespace();
        Document document = DocumentHelper.createDocument();
        // 创建schema标签
        Element schema = new DefaultElement("schema",DEFAULT_NAMESPACE);
        // 命名空间设置
        schema.addAttribute("targetNamespace",namespace);
        schema.addNamespace("",namespace);
        schema.addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
        // 放入top元素
        Element sequence = new DefaultElement("sequence", DEFAULT_NAMESPACE);
        Element complexType = new DefaultElement("complexType", DEFAULT_NAMESPACE);
        complexType.add(sequence);
        complexType.addAttribute("name",elementName);
        final List<OexsdElement> childrenList = oexsdElement.getChildrenList();
        for(OexsdElement oexsdElementChild :childrenList){
            sequence.add(getXmlElement(oexsdElementChild));
        }
        schema.add(complexType);
        document.setRootElement(schema);
        System.out.println(document.asXML());
        return res.toString();
    }
    public static Element getXmlElement(OexsdElement oexsdElement){
        Element element = new DefaultElement("element", DEFAULT_NAMESPACE);
        element.addAttribute("name",oexsdElement.getElementName());
        // 添加描述
        String desc = oexsdElement.getElementDesc();
        if(desc!=null&&!"".equals(desc)){
            Element documentation = new DefaultElement("documentation",DEFAULT_NAMESPACE);
            documentation.addText(oexsdElement.getElementDesc());
            Element annotation = new DefaultElement("annotation",DEFAULT_NAMESPACE);
            annotation.add(documentation);
            element.add(annotation);
        }
        // 若当前元素是字符串
        if(oexsdElement.getChildrenList()==null||oexsdElement.getChildrenList().size()==0){
            element.addAttribute("type","xsd:string");
            element.addAttribute("minOccurs","0");
        }else {
            // 若当前元素是数组
            Element sequence = new DefaultElement("sequence",DEFAULT_NAMESPACE);
            Element complexType = new DefaultElement("complexType",DEFAULT_NAMESPACE);
            complexType.add(sequence);
            final List<OexsdElement> childrenList = oexsdElement.getChildrenList();
            for(OexsdElement oexsdElementChild :childrenList){
                sequence.add(getXmlElement(oexsdElementChild));
            }
            element.addAttribute("minOccurs","0");
            element.addAttribute("maxOccurs","unbounded");
            element.add(complexType);
        }
        return element;
    }
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
        final List<OexsdElement> oexsdElements = readFromExcel(frameFile);
        for(OexsdElement oexsdElement:oexsdElements){
            transformXsdFromOexsdElement(oexsdElement);
        }
        // 解析获取到的OexsdElement对象
        // 输出到文件
    }
}
