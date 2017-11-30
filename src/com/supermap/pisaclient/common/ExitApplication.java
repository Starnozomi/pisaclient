package com.supermap.pisaclient.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.TempratureRange;
import com.supermap.pisaclient.entity.WarningInfo;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.service.PisaService;

public class ExitApplication extends Application {
	
	private List<Activity> activityList = new LinkedList<Activity>();
	public List<Weather> mWeathers = new ArrayList<Weather>();
	public List<City> mCities = new ArrayList<City>();
	public Map<String , List<TempratureRange>> mTemperatureMap = new HashMap<String, List<TempratureRange>>();
	public Map<String, List<WarningInfo>> mWarningMap = new HashMap<String, List<WarningInfo>>();
	public Map<String, Integer> mWarningIconMap = new HashMap<String, Integer>();
	private static ExitApplication instance;
	public CityDao mCityDao;
	public WeatherDao mWeatherDao;
	public String mDefaultCity;
	public Context mContext;
	public CropDao mCropDao;
	public SharedDeal mSharedDeal;
	public boolean mAppStarted = false;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		SDKInitializer.initialize(this);
		
		mAppStarted = true;
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		instance = this;
		initData(this);
	}
	
	public static ExitApplication getInstance() {
		if (null == instance) {
			instance = new ExitApplication();
		}
		return instance;
	}
	/**
	 * 加载缓冲数据，包括城市，当天天气，一周温度变化，默认城市，
	 * @param context
	 */
	public void initData (Context context){
		 mContext = context;
//		 WeatherDataUtil.initData();
		 mCityDao = new CityDao(context);
		 mCropDao = new CropDao();
//		 mWeatherDao = new WeatherDao();
		 Common.myCities = mCityDao.getAllCity();//mCities
//		 mWeathers = mCityDao.getAllWeathers();
//		 for(City city:mCities){
//			 mTemperatureMap.put(city.name, mCityDao.getAllTemperatures(city.name));
//		 }
		 mSharedDeal = new SharedDeal(context);
		 mDefaultCity = mSharedDeal.getDefaultCity();
		 initWarningIconMap();
	
//		 new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				List<City> list = new ArrayList<City>();
//				list = new WeatherDao().getAllArea();
//				mCityDao.addAllCity(list);
//				System.out.println("add all city success");
//				
//			}
//		}).start();
	}
	
	private void initWarningIconMap(){
		mWarningIconMap.put("1",R.drawable.wt_warning);
		mWarningIconMap.put("2",R.drawable.wt_warning2);
	}
	
	public Weather getWeather(String city){
		Weather weather = null;
		if (city==null) {
			return weather;
		}
		for(Weather temp:mWeathers){
			if (temp.city.equals(city)) {
				weather = temp;
				break;
			}
		}
		return weather;
	}
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void saveCityMsg(){
		mCityDao.clearCityTab();
		if ((mCities==null)||(mCities.size()==0)) {
			return;
		}
//		for(City city:mCities){
//			mCityDao.addCity(city);
//		}
		//保存默认城市
		mSharedDeal.setDefaultCity(mDefaultCity);
		mCityDao.close();
	}
	
	
	public void exit(int code) {
		System.out.println("exit appliction");
		mAppStarted =false;
		for (Activity activity : activityList) {
			activity.finish();
		}
		
		stopService(new Intent(this.getApplicationContext(),PisaService.class));
		System.exit(code);
	}

}
