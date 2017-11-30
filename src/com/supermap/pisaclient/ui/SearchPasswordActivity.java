/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SearchPasswordActivity.java - 2014-6-25 下午5:19:11
 */
package com.supermap.pisaclient.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ToastUtils;
import com.supermap.pisaclient.entity.SendMessageResult;

public class SearchPasswordActivity extends Activity {

	private EditText mEtLoginName;
	private EditText mEt_Verification;
	private Button mBtnFind, sendverification;
	private String mLoginName;
	private String mTel;
	private UserDao mUserDao;

	private void focusName() {
		mEtLoginName.setFocusable(true);
		mEtLoginName.setFocusableInTouchMode(true);
		mEtLoginName.clearFocus();
		mEtLoginName.requestFocus();
	}

	private void focusTel() {
		mEt_Verification.setFocusable(true);
		mEt_Verification.setFocusableInTouchMode(true);
		mEt_Verification.clearFocus();
		mEt_Verification.requestFocus();
	}

	private boolean isValid() {
		mLoginName = mEtLoginName.getText().toString().trim();
		mTel = mEt_Verification.getText().toString().trim();
		if ("".equals(mLoginName)) {
			focusName();
			ToastUtils.showToast(this, getResources().getString(R.string.user_no_loginname));
//			CommonUtil.showToask(this, getResources().getString(R.string.user_no_loginname));
			return false;
		}
		if ("".equals(mTel)) {
			focusTel();
			ToastUtils.showToast(this, getResources().getString(R.string.user_no_valid));
//			CommonUtil.showToask(this, getResources().getString(R.string.user_no_pwd));
			return false;
		}
		return true;
	}

	private class SendTask extends AsyncTask<Integer, Integer, SendMessageResult> {
		private String name;
		private String tel;

		public SendTask(String name, String tel) {
			this.name = name;
			this.tel = tel;
		}
		@Override
		protected SendMessageResult doInBackground(Integer... params) {
			return mUserDao.findPassword(name, tel);
		}
		@Override
		protected void onPostExecute(SendMessageResult result) {
			String msg = "";
			if (result.isSuccess) {
				msg = "发送成功，";
			} else {
				msg = "对不起，找回失败";
			}
			if (result.msg != null)
				CommonUtil.showToask(SearchPasswordActivity.this, msg + result.msg + ".");
			else
				CommonUtil.showToask(SearchPasswordActivity.this, msg);
			if (result.isSuccess)
				SearchPasswordActivity.this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setContentView(R.layout.search_pwd_layout);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mEtLoginName = (EditText) findViewById(R.id.et_loginname);
		mEt_Verification = (EditText) findViewById(R.id.et_verification);
		
		mBtnFind = (Button) findViewById(R.id.btn_find);
		sendverification = (Button) findViewById(R.id.sendverification);
		
		mUserDao = UserDao.getInstance();
		mBtnFind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isValid()) {
					new SendTask(mLoginName, mTel).execute();
				}
			}
		});
		
		sendverification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击启动发送短信验证码请求
			}

		});
	}

}
