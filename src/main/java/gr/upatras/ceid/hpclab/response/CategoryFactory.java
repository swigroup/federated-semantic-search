/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.response;

import gr.upatras.ceid.hpclab.client.QueryClientManager;
import gr.upatras.ceid.hpclab.owl.SKOSConcept;
import gr.upatras.ceid.hpclab.query.QueryManager;
import gr.upatras.ceid.hpclab.query.QueryTerm;
import gr.upatras.ceid.hpclab.response.model.CategoryType;
import gr.upatras.ceid.hpclab.response.model.ConceptType;
import gr.upatras.ceid.hpclab.response.model.KeywordType;
import gr.upatras.ceid.hpclab.response.model.KeywordType.Label;
import gr.upatras.ceid.hpclab.response.model.ObjectFactory;
import gr.upatras.ceid.hpclab.response.model.ResultType;
import gr.upatras.ceid.hpclab.response.model.TranslationType;
import gr.upatras.ceid.hpclab.validator.HFModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author koutsomi
 */
public class CategoryFactory {

    private final ObjectFactory fact;
    private final QueryClientManager client;
    private final QueryManager qm;
    private final HFModel model = new HFModel();
    private QueryTerm keyword;
    private CategoryType ct;
    private boolean validate;

    public CategoryFactory(QueryManager qm) {
        this.client = new QueryClientManager();
        this.fact = new ObjectFactory();
        this.qm = qm;
    }

    private Set<ResultType> returnResults(String query) {
        return client.getResultsFromAllRepositories(query);
    }

    private Boolean addResultsListToCategory(Set<ResultType> resultsList) {

        return ct.getResult().addAll(resultsList);
    }

    private void buildMatchingConcepts(Set<SKOSConcept> concepts) {
        for (SKOSConcept c : concepts) {
            ConceptType cons = fact.createConceptType();
            /*cons.setLabel(qt.getLabel());
            if (qt.getLang() != null) {
                cons.setLang(qt.getLang());
            }*/
            cons.setIRI(c.getIRI().toString());
            ct.getMatchingConcept().add(cons);
        }
    }

    private void buildTranslations() {
        Set<QueryTerm> translations = qm.getTranslations(keyword);
        for (QueryTerm t : translations) {
            TranslationType trans = fact.createTranslationType();
            trans.setLang(t.getLang());
            trans.setValue(t.getLabel());
            ct.getAlterTranslation().add(trans);
        }
    }

    private void buildCategory() {
        ct.setKeyword(keyword.getLabel());
        ct.setLang(keyword.getLang());
        buildTranslations();
    }

    private void addLabelsToKeyword(KeywordType kt, CategoryType ct) {
        KeywordType.Label l = fact.createKeywordTypeLabel();
        l.setValue(ct.getKeyword());
        l.setLang(ct.getLang());
        kt.getLabel().add(l);
    }

    /**
     *
     * @param kt
     * @param queryTermSet
     * @return whether an inserted label matches the category keyword, so the
     * latter should be removed.
     */
    private boolean addLabelsToKeyword(KeywordType kt, Set<QueryTerm> queryTermSet) {
        boolean value = false;
        for (QueryTerm s : queryTermSet) {
            KeywordType.Label l = fact.createKeywordTypeLabel();
            l.setValue(s.getLabel());
            l.setLang(s.getLang());
            kt.getLabel().add(l);
            if (s.getLabel().equalsIgnoreCase(ct.getKeyword())) {
                //keyword already exists with an IRI
                value = true;
            }
        }
        return value;
    }

    private void addKeywordsToResultsInCategory(Set<SKOSConcept> concepts) {
        KeywordType kw = fact.createKeywordType();
        addLabelsToKeyword(kw, ct);
        for (ResultType rt : ct.getResult()) {
            if (rt != null) {
                rt.getKeyword().add(kw);
                for (SKOSConcept cons : concepts) {
                    KeywordType kt = fact.createKeywordType();
                    kt.setIRI(cons.getIRI().toString());
                    if (addLabelsToKeyword(kt, cons.getPrefLabels())) {
                        rt.getKeyword().remove(kw);
                    }
                    rt.getKeyword().add(kt);
                }
                if (validate) {
                    for (KeywordType kt : rt.getKeyword()) {
                        computeScoreForKeyword(kt, rt);
                    }
                }
            }
        }
    }

    private void computeScoreForKeyword(KeywordType kt, ResultType rt) {
        String input = rt.getTitle() + " " + rt.getDescription();
        //clear HTML tags
        input = input.replaceAll("\\<.*?\\>", "");
        //Label is a pair with xml:lang and value
        List<Label> labels = kt.getLabel();
        List<String> labelslist = new ArrayList<String>();
        for (Label l : labels) {
            labelslist.add(l.getValue());
        }
        double score = model.getScore(input, labelslist);
        if (score != -1) {
            kt.setScore(score);
        }
    }

    protected CategoryType createCategory(QueryTerm keyword, boolean validate) {
        this.validate = validate;
        this.keyword = keyword;
        this.ct = fact.createCategoryType();
        Set<SKOSConcept> concepts = qm.getMatchingConcepts(keyword);
        buildCategory();
        buildMatchingConcepts(concepts);
        addResultsListToCategory(returnResults(keyword.getLabel()));
        addKeywordsToResultsInCategory(concepts);
        return ct;
    }

}
