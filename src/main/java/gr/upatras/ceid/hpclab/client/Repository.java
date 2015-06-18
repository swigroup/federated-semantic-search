/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.client;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author koutsomi
 */
enum Repository {

    OPENARCHIVES("gr.upatras.ceid.hpclab.client.OpenArchivesConnectorImpl",
            "https://openarchives.gr/aggregator-openarchives/api/search.json", Mappings.OPENARCHIVES_MAPPING),
    PUBMED("gr.upatras.ceid.hpclab.client.PubMedConnectorImpl",
            "http://www.ebi.ac.uk/europepmc/webservices/rest/search", Mappings.PUBMED_MAPPING),
    MERLOT("gr.upatras.ceid.hpclab.client.MerlotConnectorImpl",
            "http://www.merlot.org/merlot/materials.rest", Mappings.PUBMED_MAPPING),
    OJS("gr.upatras.ceid.hpclab.client.OJSConnectorImpl",
            "http://83.212.133.37/ojs/index.php/MedCom/gateway/plugin/RestPlugin/searchArticleXML", Mappings.PUBMED_MAPPING);


    private final String classImpl;
    private final String URL;
    private final Map<MapFields, String> mapping;

    private Repository(String classImpl, String URL, Map mapping) {
        this.classImpl = classImpl;
        this.URL = URL;
        this.mapping = mapping;
    }

    /**
     * @return the classImpl
     */
    @Override
    public String toString() {
        return classImpl;
    }

    /**
     * @return the URL
     */
    public String getURL() {
        return URL;
    }

    public static Repository findRepoFromClass(Class<? extends RepositoryConnector> c) {
        Repository[] repositories = Repository.values();
        for (Repository res : repositories) {
            if (res.classImpl.equals(c.getName())) {
                return res;
            }
        }
        return null;
    }

    private static enum MapFields {

        ROOT,
        TITLE,
        DESCRIPTION,
        URL
    }

    private static class Mappings {

        private static final Map<MapFields, String> OPENARCHIVES_MAPPING = new EnumMap<>(MapFields.class);

        static {
            OPENARCHIVES_MAPPING.put(MapFields.ROOT, "entry");

        }
        private static final Map<MapFields, String> PUBMED_MAPPING = new EnumMap<>(MapFields.class);

        static {
            PUBMED_MAPPING.put(MapFields.ROOT, "result");

        }
    }
}
