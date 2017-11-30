package com.supermap.pisaclient.biz;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.pisaclient.http.HttpHelper;

public class CropUploadDao {
	
	
	//主要区分int，String 参数
	public int addAgrInfo(int userId, String areaCode,int type,int breed,int growthperiod,String descript,double longitude,double latitude) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param;
			if (descript!=null) {
				param ="{\"userId\":" + userId + ", \"areaCode\":\"" + areaCode + "\", \"type\":" + type + ", \"breed\":" + breed + ", \"growthperiod\":" + growthperiod + ", \"descript\":\"" + descript + "\",\"longitude\":" + longitude + ",\"latitude\":" + latitude + "}";
			}
			else {
				param = "{\"userId\":" + userId + ", \"areaCode\":\"" + areaCode + "\", \"type\":" + type + ", \"breed\":" + breed + ", \"growthperiod\":" + growthperiod + ",\"longitude\":" + longitude + ",\"latitude\":" + latitude + "}";
			}
			if (growthperiod==0) {
				param ="{\"userId\":" + userId + ", \"areaCode\":\"" + areaCode + "\", \"type\":" + type + ", \"breed\":" + breed + ", \"descript\":\"" + descript + "\",\"longitude\":" + longitude + ",\"latitude\":" + latitude + "}";
			}
			params.put("para", param);
//			params.put("para", "{\"userId\":" + userId + ",\"areaCode\":\"" + areaCode + "\",\"type\":" + type + ",\"breed\":" + breed + ",\"growthperiod\":" + growthperiod + ",\"descript\":\"" + descript + "\"}");
			String url = "http://192.168.0.101:8080/PISAAgrManager/services/AgrManagerService/addAgrInfo";
			String res = HttpHelper.loadString(HttpHelper.ADD_AGR_INFO_URL, params, HttpHelper.METHOD_POST);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	@Deprecated
	public int addAgrImg(String descript,int agrInfoId,File file){
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"descript\":\"" + descript + "\", \"agrInfoId\":" + agrInfoId + "}";
			params.put("para", param);
			String res = HttpHelper.loadPic(file,HttpHelper.ADD_AGR_IMG_URL, params);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addAgrImg(String descript, int agrInfoId, String imageURLContent){
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param ;
			if(descript!=null){
				param ="{\"descript\":\"" + descript + "\", \"agrInfoId\":" + agrInfoId +",\"imageURLContent\":\""+imageURLContent+"\"}";
			}
			else {
				param = "{\"agrInfoId\":" + agrInfoId +",\"imageURLContent\":\""+imageURLContent+"\"}";
			}
			params.put("para", param);
			String res = HttpHelper.loadString(HttpHelper.ADD_AGR_IMG_URL, params,HttpHelper.METHOD_POST);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addAgrComment(String comment, int agrInfoId ,int userId, int parentId){
		JSONObject agrCommentt = new JSONObject(); 
//		try {
//			agrCommentt.put("userId", userId);
//			agrCommentt.put("parentId", parentId);
//			agrCommentt.put("agrInfoId", agrInfoId);
//			agrCommentt.put("comment", comment);
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println("json totring:  "+agrCommentt.toString());
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"userId\":"+userId+", \"parentId\":"+parentId+", \"agrInfoId\":"+agrInfoId+", \"comment\":\""+comment+"\"}";
			System.out.println(param);
			params.put("para", param);
			String res = HttpHelper.loadString(HttpHelper.ADD_AGR_COMMENT_URL, params,HttpHelper.METHOD_POST);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addAgrPraise(int agrInfoId ,int userId){
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"userId\":"+userId+", \"agrInfoId\":"+agrInfoId+"}";
			params.put("para", param);
			String res = HttpHelper.loadString(HttpHelper.ADD_AGR_PRAISE_URL, params,HttpHelper.METHOD_POST);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
