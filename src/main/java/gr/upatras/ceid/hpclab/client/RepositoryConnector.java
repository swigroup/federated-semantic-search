/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import java.io.InputStream;
import java.util.Set;

/**
 *
 * @author koutsomi
 */
interface RepositoryConnector {

    String buildConnectionUrl(String query);

    Set parseResponse(InputStream xml);

}
