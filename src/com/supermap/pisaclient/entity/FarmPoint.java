package com.supermap.pisaclient.entity;

/**
 * 主要用于农气一张图模块
 * @author TanRq
 *
 */
public class FarmPoint 
{
	private int id;
	private double longitude;
	private double latitude;
	private String descript;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
}
