/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @AdvisoryTypeAdapter.java - 2014-4-2 上午10:56:41
 */

package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.Subject;

public class AdvisoryTypeAdapter extends BaseAdapter {

	private List<Subject> all;
	private LayoutInflater mInflater;
	private Context mContext;

	public AdvisoryTypeAdapter(Context context, List<Subject> all) {
		this.mContext = context;
		this.all = all;
	}

	@Override
	public int getCount() {
		if(all!=null){
			return all.size();
		}
		else {
			return 0;
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.advisory_type_item, null);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvTitle.setText(Html.fromHtml("<u>"+all.get(position).name+"<u>"));
		return convertView;
	}

	private class ViewHolder {
		public TextView tvTitle;
	}
}
