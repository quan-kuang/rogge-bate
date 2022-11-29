package com.loyer.modules.system.utils;

import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * XML工具类
 *
 * @author kuangq
 * @date 2020-12-07 14:54
 */
public class XmlUtil {

    /**
     * xml转map
     *
     * @author kuangq
     * @date 2020-12-07 14:55
     */
    @SneakyThrows
    public static Map<String, String> xmlToMap(String strXml) {
        Map<String, String> map = new HashMap<>(16);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(StringUtil.decode(strXml));
        Document document = documentBuilder.parse(inputStream);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                map.put(element.getNodeName(), element.getTextContent());
            }
        }
        inputStream.close();
        return map;
    }
}