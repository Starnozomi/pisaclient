/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SuggestActivity.java - 2014-7-10 下午2:41:51
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.supermap.pisaclient.adapter.SuggestAdapter;
import com.supermap.pisaclient.biz.MenuDao;
import com.supermap.pisaclient.biz.SuggestDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.MenuDialog;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.entity.User;

public class SuggestActivity extends BaseActivity implements OnClickListener {

	private View mContent;
	private SuggestDao mDao;
	private ListView mLvSuggests;
	private CustomProgressDialog mPd;
	private int mPageSize = 5;
	private int mPageIndex = 1;
	private int count = 0;
	private int lastItem;
	private int max = 1000;
	private int mTypeId = 0;
	private List<Suggest> mSuggests;
	private SuggestAdapter mAdapter;
	private MenuDao mTypeDao;
	private List<MenuItem> mScienceTypes;
	private MenuDialog mDialog;
	private User mUser;
	private UserDao mUserDao;
	private int mId = 0;
	private boolean isAddSuggest = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.professional_advice));
		setIsMenu(true);
		setIsNavigator(false);
		setIsBack(true);
		setBackOnClickListener(this);

		mContent = inflater(R.layout.suggestlist);
		mLvSuggests = (ListView) mContent.findViewById(R.id.lv_suggests);
		mLvSuggests.setOnScrollListener(new OnScrollListener() {
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
		mSuggests = new ArrayList<Suggest>();
		mAdapter = new SuggestAdapter(SuggestActivity.this, mSuggests);
		mLvSuggests.setAdapter(mAdapter);
		
		mDao = new SuggestDao();
		mPd = CustomProgressDialog.createDialog(this);
		mPd.setMessage(Utils.getString(this, R.string.loading_data));
		
		mTypeDao = new MenuDao("cq.get.sciencetype");
		mDialog = new MenuDialog(SuggestActivity.this, getScreen()[0], getMenuHeight());
		new LoadType().execute();
		setMenuOnClickListener(this);
	}

	@Override
	protected void onResume() {
		mUserDao = UserDao.getInstance();
		mUser = mUserDao.get();
		if (mUser != null) {
			mId = mUser.id;
			super.setIsSuggest(true);
			super.setSuggestOnClickListener(this);
			if (isAddSuggest) {
				mPageIndex =1;
				isAddSuggest = false;
				mSuggests = new ArrayList<Suggest>();
			}
			refresh();
		}
		super.onResume();
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
					mTypeId = type.id;
					mSuggests = new ArrayList<Suggest>();
					mPageIndex = 1;
					max = 2000;
					new LoadTask().execute();
					mDialog.hide();
				}

			});
			mDialog.show();
			break;
		case R.id.ibtn_suggest:
			isAddSuggest = true;
			Intent intent = new Intent(this, AddSuggestActivity.class);
			startActivity(intent);
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

	private class LoadTask extends AsyncTask<Integer, Integer, List<Suggest>> {
		@Override
		protected void onPreExecute() {
			mPd.show();
		}
		@Override
		protected List<Suggest> doInBackground(Integer... params) {
			return mDao.getAll(mId, mPageSize, mPageIndex, mTypeId);
		}

		@Override
		protected void onPostExecute(List<Suggest> result) {
			if (result != null) {
				if (result.size() != mPageSize) {//加载完了
					max = mPageIndex;
				}
				if (mSuggests.size() == 0) {
					mSuggests = result;
				} else {
					mSuggests.addAll(result);
				}
				//用于判断是否滑动底部
				count = mSuggests.size();
				mAdapter.setdata(mSuggests);
			}
			if (mPd.isShowing())
				mPd.dismiss();
		}

	}
}
