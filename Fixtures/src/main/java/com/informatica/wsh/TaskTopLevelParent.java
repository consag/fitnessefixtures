
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaskTopLevelParent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskTopLevelParent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FolderId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TaskInstanceId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TaskInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TopLevelParentId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TopLevelParentName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskTopLevelParent", propOrder = {
    "folderId",
    "folderName",
    "workflowId",
    "workflowName",
    "taskInstanceId",
    "taskInstanceName",
    "topLevelParentId",
    "topLevelParentName"
})
public class TaskTopLevelParent {

    @XmlElement(name = "FolderId")
    protected int folderId;
    @XmlElement(name = "FolderName", required = true)
    protected String folderName;
    @XmlElement(name = "WorkflowId")
    protected int workflowId;
    @XmlElement(name = "WorkflowName", required = true)
    protected String workflowName;
    @XmlElement(name = "TaskInstanceId")
    protected int taskInstanceId;
    @XmlElement(name = "TaskInstanceName", required = true)
    protected String taskInstanceName;
    @XmlElement(name = "TopLevelParentId")
    protected int topLevelParentId;
    @XmlElement(name = "TopLevelParentName", required = true)
    protected String topLevelParentName;

    /**
     * Gets the value of the folderId property.
     * 
     */
    public int getFolderId() {
        return folderId;
    }

    /**
     * Sets the value of the folderId property.
     * 
     */
    public void setFolderId(int value) {
        this.folderId = value;
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
     * Gets the value of the workflowId property.
     * 
     */
    public int getWorkflowId() {
        return workflowId;
    }

    /**
     * Sets the value of the workflowId property.
     * 
     */
    public void setWorkflowId(int value) {
        this.workflowId = value;
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
     * Gets the value of the taskInstanceId property.
     * 
     */
    public int getTaskInstanceId() {
        return taskInstanceId;
    }

    /**
     * Sets the value of the taskInstanceId property.
     * 
     */
    public void setTaskInstanceId(int value) {
        this.taskInstanceId = value;
    }

    /**
     * Gets the value of the taskInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskInstanceName() {
        return taskInstanceName;
    }

    /**
     * Sets the value of the taskInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskInstanceName(String value) {
        this.taskInstanceName = value;
    }

    /**
     * Gets the value of the topLevelParentId property.
     * 
     */
    public int getTopLevelParentId() {
        return topLevelParentId;
    }

    /**
     * Sets the value of the topLevelParentId property.
     * 
     */
    public void setTopLevelParentId(int value) {
        this.topLevelParentId = value;
    }

    /**
     * Gets the value of the topLevelParentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopLevelParentName() {
        return topLevelParentName;
    }

    /**
     * Sets the value of the topLevelParentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopLevelParentName(String value) {
        this.topLevelParentName = value;
    }

}
