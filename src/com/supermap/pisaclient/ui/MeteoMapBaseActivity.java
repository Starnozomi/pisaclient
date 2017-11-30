/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @BaseActivity.java - 2014-3-21 上午10:28:20
 */

package com.supermap.pisaclient.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.views.BadgeView;
import com.supermap.pisaclient.common.views.RemindRadioButton;
import com.supermap.pisaclient.service.PisaService;

public class MeteoMapBaseActivity extends Activity implements OnClickListener {

	protected RadioGroup mRgNavigator;
	protected Intent mIntent;
	protected int mNavigator;
	protected ImageButton mibtn_back;
	protected ImageButton mibtn_ico;
	protected TextView mtv_title;
	protected TextView mtv_info;
	protected ImageButton mibtn_refresh;
	protected ImageButton mibtn_edit;
	protected ImageButton mibtn_save;
	protected ImageButton mibtn_menu;
	protected ImageButton mibtn_menu_left;
	protected ImageButton mibtn_suggest;
	protected TextView mtv_hot;
	protected TextView mtv_new;
	protected TextView mtv_my;
	protected RelativeLayout mrl_menu;
	private RelativeLayout mrl_content;
	protected RemindRadioButton mRadWeather;
	protected RadioButton mRadProduct;
	protected RemindRadioButton mRadSituation;
	protected RadioButton mRadAdvisory;
	protected RadioButton mRadMore;
	private LinearLayout mLlWeather;
	private LinearLayout mLlProduct;
	private LinearLayout mLlSituation;
	private LinearLayout mLlAdvisory;
	private LinearLayout mLlMore;
	private View mDivLineTop;
	private View mDivLineBottom;
	
	private BadgeView mWeather_badge;
	private BadgeView mCrops_badge;
	private BadgeView mMore_badge;

