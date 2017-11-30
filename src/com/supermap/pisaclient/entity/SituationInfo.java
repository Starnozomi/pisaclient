package com.supermap.pisaclient.entity;

import java.util.List;

public class SituationInfo 
{
	private String id;
	private String cropName;
	private String pubertyName;
	private String uploadTime;
	private String publishName;
	private String descript;
	private List<String> images;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCropName() {
		return cropName;
	}
	public void setCropName(String cropName) {
		this.cropName = cropName;
	}
	public String getPubertyName() {
		return pubertyName;
	}
	public void setPubertyName(String pubertyName) {
		this.pubertyName = pubertyName;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getPublishName() {
		return publishName;
	}
	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
}
