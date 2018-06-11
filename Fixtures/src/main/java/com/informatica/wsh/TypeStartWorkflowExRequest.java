
package com.informatica.wsh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeStartWorkflowExRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypeStartWorkflowExRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DIServiceInfo" type="{http://www.informatica.com/wsh}DIServiceInfo"/>
 *         &lt;element name="FolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkflowRunInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Attribute" type="{http://www.informatica.com/wsh}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Key" type="{http://www.informatica.com/wsh}Key" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ParameterFileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Parameters" type="{http://www.informatica.com/wsh}ParameterArray"/>
 *         &lt;element name="RequestMode" type="{http://www.informatica.com/wsh}ETaskRunMode"/>
 *         &lt;element name="TaskInstancePath" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "TypeStartWorkflowExRequest", propOrder = {
    "diServiceInfo",
    "folderName",
    "workflowName",
    "workflowRunInstanceName",
    "reason",
    "attribute",
    "key",
    "parameterFileName",
    "parameters",
    "requestMode",
    "taskInstancePath",
    "osUser"
})
public class TypeStartWorkflowExRequest {

    @XmlElement(name = "DIServiceInfo", required = true)
    protected DIServiceInfo diServiceInfo;
    @XmlElement(name = "FolderName", required = true)
    protected String folderName;
    @XmlElement(name = "WorkflowName", required = true)
    protected String workflowName;
    @XmlElement(name = "WorkflowRunInstanceName", required = true, nillable = true)
    protected String workflowRunInstanceName;
    @XmlElement(name = "Reason", required = true, nillable = true)
    protected String reason;
    @XmlElement(name = "Attribute")
    protected List<Attribute> attribute;
    @XmlElement(name = "Key")
    protected List<Key> key;
    @XmlElement(name = "ParameterFileName", required = true, nillable = true)
    protected String parameterFileName;
    @XmlElement(name = "Parameters", required = true, nillable = true)
    protected ParameterArray parameters;
    @XmlElement(name = "RequestMode", required = true, nillable = true)
    protected ETaskRunMode requestMode;
    @XmlElement(name = "TaskInstancePath", required = true, nillable = true)
    protected String taskInstancePath;
    @XmlElement(name = "OSUser", required = true, nillable = true)
    protected String osUser;

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
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
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
     * Gets the value of the key property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the key property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Key }
     * 
     * 
     */
    public List<Key> getKey() {
        if (key == null) {
            key = new ArrayList<Key>();
        }
        return this.key;
    }

    /**
     * Gets the value of the parameterFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameterFileName() {
        return parameterFileName;
    }

    /**
     * Sets the value of the parameterFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameterFileName(String value) {
        this.parameterFileName = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterArray }
     *     
     */
    public ParameterArray getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterArray }
     *     
     */
    public void setParameters(ParameterArray value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the requestMode property.
     * 
     * @return
     *     possible object is
     *     {@link ETaskRunMode }
     *     
     */
    public ETaskRunMode getRequestMode() {
        return requestMode;
    }

    /**
     * Sets the value of the requestMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETaskRunMode }
     *     
     */
    public void setRequestMode(ETaskRunMode value) {
        this.requestMode = value;
    }

    /**
     * Gets the value of the taskInstancePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskInstancePath() {
        return taskInstancePath;
    }

    /**
     * Sets the value of the taskInstancePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskInstancePath(String value) {
        this.taskInstancePath = value;
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
