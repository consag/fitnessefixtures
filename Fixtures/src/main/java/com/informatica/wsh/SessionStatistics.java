
package com.informatica.wsh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SessionStatistics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SessionStatistics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DIServiceInfo" type="{http://www.informatica.com/wsh}DIServiceInfo"/>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkflowRunInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MappingName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskRunStatus" type="{http://www.informatica.com/wsh}ETaskRunStatus"/>
 *         &lt;element name="WorkletRunId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LogFileCodePage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumSrcSuccessRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTgtSuccessRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumSrcFailedRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTgtFailedRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTransErrors" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumTables" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LogFileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstErrorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FirstErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TableStatistics" type="{http://www.informatica.com/wsh}TableStatistics" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SessionStatistics", propOrder = {
    "diServiceInfo",
    "folderName",
    "workflowName",
    "workflowRunId",
    "workflowRunInstanceName",
    "instanceName",
    "mappingName",
    "taskRunStatus",
    "workletRunId",
    "logFileCodePage",
    "numSrcSuccessRows",
    "numTgtSuccessRows",
    "numSrcFailedRows",
    "numTgtFailedRows",
    "numTransErrors",
    "numTables",
    "logFileName",
    "firstErrorCode",
    "firstErrorMessage",
    "tableStatistics"
})
public class SessionStatistics {

    @XmlElement(name = "DIServiceInfo", required = true, nillable = true)
    protected DIServiceInfo diServiceInfo;
    @XmlElement(name = "FolderName", required = true, nillable = true)
    protected String folderName;
    @XmlElement(name = "WorkflowName", required = true, nillable = true)
    protected String workflowName;
    @XmlElement(name = "WorkflowRunId")
    protected int workflowRunId;
    @XmlElement(name = "WorkflowRunInstanceName", required = true, nillable = true)
    protected String workflowRunInstanceName;
    @XmlElement(name = "InstanceName", required = true, nillable = true)
    protected String instanceName;
    @XmlElement(name = "MappingName", required = true, nillable = true)
    protected String mappingName;
    @XmlElement(name = "TaskRunStatus", required = true, nillable = true)
    protected ETaskRunStatus taskRunStatus;
    @XmlElement(name = "WorkletRunId")
    protected int workletRunId;
    @XmlElement(name = "LogFileCodePage")
    protected int logFileCodePage;
    @XmlElement(name = "NumSrcSuccessRows")
    protected int numSrcSuccessRows;
    @XmlElement(name = "NumTgtSuccessRows")
    protected int numTgtSuccessRows;
    @XmlElement(name = "NumSrcFailedRows")
    protected int numSrcFailedRows;
    @XmlElement(name = "NumTgtFailedRows")
    protected int numTgtFailedRows;
    @XmlElement(name = "NumTransErrors")
    protected int numTransErrors;
    @XmlElement(name = "NumTables")
    protected int numTables;
    @XmlElement(name = "LogFileName", required = true, nillable = true)
    protected String logFileName;
    @XmlElement(name = "FirstErrorCode")
    protected int firstErrorCode;
    @XmlElement(name = "FirstErrorMessage", required = true, nillable = true)
    protected String firstErrorMessage;
    @XmlElement(name = "TableStatistics")
    protected List<TableStatistics> tableStatistics;

    /**
     * Gets the value of the diServiceInfo property.
     * 
     * @return
     *     possible object is
     *     {@link DIServiceInfo }
     *     
     */
    public DIServiceInfo getDIServiceInfo() {
        return diServiceInfo;
    }

    /**
     * Sets the value of the diServiceInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIServiceInfo }
     *     
     */
    public void setDIServiceInfo(DIServiceInfo value) {
        this.diServiceInfo = value;
    }

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
     * Gets the value of the mappingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMappingName() {
        return mappingName;
    }

    /**
     * Sets the value of the mappingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMappingName(String value) {
        this.mappingName = value;
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
     * Gets the value of the numSrcSuccessRows property.
     * 
     */
    public int getNumSrcSuccessRows() {
        return numSrcSuccessRows;
    }

    /**
     * Sets the value of the numSrcSuccessRows property.
     * 
     */
    public void setNumSrcSuccessRows(int value) {
        this.numSrcSuccessRows = value;
    }

    /**
     * Gets the value of the numTgtSuccessRows property.
     * 
     */
    public int getNumTgtSuccessRows() {
        return numTgtSuccessRows;
    }

    /**
     * Sets the value of the numTgtSuccessRows property.
     * 
     */
    public void setNumTgtSuccessRows(int value) {
        this.numTgtSuccessRows = value;
    }

    /**
     * Gets the value of the numSrcFailedRows property.
     * 
     */
    public int getNumSrcFailedRows() {
        return numSrcFailedRows;
    }

    /**
     * Sets the value of the numSrcFailedRows property.
     * 
     */
    public void setNumSrcFailedRows(int value) {
        this.numSrcFailedRows = value;
    }

    /**
     * Gets the value of the numTgtFailedRows property.
     * 
     */
    public int getNumTgtFailedRows() {
        return numTgtFailedRows;
    }

    /**
     * Sets the value of the numTgtFailedRows property.
     * 
     */
    public void setNumTgtFailedRows(int value) {
        this.numTgtFailedRows = value;
    }

    /**
     * Gets the value of the numTransErrors property.
     * 
     */
    public int getNumTransErrors() {
        return numTransErrors;
    }

    /**
     * Sets the value of the numTransErrors property.
     * 
     */
    public void setNumTransErrors(int value) {
        this.numTransErrors = value;
    }

    /**
     * Gets the value of the numTables property.
     * 
     */
    public int getNumTables() {
        return numTables;
    }

    /**
     * Sets the value of the numTables property.
     * 
     */
    public void setNumTables(int value) {
        this.numTables = value;
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
     * Gets the value of the firstErrorCode property.
     * 
     */
    public int getFirstErrorCode() {
        return firstErrorCode;
    }

    /**
     * Sets the value of the firstErrorCode property.
     * 
     */
    public void setFirstErrorCode(int value) {
        this.firstErrorCode = value;
    }

    /**
     * Gets the value of the firstErrorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstErrorMessage() {
        return firstErrorMessage;
    }

    /**
     * Sets the value of the firstErrorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstErrorMessage(String value) {
        this.firstErrorMessage = value;
    }

    /**
     * Gets the value of the tableStatistics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tableStatistics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTableStatistics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TableStatistics }
     * 
     * 
     */
    public List<TableStatistics> getTableStatistics() {
        if (tableStatistics == null) {
            tableStatistics = new ArrayList<TableStatistics>();
        }
        return this.tableStatistics;
    }

}
