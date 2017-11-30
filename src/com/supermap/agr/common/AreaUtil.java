package com.supermap.agr.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.agr.obj.City;

import android.hardware.Camera.Area;

public class AreaUtil {
	public static  String getAreaBuilder(){
		InputStreamReader inputStreamReader = null;
		 StringBuilder stringBuilder = null;
	        try { 
	            inputStreamReader = new InputStreamReader(ArgApplication.getContext().getAssets().open("areaTabel.json") ,"GB2312");
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
	            String line;  
	            stringBuilder = new StringBuilder();  
	            while ((line = bufferedReader.readLine()) != null) {  
	                stringBuilder.append(line);  
	            }  
	            inputStreamReader.close();  
	            bufferedReader.close(); 
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
	        return stringBuilder.toString();
	}
	
	/**************************************************
	 * 
	 * @param jsonArray
	 * @param cityList
	 */
	public static void getAreas(JSONArray jsonArray,List<City> cityList){
		 City city = null;
		 JSONArray jo = null;
			try {
				for (int i = 0; i < jsonArray.length(); i++) {  
		               JSONObject jsonObject = jsonArray.getJSONObject(i);  
		               city = new City();
		               city.setCode(jsonObject.getString("code"));
		               city.setName(jsonObject.getString("name"));
		                jo = jsonObject.getJSONArray("children");
		                cityList.add(city);
		               getAreas(jo,city.getChildren());
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			} 
	}
	

	
}
