package org.scada_lts.serorepl.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

    private final DocumentBuilder builder;

    public XmlUtils() throws ParserConfigurationException {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public Document parse(File inputFile) throws IOException, SAXException {
        return builder.parse(inputFile);
    }


    public List<Element> getElementsByTagName(Element parentElement, String tagName) throws XPathExpressionException {

        NodeList n2 = parentElement.getChildNodes();
        NodeList nodeList = parentElement.getChildNodes();
        ArrayList<Element> results = new ArrayList<>();

        for (int index = 0 ; index < nodeList.getLength(); index++){
            Node item = n2.item(index);
            if (item.getNodeType() == Node.ELEMENT_NODE){
                results.add((Element)item);
            }
        }
        return results;
    }

}
