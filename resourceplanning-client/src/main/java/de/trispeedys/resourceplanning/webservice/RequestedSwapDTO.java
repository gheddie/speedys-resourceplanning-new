
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for requestedSwapDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requestedSwapDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="businessKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourceDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sourcePosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="swapState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="swapType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="targetDomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sourcePosition",
    "swapState",
    "swapType",
    "targetDomain",
    "targetPosition"
})
public class RequestedSwapDTO {

    protected String businessKey;
    protected String sourceDomain;
    protected String sourcePosition;
    protected String swapState;
    protected String swapType;
    protected String targetDomain;
    protected String targetPosition;

    /**
     * Gets the value of the businessKey property.
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
     * Sets the value of the businessKey property.
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
     * Gets the value of the sourceDomain property.
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
     * Sets the value of the sourceDomain property.
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
     * Gets the value of the sourcePosition property.
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
     * Sets the value of the sourcePosition property.
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
     * Gets the value of the swapState property.
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
     * Sets the value of the swapState property.
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
     * Gets the value of the swapType property.
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
     * Sets the value of the swapType property.
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
     * Gets the value of the targetDomain property.
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
     * Sets the value of the targetDomain property.
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
     * Gets the value of the targetPosition property.
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
     * Sets the value of the targetPosition property.
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
