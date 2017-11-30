/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @MainActivity.java - 2014-3-21 上午10:29:37
 */

package com.supermap.pisaclient.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CityChangePagerAdapter;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.Weather;

public class WeatherActivity extends BaseActivity {

	
	private WeatherActivity oThis = this;
	//背景view
	private ImageView miv_MainBack;
	private View mContent;
	private ViewPager mPager;// 页卡内容
	private int mPageIndex = 0;
	private List<City> mCitys;
	private List<Weather> mWeathers;
	private String mDefaultCity;
	private String mCurrentCity;
	public CityDao mCityDao;
	private CityChangePagerAdapter mAdapter = null;
	public int contentHeight = 0;
	private TextView mtv_city;
	private long firstTime = 0;
	public int mTrans = 0;
	private int mScroll = 0;
	
	
	
	
	private void setTitleIcon(){
		Drawable img = getResources().getDrawable(R.drawable.menu_add_selector);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		TextView title = (TextView)findViewById(R.id.tv_title);
		title.setCompoundDrawables(img, null, null, null); //设置左图标
		title.setCompoundDrawablePadding(10);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		LocalHelper.getInstance(WeatherActivity.this).init();
		//CommonUtil.checkUpdate(this);
		setTvTitle(Utils.getString(this, R.string.weather_city));
		setTitleIcon();
		setIsRefresh(true);
		setIsBack(true);
		setRefreshOnClickListener(this);
		setRgNavigator(R.id.rad_weather);
		mtv_city = (TextView) findViewById(R.id.tv_title);
		mtv_city.setOnClickListener(this);
		

		mContent = inflater(R.layout.weather_main);
		miv_MainBack = (ImageView)mContent.findViewById(R.id.main_blur_bg);
		
		contentHeight = getContentHeight();
		
		mDefaultCity = ExitApplication.getInstance().mDefaultCity;
		mCityDao = new CityDao(WeatherActivity.this);
		new LoadAllCityTask().execute();
		
		//mCitys =   mCityDao.getAllCity();
		//mWeathers = mCityDao.getAllWeathers();
		//new LoadWeatherDataTask().execute();
		
		/*InitViewPager();
		setDefaultPager();
		
		mAdapter.refeshData(mCitys);
		setCurrentCity();
		//mAdapter.notifyDataSetChanged();
		
		if (mCitys.size() == 0) {
			Intent intent = new Intent();
			AreaSelectParameter mAreaSelectParameter = null;
			if (mAreaSelectParameter == null) {
				mAreaSelectParameter = new AreaSelectParameter();
				mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
				mAreaSelectParameter.isWeatherArea = true;
				mAreaSelectParameter.isSelectMore = true;
				mAreaSelectParameter.isShowRemind = true;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("parameter", mAreaSelectParameter);
			intent.putExtras(bundle);
			intent.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
			intent.setClass(WeatherActivity.this, CitySetActivity.class);
			startActivityForResult(intent, Constant.CITY_ADD_REQUEST);
		}
		
		setback(mScroll, mTrans);*/
	}
	
	private class LoadAllCityTask extends AsyncTask<String,Integer,List<City>>{

		@Override
		protected List<City> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mCityDao.getAllCity();
		}
		
		@Override
		protected void onPostExecute(List<City> result) {
			mCitys =result;
			new LoadWeatherDataTask().execute();
		}
	}
	
	
	
	private class LoadWeatherDataTask extends AsyncTask<String, Integer, List<Weather>> {

		@Override
		protected List<Weather> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mCityDao.getAllWeathers();
		}
		
		@Override
		protected void onPostExecute(List<Weather> result) {
			mWeathers = result;
			InitViewPager();
			setDefaultPager();
			mAdapter.refeshData(mCitys);
			setCurrentCity();
			if (mCitys.size() == 0) {
				Intent intent = new Intent();
				AreaSelectParameter mAreaSelectParameter = null;
				if (mAreaSelectParameter == null) {
					mAreaSelectParameter = new AreaSelectParameter();
					mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
					mAreaSelectParameter.isWeatherArea = true;
					mAreaSelectParameter.isSelectMore = true;
					mAreaSelectParameter.isShowRemind = true;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("parameter", mAreaSelectParameter);
				intent.putExtras(bundle);
				intent.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
				intent.setClass(WeatherActivity.this, CitySetActivity.class);
				startActivityForResult(intent, Constant.CITY_ADD_REQUEST);
			}
			
			setback(mScroll, mTrans);
			
		}
		
	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constant.CITY_SET_RESULT) {
			List<City> list = (List<City>) data.getSerializableExtra("city");
			mCitys.addAll(list);
			mCityDao.addAllCityTocitys(list);
			// City city = (City) data.getSerializableExtra("city");
			// mCitys.add(city);
			// mCityDao.addCity(city);
			mAdapter.notifyDataSetChanged();
			setCurrentCity();
		}
		if (mCitys.size() == 0) {
			// ExitApplication.getInstance().exit(0);
			finish();
		}
	}

	
	@SuppressLint("NewApi")
	public void setback(int scrollY,int height){
		float x = scrollY;
		float y;
		if (height==0) {
			y=0;
		}
		else {
			y = x/height;
		}
		mScroll = scrollY;
		mAdapter.scrollAllTo(mPageIndex, scrollY);
		
		if (y>1) {
			y =1;
		}
		int alpha = (int) (255*y);
		if (Build.VERSION.SDK_INT >= 16) 
	    {
	            // 包含新API的代码块
			miv_MainBack.setAlpha(y);
//			setAlpha_16(alpha);
	    }
	    else
	    {
	            // 包含旧的API的代码块
	    	miv_MainBack.setAlpha(alpha);
	    }
	}
	 
