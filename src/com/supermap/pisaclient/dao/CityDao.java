/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CityDao.java - 2014-3-31 上午10:30:04
 */

package com.supermap.pisaclient.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.array;
import android.R.bool;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.TempratureRange;
import com.supermap.pisaclient.entity.WarningInfo;
import com.supermap.pisaclient.entity.WarningStandard;
import com.supermap.pisaclient.entity.Weather;
import com.umeng.common.net.l;

public class CityDao extends DatabaseHelper {
	
	private SQLiteDatabase db;
	
	private Cursor cursor;

	public CityDao(Context context) {
		super(context);
		db = this.getWritableDatabase();
	}
	
	/*****************************地区业务************************************************/
	/**********************************************************************************/
	public boolean addCity(City city) {
		ContentValues values = new ContentValues();
		values.put("areaid", city.areaid);
		values.put("name", city.name);
		values.put("areacode", city.areacode);
		values.put("parentid", city.prarentId);
		long res = db.insert("Cities", null, values);
		return true;
	}
	
	public boolean isHaveCity(City city){
		if ((city==null)||(city.areacode==null)) {
			return false;
		}
		String select="areacode=?";
		String[] selectargs={city.areacode};
		cursor = db.query("Cities", null, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			return true;
		}
		cursor.close();
		return false;
	}
	
	public boolean addAllCityTocitys(List<City> list) {
		if((list==null)||(list.size()==0)){
			return false;
		}
		for(City city: list){
			if(!isHaveCity(city)){
				addCity(city);
			}
		}
		return true;
	}
	
	public boolean addAllCity(List<City> list) {
		if((list==null)||(list.size()==0)){
			return false;
		}
		for(City city: list){
			ContentValues values = new ContentValues();
			values.put("areaid", city.areaid);
			values.put("name", city.name);
			values.put("parentid", city.prarentId);
			values.put("areacode", city.areacode);
			db.insert("AllCities", "name",values);
		}
		
		return true;
	}
	
	/**
	 * 获取数据库中已添加的所以城市
	 * @return
	 */
	public List<City> getAllCity(){
		List<City> citys = new ArrayList<City>();
		cursor=db.query("Cities", null, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast() && (cursor.getString(1) != null)){    
    		City info = new City();
    		info.name = cursor.getString(1);
    		info.name = cursor.getString(2);
    		info.areacode = cursor.getString(3);
    		info.prarentId = cursor.getInt(4);
    		citys.add(info);
    		cursor.moveToNext();
    	}
		cursor.close();
		return citys;
	}
	
	/**
	 * 根据 parent id 获取所有子地区  id:-1 获取所有城市数据
	 * @param parentid
	 * @return
	 */
	public List<City> getAllCity(int parentid){
		List<City> citys = new ArrayList<City>();
		if (parentid==-1) {
			cursor=db.query("AllCities", null, null, null, null, null, null);
		}
		else {
			cursor=db.query("AllCities", null, "parentid="+parentid, null, null, null, null);
		}
		while(cursor.moveToNext()){    
    		City info = new City();
    		info.areaid = cursor.getInt(1);
    		info.name = cursor.getString(2);
    		info.areacode = cursor.getString(3);
    		info.prarentId = cursor.getInt(4);
    		citys.add(info);
    	}
		cursor.close();
		
		
		
		
		return citys;
	}
	
	/**
	 * 获取区县的信息
	 * @return
	 */
	public List<CategoryItem> getCounty() {
		City city = new City();
		city.areaid = 1;
		city.name = "内蒙古自治区";
		city.prarentId = 0;
		city.areacode = 150000 +"";
		List<City> citys = getAllCity(2); //-----
		
		List<CategoryItem> categoryItems = new ArrayList<CategoryItem>();
		CategoryItem categoryItem=new CategoryItem();
		categoryItem.CategoryID=city.areaid;
		categoryItem.CategoryName=city.name;
		categoryItem.Code=city.areacode;
		categoryItem.ParentID=city.prarentId;
		categoryItems.add(categoryItem);
		int aaaa = citys.size();
		for(int i=0;i<citys.size();i++)  {
			categoryItem=new CategoryItem();
			categoryItem.CategoryID=citys.get(i).areaid;
			categoryItem.CategoryName=citys.get(i).name;
			categoryItem.Code=citys.get(i).areacode;
			categoryItem.ParentID=citys.get(i).prarentId;
			categoryItems.add(categoryItem);
		}
		
		return categoryItems;
	}
	
	
	public String queryCityName(String areacode){
		String name = null;
		if (areacode==null) {
			return null;
		}
		String[] columns = {"name"};
		String select="areacode=?";
		String[] selectargs={areacode};
		cursor = db.query("AllCities", columns, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			name = cursor.getString(0);
			cursor.close();
			return name;
		}
		cursor.close();
		return name;
	}

	public String queryAreacode(String cityName) {
		if (cityName==null) {
			return null;
		}
		String[] columns = {"areacode"};
		String select="name=?";
		String[] selectargs={cityName};
		cursor = db.query("Cities", columns, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			String areacode = cursor.getString(0);
			cursor.close();
			return areacode;
		}
		cursor = db.query("AllCities", columns, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			String areacode = cursor.getString(0);
			cursor.close();
			return areacode;
		}
		
		return null;
	}
	
