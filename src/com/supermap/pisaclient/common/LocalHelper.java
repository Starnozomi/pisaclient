/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @LocalHelper.java - 2014-5-20 上午10:07:29
 */

package com.supermap.pisaclient.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.supermap.pisaclient.biz.CommonDao;
import com.supermap.pisaclient.entity.Address;
import com.supermap.pisaclient.entity.CommonArea;

public class LocalHelper {

	private Context context;
	public static LocalHelper local = null;

	private LocationClient locationClient = null;
	private static final int UPDATE_TIME = 100;
	private Address address = null;

	private double longitude;
	private double latitude;
	
	private LocalHelper(Context context) {
		this.context = context;
	}

	public static LocalHelper getInstance(Context context) {
		if (local == null)  
			local = new LocalHelper(context);
		return local;
	}

	public void initLocation() {
		locationClient = new LocationClient(this.context);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setProdName("LocationPisa");
		option.setScanSpan(UPDATE_TIME);
		locationClient.setLocOption(option);
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					return;
				}
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				longitude=lng;
				latitude=lat;
				address = new Address();
				address.lat = lat;
				address.lng = lng;
				if (locationClient != null) {
					locationClient.stop();
					locationClient = null;
				}
			}

		});
		locationClient.start();
		locationClient.requestLocation();
	}

	public Address getLocation() {
		while (address == null) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
		return address;
	}

	public void init() {
		
		locationClient = new LocationClient(this.context);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setProdName("LocationPisa");
		option.setScanSpan(UPDATE_TIME);
		locationClient.setLocOption(option);
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					return;
				}
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				longitude=lng;
				latitude=lat;
				try {
					address = new Address();
					//CommonArea area = CommonDao.getInstance().getArea(lng, lat);
					//address.code = area.townCode;
					//address.address = area.townName;
					//address.pcode = area.countyCode;
					//address.pname = area.countyName;
					address.lat = lat;
					address.lng = lng;
					//获取地址信息
					getLocation(String.valueOf(lng),String.valueOf(lat));
					locationClient.stop();
					locationClient = null;
				} catch (Exception e) {
//					System.out.println("定位地区不在服务器支持的范围内");
//					e.printStackTrace();
				}
			}

		});
		locationClient.start();
		locationClient.requestLocation();
	}

	private final String ak="BPgj3OIlpdVv3FfSiDcUkLLAwkB3numy"; //这里填写申请的ak  
	private final String type="json"; //如果返回格式为json，要在url里面设置，默认的是xml  
	private final String mCode="A3:E0:DE:FC:7C:6A:09:E3:D7:C4:A2:26:01:03:A2:1D:26:AE:97:53;com.supermap.pisaclient"; //这里填写申请ak时用的安全码
	private StringBuilder stringBuilder;  
	private  BufferedReader bufferedReader;  
	private String info;  
	public void getLocation(final String longitude,final String latitude){  
  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
                try {  
                    String path="http://api.map.baidu.com/telematics/v3/reverseGeocoding?location="+longitude+","  
                            +latitude+"&output="+type+"&ak="+ak+"&mcode="+mCode;  
                    URL url=new URL(path);  
                    URLConnection connection=url.openConnection();  
                    connection.setConnectTimeout(2000);  
                    connection.connect();  
                    bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));  
                    stringBuilder=new StringBuilder();  
                    String line=null;  
                    while((line=bufferedReader.readLine())!=null){  
                        stringBuilder.append(line);  
                    }  
                    info=stringBuilder.toString();  
                  //  setLocation();  
                } catch (MalformedURLException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                finally {  
                    try{  
                        if(bufferedReader!=null){  
                            bufferedReader.close();  
                        }  
                    }  
                    catch(IOException e){  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }).start();  
    }  
	
	public void setLocation(){  
        JSONTokener jsonTokener=new JSONTokener(info);  
        try {  
            JSONObject object= (JSONObject) jsonTokener.nextValue();  
            String city=object.getString("city");  
           // Intent intent=new Intent();  
          //  intent.setAction("location");  
         //   intent.putExtra("location",city);  
           // sendBroadcast(intent);  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
    }  
	
	/**
	 * lnglat[0]是经度，lnglat[1]是纬度
	 * @return
	 */
	public double[] getLngLat()
	{
		double [] lnglat={longitude,latitude};
		return lnglat;
	}
	
	public String getCity() {
		while (address == null) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
		return address.address;
	}

	public Address getAddress() {
		while (address == null) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
		return address;
	}
	
	public Address getAddress(double lng,double lat)
	{
		try
		{
			//heyao_1
			//CommonArea area = CommonDao.getInstance().getArea(lng, lat);
			address = new Address();
			/*address.code = area.townCode;
			address.address = area.townName;
			address.pcode = area.countyCode;
			address.pname = area.countyName;*/
			address.code = "530000";
			address.address = lng+","+lat;
			address.pcode = lng+","+lat;
			address.pname = lng+","+lat;
			address.lat = lat;
			address.lng = lng;	
		}
		catch(Exception e)
		{
			address=null;
		}		
		return address;
	}

	public void close() {
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.stop();
			locationClient = null;
		}
	}

}
