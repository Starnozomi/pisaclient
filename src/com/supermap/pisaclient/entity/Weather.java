package com.supermap.pisaclient.entity;

import java.sql.Date;

public class Weather {
	
	private static final long serialVersionUID = 4318838659250781723L;
	
	public String city;
	public String ObservTimes;//观测时间
	public int WeatherState;//天气现象
	public int Temperature;//温度
	public int MaxTemperature;//最高温度
	public int MinTemperature;//最低温度
	public String WindDirection;//风向
	public String  WindSpeed;//风速
	public int Humidity;//相对湿度值
	public int MaxHumidity;
	public int MinHumidity;
	public String Pressure; //本站气压
	
}
