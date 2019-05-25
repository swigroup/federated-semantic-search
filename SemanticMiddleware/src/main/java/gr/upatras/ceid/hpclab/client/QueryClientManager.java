/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import gr.upatras.ceid.hpclab.response.model.ResultType;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koutsomi
 */
public class QueryClientManager {

    private static Client client;

    public QueryClientManager() {
        client = Client.create();
        client.setConnectTimeout(500);
        client.setReadTimeout(5000);
    }

    public Set getResultsFromAllRepositories(String query) {

        HashSet<ResultType> totalResults = new HashSet<>();
        for (Repository r : Repository.values()) {
            try {
                //this is a hashset, so no overlapping results (per category)
                totalResults.addAll(getResultsFromRepository(r, query));
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(QueryClientManager.class.getName()).log(Level.SEVERE,
                        "Failed getting results from: " + r.name(), ex);
            } catch (ClientHandlerException ex) {
                Logger.getLogger(QueryClientManager.class.getName()).log(Level.SEVERE,
                        "Timeout getting results from: " + r.name(), ex);
            } catch (Exception ex) {
                Logger.getLogger(QueryClientManager.class.getName()).log(Level.SEVERE, "Failed getting results from: " + r.name(), ex);
            }
        }
        return totalResults;
    }

    private Set getResultsFromRepository(Repository res, String query) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
        String request;
        RepositoryConnector rc
                = (RepositoryConnector) Class.forName(res.toString()).newInstance();
        request = rc.buildConnectionUrl(URLEncoder.encode(query, "UTF-8"));
        WebResource webResource = client.resource(request);
        ClientResponse response = webResource.accept("application/xml, application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new Exception("Failed access to repository: "
                    + res.name()
                    + ". HTTP error code : "
                    + response.getStatus());
        }
        InputStream output = response.getEntityInputStream();
        return rc.parseResponse(output);
    }

}
