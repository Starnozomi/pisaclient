/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SituationActivity.java - 2014-3-21 下午2:46:59
 */

package com.supermap.pisaclient.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CropSituationAdapter;
import com.supermap.pisaclient.biz.AdvMaxNumDao;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.biz.CropUploadDao;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.AdvCache;
import com.supermap.pisaclient.cache.CropsCache;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ElapseTime;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CategoryDialog;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.LeftMenuCategoryDialog;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.Address;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase;
import com.supermap.pisaclient.pullrefresh.PullToRefreshListView;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.supermap.pisaclient.service.PreferencesService;
import com.supermap.pisaclient.ui.AdvisoryQuestionActivity.LoadMaxDataNumTask;

public class SituationActivity extends BaseActivity implements OnScrollListener,OnRefreshListener<ListView>{

	private View mContent;
	private View mCommtent;
	private ListView mListView;
	private CropSituationAdapter mAdapter;
	private View mMoreView;
	private ProgressBar mpg;
	private Button mbtn_add_more;
	private Button mSend;
	private EditText met_comment;
	private Handler handler;
	private List<String> mList = new ArrayList<String>();
	
	private List<CategoryItem> cityData ;
	public int mType = -1;
	private String areaCode="50";
	private String area="50";
	private CityDao cityDao;
	private int jugg = 0;
	private TextView city, crop; //城市 和 作物 两个筛选 TextView 
	private Button sure; //筛选条件 提交按钮
	
	// private List<CropInfo> mAllCropInfos = new ArrayList<CropInfo>();
	private List<AgrInfo> mAllAgrInfos = new ArrayList<AgrInfo>();
	private List<AgrinfoComment> mAgrinfoComments = new ArrayList<AgrinfoComment>();
	private int lastVisibleIndex = 0;
	private int MaxDateNum = 0;
	private boolean isRemoveAddMoreView = false;
	private CustomProgressDialog mPdDialog;
	private int mAgrInfoId;
	private int mParentCommentId;
	private String mComment;
	private CropDao mCropDao;
	private CityDao mCityDao;
	private int mIndexPage = 0;
	private int mMaxPageSize = 5;
	private long firstTime = 0;
	private User mUser;
	private int  mUserID;
	private LinearLayout search;
	private CropsCache mCropsCache;
	private LinkedList<AgrInfo> mAgrInfos = new LinkedList<AgrInfo>();
	private PreferencesService mPreferencesService;
	private LeftMenuCategoryDialog mDialog;
	
	private PullToRefreshListView mPullListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private String mAreaCode;
	private int mCurrentSelectedCropType = -1;
	
	private void initListview(){
		mListView.setDivider(new ColorDrawable(Color.parseColor("#dcdcdc"))); 
		mListView.setDividerHeight(CommonUtil.dip2px(SituationActivity.this, 1));
		mListView.setCacheColorHint(Color.parseColor("#00000000"));
	}

