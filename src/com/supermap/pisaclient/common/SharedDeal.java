package com.supermap.pisaclient.common;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.Weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedDeal {

	private Context context;

	public SharedPreferences shared;

	public SharedDeal(Context context) {
		this.context = context;
		shared = context.getSharedPreferences("pisa_set", Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putInt("city_nums", 0);
		editor.commit();
	}

	public SharedPreferences getShared() {
		return shared;
	}

	public void setShared(SharedPreferences shared) {
		this.shared = shared;
	}
	
	public void setDefaultCity(String name){
		Editor editor = shared.edit();
		editor.putString("default_city", name);
		editor.commit();
	}
	public String getDefaultCity(){
		return shared.getString("default_city",null);
	}

	public boolean isFirstLogin(){
		   //判断是不是首次登录，  
		   if (shared.getBoolean("firststart", true)) {  
			    Editor  editor = shared.edit();  
			    //将登录标志位设置为false，下次登录时不在显示首次登录界面  
			    editor.putBoolean("firststart", false);  
			    editor.commit(); 
			    return true;
			}
		return false;
	}

	public void saveAllWeather(List<Weather> list) {
		removeAllCity();
		int city_nums =  list.size();
		ByteArrayOutputStream baos ;
		ObjectOutputStream oos ;
		String city_Base64 ;
		Editor editor = shared.edit();
		for(int i = 0;i<city_nums;i++){
			// 创建对象输出流，并封装字节流
			baos = new ByteArrayOutputStream();
			try {
				oos = new ObjectOutputStream(baos);
				// 将对象写入字节流
				oos.writeObject(list.get(i));
				// 将字节流编码成base64的字符串
				city_Base64 = new String(Base64.encodeBase64(baos.toByteArray()));
				//添加city
				editor.putString("weather"+i, city_Base64);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//增加城市数目
		editor.putInt("city_nums", city_nums);
		editor.commit();
	}
	
	public void removeAllCity() {
		
		int city_nums =  shared.getInt("city_nums",0);
		if(city_nums ==0){
			return;
		}
		Editor editor = shared.edit();
		for (int i = 0; i < city_nums; i++) {
			editor.remove("weather"+i);
		}
		editor.putInt("city_nums", 0);
		editor.commit();
	}
	public List<Weather> getAllWeather(){
		
		List<Weather> list = new ArrayList<Weather>();
		int num = shared.getInt("city_nums", 0);
		if(num == 0){
			return list;
		}
		String cityBase64;
		// 读取字节
		byte[] base64;
		// 封装到字节流
		ByteArrayInputStream bais;
		// 再次封装
		ObjectInputStream bis;
		Weather weather;
		for (int i = 0; i < num; i++) {
			cityBase64 = shared.getString("city"+i, null);
			base64 = Base64.decodeBase64(cityBase64.getBytes());
			bais = new ByteArrayInputStream(base64);
			try {
				 bis = new ObjectInputStream(bais);
				 weather = (Weather) bis.readObject();
				 list.add(weather);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
}