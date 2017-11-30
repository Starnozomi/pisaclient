/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @User.java - 2014-5-30 上午9:20:17
 */

package com.supermap.pisaclient.entity;

import java.io.Serializable;
import java.util.List;

public class WorkStation implements Serializable {

	public int id;
	public String stationName;
	public String areaCode;
	public String desc;
	public double lat;
	public double lon;
	public int managerid;
	public String createTime;

}
