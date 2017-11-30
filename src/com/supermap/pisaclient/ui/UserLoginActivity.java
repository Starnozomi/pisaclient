/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @UserLoginActivity.java - 2014-5-29 上午10:57:42
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.SecurityCode;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.User;

public class UserLoginActivity extends BaseActivity {

	private View mContent;
	private SharedPreferences sp;
	private Editor editor;
	private EditText mEtLoginName;
	private EditText mEtLoginPwd;
	private EditText mEtValid;
	private Button mBtnRefresh;
	private Button mBtnLogin;
	private Button mBtnVip;
	private ImageView mIvRefresh;
	private TextView mTvForgetPwd;
	private String mLoginName;
	private String mPassword;
	private String mValid;
	private CheckBox mChhRemember;
	private UserDao mUserDao;
	private Boolean isLogining = false;
	private String code;
	private User mUser;
	private boolean isBig=false;//用于设置验证码的大小
	@Override
	protected void onResume() {
		/*mEtLoginName.setText("");
		mEtLoginPwd.setText("");
		mEtValid.setText("");*/

		//mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(mEtValid.get()*2, mEtValid.getMeasuredHeight())) ;
		//code=SecurityCode.getInstance().getCode();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.user_login));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);
		setBackOnClickListener(this);
		setIsNavigator(false);
		mContent = inflater(R.layout.user_login);
		sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		editor = sp.edit();
		
		mEtLoginName = (EditText) mContent.findViewById(R.id.et_login_name);
		mEtLoginPwd = (EditText) mContent.findViewById(R.id.et_login_pwd);
		mEtValid = (EditText) mContent.findViewById(R.id.et_valid);
		mBtnRefresh = (Button) mContent.findViewById(R.id.btn_refresh);
		mBtnLogin = (Button) mContent.findViewById(R.id.btn_login);
		mBtnVip = (Button) mContent.findViewById(R.id.btn_vip);
		mIvRefresh = (ImageView) mContent.findViewById(R.id.iv_refresh);
		mChhRemember = (CheckBox) mContent.findViewById(R.id.chk_remember);
		mTvForgetPwd = (TextView) mContent.findViewById(R.id.tv_forget_pwd);
		mBtnRefresh.setOnClickListener(this);
		mBtnLogin.setOnClickListener(this);
		mBtnVip.setOnClickListener(this);
		mTvForgetPwd.setOnClickListener(this);
		mUserDao = UserDao.getInstance();
		
		isBig= CommonUtil.getIsbigScreenOrHighDensty(UserLoginActivity.this);
		//refresh();
		mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(isBig)) ;
		//mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(mEtValid.getMeasuredHeight()*2, mEtValid.getMeasuredHeight())) ;
		code=SecurityCode.getInstance().getCode();
	
		Bundle b = getIntent().getExtras();
		if(b != null){
			String loginName = b.getString("loginname");
			String passWord  = b.getString("password");
			mEtLoginName.setText(loginName);
			mEtLoginPwd.setText(passWord);
		}
		
		mUserDao = UserDao.getInstance();
	}

	
	//判断输入用户为空
	private boolean isValid() {
		mLoginName = mEtLoginName.getText().toString().trim();
		mPassword = mEtLoginPwd.getText().toString().trim();
		mValid = mEtValid.getText().toString().trim();
		if ("".equals(mLoginName)) {
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_loginname));
			focusName();
			return false;
		}
		if ("".equals(mPassword)) {
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_pwd));
			focusPassword();
			return false;
		}
		/*if ("".equals(mValid)) {
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_valid));
			focusValid();
			return false;
		}*/
		return true;
	}

	private void focusValid() {
		mEtValid.setFocusable(true);
		mEtValid.setFocusableInTouchMode(true);
		mEtValid.clearFocus();
		mEtValid.requestFocus();
	}

	private void focusName() {
		mEtLoginName.setFocusable(true);
		mEtLoginName.setFocusableInTouchMode(true);
		mEtLoginName.clearFocus();
		mEtLoginName.requestFocus();
	}

	private void focusPassword() {
		mEtLoginPwd.setFocusable(true);
		mEtLoginPwd.setFocusableInTouchMode(true);
		mEtLoginPwd.clearFocus();
		mEtLoginPwd.requestFocus();
	}

	private void refresh() {
		new LoadRefresh().execute();
	}

	private class LoadRefresh extends AsyncTask<Integer, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Integer... params) {
			return mUserDao.getValidate(CommonUtil.getIsbigScreenOrHighDensty(UserLoginActivity.this));
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				mIvRefresh.setImageBitmap(result);
			}
		}

	}
	
	
	private class LoginTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			User user = new User();
			user.loginName = mLoginName;
			user.password = mPassword;
			if (mUserDao.login(user, mChhRemember.isChecked())) {
				 mBinder.updateCropsMethod();
				return 1;
			} else {
				return  -1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			isLogining = false;
			switch (result) {
			case 1:
				Intent intent = new Intent();
				setResult(Constant.LOGIN_SUCCEED, intent);
				UserLoginActivity.this.finish();
				mUser = mUserDao.get();
//				String city = mUser.areaCode;
				editor.putString("CITYNAME", mUser.areaCode);  //存储用户所在城市
				editor.commit(); 
				break;
			case -1:
				//refresh();
				mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(isBig)) ;
				code=SecurityCode.getInstance().getCode();
				CommonUtil.showToask(UserLoginActivity.this, getResources().getString(R.string.invalid_login));
				focusName();
				break;
			case 0:
				//refresh();
				mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(isBig)) ;
				code=SecurityCode.getInstance().getCode();
				CommonUtil.showToask(UserLoginActivity.this, getResources().getString(R.string.user_err_valid));
				focusValid();
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_refresh:
			//refresh();
			mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(isBig)) ;
			//mIvRefresh.setImageBitmap(SecurityCode.getInstance().createBitmap(mEtValid.getMeasuredHeight()*2, mEtValid.getMeasuredHeight())) ;
			code=SecurityCode.getInstance().getCode();
			break;
		case R.id.btn_login:
			if (isValid()) {
				if(isLogining){
					CommonUtil.showToask(this, "正在登陆...");
				}else {
					isLogining = true;
					new LoginTask().execute();
				}	
			}
			break;
		case R.id.tv_forget_pwd:
//			intent = new Intent(this, SearchPasswordActivity.class);
//			startActivity(intent);
			//finish();
			break;
		case R.id.btn_vip:
			intent = new Intent(this, UserRegActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		super.onClick(v);
	}

}
