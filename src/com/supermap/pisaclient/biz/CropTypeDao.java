/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CropTypeDao.java - 2014-5-12 下午4:16:07
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.http.HttpHelper;

public class CropTypeDao {

	/**
	 * 
	 * @param parentId
	 *        0:获取农作物类型 >0：获取农作物种养类型
	 * @param areaId 
	 * 		        区域编码
	 * @return
	 */
	public List<CropType> getCropTypes(int parentId) {
		List<CropType> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCopesType\",\"CustomParams\":{\"parentId\":\"" + parentId + "\" },\"Type\":2}";
			//String param = "{\"Function\":\"crop.getCopesTypeByAreaId\",\"CustomParams\":{\"parentId\":\"" + parentId + "\"" +","+"\"areaId\":\""+areaId+"\"},\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<CropType>();
			CropType cropType = null;
			for (int i = 0; i < array.length(); i++) {
				cropType = new CropType();
				JSONObject obj = array.getJSONObject(i);
				cropType.id = obj.getInt("id");
				cropType.name = obj.getString("name");
				cropType.parentId = obj.getInt("parentId");
				list.add(cropType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	public List<CropType> getCropTypes(String areaCode) {
		List<CropType> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCopesTypeByAreaId\",\"CustomParams\":{\"areaCode\":\""+areaCode+"\"},\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<CropType>();
			CropType cropType = null;
			for (int i = 0; i < array.length(); i++) {
				cropType = new CropType();
				JSONObject obj = array.getJSONObject(i);
				cropType.id = obj.getInt("id");
				cropType.name = obj.getString("name");
				cropType.parentId = obj.getInt("parentId");
				list.add(cropType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 
	 * 根据id获取作物种类名字
	 * @return
	 */
	public String getCropTypeName(int id) {
		String name = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCopesTypeMsg\",\"CustomParams\":{\"id\":\"" + id + "\" },\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				name = obj.getString("name");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}
}
