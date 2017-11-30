package com.supermap.pisaclient.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.ui.CitySetActivity;

public class CitySetAdapter extends BaseAdapter {
	private CitySetActivity mContext;
	private List<City> mDataList;
	private LayoutInflater mInflater;
	
	public CitySetAdapter(Context context,List<City> list){
		
		this.mContext = (CitySetActivity) context;
		this.mDataList = list;
		mInflater = LayoutInflater.from(context);
	}

	public void setData(List<City> list){
		this.mDataList = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return super.getItemViewType(position);
	}
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return super.getViewTypeCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
//		ViewHolder viewHolder = null;
		City city  = mDataList.get(position);
//		if (null == convertView) {
//			convertView = mInflater.inflate(R.layout.city_set_cityitem, null);
//			viewHolder = new ViewHolder();
//			viewHolder.content = (TextView) convertView.findViewById(R.id.tv_cityset_cityname);
//			viewHolder.contentIcon = (ImageView) convertView.findViewById(R.id.iv_cityset_isselected);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
//
//		viewHolder.content.setText(city.name);
//		System.out.println(position+"====="+city.name);
//		for(City city2:mContext.mCities ){
////			viewHolder.contentIcon.setVisibility(View.GONE);
//			if (city.name.equals(city2.name)) {
//				System.out.println(position+"====="+city.name+"设置背景");
//				viewHolder.contentIcon.setVisibility(View.VISIBLE);
//			}
//		}
		convertView = mInflater.inflate(R.layout.city_set_cityitem, null);
		TextView cityName = (TextView) convertView.findViewById(R.id.tv_cityset_cityname);
		ImageView isSelect = (ImageView) convertView.findViewById(R.id.iv_cityset_isselected);
		cityName.setText(city.name);
		for(City city2:mContext.mCities ){
			if (city.name.equals(city2.name)) {
				if ((mContext.mParameter!=null)&&(mContext.mParameter.isShowRemind)) {
					isSelect.setVisibility(View.VISIBLE);
				}
				
			}
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView content;
		ImageView contentIcon;
	}

}
