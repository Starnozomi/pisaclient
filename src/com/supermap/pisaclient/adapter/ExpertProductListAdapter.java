/* 
 * Copyright 2013 Share.Ltd  mAllProducts rights reserved.
 * 
 * @ProductListAdapter.java - 2014-3-25 上午11:12:27
 */

package com.supermap.pisaclient.adapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.ui.ProductDetailActivity;
import com.supermap.pisaclient.ui.ProductExpertDetailActivity;

public class ExpertProductListAdapter extends BaseAdapter {

	private static final String TAG = "ExpertProductListAdapter";
	private Context mContext;
	private List<ExpertProduct> mAllProducts;
	private LayoutInflater mInflater;

	public ExpertProductListAdapter(Context mContext, List<ExpertProduct> mAllProducts) {
		this.mContext = mContext;
		this.mAllProducts = mAllProducts;

	}


	@Override
	public int getCount() {
		return mAllProducts.size();
	}

	@Override
	public Object getItem(int position) {
		return mAllProducts.get(position);
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.product_expert_item, null);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_create_time);
			holder.tvCrop = (TextView)convertView.findViewById(R.id.tv_crop);
			holder.tvExpert = (TextView)convertView.findViewById(R.id.tv_expert);
			holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
			holder.llItem = (LinearLayout)convertView.findViewById(R.id.ll_item);
			holder.tvFarmlandname  = (TextView)convertView.findViewById(R.id.tv_farmlandname);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ExpertProduct product = mAllProducts.get(position);
		holder.tvTitle.setText(product.title);
		
		holder.tvCrop.setText(product.crop);
		holder.tvExpert.setText(product.expert);
		holder.tvContent.setText(product.content);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date(Long.parseLong(product.createTime));
	    holder.tvTime.setText(sdf.format(date));
	    holder.tvFarmlandname.setText(product.farmlandName);
	    //product.createTime = sdf.format(date);
	    final String createTime =  sdf.format(date);
		if (position % 2 == 0) {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			//holder.tvCreateTime.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
		} else {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			//holder.tvCreateTime.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProductExpertDetailActivity.class);
				Bundle mBundle = new Bundle();
				
				mBundle.putString("title", product.title);
				mBundle.putString("content", product.content);
				mBundle.putString("time", createTime);
				mBundle.putString("crop", product.crop);
				mBundle.putString("expert", product.expert);
				mBundle.putString("farmlandname", product.farmlandName);
				intent.putExtras(mBundle);
				mContext.startActivity(intent);
			}

		});
		
		return convertView;
	}

	private class ViewHolder {
		public LinearLayout llItem;
		public TextView tvTitle;
		public TextView tvCrop;
		public TextView tvTime;
		public TextView tvExpert;
		public TextView tvContent;
		public TextView tvFarmlandname;
	}

}
