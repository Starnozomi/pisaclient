/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Constant.java - 2014-3-25 上午8:46:37
 */

package com.supermap.pisaclient.common;

import android.app.Activity;
import android.os.Environment;
import android.view.Display;

import com.supermap.android.maps.Point2D;
import com.supermap.pisaclient.entity.User;

public class Constant {

	public static final String DB_PATH = "/data/data/com.supermap.pisaclient/databases/";
	public static final String DB_NAME = "pisaDB";
	// 天气相关表
	public static final String TAB_CITY = "create table if not exists Cities(id integer primary key autoincrement, areaid integer, name text,areacode text, parentid integer)";
	public static final String TAB_ALLCITY = "create table if not exists AllCities(id integer primary key autoincrement,areaid integer, name text,areacode text, parentid integer)";
	public static final String TAB_WARNING = "create table if not exists Warnings(_id integer primary key autoincrement, type text,level text, city text, time text,title text,content text,ischeck integer)";
	public static final String TAB_WARNING_STANDARD = "create table if not exists WarningStandard(id integer primary key autoincrement, type integer,content text,tips text)";
	public static final String TAB_TEMPERATURE = "create table if not exists Temperatures(id integer primary key autoincrement, city text,time text, max integer,min integer)";
	public static final String TAB_WEATHER = "create table if not exists Weathers(id integer primary key autoincrement, city text, ObservTimes text,"
			+ "WeatherState integer,Temperature integer,MaxTemperature integer,MinTemperature integer,WindDirection text,"
			+ "WindSpeed integer,Humidity integer,Pressure text )";
	// 农情相关表
	public static final String TAB_AGRINFO = "create table if not exists Agrinfo(id integer primary key autoincrement, agrInfoId integer, userId integer,userName text,userHeaderFile text,uploadTime text, areacode text,descript text,type text,breed text,growperiod text)";
	public static final String TAB_AGRINFOCOMMENTS = "create table if not exists AgrinfoComments(id integer primary key autoincrement, commentId integer, agrInfoId integer,comment text,parentId integer,userId integer,commentTime text,username text,parentusername text)";
	public static final String TAB_AGRPRAISE = "create table if not exists Agrpraise(id integer primary key autoincrement, praiseId integer, agrInfoId integer, isPraise integer,userId integer,praiseTime text,userName text)";
	public static final String TAB_AGRIMGS = "create table if not exists AgrImgs(id integer primary key autoincrement, ImgID integer,agrInfoId integer, URLcontent text,descript text)";

	// 消息表

	public static final String TAB_MSG = "create table if not exists t_message(id integer primary key autoincrement, msgid integer, "
			+ "fromid integer, toid integer,msgtypeid integer,mainid integer, msgsendtypeid integer,sendmainid integer,content text,"
			+ " msgTime text,ischeck integer,areacode text,croptype integer)";

	// 产品相关表
	public static final String TAB_PRODUCT_CATEGORY = "create table if not exists productcategories(categoryID integer, CategoryName text, Code text,ParentID integer,isProduct integer,primary key(categoryID,isProduct))";
	public static final String TAB_PRODUCT = "create table if not exists products(ProductID integer, ProductTemplateID integer, ProductTitle text, ProductSummary text,CreateTime text,SmallUrl text,BigUrl text,HtmlUrl text,Source text, CategoryId integer, CategoryName text,info text, isProduct integer, userId integer,primary key(ProductID,isProduct))";
	public static final String TAB_PRODUCT_COMMENT = "create table if not exists productcomments(commentId integer, productId integer, comment text,parentId integer, userId int,userName text,commentTime text, isProduct integer,primary key(commentId,isProduct))";
	
	public static final int FARM_REGION_REQUEST = 101;
	public static final int CROP_TYPE_REQUEST = 102;
	public static final int CROP_VARIETY_REQUEST = 103;
	public static final int CROP_PERIOD_REQUEST = 104;
	public static final int CITY_ADD_REQUEST = 105;
	public static final int SELECT_COUNTRY_REQUEST = 106;
	public static final int ADD_FARM_REQUEST = 107;
	public static final int ADD_FARM_RESULT = 108;
	public static final int SELECT_WORKSTATION = 109;

