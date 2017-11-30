package com.supermap.pisaclient.common;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.supermap.pisaclient.R;

public class WeatherDataUtil {
	
	public static HashMap<Integer, Integer> mhm_wt_drawble = new HashMap<Integer, Integer>();
	public static HashMap<Integer, String>  mhm_wt_dec = new HashMap<Integer, String>();
	public static HashMap<String, Integer>  mhm_wt_warning = new HashMap<String, Integer>();
	public static String mWindLevel[] = {"无风","软风","轻风","微风","和风","劲风","强风","疾风","大风","烈风","狂风","暴风","台风"};
	public static String mWindDirection[] ={"北风","东北风","东风","东南风","南风","西南风","西风","西北风"};
	public static String mWarnings[][] ={{"台风蓝色","台风黄色","台风橙色","台风红色"},
								  {"暴雨蓝色","暴雨黄色","暴雨橙色","暴雨红色"},
								  {"暴雪蓝色","暴雪黄色","暴雪橙色","暴雪红色"},
								  {"寒潮蓝色","寒潮黄色","寒潮橙色","寒潮红色"},
								  {"大风蓝色","大风黄色","大风橙色","大风红色"},
								  {"沙尘暴蓝色","沙尘暴黄色","沙尘暴橙色","沙尘暴红色"},
								  {"高温蓝色","高温黄色","高温橙色","高温红色"},
								  {"干旱蓝色","干旱黄色","干旱橙色","干旱红色"},
								  {"雷电蓝色","雷电黄色","雷电橙色","雷电红色"},
								  {"冰雹蓝色","冰雹黄色","冰雹橙色","冰雹红色"},
								  {"霜冻蓝色","霜冻黄色","霜冻橙色","霜冻红色"},
								  {"大雾蓝色","大雾黄色","大雾橙色","大雾红色"},
								  {"霾蓝色","霾黄色","霾橙色","霾红色"},
								  {"道路结冰红色","道路结冰黄色","道路结冰橙色","道路结冰红色"}};
	public static void initData(){
		initHM();
	}
	
	/**
	 * 根据实况天气id获取预报天气id，从而加重天气现象图标
	 * @param id
	 * @return
	 */
	public static int nowToForcast(int id){
		int r_id =-1;
		switch (id) {
		case 0:
			r_id = 0;//晴
			break;
		case 1:
		case 2:
			r_id = 1;//多云
			break;
		case 3:
			r_id = 2;//阴
			break;
		case 4:
		case 6:
			r_id = 29;//烟尘，浮沉
			break;
		case 5:
			r_id = 53;//霾
			break;
		case 7:
		case 8:
			r_id = 30;//扬沙
			break;
		case 20:
		case 21:
		case 50:
		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
		case 58:
		case 59:
		case 60:
		case 61:
			r_id = 7;//小雨
			break;
		
		case 62:
		case 63:
			r_id = 8;//中雨
			break;
		case 64:
		case 65:
			r_id = 8;//大雨
			break;
		case 25:
			r_id = 3;//阵雨
			break;
		case 10:
		case 28:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
		case 48:
		case 49:
			r_id = 18;//雾
			break;
		case 67:
			r_id = 22;//中到大雨
			break;


		default:
			r_id = 2;//阴天
			break;
		}
		
		
		return r_id;
	}
	
