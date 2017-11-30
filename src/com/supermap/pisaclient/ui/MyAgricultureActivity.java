
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

import com.supermap.pisaclient.MyActivity;
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
import com.supermap.pisaclient.entity.CropInfo;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase;
import com.supermap.pisaclient.pullrefresh.PullToRefreshListView;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.supermap.pisaclient.service.PreferencesService;
import com.supermap.pisaclient.ui.AdvisoryQuestionActivity.LoadMaxDataNumTask;

public class MyAgricultureActivity extends BaseActivity implements OnScrollListener,OnRefreshListener<ListView>{

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
//	private TextView city, crop; //城市 和 作物 两个筛选 TextView 
//	private Button sure; //筛选条件 提交按钮
	
	private List<CropInfo> mAllCropInfos = new ArrayList<CropInfo>();
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
	private LinkedList<AgrInfo> mAgrInfos; //= new LinkedList<AgrInfo>();
	private PreferencesService mPreferencesService;
	private LeftMenuCategoryDialog mDialog;
	
	private PullToRefreshListView mPullListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private String mAreaCode;
	private int mCurrentSelectedCropType = -1;
	
	private void initListview(){
		mListView.setDivider(new ColorDrawable(Color.parseColor("#dcdcdc"))); 
		mListView.setDividerHeight(CommonUtil.dip2px(MyAgricultureActivity.this, 1));
		mListView.setCacheColorHint(Color.parseColor("#00000000"));
	}

	private void initView() {
		mPullListView = (PullToRefreshListView) mContent .findViewById(R.id.refreshable_view);
		mListView = mPullListView.getRefreshableView();
		initListview();
		
//		mPullListView.setPullLoadEnabled(false);
//      mPullListView.setScrollLoadEnabled(false);
//		mRefreshableView = (RefreshableView) mContent.findViewById(R.id.refreshable_view);
        
        //加载更多视图
  		mMoreView = getLayoutInflater().inflate(R.layout.add_more_data, null);
  		mbtn_add_more = (Button) mMoreView.findViewById(R.id.bt_load);
  		mpg = (ProgressBar) mMoreView.findViewById(R.id.pg_add_more);
  		mbtn_add_more.setOnClickListener(this);
  		mListView.addFooterView(mMoreView);
  	
		mCommtent = mContent.findViewById(R.id.ll_comment);
		met_comment = (EditText) mContent.findViewById(R.id.et_comment);

//		mAgrInfos = new LinkedList<AgrInfo>();
//		mAgrInfos.addAll(mCropsCache.getCopsList(mUserID));
//		mAdapter = new CropSituationAdapter(this, mAgrInfos);
//		mListView.setAdapter(mAdapter);
//		mAdapter.setListView(mListView);
		
		if ((mAllAgrInfos == null) || (mAllAgrInfos.size() < mMaxPageSize)) {
			mMoreView.setVisibility(View.INVISIBLE);
		}
		
		mPullListView.setOnRefreshListener(this);
		mListView.setOnScrollListener(this);
        //当service 更新有新的农情动态时，显示加载
//      mPullListView.doPullRefreshing(true, 500);
		mPdDialog = CustomProgressDialog.createDialog(MyAgricultureActivity.this);
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
	 
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.my_advisory));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.activity_myagriculture);
		
		mCropDao = new CropDao();
//	    mCityDao = new CityDao(this);
		mCropsCache = new CropsCache();
//		mCropTypeDao = new CropTypeDao();
		mPreferencesService = new PreferencesService(MyAgricultureActivity.this);
		LocalHelper.getInstance(MyAgricultureActivity.this).init();

		mUser = UserDao.getInstance().get();
		initView();
		
//		if(mUser==null) {
//			mUserID = -1;
//		} else {
//			mUserID = mUser.id;
//		}
	}

//	@Override
//	protected void onPause() {
//		// mBinder.setmCrops_Remind_num(0);
//		super.onPause();
//
//	}

//	protected void onResume() {
//		super.onResume();
//		//refresh();
//	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	
	/**
	 * 是否显示回复框
	 * @param flag
	 * @param id
	 * @param commentid
	 * @param commentUserName
	 */
//	public void setCommentLayoutVisible(boolean flag, int id, int commentid, String commentUserName) {
//		mAgrInfoId = id;
//		mParentCommentId = commentid;
//		this.mCommtent.setVisibility(flag ? View.VISIBLE : View.GONE);
//		if (flag) {// 输入框弹出时，自动弹出输入键盘
//			met_comment.requestFocus();
//			InputMethodManager imm = (InputMethodManager) met_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
//		}
//		if (flag && (mParentCommentId != -1)) {// 在显示并且回复某条回复的时候，提示回复某人
//			met_comment.setHint(Html.fromHtml("<font color=#dcdcdc>" + "回复" + commentUserName + ":" + "</font>"));
//		} else {
//			met_comment.setText("");
//			met_comment.setHint("");
//		}
//	}

//	public void onBackPressed() {
//		if (this.mCommtent.getVisibility() == View.VISIBLE) {
//			setCommentLayoutVisible(false, mAgrInfoId, -1, null);
//		} else {
//			super.onBackPressed();
//		}
//	}

//	protected void onDestroy() {
//		LocalHelper.getInstance(this).close();
//		mCropsCache.saveCrops(mAgrInfos, mUserID);
//		super.onDestroy();
//	}

//	public void refresh() {
//		if (!CommonUtil.checkNetState(this)) {
//			CommonUtil.showToask(this, "请检查网络连接");
//			mPullListView.onPullDownRefreshComplete();
//			return;
//		}
//		new LoadNewDataTask().execute();
//	}
	
