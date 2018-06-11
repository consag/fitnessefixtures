
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ETaskRunMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ETaskRunMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="NORMAL"/>
 *     &lt;enumeration value="RECOVERY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ETaskRunMode")
@XmlEnum
public enum ETaskRunMode {

    NORMAL,
    RECOVERY;

    public String value() {
        return name();
    }

    public static ETaskRunMode fromValue(String v) {
        return valueOf(v);
    }

}
