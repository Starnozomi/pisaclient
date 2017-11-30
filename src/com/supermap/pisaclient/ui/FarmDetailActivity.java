/* 
. * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @FarmDetailActivity.java - 2014-4-30 下午4:11:54
 */

package com.supermap.pisaclient.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.supermap.android.maps.Point2D;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.AreaDao;
import com.supermap.pisaclient.biz.CommonDao;
import com.supermap.pisaclient.biz.CropPeriodDao;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DataUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CommonArea;
import com.supermap.pisaclient.entity.CommonHeight;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmCrop;
import com.supermap.pisaclient.entity.User;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.GeometryType;



public class FarmDetailActivity extends BaseActivity {

	private FarmDetailActivity oThis = this;
	private View mContent;
	private ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private TextView t1, t2;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private TextView mTvLng;
	private TextView mTvLat;
	private TextView mTvHeight;
	private Button mBtnRegion;
	private Button mBtnRegionCode;
	private Button mBtnWorkStation;
	private Button mBtnCropType;
	private Button mBtnCropVariety;
	private Button mBtnCropPeriod;
	private Button mBtnEdit;
	private TextView mTvArea;
	private Drawable mEtDrawable;
	private Drawable mLngDrawable;
	private Drawable mLatDrawable;
	private Drawable mAreaDrawable;
	private int mFarmId;
	private int mOp = 0;
	private FarmDao mFarmDao;
	private Farm mFarm;
	private String mName;
	private CropTypeDao mCropTypeDao;
	private CropPeriodDao mCropPeriodDao;
	private CropType mCropType;
	private CropType mCropVariety;
	private CropPeriod mCropPeriod;
	private FarmCrop mFarmCrop;
	private boolean isFarmland = false;
	private CustomProgressDialog mPdDialog;
	private AreaSelectParameter mAreaSelectParameter;
	private Feature f;
	private int mUserId;
	private CommonDao mCommonDao;
	private EditText mEtFarmName;
	private AreaDao mAreaDao;
	
