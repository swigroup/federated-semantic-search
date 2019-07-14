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
     *
     * @param json
     * @return
     */
    private static final APIKeysConfiguration keys = new APIKeysConfiguration();
    private static final String apiKey = keys.getKey("oa_key");

    @Override
    public Set parseResponse(InputStream json) {
        Set<ResultType> resultsList = new HashSet<>();

        XMLParser parser = new XMLParser();
        JSONParser jparser = new JSONParser();
        JSONObject js = jparser.parse(json);
        JSONArray nodes = js.getJSONArray("results");
        //50 results per page, limit to 25.
        for (int i = 0; i < nodes.length() && i < 25; i++) {
            JSONObject entry = nodes.getJSONObject(i);
            if (entry != null) {
                String desc;
                //description may exist in the full item metadata.
                //Use XML because JSON seems broken 
                if (!entry.isNull("description")) {
                    desc = entry.getString("description");
                } else {
                    desc = XMLParser.getValueFromURL(entry.getString("uri"), "description");
                }
                String tit = entry.isNull("dc_title") ? "" : entry.getJSONArray("dc_title").getString(0);

                String lin = entry.isNull("edm_isShownAt") ? "" : entry.getString("edm_isShownAt");

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
        request = Repository.OPENARCHIVES.getURL() + "?apiKey=" + apiKey + "&general_term=" + query;
        return request;
    }
}
