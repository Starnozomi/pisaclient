/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommonDao.java - 2014-9-1 上午9:52:51
 */

package com.supermap.pisaclient.biz;

import java.util.HashMap;

import org.json.JSONObject;

import com.supermap.pisaclient.entity.CommonArea;
import com.supermap.pisaclient.entity.CommonHeight;
import com.supermap.pisaclient.http.HttpHelper;

public class CommonDao {

	public static CommonDao dao;

	private CommonDao() {
		
	}

	public static CommonDao getInstance() {
		if (dao == null)
			dao = new CommonDao();
		return dao;
	}

	public CommonArea getArea(double lng, double lat) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"longitude\":" + lng + ",\"latitude\":" + lat + "}");
		params.put("method", "getAreaInfoByLocation");
		CommonArea area = new CommonArea();
		try {
			JSONObject obj = HttpHelper.loadOne(HttpHelper.AREA_BY_LOCATION, params, HttpHelper.METHOD_POST);
			area.townName = obj.getString("townName");
			area.townCode = obj.getString("townCode");
			area.countyName = obj.getString("countyName");
			area.countyCode = obj.getString("countyCode");
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*String lnglat = lat+","+lng;
		String apiurl = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="+ lnglat +"&output=json&pois=1&ak=94Tmshjhp03oul7xy95Gu3wwHkjGZvkk&mcode=EE:0C:C8:50:54:53:96:5A:55:8C:23:2F:93:7E:EB:AE:D8:C8:1B:F1;com.example.tangdekun.androidannotationsdemo";
		try {
			JSONObject obj = HttpHelper.loadOne(apiurl, params, HttpHelper.METHOD_POST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return area;
	}
	
	public CommonHeight getHeight(double lng, double lat) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"longitude\":" + lng + ",\"latitude\":" + lat + "}");
		params.put("method", "getHightByLocation");
		CommonHeight height = new CommonHeight();
		try {
			String res = HttpHelper.loadString(HttpHelper.HEIGHT_BY_LOCATION, params, HttpHelper.METHOD_POST);
			double data = Double.valueOf(res);
			height.height = Integer.parseInt(new java.text.DecimalFormat("0").format(data));
		} catch (Exception e) {

		}
		return height;
	}

}