	private void initView() {
		mContent = inflater(R.layout.cropsituation_main);
		mPullListView = (PullToRefreshListView) mContent .findViewById(R.id.refreshable_view);
		mListView = mPullListView.getRefreshableView();
		initListview();
		
		city = (TextView) findViewById(R.id.city);
		city.setOnClickListener(this);
		
		crop = (TextView) findViewById(R.id.crop);
		crop.setOnClickListener(this);
		
		sure = (Button) findViewById(R.id.sure);
		crop.setOnClickListener(this);
		
		Intent intent = getIntent();
		search = (LinearLayout) findViewById(R.id.search);
		search.setVisibility(View.VISIBLE);
		if("0".equals(intent.getStringExtra("jugg"))){
			search.setVisibility(View.GONE);
		}
		
		mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(false);
//		mRefreshableView = (RefreshableView) mContent.findViewById(R.id.refreshable_view);
        
      //加载更多视图
  		mMoreView = getLayoutInflater().inflate(R.layout.add_more_data, null);
  		mbtn_add_more = (Button) mMoreView.findViewById(R.id.bt_load);
  		mpg = (ProgressBar) mMoreView.findViewById(R.id.pg_add_more);
  		mbtn_add_more.setOnClickListener(this);
  		mListView.addFooterView(mMoreView);
  	

		mCommtent = mContent.findViewById(R.id.ll_comment);
		met_comment = (EditText) mContent.findViewById(R.id.et_comment);
		mSend = (Button) mContent.findViewById(R.id.bt_send_comment);
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mComment = met_comment.getText().toString();
				if((mComment==null)||(mComment.equals(""))){
					CommonUtil.showToask(SituationActivity.this, "评论不能为空");
					return;
				}
				if (UserDao.getInstance().get() == null) {
					CommonDialog.setLoginDialog(SituationActivity.this);
				} else {
					mAdapter.dismisPopWindow();
					new uploadComment().execute();
					// 防止重复发送
					setCommentLayoutVisible(false, mAgrInfoId, mParentCommentId, null);
				}
			}
		});
		mAgrInfos.addAll(mCropsCache.getCopsList(mUserID));
		mAdapter = new CropSituationAdapter(this, mAgrInfos);
		
		if ((mAllAgrInfos==null) || (mAllAgrInfos.size()<mMaxPageSize)) {
			mMoreView.setVisibility(View.INVISIBLE);
		}
		mListView.setAdapter(mAdapter);
		mAdapter.setListView(mListView);

		
		mPullListView.setOnRefreshListener(this);
		mListView.setOnScrollListener(this);
        //当service 更新有新的农情动态时，显示加载
//      mPullListView.doPullRefreshing(true, 500);
		mPdDialog = CustomProgressDialog.createDialog(SituationActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
	}
	
	private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullListView.setLastUpdatedLabel(text);
    }
	
	  private String formatDateTime(long time) {
	        if (0 == time) {
	            return "";
	        }
	        return mDateFormat.format(new Date(time));
	    }
	 
	 private void initCropUploadView(){
		TextView askTextView = (TextView)findViewById(R.id.tv_my);
		askTextView.setText("上报");
		setIsMy(true);
		setMyOnClickListener(this);
		
//		setIsRefresh(true);
//		ImageButton upload = (ImageButton)findViewById(R.id.ibtn_refresh);
//		upload.setImageResource(R.drawable.upload_actecture);
//		setRefreshOnClickListener(this);
	 }
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
//		testdata();

		setTvTitle(Utils.getString(this, R.string.situation_title));
		setRgNavigator(R.id.rad_situation);
		
		setLeftMenu(true);
		setLeftMenuOnClickListener(SituationActivity.this);
		
		initCropUploadView();
		mCropDao = new CropDao();
		mCityDao = new CityDao(this);
		mCropsCache = new CropsCache();
		mCropTypeDao = new CropTypeDao();
		mPreferencesService = new PreferencesService(SituationActivity.this);
		LocalHelper.getInstance(SituationActivity.this).init();

		mUser = UserDao.getInstance().get();
		cityData = mCityDao.getCounty();  
		
		if(mUser==null){
			mUserID = -1;
		} else {
			mUserID = mUser.id;
		}
	
		initView();
//		refresh();

		if (mUser== null) {// 没有登陆，就只能查看当前县作物类型
			String city = LocalHelper.getInstance(SituationActivity.this).getCity();
		} else {
			
		}
		
		
		refresh();
		
	}

//	@Override
//	protected void onPause() {
//		// mBinder.setmCrops_Remind_num(0);
//		super.onPause();
//
//	}

	@Override
	protected void onResume() {
		super.onResume();
		//refresh();
	}

	final String[] provices = new String[] { "种养品种", "区县城市" };
	final boolean initChoiceSets[] = { false,false };
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//农情上报入口
//		case R.id.ibtn_refresh:
		case R.id.tv_my:
