/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SuggestActivity.java - 2014-7-10 下午2:41:51
 */

package com.supermap.pisaclient.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.SuggestDao;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.entity.Suggest;

public class SuggestDetailActivity extends BaseActivity {

	private View mContent;
	private TextView mTvTitle;
	private TextView mTvInfo;
	private TextView mTvAreaName;
	private TextView mTvUserName;
	private TextView mTvStime;
	private SuggestDao mSuggestDao;
	private CustomProgressDialog mPd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.professional_advice));
		setIsNavigator(false);
		setIsBack(true);
		setBackOnClickListener(this);

		mContent = inflater(R.layout.suggest_detail);
		mTvTitle = (TextView) mContent.findViewById(R.id.tv_title);
		mTvInfo = (TextView) mContent.findViewById(R.id.tv_info);
		mTvAreaName = (TextView) mContent.findViewById(R.id.tv_areaname);
		mTvUserName = (TextView) mContent.findViewById(R.id.tv_username);
		mTvStime = (TextView) mContent.findViewById(R.id.tv_stime);

		mSuggestDao = new SuggestDao();
		mPd = CustomProgressDialog.createDialog(this);
		mPd.setMessage(Utils.getString(this, R.string.loading_data));
		new LoadSuggest().execute(this.getIntent().getIntExtra("id", 1));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	private class LoadSuggest extends AsyncTask<Integer, Integer, Suggest> {

		@Override
		protected void onPreExecute() {
			mPd.show();
		}

		@Override
		protected Suggest doInBackground(Integer... params) {
			return mSuggestDao.getById(params[0]);
		}

		@Override
		protected void onPostExecute(Suggest result) {
			if (result != null) {
				mTvTitle.setText(result.title);
				mTvInfo.setText(result.info);
				mTvAreaName.setText("适合：" + result.areaname);
				mTvUserName.setText("来自专家：" + result.username);
				mTvStime.setText(result.stime);
			}

			if (mPd.isShowing())
				mPd.dismiss();
		}
	}

}
