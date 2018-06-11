
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EDIServerMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EDIServerMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="ASCII"/>
 *     &lt;enumeration value="UNICODE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EDIServerMode")
@XmlEnum
public enum EDIServerMode {

    ASCII,
    UNICODE;

    public String value() {
        return name();
    }

    public static EDIServerMode fromValue(String v) {
        return valueOf(v);
    }

}
