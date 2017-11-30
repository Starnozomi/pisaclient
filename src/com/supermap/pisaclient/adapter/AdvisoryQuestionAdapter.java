package com.supermap.pisaclient.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.supermap.pisaclient.R;

import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.CropUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.views.CropInGridview;
import com.supermap.pisaclient.common.views.CropInListview;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.ui.AdvisoryQuestionActivity;
import com.supermap.pisaclient.ui.CropImageActivity;
import com.supermap.pisaclient.ui.UserDetailActivity;

public class AdvisoryQuestionAdapter extends BaseAdapter {
		private AdvisoryQuestionActivity mContext;
		private List<AdvisoryInfo> mDataList;
		private LayoutInflater mInflater;
		private ViewHolder viewHolder = null;
		private int mAdvInfoID;
		private int mAdvinfoPositon;
		private CityDao mCityDao;
		private ImageLoader mImageLoader;
		private PopupWindow mPopupWindow;
		private ListView listView;
		private AdvisoryQuestionPictureAdapter mImageAdapter;
		private AdvisoryQuestionCommentAdapter mCommentAdapter;
		
		public AdvisoryQuestionAdapter(Context context, List<AdvisoryInfo> list){
			this.mContext = (AdvisoryQuestionActivity)context;
			this.mDataList = list;
			mInflater = LayoutInflater.from(context);
			mCityDao = new CityDao(context);
			mImageLoader  = new ImageLoader(context);
		}
		
		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mDataList.get(arg0);
		}

		@Override
		public long getItemId(int postion) {
			return postion;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AdvisoryInfo advInfo = mDataList.get(position);
			List<AdvinfoComment> commentsList = new ArrayList<AdvinfoComment>();
			List<AdvPraise> praisesList = new ArrayList<AdvPraise>();
			List<AdvImgs> imgList = new ArrayList<AdvImgs>();
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.crop_situation_item, null); //咨询布局
				
				viewHolder = new ViewHolder();
				viewHolder.iv_user_head = (ImageView)convertView.findViewById(R.id.iv_user_head_icon);
				viewHolder.tv_user = (TextView) convertView.findViewById(R.id.tv_crop_user_name);
				viewHolder.tv_user_comment =(TextView) convertView.findViewById(R.id.tv_crop_user_comment);
				viewHolder.tv_subjectname = (TextView) convertView.findViewById(R.id.tv_crop_type);
				viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_crop_time);
				viewHolder.tv_city = (TextView) convertView.findViewById(R.id.tv_wrop_city);
				
				viewHolder.ib_comment = (ImageButton) convertView.findViewById(R.id.ib_wrop_comment);
				viewHolder.lv_comments = (CropInListview) convertView.findViewById(R.id.lv_crop_comments);
				viewHolder.gv_crop_images = (CropInGridview) convertView.findViewById(R.id.gv_crop_images);
				
				viewHolder.tv_lile_nums = (TextView) convertView.findViewById(R.id.tv_heart_nums);
				viewHolder.tv_comment_nums = (TextView) convertView.findViewById(R.id.tv_comment_nums);
//				viewHolder.tv_time .setText(DateUtiles.getCropTime(agrInfo.uploadTime)+" ");
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
//				resetViewHolder(viewHolder);
			}
			
			
			viewHolder.iv_user_head.setImageBitmap(CommonUtil.toRoundBitmap(CommonImageUtil.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.crop_user))));
			//控件添加数据
			if ((advInfo.userHeaderFile==null)||("null".equals(advInfo.userHeaderFile))||("".equals(advInfo.userHeaderFile))) {
			}
			else {
				String url = CommonImageUtil.getThumbnailImageUrl(advInfo.userHeaderFile);
				mImageLoader.DisplayToRoundBitmap(url, viewHolder.iv_user_head, false);
			}
//			String url = CommonImageUtil.getImageUrl(advInfo.userHeaderFile);
//			mImageLoader.DisplayToRoundBitmap(url, viewHolder.iv_user_head, false);
			viewHolder.iv_user_head.setOnClickListener(new UserHeadOnClickListenner(position));
			viewHolder.tv_user.setText(advInfo.userName);
			viewHolder.tv_user_comment.setText(advInfo.qestion);
			if ((advInfo.subjectName!=null)&&(!"".equals(advInfo.subjectName))&&(!"null".equals(advInfo.subjectName))) {
				viewHolder.tv_subjectname .setText(advInfo.subjectName);
			}
			
			viewHolder.tv_time.setText(DateUtiles.getCropTime(advInfo.uploadTime)+" ");
