
package com.informatica.wsh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DIServerDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DIServerDate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NanoSeconds" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Seconds" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Minutes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Hours" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Month" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Year" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="UTCTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DIServerDate", propOrder = {
    "date",
    "nanoSeconds",
    "seconds",
    "minutes",
    "hours",
    "month",
    "year",
    "utcTime"
})
public class DIServerDate {

    @XmlElement(name = "Date")
    protected int date;
    @XmlElement(name = "NanoSeconds")
    protected int nanoSeconds;
    @XmlElement(name = "Seconds")
    protected int seconds;
    @XmlElement(name = "Minutes")
    protected int minutes;
    @XmlElement(name = "Hours")
    protected int hours;
    @XmlElement(name = "Month")
    protected int month;
    @XmlElement(name = "Year")
    protected int year;
    @XmlElement(name = "UTCTime")
    protected int utcTime;

    /**
     * Gets the value of the date property.
     * 
     */
    public int getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     */
    public void setDate(int value) {
        this.date = value;
    }

    /**
     * Gets the value of the nanoSeconds property.
     * 
     */
    public int getNanoSeconds() {
        return nanoSeconds;
    }

    /**
     * Sets the value of the nanoSeconds property.
     * 
     */
    public void setNanoSeconds(int value) {
        this.nanoSeconds = value;
    }

    /**
     * Gets the value of the seconds property.
     * 
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Sets the value of the seconds property.
     * 
     */
    public void setSeconds(int value) {
        this.seconds = value;
    }

    /**
     * Gets the value of the minutes property.
     * 
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Sets the value of the minutes property.
     * 
     */
    public void setMinutes(int value) {
        this.minutes = value;
    }

    /**
     * Gets the value of the hours property.
     * 
     */
    public int getHours() {
        return hours;
    }

    /**
     * Sets the value of the hours property.
     * 
     */
    public void setHours(int value) {
        this.hours = value;
    }

    /**
     * Gets the value of the month property.
     * 
     */
    public int getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     */
    public void setMonth(int value) {
        this.month = value;
    }

    /**
     * Gets the value of the year property.
     * 
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     */
    public void setYear(int value) {
        this.year = value;
    }

    /**
     * Gets the value of the utcTime property.
     * 
     */
    public int getUTCTime() {
        return utcTime;
    }

    /**
     * Sets the value of the utcTime property.
     * 
     */
    public void setUTCTime(int value) {
        this.utcTime = value;
    }

}
