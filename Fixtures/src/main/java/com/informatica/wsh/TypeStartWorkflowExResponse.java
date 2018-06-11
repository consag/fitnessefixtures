
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeStartWorkflowExResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypeStartWorkflowExResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TypeStartWorkflowExResponse", propOrder = {
    "runId"
})
public class TypeStartWorkflowExResponse {

    @XmlElement(name = "RunId")
    protected int runId;

    /**
     * Gets the value of the runId property.
     * 
     */
    public int getRunId() {
        return runId;
    }

    /**
     * Sets the value of the runId property.
     * 
     */
    public void setRunId(int value) {
        this.runId = value;
    }

}
