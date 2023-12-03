package com.boranget.oexsd;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultNamespace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author boranget
 * @date 2023/12/3
 */
public class OexsdElementParser {
    /**
     * 创建命名空间用于全局使用
     */
    public static final DefaultNamespace DEFAULT_NAMESPACE = new DefaultNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

    /**
     * 解析OexsdElement列表
     * @param oexsdElements
     * @return 返回一个map，键为文件名，值为解析出的文档
     */
    public static Map<String, Document> parseOexsdElements(List<OexsdElement> oexsdElements){
        Map<String,Document> res = new HashMap<>();
        for(OexsdElement oexsdElement:oexsdElements){
            final Document parseRes = transformXsdFromOexsdElement(oexsdElement);
            res.put(oexsdElement.getElementName(),parseRes);
        }
        return res;
    }

    /**
     * 解析OexsdElement根元素为xsd内容
     * @param oexsdElement
     */
    public static Document transformXsdFromOexsdElement(OexsdElement oexsdElement){
        final String elementName = oexsdElement.getElementName();
        final String namespace = oexsdElement.getNamespace();
        Document document = DocumentHelper.createDocument();
        // 创建schema标签
        Element schema = new DefaultElement("schema",DEFAULT_NAMESPACE);
        // 命名空间设置
        schema.addAttribute("targetNamespace",namespace);
        schema.addNamespace("",namespace);
        schema.addNamespace("xsd","http://www.w3.org/2001/XMLSchema");
        // 包装
        Element sequence = new DefaultElement("sequence", DEFAULT_NAMESPACE);
        Element complexType = new DefaultElement("complexType", DEFAULT_NAMESPACE);
        complexType.add(sequence);
        complexType.addAttribute("name",elementName);
        final List<OexsdElement> childrenList = oexsdElement.getChildrenList();
        // 存入子元素
        for(OexsdElement oexsdElementChild :childrenList){
            sequence.add(getXmlElement(oexsdElementChild));
        }
        schema.add(complexType);
        document.setRootElement(schema);
        // 返回文档
        return document;
    }

    /**
     * 递归将OexsdElement转为XmlElement
     * @param oexsdElement
     * @return
     */
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
}