	private LayoutInflater mInflater;
	private int[] mMenus = new int[2];
	private int[] mNavigators = new int[2];
	private int[] mDisplay;
	private int w;
	private int h;

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int num = msg.arg1;
			switch (msg.what) {
			case Constant.UPDATE_WARNING_FLAG:
//				drawRemindNum(R.id.rad_weather, msg.arg1);
				if (num>0) {
					mWeather_badge.show();
				}else {
					mWeather_badge.hide();
				}
				
				break;
			case Constant.UPDATE_CROPS_FLAG:
//				drawRemindNum(R.id.rad_situation, msg.arg1);
				mCrops_badge.setText(String.valueOf(num));
				if (num>0) {
					mCrops_badge.show();
				}
				else {
					mCrops_badge.hide();
				}
				break;
			case Constant.UPDATE_MSG_FLAG:
				if (num>0) {
					mMore_badge.show();
				}else {
					mMore_badge.hide();
				}
//				drawRemindNum(R.id.rad_situation, msg.arg1);
				break;
			default:
				break;
			}
		};
	};

	public PisaService.PisaBinder mBinder;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (PisaService.PisaBinder) service;
			mBinder.setHandler(mHandler);
			mBinder.startDownload();
			mBinder.setCitys(ExitApplication.getInstance().mCities);
			initRemindView();
			// System.out.println("bind ok");
		}
		
		/**
		 * 由于框架的baseAcitivity一直在创建和销毁
		 * 每次 onResume时 重新绑定service 初始化badage view 以及显示的数字
		 */

		public void initRemindView() {
//			drawRemindNum(R.id.rad_weather, mBinder.getmWarning_Remind_num());
//			drawRemindNum(R.id.rad_situation, mBinder.getmCrops_Remind_num());
			
			update(Constant.UPDATE_WARNING_FLAG, mBinder.getmWarning_Remind_num());
			update(Constant.UPDATE_CROPS_FLAG, mBinder.getmCrops_Remind_num());
			update(Constant.UPDATE_MSG_FLAG, mBinder.getmMsg_Remind_num());

		}
	};

	protected void setTvTitle(String title) {
		this.mtv_title.setText(title);
	}

	protected void setRgNavigator(int id) {
		mNavigator = id;
		this.mRgNavigator.check(id);
	}

	protected void setIsTitle(boolean flag) {
		this.mtv_title.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsBack(boolean flag) {
		this.mibtn_back.setVisibility(flag ? View.VISIBLE : View.GONE);
		findViewById(R.id.iv_back_div_ic).setVisibility((flag ?	View.GONE  :	View.GONE));
	}

	protected void setIsIco(boolean flag) {
		this.mibtn_ico.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsInfo(boolean flag) {
		this.mtv_info.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsRefresh(boolean flag) {
		this.mibtn_refresh.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsEdit(boolean flag) {
		this.mibtn_edit.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsSuggest(boolean flag) {
		this.mibtn_suggest.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsHot(boolean flag) {
		this.mtv_hot.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsNew(boolean flag) {
		this.mtv_new.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsMy(boolean flag) {
		this.mtv_my.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsSave(boolean flag) {
		this.mibtn_save.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsMenu(boolean flag) {
		this.mibtn_menu.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setLeftMenu(boolean flag)
	{
		this.mibtn_menu_left.setVisibility(flag?View.VISIBLE:View.GONE);
	}
	
	protected void setIsNavigator(boolean flag) {
		this.mRgNavigator.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsRlMenu(boolean flag) {
		this.mrl_menu.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsDivTop(boolean flag) {
		this.mDivLineTop.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setIsDivBottom(boolean flag) {
		this.mDivLineBottom.setVisibility(flag ? View.VISIBLE : View.GONE);
	}

	protected void setBackOnClickListener(OnClickListener onClickListener) {
		this.mibtn_back.setOnClickListener(onClickListener);
	}

	protected void setIcoOnClickListener(OnClickListener onClickListener) {
		this.mibtn_ico.setOnClickListener(onClickListener);
	}

	protected void setSuggestOnClickListener(OnClickListener onClickListener) {
		this.mibtn_suggest.setOnClickListener(onClickListener);
	}

	protected void setRefreshOnClickListener(OnClickListener onClickListener) {
		this.mibtn_refresh.setOnClickListener(onClickListener);
	}

	protected void setEditOnClickListener(OnClickListener onClickListener) {
		this.mibtn_edit.setOnClickListener(onClickListener);
	}

	protected void setHotOnClickListener(OnClickListener onClickListener) {
		this.mtv_hot.setOnClickListener(onClickListener);
	}

	protected void setNewOnClickListener(OnClickListener onClickListener) {
		this.mtv_new.setOnClickListener(onClickListener);
	}

	protected void setMyOnClickListener(OnClickListener onClickListener) {
		this.mtv_my.setOnClickListener(onClickListener);
	}

	protected void setSaveOnClickListener(OnClickListener onClickListener) {
		this.mibtn_save.setOnClickListener(onClickListener);
	}

	protected void setMenuOnClickListener(OnClickListener onClickListener) {
		this.mibtn_menu.setOnClickListener(onClickListener);
	}

	protected void setLeftMenuOnClickListener(OnClickListener onClickListener)
	{
		this.mibtn_menu_left.setOnClickListener(onClickListener);
	}
	
	protected View inflater(int resource) {
		View view = mInflater.inflate(resource, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mrl_content.removeAllViews();
		mrl_content.addView(view);
		return view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meteomap_act_base);

		mRgNavigator = (RadioGroup) findViewById(R.id.rg_navigator);
		w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		mRgNavigator.measure(w, h);
		mNavigators[0] = mRgNavigator.getMeasuredWidth();
		mNavigators[1] = mRgNavigator.getMeasuredHeight();
		mibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		mibtn_ico = (ImageButton) findViewById(R.id.ibtn_ico);
		mtv_title = (TextView) findViewById(R.id.tv_title);
		mtv_info = (TextView) findViewById(R.id.tv_info);
		mibtn_refresh = (ImageButton) findViewById(R.id.ibtn_refresh);
		mibtn_edit = (ImageButton) findViewById(R.id.ibtn_edit);
		mibtn_save = (ImageButton) findViewById(R.id.ibtn_save);
		mibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
		mibtn_menu_left=(ImageButton) findViewById(R.id.ibtn_menu_left);
		mibtn_suggest = (ImageButton) findViewById(R.id.ibtn_suggest);
		mtv_hot = (TextView) findViewById(R.id.tv_hot);
		mtv_new = (TextView) findViewById(R.id.tv_new);
		mtv_my = (TextView) findViewById(R.id.tv_my);
		mDivLineTop = (View) findViewById(R.id.div_line_top);
		mDivLineBottom = (View) findViewById(R.id.div_line_bottom);

		mrl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
		w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		mrl_menu.measure(w, h);
		mMenus[0] = mrl_menu.getMeasuredWidth();
		mMenus[1] = mrl_menu.getMeasuredHeight();
		mrl_content = (RelativeLayout) findViewById(R.id.rl_content);

		mRadWeather = (RemindRadioButton) findViewById(R.id.rad_weather);
		mRadProduct = (RadioButton) findViewById(R.id.rad_product);
		mRadSituation = (RemindRadioButton) findViewById(R.id.rad_situation);
		mRadAdvisory = (RadioButton) findViewById(R.id.rad_advisory);
		mRadMore = (RadioButton) findViewById(R.id.rad_more);

		mLlWeather = (LinearLayout) findViewById(R.id.ll_weather);
		mLlProduct = (LinearLayout) findViewById(R.id.ll_product);
		mLlSituation = (LinearLayout) findViewById(R.id.ll_situation);
		mLlAdvisory = (LinearLayout) findViewById(R.id.ll_advisory);
		mLlMore = (LinearLayout) findViewById(R.id.ll_more);

		mRadWeather.setOnClickListener(this);
		mRadProduct.setOnClickListener(this);
		mRadSituation.setOnClickListener(this);
		mRadAdvisory.setOnClickListener(this);
		mRadMore.setOnClickListener(this);

		mLlWeather.setOnClickListener(this);
		mLlProduct.setOnClickListener(this);
		mLlSituation.setOnClickListener(this);
		mLlAdvisory.setOnClickListener(this);
		mLlMore.setOnClickListener(this);
		mibtn_back.setOnClickListener(this);

		mDisplay = Constant.getDisplay(this);
		mInflater = this.getLayoutInflater();
		
		initBadageView();
	}
	
	private void initBadageView(){
		mWeather_badge = new BadgeView(MeteoMapBaseActivity.this, mLlWeather);
		mWeather_badge.setTextSize(CommonUtil.dipToPixels(MeteoMapBaseActivity.this, 5));
		mWeather_badge.hide();
		
		mCrops_badge = new BadgeView(MeteoMapBaseActivity.this, mLlSituation);
//		mCrops_badge.setBackgroundResource(R.drawable.badge_ifaux);
		mCrops_badge.hide();
		
		mMore_badge = new BadgeView(MeteoMapBaseActivity.this, mLlMore);
		mMore_badge.setTextSize(CommonUtil.dipToPixels(MeteoMapBaseActivity.this, 5));
		
		mWeather_badge.hide();
	}
	
	public void update(int flag, int num) {
		Message msg = Message.obtain();
		msg.what = flag;
		msg.arg1 = num;
		if (mHandler != null) {
			mHandler.sendMessage(msg);
		}
	}

	protected int[] getMenuSize() {
		return mMenus;
	}

	protected int[] getNavigatorSize() {
		return mNavigators;
	}

	protected int[] getScreen() {
		return Constant.getDisplay(this);
	}

	protected int getMenuHeight() {
		return mMenus[1];
	}

	protected int getContentHeight() {
		return mDisplay[1] - mMenus[1] - mNavigators[1];
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_weather:
		case R.id.rad_weather:
			mIntent = new Intent(this, WeatherActivity.class);
			if (mNavigator != R.id.rad_weather)
				startIntent();
			break;
		case R.id.ll_product:
		case R.id.rad_product:
			// Toast.makeText(this, "产品为第二期功能，请使用天气和农田管理", Toast.LENGTH_SHORT).show();
			mIntent = new Intent(this, ProductActivity.class);
			if (mNavigator != R.id.rad_product)
				startIntent();
			break;
		case R.id.ll_situation:
		case R.id.rad_situation:
			// Toast.makeText(this, "农情为第二期功能，请使用天气和农田管理", Toast.LENGTH_SHORT).show();
			mIntent = new Intent(this, SituationActivity.class);
			if (mNavigator != R.id.rad_situation)
				startIntent();
			break;
		case R.id.ibtn_back:
			this.finish();
			break;
		case R.id.ll_advisory:
		case R.id.rad_advisory:
			// Toast.makeText(this, "咨询为第二期功能，请使用天气和农田管理", Toast.LENGTH_SHORT).show();
			mIntent = new Intent(this, AdvisoryActivity.class);
			if (mNavigator != R.id.rad_advisory)
				startIntent();
			break;
		case R.id.ll_more:
		case R.id.rad_more:
			mIntent = new Intent(this, SettingActivity.class);
			if (mNavigator != R.id.rad_more)
				startIntent();
			break;
		}
	}

	/**
	 * 
	 * @param id
	 *        RemindRadioButton 控件id
	 * @param num
	 *        提示数目
	 */
	public void drawRemindNum(int id, int num) {
		switch (id) {
		case R.id.rad_weather:
			mRadWeather.drawNum(num);
			break;
		case R.id.rad_situation:
			mRadSituation.drawNum(num);
			break;

		default:
			break;
		}
	}
	
//	/**
//	 * 
//	 * @param id
//	 *        RemindRadioButton 控件id
//	 * @param num
//	 *        提示数目
//	 */
//	public void drawBadageNum(int flag, int num) {
//		switch (id) {
//		case R.id.rad_weather:
//			mRadWeather.drawNum(num);
//			break;
//		case R.id.rad_situation:
//			mRadSituation.drawNum(num);
//			break;
//
//		default:
//			break;
//		}
//	}

	private void startIntent() {
		startActivity(mIntent);
		this.finish();
	}

	@Override
	protected void onResume() {
		Intent bindIntent = new Intent(MeteoMapBaseActivity.this, PisaService.class);
		bindService(bindIntent, mConnection, BIND_AUTO_CREATE);
		super.onResume();
	}

	@Override
	protected void onPause() {
		unbindService(mConnection);
		// System.out.println(" unbind ");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	

}
