
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EWorkflowRunStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EWorkflowRunStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="SUCCEEDED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="STOPPED"/>
 *     &lt;enumeration value="ABORTED"/>
 *     &lt;enumeration value="RUNNING"/>
 *     &lt;enumeration value="SUSPENDING"/>
 *     &lt;enumeration value="SUSPENDED"/>
 *     &lt;enumeration value="STOPPING"/>
 *     &lt;enumeration value="ABORTING"/>
 *     &lt;enumeration value="WAITING"/>
 *     &lt;enumeration value="SCHEDULED"/>
 *     &lt;enumeration value="UNSCHEDULED"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *     &lt;enumeration value="TERMINATED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EWorkflowRunStatus")
@XmlEnum
public enum EWorkflowRunStatus {

    SUCCEEDED,
    FAILED,
    STOPPED,
    ABORTED,
    RUNNING,
    SUSPENDING,
    SUSPENDED,
    STOPPING,
    ABORTING,
    WAITING,
    SCHEDULED,
    UNSCHEDULED,
    UNKNOWN,
    TERMINATED;

    public String value() {
        return name();
    }

    public static EWorkflowRunStatus fromValue(String v) {
        return valueOf(v);
    }

}
