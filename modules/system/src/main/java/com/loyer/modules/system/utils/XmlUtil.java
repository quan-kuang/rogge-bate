package com.loyer.modules.system.utils;

import com.alibaba.fastjson.JSON;
import com.loyer.common.dedicine.utils.StringUtil;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
    public static Map<String, String> toMap(String strXml) {
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

    /**
     * xml字符串转对象
     *
     * @author kuangq
     * @date 2023-03-20 14:17
     */
    @SneakyThrows
    public static <T> T toJavaObject(String xmlStr, Class<T> clazz) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xmlStr);
        Object object = unmarshaller.unmarshal(reader);
        return JSON.parseObject(JSON.toJSONString(object), clazz);
    }

    /**
     * java对象转xml字符串
     *
     * @author kuangq
     * @date 2023-03-20 14:17
     */
    @SneakyThrows
    public static <T> String toXmlStr(T entity, Class<T> clazz) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(entity, stringWriter);
        return stringWriter.toString();
    }
}