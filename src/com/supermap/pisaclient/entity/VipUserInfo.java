package com.supermap.pisaclient.entity;

public class VipUserInfo 
{
	/**
	 * 大户id
	 */
	private int id;
	/**
	 * 大户名，这里主要是什么什么公司，什么什么社之类的
	 */
	private String name;
	/**
	 * 领导者的名字
	 */
	private String leaderName;
	/**
	 * 领导者手机号
	 */
	private String leaderPhone;
	/**
	 * 镇
	 */
	private String belongsTownship;
	/**
	 * 村
	 */
	private String belongsVillage;
	/**
	 * 区县
	 */
	private String countyName;
	/**
	 * 主要从事的商业活动
	 */
	private String businessScope;
	//种植面积
	private String areaScale;
	//年产值
	private String AnnualProductionValue;
	//从事生产经营人数
	private String ProdOperaCount;
	//技术人员人数
	private String TechCount;
	//技术人员平均年龄
	private String TechAverageAge;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLeaderName() {
		return leaderName;
	}
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	public String getLeaderPhone() {
		return leaderPhone;
	}
	public void setLeaderPhone(String leaderPhone) {
		this.leaderPhone = leaderPhone;
	}
	public String getBelongsTownship() {
		return belongsTownship;
	}
	public void setBelongsTownship(String belongsTownship) {
		this.belongsTownship = belongsTownship;
	}
	public String getBelongsVillage() {
		return belongsVillage;
	}
	public void setBelongsVillage(String belongsVillage) {
		this.belongsVillage = belongsVillage;
	}
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	public String getBusinessScope() {
		return businessScope;
	}
	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}
	public String getAreaScale() {
		return areaScale;
	}
	public void setAreaScale(String areaScale) {
		this.areaScale = areaScale;
	}
	public String getAnnualProductionValue() {
		return AnnualProductionValue;
	}
	public void setAnnualProductionValue(String annualProductionValue) {
		AnnualProductionValue = annualProductionValue;
	}
	public String getProdOperaCount() {
		return ProdOperaCount;
	}
	public void setProdOperaCount(String prodOperaCount) {
		ProdOperaCount = prodOperaCount;
	}
	public String getTechCount() {
		return TechCount;
	}
	public void setTechCount(String techCount) {
		TechCount = techCount;
	}
	public String getTechAverageAge() {
		return TechAverageAge;
	}
	public void setTechAverageAge(String techAverageAge) {
		TechAverageAge = techAverageAge;
	}
	
	
	
}
