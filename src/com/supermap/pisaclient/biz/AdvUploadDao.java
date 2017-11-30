package com.supermap.pisaclient.biz;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.bool;

import com.supermap.pisaclient.http.HttpHelper;

public class AdvUploadDao {
	
	public int addAdvinfo(int userId,String areacode,String qestion,int subjectid){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("userId", userId);
			jsonObj.put("areacode", areacode);
			jsonObj.put("qestion", qestion);
			
			if(subjectid==0){
				jsonObj.put("subjectid", "null");
			}
			else {
				jsonObj.put("subjectid", subjectid);
			}
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advisoryinfo.add");
			jsonParam.put("values", jsonValue);
			
//			String param = "{\"name\":\"t_advisoryinfo.add\",\"values\":[{\"userId\":3,\"areacode\":\"500229\",\"qestion\":\"我家水稻里田里有很多蝗虫，测试一些默认值\",\"subjectid\":5}]}";
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addFarmAdvinfo(int userId,String areacode,String qestion,int subjectid,int farmlandid,String lat,String lon){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("userId", userId);
			jsonObj.put("areacode", areacode);
			jsonObj.put("qestion", qestion);
			if(subjectid==0){
				jsonObj.put("subjectid", "null");
			}
			else {
				jsonObj.put("subjectid", subjectid);
			}
			jsonObj.put("farmlandid", farmlandid);
			jsonObj.put("lat", lat);
			jsonObj.put("lon", lon);
			/*if(subjectid==0){
				jsonObj.put("subjectid", "null");
			}
			else {
				jsonObj.put("subjectid", subjectid);
			}*/
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advisoryinfo.add2farm");
			jsonParam.put("values", jsonValue);
			
//			String param = "{\"name\":\"t_advisoryinfo.add\",\"values\":[{\"userId\":3,\"areacode\":\"500229\",\"qestion\":\"我家水稻里田里有很多蝗虫，测试一些默认值\",\"subjectid\":5}]}";
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	/**
	 *  服务器返回的是【id,id】
	 * @param imageURLContent
	 * @param advInfoId
	 * @param descript
	 * @return
	 */
	public int addAdvImg(String imageURLContent,int advInfoId,String descript){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("imageURLContent", imageURLContent);
			jsonObj.put("advInfoId", advInfoId);
			jsonObj.put("descript", descript);
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advinfoimg.add");
			jsonParam.put("values", jsonValue);
			
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public boolean addAdvImgs(List<String> imageURLContents,int advInfoId,String descript){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONArray jsonValue = new JSONArray();
			for (int i = 0; i < imageURLContents.size(); i++) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("imageURLContent", imageURLContents.get(i));
				jsonObj.put("advInfoId", advInfoId);
				jsonObj.put("descript", descript);
				jsonValue.put(jsonObj);
			}
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advinfoimg.add");
			jsonParam.put("values", jsonValue);
			
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			String id[] = num.split(",");
			for (int i = 0; i < id.length; i++) {
				if (Integer.valueOf(id[i])<=0) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int addAdvComment(String comment, int advInfoId ,int userId, int parentId,int expertid){
		JSONObject agrCommentt = new JSONObject(); 
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("comment", comment);
			jsonObj.put("advInfoId", advInfoId);
			jsonObj.put("parentId", parentId);
			if (userId==-1) {
				jsonObj.put("userId", "null");
			}
			else {
				jsonObj.put("userId", userId);
			}
			if (expertid==-1) {
				jsonObj.put("expertid", "null");
			}
			else {
				jsonObj.put("expertid", expertid);
			}
			
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advinfocomment.add");
			jsonParam.put("values", jsonValue);
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addAdvPrasis(int advInfoId ,int userId,int isPraise){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("userId", userId);
			jsonObj.put("advInfoId", advInfoId);
			jsonObj.put("isPraise", isPraise);
			
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advpraise.add");
			jsonParam.put("values", jsonValue);
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public int addAdvScore(int advInfoId ,int userId,int expertid,float score){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("userId", userId);
			jsonObj.put("advInfoId", advInfoId);
			jsonObj.put("expertid", expertid);
			jsonObj.put("score", score);
			
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","t_advscore.add");
			jsonParam.put("values", jsonValue);
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
}
