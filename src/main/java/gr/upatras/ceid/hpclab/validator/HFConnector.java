/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.validator;

import gr.upatras.ceid.hpclab.client.APIKeysConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;

class HFConnector {

    private static final APIKeysConfiguration keys = new APIKeysConfiguration();
    private static final String apiKey = keys.getKey("hf_key");
    private static final String endpoint = "https://api-inference.huggingface.co/models/";
    protected Model model;

    enum Model {
        BART("facebook/bart-large-mnli"), XLMR("joeddav/xlm-roberta-large-xnli");

        private String modelID;

        private Model(String modelID) {
            this.modelID = modelID;
        }

        public String getModelID() {
            return modelID;
        }

        public void setModelID(String modelID) {
            this.modelID = modelID;
        }

    }

    HFConnector (Model model) {
        this.model = model;
    }
    

    InputStream submit(String payload) throws Exception {

        Client client = Client.create();
        String uri = endpoint + model.getModelID();
        WebResource webResource = client.resource(uri);

        MultivaluedMap<String, String> queryPStrarams = new MultivaluedMapImpl();
        //Get response from RESTful Server get(ClientResponse.class);
        ClientResponse response = webResource
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Authorization", "Bearer " + apiKey)
                .post(ClientResponse.class, payload);
        if (response.getStatus() != 200) {
            throw new Exception("Failed getting scores from: "
                    + model
                    + ". HTTP error code : "
                    + response.getStatus()
                    + " URI: " + uri
                    + " JSON: " + payload
                    + " response: " + response.toString()
            );
        }
        InputStream output = response.getEntityInputStream();
        return output;
    }

}
