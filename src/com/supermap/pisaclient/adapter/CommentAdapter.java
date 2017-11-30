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
import com.supermap.pisaclient.entity.ProductComment;

public class CommentAdapter extends BaseAdapter {

	private List<ProductComment> all;
	private LayoutInflater mInflater;
	private Context mContext;

	public CommentAdapter(Context context, List<ProductComment> all) {
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
			convertView = (LinearLayout) mInflater.inflate(R.layout.comment_item, null);
			holder.llCommentItem = (LinearLayout) convertView.findViewById(R.id.ll_comment_item);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment_name);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_comment_user);
			holder.tvCommentTime = (TextView) convertView.findViewById(R.id.tv_comment_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProductComment comment = all.get(position);
		holder.tvComment.setText(comment.comment);
		holder.tvUserName.setText(comment.userName);
		holder.tvCommentTime.setText(comment.commentTime);
		if (position % 2 == 0) {
			holder.llCommentItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
			holder.tvComment.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			holder.tvUserName.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			holder.tvCommentTime.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
		} else {
			holder.llCommentItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
			holder.tvComment.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			holder.tvUserName.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			holder.tvCommentTime.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout llCommentItem;
		TextView tvComment;
		TextView tvUserName;
		TextView tvCommentTime;
	}

}
