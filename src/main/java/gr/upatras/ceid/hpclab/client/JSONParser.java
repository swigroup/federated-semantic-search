/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author koutsomi
 */
public class JSONParser {

    static JSONObject parse(InputStream json) {
        BufferedReader in = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8));
        JSONTokener tokener = new JSONTokener(in);
        JSONObject js = new JSONObject(tokener);
        return js;
    }

    static String getValueFromURL(String inputUri, String key) {
        String value = "";
        try {
            URI uri = new URI(inputUri);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            JSONObject element = parse(conn.getInputStream());
            JSONArray nodes = element.getJSONArray("@graph");
            boolean found = false;
            int i = 0;
            JSONObject js;
            while (!found) {
                js = nodes.getJSONObject(i);
                if (!js.isNull(key)) {
                    value = js.getString(key);
                    found = true;
                }
                i++;
            }

            return value;
        } catch (URISyntaxException | IOException ex ) {
            Logger.getLogger(JSONParser.class.getName()).
                    log(Level.WARNING, "error getting " + key + " from " + inputUri + ". ", ex);
        } finally {
            return value;
        }
    }
}
