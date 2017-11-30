/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SettingActivity.java - 2014-3-21 下午2:38:50
 */

package com.supermap.pisaclient.ui;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.CommissionActivity;
import com.supermap.pisaclient.MyActivity;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.BadgeView;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.UpdataInfo;
import com.supermap.pisaclient.entity.User;
//import com.supermap.pisaclient.ui.AboutappActivity.InstallTask;

public class SettingActivity extends BaseActivity implements OnClickListener {

	SharedPreferences sp;
	Editor editor;
	private View mContent;
	private LinearLayout mLlMessage;
	private LinearLayout mLlFarm;
	private LinearLayout mLlHelp;
	private LinearLayout mLlUpdate;
	private LinearLayout mLlSuggest;
	private LinearLayout mLlAboutapp;
	private Button mBtnLogin;
	private LinearLayout mLlUser;
	private ImageView mIvHeader;
	private ImageView Update_show;
	private TextView mTvName;
	private TextView mTvEdit;
	private TextView mTvEditPwd;
	private TextView mTvLogout;
	private LinearLayout mLlLogOut;
	private User mUser;
	private ImageLoader mLoader;
	private LinearLayout mLlFeed;
	private LinearLayout mLlScience;
	private long firstTime = 0;
	private CityDao mCityDao;
	private int unRedMsg = 0;
	private String bool="";
	
	private LinearLayout mLl_remind;
	
	private LinearLayout 	mLlLogin;
	private TextView		mTvLogin;
	
	private LinearLayout 	mLlMsg;
	
	private LinearLayout 	mLlFarm_msg;
	
	private LinearLayout   mLlFarm_commis;
	
	private LinearLayout 	mLlFarm_manager;
	
	private LinearLayout 	mLl_Suggest;
	
	private LinearLayout 	mLlGuide;
	
	private LinearLayout mLlMaterial;
	
	private LinearLayout 	mLlCheck_update;
	
	private LinearLayout 	mLlFeedback;
	
	private ImageView 		mIv_st_header;
	
	private TextView		mTv_user_name;
	
	private TextView 		mTv_edit_pwd;
	
	private BadgeView msgRemind ;
	
	User user;
	
	
	
	private void newDisplay(boolean flag) {
		if (flag) {
			mTv_user_name.setVisibility(View.VISIBLE);
			mTv_edit_pwd.setVisibility(View.VISIBLE);
		} else {
			mTv_user_name.setVisibility(View.VISIBLE);
			mTv_edit_pwd.setVisibility(View.INVISIBLE);
		}
	}
	

	private void display(boolean flag) {
		if (flag) {
			mBtnLogin.setVisibility(View.GONE);
			mIvHeader.setVisibility(View.VISIBLE);
			mTvName.setVisibility(View.VISIBLE);
			mTvEdit.setVisibility(View.VISIBLE);
			mTvEditPwd.setVisibility(View.VISIBLE);
			mTvLogout.setVisibility(View.VISIBLE);
			mLlLogOut.setVisibility(View.VISIBLE);
		} else {
			mBtnLogin.setVisibility(View.VISIBLE);
			mIvHeader.setVisibility(View.GONE);
			mTvName.setVisibility(View.GONE);
			mTvEdit.setVisibility(View.GONE);
			mTvEditPwd.setVisibility(View.GONE);
			mTvLogout.setVisibility(View.GONE);
			mLlLogOut.setVisibility(View.GONE);
		}
	}
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.more));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.app_setting);
		
		Update_show = (ImageView) mContent.findViewById(R.id.update_show);
		
		mLlMessage = (LinearLayout) mContent.findViewById(R.id.ll_msg_center);
		mLlMessage.setOnClickListener(this);
		
		mLlAboutapp = (LinearLayout) mContent.findViewById(R.id.st_ll_check_aboutapp);
		mLlAboutapp.setOnClickListener(this);
		
		mLlFarm = (LinearLayout) mContent.findViewById(R.id.ll_farm);
		mLlFarm.setOnClickListener(this);
		
		mLlHelp = (LinearLayout) mContent.findViewById(R.id.ll_help);
		mLlHelp.setOnClickListener(this);
		
		mLlUpdate = (LinearLayout) mContent.findViewById(R.id.ll_update);
		mLlUpdate.setOnClickListener(this);
		
		mLlFeed = (LinearLayout) mContent.findViewById(R.id.ll_feedback);
		mLlFeed.setOnClickListener(this);
		
		mLlScience = (LinearLayout) mContent.findViewById(R.id.ll_science);
		mLlScience.setOnClickListener(this);
		
		mLlSuggest = (LinearLayout) mContent.findViewById(R.id.ll_suggest);
		mLlSuggest.setOnClickListener(this);
		
		mLlLogin = (LinearLayout) mContent.findViewById(R.id.st_ll_login);
		mLlLogin.setOnClickListener(this);
		mTvLogin = (TextView) mContent.findViewById(R.id.st_tv_login);
		
		mLl_remind = (LinearLayout) mContent.findViewById(R.id.st_ll_remind);
		msgRemind = new BadgeView(SettingActivity.this, mLl_remind);
		msgRemind.setBackgroundResource(R.drawable.badge_ifaux);
		
		mLlMsg = (LinearLayout) mContent.findViewById(R.id.st_ll_msg);
		mLlMsg.setOnClickListener(this);
		
		mLlFarm_msg = (LinearLayout) mContent.findViewById(R.id.st_ll_farm_msg);
		mLlFarm_msg.setOnClickListener(this);
		
		mLlFarm_commis = (LinearLayout) mContent.findViewById(R.id.st_ll_commission);
		mLlFarm_commis.setOnClickListener(this);
		
