
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EWorkflowRunType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EWorkflowRunType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="SCHEDULE"/>
 *     &lt;enumeration value="USER_REQUEST"/>
 *     &lt;enumeration value="DEBUG_SESSION"/>
 *     &lt;enumeration value="SERVER_INIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EWorkflowRunType")
@XmlEnum
public enum EWorkflowRunType {

    SCHEDULE,
    USER_REQUEST,
    DEBUG_SESSION,
    SERVER_INIT;

    public String value() {
        return name();
    }

    public static EWorkflowRunType fromValue(String v) {
        return valueOf(v);
    }

}
