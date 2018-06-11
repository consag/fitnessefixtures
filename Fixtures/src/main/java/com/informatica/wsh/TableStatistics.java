
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TableStatistics complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TableStatistics">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WidgetName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WidgetType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WidgetInstanceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LastErrorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="LastErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="EndTime" type="{http://www.informatica.com/wsh}DIServerDate"/>
 *         &lt;element name="NumAppliedRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumAffectedRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NumRejectedRows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Throughput" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PartitionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TableStatistics", propOrder = {
    "widgetName",
    "widgetType",
    "widgetInstanceName",
    "lastErrorCode",
    "lastErrorMessage",
    "startTime",
    "endTime",
    "numAppliedRows",
    "numAffectedRows",
    "numRejectedRows",
    "throughput",
    "partitionName",
    "groupName"
})
public class TableStatistics {

    @XmlElement(name = "WidgetName", required = true, nillable = true)
    protected String widgetName;
    @XmlElement(name = "WidgetType", required = true, nillable = true)
    protected String widgetType;
    @XmlElement(name = "WidgetInstanceName", required = true, nillable = true)
    protected String widgetInstanceName;
    @XmlElement(name = "LastErrorCode")
    protected int lastErrorCode;
    @XmlElement(name = "LastErrorMessage", required = true, nillable = true)
    protected String lastErrorMessage;
    @XmlElement(name = "StartTime", required = true, nillable = true)
    protected DIServerDate startTime;
    @XmlElement(name = "EndTime", required = true, nillable = true)
    protected DIServerDate endTime;
    @XmlElement(name = "NumAppliedRows")
    protected int numAppliedRows;
    @XmlElement(name = "NumAffectedRows")
    protected int numAffectedRows;
    @XmlElement(name = "NumRejectedRows")
    protected int numRejectedRows;
    @XmlElement(name = "Throughput")
    protected int throughput;
    @XmlElement(name = "PartitionName", required = true, nillable = true)
    protected String partitionName;
    @XmlElement(name = "GroupName", required = true, nillable = true)
    protected String groupName;

    /**
     * Gets the value of the widgetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidgetName() {
        return widgetName;
    }

    /**
     * Sets the value of the widgetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidgetName(String value) {
        this.widgetName = value;
    }

    /**
     * Gets the value of the widgetType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidgetType() {
        return widgetType;
    }

    /**
     * Sets the value of the widgetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidgetType(String value) {
        this.widgetType = value;
    }

    /**
     * Gets the value of the widgetInstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidgetInstanceName() {
        return widgetInstanceName;
    }

    /**
     * Sets the value of the widgetInstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidgetInstanceName(String value) {
        this.widgetInstanceName = value;
    }

    /**
     * Gets the value of the lastErrorCode property.
     * 
     */
    public int getLastErrorCode() {
        return lastErrorCode;
    }

    /**
     * Sets the value of the lastErrorCode property.
     * 
     */
    public void setLastErrorCode(int value) {
        this.lastErrorCode = value;
    }

    /**
     * Gets the value of the lastErrorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Sets the value of the lastErrorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastErrorMessage(String value) {
        this.lastErrorMessage = value;
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
     * Gets the value of the numAppliedRows property.
     * 
     */
    public int getNumAppliedRows() {
        return numAppliedRows;
    }

    /**
     * Sets the value of the numAppliedRows property.
     * 
     */
    public void setNumAppliedRows(int value) {
        this.numAppliedRows = value;
    }

    /**
     * Gets the value of the numAffectedRows property.
     * 
     */
    public int getNumAffectedRows() {
        return numAffectedRows;
    }

    /**
     * Sets the value of the numAffectedRows property.
     * 
     */
    public void setNumAffectedRows(int value) {
        this.numAffectedRows = value;
    }

    /**
     * Gets the value of the numRejectedRows property.
     * 
     */
    public int getNumRejectedRows() {
        return numRejectedRows;
    }

    /**
     * Sets the value of the numRejectedRows property.
     * 
     */
    public void setNumRejectedRows(int value) {
        this.numRejectedRows = value;
    }

    /**
     * Gets the value of the throughput property.
     * 
     */
    public int getThroughput() {
        return throughput;
    }

    /**
     * Sets the value of the throughput property.
     * 
     */
    public void setThroughput(int value) {
        this.throughput = value;
    }

    /**
     * Gets the value of the partitionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartitionName() {
        return partitionName;
    }

    /**
     * Sets the value of the partitionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartitionName(String value) {
        this.partitionName = value;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

}
