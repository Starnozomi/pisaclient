package com.supermap.pisaclient.common.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.common.CommonLog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.WeatherDataUtil;
import com.supermap.pisaclient.common.views.MyScrollView.OnScrollListener;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.TempratureRange;
import com.supermap.pisaclient.entity.WarningInfo;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.ui.CityAdd;
import com.supermap.pisaclient.ui.WarningActivity;
import com.supermap.pisaclient.ui.WeatherActivity;

public class FarmWeatherViewPagerItemView extends LinearLayout implements OnClickListener,OnScrollListener {
	//主界面和背景view
	private View mMainView;
	private ImageView miv_MainBack;
	// 主天气控件
	private ImageView miv_wt_icon;
	private TextView mtv_wt;
	private TextView mtv_wt_now;
	private TextView mtv_wt_range;
	private TextView mtv_wt_wind;
	private TextView mtv_wt_Humidity;
	private TextView mtv_wt_daytime;
	private TextView mtv_wt_ob_time;
	
	// 更多天气控件
	private TextView mtv_wt_more_tempre;
	private TextView mtv_wt_more_humidity;
	private TextView mtv_wt_more_hpa;
	private TextView mtv_wt_more_windspeed;
	private TextView mtv_wt_more_winddec;
	// 日期时间对应控件
	private TextView mtv_day1;
	private TextView mtv_day2;
	private TextView mtv_day3;
	private TextView mtv_day4;
	private TextView mtv_day5;
	private TextView mtv_day6;
	private TextView mtv_day7;

	private TextView mtv_daynum1;
	private TextView mtv_daynum2;
	private TextView mtv_daynum3;
	private TextView mtv_daynum4;
	private TextView mtv_daynum5;
	private TextView mtv_daynum6;
	private TextView mtv_daynum7;

	private CustomProgressDialog mPdDialog;
	private String mCity;
	private Weather mWeather;
	private LoadDataTask task;
	// scrollview 中的linearlayout
	private LinearLayout mLl_content;
	private View mLl_remind_view;
	private ImageView mPull_up_view;
	private ImageView mPull_down_view;
	private MyScrollView mMyScrollView;
	// scrollview 中的RelativeLayout 用于计算高度
	private RelativeLayout mRl_main;
	private RelativeLayout mRl_wt_time;
	private ImageView miv_warming;
	private LinearLayout 		mLl_Warning;
	private boolean isHaveWaring = true;
	private Context mContext;
	private TemperatureRangeView mTemperatureRangeView;

	private View mPager;
	private View mwt_current;
	private int w;
	private int h;
	private int tem[][] = new int[7][2];
	private String weeks[] = new String[7];
	private String days[] = new String[7];
	private boolean isHaveWeather = false;
	private boolean isLocation = false;
	private String mLocalArea = null;
	private List<TempratureRange> mTemperatureList = null;
	private List<WarningInfo> mWarningList = null;
	private WeatherDataUtil mWeatherDataUtil;
	private CityDao mCityDao;
	private WeatherDao mWeatherDao;
	private int mRemind_view_height;
	private boolean isLoading = false;
	private LayoutInflater mInflater ;
	private int mTransparentHeight;
	
	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;

	public FarmWeatherViewPagerItemView(Context context, String city) {
		super(context);
		this.mCity = city;
		this.mContext = context;
		this.mInflater  = LayoutInflater.from(context);
		setOrientation(LinearLayout.VERTICAL);
		mWeatherDataUtil = new WeatherDataUtil();
		mCityDao =new CityDao(context);
		mWeatherDao = new WeatherDao();
		weeks = DateUtiles.getWeeks();
		days = DateUtiles.getDays();
		setViews();
	}

