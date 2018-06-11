
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LinkDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinkDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkletInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FromTaskInstanceDetails" type="{http://www.informatica.com/wsh}TaskDetails"/>
 *         &lt;element name="ToTaskInstanceDetails" type="{http://www.informatica.com/wsh}TaskDetails"/>
 *         &lt;element name="WorkflowRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkletRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkDetails", propOrder = {
    "folderName",
    "workflowName",
    "workletInstanceName",
    "fromTaskInstanceDetails",
    "toTaskInstanceDetails",
    "workflowRunId",
    "workletRunId"
})
public class LinkDetails {

    @XmlElement(name = "FolderName", required = true, nillable = true)
    protected String folderName;
    @XmlElement(name = "WorkflowName", required = true, nillable = true)
    protected String workflowName;
    @XmlElement(name = "WorkletInstanceName", required = true, nillable = true)
    protected String workletInstanceName;
    @XmlElement(name = "FromTaskInstanceDetails", required = true, nillable = true)
    protected TaskDetails fromTaskInstanceDetails;
    @XmlElement(name = "ToTaskInstanceDetails", required = true, nillable = true)
    protected TaskDetails toTaskInstanceDetails;
    @XmlElement(name = "WorkflowRunId")
    protected int workflowRunId;
    @XmlElement(name = "WorkletRunId")
    protected int workletRunId;

    /**
     * Gets the value of the folderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Sets the value of the folderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderName(String value) {
        this.folderName = value;
    }

    /**
     * Gets the value of the workflowName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * Sets the value of the workflowName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkflowName(String value) {
        this.workflowName = value;
    }

    /**
     * Gets the value of the workletInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkletInstanceName() {
        return workletInstanceName;
    }

    /**
     * Sets the value of the workletInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkletInstanceName(String value) {
        this.workletInstanceName = value;
    }

    /**
     * Gets the value of the fromTaskInstanceDetails property.
     * 
     * @return
     *     possible object is
     *     {@link TaskDetails }
     *     
     */
    public TaskDetails getFromTaskInstanceDetails() {
        return fromTaskInstanceDetails;
    }

    /**
     * Sets the value of the fromTaskInstanceDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskDetails }
     *     
     */
    public void setFromTaskInstanceDetails(TaskDetails value) {
        this.fromTaskInstanceDetails = value;
    }

    /**
     * Gets the value of the toTaskInstanceDetails property.
     * 
     * @return
     *     possible object is
     *     {@link TaskDetails }
     *     
     */
    public TaskDetails getToTaskInstanceDetails() {
        return toTaskInstanceDetails;
    }

    /**
     * Sets the value of the toTaskInstanceDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskDetails }
     *     
     */
    public void setToTaskInstanceDetails(TaskDetails value) {
        this.toTaskInstanceDetails = value;
    }

    /**
     * Gets the value of the workflowRunId property.
     * 
     */
    public int getWorkflowRunId() {
        return workflowRunId;
    }

    /**
     * Sets the value of the workflowRunId property.
     * 
     */
    public void setWorkflowRunId(int value) {
        this.workflowRunId = value;
    }

    /**
     * Gets the value of the workletRunId property.
     * 
     */
    public int getWorkletRunId() {
        return workletRunId;
    }

    /**
     * Sets the value of the workletRunId property.
     * 
     */
    public void setWorkletRunId(int value) {
        this.workletRunId = value;
    }

}
