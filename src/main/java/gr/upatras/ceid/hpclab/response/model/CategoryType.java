//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.05 at 06:32:19 PM EEST 
//
package gr.upatras.ceid.hpclab.response.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CategoryType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="CategoryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alter_translation" type="{}translationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Result" type="{}ResultType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="keyword" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategoryType", propOrder = {
    "alterTranslation",
    "result"
})
public class CategoryType {

    @XmlElement(name = "alter_translation")
    protected List<TranslationType> alterTranslation;
    @XmlElement(name = "Result")
    protected List<ResultType> result;
    @XmlAttribute(name = "keyword")
    protected String keyword;
    @XmlAttribute(name = "lang" /*, namespace = "http://www.w3.org/XML/1998/namespace"*/)
    protected String lang;

    /**
     * Gets the value of the alterTranslation property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the alterTranslation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlterTranslation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TranslationType }
     *
     *
     */
    public List<TranslationType> getAlterTranslation() {
        if (alterTranslation == null) {
            alterTranslation = new ArrayList<TranslationType>();
        }
        return this.alterTranslation;
    }

    /**
     * Gets the value of the result property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the result property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResult().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResultType }
     *
     *
     */
    public List<ResultType> getResult() {
        if (result == null) {
            result = new ArrayList<ResultType>();
        }
        return this.result;
    }

    /**
     * Gets the value of the keyword property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the value of the keyword property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setKeyword(String value) {
        this.keyword = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setLang(String value) {
        this.lang = value;
    }

}