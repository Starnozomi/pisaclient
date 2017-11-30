package com.supermap.pisaclient.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.supermap.pisaclient.common.views.WeatherViewPagerItemView;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.ui.WeatherActivity;

import android.R.integer;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CityChangePagerAdapter extends PagerAdapter {
		private List<City> mCitys;
		private  HashMap<Integer, WeatherViewPagerItemView> mHashMap  = new HashMap<Integer, WeatherViewPagerItemView>();;
		private WeatherActivity mContext;
		private boolean isLoadData[];
		private CityDao mCityDao;
		
		public CityChangePagerAdapter(Context context,List<City> citys,List<Weather> weathers) {
			this.mCitys = citys;
			this.mContext = (WeatherActivity) context;
			refeshData(citys);
			mCityDao = new CityDao(mContext);
			
		}

		@Override
		public void destroyItem(View arg0, int position, Object arg2) {
			WeatherViewPagerItemView view  = mHashMap.get(position);
			view.releaseData();
			((ViewPager) arg0).removeView(view);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mCitys.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			
			WeatherViewPagerItemView itemView; 
			String city = mCitys.get(position).name;
	        if(mHashMap.containsKey(city)){  
	            itemView = mHashMap.get(position);  
	        }else{  
	            itemView = new WeatherViewPagerItemView(mContext,city);
	            mHashMap.put(position, itemView);  
	        }  
	        ((ViewPager) container).addView(itemView); 
	        return itemView; 
		}
		
		@Override
		public void setPrimaryItem(View container, int position, Object object) {
//			String city = mCitys.get(position).name;
			if((isLoadData!=null)&&(!isLoadData[position])){//只加载一次
				mHashMap.get(position).setData();
				isLoadData[position]=true;
			}
//			String city = mCitys.get(position).name;
//			if (city.equals("")) {
//				
//			}
//			mCityDao.queryWeather(city)
			
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
		
		
		public void refresh(int mPageIndex){
			mHashMap.get(mPageIndex).setData();
		}
		
		public void scrollTo(int mPageIndex,int scroll){
			mHashMap.get(mPageIndex).smoothScrollTo(0, scroll);
		}
		
		public void scrollAllTo(int mPageIndex, int scroll){
			int size = mHashMap.size();
			if (size<=0) {
				return;
			}
			for(int i = 0 ;i<size; i++){
				if ((i!=mPageIndex)&&(mHashMap.get(i)!=null)) {
					mHashMap.get(i).smoothScrollTo(0, scroll);
				}
			}
		}
		
		public  void refeshData(List<City> list){
			mCitys = list;
			if(mCitys.size()>0){
				isLoadData = new boolean[mCitys.size()];
				for(boolean isloadData:isLoadData){
					isloadData = false;
				}
			}
			notifyDataSetChanged();
		}
		
		
	}

