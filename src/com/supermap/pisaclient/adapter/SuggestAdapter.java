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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.ui.SuggestDetailActivity;

public class SuggestAdapter extends BaseAdapter {

	private List<Suggest> all;
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageLoader mLoader;

	public SuggestAdapter(Context context, List<Suggest> all) {
		this.mContext = context;
		this.all = all;
		mLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return all.size();
	}
	
	public void setdata(List<Suggest> all){
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.suggest_item, null);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
			holder.tvAreaName = (TextView) convertView.findViewById(R.id.tv_areaname);
			holder.tvStime = (TextView) convertView.findViewById(R.id.tv_stime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Suggest suggest = all.get(position);
		holder.tvTitle.setText(suggest.title);
		holder.tvInfo.setText(suggest.info);
		holder.tvAreaName.setText("适合:" + suggest.areaname);
		holder.tvStime.setText(suggest.stime);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SuggestDetailActivity.class);
				intent.putExtra("id", suggest.id);
				mContext.startActivity(intent);
			}

		});
		return convertView;
	}

	private class ViewHolder {
		TextView tvTitle;
		TextView tvInfo;
		TextView tvAreaName;
		TextView tvStime;
	}

}
