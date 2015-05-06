/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.upatras.ceid.hpclab.owl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactoryNotFoundException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 *
 * @author koutsomi
 */
public class OntologyManager {

    private final OWLOntologyManager manager;
    private final PrefixManager prefixManager;

    public OntologyManager() {
        manager = OWLManager.createOWLOntologyManager();
        prefixManager = new DefaultPrefixManager();
        prefixManager.setPrefix("skos:", "http://www.w3.org/2004/02/skos/core#");
        OntologyConfiguration conf = new OntologyConfiguration();
        for (String ontologyURL : conf.getOntologyList()) {
            try {
                this.loadOntology(ontologyURL);
                //this.loadOntology("http://snf-630087.vm.okeanos.grnet.gr:8888/webprotege-2.5.0/download?ontology=999847fa-caca-4604-8b5b-461f4ec8cc4c");
            } catch (OWLOntologyCreationException | OWLOntologyFactoryNotFoundException ex) {
                Logger.getLogger(OntologyManager.class.getName()).log(Level.SEVERE, "Problems "
                        + "loading ontology: "
                        + ontologyURL, ex);
            }
        }

    }

    private OWLOntology loadOntology(String URL) throws OWLOntologyCreationException, OWLOntologyFactoryNotFoundException {
        IRI documentIRI = IRI.create(URL);

        OWLOntology ontology = manager
                .loadOntologyFromOntologyDocument(documentIRI);
        OWLDocumentFormat format = manager.getOntologyFormat(ontology);
        if (format.isPrefixOWLOntologyFormat()) {
            //format = format.asPrefixOWLOntologyFormat();
            prefixManager.copyPrefixesFrom((PrefixManager) format);
        }
        return ontology;

    }

    Set<OWLIndividual> getSKOSConcepts(OWLOntology ontology) {
        Set<OWLIndividual> skosConcepts = new HashSet<>();
        OWLClass skosConcept = manager.getOWLDataFactory().getOWLClass("skos:Concept", prefixManager);
        Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getClassAssertionAxioms(skosConcept);
        System.out.println("TOTAL CONCEPTS for ontology: "
                + ontology.getOntologyID() + ": "
                + classAssertionAxioms.size());
        for (OWLClassAssertionAxiom a : classAssertionAxioms) {
            skosConcepts.add(a.getIndividual());
        }
        return skosConcepts;
    }

    /**
     * @return the manager
     */
    public OWLOntologyManager getManager() {
        return manager;
    }

    /**
     * @return the prefixManager
     */
    public PrefixManager getPrefixManager() {
        return prefixManager;
    }
}
