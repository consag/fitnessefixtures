
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PerformanceCounter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PerformanceCounter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CounterName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CounterValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="WidgetName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CounterStringValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CounterType" type="{http://www.informatica.com/wsh}EPerformanceCounterType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerformanceCounter", propOrder = {
    "counterName",
    "counterValue",
    "widgetName",
    "counterStringValue",
    "counterType"
})
public class PerformanceCounter {

    @XmlElement(name = "CounterName", required = true, nillable = true)
    protected String counterName;
    @XmlElement(name = "CounterValue")
    protected int counterValue;
    @XmlElement(name = "WidgetName", required = true, nillable = true)
    protected String widgetName;
    @XmlElement(name = "CounterStringValue", required = true, nillable = true)
    protected String counterStringValue;
    @XmlElement(name = "CounterType", required = true)
    protected EPerformanceCounterType counterType;

    /**
     * Gets the value of the counterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCounterName() {
        return counterName;
    }

    /**
     * Sets the value of the counterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCounterName(String value) {
        this.counterName = value;
    }

    /**
     * Gets the value of the counterValue property.
     * 
     */
    public int getCounterValue() {
        return counterValue;
    }

    /**
     * Sets the value of the counterValue property.
     * 
     */
    public void setCounterValue(int value) {
        this.counterValue = value;
    }

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
     * Gets the value of the counterStringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCounterStringValue() {
        return counterStringValue;
    }

    /**
     * Sets the value of the counterStringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCounterStringValue(String value) {
        this.counterStringValue = value;
    }

    /**
     * Gets the value of the counterType property.
     * 
     * @return
     *     possible object is
     *     {@link EPerformanceCounterType }
     *     
     */
    public EPerformanceCounterType getCounterType() {
        return counterType;
    }

    /**
     * Sets the value of the counterType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EPerformanceCounterType }
     *     
     */
    public void setCounterType(EPerformanceCounterType value) {
        this.counterType = value;
    }

}