	/**
	 * 根据实况天气id获取预报天气id，从而加重天气现象图标
	 * @param id
	 * @return
	 */
	public static String nowToForcastStr(int id){
		String temp = null;
		int r_id =-1;
		switch (id) {
		case 0:
			r_id = 0;//晴
			temp = "晴";
			break;
		case 1:
		case 2:
			r_id = 1;//多云
			temp = "多云";
			break;
		case 3:
			r_id = 2;//阴
			temp = "阴";
			break;
		case 4:
		case 6:
			r_id = 29;//烟尘，浮沉
			temp = "尘";
			break;
		case 5:
			r_id = 53;//霾
			temp = "霾";
			break;
		case 7:
		case 8:
			r_id = 30;//扬沙
			temp = "扬沙";
			break;
		case 20:
		case 21:
		case 50:
		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
		case 58:
		case 59:
		case 60:
		case 61:
			r_id = 7;//小雨
			temp = "小雨";
			break;
		
		case 62:
		case 63:
			r_id = 8;//中雨
			temp = "中雨";
			break;
		case 64:
		case 65:
			r_id = 8;//大雨
			temp = "大雨";
			break;
		case 25:
			r_id = 3;//阵雨
			temp = "阵雨";
			break;
		case 10:
		case 28:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
		case 48:
		case 49:
			r_id = 18;//雾
			temp = "雾";
			break;
		case 67:
			r_id = 22;//中到大雨
			temp = "中雨";
			break;


		default:
			r_id = 2;//阴天
			temp = "阴";
			break;
		}
		
		
		return temp;
	}
	
	public static int getWeatherDrawbleID(int id,boolean isNight){
		int r_id = -1;
		String iconID = null;
		if(isNight){
			switch (id) {
			case 0:
			case 1:
			case 3:iconID ="0"+id;
				break;
			case 13:
				iconID =""+id;
				break;
			default:
				if (id<10) {
					iconID ="00"+id;
				}
				else {
					iconID="0"+id;
				}
				break;
			}
		}
		else {
			if (id<10) {
				iconID ="00"+id;
			}
			else {
				iconID="0"+id;
			}
				
		}
		
		Class drawable  =  R.drawable.class;
        Field field = null;
        try {
            field = drawable.getField("w"+iconID);
            r_id = field.getInt(field.getName());
//            iv.setBackgroundResource(r_id);
        } catch (Exception e) {
            return -1;
        }
		return r_id;
	}
	
	public  static int getWarningDrawbleID(String type, String level){
		int r_id = -1;
		int m =-1;
		int n=-1;
		//14
		String types [] = {"台风","暴雨","暴雪","寒潮","大风","沙城暴","高温","干旱","雷电","冰雹","霜冻","大雾","霾","道路结冰"};
		//
		String levels[] ={"蓝色","黄色","橙色","红色"};
		
		for(int i= 0; i<14;i++ ){
			if (types[i].equals(type)) {
				m=i+1;
				break;
			}
		}
		for (int i = 0; i <4; i++) {
			if (levels[i].equals(level)) {
				n=i+1;
				break;
			}
		}
		
		String icon = "dw"+m+""+n;
		Class drawable  =  R.drawable.class;
         Field field = null;
         try {
             field = drawable.getField(icon);
             r_id = field.getInt(field.getName());
//             iv.setBackgroundResource(r_id);
         } catch (Exception e) {
//        	 return
         }
		
		return r_id;
	}
	
