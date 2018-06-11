
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ETaskType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ETaskType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="COMMAND_TASK"/>
 *     &lt;enumeration value="DECISION_TASK"/>
 *     &lt;enumeration value="EVENTWAIT_TASK"/>
 *     &lt;enumeration value="EVENTRAISE_TASK"/>
 *     &lt;enumeration value="STARTWORKFLOW_TASK"/>
 *     &lt;enumeration value="ABORTWORKFLOW_TASK"/>
 *     &lt;enumeration value="STOPWORKFLOW_TASK"/>
 *     &lt;enumeration value="EMAIL_TASK"/>
 *     &lt;enumeration value="TIMER_TASK"/>
 *     &lt;enumeration value="ASSIGNMENT_TASK"/>
 *     &lt;enumeration value="SESSION_TASK"/>
 *     &lt;enumeration value="WORKLET_TASK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ETaskType")
@XmlEnum
public enum ETaskType {

    COMMAND_TASK,
    DECISION_TASK,
    EVENTWAIT_TASK,
    EVENTRAISE_TASK,
    STARTWORKFLOW_TASK,
    ABORTWORKFLOW_TASK,
    STOPWORKFLOW_TASK,
    EMAIL_TASK,
    TIMER_TASK,
    ASSIGNMENT_TASK,
    SESSION_TASK,
    WORKLET_TASK;

    public String value() {
        return name();
    }

    public static ETaskType fromValue(String v) {
        return valueOf(v);
    }

}
