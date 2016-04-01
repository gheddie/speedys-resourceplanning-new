
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für hierarchicalEventItemDTO complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="hierarchicalEventItemDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assignmentString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="availability" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="entityId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hierarchyLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="infoString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itemType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="priorization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hierarchicalEventItemDTO", propOrder = {
    "assignmentString",
    "availability",
    "entityId",
    "group",
    "hierarchyLevel",
    "infoString",
    "itemKey",
    "itemType",
    "priorization"
})
public class HierarchicalEventItemDTO {

    protected String assignmentString;
    protected String availability;
    protected Long entityId;
    protected String group;
    protected int hierarchyLevel;
    protected String infoString;
    protected String itemKey;
    protected String itemType;
    protected String priorization;

    /**
     * Ruft den Wert der assignmentString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignmentString() {
        return assignmentString;
    }

    /**
     * Legt den Wert der assignmentString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignmentString(String value) {
        this.assignmentString = value;
    }

    /**
     * Ruft den Wert der availability-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Legt den Wert der availability-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvailability(String value) {
        this.availability = value;
    }

    /**
     * Ruft den Wert der entityId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Legt den Wert der entityId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEntityId(Long value) {
        this.entityId = value;
    }

    /**
     * Ruft den Wert der group-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Legt den Wert der group-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

    /**
     * Ruft den Wert der hierarchyLevel-Eigenschaft ab.
     * 
     */
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    /**
     * Legt den Wert der hierarchyLevel-Eigenschaft fest.
     * 
     */
    public void setHierarchyLevel(int value) {
        this.hierarchyLevel = value;
    }

    /**
     * Ruft den Wert der infoString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoString() {
        return infoString;
    }

    /**
     * Legt den Wert der infoString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoString(String value) {
        this.infoString = value;
    }

    /**
     * Ruft den Wert der itemKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemKey() {
        return itemKey;
    }

    /**
     * Legt den Wert der itemKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemKey(String value) {
        this.itemKey = value;
    }

    /**
     * Ruft den Wert der itemType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Legt den Wert der itemType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemType(String value) {
        this.itemType = value;
    }

    /**
     * Ruft den Wert der priorization-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriorization() {
        return priorization;
    }

    /**
     * Legt den Wert der priorization-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorization(String value) {
        this.priorization = value;
    }

}