	public City queryAreaByAreaName(String cityName) {
		if (cityName==null) {
			return null;
		}
		City city = null;
		String select="name=?";
		String[] selectargs={cityName};
		cursor = db.query("Cities", null, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			city = new City();
			city.areaid = cursor.getInt(1);
			city.name = cursor.getString(2);
			city.areacode = cursor.getString(3);
			city.prarentId = cursor.getInt(4);
			return city;
		}
		cursor = db.query("AllCities", null, select,selectargs, null, null, null, null);
		if(cursor.moveToNext()){
			city = new City();
			city.areaid = cursor.getInt(1);
			city.name = cursor.getString(2);
			city.areacode = cursor.getString(3);
			city.prarentId = cursor.getInt(4);
			return city;
		}
		cursor.close();
		return city;
	}
	
	
	
	public boolean deleteCity(String name) {
		String where="name=?";
		String[] whereValue={name};
		db.delete("Cities", where, whereValue);
		return true;
	}
	
	public boolean deleteAll() {
		long res = db.delete("Cities", null, null);
		return true;
	}
	
	
	/*****************************地区业务************************************************/
	/**********************************************************************************/	
	
	
	
	/*****************************天气业务************************************************/
	/**********************************************************************************/
	
	public boolean addWeather(Weather weather) {
		
		if(weather ==null){
			return false;
		}
		
		ContentValues values = new ContentValues();
		values.put("city", weather.city);
		values.put("ObservTimes",weather.ObservTimes);
		values.put("WeatherState", weather.WeatherState);
		values.put("Temperature", weather.Temperature);
		values.put("MaxTemperature", weather.MaxTemperature);
		values.put("MinTemperature", weather.MinTemperature);
		values.put("WindDirection", weather.WindDirection);
		values.put("WindSpeed", weather.WindSpeed);
		values.put("Humidity", weather.Humidity);
		values.put("Pressure", weather.Pressure);
		db.insert("Weathers", "city",values);
		return true;
	}
	
	
	public void updateWeather(Weather weather) {
		if(weather==null){
			return;
		}
		String where="city=?";
		String[] whereValue={weather.city};
		ContentValues values=new ContentValues(); 
		values.put("ObservTimes",weather.ObservTimes);
		values.put("WeatherState", weather.WeatherState);
		values.put("Temperature", weather.Temperature);
		values.put("MaxTemperature", weather.MaxTemperature);
		values.put("MinTemperature", weather.MinTemperature);
		values.put("WindDirection", weather.WindDirection);
		values.put("WindSpeed", weather.WindSpeed);
		values.put("Humidity", weather.Humidity);
		db.update("Weathers", values, where, whereValue);
	}
	
	public Weather queryWeather(String city){
		Weather weather = null;
		String select="city=?";
		String[] selectargs={city};
		cursor=db.query("Weathers", null, select, selectargs, null, null,null);
		if ((cursor!=null)&&(cursor.moveToFirst())&&(cursor.getColumnCount()!=0)) {
			weather = new Weather();
			try {
				weather.city = cursor.getString(1);
				weather.ObservTimes = cursor.getString(2);
				weather.WeatherState = cursor.getInt(3);
				weather.Temperature = cursor.getInt(4);
				weather.MaxTemperature = cursor.getInt(5);
				weather.MinTemperature = cursor.getInt(6);
				weather.WindDirection = cursor.getString(7);
				weather.WindSpeed =cursor.getString(8);
				weather.Humidity = cursor.getInt(9);
				weather.Pressure = cursor.getString(10);
			} catch (Exception e) {
			}
			finally{
				cursor.close();
			}
		}
		return weather ;
	}
	
	public List<Weather> getAllWeathers(){
		List<Weather> weathers = new ArrayList<Weather>();
		cursor=db.query("Weathers", null, null, null, null, null,  " id asc");
		cursor.moveToFirst();
		if (cursor.getColumnCount()!=0) {
			while(!cursor.isAfterLast() && (cursor.getString(1) != null)){    
	    		Weather info = new Weather();
	    		info.city= cursor.getString(1);
	    		info.ObservTimes = cursor.getString(2);
	    		info.WeatherState = cursor.getInt(3);
	    		info.Temperature = cursor.getInt(4);
	    		info.MaxTemperature = cursor.getInt(5);
	    		info.MinTemperature = cursor.getInt(6);
	    		info.WindDirection = cursor.getString(7);
	    		info.WindSpeed = cursor.getString(8);
	    		info.Humidity = cursor.getInt(9);
	    		info.Pressure = cursor.getString(10);
	    		weathers.add(info);
	    		cursor.moveToNext();
	    	}
		}
		cursor.close();
		return weathers;
	}
	
	
	
	public boolean deleteWeather(String name)
	{
		String where="city=?";
		String[] whereValue={name};
		db.delete("Weathers", where, whereValue);
		return true;
	}
	/*****************************天气业务************************************************/
	/**********************************************************************************/
	
	
	
