
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for position complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="position">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservice.resourceplanning.trispeedys.de/}abstractDbObject">
 *       &lt;sequence>
 *         &lt;element name="assignmentPriority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="authorityOverride" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="choosable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://webservice.resourceplanning.trispeedys.de/}domain" minOccurs="0"/>
 *         &lt;element name="minimalAge" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="positionNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "position", propOrder = {
    "assignmentPriority",
    "authorityOverride",
    "choosable",
    "description",
    "domain",
    "minimalAge",
    "positionNumber"
})
public class Position
    extends AbstractDbObject
{

    protected Integer assignmentPriority;
    protected boolean authorityOverride;
    protected boolean choosable;
    protected String description;
    protected Domain domain;
    protected int minimalAge;
    protected int positionNumber;

    /**
     * Gets the value of the assignmentPriority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAssignmentPriority() {
        return assignmentPriority;
    }

    /**
     * Sets the value of the assignmentPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAssignmentPriority(Integer value) {
        this.assignmentPriority = value;
    }

    /**
     * Gets the value of the authorityOverride property.
     * 
     */
    public boolean isAuthorityOverride() {
        return authorityOverride;
    }

    /**
     * Sets the value of the authorityOverride property.
     * 
     */
    public void setAuthorityOverride(boolean value) {
        this.authorityOverride = value;
    }

    /**
     * Gets the value of the choosable property.
     * 
     */
    public boolean isChoosable() {
        return choosable;
    }

    /**
     * Sets the value of the choosable property.
     * 
     */
    public void setChoosable(boolean value) {
        this.choosable = value;
    }

    /**
     * Gets the value of the description property.
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
     * Sets the value of the description property.
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
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link Domain }
     *     
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link Domain }
     *     
     */
    public void setDomain(Domain value) {
        this.domain = value;
    }

    /**
     * Gets the value of the minimalAge property.
     * 
     */
    public int getMinimalAge() {
        return minimalAge;
    }

    /**
     * Sets the value of the minimalAge property.
     * 
     */
    public void setMinimalAge(int value) {
        this.minimalAge = value;
    }

    /**
     * Gets the value of the positionNumber property.
     * 
     */
    public int getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the value of the positionNumber property.
     * 
     */
    public void setPositionNumber(int value) {
        this.positionNumber = value;
    }

}
