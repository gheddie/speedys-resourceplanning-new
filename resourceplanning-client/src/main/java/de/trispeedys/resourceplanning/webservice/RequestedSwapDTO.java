
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für requestedSwapDTO complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="requestedSwapDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="businessKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceHelper" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourcePosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="swapState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="swapType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetHelper" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetPosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestedSwapDTO", propOrder = {
    "businessKey",
    "sourceDomain",
    "sourceHelper",
    "sourcePosition",
    "swapState",
    "swapType",
    "targetDomain",
    "targetHelper",
    "targetPosition"
})
public class RequestedSwapDTO {

    protected String businessKey;
    protected String sourceDomain;
    protected String sourceHelper;
    protected String sourcePosition;
    protected String swapState;
    protected String swapType;
    protected String targetDomain;
    protected String targetHelper;
    protected String targetPosition;

    /**
     * Ruft den Wert der businessKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessKey() {
        return businessKey;
    }

    /**
     * Legt den Wert der businessKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessKey(String value) {
        this.businessKey = value;
    }

    /**
     * Ruft den Wert der sourceDomain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceDomain() {
        return sourceDomain;
    }

    /**
     * Legt den Wert der sourceDomain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceDomain(String value) {
        this.sourceDomain = value;
    }

    /**
     * Ruft den Wert der sourceHelper-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceHelper() {
        return sourceHelper;
    }

    /**
     * Legt den Wert der sourceHelper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceHelper(String value) {
        this.sourceHelper = value;
    }

    /**
     * Ruft den Wert der sourcePosition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourcePosition() {
        return sourcePosition;
    }

    /**
     * Legt den Wert der sourcePosition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourcePosition(String value) {
        this.sourcePosition = value;
    }

    /**
     * Ruft den Wert der swapState-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwapState() {
        return swapState;
    }

    /**
     * Legt den Wert der swapState-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwapState(String value) {
        this.swapState = value;
    }

    /**
     * Ruft den Wert der swapType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwapType() {
        return swapType;
    }

    /**
     * Legt den Wert der swapType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwapType(String value) {
        this.swapType = value;
    }

    /**
     * Ruft den Wert der targetDomain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetDomain() {
        return targetDomain;
    }

    /**
     * Legt den Wert der targetDomain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetDomain(String value) {
        this.targetDomain = value;
    }

    /**
     * Ruft den Wert der targetHelper-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetHelper() {
        return targetHelper;
    }

    /**
     * Legt den Wert der targetHelper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetHelper(String value) {
        this.targetHelper = value;
    }

    /**
     * Ruft den Wert der targetPosition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetPosition() {
        return targetPosition;
    }

    /**
     * Legt den Wert der targetPosition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetPosition(String value) {
        this.targetPosition = value;
    }

}
