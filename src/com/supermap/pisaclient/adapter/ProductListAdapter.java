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
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.ui.ProductDetailActivity;

public class ProductListAdapter extends BaseAdapter {

	private static final String TAG = "ProductListAdapter";
	private Context mContext;
	private List<Product> mAllProducts;
	private LayoutInflater mInflater;
	private boolean mBusy = false;
	private ImageLoader mImageLoader;
	private int type = 0;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public ProductListAdapter(Context mContext, List<Product> mAllProducts, int type) {
		this.mContext = mContext;
		this.mAllProducts = mAllProducts;
		this.type = type;
		mImageLoader = new ImageLoader(mContext);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.product_item, null);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_item);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
			holder.ivTitle = (ImageView) convertView.findViewById(R.id.iv_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Product product = mAllProducts.get(position);
		holder.tvTitle.setText(product.ProductSummary);
		holder.tvCreateTime.setText(product.CreateTime);
		if (product.SmallUrl != null) {
			if (!mBusy) {
				mImageLoader.DisplayImage(CommonImageUtil.getImageUrl(product.SmallUrl), holder.ivTitle, false);
			} else {
				mImageLoader.DisplayImage(CommonImageUtil.getImageUrl(product.SmallUrl), holder.ivTitle, false);
			}
		} else {
			holder.ivTitle.setVisibility(View.GONE);
		}
		if (position % 2 == 0) {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			holder.tvCreateTime.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
		} else {
			holder.llItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			holder.tvCreateTime.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProductDetailActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putInt("type", type);
				mBundle.putParcelable("product", product);
				intent.putExtras(mBundle);
				mContext.startActivity(intent);
			}

		});
		return convertView;
	}

	private class ViewHolder {
		private LinearLayout llItem;
		public TextView tvTitle;
		public TextView tvCreateTime;
		public ImageView ivTitle;
	}

}
