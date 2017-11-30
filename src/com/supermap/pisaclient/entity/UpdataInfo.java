package com.supermap.pisaclient.entity;

/**
 * 自动更新的实体类
 * @author trq
 *
 */
public class UpdataInfo 
{
	private String version;
	private String url;
	private String description;
	/**
	 * 获取版本号
	 * @return
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * 设置版本号
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * 获取APK下载地址
	 * @return
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置APK下载地址
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 获取更新描述信息
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置更新描述信息
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