//			viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode).substring(0, 2));
			if(advInfo.areacode.length()>6){
				viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode.substring(0,6))+mCityDao.queryCityName(advInfo.areacode));
			}
			else {
				viewHolder.tv_city.setText(mCityDao.queryCityName(advInfo.areacode));
			}
//			commentsList = mContext.mAdvComments.get(position);
//			imgList = mContext.mImgList.get(position);
//			praisesList = mContext.mAdvPraises.get(position);
			
			commentsList = advInfo.mComments;
			imgList = advInfo.mImgs;
			praisesList = advInfo.mPraises;
			
			if (praisesList!=null) {
				viewHolder.tv_lile_nums.setText(praisesList.size()+"");
			}
			else {
				viewHolder.tv_lile_nums.setText("0");
			}
			if(commentsList!=null){
				viewHolder.tv_comment_nums.setText(commentsList.size()+"");
			}
			else {
				viewHolder.tv_comment_nums.setText("0");
			}
			viewHolder.ib_comment.setOnClickListener(new CommentButtonListenner(position));
			viewHolder.gv_crop_images.setOnItemClickListener(new CropImagesItemOnclickListener(position));
//			if ((commentsList!=null)&&(commentsList.size()>0)) {
//				viewHolder.lv_comments.setOnItemClickListener(new CropCommentsItemOnclickListener(commentsList,position));
//			}
			mCommentAdapter = new AdvisoryQuestionCommentAdapter(mContext, commentsList,position);
			viewHolder.lv_comments.setAdapter(mCommentAdapter);
			mCommentAdapter.setOutAdapter(this);
			mCommentAdapter.setListView(viewHolder.lv_comments);
			
			mImageAdapter = new AdvisoryQuestionPictureAdapter(mContext,imgList);
			viewHolder.gv_crop_images.setAdapter(mImageAdapter);
			mImageAdapter.notifyDataSetChanged();
			return convertView;
		}
		
		protected void resetViewHolder(ViewHolder p_ViewHolder)
		{
		p_ViewHolder.tv_user.setText(null);
		p_ViewHolder.tv_user_comment.setText(null);
		p_ViewHolder.gv_crop_images.setAdapter(null);
		p_ViewHolder.tv_time.setText(null);
		p_ViewHolder.tv_city.setText(null);
		p_ViewHolder.ib_comment.setOnClickListener(null);
		p_ViewHolder.lv_comments.setAdapter(null);
		p_ViewHolder.tv_lile_nums.setText(null);
		p_ViewHolder.tv_comment_nums.setText(null);
		}
		
		public class ViewHolder {
			public ImageView iv_user_head;
			public TextView tv_user;
			public TextView tv_user_comment;
			public CropInGridview gv_crop_images;
			public TextView tv_subjectname;
			public TextView tv_time;
			public TextView tv_city;
			public ImageButton ib_comment;
			public CropInListview  lv_comments;
			public TextView tv_lile_nums;
			public TextView tv_comment_nums;
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
			
			public CropCommentsItemOnclickListener(List<AdvinfoComment> list,int postion){
				this.list = list;
				mAdvInfoId = mDataList.get(postion).advInfoId;
				mAdvinfoPositon = postion;
			}
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (UserDao.getInstance().get()==null) {
					CommonDialog.setLoginDialog(mContext);
				}
				else {
					AdvinfoComment advinfoComment = list.get(position);
					mCommentId = advinfoComment.commentId;
					mCommentUser = advinfoComment.username;
					mContext.setCommentLayoutVisible(true,mAdvInfoId,mCommentId,mCommentUser,mAdvinfoPositon);
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
				List<AdvImgs> mList  =mDataList.get(mOut_positon).mImgs;
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
				intent.setClass(mContext, CropImageActivity.class);
				mContext.startActivity(intent);
			}
			
		}
		/**
		 * 显示评论和赞的popwindow
		 * @param view
		 */
		 private void showPopupWindow(View view) {
		        // 一个自定义的布局，作为显示的内容
		        View contentView = LayoutInflater.from(mContext).inflate(
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
		            		CommonDialog.setLoginDialog(mContext);
		            	}
						else {
							 dismisPopWindow();
							 AdvUploadDao uploadDao = new AdvUploadDao();
							 if (isPraised(mAdvinfoPositon)) {
								CommonUtil.showToask(mContext, "你已赞过");
								return;
							 }
//							 
							// int id =  uploadDao.addAdvPrasis(mAdvInfoID, UserDao.getInstance().get().id,1);
							 new PriaseTask(mAdvInfoID, UserDao.getInstance().get().id,1).execute();
							/*if (id!=0) {
								CommonUtil.showToask(mContext, "赞成功");
								refreshPraise();
							}
							else {
								CommonUtil.showToask(mContext, "赞失败");
							}*/
						}
					}
				});
		        //点击评论
		        Button button = (Button) contentView.findViewById(R.id.bt_commet);
		        button.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		            	if(UserDao.getInstance().get()==null){
		            		CommonDialog.setLoginDialog(mContext);
		            	}
		            	else {
		            		 dismisPopWindow();
		            		 mContext.setCommentLayoutVisible(true,mAdvInfoID,-1,null,mAdvinfoPositon);
						}
		            }
		        });
