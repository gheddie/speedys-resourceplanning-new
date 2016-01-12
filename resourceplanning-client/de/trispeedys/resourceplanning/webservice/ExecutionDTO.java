
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for executionDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="executionDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="additionalInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helperFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helperLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="waitState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executionDTO", propOrder = {
    "additionalInfo",
    "helperFirstName",
    "helperLastName",
    "waitState"
})
public class ExecutionDTO {

    protected String additionalInfo;
    protected String helperFirstName;
    protected String helperLastName;
    protected String waitState;

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalInfo(String value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the helperFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelperFirstName() {
        return helperFirstName;
    }

    /**
     * Sets the value of the helperFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelperFirstName(String value) {
        this.helperFirstName = value;
    }

    /**
     * Gets the value of the helperLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelperLastName() {
        return helperLastName;
    }

    /**
     * Sets the value of the helperLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelperLastName(String value) {
        this.helperLastName = value;
    }

    /**
     * Gets the value of the waitState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitState() {
        return waitState;
    }

    /**
     * Sets the value of the waitState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitState(String value) {
        this.waitState = value;
    }

}
