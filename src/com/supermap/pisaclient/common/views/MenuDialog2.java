/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @MenuDialog.java - 2014-3-24 上午10:43:09
 */

package com.supermap.pisaclient.common.views;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Zone;

public class MenuDialog2 {

	public Activity mActivity;
	private ListView mListView;
	private MenuDialogAdapter2 mAdapter;
	private Dialog mDialog;

	public void destroy() {
		if (mDialog != null)
			mDialog.dismiss();
	}

	public MenuDialog2(Activity activity, int x, int y) {
		mActivity = activity;
		mDialog = new Dialog(mActivity, R.style.dialog);
		View contentView = mActivity.getLayoutInflater().inflate(R.layout.dialog_content, null);
		mListView = (ListView) contentView.findViewById(R.id.listview);
		mDialog.setContentView(contentView);
		mDialog.setCanceledOnTouchOutside(true);
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.LEFT | Gravity.TOP);
		lp.x = x - Constant.MENU_WIDTH;
		lp.y = y;
		lp.width = Constant.MENU_WIDTH;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
	}

	public MenuDialogAdapter2 getAdpter() {
		return mAdapter;
	}

	public void setData(List<Zone> arr) {
		if (arr == null) {
			return;
		}
		if (mAdapter == null) {
			mAdapter = new MenuDialogAdapter2(arr);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.setData(arr);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		if (onItemClickListener != null) {
			mListView.setOnItemClickListener(onItemClickListener);
		}
	}

	public void show() {
		if (mAdapter == null) {
			return;
		}
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	public void hide() {
		mDialog.dismiss();
	}

	private class MenuDialogAdapter2 extends BaseAdapter {
		private List<Zone> datas;

		public MenuDialogAdapter2(List<Zone> datas) {
			this.datas = datas;
		}

		public void setData(List<Zone> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Integer getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mActivity.getLayoutInflater().inflate(R.layout.dialog_item, null);
			TextView tvItem = (TextView) convertView.findViewById(R.id.tvItem);
			tvItem.setText(datas.get(position).name);
			return convertView;
		}

	}

}
