package com.supermap.pisaclient.entity;

import java.io.Serializable;

public class Climate implements Serializable {
	public String areaCode;
	public String month;
	public String climateType;
	public String year_value;
	public String month_value;
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getClimateType() {
		return climateType;
	}
	public void setClimateType(String climateType) {
		this.climateType = climateType;
	}
	public String getYear_value() {
		return year_value;
	}
	public void setYear_value(String year_value) {
		this.year_value = year_value;
	}
	public String getMonth_value() {
		return month_value;
	}
	public void setMonth_value(String month_value) {
		this.month_value = month_value;
	}
	
	
	
}
