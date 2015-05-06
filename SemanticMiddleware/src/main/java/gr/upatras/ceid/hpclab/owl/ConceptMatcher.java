/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.owl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 *
 * @author koutsomi
 */
public class ConceptMatcher {

    private static final int LIMIT = 500; //THRESHOLD of concepts added
    private int size = 0;

    private final OWLOntologyManager m;
    private final OntologyManager manager;
    private final PrefixManager pm;

    public ConceptMatcher(OntologyManager manager) {
        this.manager = manager;
        m = manager.getManager();
        pm = manager.getPrefixManager();
    }

    /**
     * 
     * @param keyword
     * @return The set of matching concepts and their refinements
     * for the keyword.
     * Notice that there is a LIMIT in the number of concepts.
     */
    public Set<SKOSConcept> expandKeyword(String keyword) {
        Set<SKOSConcept> expanded = new LinkedHashSet<>();
        Set<SKOSConcept> matches = getMatchingConcepts(keyword);
        expanded.addAll(matches);
        for (SKOSConcept sc : matches) {
            expanded.addAll(getConceptRefinements(sc));
        }
        return expanded;
    }
    
    public Set<SKOSConcept> getConceptRefinements(SKOSConcept root) {
        Set<SKOSConcept> resultSet = new LinkedHashSet<>();
        Set<SKOSConcept> temp = new LinkedHashSet<>();
        if (size < LIMIT) {
            resultSet.addAll(getShallowConceptRefinements(root));
            temp.addAll(resultSet);
            size += temp.size();
            for (SKOSConcept sc : resultSet) {
                temp.addAll(getConceptRefinements(sc));
            }
        }
        return temp;
    }

    private Set<SKOSConcept> getShallowConceptRefinements(SKOSConcept root) {

        Set<SKOSConcept> resultSet = new LinkedHashSet<>();
        OWLIndividual rootConcept = root.getIndividual();
        OWLObjectProperty broader
                = m.getOWLDataFactory().getOWLObjectProperty("skos:broader", pm);
        OWLObjectProperty narrower
                = m.getOWLDataFactory().getOWLObjectProperty("skos:narrower", pm);
        for (OWLOntology o : m.getOntologies()) {
            Collection<OWLAxiom> axioms = EntitySearcher.
                    getReferencingAxioms((OWLEntity) rootConcept, o);
            for (OWLAxiom ax : axioms) {
                if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                    OWLObjectPropertyAssertionAxiom axn = (OWLObjectPropertyAssertionAxiom) ax;
                    if (axn.getProperty().equals(broader) && axn.getObject().equals(rootConcept)) {
                        OWLIndividual subject = axn.getSubject();
                        SKOSConcept sc = SKOSConcept.
                                buildSKOSConceptFromIndividual(subject, manager);
                        resultSet.add(sc);
                    }
                    if (axn.getProperty().equals(narrower) && axn.getSubject().equals(rootConcept)) {
                        OWLIndividual object = axn.getObject();
                        SKOSConcept sc = SKOSConcept.
                                buildSKOSConceptFromIndividual(object, manager);
                        resultSet.add(sc);
                    }                    
                }
            }
        }
        return resultSet;
    }

    /**
     * 
     * @param match
     * @return The set of SKOSConcepts in the loaded ontologies that match the
     * string.
     * Specifically, matching succeeds if the keyword is contained within any 
     * of the lexical representations of the concept, ignoring case.
     */
    private Set<SKOSConcept> getMatchingConcepts(String match) {

        Set<SKOSConcept> resultSet = new HashSet<>();
        OWLAnnotationProperty prefLabel
                = m.getOWLDataFactory().getOWLAnnotationProperty("skos:prefLabel", pm);
        OWLAnnotationProperty altLabel
                = m.getOWLDataFactory().getOWLAnnotationProperty("skos:altLabel", pm);

        Iterator<OWLOntology> it = m.getOntologies().iterator();
        while (size < LIMIT && it.hasNext()) {
            OWLOntology o = it.next();
            Collection<OWLAxiom> axioms = EntitySearcher.getReferencingAxioms(prefLabel, o);
            axioms.addAll(EntitySearcher.getReferencingAxioms(altLabel, o));

            Iterator<OWLAxiom> it2 = axioms.iterator();
            while (size < LIMIT && it2.hasNext()) {
                OWLAxiom ax = it2.next();
                if (ax.getAxiomType() == AxiomType.ANNOTATION_ASSERTION) {
                    OWLAnnotationAssertionAxiom axn = (OWLAnnotationAssertionAxiom) ax;
                    if (axn.getProperty().equals(prefLabel)
                            || axn.getProperty().equals(altLabel)) {
                        OWLLiteral l = axn.getValue().asLiteral().get();
                        //WAS: l.getLiteral().equalsIgnoreCase(match)
                        if (l.getLiteral().toLowerCase().equals(match)) {

                            OWLIndividual subject = m.getOWLDataFactory().
                                    getOWLNamedIndividual((IRI) axn.getSubject());
                            SKOSConcept sc = SKOSConcept.
                                    buildSKOSConceptFromIndividual(subject, manager);
                            resultSet.add(sc);
                            size++;
                        }
                    }
                }
            }
        }
        return resultSet;
    }

}