//			mPullListView.doPullRefreshing(true, 500);
//			refresh();
//			mRefreshableView.manualRefresh();
			if (!CommonUtil.checkNetState(this)) {
				CommonUtil.showToask(this, "请检查网络连接");
				break;
			}
			Intent intent = new Intent();
			intent.setClass(this, CropUploadActivity.class);
			intent.putExtra("croptype", "");
			intent.putExtra("cropvarietyname", "");
			startActivity(intent);
			break;
		case R.id.city: //选择城市
			mDialog = new LeftMenuCategoryDialog(SituationActivity.this, getScreen()[0], getMenuHeight());
			jugg = 1;
			if(cityData!=null) {
				mDialog.setData(cityData);
				mDialog.setOnItemClickListener(new OnItemClickListener() {
					@Override
					 public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						areaCode = cityData.get(arg2).Code;
						area = areaCode;
						city.setText(cityData.get(arg2).CategoryName);
						mDialog.destroy();
				     }
				    });	
				    mDialog.show();
			} else {
				Toast.makeText(SituationActivity.this, "未加载到城市数据", Toast.LENGTH_SHORT).show();
			}
	        break;
		case R.id.crop: //选择作物类型
			mDialog = new LeftMenuCategoryDialog(SituationActivity.this, getScreen()[0], getMenuHeight());
			jugg = 0;
        		if(cropTypeData!=null) {
    				mDialog.setData(cropTypeData);
    				mDialog.setOnItemClickListener(new OnItemClickListener()  {
    					@Override
    					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    						mCurrentSelectedCropType = cropTypeData.get(arg2).CategoryID;
    						crop.setText(cropTypeData.get(arg2).CategoryName);
    						mDialog.destroy();
    					}
    					});	
    					mDialog.show();
    			} else {
    				Toast.makeText(SituationActivity.this, "未加载到作物类型数据", Toast.LENGTH_SHORT).show();
    			}
			break;
		case R.id.sure: //提交筛选条件
			if("".equals(city.getText().toString())){ //城市类型为空 则启动作物筛选
				LoadNewDataInfo(mCurrentSelectedCropType);
			} else if("".equals(crop.getText().toString())){ //作物类型为空 则启动城市筛选
				area = areaCode;
				new LoadNewDataTaskByCity(areaCode).execute();
			}
			
		    break;
		case R.id.ibtn_menu_left:
			    AlertDialog.Builder builder = new Builder(this);
		        builder.setItems(provices, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface arg0, int arg1) {
		            	mDialog = new LeftMenuCategoryDialog(SituationActivity.this, getScreen()[0], getMenuHeight());
		            	jugg = arg1;
		            	if(arg1 == 0){
		            		if(cropTypeData!=null) {
		        				mDialog.setData(cropTypeData);
		        				mDialog.setOnItemClickListener(new OnItemClickListener()  {
		        					@Override
		        					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		        						mCurrentSelectedCropType = cropTypeData.get(arg2).CategoryID;
		        						LoadNewDataInfo(mCurrentSelectedCropType);
		        						mDialog.destroy();
		        					}
		        					});	
		        					mDialog.show();
		        			} else {
		        				Toast.makeText(SituationActivity.this, "未加载到作物类型数据", Toast.LENGTH_SHORT).show();
		        			}
		            	} else if(arg1 == 1){
		            		if(cityData!=null) {
		        				mDialog.setData(cityData);
		        				mDialog.setOnItemClickListener(new OnItemClickListener() {
		        					@Override
		        					 public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		        						areaCode = cityData.get(arg2).Code;
		        						area = areaCode;
		        						new LoadNewDataTaskByCity(areaCode).execute();
		        						mDialog.destroy();
		        				     }
		        				    });	
		        				    mDialog.show();
		        			} else {
		        				Toast.makeText(SituationActivity.this, "未加载到城市数据", Toast.LENGTH_SHORT).show();
		        			}
		            	}
		            }
		        });
		        builder.create().show();
		        break;
		}
		super.onClick(v);
	}

