package com.supermap.pisaclient.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.ui.CitySetActivity;
import com.supermap.pisaclient.ui.WeatherActivity;
public class PlaceManager {
	private Context context;
	private CityDao mCityDao;
	private List<City> mCitys;
	private OnPlaceManagerListener listener;
	private Map<String,Area> areaList;
	private static Area currentArea;
	
	public PlaceManager(Context context,OnPlaceManagerListener listener){
		this.context = context;
		this.listener = listener;
		this.mCityDao  = new CityDao(context);
		areaList = new HashMap<String,Area>();
		//new LoadAllCityTask().execute();
		initAreaCenter();
	}
	

	private void initAreaCenter(){

		
		Area a = new Area();
		a.areacode = "150000";a.areaname = "内蒙古自治区";a.centerlat =40.848483; a.centerlon = 111.755646; a.maplevel = 8.5f;a.enname="neimengguzizhiqu";
		areaList.put("内蒙古自治区", a);	
		
		a = new Area();
		a.areacode = "150100";a.areaname = "呼和浩特市";a.centerlat =40.848483; a.centerlon = 111.755646; a.maplevel = 11.5f;a.enname="huhehaoteshi";
		areaList.put("呼和浩特市", a);
		
		a = new Area();
		a.areacode = "150200";a.areaname = "包头市";a.centerlat =40.663367; a.centerlon = 109.845107; a.maplevel = 11.5f;a.enname="baotoushi";
		areaList.put("包头市", a);
		
		a = new Area();
		a.areacode = "150300";a.areaname = "乌海市";a.centerlat = 39.662895; a.centerlon = 106.800104; a.maplevel = 11.5f;a.enname="wuhaishi";
		areaList.put("乌海市", a);
		
		a = new Area();
		a.areacode = "150400";a.areaname = "赤峰市";a.centerlat =42.262113; a.centerlon = 118.893508; a.maplevel = 11.5f;a.enname="chifengshi";
		areaList.put("赤峰市", a);
		
		a = new Area();
		a.areacode = "150500";a.areaname = "通辽市";a.centerlat =43.65798; a.centerlon = 122.24966; a.maplevel = 11.5f;a.enname="tongliaoshi";
		areaList.put("通辽市", a);
		
		a = new Area();
		a.areacode = "150600";a.areaname = "鄂尔多斯市";a.centerlat =39.615816; a.centerlon = 109.788305; a.maplevel = 11.5f;a.enname="eerduosishi";
		areaList.put("鄂尔多斯市", a);
		
		a = new Area();
		a.areacode = "150700";a.areaname = "呼伦贝尔市";a.centerlat =49.2192; a.centerlon =119.772083; a.maplevel = 11.5f;a.enname="hulunbeiershi";
		areaList.put("呼伦贝尔市", a);
	
		a = new Area();
		a.areacode = "150800";a.areaname = "巴彦淖尔市";a.centerlat =40.749359; a.centerlon = 107.393536; a.maplevel = 11.5f;a.enname="bayannaoershi";
		areaList.put("巴彦淖尔市", a);
		
		a = new Area();
		a.areacode = "150900";a.areaname = "乌兰察布市";a.centerlat =41.000748; a.centerlon = 113.138606; a.maplevel = 11.5f;a.enname="wulanchabushi";
		areaList.put("乌兰察布市", a);
		
		a = new Area();
		a.areacode = "152200";a.areaname = "兴安盟";a.centerlat =46.088463; a.centerlon =122.044652; a.maplevel = 11.5f;a.enname="xinganmeng";
		areaList.put("兴安盟", a);
		
		a = new Area();
		a.areacode = "152500";a.areaname = "锡林郭勒盟";a.centerlat =43.939008; a.centerlon =116.054104; a.maplevel = 11.5f;a.enname="xilinguolemeng";
		areaList.put("锡林郭勒盟", a);
		
		a = new Area();
		a.areacode = "152900";a.areaname = "阿拉善盟";a.centerlat = 38.857827; a.centerlon = 105.735664; a.maplevel = 11.5f;a.enname="alashanmeng";
		areaList.put("阿拉善盟", a);
		
	}
	
	
	private class LoadAllCityTask extends AsyncTask<String,Integer,List<City>>{
		@Override
		protected List<City> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mCityDao.getAllCity();
		}
		
		@Override
		protected void onPostExecute(List<City> result) {
			mCitys =result;
			if(listener != null) 
				listener.Loaded(result);
		}
	}
	
	private class LocatedAreaTask extends AsyncTask<String, Integer, City> {
		private boolean mLocated = false;
		private String mName = null;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected City doInBackground(String... arg0) {
			City result = null;
			mName = LocalHelper.getInstance(context).getCity();
			if (!mName.equals("未定位到城市")) {// 定位成功
				mLocated = true;
				result = mCityDao.queryAreaByAreaName(mName);
			}
			return result;
		}

		@Override
		protected void onPostExecute(City result) {
			if (mLocated && (result == null)) {
				// Toast.makeText(CitySetActivity.this, "地区"+mName+"不在服务范围，请从新选择地区", Toast.LENGTH_SHORT).show();
			}
			if (result != null) {
				//setLocationCityName(result.name);
			}

		}
	}

	public List<City> getmCitys() {
		return mCitys;
	}
	
	public interface OnPlaceManagerListener{
		void Loaded(List<City> citys);
	}

	public Map<String, Area> getAreaList() {
		return areaList;
	}

	public static Area getCurrentArea() {
		return PlaceManager.currentArea;
	}

	public static void setCurrentArea(Area currentArea) {
		PlaceManager.currentArea = currentArea;
	}}
