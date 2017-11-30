package com.supermap.agr.obj;

public class BaseUserInfo {

	 private String nickName;
	 private String userName;
	 private String mobliePhone;
	 private String city;
	 private String email;
	 private String depart;
	 private String company;
	 
	public BaseUserInfo(String nickName, String userName, String mobliePhone, String city, String email, String depart, String company) {
		super();
		this.nickName = nickName;
		this.userName = userName;
		this.mobliePhone = mobliePhone;
		this.city = city;
		this.email = email;
		this.depart = depart;
		this.company = company;
	}
	
	public BaseUserInfo(){}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobliePhone() {
		return mobliePhone;
	}

	public void setMobliePhone(String mobliePhone) {
		this.mobliePhone = mobliePhone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	 
	 
}
