/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.query;

import gr.upatras.ceid.hpclab.owl.SKOSConcept;
import gr.upatras.ceid.hpclab.response.PrepareResponseWrapper;
import java.util.Arrays;
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

    private final LinkedHashMap<QueryTerm, Set<SKOSConcept>> termConcepts
            = new LinkedHashMap<>(); // Each QueryTerm carries its matching concepts.

    public QueryTerm addQueryTerm(String label, String lang) {
        QueryTerm qt = new QueryTerm(label, lang);
        addTranslation(qt, null);
        return qt;
    }

    public Set<QueryTerm> getTranslations(QueryTerm t) {
        return queryTuples.get(t);
    }

    public Set<SKOSConcept> getMatchingConcepts(QueryTerm t) {
        if (termConcepts.get(t) == null) {
            return new HashSet<>();
        } else {
            return termConcepts.get(t);
        }
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
                    termConcepts.put(t, new HashSet<>(Arrays.asList(sc)));
                }
            }
            for (QueryTerm t : sc.getAltLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getAltLabels());
                    translations.remove(t);
                    putTranslations(t, translations);
                    termConcepts.put(t, concepts);
                }
            }
        }
    }

}