public void LoadNewDataInfo(int cropTypeId)
	{
		if(cropTypeId==-1)
		{
			new LoadNewDataTask().execute();
		}
		else
		{
			new LoadSpecifiedTypeDataTask(cropTypeId).execute();
		}
	}
	
	public void LoadMoreDataInfo(int cropTypeId)
	{
		if(cropTypeId==-1)
		{
			new LoadMoreDataTask().execute();
		}
		else
		{
			new LoadSpecifiedTypeMoreDataTask(cropTypeId).execute();
		}
	}
	
	/**
	 * 是否显示回复框
	 * @param flag
	 * @param id
	 * @param commentid
	 * @param commentUserName
	 */
	public void setCommentLayoutVisible(boolean flag, int id, int commentid, String commentUserName) {
		mAgrInfoId = id;
		mParentCommentId = commentid;
		this.mCommtent.setVisibility(flag ? View.VISIBLE : View.GONE);
		if (flag) {// 输入框弹出时，自动弹出输入键盘
			met_comment.requestFocus();
			InputMethodManager imm = (InputMethodManager) met_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}
		if (flag && (mParentCommentId != -1)) {// 在显示并且回复某条回复的时候，提示回复某人
			met_comment.setHint(Html.fromHtml("<font color=#dcdcdc>" + "回复" + commentUserName + ":" + "</font>"));
		} else {
			met_comment.setText("");
			met_comment.setHint("");
		}
	}

	@Override
	public void onBackPressed() {

		if (this.mCommtent.getVisibility() == View.VISIBLE) {
			setCommentLayoutVisible(false, mAgrInfoId, -1, null);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(this).close();
		mCropsCache.saveCrops(mAgrInfos, mUserID);
		super.onDestroy();
	}

	public void refresh() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, "请检查网络连接");
			mPullListView.onPullDownRefreshComplete();
			return;
		}
		new LoadNewDataTask().execute();
	}
	
	public void loadMore() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, "请检查网络连接");
//			mPullListView.onPullUpRefreshComplete();
			mMoreView.setVisibility(View.INVISIBLE);
			return;
		}
		LoadMoreDataInfo(mCurrentSelectedCropType);
		//new LoadMoreDataTask().execute();
	}
	
	public void loadMoreByCity() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, "请检查网络连接");
