
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eventDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assignmentCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eventId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="eventState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="positionCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventDTO", propOrder = {
    "assignmentCount",
    "description",
    "eventDate",
    "eventId",
    "eventState",
    "positionCount"
})
public class EventDTO {

    protected int assignmentCount;
    protected String description;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDate;
    protected Long eventId;
    protected String eventState;
    protected int positionCount;

    /**
     * Gets the value of the assignmentCount property.
     * 
     */
    public int getAssignmentCount() {
        return assignmentCount;
    }

    /**
     * Sets the value of the assignmentCount property.
     * 
     */
    public void setAssignmentCount(int value) {
        this.assignmentCount = value;
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
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventDate(XMLGregorianCalendar value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the eventId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEventId(Long value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the eventState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventState() {
        return eventState;
    }

    /**
     * Sets the value of the eventState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventState(String value) {
        this.eventState = value;
    }

    /**
     * Gets the value of the positionCount property.
     * 
     */
    public int getPositionCount() {
        return positionCount;
    }

    /**
     * Sets the value of the positionCount property.
     * 
     */
    public void setPositionCount(int value) {
        this.positionCount = value;
    }

}
