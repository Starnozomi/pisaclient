/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommentAdapter.java - 2014-4-4 下午5:31:27
 */

package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.entity.Science;
import com.supermap.pisaclient.ui.ScienceDetailActivity;

public class ScienceAdapter extends BaseAdapter {

	private List<Science> all;
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageLoader mLoader;

	public ScienceAdapter(Context context, List<Science> all) {
		this.mContext = context;
		this.all = all;
		mLoader = new ImageLoader(context);
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.science_item, null);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_science_item);
			holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
			holder.tvStime = (TextView) convertView.findViewById(R.id.tv_stime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Science science = all.get(position);
		String url = CommonImageUtil.getImageUrl(science.pic);
		mLoader.DisplayImage(url, holder.ivPic, false);
		holder.tvTitle.setText(science.title);
		holder.tvInfo.setText(science.info);
		holder.tvStime.setText(science.stime);
		if (position % 2 == 0) {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
		} else {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ScienceDetailActivity.class);
				intent.putExtra("id", science.id);
				mContext.startActivity(intent);
			}

		});
		return convertView;
	}

	private class ViewHolder {
		LinearLayout llItem;
		ImageView ivPic;
		TextView tvTitle;
		TextView tvInfo;
		TextView tvStime;
	}

}