//		        final PopupWindow popupWindow = new PopupWindow(contentView,
//		        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		        int commentWith = dm.widthPixels/2;
		        mPopupWindow= new PopupWindow(contentView,
		        		commentWith, LayoutParams.WRAP_CONTENT, true);
		        mPopupWindow.setTouchable(true);
		        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
		            @Override
		            public boolean onTouch(View v, MotionEvent event) {
		                return false;
		                // 这里如果返回true的话，touch事件将被拦截
		                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
		            }
		        });
		        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		        // 我觉得这里是API的一个bug
		        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		        // 设置好参数之后再show
//		        popupWindow.showAsDropDown(view,-1,0);
		       
//				int x = location[0]-contentView.getMeasuredWidth();
//				int y = location[1]-(contentView.getMeasuredHeight()-view.getMeasuredHeight());
				int x = location[0]-commentWith;
				int y = location[1]-(contentView.getMeasuredHeight()-view.getMeasuredHeight());
				mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x-10,y);
		    }
		 
		 
		 private class PriaseTask extends AsyncTask<Integer, Integer,Integer> {
				private Integer advInfoId;
				private Integer userId;
				private Integer isPraise;
				public PriaseTask(int advInfoId ,int userId,int isPraise)
				{
					this.advInfoId=advInfoId;
					this.userId = userId;
					this.isPraise  =isPraise;
				}
				
				@Override
				protected Integer doInBackground(Integer... params) {
					AdvUploadDao uploadDao = new AdvUploadDao();
					 int id = uploadDao.addAdvPrasis(mAdvInfoID, UserDao.getInstance().get().id,1);
					 return id;
				}
				@Override
				protected void onPostExecute(Integer result) 
				{
					if (result!=0) {
						CommonUtil.showToask(mContext, "赞成功");
						refreshPraise();
				 }
				else {
					CommonUtil.showToask(mContext, "赞失败");
				}
				}
			}
		 
		 
		 /**
		  * 检查是否赞过
		  * @param mOutPosition
		  * @return
		  */
		 private boolean isPraised(int mOutPosition){
			 List<AdvPraise> praisesList = mDataList.get(mOutPosition).mPraises;
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
			 List<AdvPraise> advPraises = mDataList.get(mAdvinfoPositon).mPraises;
				AdvPraise advPraise = new AdvPraise();
				advPraise.userId = UserDao.getInstance().get().id;
				if (advPraises==null) {
					advPraises = new ArrayList<AdvPraise>();
					advPraises.add(advPraise);
				}
				else {
					advPraises.add(advPraise);
				}
				
				AdvisoryQuestionAdapter.this.notifyDataSetChanged();
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
				mAdvInfoID = mDataList.get(postion).advInfoId;
				mAdvinfoPositon = this.postion;
				showPopupWindow(v);
			}
		 }
		 
		 private class UserHeadOnClickListenner implements OnClickListener{
			 
			 private  int userID;
			 public  UserHeadOnClickListenner(int postion){
				 userID = mDataList.get(postion).userId;
			 }
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(mContext, UserDetailActivity.class);
				intent.putExtra("id", userID);
				mContext.startActivity(intent);
			}
			 
		 }
		 
//		  public void  updateItemView(int itemIndex){
//		    	int visiblePosition = listView.getFirstVisiblePosition(); 
//		    	//得到你需要更新item的View
//		    	View view = listView.getChildAt(itemIndex - visiblePosition);
////		    	mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////				View convertView = mInflater.inflate(R.layout.crop_situation_item, null);
//				ViewHolder holder = new ViewHolder();
//				
//				holder.lv_comments = (CropInListview) view.findViewById(R.id.lv_crop_comments);
//				mCommentAdapter = new AdvisoryQuestionCommentAdapter(mContext, mContext.mAdvComments.get(itemIndex),itemIndex);
//				mCommentAdapter.setOutAdapter(this);
//				holder.lv_comments.setAdapter(mCommentAdapter);
//		  }
		 
		 public void updateView(int index){
			 View view = listView.getChildAt(index);
			 ViewHolder holder = (ViewHolder)view.getTag();
//			 holder.lv_comments.setAdapter(adapter)
		 }
		 
		 
		 public void setListView(ListView view)
	    {
	        this.listView = view;
	    }
		 
		 
}

