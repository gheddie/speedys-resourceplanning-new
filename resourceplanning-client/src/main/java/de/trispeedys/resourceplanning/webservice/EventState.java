
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="eventState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PLANNED"/>
 *     &lt;enumeration value="INITIATED"/>
 *     &lt;enumeration value="FINISHED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "eventState")
@XmlEnum
public enum EventState {

    PLANNED,
    INITIATED,
    FINISHED;

    public String value() {
        return name();
    }

    public static EventState fromValue(String v) {
        return valueOf(v);
    }

}
