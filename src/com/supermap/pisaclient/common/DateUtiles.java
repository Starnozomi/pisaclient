package com.supermap.pisaclient.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;

import Date.RiqiTest;

public class DateUtiles {
	
	
	private static String mMonths[] ={"正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","腊月"};
	private static String mDays[] ={"初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
							"十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
							"廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十"};
	private static String mWeeks[] ={"周日","周一","周二","周三","周四","周五","周六"};
	
	/**
	 * 获取当前时间的阳历和农历日期
	 * @return
	 */
	public static String getCurrentTime(){
		
		String time = null;
//		long current = System.currentTimeMillis();
//		Date currentTime = new Date(current);
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String forscastDate = df.format(current);
		
		 Calendar ca = Calendar.getInstance();
	      int year = ca.get(Calendar.YEAR);//获取年份
	      int month = ca.get(Calendar.MONTH)+1;//获取月份 
	      int day = ca.get(Calendar.DATE);//获取日
	      int minute = ca.get(Calendar.MINUTE);//分 
	      int hour = ca.get(Calendar.HOUR);//小时 
	      int second = ca.get(Calendar.SECOND);//秒
	      int dayOfweek = ca.get(Calendar.DAY_OF_WEEK); 
	     
	      //获取农历时间
	      String noli = new ChinaDate().today(0);
//	      RiqiTest riqiTest = new RiqiTest(year,month,day);
//	      int mMonth = riqiTest.getMonth();
//	      int mDay   = riqiTest.getDay();
//	      try {
//	    	  time = month+"/"+day+" "+mMonths[mMonth-1]+mDays[mDay-1];
//	      } catch (Exception e) {
//	      }
	      if (noli!=null) {
	    	  time = month+"/"+day+"  "+noli;
	      }
	      else {
	    	  time = month+"/"+day+"  ";
	      }
	      return time;
	}
	
	/**
	 * 白天   早8:00 - 晚8:00 
	 * @return
	 */
	public static boolean isNignt(String time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date d = null;
		try {
			d = sdf.parse(time);
		} catch (ParseException e) {
			return false;
		}
		long dvalue=d.getTime();
		SimpleDateFormat df = new SimpleDateFormat("HH");
		String hour = df.format(dvalue);
		int h = Integer.parseInt(hour);
		if ((h>=8)&&(h<=20)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取昨天开始的七天的星期
	 * @return
	 */
	
	public static String[] getWeeks(){
		
		 String weeks[] = new String[7];
		 Calendar ca = Calendar.getInstance();
		 
		 int index = -1;//一周第几天
		 int dayOfweek = ca.get(Calendar.DAY_OF_WEEK);
		 index = dayOfweek;
		 weeks[1] = mWeeks[index-1];
		 
		 if((dayOfweek-1)==0){
			 index = 7;
			 weeks[0] = mWeeks[index-1];
		 }
		for (int i = 1; i <=5; i++) {
			index = dayOfweek;
			index+=i;
			if (index>7) {
				index = index%7;
			}
			weeks[i+1]=mWeeks[index-1];
		}
		 weeks[0] = "昨天";
		 weeks[1] ="今天";
		 weeks[2] ="明天";
		 
		return weeks;
	}
	/**
	 * 获取昨天开始的七天的日期
	 * @return
	 */
	public static String[] getDays(){
		
		String days[]=new String[7];
		Calendar cal = Calendar.getInstance();
//		String yesterday = new SimpleDateFormat("MM-dd").format(cal.getTime());
		int month;
		int day;
	    String strday = null; 
	    for(int i=0;i<7;i++){
	    	cal = Calendar.getInstance();//
	    	cal.add(Calendar.DATE, i-1);
	    	month=cal.get(Calendar.MONTH)+1;//获取月份 
		    day=cal.get(Calendar.DATE);//获取日
	    	strday  = month+"/"+day;
	    	days[i] = strday;
	    }
		return days;
	}
	/**
	 * 
	 * @param time 格式 :yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static String getCurrentTimeST(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		long now = System.currentTimeMillis();
		String str_now = sdf.format(now);
		
		java.util.Date d = null;
		try {
			d = sdf.parse(str_now);
		} catch (ParseException e) {
			return null;
		}
		long dvalue=d.getTime();
		dvalue-=1000*60*60;
		return sdf.format(dvalue);
		
		
	}
	@Deprecated
	public static String getCurrentTimeBeforday(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		long now = System.currentTimeMillis();
		String str_now = sdf.format(now);
		
		java.util.Date d = null;
		try {
			d = sdf.parse(str_now);
		} catch (ParseException e) {
			return null;
		}
		long dvalue=d.getTime();
		dvalue-=1000*60*60*24;
		return sdf.format(dvalue);
		
		
	}
	
	public static String getCurrentTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date d = null;
		try {
			d = sdf.parse(time);
		} catch (ParseException e) {
			return null;
		}
		long dvalue=d.getTime();
		SimpleDateFormat df = new SimpleDateFormat("HH");
		String hour = df.format(dvalue);
		return hour+":00";
	}
	
	public static boolean isNeedRefresh(String time){
		if(time==null){
			return true;
		}
		long now = System.currentTimeMillis();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date d = null;
		try {
			d = sdf.parse(time);
		} catch (ParseException e) {
			return true;
		}
		long dvalue=d.getTime();
		long space = now -dvalue;
		if (space<=Constant.WEATHER_FRESH_TIME) {
			return false;
		}
		return true;
	}
	
	public static String getToday(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(System.currentTimeMillis());
	}
	
	public static String getForcastdTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 01:00:00");
		return sdf.format(System.currentTimeMillis());
	}
	
	public static String getNow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(System.currentTimeMillis());
	}
	
	public static String getLastDayTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(System.currentTimeMillis()-1000*60*60*24);
	}
	
	public static String getLastDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(System.currentTimeMillis()-1000*60*60*24);
	}
	
	
	public static String getCropTime(String time){
		String croptime = null;
		long now = System.currentTimeMillis();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date d = null;
		try {
			d = sdf.parse(time);
		} catch (ParseException e) {
			return croptime;
		}
		int month = d.getMonth() + 1;
		int day = d.getDate();
		croptime = month+"月"+day+"日";
		return croptime;
	}
	
	
	
}
