
package de.trispeedys.resourceplanning.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for helperCallback.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="helperCallback">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ASSIGNMENT_AS_BEFORE"/>
 *     &lt;enumeration value="PAUSE_ME"/>
 *     &lt;enumeration value="CHANGE_POS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "helperCallback")
@XmlEnum
public enum HelperCallback {

    ASSIGNMENT_AS_BEFORE,
    PAUSE_ME,
    CHANGE_POS;

    public String value() {
        return name();
    }

    public static HelperCallback fromValue(String v) {
        return valueOf(v);
    }

}
