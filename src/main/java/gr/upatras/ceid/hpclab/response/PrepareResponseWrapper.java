/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.response;

import gr.upatras.ceid.hpclab.owl.ConceptMatcher;
import gr.upatras.ceid.hpclab.owl.OntologyManager;
import gr.upatras.ceid.hpclab.owl.SKOSConcept;
import gr.upatras.ceid.hpclab.query.QueryManager;
import gr.upatras.ceid.hpclab.query.QueryTerm;
import gr.upatras.ceid.hpclab.response.model.*;
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
    private final OntologyManager om = new OntologyManager();
    private final CategoryFactory cf = new CategoryFactory(qm);

    public Results getResults(List<String> keywords, boolean validate) {
        Set temp = new HashSet();

        for (String keyword : polishKeywordSet(keywords)) {

            //Expand keywords
            ConceptMatcher cm = new ConceptMatcher(om);
            //get direct matches first
            Set<SKOSConcept> matches = cm.getMatchingConcepts(keyword);
            //add the term and top-level matches
            qm.addQueryTerm(keyword, null, matches);
            //reuse them for expansion
            qm.buildQuerySet(cm.expandKeyword(matches));

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
                CategoryType ct = cf.createCategory(v, validate);
                res.getCategory().add(ct);
                counter++;
            }
            qm.getQueryTuples().keySet().clear(); //possibly reduntant
        }
        return res;
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