	/*
	 * 往pager中 动态添加view
	 */
	private void setViews() {
		mPager = mInflater.inflate(R.layout.weather_pager, null);
		mMyScrollView = (MyScrollView) mPager.findViewById(R.id.sv);
		mMyScrollView.setOnScrollListener(FarmWeatherViewPagerItemView.this);
		mLl_content = (LinearLayout) mPager.findViewById(R.id.ll_content);
		mwt_current = mInflater.inflate(R.layout.weather_current, null);
		mPull_down_view = (ImageView) mwt_current.findViewById(R.id.wt_pull_down_flag);
		mPull_up_view = (ImageView) mwt_current.findViewById(R.id.wt_pull_up_flag);

		View transparentView = createTransparentView();
		
		//第一步：添加透明视图scrollview中的linearLayout中
		mLl_content.addView(transparentView);// 
		
		//第二步：添加实况天气布局
		mLl_content.addView(mwt_current);
		
		LinearLayout wether_more = (LinearLayout) mInflater.inflate(R.layout.weather_more, null);
		// 添加自定义温度趋势view到wether_more中
		mTemperatureRangeView = new TemperatureRangeView(mContext);
		mTemperatureRangeView.setpadding(15);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, CommonUtil.getDeviceScreenHeight(mContext)/4);  
		lp.setMargins(0,  CommonUtil.dip2px(mContext, 20), 0,  CommonUtil.dip2px(mContext, 20));
		wether_more.addView(mTemperatureRangeView, lp );
		
		//第三步：添加更多天气视图
		mLl_content.addView(wether_more);// 添加更多天气到scrollview中的linearLayout中
		addView(mPager); // 添加weather_pager到pagerivewItem中
		
