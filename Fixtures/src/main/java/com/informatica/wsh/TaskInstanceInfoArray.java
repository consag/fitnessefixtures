
package com.informatica.wsh;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaskInstanceInfoArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskInstanceInfoArray">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TaskInstanceInfo" type="{http://www.informatica.com/wsh}TaskInstanceInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskInstanceInfoArray", propOrder = {
    "taskInstanceInfo"
})
public class TaskInstanceInfoArray {

    @XmlElement(name = "TaskInstanceInfo")
    protected List<TaskInstanceInfo> taskInstanceInfo;

    /**
     * Gets the value of the taskInstanceInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskInstanceInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskInstanceInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskInstanceInfo }
     * 
     * 
     */
    public List<TaskInstanceInfo> getTaskInstanceInfo() {
        if (taskInstanceInfo == null) {
            taskInstanceInfo = new ArrayList<TaskInstanceInfo>();
        }
        return this.taskInstanceInfo;
    }

}
