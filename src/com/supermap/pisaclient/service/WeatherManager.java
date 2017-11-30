package com.supermap.pisaclient.service;

import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.ui.CitySetActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class WeatherManager {
	private WeatherManager oThis =this;
	private Context context = null;
	private BaiduMap mBaiduMap = null;
	private OnWeatherLoadListener listener;
	private CityDao mCityDao;
	private WeatherDao mWeatherDao;
	public interface OnWeatherLoadListener{
		void Loaded();
	}
	
	public WeatherManager(Context context,BaiduMap mBaiduMap,OnWeatherLoadListener listener){
		this.context  =context;
		this.mBaiduMap = mBaiduMap;
		this.listener = listener;
		this.mCityDao = new CityDao(context);
		this.mWeatherDao = new WeatherDao();
	}
	
	
	public void loadLiveWeather(String city,OnLiveWeatherLoadListener listener){
		new LoadLiveWeatherTask(city,listener).execute();
	}
	
	public class LoadLiveWeatherTask extends AsyncTask<String, Integer, Weather> {
		
		private LoadLiveWeatherTask that = this;
		private String city;
		private OnLiveWeatherLoadListener listener;
		
		public LoadLiveWeatherTask(String city,OnLiveWeatherLoadListener listener){
			this.city = city;
			this.listener = listener;
		}
		
		
		@Override
		protected Weather doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return oThis.getWeatherFromNet(that.city);
		}
		
		@Override
		protected void onPostExecute(Weather result) {
			if(that.listener != null){
				that.listener.Loaded(result);
			}		
		}	
	}
	
	private Weather getWeatherFromNet(String city) {
		Weather weather = null;
		String areacode = null;
		areacode = mCityDao.queryAreacode(city);
		if (areacode==null) {
			return null;
		}
		if (areacode.length()>6) 
		{
			weather = mWeatherDao.getByCity(areacode.substring(0, 6), DateUtiles.getCurrentTimeST());				
		}
		else
		{
			weather = mWeatherDao.getByCity(areacode, DateUtiles.getCurrentTimeST());
		}
		if (weather!=null) {
				weather.city = mCityDao.queryCityName(areacode);
			}

		if (weather==null) {
			return null;
		}

		return weather;
	}
	
	/******************
	 * 显示实况温度面板
	 */
	public void showLiveWeatherPanel(){
		
	}
	/*****************
	 * 隐藏实况温度面板
	 */
	public void hideLiveWeatherPanel(){
		
	}
	
	public interface OnLiveWeatherLoadListener{
		void Loaded(Weather weather);
	}
	
}
