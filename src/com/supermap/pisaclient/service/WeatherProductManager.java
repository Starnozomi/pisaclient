package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.http.JsonHelper;
import com.supermap.pisaclient.http.JsonHelper.WeatherInfoModel;

public class WeatherProductManager {

	private BaiduMap mBaiduMap;
	private Context mContext;
	private List<OverlayOptions> lstMaker= new ArrayList<OverlayOptions>();
	private List<Overlay> lstOverLay = new ArrayList<Overlay>();
	private  HashMap<String, Integer> mapWeather = new HashMap<String, Integer>();
	public boolean  isChecked = false;
	
	public  WeatherProductManager(Context context,BaiduMap BaiduMap)
	{
		this.mContext = context;
		this.mBaiduMap = BaiduMap;
//		for(int i=0;i<32;i++)
		mapWeather.put("0", R.drawable.d0);	
		mapWeather.put("1", R.drawable.d1);	
		mapWeather.put("2", R.drawable.d2);	
		mapWeather.put("3", R.drawable.d3);	
		mapWeather.put("4", R.drawable.d4);	
		mapWeather.put("5", R.drawable.d5);	
		mapWeather.put("6", R.drawable.d6);	
		mapWeather.put("7", R.drawable.d7);	
		mapWeather.put("8", R.drawable.d8);	
		mapWeather.put("9", R.drawable.d9);	
		mapWeather.put("10", R.drawable.d10);	
		mapWeather.put("11", R.drawable.d11);	
		mapWeather.put("12", R.drawable.d12);	
		mapWeather.put("13", R.drawable.d13);	
		mapWeather.put("14", R.drawable.d14);	
		mapWeather.put("15", R.drawable.d15);	
		mapWeather.put("16", R.drawable.d16);	
		
		mapWeather.put("17", R.drawable.d17);	
		mapWeather.put("18", R.drawable.d18);	
		mapWeather.put("19", R.drawable.d19);	
		mapWeather.put("20", R.drawable.d20);	
		mapWeather.put("21", R.drawable.d21);	
		
		mapWeather.put("22", R.drawable.d22);	
		mapWeather.put("23", R.drawable.d23);
		mapWeather.put("24", R.drawable.d24);	
		mapWeather.put("25", R.drawable.d25);
		mapWeather.put("26", R.drawable.d26);	
		mapWeather.put("27", R.drawable.d27);
		mapWeather.put("28", R.drawable.d28);	
		mapWeather.put("29", R.drawable.d29);
		
		mapWeather.put("30", R.drawable.d30);
	}
	
	public void ShowWeatherInfo(boolean isShow)
	{
		if(!isShow)
		{
		for(Overlay lay:lstOverLay)
		{
			if(lay!=null)
				lay.remove();
		}
		}
		else
		{
			new Thread(new Runnable()
			{
			 JsonHelper json = new JsonHelper();
			 @Override
			 public void run() {
				List<WeatherInfoModel> lstData= json.GetWeatherForecastInfo();
				for(WeatherInfoModel obj :lstData )
				{
					addPoint(obj.strTemp,obj.lat,obj.strWeaState);
				}
				lstOverLay = mBaiduMap.addOverlays(lstMaker);
				}

			}).start();
		}
	}
	
	private void addPoint(String strTemp, LatLng point,String strState) {
		int state = R.drawable.d0;	
		if(mapWeather.containsKey(strState))
			state = mapWeather.get(strState);
		BitmapDescriptor bdA = BitmapDescriptorFactory
	            .fromResource(state);
	    MarkerOptions ooA = new MarkerOptions().position(point).icon(bdA).title(strTemp)
                .zIndex(9).draggable(true);
	    point = new LatLng(point.latitude-0.015,point.longitude);
	    
	    TextOptions txtOption = new TextOptions()
		.fontSize(22).fontColor(Color.RED).text(strTemp).rotate(0).position(point); 
	    
	    lstMaker.add(ooA);
	    lstMaker.add(txtOption);
	}
}
