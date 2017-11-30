package com.supermap.pisaclient.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.R.bool;
import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryQuestionAdapter;
import com.supermap.pisaclient.biz.AdvMaxNumDao;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.AdvCache;
import com.supermap.pisaclient.common.Common;
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
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase;
import com.supermap.pisaclient.pullrefresh.PullToRefreshBase.OnRefreshListener;
import com.supermap.pisaclient.pullrefresh.PullToRefreshListView;

public class AdvisoryQuestionActivity extends BaseActivity  implements OnScrollListener,OnRefreshListener<ListView> {
	
	SharedPreferences sp;
	Editor editor;
	private View mContent;
	private View mCommtent;
	private ListView mListView;
	private AdvisoryQuestionAdapter mAdapter;
	private View mMoreView;
	private ProgressBar mpg;
	private Button mbtn_add_more;
	private Button mSend;
	private EditText met_comment;
	
	private CityDao cityDao;
	private String areaCode="50%";
	
	private CategoryDialog mDialog;
	
	private List<CategoryItem> cityData ;

	private List<AdvisoryInfo> mAllAdvInfos = new ArrayList<AdvisoryInfo>();
	
	private int lastVisibleIndex = 0;
	private CustomProgressDialog mPdDialog;
	private int mAdvInfoId;
	private int mParentCommentId = -1;
	private String mComment;
	private AdvQueryDao mAdvQueryDao;
	private AdvUploadDao mAdvUploadDao;
	private int MaxDateNum = 100;
	private int mPageIndex = 0;
	private int mMaxPageSize = 5;
	private int mMaxPageNum = 1;
	public int mType = -1;
	private Subject mSubject;
	int mScore = 0;
	private MessageUploadDao messageUploadDao;
	private int mAdvinfoPositon = 0;
	private String mSerchContent;
	private PullToRefreshListView mPullListView;
	private AdvCache mAdvCache;
	private AdvMaxNumDao mNumDao;
	private int mUserID;
	private boolean mIsExpert;
	private User mUser;
	private LinearLayout search;
	private int mMaxAdvCacheNum = mMaxPageSize;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	
	
	private void initListview(){
		mListView.setDivider(new ColorDrawable(Color.parseColor("#dcdcdc"))); 
		mListView.setDividerHeight(CommonUtil.dip2px(AdvisoryQuestionActivity.this, 1));
		mListView.setCacheColorHint(Color.parseColor("#00000000"));
//		mListView.setFooterDividersEnabled(false);
	}
	
	/**
	 *  获取当天时间 
	 *  test格式 x月xx日 xx:xx 
	 */
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
	
