/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.dataverse.duth.semantic.client;

import gr.dataverse.duth.semantic.response.model.ResultType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author koutsomi
 */
class OJSConnectorImpl implements RepositoryConnector {

    @Override
    public Set parseResponse(InputStream xml) {
        Set<ResultType> resultsList = new HashSet<>();

        try {
            XMLParser parser = new XMLParser(xml);
            Document doc = parser.getDocument();
            NodeList nodes = doc.getDocumentElement().getElementsByTagName("article");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element entry = (Element) nodes.item(i);
                if (entry != null) {
                    String desc
                            = parser.getChildElement(entry, "abstract");
                    String tit
                            = parser.getChildElement(entry, "title");
                    String lin
                            = parser.getChildElement(entry, "url");
                    ResultType r
                            = parser.createResult(tit, desc, lin, Repository.findRepoFromClass(this.getClass()));
                    resultsList.add(r);
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultsList;
    }

    @Override
    public String buildConnectionUrl(String query) {
        String request;
        //Get the first 10 results.
        request = Repository.OJS.getURL() + "/10/" + query;
        return request;
    }
}
