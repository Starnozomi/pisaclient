package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryDeatailQuestionCommentAdapter;
import com.supermap.pisaclient.adapter.AdvisoryQuestionAdapter;
import com.supermap.pisaclient.adapter.AdvisoryQuestionCommentAdapter;
import com.supermap.pisaclient.adapter.AdvisoryQuestionPictureAdapter;
import com.supermap.pisaclient.adapter.MessageAdapter;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CropInGridview;
import com.supermap.pisaclient.common.views.CropInListview;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.User;

public class AdvDetailActivity extends BaseActivity {
	
	private View mContent ;
	private ViewHolder viewHolder;
	private int mMsgid = 0;
	private int mAdvinfoId = 0;
	private CityDao mCityDao;
	private ClientMsg mClientMsg;
	private AdvQueryDao mAdvQueryDao;
	private AdvisoryInfo mAdvisoryInfo;
	private List<AdvImgs> mAdvImgs;
	private List<AdvinfoComment> mAdvinfoComment;
	private List<AdvPraise> praisesList;
	private ImageLoader mImageLoader;
	private AdvisoryDeatailQuestionCommentAdapter mCommentAdapter;
	private AdvisoryQuestionPictureAdapter mImageAdapter;
	private PopupWindow mPopupWindow;
	private View mCommtent;
	private EditText met_comment;
	private int mParentCommentId = 0;
	private Button mSend;
	private String mComment;
	private AdvUploadDao mAdvUploadDao;
	private CustomProgressDialog mPdDialog;
	private MessageUploadDao messageUploadDao;
	public int mType = 3;
	
