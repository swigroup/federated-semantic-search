/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import gr.upatras.ceid.hpclab.response.model.ResultType;
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
class OpenArchivesConnectorImpl implements RepositoryConnector {

    @Override
    public Set parseResponse(InputStream xml) {
        Set<ResultType> resultsList = new HashSet<>();

        try {
            XMLParser parser = new XMLParser(xml);
            Document doc = parser.getDocument();
            NodeList nodes = doc.getDocumentElement().getElementsByTagName("entry");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element entry = (Element) nodes.item(i);
                if (entry != null) {
                    String desc
                            = parser.getChildElement(entry, "content", "type", "text");
                    String tit
                            = parser.getChildElement(entry, "title");
                    String lin
                            = parser.getChildElement(entry, "dc:identifier");
                    ResultType r
                            = parser.createResult(tit, desc, lin, Repository.findRepoFromClass(this.getClass()));
                    resultsList.add(r); //unique results are added (per URL)
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
        //limit to first 50 results (make it 25?)
        request = Repository.OPENARCHIVES.getURL() + "/" + query + "/limit:25";
        return request;
    }
}