	/*****************************一周预报温度************************************************/
	/*************************************************************************************/
	public boolean addTempraRange(TempratureRange tempraRange) {
		ContentValues values = new ContentValues();
		values.put("city", tempraRange.city);
		values.put("time", tempraRange.time);
		values.put("max", tempraRange.max);
		values.put("min", tempraRange.min);
		db.insert("Temperatures", "city",values);
		return true;
	}
	
	public boolean addTempraRange(List<TempratureRange> list) {
		if((list==null)||(list.size()==0)){
			return false;
		}
		for(TempratureRange tempratureRange:list){
			addTempraRange(tempratureRange);
		}
		return true;
	}
	

	public List<TempratureRange> getAllTemperatures(String city){
		String[] columns = {"id","city","time","max","min"};
		String select="city=?";
		String[] selectargs={city};
		List<TempratureRange> list = new ArrayList<TempratureRange>();
		cursor=db.query("Temperatures", columns, select, selectargs, null, null,  " time asc");
		if (cursor==null) {
//			cursor.close();
			return null;
		}
		if ((cursor!=null)&&(cursor.moveToFirst())) {
			try {
				while(!cursor.isAfterLast() && (cursor.getString(1) != null)){    
		    			TempratureRange tempratureRange = new TempratureRange();
			    		tempratureRange.city = cursor.getString(1);
			    		tempratureRange.time = cursor.getString(2);
			    		tempratureRange.max = cursor.getInt(3);
			    		tempratureRange.min = cursor.getInt(4);
			    		list.add(tempratureRange);
			    		cursor.moveToNext();
		    	}
			} catch (Exception e) {
				return null;
			}finally{
				cursor.close();
			}
		}
		return list;
	}
	
	public int[][] getWeekTemperatures(String city){
		 int[][] tem = new int[7][2];
		String[] columns = {"max","min","time","city"};
		String select="city=?";
		String[] selectargs={city};
		cursor=db.query("Temperatures", columns, select, selectargs, null, null,  " time asc");
		cursor.moveToFirst();
		int i = 0;
		while(!cursor.isAfterLast() && (cursor.getString(1) != null)){ 
			if(i==7){
				break;
			}
			tem[i][0] = cursor.getInt(1);
			tem[i][1] = cursor.getInt(0);
			i++;
    		cursor.moveToNext();
    	}
		cursor.close();
		return tem;
	}
	
	public boolean deleteTemperature(String name)
	{
		String where="city=?";
		String[] whereValue={name};
		db.delete("Temperatures", where, whereValue);
		return true;
	}
	/*****************************一周预报温度************************************************/
	/*************************************************************************************/
	
	
	/*****************************预警业务**************************************************/
	/*************************************************************************************/
	public boolean addWarning(WarningInfo warningInfo) {
		
		if (!isHaveTheWarning(warningInfo)) {
			ContentValues values = new ContentValues();
			values.put("type", warningInfo.type);
			values.put("level", warningInfo.level);
			values.put("city", warningInfo.city);
			values.put("time", warningInfo.time);
			values.put("content", warningInfo.content);
			values.put("title", warningInfo.title);
			values.put("ischeck", 0);
			db.insert("Warnings", "city",values);
			return true;
		}
		return true;
	}
	
