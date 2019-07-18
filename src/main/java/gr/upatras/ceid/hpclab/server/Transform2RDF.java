/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.server;

/**
 *
 * @author koutsomi
 */
import gr.upatras.ceid.hpclab.response.model.Results;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public  class Transform2RDF {

    private static String realPath;
    private static TransformerFactory tf;
    private static StreamSource xslt;

    static void transform(Results r, StreamResult result, ServletContext context)
      {
        try {
            realPath = context.getRealPath("/WEB-INF/classes/2LOM.xsl");
            xslt = new StreamSource(realPath);
            
            tf = TransformerFactory.newInstance();
            JAXBContext jc = JAXBContext.newInstance(Results.class);
            JAXBSource source = new JAXBSource(jc, r);
            Transformer transformer = tf.newTransformer(xslt);
            transformer.transform(source, result);
        } catch (JAXBException ex) {
            Logger.getLogger(Transform2RDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Transform2RDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Transform2RDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}
