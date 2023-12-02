package com.boranget.oexsd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boranget
 * @date 2023/12/2
 */
public class OexsdElement {
    private String elementName;
    private String elementDesc;
    private String namespace;
    private List<OexsdElement> childrenList;

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

}