	public static final int CITY_SET_RESULT = 201;
	public static final int LOGIN_SUCCEED = 10086;
	public static final int PRAISE_COUNT = 1;

	public static final int UPDATE_WARNING_FLAG = 301;
	public static final int UPDATE_CROPS_FLAG = 302;
	public static final int UPDATE_MSG_FLAG = 303;
	public static final int UPDATE_NO_NEW_REMIND_FLAG = 0;
	public static long selectedScale = 0;
	public static int MAP_RESOLUTION = 2;
	public static final int MAX_FONT = 40;
	public static final int MIN_FONT = 10;
	public static final int FONT_STEP = 1;
	public static Point2D CURRENT_CENTER = null;
	public static int ZOOM = 0;
	public static int SCALE_INDEX = -1;
	public static int LAST_SATTELITE = -1;
	public static final int SATTELITE = 1;
	public static final int TERRAIN = 2;
	public static final int MAP2D = 3;
	public static int MAP_TYPE = -1;
	public static final int DEFAULT_ZOOM = 10;
	public static final int LOCATION_ZOOM = 6;
	public static final int LOCATION_ZOOM_VIP = 11;
	public static long[] mSattelites = new long[] { 81920000, 40960000, 20480000, 10240000, 5120000, 2560000, 1280000, 640000, 320000,
			160000, 80000, 40000, 20000, 10000, 5000 };
	public static long[] mTerrains = new long[] { 81920000, 40960000, 20480000, 10240000, 5120000, 2560000, 1280000, 640000, 320000, 160000,
			80000 };
	
	public static long[] map2d = new long[] { 81920000, 40960000, 20480000, 10240000, 5120000, 2560000, 1280000, 640000, 320000, 160000,
		80000 };
	
	public static long[] mLabels = new long[] { 81920000, 40960000, 20480000, 10240000, 5120000, 2560000, 1280000, 640000, 320000, 160000,
			80000, 40000, 20000, 10000, 5000 };

	public static final int ADD_NET_AREA = 401;
	public static final int CROPS_UPLOAD_REQUEST = 402;
	public static final int ADV_UPLOAD_REQUEST = 403;
	public static final String CITY_SET_ACTIVITY_START_FLAG = "add";
	// 获取camera相片请求
	public static final int IMAGE_CAPTURE_REQUEST = 501;
	public static final int IMAGE_GET_REQUEST = 502;
	// 下拉刷新id
	public static final int CROP_PULL_DOWN_ID = 601;

	public static final int WEATHER_FRESH_TIME = 1000 * 60 * 60;

	public static final int NO_REMIND_FLAG = 0;
	public static User mLocalUser = null;
	public static String SESSIONID = null;
	public static String MANAGER_TEL = "";
	public static int MENU_WIDTH = 320;
	
	public static final String DEFAULT_RESHREN_TIME = "2012-12-01"; 
	//自动更新相关
	public static final int UPDATA_CLIENT=1;
	public static final int UPDATA_ERROR=0;
	public static final int DOWN_ERROR=-1;
	public static final int UPDATA_NO=2;
	
	//咨询相关
	public static final int ADV_TYPE_NO = -1;
	public static final int ADV_TYPE_HOT = 1;
	public static final int ADV_TYPE_NEW = 2;
	public static final int ADV_TYPE_MY = 3;
	public static final int ADV_TYPE_SERCH = 4;
	
	//从地图获取坐标信息
	public static final int MAP_POINT_REQUEST=701;
	public static final int MAP_POINT_RESULT=702;

	public static int[] getDisplay(Activity activity) {
		int[] data = new int[2];
		Display mDisplay = activity.getWindowManager().getDefaultDisplay();
		data[0] = mDisplay.getWidth();
		data[1] = mDisplay.getHeight();
		return data;
	}

}