	public static void initHM(){
		mhm_wt_dec.put(0, "晴");  mhm_wt_drawble.put(0, R.drawable.wt_sun);
		mhm_wt_dec.put(1, "多云"); mhm_wt_drawble.put(1, R.drawable.wt_sun);
		mhm_wt_dec.put(2, "阴");
		mhm_wt_dec.put(3, "晴");
		mhm_wt_dec.put(4, "雷阵雨");
		mhm_wt_dec.put(5, "雷阵雨并有冰雹");
		mhm_wt_dec.put(6, "雨夹雪");
		mhm_wt_dec.put(7, "小雨");
		mhm_wt_dec.put(8, "中雨");
		mhm_wt_dec.put(9, "大雨");
		mhm_wt_dec.put(10, "暴雨");
		mhm_wt_dec.put(11, "大暴雨");
		mhm_wt_dec.put(12, "特大暴雨");
		mhm_wt_dec.put(13, "阵雪");
		mhm_wt_dec.put(14, "小雪");
		mhm_wt_dec.put(15, "中雪");
		mhm_wt_dec.put(16, "大雪");
		mhm_wt_dec.put(17, "暴雪");
		mhm_wt_dec.put(18, "雾");
		mhm_wt_dec.put(19, "冻雨");
		mhm_wt_dec.put(20, "沙城暴");
		mhm_wt_dec.put(21, "小到中雨");
		mhm_wt_dec.put(22, "中到大雨");
		mhm_wt_dec.put(23, "大到暴雨");
		mhm_wt_dec.put(24, "暴雨到大暴雨");
		mhm_wt_dec.put(25, "大暴雨到特大暴雨");
		mhm_wt_dec.put(26, "小到中雪");
		mhm_wt_dec.put(27, "中到大雪");
		mhm_wt_dec.put(28, "大到暴雪");
		mhm_wt_dec.put(29, "浮尘");
		mhm_wt_dec.put(30, "扬尘");
		mhm_wt_dec.put(31, "强沙尘暴");
		mhm_wt_dec.put(53, "雾霾");
		
		
		
		mhm_wt_warning.put(mWarnings[0][0], R.drawable.dw11);
		mhm_wt_warning.put(mWarnings[0][1], R.drawable.dw12);
		mhm_wt_warning.put(mWarnings[0][2], R.drawable.dw13);
		mhm_wt_warning.put(mWarnings[0][3], R.drawable.dw14);
		mhm_wt_warning.put(mWarnings[1][0], R.drawable.dw21);
		mhm_wt_warning.put(mWarnings[1][1], R.drawable.dw22);
		mhm_wt_warning.put(mWarnings[1][2], R.drawable.dw23);
		mhm_wt_warning.put(mWarnings[1][3], R.drawable.dw24);
		mhm_wt_warning.put(mWarnings[2][0], R.drawable.dw31);
		mhm_wt_warning.put(mWarnings[2][1], R.drawable.dw32);
		mhm_wt_warning.put(mWarnings[2][2], R.drawable.dw33);
		mhm_wt_warning.put(mWarnings[2][3], R.drawable.dw34);
		mhm_wt_warning.put(mWarnings[3][0], R.drawable.dw41);
		mhm_wt_warning.put(mWarnings[3][1], R.drawable.dw42);
		mhm_wt_warning.put(mWarnings[3][2], R.drawable.dw43);
		mhm_wt_warning.put(mWarnings[3][3], R.drawable.dw44);
		mhm_wt_warning.put(mWarnings[4][0], R.drawable.dw51);
		mhm_wt_warning.put(mWarnings[4][1], R.drawable.dw52);
		mhm_wt_warning.put(mWarnings[4][2], R.drawable.dw53);
		mhm_wt_warning.put(mWarnings[4][3], R.drawable.dw54);
		mhm_wt_warning.put(mWarnings[5][0], R.drawable.dw61);
		mhm_wt_warning.put(mWarnings[5][1], R.drawable.dw62);
		mhm_wt_warning.put(mWarnings[5][2], R.drawable.dw63);
		mhm_wt_warning.put(mWarnings[5][3], R.drawable.dw64);
		mhm_wt_warning.put(mWarnings[7][0], R.drawable.dw71);
		mhm_wt_warning.put(mWarnings[7][1], R.drawable.dw72);
		mhm_wt_warning.put(mWarnings[7][2], R.drawable.dw73);
		mhm_wt_warning.put(mWarnings[7][3], R.drawable.dw74);
		mhm_wt_warning.put(mWarnings[8][0], R.drawable.dw91);
		mhm_wt_warning.put(mWarnings[8][1], R.drawable.dw92);
		mhm_wt_warning.put(mWarnings[8][2], R.drawable.dw93);
		mhm_wt_warning.put(mWarnings[8][3], R.drawable.dw94);
		mhm_wt_warning.put(mWarnings[9][0], R.drawable.dw101);
		mhm_wt_warning.put(mWarnings[9][1], R.drawable.dw102);
		mhm_wt_warning.put(mWarnings[9][2], R.drawable.dw103);
		mhm_wt_warning.put(mWarnings[9][3], R.drawable.dw104);
		mhm_wt_warning.put(mWarnings[10][0], R.drawable.dw111);
		mhm_wt_warning.put(mWarnings[10][1], R.drawable.dw112);
		mhm_wt_warning.put(mWarnings[10][2], R.drawable.dw113);
		mhm_wt_warning.put(mWarnings[10][3], R.drawable.dw114);
		mhm_wt_warning.put(mWarnings[11][0], R.drawable.dw121);
		mhm_wt_warning.put(mWarnings[11][1], R.drawable.dw122);
		mhm_wt_warning.put(mWarnings[11][2], R.drawable.dw123);
		mhm_wt_warning.put(mWarnings[11][3], R.drawable.dw124);
		mhm_wt_warning.put(mWarnings[12][0], R.drawable.dw131);
		mhm_wt_warning.put(mWarnings[12][1], R.drawable.dw132);
		mhm_wt_warning.put(mWarnings[12][2], R.drawable.dw133);
		mhm_wt_warning.put(mWarnings[12][3], R.drawable.dw134);
		mhm_wt_warning.put(mWarnings[13][0], R.drawable.dw141);
		mhm_wt_warning.put(mWarnings[13][1], R.drawable.dw142);
		mhm_wt_warning.put(mWarnings[13][2], R.drawable.dw143);
		mhm_wt_warning.put(mWarnings[13][3], R.drawable.dw144);
	}
	
