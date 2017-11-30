/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SuggestDao.java - 2014-7-10 下午5:06:51
 */

package com.supermap.pisaclient.biz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;

import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.FactorIndexRangeMsg;
import com.supermap.pisaclient.entity.Material;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Zone;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.http.HttpHelper;

public class MaterialDao {

	public String doSwitch(String deviceId,String feed){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceId);
		params.put("feed", feed);
		try {
			JSONArray array = HttpHelper.load(HttpHelper.MATERIAL_URL+"setStatus", params);
			return array.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	public List<Material> getAll(String zone) {
		List<Material> all = new ArrayList<Material>();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("zone", zone);
			JSONArray array = HttpHelper.load(HttpHelper.MATERIAL_URL+"getDevicesFromLocal", params);
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Calendar ca = Calendar.getInstance();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Material s = new Material();
				s.id = obj.getString("id");
				s.name = obj.getString("device");
				s.group = obj.getString("zone");
				s.latestValue = obj.getString("degree");
				//ca.setTimeInMillis(Long.parseLong(String.valueOf(obj.getInt("created")))*1000);
				//Date date = new Date();
				//s.latestTime = sdf.fobrmat(ca.getTime());
				String created = obj.getString("created");
				if(created.indexOf("_")!=-1) created=  created.substring(0,created.length()-1);
				s.latestTime =created ;
				s.alarm = obj.getString("alarm");
				s.feed = obj.getInt("feed");
				String unit = "";
				if(s.name.equals("氨气"))
					unit = "ppm";
				else if(s.name.equals("温度"))		
						unit = "℃";
				else if(s.name.equals("湿度"))		
						unit = "%";
				else if(s.name.equals("光照度"))		
					unit = "分钟";
				else if(s.name.equals("二氧化碳"))	
					unit = "ppm";
				else if(s.name.equals("三箱控制器"))	
						unit = "";
			
				s.latestValue += " "+ unit +"";
				all.add(s);
			}
		} catch (Exception e) {
				String  x = e.getMessage();
		}
		return all;
	}
	
	
	public List<Zone> getZones() {
		List<Zone> all = new ArrayList<Zone>();
		try {
		
			HashMap<String, String> params = new HashMap<String, String>();
			JSONArray array = HttpHelper.load(HttpHelper.MATERIAL_URL+"getZones", params);
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Calendar ca = Calendar.getInstance();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Zone s = new Zone();
				s.id = obj.getString("id");
				s.name = obj.getString("name");
				all.add(s);
			}
		} catch (Exception e) {
				
		}
		return all;
	}

	
}
