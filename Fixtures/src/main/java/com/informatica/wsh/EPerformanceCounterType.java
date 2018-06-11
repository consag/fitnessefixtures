
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EPerformanceCounterType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EPerformanceCounterType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="INTEGER"/>
 *     &lt;enumeration value="STRING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EPerformanceCounterType")
@XmlEnum
public enum EPerformanceCounterType {

    INTEGER,
    STRING;

    public String value() {
        return name();
    }

    public static EPerformanceCounterType fromValue(String v) {
        return valueOf(v);
    }

}
