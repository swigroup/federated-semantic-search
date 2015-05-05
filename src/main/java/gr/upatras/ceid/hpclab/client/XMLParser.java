/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import gr.upatras.ceid.hpclab.response.model.ObjectFactory;
import gr.upatras.ceid.hpclab.response.model.ResultType;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author koutsomi
 */
class XMLParser {

    private final ObjectFactory fact = new ObjectFactory();
    private final Document doc;

    XMLParser(InputStream xml) throws
            SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        /*InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));*/
        doc = db.parse(xml);
    }

    ResultType createResult(String title, String description, String URL) {
        //add only results that have a URL
        if (URL != null && !URL.equals("")) {
            ResultType r = fact.createResultType();
            r.setDescription(description);
            r.setTitle(title);
            r.setURL(URL);
            return r;
        }
        return null;
    }

    String getChildElement(Element parent, String name) {
        String value = null;
        Node node = parent.getElementsByTagName(name).item(0);
        if (node != null) {
            value = node.getTextContent();
        }
        return value;
    }

    String getChildElement(Element parent, String name, String attr, String val) {
        String value = null;
        Node node = parent.getElementsByTagName(name).item(0);
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            Element nl = (Element) node;
            if (nl.hasAttribute(attr) && nl.getAttribute(attr).equals(val)) {
                value = node.getTextContent();
            }
        }
        return value;
    }

    /**
     * @return the doc
     */
    Document getDocument() {
        return doc;
    }

}