	private void initView(){
		
		mContent= inflater(R.layout.advdetail_main);
		List<AdvisoryInfo> temp = null;
		viewHolder =new ViewHolder();
		viewHolder.iv_user_head = (ImageView)mContent.findViewById(R.id.iv_user_head_icon);
		viewHolder.tv_user = (TextView) mContent.findViewById(R.id.tv_crop_user_name);
		viewHolder.tv_user_comment =(TextView) mContent.findViewById(R.id.tv_crop_user_comment);
		viewHolder.tv_time = (TextView) mContent.findViewById(R.id.tv_crop_time);
		viewHolder.tv_city = (TextView) mContent.findViewById(R.id.tv_wrop_city);
		
		viewHolder.ib_comment = (ImageButton) mContent.findViewById(R.id.ib_wrop_comment);
		viewHolder.lv_comments = (CropInListview) mContent.findViewById(R.id.lv_crop_comments);
		viewHolder.gv_crop_images = (CropInGridview) mContent.findViewById(R.id.gv_crop_images);
		
		viewHolder.tv_lile_nums = (TextView) mContent.findViewById(R.id.tv_heart_nums);
		viewHolder.tv_comment_nums = (TextView) mContent.findViewById(R.id.tv_comment_nums);
		
		mCommtent = mContent.findViewById(R.id.ll_comment);
		met_comment = (EditText) mContent.findViewById(R.id.et_comment);
		mSend = (Button) mContent.findViewById(R.id.bt_send_comment);
		mSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mComment = met_comment.getText().toString();
				if (UserDao.getInstance().get()==null) {
					CommonDialog.setLoginDialog(AdvDetailActivity.this);
				}
				else {
				      dismisPopWindow();
					new uploadComment().execute();
					//防止重复提交
					setCommentLayoutVisible(false,mAdvinfoId , mParentCommentId, null);
				}
			}
		});
		
		mPdDialog = CustomProgressDialog.createDialog(AdvDetailActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
	}
	
	private void initViewData(AdvisoryInfo advInfo){
		
//		//控件添加数据
//		String url = CommonImageUtil.getImageUrl(advInfo.userHeaderFile);
//		mImageLoader.DisplayImage(url, viewHolder.iv_user_head, false);
//		
		
		//控件添加数据
		viewHolder.iv_user_head.setImageBitmap(CommonUtil.toRoundBitmap(CommonImageUtil.drawableToBitmap(this.getResources().getDrawable(R.drawable.crop_user))));
		if ((advInfo.userHeaderFile==null)||("null".equals(advInfo.userHeaderFile))||("".equals(advInfo.userHeaderFile))) {
		}
		else {
			String url = CommonImageUtil.getThumbnailImageUrl(advInfo.userHeaderFile);
			mImageLoader.DisplayToRoundBitmap(url, viewHolder.iv_user_head, false);
		}
		viewHolder.iv_user_head.setOnClickListener(new UserHeadOnClickListenner(advInfo.userId));
		viewHolder.tv_user.setText(advInfo.userName);
		viewHolder.tv_user_comment.setText(advInfo.qestion);
		viewHolder.tv_time .setText(DateUtiles.getCropTime(advInfo.uploadTime)+" ");
//		viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode).substring(0, 2));
		if(advInfo.areacode.length()>6){
			viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode.substring(0,6))+mCityDao.queryCityName(advInfo.areacode));
		}
		else {
			viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode));
		}
		
		
		if (praisesList!=null) {
			viewHolder.tv_lile_nums.setText(praisesList.size()+"");
		}
		else {
			viewHolder.tv_lile_nums.setText("0");
		}
		if(mAdvinfoComment!=null){
			viewHolder.tv_comment_nums.setText(mAdvinfoComment.size()+"");
		}
		else {
			viewHolder.tv_comment_nums.setText("0");
		}
		viewHolder.ib_comment.setOnClickListener(new CommentButtonListenner(0));
		viewHolder.gv_crop_images.setOnItemClickListener(new CropImagesItemOnclickListener(0));
		if ((mAdvinfoComment!=null)&&(mAdvinfoComment.size()>0)) {
			viewHolder.lv_comments.setOnItemClickListener(new CropCommentsItemOnclickListener(mAdvinfoComment,0));
		}
		//农情评论内容适配器
		mCommentAdapter = new AdvisoryDeatailQuestionCommentAdapter(AdvDetailActivity.this, mAdvinfoComment);
		viewHolder.lv_comments.setAdapter(mCommentAdapter);
		mCommentAdapter.notifyDataSetChanged();
		mCommentAdapter.setListView(viewHolder.lv_comments);
		
		mImageAdapter = new AdvisoryQuestionPictureAdapter(AdvDetailActivity.this,mAdvImgs);
		viewHolder.gv_crop_images.setAdapter(mImageAdapter);
		mImageAdapter.notifyDataSetChanged();
		
	}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			ExitApplication.getInstance().addActivity(this);
			setTvTitle(Utils.getString(this, R.string.adv_detail));
			setIsBack(true);
			
			setIsNavigator(false);
			setBackOnClickListener(this);
			
			initView();
			mCityDao = new CityDao(this);
			mAdvQueryDao = new AdvQueryDao();
			mAdvUploadDao = new AdvUploadDao();
			mImageLoader = new ImageLoader(this);
			messageUploadDao = new MessageUploadDao();
			mMsgid = getIntent().getIntExtra("msgid", 0);
			if (mMsgid!=0) {
				System.out.println("msgid:"+mMsgid);
//				mCityDao.updateMsgCheckedByID(mMsgid);
				mClientMsg =mCityDao.getMsgs(mMsgid);
				//更新本地数据库 消息已查看
				mCityDao.updateMsgChecked(mClientMsg);
			}
			mAdvinfoId = getIntent().getIntExtra("advinfoid", 0);
			if (mClientMsg!=null) {
				new LoadDataTask().execute();
			}
			
		}
		
		private class UserHeadOnClickListenner implements OnClickListener{
			 
			 private  int userID;
			 public  UserHeadOnClickListenner(int userid){
				 this.userID = userid ;
			 }
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(AdvDetailActivity.this, UserDetailActivity.class);
				intent.putExtra("id", userID);
				AdvDetailActivity.this.startActivity(intent);
			}
			 
		 }
		
		/**
		 * 点击评论item 弹出回复框，并提示回复谁
		 * @author Administrator
		 *
		 */
		private class CropCommentsItemOnclickListener implements OnItemClickListener{
			private List<AdvinfoComment> list ;
			private int mCommentId = -1;
			private String mCommentUser = null;
			private int mAdvInfoId;
			private int position;
			public CropCommentsItemOnclickListener(List<AdvinfoComment> list,int postion){
				this.list = list;
				mAdvInfoId = mAdvinfoId;
				this.position = postion;
			}
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (UserDao.getInstance().get()==null) {
					CommonDialog.setLoginDialog(AdvDetailActivity.this);
				}
				else {
					AdvinfoComment addvinfoComment = list.get(this.position);
					mCommentId = addvinfoComment.commentId;
					if(addvinfoComment.isExpert){
						mCommentUser = addvinfoComment.expertName;
					}
					else {
						mCommentUser = addvinfoComment.username;
					}
					AdvDetailActivity.this.setCommentLayoutVisible(true,mAdvInfoId,mCommentId,mCommentUser);
				}
				
			}
			
		}
		/**
		 * 点击农情图片，显示大图
		 * @author Administrator
		 *
		 */
		private class CropImagesItemOnclickListener implements OnItemClickListener{
			private int mOut_positon;
			
			public CropImagesItemOnclickListener(int position){
				mOut_positon = position;
			}

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				List<AdvImgs> mList  =mAdvImgs;
				String imagepath[] =null;
				if ((mList!=null)&&(mList.size()>0)) {
					imagepath= new String[mList.size()];
					for(int i = 0;i<mList.size();i++){
						imagepath[i] = mList.get(i).URLcontent;
					}
				}
				Intent intent = new Intent();
				intent.putExtra("image_position", position);
				intent.putExtra("image_deses", imagepath);
				intent.setClass(AdvDetailActivity.this, CropImageActivity.class);
				AdvDetailActivity.this.startActivity(intent);
			}
			
		}
		/**
		 * 显示评论和赞的popwindow
		 * @param view
		 */
		 private void showPopupWindow(View view) {
		        // 一个自定义的布局，作为显示的内容
		        View contentView = LayoutInflater.from(AdvDetailActivity.this).inflate(
		                R.layout.comment_pop_window, null);
		        
		        //设置弹出窗口跟靠view 左边，底部与view对齐
		        int w,h;
		    	w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				contentView.measure(w, h);
		        int[] location = new int[2];  
		        
		        view.getLocationOnScreen(location); 
		        
		        int w2,h2;
		        w2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				h2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
				view.measure(w2, h2);
				
		        // 点击赞
		        Button like = (Button) contentView.findViewById(R.id.bt_like);
		        Button show = (Button) contentView.findViewById(R.id.bt_commet);
		        if ((UserDao.getInstance().get()!=null)&&(UserDao.getInstance().get().isExpert)) {
		        	 show.setText("我要回答");
				}
		        like.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(UserDao.getInstance().get()==null){
		            		CommonDialog.setLoginDialog(AdvDetailActivity.this);
		            	}
						else {
							 dismisPopWindow();
							 AdvUploadDao uploadDao = new AdvUploadDao();
							 if (isPraised()) {
								CommonUtil.showToask(AdvDetailActivity.this, "你已赞过");
								return;
							 }
//							 
							 int id =  uploadDao.addAdvPrasis(mAdvinfoId, UserDao.getInstance().get().id,1);
							if (id!=0) {
								CommonUtil.showToask(AdvDetailActivity.this, "赞成功");
								refreshPraise();
							}
							else {
								CommonUtil.showToask(AdvDetailActivity.this, "赞失败");
							}
						}
					}
				});
		        //点击评论
		        Button button = (Button) contentView.findViewById(R.id.bt_commet);
		        button.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		            	if(UserDao.getInstance().get()==null){
		            		CommonDialog.setLoginDialog(AdvDetailActivity.this);
		            	}
		            	else {
		            		 dismisPopWindow();
		            		 AdvDetailActivity.this.setCommentLayoutVisible(true,mAdvinfoId,-1,null);
						}
		            }
		        });
		        DisplayMetrics dm = AdvDetailActivity.this.getResources().getDisplayMetrics();
		        int commentWith = dm.widthPixels/2;
		        mPopupWindow= new PopupWindow(contentView,
		        		commentWith, LayoutParams.WRAP_CONTENT, true);
		        mPopupWindow.setTouchable(true);
		        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
		            @Override
		            public boolean onTouch(View v, MotionEvent event) {
		                return false;
		            }
		        });
		        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
				int x = location[0]-commentWith;
				int y = location[1]-(contentView.getMeasuredHeight()-view.getMeasuredHeight());
				mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x-10,y);
		    }
		 
		 /**
		  * 检查是否赞过
		  * @param mOutPosition
		  * @return
		  */
		 private boolean isPraised(){
			 List<AdvPraise> praisesList = mAdvisoryInfo.mPraises;
			 if ((praisesList==null)||(praisesList.size()==0)) {
				return false;
			 }
			 for(AdvPraise advPraise :praisesList){
				 if (advPraise.userId==UserDao.getInstance().get().id) {
					return true;
				 }
			 }
			 return false;
		 }
		 
		 private void refreshPraise(){
			 List<AdvPraise> advPraises = mAdvisoryInfo.mPraises;
				AdvPraise advPraise = new AdvPraise();
				advPraise.userId = UserDao.getInstance().get().id;
				if (advPraises==null) {
					advPraises = new ArrayList<AdvPraise>();
					advPraises.add(advPraise);
				}
				else {
					advPraises.add(advPraise);
				}
				
				if (praisesList!=null) {
					viewHolder.tv_lile_nums.setText(praisesList.size()+"");
				}
				else {
					viewHolder.tv_lile_nums.setText("0");
				}
		 }
		 
		 public void dismisPopWindow(){
			 if ((mPopupWindow!=null)&&(mPopupWindow.isShowing())) {
				mPopupWindow.dismiss();
			}
		 }
		 
		 /**
		  * 点击评论，弹出评论和赞popwindow
		  * @author Administrator
		  *
		  */
		 private class CommentButtonListenner implements OnClickListener{
			 private int postion;
			 public CommentButtonListenner(int postion){
				 this.postion = postion;
				
			 }
			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		 }
		
		private class LoadDataTask extends AsyncTask<String, Integer,AdvisoryInfo>{
			
			private float mScrore  = -1;
			
			@Override
			protected void onPreExecute() {
				if (!mPdDialog.isShowing()){
					mPdDialog.show();
				}
			}
			
			@Override
			protected AdvisoryInfo doInBackground(String... params) {
				
				if (mClientMsg.msgtypeid==4) {//打分消息
					mAdvisoryInfo= mAdvQueryDao.getAdvisoryInfoByScoreID(mClientMsg.mainid);
				}else {
					List<AdvinfoComment> list = mAdvQueryDao.getAdvComment(mClientMsg.mainid);
					if ((list!=null)&&(list.size()>0)) {
						mAdvinfoId = list.get(0).advInfoId;
					}
					if(mAdvinfoId!=0){
						mAdvisoryInfo = mAdvQueryDao.getAdvisoryInfo(mAdvinfoId);
					}
				}
				
				if (mClientMsg.msgid!=0) {
					int result  = messageUploadDao.updateCheckMsg(mClientMsg.toid, mClientMsg.msgid);
				}
				
				return  mAdvisoryInfo;
			}
			
			@Override
			protected void onPostExecute(AdvisoryInfo result) {
				if (result!=null) {
					mAdvImgs =mAdvisoryInfo.mImgs;
					mAdvinfoComment = mAdvisoryInfo.mComments;
					praisesList = mAdvisoryInfo.mPraises;
					initViewData(result);
//					if ((mClientMsg.msgtypeid==4)&&(mScrore!=-1)) {
//						CommonUtil.showToask(AdvDetailActivity.this, "用户给你打了"+mScrore+"分");
//					}
				}
				if (mPdDialog.isShowing()){
					mPdDialog.dismiss();
				}
				
			}
		}
		
		public void setCommentLayoutVisible(boolean flag,int id,int commentid,String commentUserName){
			mAdvinfoId = id;
			mParentCommentId = commentid;
			this.mCommtent.setVisibility(flag ? View.VISIBLE : View.GONE);
			if (flag) {//输入框弹出时，自动弹出输入键盘
				met_comment.requestFocus();
				InputMethodManager imm = (InputMethodManager) met_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);  
			}
			
				
			if (flag&&(mParentCommentId!=-1)) {//在显示并且回复某条回复的时候，提示回复某人
				met_comment.setHint(Html.fromHtml("<font color=#dcdcdc>"+"回复"+commentUserName+":"+"</font>"));
			}
			else {
				met_comment.setText("");
				met_comment.setHint("");
			}
		}
		
		private class uploadComment extends AsyncTask<String, integer,Boolean>{
			private List<AdvinfoComment> advinfoComments;
			@Override
			protected Boolean doInBackground(String... params) {//这个参数//返回值
				int commentid = 0;
				if (UserDao.getInstance().get().isExpert) {
					commentid= mAdvUploadDao.addAdvComment(mComment, mAdvinfoId, -1, mParentCommentId, UserDao.getInstance().getExpertIdByUserId(UserDao.getInstance().get().id));
				}
				else {
					commentid = mAdvUploadDao.addAdvComment(mComment, mAdvinfoId,  UserDao.getInstance().get().id, mParentCommentId,-1);
				}
				if (commentid == 0) {
					return false;
				} else {// 用于刷新评论
					advinfoComments = mAdvQueryDao.getAdvComments(mAdvinfoId);
				}
				
				
				if (UserDao.getInstance().get().isExpert) {
					//专家回复用户
					
//					int msgid = messageUploadDao.addMsg(UserDao.getInstance().get().id, mAdvisoryInfo.userId, 2, commentid, 0, 0, "专家回复了我的咨询");
					ClientMsg answerMsg = new ClientMsg();
					answerMsg.fromid = UserDao.getInstance().get().id;
					answerMsg.toid = mAdvisoryInfo.userId;
					answerMsg.msgtypeid = 2;
					answerMsg.mainid = commentid;
					answerMsg.msgsendtypeid =1;
					answerMsg.content = "专家回复了我的咨询";
					int msgid = messageUploadDao.addMsg(answerMsg);
				}
				else {
					//用户回复专家
					int experid = 0;
					for(AdvinfoComment advinfoComment :mAdvinfoComment){
						if (advinfoComment.commentId ==mParentCommentId) {
							experid = advinfoComment.expertid;
						}
					}
					int msgid = 0;
					if (experid!=0) {//继续追问问题？
						ClientMsg answerMsg = new ClientMsg();
						answerMsg.fromid = UserDao.getInstance().get().id;
						answerMsg.toid = UserDao.getInstance().getUserIdByExpertId(experid);
						answerMsg.msgtypeid = 3;
						answerMsg.mainid = commentid;
						answerMsg.msgsendtypeid =1;
						answerMsg.content = "用户回复了我的专家建议";
						msgid = messageUploadDao.addMsg(answerMsg);
					}
				}
				return true;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					setCommentLayoutVisible(false,mAdvinfoId , mParentCommentId, null);
					CommonUtil.showToask(AdvDetailActivity.this, "评论上传成功");
					refreshComments();
				}
				super.onPostExecute(result);
			}
			
			private void refreshComments() {
				mAdvisoryInfo.mComments = advinfoComments;
				mCommentAdapter.notifyDataSetChanged();
			}
		}
		
		public class ViewHolder {
			public ImageView iv_user_head;
			public TextView tv_user;
			public TextView tv_user_comment;
			public CropInGridview gv_crop_images;
			public TextView tv_time;
			public TextView tv_city;
			public ImageButton ib_comment;
			public CropInListview  lv_comments;
			public TextView tv_lile_nums;
			public TextView tv_comment_nums;
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
}
