
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entityTreeNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entityTreeNode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payLoad" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entityTreeNode", propOrder = {
    "payLoad"
})
public abstract class EntityTreeNode {

    protected Object payLoad;

    /**
     * Gets the value of the payLoad property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPayLoad() {
        return payLoad;
    }

    /**
     * Sets the value of the payLoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPayLoad(Object value) {
        this.payLoad = value;
    }

}
