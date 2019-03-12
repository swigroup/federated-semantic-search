/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import gr.upatras.ceid.hpclab.response.model.ResultType;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author koutsomi
 */
class OpenArchivesConnectorImpl implements RepositoryConnector {
    /**
     * Connect to the new OpenArchives.gr API that supports json.
     * @param json
     * @return 
     */
    @Override
    public Set parseResponse(InputStream json) {
        Set<ResultType> resultsList = new HashSet<>();

        XMLParser parser = new XMLParser();
        JSONObject js = new JSONObject(json);
        JSONArray nodes = js.getJSONArray("results");
        for (int i = 0; i < nodes.length(); i++) {
            JSONObject entry = nodes.getJSONObject(i);
            if (entry != null) {
                String desc
                        = entry.getString("desc");
                String tit
                        = entry.getString("title");
                String lin
                        = entry.getString("edm_isShownAt");
                ResultType r
                        = parser.createResult(tit, desc, lin, Repository.findRepoFromClass(this.getClass()));
                resultsList.add(r); //unique results are added (per URL)
            }
        }
        return resultsList;
    }

    /**
     * @param query
     * @return 
     */
    @Override
    public String buildConnectionUrl(String query) {
        String request;
        //limit to first 50 results (make it 25?)
        request = Repository.OPENARCHIVES.getURL() + "/" + query + "/limit:25";
        return request;
    }
}
