package com.supermap.pisaclient.entity;

import java.io.Serializable;

public class AreaSelectParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int flag;			//地区选择的业务标志
	public boolean isSelectMore ;   //是否可以多选
	public boolean isWeatherArea;	//是否可以和天气中的地区重复
	public boolean isShowRemind;	//是否用*显示已选择地区

}
