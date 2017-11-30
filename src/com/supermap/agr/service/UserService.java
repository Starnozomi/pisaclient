package com.supermap.agr.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.agr.http.HttpHandler;
import com.supermap.agr.http.UrlManager;
import com.supermap.agr.obj.BaseUserInfo;

import android.content.Context;
import android.content.SharedPreferences;



public class UserService {
	
	public JSONObject userLogin(String acount, String passwd){
		String msg ="";
		int pass = 0;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", acount));
		params.add(new BasicNameValuePair("password", passwd));
		JSONObject jsload = HttpHandler.postrequestT(UrlManager.userLogin(), params);
		return jsload;
	}
	
	public JSONObject userRegist(String acount, String passwd, String mobile, String city){
		String msg ="";
		int pass = 0;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", acount));
		params.add(new BasicNameValuePair("password", passwd));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("userArea", city));
		JSONObject jsload = HttpHandler.postrequestT(UrlManager.userRegist(), params);
		return jsload;
	}

	public List<BaseUserInfo> getUserInfo(String userid){
		List<BaseUserInfo> list = new ArrayList<BaseUserInfo>();
		BaseUserInfo baseUserInfo = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userid));
		JSONObject jsload = new JSONObject(); 
		jsload = HttpHandler.postrequestT(UrlManager.getUserinfo(), params);
		try {
			jsload = new JSONObject( jsload.getString("jsonStr") );
			jsload = new JSONObject( jsload.getString("obj") );
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=0;i<jsload.length();i++){
			try {
				if(jsload != null) {
						baseUserInfo = new BaseUserInfo();
						if(userid.equals(jsload.getString("id"))){
							baseUserInfo.setNickName(jsload.getString("realName"));
							baseUserInfo.setMobliePhone(jsload.getString("mobilePhone"));
							baseUserInfo.setDepart(jsload.getString("userDepartment"));
							list.add(baseUserInfo);
						}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
}
