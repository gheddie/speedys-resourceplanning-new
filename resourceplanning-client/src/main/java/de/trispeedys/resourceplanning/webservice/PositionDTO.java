
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für positionDTO complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="positionDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minimalAge" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="positionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="positionNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "positionDTO", propOrder = {
    "description",
    "domain",
    "minimalAge",
    "positionId",
    "positionNumber"
})
public class PositionDTO {

    protected String description;
    protected String domain;
    protected int minimalAge;
    protected Long positionId;
    protected int positionNumber;

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der domain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Legt den Wert der domain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Ruft den Wert der minimalAge-Eigenschaft ab.
     * 
     */
    public int getMinimalAge() {
        return minimalAge;
    }

    /**
     * Legt den Wert der minimalAge-Eigenschaft fest.
     * 
     */
    public void setMinimalAge(int value) {
        this.minimalAge = value;
    }

    /**
     * Ruft den Wert der positionId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPositionId() {
        return positionId;
    }

    /**
     * Legt den Wert der positionId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPositionId(Long value) {
        this.positionId = value;
    }

    /**
     * Ruft den Wert der positionNumber-Eigenschaft ab.
     * 
     */
    public int getPositionNumber() {
        return positionNumber;
    }

    /**
     * Legt den Wert der positionNumber-Eigenschaft fest.
     * 
     */
    public void setPositionNumber(int value) {
        this.positionNumber = value;
    }

}
