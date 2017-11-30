/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CropPeriodDao.java - 2014-5-12 下午4:17:20
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.CropBreed;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.http.HttpHelper;

public class CropPeriodDao {

	public List<CropPeriod> getCropGrowPeriodByBreed(CropType cropType) {
		List<CropPeriod> list = null;
		String param;
		JSONArray array;
		try
		{
			HashMap<String,String> params=new HashMap<String,String>();
			param = "{\"Function\":\"crop.getGrowthperiod\",\"CustomParams\":{\"cropsType\":\"" + cropType.id + "\" },\"Type\":2}";
			params.put("param",param);
			array=HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<CropPeriod>();
			for(int i=0;i<array.length();i++)
			{
				JSONObject obj=array.getJSONObject(i);
				CropPeriod period=new CropPeriod();
				if (obj.opt("id") != null)
					period.id = obj.getInt("id");
				if (obj.opt("growthperiod") != null)
					period.growthperiod = obj.getString("growthperiod");
				list.add(period);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return list;
	}
	/**
	 * 
	 * @param cropsType
	 * @return
	 */
	public List<CropPeriod> getCropGrowPeriod(CropType cropType) {
		List<CropPeriod> list = null;
		String param;
		JSONArray array;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			param = "{\"Function\":\"crop.getGrowthperiod\",\"CustomParams\":{\"cropsType\":\"" + cropType.parentId + "\" },\"Type\":2}";
			params.put("param", param);
			array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<CropPeriod>();
			if (array.length() == 0) {
				param = "{\"Function\":\"crop.getGrowthperiod\",\"CustomParams\":{\"cropsType\":\"" + cropType.id + "\" },\"Type\":2}";
				params.put("param", param);
				array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			}
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CropPeriod period = new CropPeriod();
				if (obj.opt("id") != null)
					period.id = obj.getInt("id");
				if (obj.opt("growthperiod") != null)
					period.growthperiod = obj.getString("growthperiod");
				list.add(period);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	

	/**
	 * 
	 * 根据id获取作物发育期名字
	 * @return
	 */
	public String getCropGrowPeriod(int id) {
		String name = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getGrowthperiodMsg\",\"CustomParams\":{\"id\":\"" + id + "\" },\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				name = obj.getString("growthperiod");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}

}
