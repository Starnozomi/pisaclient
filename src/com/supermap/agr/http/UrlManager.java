package com.supermap.agr.http;

public class UrlManager {

	public static String BASE_URL = "http://192.168.0.125"; 
	public static String FORMAL_URL = "www.ynpasc.cn"; 
	public static String PORT = ":8088";

	static String USER_LOGIN = "/pro/loginController.do?ajaxLogin";  //��¼
	static String USER_INFO = "/pro/userController.do?getUserInfo";  //��ȡָ���û���Ϣ
	static String USER_FOCUS = "/pro/tSScanController.do?getMyScan"; //��ȡ�ҵĹ�ע
	static String USER_REGIST = "/pro/userController.do?ajaxWasReg"; //ע��
	
	public static String userLogin() {
		return FORMAL_URL + USER_LOGIN;
	}
	
	public static String getUserinfo() {
		return FORMAL_URL + USER_INFO;
	}

	public static String userRegist() {
		return FORMAL_URL + USER_REGIST;
	}
}