	public boolean isHaveTheWarning(WarningInfo warningInfo){
		if (warningInfo==null) {
			return false;
		}
		String select="time=?";
		String[] selectargs={warningInfo.time};
		cursor=db.query("Warnings", null, select, selectargs, null, null,  " time desc");
		while (cursor.moveToNext()) {
			if (cursor.getString(3).equals(warningInfo.city)) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}
	
	public boolean addWarning(List<WarningInfo> list) {
		if (list==null) {
			return false;
		}
		for(WarningInfo warningInfo:list){
			addWarning(warningInfo);
		}
		return true;
	}
	
	public WarningStandard getWarningStandard(String type,String level){
		WarningStandard warningStandard = new WarningStandard();
		String[] selectargs={type,level};
		Cursor cursor = db.rawQuery("select * from warning_standard " +"where type=? and level=?",selectargs);
		if(cursor.moveToNext()){
			warningStandard.type = cursor.getString(1);
			warningStandard.tips = cursor.getString(2);
			warningStandard.standard = cursor.getString(3);
			warningStandard.tips = cursor.getString(4);
		}
		cursor.close();
		return warningStandard;
	}
	public Cursor getWarningsCursor(String city){
		
		String select="city=?";
		String[] selectargs={city};
		cursor=db.query("Warnings", null, select, selectargs, null, null,  " time desc");
		return  cursor;
	}
	
	public boolean isHaveWarnings(String city){
		
		String select="city=?";
		String[] selectargs={city};
		cursor=db.query("Warnings", null, select, selectargs, null, null,  " time desc");
		return  cursor.moveToNext();
	}
	
	
	public List<WarningInfo> queryWarnings(String city){
		List<WarningInfo> list = new ArrayList<WarningInfo>();
		WarningInfo warningInfo = null;
		String select="city=?";
		String[] selectargs={city};
		cursor=db.query("Warnings", null, select, selectargs, null, null,"time desc");
		while (cursor.moveToNext()) {
			warningInfo = new WarningInfo();
			warningInfo.type = cursor.getString(1);
			warningInfo.level =cursor.getString(2);
			warningInfo.city = cursor.getString(3);
			warningInfo.time = cursor.getString(4);
			warningInfo.title = cursor.getString(5);
			warningInfo.content = cursor.getString(6);
			list.add(warningInfo);
		}
		cursor.close();
		return list ;
	}
	
	public List<WarningInfo> queryOtherWarnings(String city){
		List<WarningInfo> list = new ArrayList<WarningInfo>();
		WarningInfo warningInfo = null;
		String select="city!=?";
		String[] selectargs={city};
		cursor=db.query("Warnings", null, select, selectargs, null, null,"time desc");
		while (cursor.moveToNext()) {
			warningInfo = new WarningInfo();
			warningInfo.type = cursor.getString(1);
			warningInfo.level =cursor.getString(2);
			warningInfo.city = cursor.getString(3);
			warningInfo.time = cursor.getString(4);
			warningInfo.title = cursor.getString(5);
			warningInfo.content = cursor.getString(6);
			list.add(warningInfo);
		}
		cursor.close();
		return list ;
	}
	public int getUnCheckedWarningNum(){
		int num  =0;
		cursor=db.query("Warnings", null, "ischeck="+0, null, null, null, null);
		if (cursor!=null) {
			while(cursor.moveToNext()){
				num++;
			}
		}
		cursor.close();
		return num;
	}
	public void setChecked(String time){
		String where="time<?";
		String[] whereValue={time};
		ContentValues values = new ContentValues();
		values.put("ischeck", 1);
		db.execSQL("update Warnings set ischeck = 1 where time <'"+time+"';");
	}
	
	public void initWaringDB(){
		
		String where="time<?";
		String[] whereValue={DateUtiles.getLastDayTime()};
		db.delete("Warnings", where, whereValue);
	}
	
	public Cursor getWarningsCursor(int index, int size){
		String sql = "select * from Warnings limit ?,?"; 
		   String[] selectionArgs = {String.valueOf(index),String.valueOf(size)}; 
		   cursor = db.rawQuery(sql, selectionArgs); 
			return  cursor;
		}
	
	public boolean deleteWarning(String name)
	{
		String where="city=?";
		String[] whereValue={name};
		db.delete("Warnings", where, whereValue);
		return true;
	}
	/*****************************预警业务**************************************************/
	/*************************************************************************************/
	
	/*****************************农情业务**************************************************/
	/*************************************************************************************/
	public boolean addAgrInfo(AgrInfo agrInfo) {
		if (agrInfo==null) {
			return true;
		}
		if (isHaveAgriInfo(agrInfo.agrInfoId)) {
			return true;
		}
		ContentValues values = new ContentValues();
		values.put("agrInfoId", agrInfo.agrInfoId);
		values.put("userId", agrInfo.userId);
		values.put("userName", agrInfo.userName);
		values.put("userHeaderFile", agrInfo.userHeaderFile);
		values.put("uploadTime", agrInfo.uploadTime);
		values.put("areacode", agrInfo.areacode);
		values.put("descript", agrInfo.descript);
		values.put("type", agrInfo.croptype);
		values.put("breed", agrInfo.breed);
		values.put("growperiod", agrInfo.growperiod);
		db.insert("Agrinfo", "agrInfoId",values);
		
		return true;
	}
	
	
	public int getAgrInfoNums(){
		cursor=db.query("Agrinfo", null, null, null, null, null, null);
		int all = cursor.getCount();
		cursor.close();
		return all;
	}
	public boolean isHaveAgriInfo(int agrInfoId){
		
		String select="agrInfoId=?";
		String[] selectargs={agrInfoId+""};
		cursor=db.query("Agrinfo", null, select, selectargs, null, null,  " uploadTime desc");
		return  cursor.moveToNext();
	}
	
		/**
	      * 分页查询
	      * @param firstResult 从第几条数据开始查询。
	      * @param maxResult   每页显示多少条记录。
	      * @return   当前页的记录
	      */
	     public List<AgrInfo> getAgrInfos(int firstResult, int maxResult) {
	    	 List<AgrInfo> list = new ArrayList<AgrInfo>();
	 		AgrInfo agrInfo = null;
	 		String sql = "select * from Agrinfo order  by uploadTime desc limit ?,?";
	 		cursor = db.rawQuery(sql, new String[]{String.valueOf(firstResult), String.valueOf(maxResult)});
	 		while (cursor.moveToNext()) {
	 			agrInfo = new AgrInfo();
	 			agrInfo.agrInfoId = cursor.getInt(1);
	 			agrInfo.userId = cursor.getInt(2);
	 			agrInfo.userName = cursor.getString(3);
	 			agrInfo.userHeaderFile = cursor.getString(4);
	 			agrInfo.uploadTime = cursor.getString(5);
	 			agrInfo.areacode = cursor.getString(6);
	 			agrInfo.descript = cursor.getString(7);
	 			agrInfo.croptype = cursor.getString(8);
	 			agrInfo.breed = cursor.getString(9);
	 			agrInfo.growperiod = cursor.getString(10);
	 			
	 			list.add(agrInfo);
	 		}
	 		cursor.close();
	 		return list ;
	     }
	     
	     /**
	      * 分页查询
	      * @param firstResult 从第几条数据开始查询。
	      * @param maxResult   每页显示多少条记录。
	      * @return   当前页的记录
	      */
	     public List<AgrInfo> getAgrInfos(int firstResult, int maxResult, String areacode,int userid) {
	    	 List<AgrInfo> list = new ArrayList<AgrInfo>();
	    	 if (areacode==null) {
				return list;
	    	 }
	    	String value; 
    		 if (!"null".equals(areacode.trim()) && !"".equals(areacode.trim())) {
 				String[] codes = areacode.split(",");
 				StringBuffer sb = new StringBuffer();
 				for (String code : codes) {
 					sb.append("'"+code.substring(0,6) + "',");
 				}
 				String res = sb.toString();
 				value = res.substring(0, res.length() - 1);
 			}
    		else {
				return list;
			}
				
	 		AgrInfo agrInfo = null;
	 		String sql ;
	 		if (userid==-1) {
	 			sql= "select * from Agrinfo where substr(areacode,1,6) in ("+value+") order  by uploadTime desc limit ?,?";
	 			cursor = db.rawQuery(sql, new String[]{String.valueOf(firstResult), String.valueOf(maxResult)});
			}
	 		else{
	 			sql ="select * from (select * from Agrinfo where substr(areacode,1,6) in ("+value+") " +
	 					"union all select * from Agrinfo where userId = ? ) as u " +
	 					"group by agrInfoId " +
	 					"order  by uploadTime desc " +
	 					"limit ?,?";
	 			cursor = db.rawQuery(sql, new String[]{String.valueOf(userid),String.valueOf(firstResult), String.valueOf(maxResult)});
	 		}
	 		
	 		while (cursor.moveToNext()) {
	 			agrInfo = new AgrInfo();
	 			agrInfo.agrInfoId = cursor.getInt(1);
	 			agrInfo.userId = cursor.getInt(2);
	 			agrInfo.userName = cursor.getString(3);
	 			agrInfo.userHeaderFile = cursor.getString(4);
	 			agrInfo.uploadTime = cursor.getString(5);
	 			agrInfo.areacode = cursor.getString(6);
	 			agrInfo.descript = cursor.getString(7);
	 			agrInfo.croptype = cursor.getString(8);
	 			agrInfo.breed = cursor.getString(9);
	 			agrInfo.growperiod = cursor.getString(10);
	 			
	 			list.add(agrInfo);
	 		}
	 		cursor.close();
	 		return list ;
	     }
	     
	     public int getAgrInfoNums(String areacode,int userid){
		    	 if (areacode==null) {
					return 0;
		    	 }
		    	String value; 
	    		 if (!"null".equals(areacode.trim()) && !"".equals(areacode.trim())) {
	 				String[] codes = areacode.split(",");
	 				StringBuffer sb = new StringBuffer();
	 				for (String code : codes) {
	 					sb.append("'"+code.substring(0,6) + "',");
	 				}
	 				String res = sb.toString();
	 				value = res.substring(0, res.length() - 1);
	 			}
	    		else {
					return 0;
				}
					
		 		AgrInfo agrInfo = null;
		 		String sql ;
		 		if (userid==-1) {
		 			sql= "select * from Agrinfo where substr(areacode,1,6) in ("+value+") order  by uploadTime desc ";
		 			cursor = db.rawQuery(sql, null);
				}
		 		else{
		 			sql ="select * from (select * from Agrinfo where substr(areacode,1,6) in ("+value+") " +
		 					"union all select * from Agrinfo where userId = ? ) as u " +
		 					"group by agrInfoId " +
		 					"order  by uploadTime desc ";
		 			cursor = db.rawQuery(sql, new String[]{String.valueOf(userid)});
		 		}
	 		int all = cursor.getCount();
	 		cursor.close();
	 		return all;
	 	}
	     
	     public String getLastFreshTime(String areacode,int userid) {
	    	 String startTime = "2012-01-01 00:00:00";
	    	 if (areacode==null) {
					return startTime;
		    	 }
		    	String value; 
	    		 if (!"null".equals(areacode.trim()) && !"".equals(areacode.trim())) {
	 				String[] codes = areacode.split(",");
	 				StringBuffer sb = new StringBuffer();
	 				for (String code : codes) {
	 					sb.append("'"+code.substring(0,6) + "',");
	 				}
	 				String res = sb.toString();
	 				value = res.substring(0, res.length() - 1);
	 			}
	    		else {
					return startTime;
				}
					
	 	 		String sql ;
	 	 		if (userid==-1) {
	 	 			sql= "select * from Agrinfo where substr(areacode,1,6) in ("+value+") order  by uploadTime desc limit ?,?";
	 	 			cursor = db.rawQuery(sql, new String[]{String.valueOf(0), String.valueOf(1)});
	 			}
	 	 		else{
	 	 			sql ="select * from (select * from Agrinfo where substr(areacode,1,6) in ("+value+") " +
	 	 					"union all select * from Agrinfo where userId = ? ) as u " +
	 	 					"group by agrInfoId " +
	 	 					"order  by uploadTime desc " +
	 	 					"limit ?,?";
	 	 			cursor = db.rawQuery(sql, new String[]{String.valueOf(userid),String.valueOf(0), String.valueOf(1)});
	 	 		}
		    	String time = null;
		 		if(cursor.moveToNext()) {
		 			AgrInfo agrInfo = new AgrInfo();
		 			time = cursor.getString(5);
		 		}
		 		else {
					time ="2012-01-01 00:00:00";
				}
		 		cursor.close();
		 		return time ;
		     }
	     
	     public String getLastFreshTime() {
	    	String time = null;
	 		String sql = "select * from Agrinfo  order  by uploadTime desc limit ?,?";
	 		cursor = db.rawQuery(sql, new String[]{String.valueOf(0), String.valueOf(1)});
	 		if(cursor.moveToNext()) {
	 			AgrInfo agrInfo = new AgrInfo();
	 			time = cursor.getString(5);
	 		}
	 		else {
				time ="2012-01-01 00:00:00";
			}
	 		cursor.close();
	 		return time ;
	     }
	
	public void addAgrInfoList(List<AgrInfo> list){
		if ((list==null)||(list.size()==0)) {
			return ;
		}
		for(AgrInfo agrInfo :list){
			addAgrInfo(agrInfo);
		}
		return ;
	}
	
	/*****************************农情业务**************************************************/
	/*************************************************************************************/
	
	/*****************************农情评论业务**************************************************/
	/*************************************************************************************/
	public boolean addAgrinfoComment(AgrinfoComment agrinfoComment) {
		if (isHavingTheComment(agrinfoComment)) {
			return true;
		}
		ContentValues values = new ContentValues();
		values.put("agrInfoId", agrinfoComment.agrInfoId);
		values.put("commentId", agrinfoComment.commentId);
		values.put("parentId", agrinfoComment.parentId);
		values.put("userId", agrinfoComment.userId);
		values.put("comment", agrinfoComment.comment);
		values.put("commentTime", agrinfoComment.commentTime);
		values.put("username", agrinfoComment.username);
		values.put("parentusername", agrinfoComment.parentusername);
		db.insert("AgrinfoComments", "commentId",values);
		return true;
	}
	
	public boolean isHavingTheComment(AgrinfoComment agrinfoComment){
			String select="commentId=?";
			String[] selectargs={agrinfoComment.commentId+""};
			cursor=db.query("AgrinfoComments", null, select, selectargs, null, null, null);
			if (cursor.moveToNext()) {
				cursor.close();
				return true;
			}
			cursor.close();
		    return false;
	}
	
	public void addAgrinfoCommentList(List<AgrinfoComment> list){
		if ((list==null)||(list.size()==0)) {
			return ;
		}
		for(AgrinfoComment agrinfoComment :list){
			addAgrinfoComment(agrinfoComment);
		}
		return ;
	}
	
	
	public  List<AgrinfoComment> getAgrinfoComments(int agrinfoId){
		   List<AgrinfoComment> list = new ArrayList<AgrinfoComment>();
		   AgrinfoComment agrinfoComment = null;
//	 	   String[] selectargs={agrinfoId+""};
//		   Cursor cursor = db.rawQuery("select * from AgrinfoComments where agrInfoId=?",selectargs);
		   
		   String select="agrInfoId=?";
		   String[] selectargs={agrinfoId+""};
		   cursor=db.query("AgrinfoComments", null, select, selectargs, null, null,"commentTime asc");
	 	   while (cursor.moveToNext()) {
	 			agrinfoComment = new AgrinfoComment();
	 			agrinfoComment.commentId = cursor.getInt(1);
	 			agrinfoComment.agrInfoId = cursor.getInt(2);
	 			agrinfoComment.comment = cursor.getString(3);
	 			agrinfoComment.parentId = cursor.getInt(4);
	 			agrinfoComment.userId =  cursor.getInt(5);
	 			agrinfoComment.commentTime = cursor.getString(6);
	 			agrinfoComment.username = cursor.getString(7);
	 			agrinfoComment.parentusername = cursor.getString(8);
	 			list.add(agrinfoComment);
//	 			agrinfoComment.commentId = cursor
	 	   }
	 	   cursor.close();
	 	   return list ;
	}
	
	public String getLocalCommentMaxTime(int agrinfoId){
		   String time = null;
		   String[] columns = {"commentTime"};
		   String select="agrInfoId=?";
		   String[] selectargs={agrinfoId+""};
		   cursor=db.query("AgrinfoComments", columns, select, selectargs, null, null,"commentTime desc");
		   if (cursor.moveToNext()) {
			   time = cursor.getString(0);
		   }
		   else{
		    	time ="2012-01-01 00:00:00";
		    }
		   return time;
	}
	
	public String getLocalCommentMaxTime(){
		   String time = null;
		   String[] columns = {"commentTime"};
		   cursor=db.query("AgrinfoComments", columns, null, null, null, null,"commentTime desc");
		   if (cursor.moveToNext()) {
			   time = cursor.getString(0);
			}
		    else{
		    	time ="2012-01-01 00:00:00";
		    }
		   if (time==null) {
			   time ="2012-01-01 00:00:00";
		   }
		   return time;
		}
	/*****************************农情评论业务**************************************************/
	/***************************************************************************************/
	
	/*****************************农情评论赞业务**************************************************/
	/***************************************************************************************/
	public boolean addAgrpraise(AgrPraise agrPraise) {
		ContentValues values = new ContentValues();
		values.put("praiseId", agrPraise.praiseId);
		values.put("agrInfoId", agrPraise.agrInfoId);
		values.put("isPraise", agrPraise.isPraise);
		values.put("userId", agrPraise.userId);
		values.put("praiseTime", agrPraise.praiseTime);
		values.put("userName", agrPraise.userName);
		db.insert("Agrpraise", "agrInfoId",values);
		return true;
	}
	
	public boolean isHavingThePraise(AgrPraise agrPraise){
		String select="praiseId=?";
		String[] selectargs={agrPraise.praiseId+""};
		cursor=db.query("Agrpraise", null, select, selectargs, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		cursor.close();
	    return false;
}
	
	public void addAgrpaiseList(List<AgrPraise> list){
		if ((list==null)||(list.size()==0)) {
			return ;
		}
		for(AgrPraise agrPraise :list){
			if (!isHavingThePraise(agrPraise)) {
				addAgrpraise(agrPraise);
			}
		}
		return ;
	}
	
	
	public String getLocalPrasieMaxTime(){
		   String time = null;
		   String[] columns = {"praiseTime"};
		   cursor=db.query("Agrpraise", columns, null, null, null, null,"praiseTime desc");
		   if (cursor.moveToNext()) {
			time = cursor.getString(0);
		   }else {
			time ="2012-01-01 00:00:00";
		   }
		   return time;
	}
	public List<AgrPraise> getAgrPraises(int agrinfoId){
		  List<AgrPraise> list = new ArrayList<AgrPraise>();
		  AgrPraise agrPraise = null;
		   String select="agrInfoId=?";
		   String[] selectargs={agrinfoId+""};
		   cursor=db.query("Agrpraise", null, select, selectargs, null, null,null);
	 	   while (cursor.moveToNext()) {
	 			agrPraise = new AgrPraise();
	 			agrPraise.praiseId = cursor.getInt(1);
	 			agrPraise.agrInfoId = cursor.getInt(2);
	 			agrPraise.isPraise = cursor.getInt(3);
	 			agrPraise.userId = cursor.getInt(4);
	 			agrPraise.praiseTime = cursor.getString(5);
	 			agrPraise.userName = cursor.getString(6);
	 			list.add(agrPraise);
	 	   }
	 	   cursor.close();
	 	   return list;
	 	   
	}
	
	/*****************************农情评论赞业务**************************************************/
	/***************************************************************************************/
	

	/*****************************农情图片业务**************************************************/
	/***************************************************************************************/
	public boolean addAgrImg(AgrImgs agrImgs) {
		ContentValues values = new ContentValues();
		values.put("ImgID", agrImgs.id);
		values.put("agrInfoId", agrImgs.agrInfoId);
		values.put("URLcontent", agrImgs.URLcontent);
		values.put("descript", agrImgs.descript);
		db.insert("AgrImgs", "agrInfoId",values);
		return true;
	}
	
	public boolean isHaveAgrImg(AgrImgs agrImgs){
		
		String select="ImgID=?";
		String[] selectargs={String.valueOf(agrImgs.id)};
		cursor=db.query("AgrImgs", null, select, selectargs, null, null,null);
		return  cursor.moveToNext();
	}
	
	
	public void addAgrImgList(List<AgrImgs> list){
		if ((list==null)||(list.size()==0)) {
			return ;
		}
		for(AgrImgs agrImgs :list){
			if (!isHaveAgrImg(agrImgs)) {
				addAgrImg(agrImgs);
			}
		}
	}
	
	public List<AgrImgs> getAgrImgs(int agrinfoId){
		  List<AgrImgs> list = new ArrayList<AgrImgs>();
		  AgrImgs agrImgs = null;
		   String select="agrInfoId=?";
		   String[] selectargs={agrinfoId+""};
		   cursor=db.query("AgrImgs", null, select, selectargs, null, null,null);
	 	   while (cursor.moveToNext()) {
	 		  agrImgs = new AgrImgs();
	 		  agrImgs.id = cursor.getInt(1);
	 		  agrImgs.agrInfoId = cursor.getInt(2);
	 		  agrImgs.URLcontent = cursor.getString(3);
	 		  agrImgs.descript = cursor.getString(4);
	 		  list.add(agrImgs);
	 	   }
	 	   cursor.close();
	 	   return list;
	}
	/*****************************农情图片业务**************************************************/
	/***************************************************************************************/
	
	
	/*****************************消息业务**************************************************/
	/***************************************************************************************/
	public boolean addMsg(ClientMsg clientMsg) {
		ContentValues values = new ContentValues();
		values.put("msgid", clientMsg.msgid);
		values.put("fromid", clientMsg.fromid);
		values.put("toid", clientMsg.toid);
		values.put("msgtypeid", clientMsg.msgtypeid);
		values.put("mainid", clientMsg.mainid);
		values.put("msgsendtypeid", clientMsg.msgsendtypeid);
		values.put("sendmainid", clientMsg.sendmainid);
		values.put("content", clientMsg.content);
		values.put("msgTime", clientMsg.msgTime);
		values.put("areacode", clientMsg.areacode);
		values.put("croptype", clientMsg.croptype);
		values.put("ischeck", 0);//默认是未查看
		int result = (int) db.insert("t_message", "msgid",values);
		if (result==-1) {
			return false;
		}
		return true;
	}
	
	public boolean updateMsgChecked(ClientMsg clientMsg) {
		
		String where="msgid=?";
		String[] whereValue={clientMsg.msgid+""};
		ContentValues values=new ContentValues(); 
		values.put("ischeck", 1);
		db.update("t_message", values, where, whereValue);
		return true;
	}
	
	public boolean updateMsgCheckedByID(int msgid) {
		
		String where="msgid=?";
		String[] whereValue={msgid+""};
		ContentValues values=new ContentValues(); 
		values.put("ischeck", 1);
		int temp = db.update("t_message", values, where, whereValue);
		return true;
	}
	
	/**
	 * 未完成，查询数据库这个消息是否查看
	 * @param msgid
	 * @return
	 */
	@Deprecated
	public boolean isCheckMsg(int msgid) {
		
		String where="msgid=?";
		String[] whereValue={msgid+""};
		ContentValues values=new ContentValues(); 
		values.put("ischeck", 1);
		db.update("t_message", values, where, whereValue);
		return true;
	}
	
	
	public boolean isHavingTheMsg(ClientMsg clientMsg){
		if (clientMsg==null) {
			return true;
		}
		String select="msgid=?";
		String[] selectargs={clientMsg.msgid+""};
		cursor=db.query("t_message", null, select, selectargs, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		cursor.close();
	    return false;
}
	
	/**
     * 分页查询
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @return   当前页的记录
     */
    public List<ClientMsg> getMsgs(int userid ,int firstResult, int maxResult) {
   	 List<ClientMsg> list = new ArrayList<ClientMsg>();
		ClientMsg clientMsg = null;
		String sql = "select * from t_message where toid = ? order  by msgTime desc limit ?,?";
		cursor = db.rawQuery(sql, new String[]{String.valueOf(userid),String.valueOf(firstResult), String.valueOf(maxResult)});
		while (cursor.moveToNext()) {
			clientMsg = new ClientMsg();
			clientMsg.msgid = cursor.getInt(1);
			clientMsg.fromid= cursor.getInt(2);
			clientMsg.toid = cursor.getInt(3);
			clientMsg.msgtypeid = cursor.getInt(4);
			clientMsg.mainid = cursor.getInt(5);
			clientMsg.msgsendtypeid = cursor.getInt(6);
			clientMsg.sendmainid = cursor.getInt(7);
			clientMsg.content = cursor.getString(8);
			clientMsg.msgTime =cursor.getString(9);
			clientMsg.ischeck =cursor.getInt(10);
			clientMsg.areacode =cursor.getString(11);
			clientMsg.croptype =cursor.getInt(12);
			list.add(clientMsg);
		}
		cursor.close();
		return list ;
    }
    
    public ClientMsg getMsgs(int msgid) {
      	
   			ClientMsg clientMsg = null;
   			String select="msgid=?";
   			String[] selectargs={""+msgid};
   			cursor=db.query("t_message", null, select, selectargs, null, null,null);
   			if (cursor.moveToNext()) {
   				clientMsg = new ClientMsg();
   				clientMsg.msgid = cursor.getInt(1);
   				clientMsg.fromid= cursor.getInt(2);
   				clientMsg.toid = cursor.getInt(3);
   				clientMsg.msgtypeid = cursor.getInt(4);
   				clientMsg.mainid = cursor.getInt(5);
   				clientMsg.msgsendtypeid = cursor.getInt(6);
   				clientMsg.sendmainid = cursor.getInt(7);
   				clientMsg.content = cursor.getString(8);
   				clientMsg.msgTime =cursor.getString(9);
   				clientMsg.ischeck =cursor.getInt(10);
   				clientMsg.areacode =cursor.getString(11);
   				clientMsg.croptype =cursor.getInt(12);
   			}
   			cursor.close();
   			return clientMsg ;
       }
	
    
    public int getUnCheckedMsgNum(int userid){
		int num  =0;
		String select="toid=? and ischeck=?";
		String[] selectargs={String.valueOf(userid),String.valueOf(0)};
		cursor=db.query("t_message", null, select, selectargs, null, null,null);
		if (cursor!=null) {
			while(cursor.moveToNext()){
				num++;
			}
		}
		cursor.close();
		return num;
	}
    
    public int getMsgNums(){
		cursor=db.query("t_message", null, null, null, null, null, null);
		int all = cursor.getCount();
		cursor.close();
		return all;
	}
	
	/*****************************消息业务**************************************************/
	/***************************************************************************************/
	public boolean clearCityTab(){
		db.execSQL("drop table Cities");
		db.execSQL(Constant.TAB_CITY);
		return true;
	}
	
	public void close(){
		cursor.close();
		db.close();
	}

}
