/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.validator;

import gr.upatras.ceid.hpclab.client.JSONParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author koutsomi
 */
public class HFModel {

    private HFConnector hf;

    public HFModel(HFConnector hf) {
        this.hf = hf;
    }

    public HFModel() {
        this.hf = new HFConnector(HFConnector.Model.XLMR); //default
    }

    JSONObject parseResponse(InputStream json) {
        JSONParser jparser = new JSONParser();
        JSONObject js = jparser.parse(json);
        return js;
    }

    public double getScore(String text, Collection labels) {
        double score = -1;
        String payload = buildPayload(text, labels);
        try {
            InputStream is = hf.submit(payload);
            JSONObject json = parseResponse(is);
            JSONArray scoresArray = json.getJSONArray("scores");

            //implement argmax
            List<Double> scoresList = new ArrayList<>();
            for (Object s : scoresArray) {
                scoresList.add((Double) s);
            }
            Double maxScore = Collections.max(scoresList);
            int index = scoresList.indexOf(maxScore);
            score = maxScore;
        } catch (Exception ex) {
            Logger.getLogger(HFModel.class.getName()).log(Level.WARNING,
                    "Failed getting scores from: " + hf.model, ex);
        }
        return Math.floor(score * 100) / 100;

    }

    String buildPayload(String text, Collection labels) {
        JSONObject payload = new JSONObject();
        payload.put("inputs", text);
        JSONObject parameters = new JSONObject();
        parameters.put("multi_label", "true");
        JSONArray labelsArray = new JSONArray(labels);
        parameters.put("candidate_labels", labelsArray);
        payload.put("parameters", parameters);
        return payload.toString();

    }
}
