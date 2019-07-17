/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.owl;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author koutsomi
 */
class OntologyConfiguration {

    private PropertiesConfiguration prop = new PropertiesConfiguration();

    OntologyConfiguration() {
        try {
            // load a properties file
            prop = new PropertiesConfiguration("ontologies.cfg");
        } catch (ConfigurationException ex) {
            Logger.getLogger(OntologyConfiguration.class.getName()).log(Level.SEVERE,
                    "Cannot find/read ontologies.cfg", ex);
        }
    }

    Set<String> getOntologyList() {

        Set<String> values = new HashSet();
        UrlValidator urlValidator = new UrlValidator();
        String[] ontologyList = prop.getStringArray("ontology");
        for (String value : ontologyList) {
            if (urlValidator.isValid(value)) {
                values.add(value);
            } else if (getClass().getResource(value) != null) {
                URL url = getClass().getResource(value);
                values.add(url.toString());
            } else {
                Logger.getLogger(OntologyConfiguration.class.getName()).log(Level.WARNING,
                        "Ontology URL {0} is not valid. Ignoring.", value);
            }
        }
        return values;
    }

}
