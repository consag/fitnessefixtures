
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDIServerRunStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDIServerRunStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="INITIALIZING"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="SHUTTING_DOWN"/>
 *     &lt;enumeration value="STOPPING"/>
 *     &lt;enumeration value="ABORTING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDIServerRunStatus")
@XmlEnum
public enum EDIServerRunStatus {

    INITIALIZING,
    RUNNING,
    SHUTTING_DOWN,
    STOPPING,
    ABORTING;

    public String value() {
        return name();
    }

    public static EDIServerRunStatus fromValue(String v) {
        return valueOf(v);
    }

}
