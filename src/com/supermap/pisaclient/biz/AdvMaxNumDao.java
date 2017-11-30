package com.supermap.pisaclient.biz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ElapseTime;
import com.supermap.pisaclient.http.HttpHelper;


public class AdvMaxNumDao {
	
	public int getMaxNum(int type,int userID,String content ,boolean isExpert){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params = getParams(type, userID, content, isExpert);
			ElapseTime elapseTime = new ElapseTime("最大数目");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
			return -1;
		}
		return totalCount;
	}
	
	public int getMaxNum(String areaCode,int type,int userID,String content ,boolean isExpert){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params = getParams(areaCode,type, userID, content, isExpert);
			ElapseTime elapseTime = new ElapseTime("最大数目");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
			return -1;
		}
		return totalCount;
	}
	
public HashMap<String, String> getParams(String areaCode,int type ,int userID,String content,boolean isExpert){
		
		HashMap<String, String> params = new HashMap<String, String>();
		JSONObject jsonParam ;
		JSONObject customParams ;
		try {
			switch (type) {
			case Constant.ADV_TYPE_NEW:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				customParams.put("areaCode", areaCode);
				jsonParam.put("Function", "advisoryinfo.getbyareacode.all.new.num");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_HOT:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				customParams.put("areaCode", areaCode);
				jsonParam.put("Function", "advisoryinfo.getbyareacode.all.more.num");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_MY:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				if (!isExpert) {
					jsonParam.put("Function", "advisoryinfo.get.all.my.num");
				}else {
					jsonParam.put("Function", "advisoryinfo.get.all.my.expert.num");
				}
				customParams.put("userId", userID);
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_SERCH:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("Function", "advisoryinfo.get.by.content.num");
				customParams.put("content", "%"+content+"%");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			default:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("CustomParams", customParams.toString());
				break;
			}
		    jsonParam.put("Type", 2);
			params.put("param",  jsonParam.toString());
		} catch (Exception e) {
		}
		return params;
	}
	
	public HashMap<String, String> getParams(int type ,int userID,String content,boolean isExpert){
		
		HashMap<String, String> params = new HashMap<String, String>();
		JSONObject jsonParam ;
		JSONObject customParams ;
		try {
			switch (type) {
			case Constant.ADV_TYPE_NEW:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("Function", "advisoryinfo.get.all.new.num");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_HOT:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("Function", "advisoryinfo.get.all.more.num");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_MY:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				if (!isExpert) {
					jsonParam.put("Function", "advisoryinfo.get.all.my.num");
				}else {
					jsonParam.put("Function", "advisoryinfo.get.all.my.expert.num");
				}
				customParams.put("userId", userID);
				jsonParam.put("CustomParams", customParams.toString());
				break;
			case Constant.ADV_TYPE_SERCH:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("Function", "advisoryinfo.get.by.content.num");
				customParams.put("content", "%"+content+"%");
				jsonParam.put("CustomParams", customParams.toString());
				break;
			default:
				jsonParam = new JSONObject();
				customParams = new JSONObject();
				jsonParam.put("CustomParams", customParams.toString());
				break;
			}
		    jsonParam.put("Type", 2);
			params.put("param",  jsonParam.toString());
		} catch (Exception e) {
		}
		return params;
	}
	

}