	/**
	 * 获取风的强度，列：微风，大风
	 * @param windspeed
	 * @return
	 */
	public String getWindDec(float windspeed){
		String strWindDec = null;
		if ((windspeed>=0.0)&&(windspeed<=0.2)) {
//			return mWindLevel[0];
			return "微风";
		}
		else if ((windspeed>=0.3)&&(windspeed<=1.5)) {
//			return mWindLevel[1];
			return "微风";
		}
		else if ((windspeed>=1.6)&&(windspeed<=3.3)) {
//			return mWindLevel[2];
			return "微风";
		}
		else if ((windspeed>=3.4)&&(windspeed<=5.4)) {
//			return mWindLevel[3];
			return "微风";
		}
		else if ((windspeed>=5.5)&&(windspeed<=7.9)) {
//			return mWindLevel[4];
			return "4级";
		}
		else if ((windspeed>=8.0)&&(windspeed<=10.7)) {
//			return mWindLevel[5];
			return "5级";
		}
		else if ((windspeed>=10.8)&&(windspeed<=13.8)) {
//			return mWindLevel[6];
			return "6级";
		}
		else if ((windspeed>=13.9)&&(windspeed<=17.1)) {
//			return mWindLevel[7];
			return "7级";
		}
		else if ((windspeed>=17.2)&&(windspeed<=20.7)) {
//			return mWindLevel[8];
			return "8级";
		}
		else if ((windspeed>=20.8)&&(windspeed<=24.4)) {
//			return mWindLevel[9];
			return "9级";
		}
		else if ((windspeed>=24.5)&&(windspeed<=28.4)) {
//			return mWindLevel[10];
			return "10级";
		}
		else if ((windspeed>=28.5)&&(windspeed<=32.6)) {
//			return mWindLevel[11];
			return "11级";
		}
		else if (windspeed>=32.7) {
//			return mWindLevel[12];
			return "12级";
		}
		else {
			return "微风";
		}
	}
	
	/**
	 * 获取风向
	 * @param windAngle
	 * @return
	 */
	public String getWindDirection(float windAngle){
		String strWindDerection = "";
		
		if ((windAngle>=337.5)&&(windAngle<22.5)) {
			return mWindDirection[0];
		}
		else if ((windAngle>=22.5)&&(windAngle<67.5)) {
			return mWindDirection[1];
		}
		else if ((windAngle>=67.5)&&(windAngle<112.5)) {
			return mWindDirection[2];
		}
		else if ((windAngle>=112.5)&&(windAngle<157.5)) {
			return mWindDirection[3];
		}
		else if ((windAngle>=157.5)&&(windAngle<202.5)) {
			return mWindDirection[4];
		}
		else if ((windAngle>=202.5)&&(windAngle<247.5)) {
			return mWindDirection[5];
		}
		else if ((windAngle>=247.5)&&(windAngle<292.5)) {
			return mWindDirection[6];
		}
		else if ((windAngle>=292.5)&&(windAngle<337.5)) {
			return mWindDirection[7];
		}
		return strWindDerection;
	}
	

	
}
