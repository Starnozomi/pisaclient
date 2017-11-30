package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.DragAdapter;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.DragGridView;
import com.supermap.pisaclient.common.views.DragGridView.OnChanageListener;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.TempratureRange;
import com.supermap.pisaclient.entity.Weather;

public class CityAdd extends BaseActivity {
	
	private List<Weather> mWeathers;
	private List<City> mCities;
	private DragAdapter mDragAdapter;
	private DragGridView mDragGridView;
	private int mDefultcity = 0;
	private View mContent;
	private boolean isEdit = false;
	private boolean isDefaultCityClicked = false;
	private int mPosstion = -1;
	private CityDao mCityDao;
	private City city;
	private AreaSelectParameter mAreaSelectParameter = null ;
	//private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
	private CustomProgressDialog mPdDialog;
	//private boolean isRefreshing = false;
	private WeatherDao mWeatherDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		mCityDao = new CityDao(this);
		mCities = Common.myCities;
		//mCities = mCityDao.queryAreaByAreaName("渝北区");
		setTvTitle(Utils.getString(this, R.string.weather_city_add));
		setIsBack(true);
		setIsNavigator(false);
//		setIsMenu(true);
		setIsRefresh(true);
		setIsEdit(true);
		//mWeatherDao = new WeatherDao();
		mContent = inflater(R.layout.cityadd_main);
		setBackOnClickListener(this);
		setEditOnClickListener(this);
		setRefreshOnClickListener(this);
		
		mDragGridView= (DragGridView) mContent.findViewById(R.id.dragGridView);
	    mDragAdapter = new DragAdapter(this, mWeathers, mCities);
		mDragGridView.setAdapter(mDragAdapter);
		
		int citySize = mCities.size();
		String defaltcityString = ExitApplication.getInstance().mDefaultCity;
		if (citySize>0) {
			for(int i = 0;i <citySize ;i++){
				if (mCities.get(i).name.equals(defaltcityString)) {
					mDefultcity = i;//
					break;
				}
			}
		}
		
		
		
		mDragGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long arg3) {
				TextView textView;
				TextView tv_city;
				mPosstion = postion;
				if (isEdit) {
					if ((postion<mCities.size())) {
						tv_city = (TextView)(arg1.findViewById(R.id.tv_cityname));
//						tv_city = (TextView)(arg0.getChildAt(postion).findViewById(R.id.tv_cityname)); //当position>初始化屏幕显示的最大值值 就找不到
						if (tv_city.getText().equals(ExitApplication.getInstance().mDefaultCity)) {
							isDefaultCityClicked =true;
						}
						else {
							isDefaultCityClicked = false;
						}
//						ImageView iv_delete = (ImageView)(arg0.getChildAt(postion).findViewById(R.id.iv_delete_city));
						new AlertDialog.Builder(CityAdd.this).setIcon(R.drawable.iv_delete_city) 
					    .setTitle("编辑").setMessage("是否删除地区"+mCities.get(mPosstion).name+"?")  
					    .setPositiveButton("是",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								City city = mCities.remove(mPosstion);
								mCityDao.deleteCity(city.name);  //这儿删除数据跟 service 里面遍历city有冲突
								if (isDefaultCityClicked) {
									ExitApplication.getInstance().mDefaultCity=null;
								}
								mDragAdapter.notifyDataSetChanged();
								return;
							}
					    })  
					    .setNegativeButton("否",new DialogInterface.OnClickListener() {  
					                public void onClick(DialogInterface dialog,int which) { 
					                	return;
					                }  
					    }).show();
					}
					return;
				}
				
				if(postion == mCities.size()){
					Intent cityset = new Intent();
					if (mAreaSelectParameter==null) {
						mAreaSelectParameter= new AreaSelectParameter();
						mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
						mAreaSelectParameter.isWeatherArea = true;
						mAreaSelectParameter.isSelectMore = true;
						mAreaSelectParameter.isShowRemind = true;
					}
					Bundle bundle = new Bundle();
					bundle.putSerializable("parameter", mAreaSelectParameter);
					cityset.putExtras(bundle);
					cityset.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
					cityset.setClass(CityAdd.this, CitySetActivity.class);
					startActivityForResult(cityset, Constant.CITY_ADD_REQUEST); //之后版本会注释
				} else {//设置默认城市
					textView = (TextView)(arg1.findViewById(R.id.tv_default));
					tv_city = (TextView)(arg1.findViewById(R.id.tv_cityname));
					textView.setPressed(true);
					textView.setText("默认");
					for(int i = 0 ; i<mCities.size(); i++){
						if (mCities.get(i).name.equals(ExitApplication.getInstance().mDefaultCity)) {
							mDefultcity = i;
							break;
						}
					}
					ExitApplication.getInstance().mDefaultCity = (String) tv_city.getText();
					
					if(postion!=mDefultcity){
						mDragAdapter.notifyDataSetChanged();
						mDefultcity = postion;
					}
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("selectedCityName",(String) tv_city.getText()); 
					bundle.putString("selectedCityCode",mCities.get(mDefultcity).areacode);
					intent.putExtras(bundle);
					setResult(999, intent);
					finish();
				}
				
			}
		});
		
		mDragGridView.setOnChangeListener(new OnChanageListener() {
			
			@Override
			public void onChange(int from, int to) {
				
				
				if((from==mCities.size())||(to==mCities.size())){
					//不交换最后一个view
				}
				else {
//					int tem_from = from -1;
//					int tem_to = to-1;
					int tem_from = from;
					int tem_to = to;
					City temp = mCities.get(tem_from);
					//这里的处理需要注意下
					if(from < to){
						for(int i=tem_from; i<tem_to; i++){
							Collections.swap(mCities, i, i+1);
						}
					}else if(from > to){
						for(int i=tem_from; i>tem_to; i--){
							Collections.swap(mCities, i, i-1);
						}
					}
					mCities.set(tem_to, temp);
				}
				
				
				//设置新到的item隐藏，不用调用notifyDataSetChanged来刷新界面，因为setItemHide方法里面调用了
				mDragAdapter.setItemHide(to);
				
			}
		});
		
		mPdDialog = CustomProgressDialog.createDialog(this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		LocalHelper.getInstance(this).init();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			if(mCities == null || mCities.size() == 0){
				//mCityDao.addCity(city);
				mCities.add(mCityDao.queryAreaByAreaName("昆明市"));
			} else {
					mCityDao.deleteAll();
					for(int i = 0; i<mCities.size(); i++){
						city = new City();
						city.areaid = mCities.get(i).areaid;
						city.name = mCities.get(i).name;
						city.areacode = mCities.get(i).areacode;
						city.prarentId = mCities.get(i).prarentId;
						mCityDao.addCity(city);
				}
			}
			Common.myCities = mCityDao.getAllCity();
			finish();
			break;
		case R.id.ibtn_edit:
			isEdit = !isEdit;
			mDragAdapter.setEdit(isEdit);
			mDragAdapter.notifyDataSetChanged();
			break;
		case R.id.ibtn_refresh:
			new LoadDataTask().execute();
			break;
		default:
				break;
		}
		super.onClick(v);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==Constant.CITY_SET_RESULT) {
			List<City> list  = (List<City>)data.getSerializableExtra("city");
			//mCities.addAll(list);
			mCities = mCityDao.getAllCity();
			mCities.addAll(list);
			mCityDao.addAllCityTocitys(mCities);
			mCities = mCityDao.getAllCity();
			mDragAdapter.setCitys(mCities);
			//new LoadDataTask().execute();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		ExitApplication.getInstance().mCities = mCities;
		super.onPause();
	}
	@Override
	public void onBackPressed() {
		if (mCities.size()==0) {
			super.onBackPressed();
			ExitApplication.getInstance().mCities = mCities;
			ExitApplication.getInstance().saveCityMsg();
			ExitApplication.getInstance().exit(0);
			return;
		}
		if (isEdit) {
			isEdit = !isEdit;
			mDragAdapter.setEdit(isEdit);
			mDragAdapter.notifyDataSetChanged();
		}
		else {
			super.onBackPressed();
		}
	
	}
	
	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(this).close();
		ExitApplication.getInstance().saveCityMsg();
		super.onDestroy();
	}
	
	private class LoadDataTask extends AsyncTask<String, Integer, List<Weather>>{
		
		private boolean  mNetStatus = true;
		private boolean isLocation = false; 
		private boolean isHaveWeather = false;
		private String mCity;
		private String mLocalArea;
		private WeatherDao mWeatherDao;
		private List<TempratureRange> mTemperatureList = null;
		private List<Weather> mWeathersList = null;
		private List<List<TempratureRange>> mTempreatureList = null;
		
		public LoadDataTask(){
			mWeatherDao = new WeatherDao();
		}
		
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()){
				mPdDialog.show();
			}
				
			
		}
		@Override
		protected List<Weather> doInBackground(String... arg0) {
			
			if ((mCities==null)||(mCities.size()==0)) {
				return null;
			}
			
			Weather weather = null;
			mWeathersList = new ArrayList<Weather>();
			
			for(City city:mCities) {
				isHaveWeather = false;
				String mCityName = null;
				if (city.equals("自动定位")) {
					isLocation = true;
					mLocalArea = LocalHelper.getInstance(CityAdd.this).getCity();
				}
				
				if (city.name.equals("内蒙古自治区")) {
					mCityName = "呼和浩特市";
				}else {
					mCityName = city.name;
				}
				// 查询本地数据库
				weather = mCityDao.queryWeather(mCityName);
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
				}
			
				if (weather!=null) {
					mWeathersList.add(weather);
				}
			}
			
			return mWeathersList;
		}
		
		private Weather getWeatherFromNet(String city){
			Weather weather = null;
			if (!CommonUtil.checkNetState(CityAdd.this)) {
				mNetStatus = false;
				return weather;
			}
			String areacode = null;
			if (isLocation) {
				areacode = mCityDao.queryAreacode(mLocalArea);
			} else {
				areacode = mCityDao.queryAreacode(city);
			}
			if ((weather == null)&&(areacode.length()>6)) {
				weather = mWeatherDao.getByCity(areacode.substring(0, 6), DateUtiles.getCurrentTimeST());
				if (weather!=null) {
					weather.city = mCityDao.queryCityName(areacode);
				}
			}
			if (weather==null) {
				return null;
			}
			if (isHaveWeather) {
				mCityDao.deleteWeather(city);
			}
			mCityDao.addWeather(weather);
			isHaveWeather = true;
			return weather;
		}

		@Override
		protected void onPostExecute(List<Weather> result) {
			if (!mNetStatus) {
				CommonUtil.showToask(CityAdd.this, "请检查网络连接");
			}else {
				if(result != null){
					mDragAdapter.setWeathers(result);
					if (result.size()>0) {
						if (!DateUtiles.isNeedRefresh(result.get(result.size()-1).ObservTimes)) {
							CommonUtil.showToask(CityAdd.this, "已经是最新数据");
						}
					}
					
				}
			}
			
			if (mPdDialog.isShowing()){
				mPdDialog.dismiss();
			}
			new LoadForcastdataTask().execute();
				
		}
		
	}
	
	private class LoadForcastdataTask extends AsyncTask<String, Integer, Boolean> {

		private boolean mNetStatus = true;
		private String mCityName = null;
		private boolean isExist = false; 
		private boolean isLocation = false;
		private String mLocalArea = null;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			for(City city:mCities){
				String mCityName = null;
				List<TempratureRange> list = new ArrayList<TempratureRange>();
				if (city.name.equals("自动定位")) {
					isLocation = true;
					mLocalArea = LocalHelper.getInstance(CityAdd.this).getCity();
				}
				if (city.name.equals("内蒙古自治区")) {
					mCityName = "呼和浩特市";
				}else {
					mCityName = city.name;
				}
				// 查询本地数据库
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
				}
			}
			return true;
		}

		private List<TempratureRange> getForcastWeatherFromNet(String city) {
			List<TempratureRange> list = null;
			if (!CommonUtil.checkNetState(CityAdd.this)) {
				mNetStatus = false;
				return list;
			}
			String areacode = null;
			if (isLocation) {
				areacode = mCityDao.queryAreacode(mLocalArea);
			} else {
				areacode = mCityDao.queryAreacode(city);
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
		protected void onPostExecute(Boolean result) {
			if (!mNetStatus) {
//				CommonUtil.showToask(mContext, "请检查网络连接");
			} else {
				
			}
		}

	}
	
}
