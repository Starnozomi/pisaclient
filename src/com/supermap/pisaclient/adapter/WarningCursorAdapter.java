package com.supermap.pisaclient.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.WeatherDataUtil;

public class WarningCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private Cursor mCursor;
	
	public WarningCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		mCursor = c;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor arg2) {
		//把数据设置到界面上
		  ViewHolder holder = (ViewHolder) view.getTag();
		  String  type = mCursor.getString(mCursor.getColumnIndex("type"));
		  String  level = mCursor.getString(mCursor.getColumnIndex("level"));
		  String title = mCursor.getString(mCursor.getColumnIndex("title"));
		  String time = mCursor.getString(mCursor.getColumnIndex("time"));
//		  holder.ivType.setImageResource(WeatherDataUtil.mhm_wt_warning.get(type+level));
		  holder.ivType.setImageResource(WeatherDataUtil.getWarningDrawbleID(type, level));
		  holder.tvCreateTime.setText(time);
		  holder.tvTitle.setText(title);
		  
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		//找到布局和控件
		ViewHolder holder = new ViewHolder();
		View content = mInflater.inflate(R.layout.warning_list_item, null);
		holder.ivType = (ImageView) content.findViewById(R.id.iv_warning_tpye);
		holder.tvCreateTime = (TextView)content.findViewById(R.id.tv_warning_create_time);
		holder.tvTitle = (TextView)content.findViewById(R.id.tv_warning_title);
		content.setTag(holder);
		return content;//返回的view传给bindView。
	}
	
	private class ViewHolder {
		public TextView tvTitle;
		public TextView tvCreateTime;
		public ImageView ivType;
	}

}
