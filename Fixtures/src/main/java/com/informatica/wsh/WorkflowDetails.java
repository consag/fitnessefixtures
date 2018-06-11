
package com.informatica.wsh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WorkflowDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WorkflowDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkflowRunInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowRunStatus" type="{http://www.informatica.com/wsh}EWorkflowRunStatus"/>
 *         &lt;element name="WorkflowRunType" type="{http://www.informatica.com/wsh}EWorkflowRunType"/>
 *         &lt;element name="RunErrorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="RunErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="EndTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="Attribute" type="{http://www.informatica.com/wsh}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LogFileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LogFileCodePage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="OSUser" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkflowDetails", propOrder = {
    "folderName",
    "workflowName",
    "workflowRunId",
    "workflowRunInstanceName",
    "workflowRunStatus",
    "workflowRunType",
    "runErrorCode",
    "runErrorMessage",
    "startTime",
    "endTime",
    "attribute",
    "userName",
    "logFileName",
    "logFileCodePage",
    "osUser"
})
public class WorkflowDetails {

    @XmlElement(name = "FolderName", required = true, nillable = true)
    protected String folderName;
    @XmlElement(name = "WorkflowName", required = true, nillable = true)
    protected String workflowName;
    @XmlElement(name = "WorkflowRunId")
    protected int workflowRunId;
    @XmlElement(name = "WorkflowRunInstanceName", required = true, nillable = true)
    protected String workflowRunInstanceName;
    @XmlElement(name = "WorkflowRunStatus", required = true, nillable = true)
    protected EWorkflowRunStatus workflowRunStatus;
    @XmlElement(name = "WorkflowRunType", required = true, nillable = true)
    protected EWorkflowRunType workflowRunType;
    @XmlElement(name = "RunErrorCode")
    protected int runErrorCode;
    @XmlElement(name = "RunErrorMessage", required = true, nillable = true)
    protected String runErrorMessage;
    @XmlElement(name = "StartTime", required = true, nillable = true)
    protected DIServerDate startTime;
    @XmlElement(name = "EndTime", required = true, nillable = true)
    protected DIServerDate endTime;
    @XmlElement(name = "Attribute")
    protected List<Attribute> attribute;
    @XmlElement(name = "UserName", required = true, nillable = true)
    protected String userName;
    @XmlElement(name = "LogFileName", required = true, nillable = true)
    protected String logFileName;
    @XmlElement(name = "LogFileCodePage")
    protected int logFileCodePage;
    @XmlElement(name = "OSUser", required = true, nillable = true)
    protected String osUser;

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
     * Gets the value of the workflowRunStatus property.
     * 
     * @return
     *     possible object is
     *     {@link EWorkflowRunStatus }
     *     
     */
    public EWorkflowRunStatus getWorkflowRunStatus() {
        return workflowRunStatus;
    }

    /**
     * Sets the value of the workflowRunStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link EWorkflowRunStatus }
     *     
     */
    public void setWorkflowRunStatus(EWorkflowRunStatus value) {
        this.workflowRunStatus = value;
    }

    /**
     * Gets the value of the workflowRunType property.
     * 
     * @return
     *     possible object is
     *     {@link EWorkflowRunType }
     *     
     */
    public EWorkflowRunType getWorkflowRunType() {
        return workflowRunType;
    }

    /**
     * Sets the value of the workflowRunType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EWorkflowRunType }
     *     
     */
    public void setWorkflowRunType(EWorkflowRunType value) {
        this.workflowRunType = value;
    }

    /**
     * Gets the value of the runErrorCode property.
     * 
     */
    public int getRunErrorCode() {
        return runErrorCode;
    }

    /**
     * Sets the value of the runErrorCode property.
     * 
     */
    public void setRunErrorCode(int value) {
        this.runErrorCode = value;
    }

    /**
     * Gets the value of the runErrorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunErrorMessage() {
        return runErrorMessage;
    }

    /**
     * Sets the value of the runErrorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunErrorMessage(String value) {
        this.runErrorMessage = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link DIServerDate }
     *     
     */
    public DIServerDate getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServerDate }
     *     
     */
    public void setStartTime(DIServerDate value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link DIServerDate }
     *     
     */
    public DIServerDate getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServerDate }
     *     
     */
    public void setEndTime(DIServerDate value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the logFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * Sets the value of the logFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogFileName(String value) {
        this.logFileName = value;
    }

    /**
     * Gets the value of the logFileCodePage property.
     * 
     */
    public int getLogFileCodePage() {
        return logFileCodePage;
    }

    /**
     * Sets the value of the logFileCodePage property.
     * 
     */
    public void setLogFileCodePage(int value) {
        this.logFileCodePage = value;
    }

    /**
     * Gets the value of the osUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOSUser() {
        return osUser;
    }

    /**
     * Sets the value of the osUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOSUser(String value) {
        this.osUser = value;
    }

}
