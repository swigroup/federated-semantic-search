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
import java.io.InputStream;

class HFConnector {

    private static final APIKeysConfiguration keys = new APIKeysConfiguration();
    private static final String apiKey = keys.getKey("hf_key");
    private static final String endpoint = "https://api-inference.huggingface.co/models/";
    private static final Client client;
    private final WebResource webResource;
    protected Model model;
    
    static {
        client = Client.create();
        client.setConnectTimeout(500);
        client.setReadTimeout(1000);
    }

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
   
        String uri = endpoint + model.getModelID();
        webResource = client.resource(uri);
    }
    

    InputStream submit(String payload) throws Exception {

       
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
                    + " URI: " + webResource.getURI()
                    + " JSON: " + payload
                    + " response: " + response.toString()
            );
        }
        InputStream output = response.getEntityInputStream();
        return output;
    }

}
