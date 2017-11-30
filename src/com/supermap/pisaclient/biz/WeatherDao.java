package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.TempratureRange;
import com.supermap.pisaclient.entity.WarningInfo;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.http.HttpHelper;

public class WeatherDao  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1736914905959303927L;

	
	public Weather getByCity(String areacode,String observTimes) {
		
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String observTimes = df.format(current).toString();
//		observTimes = "2014-03-30 13:00:00";
		Weather w = null;
		try {
			
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"weather.query\",\"CustomParams\":{\"areaCode\":\""+areacode+"\",\"observTimes\":\""+observTimes+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_WEATHER_BY_CITY_URL, params, HttpHelper.METHOD_POST);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				
				w = new Weather();
				
				w.city = obj.getString("city");
				String str_tem = null;
				str_tem = obj.getString("WeatherState");
				if (isNull(str_tem)) {
					w.WeatherState = 0;
				}
				else {
					w.WeatherState = obj.getInt("WeatherState");
				}
				
				str_tem = null;
				str_tem = obj.getString("ObservTimes");
				if (isNull(str_tem)) {
					w.ObservTimes = 0+"";
				}
				else {
					w.ObservTimes = obj.getString("ObservTimes");
				}
				
				str_tem = null;
				str_tem = obj.getString("Temperature");
				if (isNull(str_tem)) {
					w.Temperature = 0;
				}
				else {
					w.Temperature = obj.getInt("Temperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("MaxTemperature");
				if (isNull(str_tem)) {
					w.MaxTemperature = 0;
				}
				else {
					w.MaxTemperature = obj.getInt("MaxTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("MinTemperature");
				if (isNull(str_tem)) {
					w.MinTemperature = 0;
				}
				else {
					w.MinTemperature = obj.getInt("MinTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindDirection");
				if (isNull(str_tem)) {
					w.WindDirection = 0+"";
				}
				else {
					w.WindDirection = obj.getString("WindDirection");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindSpeed");
				if (isNull(str_tem)) {
					w.WindSpeed = 0+"";
				}
				else {
					w.WindSpeed = obj.getString("WindSpeed");
				}
				
				str_tem = null;
				str_tem = obj.getString("Humidity");
				if (isNull(str_tem)) {
					w.Humidity = 0;
				}
				else {
					w.Humidity = obj.getInt("Humidity");
				}
				
				
				str_tem = null;
				str_tem = obj.getString("Pressure");
				if (isNull(str_tem)) {
					w.Pressure = 0+"";
				}
				else {
					w.Pressure = obj.getString("Pressure");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return w;
	}
	
	private  boolean isNull(String str){
		return str.equals("null");
	}
	
	/**
	 * 根据地区获取预报温度
	 * @param areaCode
	 * @return
	 */
	public List<Weather> getForcastWeather(String areaCode) {
		List<Weather> list = null;
		Weather w = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("areaCode", areaCode);
			jsonParam.put("Function", "weekWeather.forcast");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<Weather>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				w = new Weather();
				String str_tem = null;
				str_tem = obj.getString("WeatherState12");
				if (isNull(str_tem)) {
					w.WeatherState = 0;
				}
				else {
					w.WeatherState = obj.getInt("WeatherState12");
				}
				
				str_tem = null;
				str_tem = obj.getString("ForscastDate");
				if (isNull(str_tem)) {
					w.ObservTimes = 0+"";
				}
				else {
					w.ObservTimes = obj.getString("ForscastDate");
				}
				
				str_tem = null;
				str_tem = obj.getString("drybultemp");//
				if (isNull(str_tem)) {
					w.Temperature = 0;
				}
				else {
					w.Temperature = obj.getInt("drybultemp");
				}
				
				str_tem = null;
				str_tem = obj.getString("MaxTemperature");
				if (isNull(str_tem)) {
					w.MaxTemperature = 0;
				}
				else {
					w.MaxTemperature = obj.getInt("MaxTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("MinTemperature");
				if (isNull(str_tem)) {
					w.MinTemperature = 0;
				}
				else {
					w.MinTemperature = obj.getInt("MinTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindDirection12");
				if (isNull(str_tem)) {
					w.WindDirection = 0+"";
				}
				else {
					w.WindDirection = obj.getString("WindDirection12");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindSpeed");
				if (isNull(str_tem)) {
					w.WindSpeed = 0+"";
				}
				else {
					w.WindSpeed = obj.getString("WindSpeed");
				}
				
				str_tem = null;
				str_tem = obj.getString("Humidity");
				if (isNull(str_tem)) {
					w.Humidity = 0;
				}
				else {
					w.Humidity = obj.getInt("Humidity");
				}
				
				str_tem = null;
				str_tem = obj.getString("MaxHumidity");
				if (isNull(str_tem)) {
					w.MaxHumidity = 0;
				}
				else {
					w.MaxHumidity = obj.getInt("MaxHumidity");
				}
				
				str_tem = null;
				str_tem = obj.getString("MinHumidity");
				if (isNull(str_tem)) {
					w.MinHumidity = 0;
				}
				else {
					w.MinHumidity = obj.getInt("MinHumidity");
				}
				str_tem = null;
				str_tem = obj.getString("Pressure");
				if (isNull(str_tem)) {
					w.Pressure = 0+"";
				}
				else {
					w.Pressure = obj.getString("Pressure");
				}
				list.add(w);
			}
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	/**
	 * 根据parentid 获取所有子地区
	 * @param parentid 0:云南省 ，2：区县 ，其他：乡镇
	 * @return
	 */
	public List<City> getAllArea( int parentid){
		List<City> list = null;
	
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getAllAreas\",\"CustomParams\":{\"parentid\":\""+parentid+"\"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_WEATHER_BY_CITY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<City>();
			City city = null;
			for (int i = 0; i < array.length(); i++) {
				city= new City();
				JSONObject obj = array.getJSONObject(i);
				city.areaid = obj.getInt("areaid");
				city.name = obj.getString("areaname");
				city.areacode = obj.getString("areacode");
				city.prarentId = obj.getInt("parentid");
				list.add(city);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	/**
	 * 获取所有城市信息
	 * @return
	 */
	public List<City> getAllArea(){
		List<City> list = null;
	
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getAll\",\"CustomParams\":{\"parentid\":\"0\"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_WEATHER_BY_CITY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<City>();
			City city = null;
			for (int i = 0; i < array.length(); i++) {
				city= new City();
				JSONObject obj = array.getJSONObject(i);
				city.areaid = obj.getInt("areaid");
				city.name = obj.getString("areaname");
				city.areacode = obj.getString("areacode");
				city.prarentId = obj.getInt("parentid");
				list.add(city);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	public List<TempratureRange> getTemperaturs(String areacode,String forscastDate){
		List<TempratureRange> list = null;
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String forscastDate = df.format(current).toString();
//		forscastDate = "2014-03-17 10:00:00";
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"weekTemp.forcast\",\"CustomParams\":{\"areaCode\":\""+areacode+"\", \"ForscastDate\":\""+forscastDate+"\"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_WEATHER_BY_CITY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<TempratureRange>();
			TempratureRange tempratureRange = null;
			if(array==null){
				return null;
			}
			for (int i = 0; i < array.length(); i++) {
				tempratureRange = new TempratureRange();
				JSONObject obj = array.getJSONObject(i);
				tempratureRange.city = obj.getString("areaname");
				tempratureRange.time = obj.getString("ForscastDate");
				tempratureRange.max = obj.getInt("MaxTemperature");
				tempratureRange.min = obj.getInt("MinTemperature");
				list.add(tempratureRange);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	public List<WarningInfo> getWarningInfos(String areacode,String lastRefreshTime){
		
		List<WarningInfo> warningList = null;
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String lastRefreshTime = df.format(lastDate).toString();
//		lastRefreshTime = "2014-05-13 12:00:00";
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param ="{\"Function\":\"alert.signal\",\"CustomParams\":{\"areaCode\":\""+areacode+"\",\"lastIssueTime\":\""+lastRefreshTime+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_WEATHER_BY_CITY_URL, params, HttpHelper.METHOD_POST);
			warningList = new ArrayList<WarningInfo>();
			WarningInfo warningInfo =null;
			for (int i = 0; i < array.length(); i++) {
				
				warningInfo =  new WarningInfo();
				JSONObject obj = array.getJSONObject(i);
				if ((areacode!=null)&&(areacode.equals("500106"))) {
					warningInfo.city = "内蒙古自治区"; 
				}
				else {
					warningInfo.city = obj.getString("areaname");
				}
				
				warningInfo.type = obj.getString("SignalType");
				warningInfo.level = obj.getString("SignalLevel");
				warningInfo.time = obj.getString("IssueTime");
				warningInfo.title = warningInfo.city+warningInfo.type+warningInfo.level+"预警";
				warningInfo.content = obj.getString("IssueContent");
				warningList.add(warningInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return warningList;
	}
	
	
	/**
	 * 获取今天的预报温度
	 * @param areaCode
	 * @param ForscastDate
	 * @return
	 */
	public Weather getTodayForcastWeather(String areaCode,String ForscastDate){
		Weather w = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("areaCode", areaCode);
			customParams.put("ForscastDate","%"+ForscastDate+"%");
			jsonParam.put("Function", "todayWeather.forcast");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				w= new Weather();
				String str_tem = null;
				str_tem = obj.getString("WeatherState12");
				if (isNull(str_tem)) {
					w.WeatherState = 0;
				}
				else {
					w.WeatherState = obj.getInt("WeatherState12");
				}
				
				str_tem = null;
				str_tem = obj.getString("ForscastDate");
				if (isNull(str_tem)) {
					w.ObservTimes = 0+"";
				}
				else {
					w.ObservTimes = obj.getString("ForscastDate");
				}
				
				str_tem = null;
				str_tem = obj.getString("drybultemp");//
				if (isNull(str_tem)) {
					w.Temperature = 0;
				}
				else {
					w.Temperature = obj.getInt("drybultemp");
				}
				
				str_tem = null;
				str_tem = obj.getString("MaxTemperature");
				if (isNull(str_tem)) {
					w.MaxTemperature = 0;
				}
				else {
					w.MaxTemperature = obj.getInt("MaxTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("MinTemperature");
				if (isNull(str_tem)) {
					w.MinTemperature = 0;
				}
				else {
					w.MinTemperature = obj.getInt("MinTemperature");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindDirection12");
				if (isNull(str_tem)) {
					w.WindDirection = 0+"";
				}
				else {
					w.WindDirection = obj.getString("WindDirection12");
				}
				
				str_tem = null;
				str_tem = obj.getString("WindSpeed");
				if (isNull(str_tem)) {
					w.WindSpeed = 0+"";
				}
				else {
					w.WindSpeed = obj.getString("WindSpeed");
				}
				
				str_tem = null;
				str_tem = obj.getString("Humidity");
				if (isNull(str_tem)) {
					w.Humidity = 0;
				}
				else {
					w.Humidity = obj.getInt("Humidity");
				}
				
				str_tem = null;
				str_tem = obj.getString("MaxHumidity");
				if (isNull(str_tem)) {
					w.MaxHumidity = 0;
				}
				else {
					w.MaxHumidity = obj.getInt("MaxHumidity");
				}
				
				str_tem = null;
				str_tem = obj.getString("MinHumidity");
				if (isNull(str_tem)) {
					w.MinHumidity = 0;
				}
				else {
					w.MinHumidity = obj.getInt("MinHumidity");
				}
				str_tem = null;
				str_tem = obj.getString("Pressure");
				if (isNull(str_tem)) {
					w.Pressure = 0+"";
				}
				else {
					w.Pressure = obj.getString("Pressure");
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return w;
	}
	
	public Weather getLastWeather(String areaCode,String ForscastDate){
		Weather w = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("areaCode", areaCode);
			customParams.put("ForscastDate","%"+ForscastDate+"%");
			jsonParam.put("Function", "weather.lastWeather");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				w= new Weather();
				w.MaxTemperature = obj.getInt("MaxTemperature");
				w.MinTemperature = obj.getInt("MinTemperature");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return w;
	}
}
