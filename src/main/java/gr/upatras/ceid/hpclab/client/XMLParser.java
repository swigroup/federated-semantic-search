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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author koutsomi
 */
class XMLParser {

    private final ObjectFactory fact = new ObjectFactory();
    private final Document doc;

    XMLParser() {
        doc = null;
    }

    XMLParser(InputStream xml) throws
            SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        /*InputSource is = new InputSource();
         is.setCharacterStream(new StringReader(xml));*/
        doc = db.parse(xml);
    }

    ResultType createResult(String title, String description, String URL, Repository res) {
        //add only results that have a URL
        if (URL != null && !URL.equals("")) {
            ResultType r = fact.createResultType();
            String origin = res.name();
            description = description == null ? "null" : description;
            title = title == null ? "null" : title;
            r.setDescription("(" + origin + ") " + description);
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
 * Search dc:description within item's metadata, since it is not returned in
 * search results by OpenArchives API.
 * @param inputUri
 * @param key
 * @return the value of the xml element matching the key (dc:description)
 */
    static String getValueFromURL(String inputUri, String key) {
        String value = null;
        try {
            URI uri = new URI(inputUri);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            //conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setConnectTimeout(1 * 1000);
            conn.setReadTimeout(2 * 1000);
            conn.setRequestProperty("Accept", "application/xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(conn.getInputStream());
            NodeList nodes = doc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", key);
            for (int i = 0; i < nodes.getLength(); i++) {
                value = "";
                Element entry = (Element) nodes.item(i);
                if (entry != null) {
                    if (!value.equals("")) value +=" ";
                    value += entry.getTextContent();
                }
            }
        } catch (SAXException | ParserConfigurationException | URISyntaxException | IOException ex) {
            Logger.getLogger(JSONParser.class.getName()).
                    log(Level.WARNING, "error getting " + key + " from " + inputUri + ". ", ex.getMessage());
        } finally {
            return value;
        }
    }

    /**
     * @return the doc
     */
    Document getDocument() {
        return doc;
    }

}
