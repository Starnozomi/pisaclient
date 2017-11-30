/* 
 * Copyright 2013 Share.Ltd  mAllProducts rights reserved.
 * 
 * @ProductListAdapter.java - 2014-3-25 上午11:12:27
 */

package com.supermap.pisaclient.adapter;

import java.util.List;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.service.SpecialMap;
import com.supermap.pisaclient.ui.ProductDetailActivity;

public class ServiceProductListAdapter extends BaseAdapter {

	
	private Context mContext;
	private List<SpecialMap> mAllProducts;
	private LayoutInflater mInflater;

	public ServiceProductListAdapter(Context mContext, List<SpecialMap> mAllProducts) {
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
			convertView = (RelativeLayout) mInflater.inflate(R.layout.service_product_item, null);
			holder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final SpecialMap product = mAllProducts.get(position);
		holder.tvTitle.setText(product.title);
		
		/*convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}

		});*/
		return convertView;
	}

	private class ViewHolder {
		private RelativeLayout rl_item;
		public TextView tvTitle;
	}

}