	private void initView() {
		//设置baseview
//		setTvTitle(Utils.getString(this, R.string.advisory_question_list));
		setIsBack(true);
		setIsNavigator(false);
		setBackOnClickListener(this);
		//设置布局内容
		mContent = inflater(R.layout.cropsituation_main);
		
		Intent intent = getIntent();
		String aaaa = intent.getStringExtra("jugg");
		search = (LinearLayout) findViewById(R.id.search);
		search.setVisibility(View.VISIBLE);
		if( "0".equals(intent.getStringExtra("jugg")) ) {
			search.setVisibility(View.GONE);
		}
		
//		mListView = (ListView) mContent.findViewById(R.id.lv_crop_situation);
		mPullListView = (PullToRefreshListView) mContent .findViewById(R.id.refreshable_view);
		mListView = mPullListView.getRefreshableView();
		initListview();
		
		if (mType==Constant.ADV_TYPE_MY) {
			TextView askTextView = (TextView)findViewById(R.id.tv_my);
			askTextView.setText("提问");
			setIsMy(true);
			setMyOnClickListener(this);
		}
		sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		
		mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(false);
        //评论输入框视图
		mCommtent = mContent.findViewById(R.id.ll_comment);
		met_comment = (EditText) mContent.findViewById(R.id.et_comment);
		mSend = (Button) mContent.findViewById(R.id.bt_send_comment);
		mSend.setOnClickListener(this);
		//加载更多视图
		mMoreView = getLayoutInflater().inflate(R.layout.add_more_data, null);
		mbtn_add_more = (Button) mMoreView.findViewById(R.id.bt_load);
		mpg = (ProgressBar) mMoreView.findViewById(R.id.pg_add_more);
		mbtn_add_more.setOnClickListener(this);
		mListView.addFooterView(mMoreView);
		
		mAllAdvInfos = mAdvCache.getAdvisoryInfos(mType, mUserID, mSerchContent);
		mAdapter = new AdvisoryQuestionAdapter(this, mAllAdvInfos);
		
		if ((mAllAdvInfos==null)||(mAllAdvInfos.size()<mMaxPageSize)) {
			mMoreView.setVisibility(View.INVISIBLE);
		}
		mListView.setAdapter(mAdapter);
		mAdapter.setListView(mListView);
		mListView.setOnScrollListener(this);
		mPullListView.setOnRefreshListener(this);
		
		//正在加载视图
		mPdDialog = CustomProgressDialog.createDialog(AdvisoryQuestionActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		
		areaCode = sp.getString("CITYNAME", "") + "%";
		if("".equals(areaCode) || areaCode == null){
			
		} else {
		   new LoadDataTask(areaCode).execute(false);
		}
	}

	private void initData(){
		mAdvQueryDao = new AdvQueryDao();
		mAdvUploadDao = new AdvUploadDao();
		messageUploadDao = new MessageUploadDao();
		mAdvCache = new AdvCache();
		mNumDao = new AdvMaxNumDao();
		cityDao=new CityDao(this);
		
		mUser = UserDao.getInstance().get();
		if(mUser==null){
			mUserID = -1;
			mIsExpert = false;
		}
		else {
			mUserID = mUser.id;
			mIsExpert = mUser.isExpert;
		}
		
		mType = getIntent().getIntExtra("type", -1);
		if (mType == 0) {
			mSubject = (Subject) getIntent().getSerializableExtra("subject");
			setTvTitle(mSubject.name + "咨询");
		} else if (mType == 1) {
			setTvTitle("热门咨询");
			setIsMenu(true);
			setMenuOnClickListener(this);
		} else if (mType == 3) {
			setTvTitle("我的咨询");
		} else if (mType == 2) {
			setTvTitle("最新咨询");
			setIsMenu(true);
			setMenuOnClickListener(this);
		} else if (mType == 4) {
			mSerchContent = getIntent().getStringExtra("content");
			setTvTitle("搜索关于" + mSerchContent + "");
		}
	}
	
	public void testdata(){
		User user = new User();
		user.id=71;
		user.isExpert = true;
		UserDao.getInstance().save(user, true);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		testdata();
		initData();
		initView();
		cityData = cityDao.getCounty();
	}
	
	@Override
	protected void onResume() {
		refresh();
		super.onResume();
	}
	
	private void loadComment(){
		mComment = met_comment.getText().toString();
		if (UserDao.getInstance().get() == null) {
			CommonDialog.setLoginDialog(AdvisoryQuestionActivity.this);
			return;
		} 
		if(mComment == null || "".equals(mComment)){
			Toast.makeText(AdvisoryQuestionActivity.this, "评论信息不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mAdapter.dismisPopWindow();
		if ((mType == 3)) {
			new uploadComment().execute();
		} else {
			if (UserDao.getInstance().get().isExpert) {
				new uploadComment().execute();
			} else {
				CommonUtil.showToask(AdvisoryQuestionActivity.this, "不是专家不能回复不属于自己的咨询信息");
			}
		}
		//防止重复发送
		setCommentLayoutVisible(false, mAdvInfoId, mParentCommentId, null, mAdvinfoPositon);
	}
	

	private void loadMoreDate() {
		mPageIndex++;
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, "请检查网络连接");
			mMoreView.setVisibility(View.INVISIBLE);
			return;
		}
		new LoadDataTask(areaCode).execute(true);
	}
	
	private void clickLoadMore(){
		mpg.setVisibility(View.VISIBLE);// 将进度条可见
		mbtn_add_more.setVisibility(View.GONE);// 按钮不可见
		loadMoreDate();// 加载更多数据
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_my://进入咨询提问界面
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				Intent intent = new Intent();
				intent.setClass(this, AdvisoryAskActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ibtn_back://返回
			finish();
			break;
		case R.id.bt_send_comment://上传评论
			loadComment();
			break;
		case R.id.bt_load://点击加载更多
			clickLoadMore();
			break;
		case R.id.ibtn_menu://右上角选择区县
			if(cityData!=null) {
				mDialog = new CategoryDialog(AdvisoryQuestionActivity.this, getScreen()[0], getMenuHeight());
				mDialog.setData(cityData);
				mDialog.setOnItemClickListener(new OnItemClickListener() 
				{
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						areaCode = cityData.get(arg2).Code+"%";
						String aaaaa = areaCode;
						new	LoadDataTask(areaCode).execute(false);
						mDialog.destroy();
					}
				});	
				mDialog.show();
			}
			else {
				Toast.makeText(AdvisoryQuestionActivity.this, "未加载到数据", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * 是否显示回复框
	 * @param flag
	 * @param id
	 * @param commentid
	 *        被评论的评论id
	 * @param commentUserName
	 *        被评论的人的名字
	 * @param position
	 *        被评论的农情在农情列表中的位置
	 */
	public void setCommentLayoutVisible(boolean flag, int id, int commentid, String commentUserName, int position) {
		mAdvInfoId = id;
		mAdvinfoPositon = position;
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
			setCommentLayoutVisible(false, mAdvInfoId, -1, null, 0);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(this).close();
		saveAdvCache();
		super.onDestroy();
	}
	
	private void saveAdvCache(){
		if ((mAllAdvInfos==null)&&(mAllAdvInfos.size()==0)) {
			return;
		}
		if (mAllAdvInfos.size()>mMaxAdvCacheNum) {
			mAdvCache.saveAdvs(mAllAdvInfos.subList(0, mMaxAdvCacheNum-1), mType, mUserID,mSerchContent);
		}
		else {
			mAdvCache.saveAdvs(mAllAdvInfos, mType, mUserID,mSerchContent);
		}
	}

	
	/**
	 *  加载数据 
	 */
	private class LoadDataTask extends AsyncTask<Boolean, Integer, List<AdvisoryInfo>> {
		
		private boolean isLoadMore;
		private String areaCode;
		public LoadDataTask(String areaCode) {
			this.areaCode = areaCode;
		}
		@Override
		protected void onPreExecute() {
			if ((!mPdDialog.isShowing())&&!isLoadMore)
				mPdDialog.show();
		}

		@Override
		protected List<AdvisoryInfo> doInBackground(Boolean... params) {
			this.isLoadMore = params[0];
			ElapseTime elapseTime = new ElapseTime("所有咨询信息");
			elapseTime.start();
			List<AdvisoryInfo> result = new ArrayList<AdvisoryInfo>();
			//result = mAdvQueryDao.getAdvInfoByPage(mType, mUserID, mSerchContent, mIsExpert, (mPageIndex-1)*mMaxPageSize,mMaxPageSize);
			result = mAdvQueryDao.getAdvInfoByPage(this.areaCode, mType, mUserID, mSerchContent, mIsExpert, (mPageIndex-1)*mMaxPageSize, mMaxPageSize);
			elapseTime.end();
			return result;
		}

		@Override
		protected void onPostExecute(List<AdvisoryInfo> result) {
			setLastUpdateTime();
			if (isLoadMore) {
				mMoreView.setVisibility(View.INVISIBLE);
				if ((result!=null)&&(result.size()>0)) {
					mAllAdvInfos.addAll(result);
					mAdapter.notifyDataSetChanged();
				}
			}else {
				mPullListView.onPullDownRefreshComplete();
				if ((result!=null)&&(result.size()>0)) {
					new LoadMaxDataNumTask().execute();		//加载最大数目
					mAllAdvInfos.clear();
					mAllAdvInfos.addAll(result);
					mAdapter.notifyDataSetChanged();
				}
			}
			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}
	}

	public void refresh() {
		mPageIndex = 1;
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, "请检查网络连接");
			mPullListView.onPullDownRefreshComplete();
			return;
		}
		new LoadDataTask(areaCode).execute(false);
	}

	/**
	 *  加载评论 
	 */
	private class uploadComment extends AsyncTask<String, integer, Boolean> {

		private List<AdvinfoComment> advinfoComments;
		@Override
		protected Boolean doInBackground(String... params) {// 这个参数//返回值
			AdvisoryInfo advInfo = mAllAdvInfos.get(mAdvinfoPositon);
			User user = UserDao.getInstance().get();
			int commentid = 0;
			if (user.isExpert) {
				commentid = mAdvUploadDao.addAdvComment(mComment, mAdvInfoId, -1, mParentCommentId, UserDao.getInstance()
						.getExpertIdByUserId(user.id));
			} else {
				commentid = mAdvUploadDao.addAdvComment(mComment, mAdvInfoId, user.id, mParentCommentId, -1);
			}
			if (commentid == 0) {
				return false;
			} else {// 用于刷新评论
				advinfoComments = mAdvQueryDao.getAdvComments(mAdvInfoId);
			}

			// 消息中心添加消息
			if (UserDao.getInstance().get().isExpert) {
				// 专家回复用户
				ClientMsg answerMsg = new ClientMsg();
				answerMsg.fromid = UserDao.getInstance().get().id;
				answerMsg.toid = advInfo.userId;
				answerMsg.msgtypeid = 2;
				answerMsg.mainid = commentid;
				answerMsg.msgsendtypeid = 1;
				answerMsg.content = "专家回复了我的咨询";
				int msgid = messageUploadDao.addMsg(answerMsg);
			} else {
				// 用户回复专家
				int experid = 0;
				for (AdvinfoComment advinfoComment : advinfoComments) {
					if (advinfoComment.commentId == mParentCommentId) {
						experid = advinfoComment.expertid;
					}
				}
				if (experid != 0) {
					ClientMsg answerMsg = new ClientMsg();
					answerMsg.fromid = UserDao.getInstance().get().id;
					answerMsg.toid = UserDao.getInstance().getUserIdByExpertId(experid);
					answerMsg.msgtypeid = 3;
					answerMsg.mainid = commentid;
					answerMsg.msgsendtypeid = 1;
					answerMsg.content = "用户回复了我的专家建议";
					int msgid = messageUploadDao.addMsg(answerMsg);
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setCommentLayoutVisible(false, mAdvInfoId, mParentCommentId, null, mAdvinfoPositon);
				CommonUtil.showToask(AdvisoryQuestionActivity.this, "评论上传成功");
				refreshComments();
			} else {
				CommonUtil.showToask(AdvisoryQuestionActivity.this, "评论上传失败");
			}
			super.onPostExecute(result);
		}

		private void refreshComments() {
			mAllAdvInfos.get(mAdvinfoPositon).mComments = advinfoComments;
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 *   加载数据最大数目
	 */
	class LoadMaxDataNumTask extends AsyncTask<Integer, Integer, Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			int num = 0;
			num = mNumDao.getMaxNum(areaCode, mType, mUserID, mSerchContent,mIsExpert);
			if ((num!=-1)&&(num!=0)) {
				MaxDateNum = num;
				System.out.println("MaxDateNum=="+MaxDateNum);
				mMaxPageNum = MaxDateNum/mMaxPageSize +1;
			}
			return num;
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		drawRemindNum(R.id.rad_situation, Constant.NO_REMIND_FLAG);
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount() && mAdapter.getCount()<MaxDateNum) {
			mMoreView.setVisibility(View.VISIBLE);
			loadMoreDate();
		}
		if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)&&(lastVisibleIndex>=MaxDateNum)) {
			CommonUtil.showToask(AdvisoryQuestionActivity.this, "没有更多数据");
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		if (totalItemCount >= MaxDateNum) {
			mMoreView.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		refresh();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

}