//	public void loadMore() {
//		if (!CommonUtil.checkNetState(this)) {
//			CommonUtil.showToask(this, "请检查网络连接");
////			mPullListView.onPullUpRefreshComplete();
//			mMoreView.setVisibility(View.INVISIBLE);
//			return;
//		}
//		LoadMoreDataInfo(mCurrentSelectedCropType);
//		//new LoadMoreDataTask().execute();
//	}
	
//	public void loadMoreByCity() {
//		if (!CommonUtil.checkNetState(this)) {
//			CommonUtil.showToask(this, "请检查网络连接");
////			mPullListView.onPullUpRefreshComplete();
//			mMoreView.setVisibility(View.INVISIBLE);
//			return;
//		}
//		new LoadMoreDataTaskByCity(area).execute();
//		//new LoadMoreDataTask().execute();
//	}
	
	
//	public void refreshData(){
//		mAdapter.notifyDataSetChanged();
//	}

//	private class LoadAgrInfoTaskByuserId extends AsyncTask<String, Integer, List<AgrInfo>> {
//		private int userID;
//		protected void onPreExecute() {
//		}
//
//		private LoadAgrInfoTaskByuserId(int userid) {
//			 this.userID = userid;
//		}
//		@Override
//		protected List<AgrInfo> doInBackground(String... params) {
//			ElapseTime cropElapseTime = new ElapseTime("农情动态");
//			cropElapseTime.start();
//			List<AgrInfo> result = new ArrayList<AgrInfo>();
//			mIndexPage = 1;
//			if (UserDao.getInstance().get() == null) {  //未获取到用户ID则显示暂无数据
//				
//			} else {
//				mUser = UserDao.getInstance().get();
//				result = mCropDao.getAgrInfosByuserId(mUser.id);
//			}
//			cropElapseTime.end();
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(List<AgrInfo> result) {
//			if(UserDao.getInstance().get() != null) {
//				mBinder.setmCrops_Remind_num(0);
//				mPullListView.onPullDownRefreshComplete();
//				//new LoadMaxNumTask().execute();
//				setLastUpdateTime();
//				
//				if ((result!=null)&&(result.size()>0)) {
//					saveCropsLastRefreshTime(result.get(0).uploadTime);
//					mAgrInfos = new LinkedList<AgrInfo>();
//					mAgrInfos.addAll(result);
//					mAdapter = new CropSituationAdapter(MyAgricultureActivity.this, mAgrInfos);
//					mListView.setAdapter(mAdapter);
//					mAdapter.setListView(mListView);
////					mAgrInfos.clear();
////					mAdapter.setData(mAgrInfos);
//				}
//			}
//		}
//
//	}
	
	
//	private List<CropType> mCropTypeList; 
//	private CropTypeDao mCropTypeDao;
//	private List<CategoryItem> cropTypeData ;
	/**
	 * 获取种养类型
	 * @author trq
	 *
	 */
//	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean> {
//		private String areaCode;
//		public LoadAgrTypeInfoTask(String areaCode)
//		{
//			this.areaCode=areaCode;
//		}
//		private boolean mLoadPass = false;
//		@Override
//		protected Boolean doInBackground(Integer... params) {
//			//mCropTypeList = mCropTypeDao.getCropTypes(areaCode.substring(0,6));
//			if(areaCode.length()<6){
//				return null;
//			}
//			mCropTypeList = mCropTypeDao.getCropTypes(areaCode.substring(0,6));
//			if (mCropTypeList != null) {
//				mLoadPass = true;
//			}
//			return null;
//		}
//		@Override
//		protected void onPostExecute(Boolean result) 
//		{
//			if (mCropTypeList != null && mCropTypeList.size() > 0) 		
//			{
//				cropTypeData = new ArrayList<CategoryItem>();
//				for (int i = 0; i < mCropTypeList.size(); i++)
//				{
//					CategoryItem categoryItem=new CategoryItem();
//					categoryItem.CategoryName=mCropTypeList.get(i).name;
//					categoryItem.CategoryID=mCropTypeList.get(i).id;
//					cropTypeData.add(categoryItem);
//				}			
//			}
//		}
//	}
	
	
//	private void saveCropsLastRefreshTime(String lastRefreshTime) {
//		mPreferencesService.saveCropLastRefreshTime(mUserID, lastRefreshTime);
//	}
	
	Intent intent;
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			intent = new Intent(MyAgricultureActivity.this, MyActivity.class);
        	startActivity(intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	/**
	 * listview 滑动监听器实现
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
////		drawRemindNum(R.id.rad_situation, Constant.NO_REMIND_FLAG);
//		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount() && mAdapter.getCount()<MaxDateNum) {
//			mMoreView.setVisibility(View.VISIBLE);
//			if(jugg == 1){
//				loadMoreByCity();
//			} else {
//			 loadMore();
//			}
//		}
//		if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)&&(lastVisibleIndex>=MaxDateNum)) {
//			CommonUtil.showToask(MyadvisoryActivity.this, "没有更多数据");
//		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
//		if (totalItemCount >= MaxDateNum) {
//			mMoreView.setVisibility(View.INVISIBLE);
//		}
	}

	/**
	 * pulllistview 滑动监听器
	 * @param refreshView
	 */
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//		// TODO Auto-generated method stub
////		refresh();
//		LoadNewDataInfo(mCurrentSelectedCropType);
	}

	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//		// TODO Auto-generated method stub
//		//loadMore();
//		LoadMoreDataInfo(mCurrentSelectedCropType);
	}
}