//		mLlFarm_manager = (LinearLayout) mContent.findViewById(R.id.st_ll_farm_manager);
//		mLlFarm_manager.setOnClickListener(this);
		
		mLl_Suggest = (LinearLayout) mContent.findViewById(R.id.st_ll_suggest);
		mLl_Suggest.setOnClickListener(this);
		mLl_Suggest.setVisibility(View.INVISIBLE);
		
		mLlGuide = (LinearLayout) mContent.findViewById(R.id.st_ll_guide);
		mLlGuide.setOnClickListener(this);

		mLlMaterial = (LinearLayout) mContent.findViewById(R.id.st_ll_material);
		mLlMaterial.setVisibility(View.INVISIBLE);
		mLlMaterial.setOnClickListener(this);
		
		
		mLlCheck_update = (LinearLayout) mContent.findViewById(R.id.st_ll_check_update);
		mLlCheck_update.setOnClickListener(this);
		
		mLlFeedback = (LinearLayout) mContent.findViewById(R.id.st_ll_feedback);
		mLlFeedback.setOnClickListener(this);
		
		mIv_st_header = (ImageView) mContent.findViewById(R.id.st_iv_user_head);
		
		mIv_st_header.setOnClickListener(this);
		mTv_user_name = (TextView) mContent.findViewById(R.id.st_tv_user_name);
		mTv_user_name.setOnClickListener(this);
		mTv_edit_pwd  = (TextView) mContent.findViewById(R.id.st_tv_edit_paseword);
		mTv_edit_pwd.setOnClickListener(this);
		mLoader = new ImageLoader(this);
		mCityDao = new CityDao(this);
		