		initViews();
	}
	

	private void initViews() {
		mPdDialog = CustomProgressDialog.createDialog(mContext);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		// 主背景
		//miv_MainBack = (ImageView)mMainView.findViewById(R.id.main_blur_bg);
		// 主天气控件初始化
//		miv_wt_icon = (ImageView) mPager.findViewById(R.id.iv_wt);
		mtv_wt = (TextView) mwt_current.findViewById(R.id.tv_wt);
		mtv_wt_now = (TextView) mwt_current.findViewById(R.id.tv_wt_now);
//		mtv_wt_range = (TextView) mPager.findViewById(R.id.tv_wt_range);
		mtv_wt_wind = (TextView) mwt_current.findViewById(R.id.tv_wt_wind);
		mtv_wt_Humidity = (TextView) mwt_current.findViewById(R.id.tv_wt_Humidity);
		mtv_wt_daytime = (TextView) mwt_current.findViewById(R.id.tv_wt_daytime);
		mtv_wt_ob_time = (TextView) mwt_current.findViewById(R.id.tv_wt_ob_time);
		mtv_wt_ob_time.setVisibility(View.GONE);//取消时间显示
		// 警告控件初始化
		miv_warming = (ImageView) mwt_current.findViewById(R.id.iv_Warning);
		mLl_Warning = (LinearLayout) mwt_current.findViewById(R.id.ll_Warning);
		// miv_warming.setVisibility(isHaveWaring?View.VISIBLE:View.GONE);
		miv_warming.setOnClickListener(this);
		mLl_Warning.setOnClickListener(this);
		// 更多天气控件初始化
		mtv_wt_more_tempre = (TextView) findViewById(R.id.tv_wt_more_tepre);
		mtv_wt_more_humidity = (TextView) findViewById(R.id.tv_wt_more_humidity);
		mtv_wt_more_hpa = (TextView) findViewById(R.id.tv_wt_more_hpa);
		mtv_wt_more_windspeed = (TextView) findViewById(R.id.tv_wt_more_wind_speed);
		mtv_wt_more_winddec = (TextView) findViewById(R.id.tv_wt_more_wind_dsc);
		// 日期时间对应控件
		mtv_day1 = (TextView) findViewById(R.id.day1);
		mtv_day2 = (TextView) findViewById(R.id.day2);
		mtv_day3 = (TextView) findViewById(R.id.day3);
		mtv_day4 = (TextView) findViewById(R.id.day4);
		mtv_day5 = (TextView) findViewById(R.id.day5);
		mtv_day6 = (TextView) findViewById(R.id.day6);
		mtv_day7 = (TextView) findViewById(R.id.day7);

		mtv_daynum1 = (TextView) findViewById(R.id.num1);
		mtv_daynum2 = (TextView) findViewById(R.id.num2);
		mtv_daynum3 = (TextView) findViewById(R.id.num3);
		mtv_daynum4 = (TextView) findViewById(R.id.num4);
		mtv_daynum5 = (TextView) findViewById(R.id.num5);
		mtv_daynum6 = (TextView) findViewById(R.id.num6);
		mtv_daynum7 = (TextView) findViewById(R.id.num7);

		// 初始化天气数据
		//setUIdata(mWeather);
		if (setTemperaturesData()) {
			mTemperatureRangeView.setData(tem);
		}
	}

	private boolean setTemperaturesData() {
		if ((mTemperatureList == null) || (mTemperatureList.size() == 0) || (mTemperatureList.size() < 7)) {
			return false;
		}
		try {
			for (int i = 0; i < 7; i++) {
				tem[i][0] = mTemperatureList.get(i).min / 10;
				tem[i][1] = mTemperatureList.get(i).max / 10;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	/**
	 * 创建一个透明的view
	 * 高度为中间内容view的高度 - 实况天气view的高度
	 * @return
	 */
	private View createTransparentView() {
		LinearLayout view = new LinearLayout(getContext());
		view.setBackgroundColor(getResources().getColor(R.color.transparent));
		view.setOrientation(LinearLayout.VERTICAL);
		mwt_current = mInflater.inflate(R.layout.weather_current, null);
		LinearLayout.LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, mTransparentHeight);
		view.setLayoutParams(lp);
		return view;
	}

	public void setData() {
		if(!isLoading){
			task = new LoadDataTask();
			task.execute();	
		}
	}

	public void reloadData() {
		task = new LoadDataTask();
		task.execute(mCity);
	}

	public void releaseData() {
		// weathers.clear();
	}

	/**
	 * 获取activity的宽和高
	 * @param activity
	 * @return
	 */
	public static int[] getDisplay(Activity activity) {
		int[] data = new int[2];
		Display mDisplay = activity.getWindowManager().getDefaultDisplay();
		data[0] = mDisplay.getWidth();
		data[1] = mDisplay.getHeight();
		return data;
	}

	private class LoadDataTask extends AsyncTask<String, Integer, Weather> {

		private boolean mNetStatus = true;
		private String mCityName = null;

		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.show();
			}
		}

		@Override
		protected Weather doInBackground(String... arg0) {
			isLoading = true;
			Weather weather = null;
			if (mCity.equals("自动定位")) {
				isLocation = true;
				mLocalArea = LocalHelper.getInstance(mContext).getCity();
			}
			if (mCity.equals("内蒙古自治区")) {
				mCityName = "呼和浩特市";
			}else {
				mCityName = mCity;
			}
			weather = getWeatherFromNet(mCityName);
			/********先注释这部分********/
			// 查询本地数据库
			/*weather = mCityDao.queryWeather(mCityName);
			// 如果数据库没有，则从服务器获取
			if (weather != null) {
				isHaveWeather = true;
				// 刷新时间跟上次天气时间的时间差在两个小时内就没有必要从服务器拿数据
				if (DateUtiles.isNeedRefresh(weather.ObservTimes)) {
					weather = getWeatherFromNet(mCityName);
				}
			}
			else {
				weather = getWeatherFromNet(mCityName);
			}*/
			/**************************/
			return weather;
		}

		private Weather getWeatherFromNet(String city) {
			Weather weather = null;
			if (!CommonUtil.checkNetState(mContext)) {
				mNetStatus = false;
				return weather;
			}
			String areacode = null;
			if (isLocation) {
				areacode = mCityDao.queryAreacode(mLocalArea);
			} else {
				areacode = mCityDao.queryAreacode(city);
			}
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
			if (isHaveWeather) {
				mCityDao.deleteWeather(city);
			}
			mCityDao.addWeather(weather);
			ExitApplication.getInstance().mWeathers.add(weather);
			isHaveWeather = true;
			return weather;
		}

		@Override
		protected void onPostExecute(Weather result) {
			if (!mNetStatus) {
				CommonUtil.showToask(mContext, "请检查网络连接");
			} else {
				if (result != null) {
					try {
						setUIdata(result);
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			}
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
				
			}
			isLoading = false;
			new LoadForcastdataTask().execute();

		}

	}
	
	private class LoadForcastdataTask extends AsyncTask<String, Integer, List<TempratureRange>> {

		private boolean mNetStatus = true;
		private String mCityName = null;
		private boolean isExist = false; 
	
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.show();
			}
		}
		
		@Override
		protected List<TempratureRange> doInBackground(String... arg0) {
			isLoading = true;
			List<TempratureRange> list = new ArrayList<TempratureRange>();
			if (mCity.equals("自动定位")) {
				isLocation = true;
				mLocalArea = LocalHelper.getInstance(mContext).getCity();
			}
			if (mCity.equals("内蒙古自治区")) {
				mCityName = "呼和浩特市";
			}else {
				mCityName = mCity;
			}
			list = getForcastWeatherFromNet(mCityName);
			/*// 查询本地数据库
			System.out.println("异步加载本地天气数据");
			list = mCityDao.getAllTemperatures(mCityName);
			// 如果数据库没有，则从服务器获取
			if ((list != null)&&(list.size()>0)) {
				isExist = true;
				// 刷新时间跟上次天气时间的时间差在两个小时内就没有必要从服务器拿数据
				if (DateUtiles.isNeedRefresh(list.get(list.size()-1).time)) {
					list = getForcastWeatherFromNet(mCityName);
				}
			}
			else {
				list = getForcastWeatherFromNet(mCityName);
			}*/
			mTemperatureList = list;
			return list;
		}

		private List<TempratureRange> getForcastWeatherFromNet(String city) {
			List<TempratureRange> list = null;
			if (!CommonUtil.checkNetState(mContext)) {
				mNetStatus = false;
				return list;
			}
			String areacode = null;
			if (isLocation) {
				areacode = mCityDao.queryAreacode(mLocalArea);
			} else {
				areacode = mCityDao.queryAreacode(city);
			}
			//用区县的温度趋势
			if (areacode.length()>6) {
				areacode = areacode.substring(0,6);
			}
			//未来七天的预报天气
			List<Weather> listdd = mWeatherDao.getForcastWeather(areacode);
			//今天的预报天气
			Weather todWeather = mWeatherDao.getTodayForcastWeather(areacode, DateUtiles.getLastDay());
			//昨天的实况天气的最大最小温度值
			Weather lastday = mWeatherDao.getLastWeather(areacode, DateUtiles.getLastDay());
			list = new ArrayList<TempratureRange>();
			if (lastday!=null) {
				TempratureRange range = new TempratureRange();
				range.max = lastday.MaxTemperature*10;
				range.min = lastday.MinTemperature*10;
				range.city = city;
				list.add(range);
			}
			else {
				System.out.println("昨天实况天气数据不存在");
				TempratureRange range = new TempratureRange();
				range.max = 0;
				range.min = 0;
				
				range.city = city;
				list.add(range);
			}
			if (todWeather!=null) {
				TempratureRange range = new TempratureRange();
				range.max = todWeather.MaxTemperature;
				range.min = todWeather.MinTemperature;
				range.city = city;
				list.add(range);
			}
			else {
				System.out.println("今天预报天气数据不存在");
				TempratureRange range = new TempratureRange();
				range.max = 0;
				range.min = 0;
				range.city = city;
				list.add(range);
			}
			if ((listdd != null) && (listdd.size() > 0) && (listdd.size() % 2 == 0)) {
				for (int i = 1; i < 10; i += 2) {
					TempratureRange range = new TempratureRange();
					range.max = listdd.get(i).MaxTemperature;
					range.min = listdd.get(i).MinTemperature;
					range.city = city;
					range.time = listdd.get(i).ObservTimes;
					list.add(range);
				}
			}
			else {
				System.out.println("未来5天预报天气数据不存在");
				for(int i = 0;i<5;i++){
					TempratureRange range = new TempratureRange();
					range.max = 0;
					range.min = 0;
					range.city = city;
					list.add(range);
				}
			}
			if (isExist) {
				mCityDao.deleteTemperature(mCityName);
			}
			mCityDao.addTempraRange(list);
			return list;
}

		@Override
		protected void onPostExecute(List<TempratureRange> result) {
			
			if (!mNetStatus) {
//				CommonUtil.showToask(mContext, "请检查网络连接");
			} else {
				
				if (setTemperaturesData()) {
					mTemperatureRangeView.setData(tem);
				}
				
			}
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
				
			}
			isLoading = false;
		}

	}

	private void setUIdata(Weather weather) {

		// 日期时间设置
		mtv_day1.setText(weeks[0]);
		mtv_day2.setText(weeks[1]);
		mtv_day3.setText(weeks[2]);
		mtv_day4.setText(weeks[3]);
		mtv_day5.setText(weeks[4]);
		mtv_day6.setText(weeks[5]);
		mtv_day7.setText(weeks[6]);

		mtv_daynum1.setText(days[0]);
		mtv_daynum2.setText(days[1]);
		mtv_daynum3.setText(days[2]);
		mtv_daynum4.setText(days[3]);
		mtv_daynum5.setText(days[4]);
		mtv_daynum6.setText(days[5]);
		mtv_daynum7.setText(days[6]);

		if (weather == null) {

		} else {
			
			try {
				// 主天气数据设置
				int r_id = WeatherDataUtil.getWeatherDrawbleID(WeatherDataUtil.nowToForcast(weather.WeatherState), DateUtiles.isNignt(weather.ObservTimes));
//			if (r_id != -1) {
//				miv_wt_icon.setBackgroundResource(r_id);
//			}
				mtv_wt.setText(WeatherDataUtil.nowToForcastStr(weather.WeatherState));
				mtv_wt_now.setText(String.valueOf(weather.Temperature));
//			mtv_wt_range.setText(weather.MinTemperature + "~" + weather.MaxTemperature + "℃");
				mtv_wt_wind.setText(mWeatherDataUtil.getWindDirection(Float.parseFloat(weather.WindDirection)) + ""
						+ mWeatherDataUtil.getWindDec(Float.parseFloat(weather.WindSpeed) / 10));
				mtv_wt_Humidity.setText(weather.Humidity + "%");
				mtv_wt_daytime.setText(DateUtiles.getCurrentTime());
				mtv_wt_ob_time.setText(DateUtiles.getCurrentTime(weather.ObservTimes) + "发布");
				
				// 更多天气数据设置
				mtv_wt_more_tempre.setText(weather.Temperature + "℃");
				mtv_wt_more_humidity.setText(weather.Humidity + "%");
				mtv_wt_more_hpa.setText((Float.parseFloat(weather.Pressure) / 10 + ""));
				mtv_wt_more_winddec.setText(mWeatherDataUtil.getWindDirection(Float.parseFloat(weather.WindDirection)) + "  "
						+ mWeatherDataUtil.getWindDec(Float.parseFloat(weather.WindSpeed) / 10));
				mtv_wt_more_windspeed.setText("风速  " + Float.parseFloat(weather.WindSpeed) / 10 + "m/s");
				
				if ((mWarningList != null) && (mWarningList.size() > 0)) {
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(int scrollY) {
		// TODO Auto-generated method stub
		
	}


	

}
