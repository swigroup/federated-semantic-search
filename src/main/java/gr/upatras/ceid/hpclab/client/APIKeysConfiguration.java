/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author koutsomi
 */
class APIKeysConfiguration {

    private PropertiesConfiguration prop = new PropertiesConfiguration();

    APIKeysConfiguration() {
        try {
            // load a properties file
            prop = new PropertiesConfiguration("keys");
        } catch (ConfigurationException ex) {
            Logger.getLogger(APIKeysConfiguration.class.getName()).log(Level.SEVERE,
                    "Cannot find/read keys file", ex);
        }
    }

    String getKey(String name) {

        String key = prop.getStringArray(name)[0];
        if (key == null || key.equals("")) {

            Logger.getLogger(APIKeysConfiguration.class.getName()).
                    log(Level.WARNING, "API key  is empty for {0}", name);
        }
        return key;
    }

}