//		new Thread(new CheckVersionTask()).start();
		
		sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		User user = UserDao.getInstance().get();
		if(user == null){
		} else{
			String aaaaa = user.loginName;
			if ((user != null) && (user.loginName.equals("17783199652") || user.loginName.equals("18996758296")) ) {
				mLlMaterial.setVisibility(View.VISIBLE);
			} else {
				mLlMaterial.setVisibility(View.INVISIBLE);
			}
		}
		
		
		if (checkNet()) {
			if ((user != null) && (user.isExpert)) {
				mLl_Suggest.setVisibility(View.VISIBLE);
			} else {
				mLl_Suggest.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	
	

	private void setRemind() {
		User user = UserDao.getInstance().get();
		if (user != null) {
			unRedMsg = mCityDao.getUnCheckedMsgNum(user.id);
		}
//		Button remindButton = (Button) mLlMessage.findViewById(R.id.msg_remind_num);
		if (unRedMsg > 0) {
			
//			remindButton.setVisibility(View.VISIBLE);
			if (unRedMsg > 99) {
//				remindButton.setText("99");
				msgRemind.setText("99+");
			} else {
//				remindButton.setText(unRedMsg + "");
				msgRemind.setText(unRedMsg + "");
			}
			msgRemind.show();
		} else {
//			remindButton.setVisibility(View.INVISIBLE);
			msgRemind.hide();
			update(Constant.UPDATE_MSG_FLAG, Constant.UPDATE_NO_NEW_REMIND_FLAG);
		}
		
	}

	@Override
	public void onResume() {
		mUser = UserDao.getInstance().get();
		mBtnLogin = (Button) mContent.findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(this);
		mIvHeader = (ImageView) mContent.findViewById(R.id.iv_header);
		mIvHeader.setBackgroundResource(R.drawable.default_header);
		if (mUser != null) {
			mLlUser = (LinearLayout) mContent.findViewById(R.id.ll_user);
			mLlUser.setOnClickListener(this);
			if (mUser.headImage != null)
				mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIvHeader, false);
			mTvName = (TextView) mContent.findViewById(R.id.tv_name);
			if (mUser.showName != null) {
				if (mUser.showName.length() > 20) {
					mTvName.setText(mUser.showName + "...");
				} else {
					mTvName.setText(mUser.showName);
				}
			}
			mTvEdit = (TextView) mContent.findViewById(R.id.tv_edit);
			mTvEditPwd = (TextView) mContent.findViewById(R.id.tv_edit_pwd);
			mTvLogout = (TextView) mContent.findViewById(R.id.tv_logout);
			mLlLogOut = (LinearLayout) mContent.findViewById(R.id.ll_logout);
			display(true);
			mTvEdit.setOnClickListener(this);
			mTvEditPwd.setOnClickListener(this);
			mTvLogout.setOnClickListener(this);
			
			
		}
		if (mUser != null) {
			mTvLogin.setText("注销");
			if (mUser.headImage != null){
				mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIv_st_header, false);
			}
			
			if (mUser.showName != null) {
				if (mUser.showName.length() > 30) {
					mTv_user_name.setText(mUser.showName + "...");
				} else {
					mTv_user_name.setText(mUser.showName);
				}
			}
			newDisplay(true);
		}else {
			mTvLogin.setText("登录/注册");
			newDisplay(false);
		}
		setRemind();
		super.onResume();
	}

	public boolean checkNet() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
			return false;
		}
		return true;
	}
	

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ll_msg_center:
			if (checkNet()) {
				intent = new Intent(this, MessageCenterActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_farm:
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				intent = new Intent(this, FarmManagerActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_suggest:
			if (checkNet()) {
				User user = UserDao.getInstance().get();
				if ((user != null) && (user.isExpert)) {
					intent = new Intent(this, SuggestActivity.class);
					startActivity(intent);
				} else {
					CommonUtil.showToask(this, "专家才能查看");
				}
			}
			break;
		case R.id.ll_science:
			if (checkNet()) {
				intent = new Intent(this, ScienceActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_help:
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			break;
//		case R.id.ll_update:
//			if (checkNet()) {
//				CommonUtil.checkUpdateForClick(this);
//			}
//			break;
		case R.id.st_ll_check_aboutapp:
			if (checkNet()) {				
				intent = new Intent();
				//intent.putExtra("Visibility", bool);
				intent.setClass(this, AboutappActivity.class);			
				startActivity(intent);
			}
			break;	
		case R.id.btn_login:
			if (checkNet()) {
				intent = new Intent(this, UserLoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_feedback:
			if (checkNet()) {
				intent = new Intent(this, FeedBackActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.tv_edit:
			if (checkNet()) {
				intent = new Intent(this, UserRegActivity.class);
				intent.putExtra("edit", 1);
				intent.putExtra("pwd", 1);
				startActivity(intent);
			}
			break;
		case R.id.tv_edit_pwd:
			if (checkNet()) {
				intent = new Intent(this, UserRegActivity.class);
				intent.putExtra("edit", 1);
				intent.putExtra("pwd", 2);
				startActivity(intent);
			}
			break;
		case R.id.tv_logout:
			UserDao.getInstance().logout();
//			mIvHeader.setBackgroundResource(R.drawable.default_header);
			mIvHeader.setImageResource(R.drawable.default_header);
			display(false);
			break;
			
		
		//新UI
		case R.id.st_iv_user_head:
		case R.id.st_tv_user_name:
			if (checkNet()&&(mUser!=null)) {
				intent = new Intent(this, UserRegActivity.class);
				intent.putExtra("edit", 1);
				intent.putExtra("pwd", 1);
				startActivity(intent);
			}
			break;
		case R.id.st_tv_edit_paseword:
			if (checkNet()) {
				CommonUtil.showToask(this, "很抱歉，该功能暂未开放");
//				intent = new Intent(this, UserRegActivity.class);
//				intent.putExtra("edit", 1);
//				intent.putExtra("pwd", 2);
//				startActivity(intent);
			}
			break;
		case R.id.st_ll_login:
			if(mUser!=null){
				UserDao.getInstance().logout();
				mUser=null;
				mIv_st_header.setImageResource(R.drawable.default_header);
				newDisplay(false);
				mTvLogin.setText("登陆/注册");
				mTv_user_name.setText("未登录");
//				editor.remove("USER_NAME");
//				editor.remove("PASSWORD");
//				editor.remove("CITYNAME");
//				editor.commit();

			}
			else {
				if (checkNet()) {
					intent = new Intent(this, UserLoginActivity.class);
					startActivity(intent);
				}
			}
			
			break;
		case R.id.st_ll_msg:
			if (checkNet()) {
				intent = new Intent(this, MessageCenterActivity.class);
				//MessageCenterActivity
				startActivity(intent);
			}
			break;
		case R.id.st_ll_farm_msg:
			if (checkNet()) {
				intent = new Intent(this, AdvisoryActivity.class);
				intent.putExtra("jugg", "0");
				startActivity(intent);
			}
			break;
//		case R.id.st_ll_farm_manager:
//			if (UserDao.getInstance().get() == null) {
//				CommonDialog.setLoginDialog(this);
//			} else {
//				intent = new Intent(this, FarmManagerActivity.class);
//				startActivity(intent);
//			}
//			break;
		case R.id.st_ll_suggest:
			if (checkNet()) {
				User user = UserDao.getInstance().get();
				if ((user != null) && (user.isExpert)) {
					intent = new Intent(this, SuggestActivity.class);
					startActivity(intent);
				} else {
					CommonUtil.showToask(this, "专家才能查看");
				}
			}
			break;	
		case R.id.st_ll_guide:
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				intent = new Intent(this, ProductNewActivity.class); //原来是使用指南
				startActivity(intent);
			}
			break;	
		case R.id.st_ll_commission:
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				intent = new Intent(this, MyActivity.class); //原来是使用指南
				startActivity(intent);
			}
			//CommonUtil.showToask(this, "功能暂未开放");
			break;
		case R.id.st_ll_material:
			if (checkNet()) {
				User user = UserDao.getInstance().get();
				if ((user != null) && (mUser.loginName.equals("17783199652") || mUser.loginName.equals("18996758296")) ) {					
					intent = new Intent(this, MaterialActivity.class);//物联设备列表
					startActivity(intent);
				} else {
				}
			}
			break;				
		case R.id.st_ll_feedback:
			if (checkNet()) {
				//intent = new Intent(this, FeedBackActivity.class);
				intent = new Intent(this, SituationActivity.class);
				intent.putExtra("jugg", "1");
				intent.putExtra("key", "point");//farm则加载显示农田选项,point则加载显示地区选项
				//intent = new Intent(this, OneMapOOActivity.class);
				//intent = new Intent(this, DCloudActivity.class);
				startActivity(intent);
			}
			break;	
		
		}
		super.onClick(v);
	}
	
//	UpdataInfo info;
//	public class CheckVersionTask implements Runnable
//	{
//		@Override
//		public void run() {
//			try {
//				String path=getResources().getString(R.string.apk_xml_url);
//				URL url=new URL(path);
//				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//				conn.setConnectTimeout(5000);
//				InputStream is =conn.getInputStream();
//				info=CommonUtil.getUpdataInfo(is);
//				String aaaa=info.getVersion();  //获取服务器版本号
//				String bbbb=CommonUtil.getVersionName(SettingActivity.this); //获取本地版本号
//					
//				
//				if(info.getVersion().compareTo(CommonUtil.getVersionName(SettingActivity.this)) <= 0) //使用  大于小于  判断版本号 确认是否更新
//				{
//					//小于或等于0 无需更新
//					System.out.println("版本号相同无须升级");
//					//Toast.makeText(SettingActivity.this, "版本号相同，无须升级", Toast.LENGTH_SHORT).show();
//					Message msg=new Message();
//					msg.what=Constant.UPDATA_NO;
//					handler.sendMessage(msg);
//				}
//				else
//				{   //大于0 则更新
//					System.out.println("版本号不同，提示升级");
//					Message msg=new Message();
//					msg.what=Constant.UPDATA_CLIENT;
//					handler.sendMessage(msg);
//				}
//			}
//			catch (Exception e)
//			{
//				Message msg=new Message();
//				msg.what=Constant.UPDATA_ERROR;
//				handler.sendMessage(msg);
//				e.printStackTrace();
//			}
//		}	
//	}
//	
//	Handler handler=new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch(msg.what) {
//			case Constant.UPDATA_CLIENT:
//				Update_show.setVisibility(View.VISIBLE);   //如果有新版本更新，则显示红点
//				bool="1";
//				//CommonUtil.showToask(SettingActivity.this, bool);
//				break;
//			case Constant.UPDATA_NO:
//                Update_show.setVisibility(View.INVISIBLE); //如果没有版本更新，则隐藏红点
//                bool="0";
//                //CommonUtil.showToask(SettingActivity.this, bool);
//				break;
//			}
//		}
//	};
}