	private int workStationId = 0;

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
	}

	
	//判断有无数据的方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent data = new Intent();
			data.putExtra("farmid", "-1");
			data.putExtra("descript", "无数据");
			setResult(Constant.ADD_FARM_RESULT, data);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	//初始化ViewPager控件
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.farm_fundation_layout, null));
		listViews.add(mInflater.inflate(R.layout.farm_crop_info, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);

		mPdDialog = CustomProgressDialog.createDialog(FarmDetailActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_farmdetail_data));

		if (mOp == 1) {//缂栬緫鍐滅敯灞炴�
			new LoadFarmTask().execute();
			addData();	
		} else if (mOp == 0) {//娣诲姞鍐滅敯
			addData();
			setFarm();	
		}
	}

	
	
	private class LoadFarmTask extends AsyncTask<Integer, Integer, Farm> {

		@Override
		protected Farm doInBackground(Integer... params) {
			return mFarmDao.getFarmAttributeByIdNew(mFarmId);
		}

		@Override
		protected void onPostExecute(Farm result) {
			if (result != null) {
				mFarm = result;
			}
			bindData(0);
			bindData(1);
			setEditable(false);
			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			//setFarm();
			if (!mPdDialog.isShowing())
				mPdDialog.show();
		}

	}

	private class LoadParentAreaTask extends AsyncTask<String, Integer, Area> {

		private String code;

		public LoadParentAreaTask(String code) {
			this.code = code;
		}

		@Override
		protected Area doInBackground(String... params) {
			return mAreaDao.getParentAreaByCode(code);
		}

		@Override
		protected void onPostExecute(Area result) {
			if (result != null) {
				if (mBtnRegion == null) {
					mBtnRegion = (Button) listViews.get(0).findViewById(R.id.btn_region);
					mBtnRegionCode = (Button) listViews.get(0).findViewById(R.id.btn_region_code);
				}
				mBtnRegion.setText(result.pareaname + "/" + result.areaname);
			}
		}

	}

	private class LoadAreaTask extends AsyncTask<String, Integer, CommonArea> {

		@Override
		protected CommonArea doInBackground(String... params) {
			return mCommonDao.getArea(Double.valueOf(params[0]), Double.valueOf(params[1]));
		}

		@Override
		protected void onPostExecute(CommonArea result) {
			if (result != null) {
				if (mBtnRegion == null) {
					mBtnRegion = (Button) listViews.get(0).findViewById(R.id.btn_region);
					mBtnRegionCode = (Button) listViews.get(0).findViewById(R.id.btn_region_code);
				}
				mBtnRegion.setText(result.countyName + "/" + result.townName);
				if ("".equals(mBtnRegionCode.getText().toString().trim())) {
					mBtnRegionCode.setText(result.townCode);
				}
				mFarm.code = result.townCode;
			}
		}

	}
	

	private class LoadHeightTask extends AsyncTask<String, Integer, CommonHeight> {

		@Override
		protected CommonHeight doInBackground(String... params) {
			return mCommonDao.getHeight(Double.valueOf(params[0]), Double.valueOf(params[1]));
		}

		@Override
		protected void onPostExecute(CommonHeight result) {
			if (result != null) {
				if (mTvHeight == null) {
					mTvHeight = (TextView) listViews.get(0).findViewById(R.id.et_height);
				}
				mTvHeight.setText(result.height + "");
				mFarm.height = result.height + "";
			}
		}
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.menu_line).getWidth();
		int[] data = Constant.getDisplay(this);
		offset = (data[0] / 2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		float sx = 1.0f * data[0] / 2 / bmpW;
		matrix.postScale(sx, 1.0f);
		cursor.setImageMatrix(matrix);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.FARM_REGION_REQUEST) {
			if (resultCode == Constant.CITY_SET_RESULT) {
				City city = (City) data.getSerializableExtra("city");
				if (city != null) {
					mBtnRegion.setText(city.name);
				}
			}
		}else if(resultCode == Constant.SELECT_WORKSTATION){
			Bundle b = data.getExtras();
			workStationId = b.getInt("workstationid");
			String workStationName = b.getString("workstationname");
			mBtnWorkStation.setText(workStationName);
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	

	public void setFarm() {
		com.supermap.services.components.commontypes.Point2D point = CommonUtil.getCenterPoint(f.geometry.points);
		List<com.supermap.services.components.commontypes.Point2D> all = Arrays.asList(f.geometry.points);
		List<Point2D> data = new ArrayList<Point2D>();
		StringBuffer sb = new StringBuffer();
		for (com.supermap.services.components.commontypes.Point2D p : all) {
			Point2D p2 = new Point2D();
			p2.x = p.x;
			p2.y = p.y;
			data.add(p2);
			sb.append(p.x + "," + p.y + " ");
		}

		sb.append(data.get(0).x + "," + data.get(0).y + " ");
		data.add(data.get(0));
		Geometry geometry = new Geometry();
		geometry.points = f.geometry.points;
		geometry.parts = new int[] { f.geometry.points.length };
		geometry.type = GeometryType.REGION;

		mFarm.latitude = point.y + "";
		mFarm.longitude = point.x + "";

		mFarm.area = CommonUtil.getMetreSquare(Constant.selectedScale, DataUtil.getArea(data)) + "";
		mTvLat.setText(CommonUtil.getCutString(mFarm.latitude, 2));
		mTvLng.setText(CommonUtil.getCutString(mFarm.longitude, 2));
		mTvArea.setText(CommonUtil.getCutString(mFarm.area, 2));
		String coordinates = sb.toString().trim();
		coordinates = coordinates.substring(0, coordinates.length() - 1);
		mFarm.coordinates = coordinates;
		mFarm.userId = mUserId;

		new LoadAreaTask().execute(new String[] { mFarm.longitude, mFarm.latitude });
		new LoadHeightTask().execute(new String[] { mFarm.longitude, mFarm.latitude });
	}

	//初始化activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.farm_feed_info));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);
		setIsNavigator(false);
		mContent = inflater(R.layout.farm_detail);

		mCommonDao = CommonDao.getInstance();
		mAreaDao = new AreaDao();
		f = (Feature) this.getIntent().getSerializableExtra("feature");
		mBtnEdit = (Button) mContent.findViewById(R.id.btn_edit);
		mBtnEdit.setOnClickListener(this);

		mFarm = new Farm();
		mFarmCrop = new FarmCrop();

		mCropTypeDao = new CropTypeDao();
		mCropPeriodDao = new CropPeriodDao();
		User user = UserDao.getInstance().get();
		if (user != null) {
			mUserId = user.id;
		}

		Intent intent = this.getIntent();
		mOp = intent.getIntExtra("op", 0); // /0表示添加 1表示修改
		if (mOp == 1) { //修改
			mFarmId = f.getID();
		} else {
			mFarmId = 0;
		}
		mFarmDao = new FarmDao();
		InitImageView();
		InitTextView();
		InitViewPager();
		setMenuOnClickListener(FarmDetailActivity.this);
		
		//初始化绑定数据
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
			
	}
	

	public class MyPagerAdapter extends PagerAdapter {

		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	
	//切换TAB的点击事件
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				t1.setTextColor(getResources().getColor(R.color.menu_title_selected));
				t2.setTextColor(getResources().getColor(R.color.menu_title));
				bindData(arg0);
			} else if (arg0 == 1) {
				t1.setTextColor(getResources().getColor(R.color.menu_title));
				t2.setTextColor(getResources().getColor(R.color.menu_title_selected));
				bindData(arg0);
			}
			Animation animation = null;
			animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void addData() {
		View view1 = listViews.get(0);
		if (mTvLng == null)
			mTvLng = (TextView) view1.findViewById(R.id.et_lng);
		if (mEtFarmName == null)
			mEtFarmName = (EditText) view1.findViewById(R.id.et_farm_name);
		if (mTvLat == null)
			mTvLat = (TextView) view1.findViewById(R.id.et_lat);
		if (mTvHeight != null)
			mTvHeight = (TextView) view1.findViewById(R.id.et_height);
		if (mBtnRegion == null) {
			mBtnRegion = (Button) view1.findViewById(R.id.btn_region);
		}
		if (mBtnRegionCode == null)
			mBtnRegionCode = (Button) view1.findViewById(R.id.btn_region_code);
		if (mTvArea == null)
			mTvArea = (TextView) view1.findViewById(R.id.et_area);
		if(mBtnWorkStation == null){
			mBtnWorkStation = (Button)view1.findViewById(R.id.btn_workstation_apply);
			mBtnWorkStation.setOnClickListener(this);
		}
		
		View view2 = listViews.get(1);
		if (mBtnCropType == null) {
			mBtnCropType = (Button) view2.findViewById(R.id.btn_type);
			mBtnCropType.setOnClickListener(this);
		}
		if (mBtnCropVariety == null) {
			mBtnCropVariety = (Button) view2.findViewById(R.id.btn_variety);
			mBtnCropVariety.setOnClickListener(this);
		}
		if (mBtnCropPeriod == null) {
			mBtnCropPeriod = (Button) view2.findViewById(R.id.btn_period);
			mBtnCropPeriod.setOnClickListener(this);
		}
		mBtnEdit.setText(getResources().getString(R.string.farm_new));
	}

	private void bindData(int index) {
		View view = listViews.get(index);
		switch (index) {
		case 0:
			if (mTvLng == null) {
				mTvLng = (TextView) view.findViewById(R.id.et_lng);
				if (mLngDrawable == null)
					mLngDrawable = mTvLng.getBackground();
			}
			if (mEtFarmName == null) {
				mEtFarmName = (EditText) view.findViewById(R.id.et_farm_name);
				if (mEtDrawable == null)
					mEtDrawable = mEtFarmName.getBackground();
			}
			if (mTvLat == null) {
				mTvLat = (TextView) view.findViewById(R.id.et_lat);
				if (mLatDrawable == null)
					mLatDrawable = mTvLat.getBackground();
			}
			if (mTvHeight == null) {
				mTvHeight = (TextView) view.findViewById(R.id.et_height);
				if (mLatDrawable == null)
					mLatDrawable = mTvHeight.getBackground();
			}
			if (mBtnRegion == null) {
				mBtnRegion = (Button) view.findViewById(R.id.btn_region);
			}
			if (mBtnRegionCode == null) {
				mBtnRegionCode = (Button) view.findViewById(R.id.btn_region_code);
			}
			if (mTvArea == null) {
				mTvArea = (TextView) view.findViewById(R.id.et_area);
				if (mAreaDrawable == null)
					mAreaDrawable = mTvArea.getBackground();
			}
			if (mFarm != null) {
				if (mFarm.code != null) {
					mBtnRegionCode.setText(mFarm.code);
				}
				if (mFarm.longitude != null && !"".equals(mFarm.longitude))
					mTvLng.setText(CommonUtil.getCutString(mFarm.longitude, 2));
				if (mFarm.descript != null && !"".equals(mFarm.descript))
					mEtFarmName.setText(mFarm.descript);
				if (mFarm.latitude != null && !"".equals(mFarm.latitude))
					mTvLat.setText(CommonUtil.getCutString(mFarm.latitude, 2));
				if (mFarm.height != null && !"".equals(mFarm.height))
					mTvHeight.setText(mFarm.height);
				if (mFarm.area != null && !"".equals(mFarm.area))
					mTvArea.setText(CommonUtil.getCutString(mFarm.area, 2));
						
				if (mFarm.cropVarietyId != null && !"null".equals(mFarm.cropVarietyId) && !"".equals(mFarm.cropVarietyId)) {
					isFarmland = true;
					//mBtnCropType.setText(mFarm.cropTypeName);
				}
				//
				if (mFarm.cropVarietyId != null && !"null".equals(mFarm.cropVarietyId) && !"".equals(mFarm.cropVarietyId)){
					mFarmCrop.crop = mFarm.cropVarietyId + "";
					//this.mBtnCropVariety.setText(mFarm.cropVarietyName);
				}
					
				if (mFarm.cropPeriodId != null && !"null".equals(mFarm.cropPeriodId) && !"".equals(mFarm.cropPeriodId))
					mFarmCrop.agrweaquota = mFarm.cropPeriodId + "";
				if (mFarm.id != 0)
					mFarmCrop.farmland = mFarm.id + "";
				if (mFarm.longitude != null && mFarm.latitude != null && !"".equals(mFarm.longitude) && !"".equals(mFarm.latitude)) {
					new LoadParentAreaTask(mFarm.code).execute();
				}
				
				if(mFarm.workStationId >0 && mFarm.workStationName != null){
					this.workStationId = mFarm.workStationId;
					this.mBtnWorkStation.setText(mFarm.workStationName);
				}
			}
			break;
		case 1:
			if (mBtnCropType == null) {
				mBtnCropType = (Button) view.findViewById(R.id.btn_type);
				mBtnCropType.setOnClickListener(this);
			}
			if (mBtnCropVariety == null) {
				mBtnCropVariety = (Button) view.findViewById(R.id.btn_variety);
				mBtnCropVariety.setOnClickListener(this);
			}
			if (mBtnCropPeriod == null) {
				mBtnCropPeriod = (Button) view.findViewById(R.id.btn_period);
				mBtnCropPeriod.setOnClickListener(this);
			}
			
			
			if (mFarm != null) {
				if (mFarm.cropTypeId != null && !"".equals(mFarm.cropTypeId) && !"null".equals(mFarm.cropTypeId)) {
					mCropType = new CropType();
					mCropType.id = Integer.valueOf(mFarm.cropTypeId);
					mCropType.name = mFarm.cropTypeName;

					mCropVariety = new CropType();
					mCropVariety.id = Integer.valueOf(mFarm.cropVarietyId);
					mCropVariety.name = mFarm.cropVarietyName;
					mCropVariety.parentId = Integer.valueOf(mFarm.cropTypeId);

					mBtnCropType.setText(mFarm.cropTypeName);
					mBtnCropVariety.setText(mFarm.cropVarietyName);
					mBtnCropPeriod.setText(mFarm.cropPeriodName);
					isFarmland = true;
				}
			}
			break;
		}
	}

	private void setEditable(boolean flag) {
		mTvLng.setEnabled(flag);
		mTvLat.setEnabled(flag);
		mTvHeight.setEnabled(flag);
		mTvArea.setEnabled(flag);
		mEtFarmName.setEnabled(flag);

		if (!flag) {
			mTvLng.setBackgroundColor(Color.TRANSPARENT);
			mTvLat.setBackgroundColor(Color.TRANSPARENT);
			mTvHeight.setBackgroundColor(Color.TRANSPARENT);
			mTvArea.setBackgroundColor(Color.TRANSPARENT);
			mEtFarmName.setBackgroundColor(Color.TRANSPARENT);
		} else {
			mTvLng.setBackgroundDrawable(mLngDrawable);
			mTvLat.setBackgroundDrawable(mLatDrawable);
			mTvHeight.setBackgroundDrawable(mLatDrawable);
			mTvArea.setBackgroundDrawable(mAreaDrawable);
			mEtFarmName.setBackgroundDrawable(mEtDrawable);
		}
	}

	public void onClick(View v) {
		Intent data = new Intent();
		switch (v.getId()) {
		case R.id.ibtn_back:
			data.putExtra("farmid", "-1");
			data.putExtra("descript", "无数据");
			setResult(Constant.ADD_FARM_RESULT, data);
			finish();
			break;
		case R.id.btn_region:
			// 弹出地区窗口
			if (mOp == 2 || mOp == 0) {
				Intent intent = new Intent(FarmDetailActivity.this, CitySetActivity.class);

				if (mAreaSelectParameter == null) {
					mAreaSelectParameter = new AreaSelectParameter();
					mAreaSelectParameter.flag = Constant.FARM_REGION_REQUEST;
					mAreaSelectParameter.isWeatherArea = false;
					mAreaSelectParameter.isSelectMore = true;
					mAreaSelectParameter.isShowRemind = false;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("parameter", mAreaSelectParameter);
				intent.putExtras(bundle);
				startActivityForResult(intent, Constant.FARM_REGION_REQUEST);
			}
			break;
		case R.id.btn_edit:
             	if (mOp == 0) { // 添加
				if (isValid()) {
					mFarm.descript = mName;
					String code = mBtnRegionCode.getText().toString();
					if (code != null & !"".equals(code) && code.length() == 12) {
						mFarm.areaCode = code.substring(0, code.length() - 3);
					} else {
						mFarm.areaCode = code;
					}
					if(workStationId == 0){
						CommonUtil.showToask(oThis, "请选择工作站");
					}else{
						mFarm.workStationId = workStationId;
					}
					String res = mFarmDao.addFarmland(mFarm);

					if (!"0".equals(res)) {
						if (mFarmCrop.crop != null && !"".equals(mFarmCrop.crop) && !"null".equals(mFarmCrop.crop)
								&& mFarmCrop.agrweaquota != null && !"".equals(mFarmCrop.agrweaquota)
								&& !"null".equals(mFarmCrop.agrweaquota)) {
							mFarmCrop.farmland = res;
					
							mFarmDao.addFarmCrops(mFarmCrop);
						}
						CommonUtil.showToask(this, getResources().getString(R.string.insert_farm_scrop_sucess));
						data.putExtra("farmid", res);
						data.putExtra("descript", mFarm.descript);
						setResult(Constant.ADD_FARM_RESULT, data);
						finish();
					} else {
						CommonUtil.showToask(this, getResources().getString(R.string.insert_farm_scrop_bad));
					}
				}
			} else if (mOp == 1) { // 显示->编辑
				setEditable(true);
				mOp = 2;
				mBtnEdit.setText(getResources().getString(R.string.save));
			} else if (mOp == 2) { // 编辑->保存
				if (isValid()) {
					setEditable(false);
					mOp = 1;
					mBtnEdit.setText(getResources().getString(R.string.edit));
					mFarm.descript = mName;
					String code = mBtnRegionCode.getText().toString();
					if (code != null & !"".equals(code) && code.length() == 12) {
						mFarm.areaCode = code.substring(0, code.length() - 3);
					} else {
						mFarm.areaCode = code;
					}
					if(workStationId > 0){
						mFarm.workStationId = workStationId;
					}

					if (mFarmDao.updateFarmlandAttribute(mFarm)) {
						mFarmCrop.farmland = mFarm.id + "";
						if (isFarmland) {
							if (!mFarmDao.updateFarmCrops(mFarmCrop)) {
								CommonUtil.showToask(this, getResources().getString(R.string.update_farm_scrop_bad));
								break;
							}
						} else {
							mFarmDao.addFarmCrops(mFarmCrop);
						}
						CommonUtil.showToask(this, getResources().getString(R.string.update_farm_scrop_sucess));
						data.putExtra("farmid", mFarm.id + "");
						data.putExtra("descript", mFarm.descript);
						setResult(Constant.ADD_FARM_RESULT, data);
						finish();
					} else
						CommonUtil.showToask(this, getResources().getString(R.string.update_farm_scrop_bad));
				}
			}
			break;
		case R.id.btn_type:    //种养类型
			if (mOp == 2 || mOp == 0 || mOp == 1) {
				final List<CropType> mCropTypes = mCropTypeDao.getCropTypes(0);
				if (mCropTypes != null && mCropTypes.size() > 0) {
					String[] datas = new String[mCropTypes.size()];
					for (int i = 0; i < mCropTypes.size(); i++) {
						datas[i] = mCropTypes.get(i).name;
					}
					new AlertDialog.Builder(FarmDetailActivity.this).setItems(datas, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mCropType = mCropTypes.get(which);
									mFarmCrop.crop = mCropType.id + "";
									mBtnCropType.setText(mCropType.name);
									setNoBreed();
									setNoGrowPeriod();
								}

					}).show();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.response_no_crop_info));
				}
			}
			break;
		case R.id.btn_variety:  //种养品种
			if (mOp == 2 || mOp == 0 || mOp == 1) {
				if (mCropType == null) {
					CommonUtil.showToask(this,getResources().getString(R.string.response_no_crop_info));
					break;
				}
				final List<CropType> mCropVarieties = mCropTypeDao.getCropTypes(mCropType.id);
				if (mCropVarieties != null && mCropVarieties.size() > 0) {
					String[] datas = new String[mCropVarieties.size()];
					for (int i = 0; i < mCropVarieties.size(); i++) {
						datas[i] = mCropVarieties.get(i).name;
					}
					new AlertDialog.Builder(FarmDetailActivity.this).setItems(datas, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropVariety = mCropVarieties.get(which);
							mCropType = mCropVarieties.get(which);
							mFarmCrop.crop = mCropVariety.id + "";
							mBtnCropVariety.setText(mCropVariety.name);
						}

					}).show();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.response_no_crop_info));
				}
			}
			break;
		case R.id.btn_period:   //
			if (mOp == 2 || mOp == 0) {
				if (mCropType == null) {
					CommonUtil.showToask(this,getResources().getString(R.string.response_no_crop_info));
					break;
				}
				final List<CropPeriod> mCropPeroids = mCropPeriodDao.getCropGrowPeriod(mCropType);
				if (mCropPeroids != null && mCropPeroids.size() > 0) {
					String[] datas = new String[mCropPeroids.size()];
					for (int i = 0; i < mCropPeroids.size(); i++) {
						datas[i] = mCropPeroids.get(i).growthperiod;
					}
					new AlertDialog.Builder(FarmDetailActivity.this).setItems(datas, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropPeriod = mCropPeroids.get(which);
							mFarmCrop.agrweaquota = mCropPeriod.id + "";
							//mBtnCropPeriod.setText(mCropPeriod.growthperiod);
						}

					}).show();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.response_no_crop_info));
				}
			}
			break;
			case R.id.btn_workstation_apply:           //隶属基地
				List<String> areaCodes =new ArrayList<String>();
				areaCodes.add(UserDao.getInstance().get().areaCode);
				 Intent  intent = new Intent(this, WorkStationSelectActivity.class);
				 Bundle bundle = new Bundle();
				 bundle.putSerializable("areas",(Serializable) areaCodes );
				//bundle.putString("parameter", areaCodes);
				intent.putExtra("parameter",bundle);
				startActivityForResult(intent, Constant.SELECT_WORKSTATION);
			break;
			}
	super.onClick(v);
	}
	
	private void setNoBreed(){
		mBtnCropVariety.setText("");
	}
	
	private void setNoGrowPeriod(){
		
		mCropPeriod =null;
		mFarmCrop.agrweaquota = "0";
		mBtnCropPeriod.setText("");
	}

	private boolean isValid() {
		mName = mEtFarmName.getText().toString();
		if ("".equals(mName)) {
			mEtFarmName.setFocusable(true);
			mEtFarmName.setFocusableInTouchMode(true);
			mEtFarmName.clearFocus();
			mEtFarmName.requestFocus();
			mEtFarmName.setSelection(mEtFarmName.length());
			CommonUtil.showToask(this, getResources().getString(R.string.farm_name_empty));
			return false;
			} else if (!CommonUtil.isValidString(mName)) {
		mEtFarmName.setFocusable(true);
			mEtFarmName.setFocusableInTouchMode(true);
			mEtFarmName.clearFocus();
			mEtFarmName.requestFocus();
			mEtFarmName.setSelection(mEtFarmName.length());
			CommonUtil.showToask(this, getResources().getString(R.string.farm_name_no_rows));
			return false;
		}
		return true;
	}
}
