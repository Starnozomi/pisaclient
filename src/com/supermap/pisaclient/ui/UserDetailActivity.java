/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @UserRegActivity.java - 2014-5-28 上午11:00:01
 */

package com.supermap.pisaclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.User;

public class UserDetailActivity extends BaseActivity {

	private View mContent;
	private ImageView mIvHeader;
	private TextView mTvUserName;
	private TextView mTvLoginName;
	private TextView mTvPwd;
	private TextView mTvTel;
	private TextView mTvCountry;
	private User mUser;
	private ImageLoader mLoader;
	private LinearLayout mLlPwd;
	private LinearLayout mLlAboutCountry;
	private LinearLayout mLlCountries;
	private Button mBtnEdit;
	private CityDao mCityDao;
	private UserDao mUserDao;
	private int mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.user_detail));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);
		setBackOnClickListener(this);
		setIsNavigator(false);
		mContent = inflater(R.layout.user_detail);

		mUserId = this.getIntent().getIntExtra("id", 8);
		mUserDao = UserDao.getInstance();
		mCityDao = new CityDao(this);
		mLoader = new ImageLoader(this);
		mIvHeader = (ImageView) mContent.findViewById(R.id.iv_header);
		mTvUserName = (TextView) mContent.findViewById(R.id.tv_username);
		mTvLoginName = (TextView) mContent.findViewById(R.id.tv_loginname);
		mTvCountry = (TextView) mContent.findViewById(R.id.tv_countries);
		mLlPwd = (LinearLayout) mContent.findViewById(R.id.ll_pwd);
		mLlAboutCountry = (LinearLayout) mContent.findViewById(R.id.ll_about_country);
		mLlCountries = (LinearLayout) mContent.findViewById(R.id.ll_countries);
		mTvTel = (TextView) mContent.findViewById(R.id.tv_tel);
		mTvPwd = (TextView) mContent.findViewById(R.id.tv_pwd);
		mBtnEdit = (Button) mContent.findViewById(R.id.btn_edit);

		mBtnEdit.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mUser = mUserDao.get();
		if (mUser == null || mUserId != mUser.id) {
			mUser = mUserDao.searchById(mUserId);
		} else {
			mBtnEdit.setVisibility(View.VISIBLE);
			mLlPwd.setVisibility(View.VISIBLE);
			mLlAboutCountry.setVisibility(View.VISIBLE);
			mLlCountries.setVisibility(View.VISIBLE);
			if (mUser != null && mUser.password != null)
				mTvPwd.setText(mUser.password);
			if (!"null".equals(mUser.areaCode.trim()) && !"".equals(mUser.areaCode.trim())) {
				String[] codes = mUser.areaCode.split(",");
				StringBuffer sb = new StringBuffer();
				for (String code : codes) {
					sb.append(mCityDao.queryCityName(code) + " ");
				}
				String res = sb.toString();
				res = res.substring(0, res.length() - 1);
				mTvCountry.setText(res);
			}
		}
		if (mUser != null && mUser.headImage != null)
			mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIvHeader, false);
		if (mUser != null && mUser.showName != null)
			mTvUserName.setText(mUser.showName);
		if (mUser != null && mUser.loginName != null)
			mTvLoginName.setText(mUser.loginName);
		if (mUser != null && mUser.mobile != null)
			mTvTel.setText(mUser.mobile);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.ibtn_back:
			this.finish();
			break;
		case R.id.btn_edit:
			intent = new Intent(this, UserRegActivity.class);
			intent.putExtra("edit", 1);
			intent.putExtra("pwd", 1);
			startActivity(intent);
			this.finish();
			break;
		}
		super.onClick(v);
	}
}
