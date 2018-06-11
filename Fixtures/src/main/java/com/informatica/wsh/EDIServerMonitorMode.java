
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDIServerMonitorMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDIServerMonitorMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="ALL"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="SCHEDULED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDIServerMonitorMode")
@XmlEnum
public enum EDIServerMonitorMode {

    ALL,
    RUNNING,
    SCHEDULED;

    public String value() {
        return name();
    }

    public static EDIServerMonitorMode fromValue(String v) {
        return valueOf(v);
    }

}
