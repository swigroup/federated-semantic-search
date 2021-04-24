<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:lom="http://ltsc.ieee.org/xsd/LOM/"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                xmlns="http://lom.hpclab.ceid.upatras.gr/">
    <xsl:template match="/">
        <rdf:RDF xmlns="http://lom.hpclab.ceid.upatras.gr/"
                 xml:base="http://lom.hpclab.ceid.upatras.gr"
                 xmlns:xml="http://www.w3.org/XML/1998/namespace"
                 xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#">
            <owl:Ontology rdf:about="">
       
            </owl:Ontology>
    
            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>
 
    <xsl:template match="Results/Category">
        <xsl:apply-templates select="Result"/>
    </xsl:template>
  
    <xsl:template match="keyword[(@IRI)]">
        <lom:keyword>
            <owl:NamedIndividual rdf:about="{@IRI}">
                <xsl:apply-templates select="label"/>
            </owl:NamedIndividual>
            <xsl:apply-templates select="score"/>
        </lom:keyword>    
    </xsl:template>
    
    <xsl:template match="keyword[not(@IRI)]">
        <lom:keyword>
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="score"/>
        </lom:keyword>    
    </xsl:template>
    
    <xsl:template match="keyword/label">
        <rdfs:label>
            <xsl:copy-of select="@xml:lang"/>
            <xsl:value-of select="."/>
        </rdfs:label>
    </xsl:template>
    
    <xsl:template match="keyword/score">
        <score rdf:datatype="xsd:double">
            <xsl:value-of select="."/>
        </score>
    </xsl:template>
  
    <xsl:template match="Result">
        <owl:NamedIndividual rdf:ID="{generate-id(.)}">
            <xsl:apply-templates select="keyword[not(@IRI)]"/>
            <xsl:apply-templates select="keyword[(@IRI)]"/>
            <lom:identifier rdf:datatype="xsd:anyURI">
                <xsl:value-of select="URL"/>
            </lom:identifier>
            <lom:title>
                <xsl:value-of select="Title"/>
            </lom:title>
            <lom:description>
                <xsl:value-of select="Description"/>
            </lom:description>
        </owl:NamedIndividual>
    </xsl:template>

  
</xsl:stylesheet>
