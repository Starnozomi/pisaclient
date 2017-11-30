/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommentAdapter.java - 2014-4-4 下午5:31:27
 */

package com.supermap.pisaclient.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.entity.Material;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.ui.MaterialActivity;
import com.supermap.pisaclient.ui.SuggestDetailActivity;

public class MaterialAdapter extends BaseAdapter {

	private List<Material> all;
	private LayoutInflater mInflater;
	private MaterialActivity mContext;
	private ImageLoader mLoader;

	public MaterialAdapter(MaterialActivity context, List<Material> all) {
		this.mContext = context;
		this.all = all;
		mLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return all.size();
	}
	
	public void setdata(List<Material> all){
		if ((all!=null)&&(all.size()>0)) {
			this.all = all;
			notifyDataSetChanged();
		}
	}

	@Override
	public Object getItem(int position) {
		return all.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (LinearLayout) mInflater.inflate(R.layout.material_item, null);
			holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
			holder.tvGroup = (TextView) convertView.findViewById(R.id.tv_group);
			holder.tvLatestValue = (TextView) convertView.findViewById(R.id.tv_latest_value);
			holder.tvLatestTime = (TextView) convertView.findViewById(R.id.tv_latest_time);
			
			holder.switcher = (Switch)convertView.findViewById(R.id.switcher);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Material material = all.get(position);
		holder.tvName.setText(material.name);
		holder.tvGroup.setText("（"+material.group+"）");
		holder.tvLatestValue.setText(material.latestValue);
		holder.tvLatestTime.setText("（"+material.latestTime+"）");
		
		holder.switcher.setOnCheckedChangeListener(mContext);
		if(material.name.equals("三相控制器")){
			holder.switcher.setVisibility(View.VISIBLE);
			boolean flag = material.feed == 0 ? false : true;
			holder.switcher.setChecked(flag);
			holder.switcher.setTag(material.id);
		}else{
			holder.switcher.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvGroup;
		TextView tvLatestValue;
		TextView tvLatestTime;
		Switch switcher; 
	}
	
	

}
