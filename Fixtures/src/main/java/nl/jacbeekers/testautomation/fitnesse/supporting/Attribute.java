package nl.jacbeekers.testautomation.fitnesse.supporting;

import java.util.Date;

public class Attribute {
	String format;
	String text;
	Date date;
	Double number;
		
	public void setFormat(String format){
		this.format = format;
	}
	
	public String getFormat(){
		return format;
	}

	public void setDate(Date date){
		this.date = date;
	}
	
	public Date getDate(){
		return date;
	}

	public void setNumber(Double number){
		this.number = number;
	}
	
	public Double getNumber(){
		return number;
	}

	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
}
