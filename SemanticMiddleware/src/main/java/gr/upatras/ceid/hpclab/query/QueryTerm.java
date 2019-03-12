/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.query;

import java.util.Objects;

/**
 *
 * @author koutsomi
 */
public class QueryTerm {

    private final String label;
    private final String lang;

    public QueryTerm(String term, String lang) {
        this.label = term;
        this.lang = lang;
    }

    public QueryTerm(String term) {
        this(term, null);
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.label.toLowerCase());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryTerm other = (QueryTerm) obj;
        if (!this.label.toLowerCase().equals(other.label.toLowerCase())) {
            return false;
        }
        return true;
    }



}
