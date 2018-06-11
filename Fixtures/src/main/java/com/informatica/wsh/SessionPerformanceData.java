
package com.informatica.wsh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SessionPerformanceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SessionPerformanceData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskRunStatus" type="{http://www.informatica.com/wsh}ETaskRunStatus"/>
 *         &lt;element name="WorkflowRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkletRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkflowRunInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PerformanceCounter" type="{http://www.informatica.com/wsh}PerformanceCounter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SessionPerformanceData", propOrder = {
    "folderName",
    "workflowName",
    "instanceName",
    "taskRunStatus",
    "workflowRunId",
    "workletRunId",
    "workflowRunInstanceName",
    "performanceCounter"
})
public class SessionPerformanceData {

    @XmlElement(name = "FolderName", required = true, nillable = true)
    protected String folderName;
    @XmlElement(name = "WorkflowName", required = true, nillable = true)
    protected String workflowName;
    @XmlElement(name = "InstanceName", required = true, nillable = true)
    protected String instanceName;
    @XmlElement(name = "TaskRunStatus", required = true, nillable = true)
    protected ETaskRunStatus taskRunStatus;
    @XmlElement(name = "WorkflowRunId")
    protected int workflowRunId;
    @XmlElement(name = "WorkletRunId")
    protected int workletRunId;
    @XmlElement(name = "WorkflowRunInstanceName", required = true)
    protected String workflowRunInstanceName;
    @XmlElement(name = "PerformanceCounter")
    protected List<PerformanceCounter> performanceCounter;

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
     * Gets the value of the instanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Sets the value of the instanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceName(String value) {
        this.instanceName = value;
    }

    /**
     * Gets the value of the taskRunStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ETaskRunStatus }
     *     
     */
    public ETaskRunStatus getTaskRunStatus() {
        return taskRunStatus;
    }

    /**
     * Sets the value of the taskRunStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETaskRunStatus }
     *     
     */
    public void setTaskRunStatus(ETaskRunStatus value) {
        this.taskRunStatus = value;
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

    /**
     * Gets the value of the workflowRunInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkflowRunInstanceName() {
        return workflowRunInstanceName;
    }

    /**
     * Sets the value of the workflowRunInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkflowRunInstanceName(String value) {
        this.workflowRunInstanceName = value;
    }

    /**
     * Gets the value of the performanceCounter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the performanceCounter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPerformanceCounter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PerformanceCounter }
     * 
     * 
     */
    public List<PerformanceCounter> getPerformanceCounter() {
        if (performanceCounter == null) {
            performanceCounter = new ArrayList<PerformanceCounter>();
        }
        return this.performanceCounter;
    }

}
