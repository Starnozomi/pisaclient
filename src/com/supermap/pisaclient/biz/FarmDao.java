/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @FarmDao.java - 2014-4-25 下午3:39:09
 */

package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;






import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmCrop;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.Point2D;

public class FarmDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5784586789901965621L;

	public List<Farm> getFarmByUserId(int userId) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"userId\":\"" + userId + "\"}");
		List<Farm> all = new ArrayList<Farm>();
		try {
			JSONArray array = HttpHelper.load(HttpHelper.GET_FARMLAND_BY_USERID_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Farm farm = new Farm();
				if (obj.opt("id") != null)
					farm.id = obj.getInt("id");
				if (obj.opt("coordinates") != null)
					farm.coordinates = obj.getString("coordinates");
				if (obj.opt("descript") != null)
					farm.descript = obj.getString("descript");
				if (obj.opt("height") != null)
					farm.height = obj.getString("height");
				if (obj.opt("longitude") != null)
					farm.longitude = CommonUtil.getCutString(obj.getString("longitude"), 6);
				if (obj.opt("latitude") != null)
					farm.latitude = CommonUtil.getCutString(obj.getString("latitude"), 6);
				if (obj.opt("areacode") != null)
					farm.areaCode = obj.getString("areacode");
				if (obj.opt("areaname") != null)
					farm.areaName = obj.getString("areaname");
				if (obj.opt("userId") != null)
					farm.userId = obj.getInt("userId");
				if (obj.opt("area") != null)
					farm.area = CommonUtil.getCutString(obj.getString("area"), 2);
				if (obj.opt("ctpid") != null)
					farm.cropTypeId = obj.getString("ctpid");
				if (obj.opt("ctpname") != null)
					farm.cropTypeName = obj.getString("ctpname");
				if (obj.opt("ctcid") != null)
					farm.cropVarietyId = obj.getString("ctcid");
				if (obj.opt("ctcname") != null)
					farm.cropVarietyName = obj.getString("ctcname");
				if (obj.opt("agrid") != null)
					farm.cropPeriodId = obj.getString("agrid");
				if (obj.opt("growthperiod") != null)
					farm.cropPeriodName = obj.getString("growthperiod");
				if (obj.opt("code") != null)
					farm.code = obj.getString("code");
				all.add(farm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}

	public List<Feature> getFarmFeaturesByUserId(int userId) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"userId\":\"" + userId + "\"}");
		List<Feature> all = new ArrayList<Feature>(); 
		try {
			JSONArray array = HttpHelper.load(HttpHelper.GET_FARMLAND_BY_USERID_URL, params, HttpHelper.METHOD_POST);
			String coordinates = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Feature f = new Feature();
				f.geometry = new Geometry();
				f.fieldNames = new String[] { "descript", "height", "longitude", "latitude" };
				f.fieldValues = new String[4];
				if (obj.opt("id") != null) {
					f.setID(obj.getInt("id"));
					f.geometry.id = obj.getInt("id");
				}
				if (obj.opt("descript") != null)
					f.fieldValues[0] = obj.getString("descript");
				if (obj.opt("coordinates") != null) {
					coordinates = obj.getString("coordinates");
					String[] points = coordinates.split(" ");
					Point2D[] p2d = new Point2D[points.length];
					for (int k = 0; k < points.length; k++) {
						String p = points[k];
						String[] point = p.split(",");
						Point2D pd = new Point2D();
						pd.x = Double.parseDouble(point[0]);
						pd.y = Double.parseDouble(point[1]);
						p2d[k] = pd;
					}
					f.geometry.points = p2d;
				}
				if (obj.opt("height") != null)
					f.fieldValues[1] = obj.getString("height");
				if (obj.opt("longitude") != null)
					f.fieldValues[2] = CommonUtil.getCutString(obj.getString("longitude"), 6);
				if (obj.opt("latitude") != null)
					f.fieldValues[3] = CommonUtil.getCutString(obj.getString("latitude"), 6);
				all.add(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return all;
	}
	
	public Farm getFarmAttributeByIdNew(int id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"farmattribute.query.new\",\"CustomParams\":{\"id\":" + id + "},\"Type\":2}");
		Farm farm = new Farm();
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("id") != null)
					farm.id = obj.getInt("id");
				if (obj.opt("coordinates") != null)
					farm.coordinates = obj.getString("coordinates");
				if (obj.opt("descript") != null)
					farm.descript = obj.getString("descript");
				if (obj.opt("height") != null)
					farm.height = obj.getString("height");
				if (obj.opt("longitude") != null)
					farm.longitude = CommonUtil.getCutString(obj.getString("longitude"), 6);
				if (obj.opt("latitude") != null)
					farm.latitude = CommonUtil.getCutString(obj.getString("latitude"), 6);
				if (obj.opt("areacode") != null)
					farm.areaCode = obj.getString("areacode");
				if (obj.opt("areaname") != null)
					farm.areaName = obj.getString("areaname");
				if (obj.opt("userId") != null)
					farm.userId = obj.getInt("userId");
				if (obj.opt("area") != null)
					farm.area = CommonUtil.getCutString(obj.getString("area"), 2);
				
				if (obj.opt("ctpid") != null) {
					farm.cropTypeId = obj.getString("ctpid");
				}
				if (obj.opt("ctpname") != null)
					farm.cropTypeName = obj.getString("ctpname");
				if (obj.opt("ctcid") != null)
					farm.cropVarietyId = obj.getString("ctcid");
				if (obj.opt("ctcname") != null)
					farm.cropVarietyName = obj.getString("ctcname");
				
				if (farm.cropTypeId==farm.cropVarietyId) {
					farm.cropVarietyName ="";
					farm.cropVarietyId ="0";
				}
			
				if (obj.opt("agrid") != null)
					farm.cropPeriodId = obj.getString("agrid");
				if (obj.opt("growthperiod") != null)
					farm.cropPeriodName = obj.getString("growthperiod");
				if (obj.opt("code") != null)
					farm.code = obj.getString("code");
				
				if(obj.opt("workstationid") != null){
						farm.workStationId = obj.getInt("workstationid");
				}
				if(obj.opt("stationname") != null){
					farm.workStationName = obj.getString("stationname");
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return farm;
	}

	
	
	
	public Farm getFarmAttributeById(int id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"farmattribute.query\",\"CustomParams\":{\"id\":" + id + "},\"Type\":2}");
		Farm farm = new Farm();
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("id") != null)
					farm.id = obj.getInt("id");
				if (obj.opt("coordinates") != null)
					farm.coordinates = obj.getString("coordinates");
				if (obj.opt("descript") != null)
					farm.descript = obj.getString("descript");
				if (obj.opt("height") != null)
					farm.height = obj.getString("height");
				if (obj.opt("longitude") != null)
					farm.longitude = CommonUtil.getCutString(obj.getString("longitude"), 6);
				if (obj.opt("latitude") != null)
					farm.latitude = CommonUtil.getCutString(obj.getString("latitude"), 6);
				if (obj.opt("areacode") != null)
					farm.areaCode = obj.getString("areacode");
				if (obj.opt("areaname") != null)
					farm.areaName = obj.getString("areaname");
				if (obj.opt("userId") != null)
					farm.userId = obj.getInt("userId");
				if (obj.opt("area") != null)
					farm.area = CommonUtil.getCutString(obj.getString("area"), 2);
				int parentid = 0;
				if (obj.opt("ctpid") != null)
				{
					String strid = obj.getString("ctpid");
					if ((strid!=null)&&(!strid.equals("null"))) {
						parentid = Integer.valueOf(strid);
					}
				}
				if (parentid!=0) {
					farm.cropTypeId = obj.getString("ctpid");
					if (obj.opt("ctpname") != null)
						farm.cropTypeName = obj.getString("ctpname");
					if (obj.opt("ctcid") != null)
						farm.cropVarietyId = obj.getString("ctcid");
					if (obj.opt("ctcname") != null)
						farm.cropVarietyName = obj.getString("ctcname");
				}
				else {
					if (obj.opt("ctcid") != null)
						farm.cropTypeId = obj.getString("ctcid");
					if (obj.opt("ctcname") != null){
						farm.cropTypeName = obj.getString("ctcname");
					}
					farm.cropVarietyId ="0";
					farm.cropVarietyName = "";
				}
				
				if (obj.opt("agrid") != null)
					farm.cropPeriodId = obj.getString("agrid");
				if (obj.opt("growthperiod") != null)
					farm.cropPeriodName = obj.getString("growthperiod");
				if (obj.opt("code") != null)
					farm.code = obj.getString("code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return farm;
	}

	public boolean updateFarmlandCoordinate(Farm farm) {
		farm.coordinates = farm.coordinates == null ? "" : farm.coordinates;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para","{\"id\":" + farm.id + ",\"coordinates\":\"" + farm.coordinates + "\",\"longitude\":"
						+ CommonUtil.getCutString(farm.longitude, 6) + ",\"latitude\":" + CommonUtil.getCutString(farm.latitude, 6)
						+ ",\"area\":" + CommonUtil.getCutString(farm.area, 2) + "}");
		try {
			String res = HttpHelper.loadString(HttpHelper.UPDATE_FARMLAND_COORDINATE_USERID_URL, params, HttpHelper.METHOD_POST);
			return !"0".equals(res) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateFarmlandAttribute(Farm farm) {
		farm.descript = farm.descript == null ? "" : farm.descript;
		farm.height = farm.height == null ? "0" : farm.height;
		farm.longitude = farm.longitude == null ? "0" : farm.longitude;
		farm.latitude = farm.latitude == null ? "0" : farm.latitude;
		farm.area = farm.area == null ? "0" : farm.area;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"id\":" + farm.id + ",\"descript\":\"" + farm.descript + "\",\"height\":" + farm.height + ", \"longitude\":"
				+ CommonUtil.getCutString(farm.longitude, 6) + ",\"latitude\":" + CommonUtil.getCutString(farm.latitude, 6) + ",\"userId\":"
				+ farm.userId + ",  \"area\":" + CommonUtil.getCutString(farm.area, 2) + ", \"areaCode\":\"" + farm.areaCode + "\", \"workStationID\":" + farm.workStationId + "}");
		try {
			String res = HttpHelper.loadString(HttpHelper.UPDATE_FARMLAND_ATTRIBUTE_URL, params, HttpHelper.METHOD_POST);
			return !"0".equals(res) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteFarmland(int id) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"name\":\"cq.update.delete.farmland\", \"values\":\"[{\\\"id\\\":\\\"" + id + "\\\"}]\"}");
		try {
			HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String addFarmland(Farm farm) {
		farm.coordinates = farm.coordinates == null ? "0" : farm.coordinates;
		farm.descript = farm.descript == null ? "默认农田" : farm.descript;
		farm.height = farm.height == null ? "0" : farm.height;
		farm.longitude = farm.longitude == null ? "0" : CommonUtil.getCutString(farm.longitude, 6);
		farm.latitude = farm.latitude == null ? "0" : CommonUtil.getCutString(farm.latitude, 6);
		farm.areaCode = farm.areaCode == null ? "0" : farm.areaCode;
		farm.area = farm.area == null ? "0" : CommonUtil.getCutString(farm.area, 2);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"coordinates\":\"" + farm.coordinates + "\", \"descript\":\"" + farm.descript + "\",\"height\":" + farm.height
				+ ", \"longitude\":" + farm.longitude + ",\"latitude\":" + farm.latitude + ",\"userId\":" + farm.userId + ", \"areaCode\":\""
				+ farm.areaCode + "\", \"area\":" + farm.area + ",\"workStationId\":"+ farm.workStationId +"}");
		try {
			String s = HttpHelper.loadString(HttpHelper.ADD_FARMLAND_URL, params, HttpHelper.METHOD_POST);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean updateFarmCrops(FarmCrop farmCrop) {
		farmCrop.crop = farmCrop.crop == null ? "0" : farmCrop.crop;
		farmCrop.farmland = farmCrop.farmland == null ? "0" : farmCrop.farmland;
		farmCrop.agrweaquota = farmCrop.agrweaquota == null ? "0" : farmCrop.agrweaquota;
		HashMap<String, String> params = new HashMap<String, String>();
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String nowStr = sdf.format(dt);
		
		params.put("param", "{\"name\":\"farmcrops.update\", \"values\":\"[{\\\"crop\\\":" + farmCrop.crop + ",\\\"agrweaquota\\\":"
				+ farmCrop.agrweaquota + ",\\\"updatetime\\\":'" + nowStr + "',\\\"farmland\\\":" + farmCrop.farmland + "}]\"}");
		try {
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			return !"0".equals(res) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addFarmCrops(FarmCrop farmCrop) {
		farmCrop.crop = farmCrop.crop == null ? "0" : farmCrop.crop;
		farmCrop.farmland = farmCrop.farmland == null ? "0" : farmCrop.farmland;
		farmCrop.agrweaquota = farmCrop.agrweaquota == null ? "0" : farmCrop.agrweaquota;
		HashMap<String, String> params = new HashMap<String, String>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
		String updatetime =myFmt.format(c.getTime());

		params.put("param", "{\"name\":\"farmcrops.add\", \"values\":\"[{\\\"crop\\\":" + farmCrop.crop + ",\\\"farmland\\\":"
				+ farmCrop.farmland + ",\\\"agrweaquota\\\":" + farmCrop.agrweaquota + ",\\\"updatetime\\\":'" + updatetime + "'}]\"}");
		try {
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			return !"0".equals(res) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public int addFarmImg(String descript,int FarmlandId,String imageURLContent){
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param ;
			if(descript!=null){
				param ="{\"descript\":\"" + descript + "\", \"farmlandId\":" + FarmlandId +",\"imageURLContent\":\""+imageURLContent+"\"}";
			}
			else {
				param = "{\"farmlandId\":" + FarmlandId +",\"imageURLContent\":\""+imageURLContent+"\"}";
			}
			params.put("para", param);
			String res = HttpHelper.loadString(HttpHelper.ADD_FARM_IMG_URL, params,HttpHelper.METHOD_POST);
			return Integer.parseInt(res);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
