package com.supermap.pisaclient.entity;

import java.io.Serializable;

/**
 * 一张图功能中Marker点的额外信息存放类
 * @author TanRq
 *
 */
public class MarkerExtraInfo implements Serializable
{
	private static final long serialVersionUID = -501316013577096683L;
	
	private int id;
	private int type;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * marker点的类型(1:大户，2：农田，3：农情,4:区县点,5:实景监测)
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 设置Marker点的类型
	 * @param type (1:大户，2：农田，3：农情)
	 */
	public void setType(int type) {
		this.type = type;
	}

	
	
	
	
}
