/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommentAdapter.java - 2014-4-4 下午5:31:27
 */

package com.supermap.pisaclient.adapter;

import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.Science;
import com.supermap.pisaclient.ui.AdvDetailActivity;
import com.supermap.pisaclient.ui.CropUploadActivity;
import com.supermap.pisaclient.ui.MessageCenterActivity;
import com.supermap.pisaclient.ui.ScienceDetailActivity;
import com.supermap.pisaclient.ui.WeatherActivity;
import com.supermap.services.components.Map;

public class FactorRangeAdapter extends BaseAdapter {

	private List<HashMap<String, String>> all;
	private LayoutInflater mInflater;
	private Context mContext;
	private CityDao mCityDao;
	private int mMsgid = 0;
	private MessageUploadDao messageUploadDao;
	public FactorRangeAdapter(Context context, List<HashMap<String, String>> all) {
		this.mContext = context;
		this.all = all;
		this.mCityDao = new CityDao(context);
		messageUploadDao = new MessageUploadDao();
	}

	@Override
	public int getCount() {
		return all.size();
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
			convertView = mInflater.inflate(R.layout.suggest_factor_item, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_factor_name);
			holder.tvRange = (TextView) convertView.findViewById(R.id.tv_factor_range);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(all.get(position).get("name"));
		holder.tvRange.setText(all.get(position).get("range"));
		return convertView;
	}

	private class ViewHolder {
		TextView tvName;
		TextView tvRange;
	}
	

}
