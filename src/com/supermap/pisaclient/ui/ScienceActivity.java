/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ScienceActivity.java - 2014-7-4 上午9:39:07
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.ScienceAdapter;
import com.supermap.pisaclient.biz.MenuDao;
import com.supermap.pisaclient.biz.ScienceDao;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.MenuDialog;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Science;

public class ScienceActivity extends BaseActivity implements OnClickListener {

	private View mContent;
	private ListView mLvSciences;
	private ScienceAdapter mAdapter;
	private int mPageSize = 5;
	private int mPageIndex = 1;
	private int mScienceType = 0;
	private List<Science> mSciences = new ArrayList<Science>();
	private ScienceDao mDao;
	private MenuDao mTypeDao;
	private MenuDialog mDialog;
	private CustomProgressDialog mPd;
	private int count = 0;
	private int lastItem;
	private int max = 100;
	private List<MenuItem> mScienceTypes = new ArrayList<MenuItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.science));
		setIsNavigator(false);
		setIsMenu(true);
		setIsBack(true);
		setBackOnClickListener(this);

		mContent = inflater(R.layout.sciencelist);
		mDao = new ScienceDao();
		mPd = CustomProgressDialog.createDialog(this);
		mPd.setMessage(Utils.getString(this, R.string.loading_data));
		mTypeDao = new MenuDao("cq.get.sciencetype");
		mDialog = new MenuDialog(ScienceActivity.this, getScreen()[0], getMenuHeight());
		mLvSciences = (ListView) mContent.findViewById(R.id.lv_sciences);
		mLvSciences.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem == count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						mPageIndex++;
						if (mPageIndex < max) {
							refresh();
						}
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}

		});
		setMenuOnClickListener(this);
		refresh();
		new LoadType().execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			this.finish();
			break;
		case R.id.ibtn_menu:
			mDialog.setData(mScienceTypes);
			mDialog.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					MenuItem type = mScienceTypes.get(arg2);
					mScienceType = type.id;
					mPageIndex = 1;
					max = 2000;
					mSciences.clear();
					new LoadTask().execute();
					mDialog.hide();
				}

			});
			mDialog.show();
			break;
		}
		super.onClick(v);
	}

	private void refresh() {
		new LoadTask().execute();
	}

	private class LoadType extends AsyncTask<Integer, Integer, List<MenuItem>> {

		@Override
		protected List<MenuItem> doInBackground(Integer... params) {
			return mTypeDao.getAll();
		}

		@Override
		protected void onPostExecute(List<MenuItem> result) {
			if (result != null) {
				mScienceTypes = result;
				MenuItem type = new MenuItem();
				type.id = 0;
				type.name = "全部";
				mScienceTypes.add(type);
			}
		}

	}

	private class LoadTask extends AsyncTask<Integer, Integer, List<Science>> {

		@Override
		protected void onPreExecute() {
			mPd.show();
		}

		@Override
		protected List<Science> doInBackground(Integer... params) {
			return mDao.getAll(mPageSize, mPageIndex, mScienceType);
		}

		@Override
		protected void onPostExecute(List<Science> result) {
			if (result != null) {
				if (result.size() != mPageSize) {
					max = mPageIndex;
				}
				if (mSciences.size() == 0) {
					mSciences = result;
					mAdapter = new ScienceAdapter(ScienceActivity.this, result);
					mLvSciences.setAdapter(mAdapter);
				} else {
					mSciences.addAll(result);
					mAdapter.notifyDataSetChanged();
				}
				count = mSciences.size();
			}
			if (mPd.isShowing())
				mPd.dismiss();
		}

	}

}
