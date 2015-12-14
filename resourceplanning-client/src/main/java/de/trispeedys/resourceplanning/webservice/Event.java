
package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for event complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="event">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservice.resourceplanning.trispeedys.de/}abstractDbObject">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eventKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventPositions" type="{http://webservice.resourceplanning.trispeedys.de/}eventPosition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="eventState" type="{http://webservice.resourceplanning.trispeedys.de/}eventState" minOccurs="0"/>
 *         &lt;element name="eventTemplate" type="{http://webservice.resourceplanning.trispeedys.de/}eventTemplate" minOccurs="0"/>
 *         &lt;element name="parentEvent" type="{http://webservice.resourceplanning.trispeedys.de/}event" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "event", propOrder = {
    "description",
    "eventDate",
    "eventKey",
    "eventPositions",
    "eventState",
    "eventTemplate",
    "parentEvent"
})
public class Event
    extends AbstractDbObject
{

    protected String description;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventDate;
    protected String eventKey;
    @XmlElement(nillable = true)
    protected List<EventPosition> eventPositions;
    protected EventState eventState;
    protected EventTemplate eventTemplate;
    protected Event parentEvent;

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
     * Gets the value of the eventKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * Sets the value of the eventKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventKey(String value) {
        this.eventKey = value;
    }

    /**
     * Gets the value of the eventPositions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventPositions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventPositions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventPosition }
     * 
     * 
     */
    public List<EventPosition> getEventPositions() {
        if (eventPositions == null) {
            eventPositions = new ArrayList<EventPosition>();
        }
        return this.eventPositions;
    }

    /**
     * Gets the value of the eventState property.
     * 
     * @return
     *     possible object is
     *     {@link EventState }
     *     
     */
    public EventState getEventState() {
        return eventState;
    }

    /**
     * Sets the value of the eventState property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventState }
     *     
     */
    public void setEventState(EventState value) {
        this.eventState = value;
    }

    /**
     * Gets the value of the eventTemplate property.
     * 
     * @return
     *     possible object is
     *     {@link EventTemplate }
     *     
     */
    public EventTemplate getEventTemplate() {
        return eventTemplate;
    }

    /**
     * Sets the value of the eventTemplate property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventTemplate }
     *     
     */
    public void setEventTemplate(EventTemplate value) {
        this.eventTemplate = value;
    }

    /**
     * Gets the value of the parentEvent property.
     * 
     * @return
     *     possible object is
     *     {@link Event }
     *     
     */
    public Event getParentEvent() {
        return parentEvent;
    }

    /**
     * Sets the value of the parentEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Event }
     *     
     */
    public void setParentEvent(Event value) {
        this.parentEvent = value;
    }

}
