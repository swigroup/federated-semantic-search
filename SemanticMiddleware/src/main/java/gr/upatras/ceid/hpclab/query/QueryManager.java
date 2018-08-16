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

    private QueryTerm keywordTerm = new QueryTerm(null);

    public QueryTerm addQueryTerm(String label, String lang, Set<SKOSConcept> matches) {
        //get proper lang for term (search within concept matches)
        for (SKOSConcept sc : matches) {
            for (QueryTerm t : sc.getPrefLabels()) {
                if (t != null && t.getLabel().contains(label)) {
                    lang = t.getLang();
                }
            }
            for (QueryTerm t : sc.getAltLabels()) {
                if (t != null && t.getLabel().contains(label)) {
                    lang = t.getLang();
                }
            }
        }
        QueryTerm qt = new QueryTerm(label, lang);
        addTranslation(qt, null);
        termConcepts.put(qt, matches);
        return keywordTerm = qt;
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
        //Need to remove first, to force replace the key (with lang).
        /**
         * note: we already get the lang when adding the term *
         */
        // This may break ordering, so suppose matching terms come first.
        /*if (queryTuples.containsKey(t)) {
         queryTuples.remove(t);
         }*/
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

    public void buildQuerySet(Set<SKOSConcept> concepts) {
        for (SKOSConcept sc : concepts) {
            for (QueryTerm t : sc.getPrefLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getPrefLabels());
                    translations.remove(t);
                    putTranslations(t, translations);
                    putConcepts(t, sc);
                }
            }
            for (QueryTerm t : sc.getAltLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getAltLabels());
                    translations.remove(t);
                    putTranslations(t, translations);
                    putConcepts(t, sc);
                }
            }
        }
    }

    private void putConcepts(QueryTerm t, SKOSConcept sc) {
        Set<SKOSConcept> concepts = new HashSet<>();
        if (termConcepts.containsKey(t)) {
            //they are already there, but put replaces
            concepts.addAll(termConcepts.get(t));
        }
        //add also parent concept,  one that exactly matches the initial 
        // keyword if any (usually the first). 
        /*TODO: get the whole broader path to parent. 
                Currently sets are flattened*/
        if (keywordTerm != null && !termConcepts.get(keywordTerm).isEmpty()) {
            concepts.add(termConcepts.get(keywordTerm).iterator().next());
        }
        //For initial queryTerms, it is already there
        concepts.add(sc);
        termConcepts.put(t, concepts);
    }

}
