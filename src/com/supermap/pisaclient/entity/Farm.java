/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Farm.java - 2014-4-25 上午9:51:56
 */

package com.supermap.pisaclient.entity;

public class Farm {

	public int id; // id
	public String code; // 地块编号
	public String coordinates; // 经纬度
	public String descript; // 描述
	public String height; // 高度
	public String longitude; // 经度
	public String latitude; // 纬度
	public String areaCode; // 地区编号
	public String areaName; // 地区名称
	public int userId; //
	public String area;
	public String cropTypeId;
	public String cropTypeName;
	public String cropVarietyId;
	public String cropVarietyName;
	public String cropPeriodId;
	public String cropPeriodName;
	
	public int workStationId;
	public String workStationName;

	@Override
	public String toString() {
		return "id:" + id + ",code:" + code + ",coordinates:" + coordinates + ",descript:" + descript + ",height:" + height
				+ ",longitude:" + longitude + ",latitude:" + latitude + ",areaCode:" + areaCode + ",areaName:" + areaName + ",area:" + area
				+",workStationId:"+ workStationId +",workStationName:"+ workStationName +"";
	}

}
