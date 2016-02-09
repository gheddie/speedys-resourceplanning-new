
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
 *         &lt;element name="sourcePosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sourcePosition",
    "targetPosition"
})
public class RequestedSwapDTO {

    protected String sourcePosition;
    protected String targetPosition;

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
