/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.response;

import gr.upatras.ceid.hpclab.client.QueryClientManager;
import gr.upatras.ceid.hpclab.owl.ConceptMatcher;
import gr.upatras.ceid.hpclab.owl.OntologyManager;
import gr.upatras.ceid.hpclab.owl.SKOSConcept;
import gr.upatras.ceid.hpclab.query.QueryManager;
import gr.upatras.ceid.hpclab.query.QueryTerm;
import gr.upatras.ceid.hpclab.response.model.CategoryType;
import gr.upatras.ceid.hpclab.response.model.ObjectFactory;
import gr.upatras.ceid.hpclab.response.model.ResultType;
import gr.upatras.ceid.hpclab.response.model.Results;
import gr.upatras.ceid.hpclab.response.model.TranslationType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author koutsomi
 */
public class PrepareResponseWrapper {

    private final ObjectFactory fact = new ObjectFactory();
    private final Results res = fact.createResults();
    private final QueryManager qm = new QueryManager();
    private final QueryClientManager client = new QueryClientManager();
    private final OntologyManager om = new OntologyManager();

    private Set<ResultType> returnResults(String query) {
        return client.getResultsFromAllRepositories(query);
    }

    private Boolean addResultsListToCategory(CategoryType ct, Set<ResultType> resultsList) {

        return ct.getResult().addAll(resultsList);
    }

    private void buildTranslations(QueryTerm keyword, CategoryType ct) {
        Set<QueryTerm> translations = qm.getTranslations(keyword);
        for (QueryTerm t : translations) {
            TranslationType trans = fact.createTranslationType();
            trans.setLang(t.getLang());
            trans.setValue(t.getLabel());
            ct.getAlterTranslation().add(trans);
        }
    }

    private CategoryType buildCategory(QueryTerm keyword) {
        CategoryType ct = fact.createCategoryType();
        ct.setKeyword(keyword.getLabel());
        ct.setLang(keyword.getLang());
        buildTranslations(keyword, ct);
        res.getCategory().add(ct);
        return ct;
    }

    private Boolean buildResults(QueryTerm keyword) {
        CategoryType ct = buildCategory(keyword);
        return addResultsListToCategory(ct, returnResults(keyword.getLabel()));
    }

    public Results getResults(List<String> keywords) {
        Set temp = new HashSet();

        for (String keyword : polishKeywordSet(keywords)) {

            qm.addQueryTerm(keyword, null);
            //Expand keywords
            ConceptMatcher cm = new ConceptMatcher(om);
            buildQuerySet(cm.expandKeyword(keyword));

            /*KeywordTerms: Terms for current keyword
             temp: contains terms for previous keywords
             So, remove possibly overlapping terms in current terms set,
             then keep term history in temp.
             Point is to avoid duplicate queries, for all keywords supplied.
             
             The counter threshold set below would limit the expanded 
             queries per keyword. If  the threshold is reached before the
             available terms run out, the next iteration (next keyword) would 
             start with a fresh set of terms, only for the particular keyword.
            
             */
            Set keywordTerms = qm.getQueryTuples().keySet();
            keywordTerms.removeAll(temp);
            temp.addAll(keywordTerms);
            Iterator<QueryTerm> it = keywordTerms.iterator();
            int counter = 0;
            while (counter < 4 && it.hasNext()) {
                QueryTerm v = it.next();
                buildResults(v);
                counter++;
            }
            qm.getQueryTuples().keySet().clear(); //possibly reduntant
        }
        return res;
    }

    void buildQuerySet(Set<SKOSConcept> concepts) {
        for (SKOSConcept sc : concepts) {

            for (QueryTerm t : sc.getPrefLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getPrefLabels());
                    translations.remove(t);
                    qm.putTranslations(t, translations);
                }
            }
            for (QueryTerm t : sc.getAltLabels()) {
                if (t != null) {
                    Set translations = new HashSet();
                    translations.addAll(sc.getAltLabels());
                    translations.remove(t);
                    qm.putTranslations(t, translations);
                }
            }
        }
    }

    private List<String> polishKeywordSet(List<String> keywords) {
        List keys = new ArrayList();
        for (String keyword : keywords) {
            if (keyword != null) {
                keyword = keyword.replaceAll("\\s+", " ").trim();
                if (!keyword.equals("")) {
                    keys.add(keyword.toLowerCase());
                }
            }
        }
        return keys;
    }
}