//			mPullListView.onPullUpRefreshComplete();
			mMoreView.setVisibility(View.INVISIBLE);
			return;
		}
		new LoadMoreDataTaskByCity(area).execute();
		//new LoadMoreDataTask().execute();
	}
	
	
	public void refreshData(){
		mAdapter.notifyDataSetChanged();
	}

	private class uploadComment extends AsyncTask<String, integer, Boolean> {
		
		private AgrinfoComment mNewComment;

		@Override
		protected Boolean doInBackground(String... params) {// 这个参数//返回值
			CropUploadDao uploadDao = new CropUploadDao();
			int commentid = uploadDao.addAgrComment(mComment, mAgrInfoId, UserDao.getInstance().get().id, mParentCommentId);
			if (commentid == 0) {
				return false;
			}
			mNewComment = mCropDao.getAgrComment(commentid);
			if (mNewComment!=null) {
				mCityDao.addAgrinfoComment(mNewComment);
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setCommentLayoutVisible(false, mAgrInfoId, mParentCommentId, null);
//				CommonUtil.showToask(SituationActivity.this, "评论上传成功");
				refreshComments();
			} else {
				CommonUtil.showToask(SituationActivity.this, "评论上传失败");
			}
			
			super.onPostExecute(result);
		}
		
		private void refreshComments() {
			if (mNewComment==null) {
				return;
			}
			int position = mAdapter.getAgrinfoPosition();
			List<AgrinfoComment> list = mAgrInfos.get(position).mComments;
			if (list==null) {
				list = new ArrayList<AgrinfoComment>();
				mAgrInfos.get(position).mComments = list;
			}
			list.add(mNewComment);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private class LoadSpecifiedTypeDataTask extends AsyncTask<String, Integer, List<AgrInfo>> {
		private int typeId;
		public LoadSpecifiedTypeDataTask(int typeId)
		{
			this.typeId=typeId;
		}
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			ElapseTime cropElapseTime = new ElapseTime("农情动态");
			cropElapseTime.start();
			List<AgrInfo> result = new ArrayList<AgrInfo>();
			mIndexPage = 1;
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				String city = LocalHelper.getInstance(SituationActivity.this).getCity();
				
				mAreaCode = mCityDao.queryAreacode(city);
				if (mAreaCode==null) {
					return result;
				}
				result = mCropDao.getAgrInfosByAreacode(mAreaCode,typeId,mMaxPageSize,mIndexPage);
			} else {
				mUser = UserDao.getInstance().get();
				result = mCropDao.getAgrInfos(mUser.id,typeId,mMaxPageSize,mIndexPage);
			}
			cropElapseTime.end();
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			mBinder.setmCrops_Remind_num(0);
			mPullListView.onPullDownRefreshComplete();
			new LoadSpecifiedTypeMaxNumTask(typeId).execute();
			setLastUpdateTime();
			
			if ((result!=null)&&(result.size()>0)) {
				saveCropsLastRefreshTime(result.get(0).uploadTime);
				mAgrInfos.clear();
				mAgrInfos.addAll(result);
				mAdapter.setData(mAgrInfos);
//				mPullListView.setScrollLoadEnabled(true);
			}	
		}
	}
	
	private class LoadSpecifiedTypeMaxNumTask extends AsyncTask<String, Integer, Integer>{
		private int typeId;
		public LoadSpecifiedTypeMaxNumTask(int typeId)
		{
			this.typeId=typeId;
		}
		@Override
		protected Integer doInBackground(String... params) {
			ElapseTime cropNumElapseTime = new ElapseTime("农情数目");
			cropNumElapseTime.start();
			int maxNum = 0;
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				if (mAreaCode==null) {
					return maxNum;
				}
				maxNum = mCropDao.getAgrInfosByAreacodeNum(mAreaCode,typeId);
			}
			else {
				mUser = UserDao.getInstance().get();
				maxNum = mCropDao.getAgrInfosNum(mUser.id,typeId);
			}
			cropNumElapseTime.end();
			return maxNum;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result==-1) {//加载失败
				
			}
			else {
				MaxDateNum = result;
			}
		}
	}
	
	private class LoadNewDataTask extends AsyncTask<String, Integer, List<AgrInfo>> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			ElapseTime cropElapseTime = new ElapseTime("农情动态");
			cropElapseTime.start();
			List<AgrInfo> result = new ArrayList<AgrInfo>();
			mIndexPage = 1;
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				String city = LocalHelper.getInstance(SituationActivity.this).getCity();
				
				mAreaCode = mCityDao.queryAreacode(city);
				if (mAreaCode==null) {
					return result;
				}
				//mBinder.setLocalArea(mAreaCode);
				result = mCropDao.getAgrInfosByAreacode(mAreaCode,mMaxPageSize,mIndexPage);
			} else {
				mUser = UserDao.getInstance().get();
				result = mCropDao.getAgrInfos(mUser.id,mMaxPageSize,mIndexPage);
			}
			cropElapseTime.end();
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			if(UserDao.getInstance().get() != null) {
				mBinder.setmCrops_Remind_num(0);
				mPullListView.onPullDownRefreshComplete();
				new LoadMaxNumTask().execute();
				setLastUpdateTime();
				
				if ((result!=null)&&(result.size()>0)) {
					saveCropsLastRefreshTime(result.get(0).uploadTime);
					mAgrInfos.clear();
					mAgrInfos.addAll(result);
					mAdapter.setData(mAgrInfos);
				}
			}
			
			
			if(mUser==null)
			{
				if(mAreaCode!=null)
				{
					//通过区域代码获取种养类型
					new LoadAgrTypeInfoTask(mAreaCode).execute();
				}
				else
				{
					cropTypeData=null;
				}
			}
			else
			{
				//通过用户的区域代码来获取种养类型
				new LoadAgrTypeInfoTask(mUser.areaCode).execute();
			}
		}

	}
	
	private class LoadNewDataTaskByCity extends AsyncTask<String, Integer, List<AgrInfo>> {
		
		private String areaCode;
		private LoadNewDataTaskByCity(String areaCode){
			this.areaCode = areaCode;
		}
		
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			ElapseTime cropElapseTime = new ElapseTime("农情动态");
			cropElapseTime.start();
			List<AgrInfo> result = new ArrayList<AgrInfo>();
			mIndexPage = 1;
				result = mCropDao.getAgrInfosByAreacode(areaCode, mMaxPageSize, mIndexPage);
			cropElapseTime.end();
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			if(UserDao.getInstance().get() != null) {
				mBinder.setmCrops_Remind_num(0);
				mPullListView.onPullDownRefreshComplete();
				new LoadMaxNumTaskByCity(areaCode).execute();
				setLastUpdateTime();
				
				if ((result!=null)&&(result.size()>0)) {
					saveCropsLastRefreshTime(result.get(0).uploadTime);
					mAgrInfos.clear();
					mAgrInfos.addAll(result);
					mAdapter.setData(mAgrInfos);
				}
			}
			
			
			if(mUser==null)
			{
				if(mAreaCode!=null)
				{
					//通过区域代码获取种养类型
					new LoadAgrTypeInfoTask(mAreaCode).execute();
				}
				else
				{
					cropTypeData=null;
				}
			}
			else
			{
				//通过用户的区域代码来获取种养类型
				new LoadAgrTypeInfoTask(mUser.areaCode).execute();
			}
		}

	}
	
	private List<CropType> mCropTypeList; 
	private CropTypeDao mCropTypeDao;
	private List<CategoryItem> cropTypeData ;
	/**
	 * 获取种养类型
	 * @author trq
	 *
	 */
	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean>
	{
		private String areaCode;
		public LoadAgrTypeInfoTask(String areaCode)
		{
			this.areaCode=areaCode;
		}
		private boolean mLoadPass = false;
		@Override
		protected Boolean doInBackground(Integer... params) {
			//mCropTypeList = mCropTypeDao.getCropTypes(areaCode.substring(0,6));
			if(areaCode.length()<6){
				return null;
			}
			mCropTypeList = mCropTypeDao.getCropTypes(areaCode.substring(0,6));
			if (mCropTypeList != null) {
				mLoadPass = true;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Boolean result) 
		{
			if (mCropTypeList != null && mCropTypeList.size() > 0) 		
			{
				cropTypeData = new ArrayList<CategoryItem>();
				for (int i = 0; i < mCropTypeList.size(); i++)
				{
					CategoryItem categoryItem=new CategoryItem();
					categoryItem.CategoryName=mCropTypeList.get(i).name;
					categoryItem.CategoryID=mCropTypeList.get(i).id;
					cropTypeData.add(categoryItem);
				}			
			}
		}
	}
	
	private void saveCropsLastRefreshTime(String lastRefreshTime){
		mPreferencesService.saveCropLastRefreshTime(mUserID, lastRefreshTime);
	}
	
	private class LoadSpecifiedTypeMoreDataTask extends AsyncTask<String, Integer, List<AgrInfo>> {
		private int typeId;
		public LoadSpecifiedTypeMoreDataTask(int typeId)
		{
			this.typeId=typeId;
		}
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			mIndexPage++;
			List<AgrInfo> result = new ArrayList<AgrInfo>();
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				String city = LocalHelper.getInstance(SituationActivity.this).getCity();
				
				String areacode = mCityDao.queryAreacode(city);
				result = mCropDao.getAgrInfosByAreacode(areacode,typeId,mMaxPageSize,mIndexPage);
			} else {
				mUser = UserDao.getInstance().get();
				result = mCropDao.getAgrInfos(mUser.id,typeId,mMaxPageSize,mIndexPage);
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			
			mPullListView.onPullUpRefreshComplete();
			if (result!=null) {//加载成功
				int counts = mAgrInfos.size()+result.size();
				if (counts>=MaxDateNum) {
//					mPullListView.setScrollLoadEnabled(false);
//					CommonUtil.showToask(SituationActivity.this,"没有更多数据");
				}
				mAgrInfos.addAll(result);
				mAdapter.setData(mAgrInfos);
			}else {//加载失败
				mIndexPage--;
			}
		}

	}
	
	private class LoadMoreDataTask extends AsyncTask<String, Integer, List<AgrInfo>> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			mIndexPage++;
			List<AgrInfo> result = new ArrayList<AgrInfo>();
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				String city = LocalHelper.getInstance(SituationActivity.this).getCity();
				
				String areacode = mCityDao.queryAreacode(city);
				result = mCropDao.getAgrInfosByAreacode(areacode,mMaxPageSize,mIndexPage);
			} else {
				mUser = UserDao.getInstance().get();
				result = mCropDao.getAgrInfos(mUser.id,mMaxPageSize,mIndexPage);
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			
			mPullListView.onPullUpRefreshComplete();
			if (result!=null) {//加载成功
				int counts = mAgrInfos.size()+result.size();
				if (counts>=MaxDateNum) {
//					mPullListView.setScrollLoadEnabled(false);
//					CommonUtil.showToask(SituationActivity.this,"没有更多数据");
				}
				mAgrInfos.addAll(result);
				mAdapter.setData(mAgrInfos);
			}else {//加载失败
				mIndexPage--;
			}
		}

	}
	
	private class LoadMoreDataTaskByCity extends AsyncTask<String, Integer, List<AgrInfo>> {
		private String areaCode;
		private LoadMoreDataTaskByCity(String areaCode) {
			this.areaCode = areaCode;
		}
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			mIndexPage++;
			List<AgrInfo> result = new ArrayList<AgrInfo>();
				result = mCropDao.getAgrInfosByAreacode(areaCode,mMaxPageSize,mIndexPage);
			return result;
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			mPullListView.onPullUpRefreshComplete();
			if (result!=null) {//加载成功
				int counts = mAgrInfos.size()+result.size();
				if (counts>=MaxDateNum) {
//					mPullListView.setScrollLoadEnabled(false);
//					CommonUtil.showToask(SituationActivity.this,"没有更多数据");
				}
				mAgrInfos.addAll(result);
				mAdapter.setData(mAgrInfos);
			}else {//加载失败
				mIndexPage--;
			}
		}

	}
	
	/**
	 * 有时加载最大数目比较耗时
	 * @author kael
	 *
	 */
	
	private class LoadMaxNumTask extends AsyncTask<String, Integer, Integer>{
		@Override
		protected Integer doInBackground(String... params) {
			ElapseTime cropNumElapseTime = new ElapseTime("农情数目");
			cropNumElapseTime.start();
			int maxNum = 0;
			if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
				if (mAreaCode==null) {
					return maxNum;
				}
				maxNum = mCropDao.getAgrInfosByAreacodeNum(mAreaCode);
			}
			else {
				mUser = UserDao.getInstance().get();
				maxNum = mCropDao.getAgrInfosNum(mUser.id);
			}
			cropNumElapseTime.end();
			return maxNum;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result==-1) {//加载失败
				
			}
			else {
				MaxDateNum = result;
			}
		}
	}
	
	/**
	 * 有时加载最大数目比较耗时
	 * @author kael
	 *
	 */
	
	private class LoadMaxNumTaskByCity extends AsyncTask<String, Integer, Integer>{
		private String areaCode;
		private LoadMaxNumTaskByCity(String areaCode){
			this.areaCode = areaCode;
		}
		protected Integer doInBackground(String... params) {
			ElapseTime cropNumElapseTime = new ElapseTime("农情数目");
			cropNumElapseTime.start();
			int maxNum = 0;
			maxNum = mCropDao.getAgrInfosByAreacodeNum(areaCode);
			cropNumElapseTime.end();
			return maxNum;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result==-1) {//加载失败
			}
			else {
				MaxDateNum = result;
			}
		}
	}
	
	public void testdata(){
		User user = new User();
		user.id=71;
		user.isExpert = true;
		UserDao.getInstance().save(user, true);
	}
	

/*	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				CommonUtil.showToask(SituationActivity.this, getResources().getString(R.string.exit_wait));
				firstTime = secondTime;
				return true;
			} else {
				ExitApplication.getInstance().exit(0);
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/

	
	/**
	 * listview 滑动监听器实现
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		drawRemindNum(R.id.rad_situation, Constant.NO_REMIND_FLAG);
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount() && mAdapter.getCount()<MaxDateNum) {
			mMoreView.setVisibility(View.VISIBLE);
			if(jugg == 1){
				loadMoreByCity();
			} else {
			 loadMore();
			}
		}
		if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)&&(lastVisibleIndex>=MaxDateNum)) {
			CommonUtil.showToask(SituationActivity.this, "没有更多数据");
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		if (totalItemCount >= MaxDateNum) {
			mMoreView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * pulllistview 滑动监听器
	 * @param refreshView
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
//		refresh();
		LoadNewDataInfo(mCurrentSelectedCropType);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		//loadMore();
		LoadMoreDataInfo(mCurrentSelectedCropType);
	}
}
