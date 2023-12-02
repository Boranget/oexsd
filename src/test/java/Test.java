import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author boranget
 * @date 2023/12/2
 */
public class Test {
    public static void main(String[] args) {
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("xsd:schema");
        rootElement.addAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
        rootElement.addAttribute("xmlns","http://www.democz.com/collect");
        rootElement.addAttribute("targetNamespace","http://www.democz.com/collect");
        Element empId = rootElement.addElement("ID");
        empId.setText("2021");
        Element empCode = rootElement.addElement("CODE");
        empCode.setText("200");
        String xmlStr = document.asXML();
        System.out.println(xmlStr);
    }
}
