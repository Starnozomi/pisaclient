/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @User.java - 2014-5-30 上午9:20:17
 */

package com.supermap.pisaclient.entity;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 874818945678709303L;
	public int id;
	public String userName;
	public String loginName;
	public String showName;
	public String password;
	public String mobile;
	public String address;
	public String email;
	public String headImage;
	public String headImageType;
	public String introduction;
	public String areaCode;
	public boolean isExpert = false;
	public List<String> subjects;
	public boolean isSave = false;
	public int roleid = -1;
	public boolean isVip = false;

}
