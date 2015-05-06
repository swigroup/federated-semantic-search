/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.query;

import gr.dataverse.duth.semantic.owl.SKOSConcept;
import gr.dataverse.duth.semantic.response.PrepareResponseWrapper;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author koutsomi
 */
public class QueryManager {

    private final LinkedHashMap<QueryTerm, Set<QueryTerm>> queryTuples
            = new LinkedHashMap<>(); // L(v). Keys are unique per label only (not lang).

    public QueryTerm addQueryTerm(String label, String lang) {
        QueryTerm qt = new QueryTerm(label, lang);
        addTranslation(qt, null);
        return qt;
    }

    public Set<QueryTerm> getTranslations(QueryTerm t) {
        return queryTuples.get(t);
    }

    private Set<QueryTerm> putTranslations(QueryTerm t, Set<QueryTerm> translations) {
        //in case there is a matching term from the ontology, prefer it.
        if (queryTuples.containsKey(t)) {
            queryTuples.remove(t);
        }
        return queryTuples.put(t, translations);
    }

    private Boolean addTranslation(QueryTerm t, QueryTerm l) {
        Set<QueryTerm> translations = this.getTranslations(t);
        if (translations == null) {
            translations = new HashSet<>(); //initially empty
            this.putTranslations(t, translations);
        }
        if (l != null) {
            return translations.add(l);
        }
        return false;
    }

    /**
     * @return the queryTuples
     */
    public Map<QueryTerm, Set<QueryTerm>> getQueryTuples() {
        return queryTuples;
    }

    public void buildQuerySet(Set<SKOSConcept> concepts, PrepareResponseWrapper prepareResponseWrapper) {
        for (SKOSConcept sc : concepts) {
            for (QueryTerm t : sc.getPrefLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getPrefLabels());
                    translations.remove(t);
                    putTranslations(t, translations);
                }
            }
            for (QueryTerm t : sc.getAltLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getAltLabels());
                    translations.remove(t);
                    putTranslations(t, translations);
                }
            }
        }
    }

}
