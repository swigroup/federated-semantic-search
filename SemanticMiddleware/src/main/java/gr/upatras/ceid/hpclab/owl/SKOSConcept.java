/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.owl;

import gr.upatras.ceid.hpclab.query.QueryTerm;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 *
 * @author koutsomi
 */
public class SKOSConcept {

    private final Set<QueryTerm> prefLabels;
    private final Set<QueryTerm> altLabels;
    private OWLIndividual individual;

    private SKOSConcept() {
        prefLabels = new HashSet();
        altLabels = new HashSet();

    }

    public static SKOSConcept buildSKOSConceptFromIndividual(OWLIndividual in,
            OntologyManager manager) {
        SKOSConcept sc = new SKOSConcept();
        sc.individual = in;
        setPrefLabels(sc, manager, "skos:prefLabel");
        setAltLabels(sc, manager, "skos:altLabel");
        return sc;
    }

    private static void setPrefLabels(SKOSConcept sc, OntologyManager manager, String property) {
        OWLOntologyManager m = manager.getManager();
        PrefixManager pm = manager.getPrefixManager();
        OWLAnnotationProperty label
                = m.getOWLDataFactory().getOWLAnnotationProperty(property, pm);
        Collection<OWLAnnotation> labelAnnotations
                = EntitySearcher.getAnnotations(
                        (OWLEntity) sc.individual, m.getOntologies(), label);
        for (OWLAnnotation a : labelAnnotations) {
            OWLLiteral l = a.getValue().asLiteral().get();
            QueryTerm qt = new QueryTerm(l.getLiteral(), l.getLang());
            sc.getPrefLabels().add(qt);

        }
    }

    private static void setAltLabels(SKOSConcept sc, OntologyManager manager, String property) {
        OWLOntologyManager m = manager.getManager();
        PrefixManager pm = manager.getPrefixManager();
        OWLAnnotationProperty label
                = m.getOWLDataFactory().getOWLAnnotationProperty(property, pm);
        Collection<OWLAnnotation> labelAnnotations
                = EntitySearcher.getAnnotations(
                        (OWLEntity) sc.individual, m.getOntologies(), label);
        for (OWLAnnotation a : labelAnnotations) {
            OWLLiteral l = a.getValue().asLiteral().get();
            QueryTerm qt = new QueryTerm(l.getLiteral(), l.getLang());
            sc.getAltLabels().add(qt);
        }
    }

    /**
     * @return the prefLabels
     */
    public Set<QueryTerm> getPrefLabels() {
        return prefLabels;
    }

    /**
     * @return the altLabels
     */
    public Set<QueryTerm> getAltLabels() {
        return altLabels;
    }

    /**
     * @return the individual
     */
    public OWLIndividual getIndividual() {
        return individual;
    }
    
    public IRI getIRI() {
        return individual.asOWLNamedIndividual().getIRI();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.individual);
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
        final SKOSConcept other = (SKOSConcept) obj;
        if (!Objects.equals(this.individual, other.individual)) {
            return false;
        }
        return true;
    }

}
