/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @FeedBackActivity.java - 2014-7-4 上午9:38:18
 */

package com.supermap.pisaclient.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.FeedBackAdapter;
import com.supermap.pisaclient.biz.FeedBackDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.entity.FeedBack;
import com.supermap.pisaclient.entity.User;

public class FeedBackActivity extends BaseActivity implements OnClickListener {

	private View mContent;
	private ListView mLvFeed;
	private FeedBackAdapter mAdapter;
	private FeedBackDao mDao;
	private EditText mEtContent;
	private Button mBtnSend;
	private List<FeedBack> mAll = new ArrayList<FeedBack>();
	private int mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.feedback));
		setIsNavigator(false);
		setIsBack(true);
		setBackOnClickListener(this);

		mContent = inflater(R.layout.feedback_layout);
		mLvFeed = (ListView) mContent.findViewById(R.id.lv_feeds);
		mDao = new FeedBackDao();
		mEtContent = (EditText) mContent.findViewById(R.id.et_content);
		mBtnSend = (Button) mContent.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);

		User user = UserDao.getInstance().get();
		if (user != null) {
			mUserId = user.id;
		} else {
			mUserId = 0;
		}
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			this.finish();
			break;
		case R.id.btn_send:
			String content = mEtContent.getText().toString().trim();
			if (!"".equals(content)) {
				FeedBack feed = new FeedBack();
				feed.info = content;
				feed.userId = mUserId + "";
				feed.imei = CommonUtil.getImei(this);
				feed.stime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
				if (mDao.addFeedBack(feed)) {
					mAll.add(feed);
					mAdapter.notifyDataSetChanged();
					mLvFeed.setSelection(mAll.size() - 1);
					mEtContent.setText("");
				} else {
					CommonUtil.showToask(FeedBackActivity.this, getResources().getString(R.string.feed_fail));
				}
			}
			break;
		}
		super.onClick(v);
	}

	private void refresh() {
		new LoadTask(this).execute(mUserId);
	}

	private class LoadTask extends AsyncTask<Integer, Integer, List<FeedBack>> {

		private Context context;

		public LoadTask(Context context) {
			this.context = context;
		}

		@Override
		protected List<FeedBack> doInBackground(Integer... params) {
			return mDao.getAll(context);
		}

		@Override
		protected void onPostExecute(List<FeedBack> result) {
			if (result != null && result.size() != 0) {
				mAll = result;
				mAdapter = new FeedBackAdapter(FeedBackActivity.this, mAll);
				mLvFeed.setAdapter(mAdapter);
				mLvFeed.setSelection(mAll.size() - 1);
			} else {
				mAdapter = new FeedBackAdapter(FeedBackActivity.this, mAll);
				mLvFeed.setAdapter(mAdapter);
			}
		}

	}
}
