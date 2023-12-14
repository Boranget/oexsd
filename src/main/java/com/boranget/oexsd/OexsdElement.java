package com.boranget.oexsd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boranget
 * @date 2023/12/2
 * 包装XSD Element节点的实体类
 */
public class OexsdElement {
    /**
     * 元素名
     */
    private String elementName;
    /**
     * 元素描述
     */
    private String elementDesc;
    /**
     * 命名空间（根元素使用）
     */
    private String namespace;
    /**
     * 子元素（数组标签使用）
     */
    private List<OexsdElement> childrenList;
    /**
     * 文件名，根元素存一份即可
     */
    private String fileName;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementDesc() {
        return elementDesc;
    }

    public void setElementDesc(String elementDesc) {
        this.elementDesc = elementDesc;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<OexsdElement> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<OexsdElement> childrenList) {
        this.childrenList = childrenList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