//		@TargetApi(19)
//	 private void setAlpha_16(int y){
//			miv_MainBack.setImageAlpha(y);
//	 }
	
	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		mAdapter = new CityChangePagerAdapter(this, mCitys, mWeathers);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		setTvTitle(Utils.getString(WeatherActivity.this, R.string.weather_city));
	}

	private void setDefaultPager() {
		if (mDefaultCity == null) {
			return;
		}
		int temsize = mCitys.size();
		if (temsize > 0) {
			for (int i = 0; i < temsize; i++) {
				if (mCitys.get(i).name.equals(mDefaultCity)) {
					mPager.setCurrentItem(i);
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title:
			Intent intent = new Intent(WeatherActivity.this, CityAdd.class);
			startActivity(intent);
			break;
		case R.id.ibtn_refresh:
			mAdapter.refresh(mPageIndex);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@SuppressLint("NewApi")
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			
			mPageIndex = arg0;
			mCurrentCity = mCitys.get(mPageIndex).name;
			if (mCurrentCity.equals("自动定位")) {
				setTvTitle(mCurrentCity);
				new LocatedAreaTask().execute();
			} else {
				setTvTitle(mCurrentCity);
			}
			if (mCitys.size() == 0) {
				setTvTitle(Utils.getString(WeatherActivity.this, R.string.weather_city));
			}
			setback(mScroll, mTrans);
		}
	}

	@Override
	protected void onPause() {
		ExitApplication.getInstance().mCities = mCitys;
		super.onPause();
	}

	@Override
	protected void onResume() {

		/*mCitys =   mCityDao.getAllCity();
		if (mCitys.size() == 0) {

			InitViewPager();
			super.onResume();
			return;
		}*/
		/*mAdapter.refeshData(mCitys);
		setCurrentCity();
		mAdapter.notifyDataSetChanged();*/
		super.onResume();
		setCurrentCity();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// ExitApplication.getInstance().saveWeatherMsg();
		LocalHelper.getInstance(WeatherActivity.this).close();
		super.onDestroy();

	}

	public void setLocationCityName(String city) {
		setTvTitle("自动定位(" + city + ")");
	}

	private void setCurrentCity() {

		// 以前没地区，或者以前显示的地区被删除了，显示默认地区
		if(mCitys == null) return;
		for (int i = 0; i < mCitys.size(); i++) {
			if (mCitys.get(i).name.equals(ExitApplication.getInstance().mDefaultCity)) {
				mPageIndex = i;
				mPager.setCurrentItem(i);
				mAdapter.notifyDataSetChanged();
				mCurrentCity = mCitys.get(i).name;
				setTvTitle(mCurrentCity);
				return;
			}
		}

		if (mCurrentCity != null) {// 首先显示以前滑到的地区
			for (int i = 0; i < mCitys.size(); i++) {
				if (mCitys.get(i).name.equals(mCurrentCity)) {
					mPageIndex = i;
					mPager.setCurrentItem(i);
					mAdapter.notifyDataSetChanged();
					mCurrentCity = mCitys.get(i).name;
					setTvTitle(mCurrentCity);
					return;
				}
			}
		}

		// 如果连默认地区也删除了，就显示第一个地区
		mPageIndex = 0;
		mPager.setCurrentItem(0);
		mAdapter.notifyDataSetChanged();
		if (mCitys.size() > 0) {
			mCurrentCity = mCitys.get(0).name;
			setTvTitle(mCurrentCity);
		}
		return;
	}

	private class LocatedAreaTask extends AsyncTask<String, Integer, City> {
		private boolean mLocated = false;
		private String mName = null;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected City doInBackground(String... arg0) {
			City result = null;
			mName = LocalHelper.getInstance(WeatherActivity.this).getCity();
			if (!mName.equals("未定位到城市")) {// 定位成功
				mLocated = true;
				result = mCityDao.queryAreaByAreaName(mName);
			}
			return result;
		}

		@Override
		protected void onPostExecute(City result) {
			if (mLocated && (result == null)) {
				// Toast.makeText(CitySetActivity.this, "地区"+mName+"不在服务范围，请从新选择地区", Toast.LENGTH_SHORT).show();
			}
			if (result != null) {
				setLocationCityName(result.name);
			}

		}
	}

	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				CommonUtil.showToask(WeatherActivity.this, getResources().getString(R.string.exit_wait));
				firstTime = secondTime;
				return true;
			} else {
				ExitApplication.getInstance().exit(0);
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/

}
