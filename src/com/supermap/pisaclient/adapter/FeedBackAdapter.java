/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommentAdapter.java - 2014-4-4 下午5:31:27
 */

package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.FeedBack;

public class FeedBackAdapter extends BaseAdapter {

	private List<FeedBack> all;
	private LayoutInflater mInflater;
	private Context mContext;

	public FeedBackAdapter(Context context, List<FeedBack> all) {
		this.mContext = context;
		this.all = all;
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.feed_item, null);
			holder.llFeedback = (LinearLayout) convertView.findViewById(R.id.ll_feedback_item);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
			holder.tvStime = (TextView) convertView.findViewById(R.id.tv_stime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FeedBack feed = all.get(position);
		holder.tvInfo.setText(feed.info);
		holder.tvStime.setText(feed.stime);
		if (position % 2 == 0) {
			holder.llFeedback.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
			holder.tvInfo.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			holder.tvStime.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
		} else {
			holder.llFeedback.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
			holder.tvInfo.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			holder.tvStime.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout llFeedback;
		TextView tvInfo;
		TextView tvStime;
	}

}
