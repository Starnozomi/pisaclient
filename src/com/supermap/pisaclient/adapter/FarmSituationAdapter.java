package com.supermap.pisaclient.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.R.layout;
import com.supermap.pisaclient.biz.CropDao;
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
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.ui.CropImageActivity;
import com.supermap.pisaclient.ui.UserDetailActivity;

public class FarmSituationAdapter extends BaseAdapter {
		private Context mContext;
		private List<AgrInfo> mDataList;
		private LayoutInflater mInflater;
		private ViewHolder viewHolder = null;
		private int mAgrInfoID;
		private int	mOut_position;
		private CityDao mCityDao;
		private ImageLoader mImageLoader;
		private PopupWindow mPopupWindow;
		private ListView listView;
		private boolean isEditing = false;
		private CropDao mCropDao;
		private HashMap<Integer, View> mHeaderMap = new HashMap<Integer, View>();
		
		public FarmSituationAdapter(Context context,List<AgrInfo> list){
			
			this.mContext = context;
			this.mDataList = list;
			mInflater = LayoutInflater.from(context);
			mCityDao = new CityDao(context);
			mImageLoader  = new ImageLoader(context);
			mCropDao = new CropDao();
		}
		
		public void setData(List<AgrInfo> list){
			if ((list!=null)&&(list.size()>0)) {
				this.mDataList = list;
				this.notifyDataSetChanged();
			}
			
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
			AgrInfo agrInfo = mDataList.get(position);
			List<AgrinfoComment> commentsList = new ArrayList<AgrinfoComment>();
			List<AgrPraise> praisesList = new ArrayList<AgrPraise>();
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.crop_situation_item, null);
				
				viewHolder = new ViewHolder();
				viewHolder.iv_user_head = (ImageView)convertView.findViewById(R.id.iv_user_head_icon);
				viewHolder.tv_user = (TextView) convertView.findViewById(R.id.tv_crop_user_name);
				viewHolder.tv_user_comment =(TextView) convertView.findViewById(R.id.tv_crop_user_comment);
				viewHolder.tv_crop_type = (TextView) convertView.findViewById(R.id.tv_crop_type);
				viewHolder.tv_crop_breed = (TextView) convertView.findViewById(R.id.tv_crop_breed);
				viewHolder.tv_crop_grow = (TextView) convertView.findViewById(R.id.tv_crop_grow);
				viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_crop_time);
				viewHolder.tv_city = (TextView) convertView.findViewById(R.id.tv_wrop_city);
				
				viewHolder.ib_comment = (ImageButton) convertView.findViewById(R.id.ib_wrop_comment);
				
				viewHolder.ib_comment.setVisibility(View.GONE);
				viewHolder.lv_comments = (CropInListview) convertView.findViewById(R.id.lv_crop_comments);
				viewHolder.gv_crop_images = (CropInGridview) convertView.findViewById(R.id.gv_crop_images);
				
				viewHolder.tv_lile_nums = (TextView) convertView.findViewById(R.id.tv_heart_nums);
				viewHolder.tv_comment_nums = (TextView) convertView.findViewById(R.id.tv_comment_nums);
				viewHolder.headView = mInflater.inflate(R.layout.praise_header, null);
//				viewHolder.tv_time .setText(DateUtiles.getCropTime(agrInfo.uploadTime)+" ");
				convertView.setTag(viewHolder);
				
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
//				resetViewHolder(viewHolder);
			}
			
			
			//控件添加数据
			viewHolder.iv_user_head.setImageBitmap(CommonUtil.toRoundBitmap(CommonImageUtil.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.crop_user))));
			if ((agrInfo.userHeaderFile==null)||("null".equals(agrInfo.userHeaderFile))||("".equals(agrInfo.userHeaderFile))) {
			}
			else {
				String url = CommonImageUtil.getThumbnailImageUrl(agrInfo.userHeaderFile);
				mImageLoader.DisplayToRoundBitmap(url, viewHolder.iv_user_head, false);
			}
			
			viewHolder.iv_user_head.setOnClickListener(new UserHeadOnClickListenner(position));
			viewHolder.tv_user.setText(agrInfo.userName);
			viewHolder.tv_user_comment.setText(agrInfo.descript);
			if ((agrInfo.croptype!=null)&&(!"".equals(agrInfo.croptype))) {
				viewHolder.tv_crop_type .setText(agrInfo.croptype);
			}
			else {
				viewHolder.tv_crop_type .setText("");
			}
			if ((agrInfo.breed!=null)&&(!"".equals(agrInfo.breed))) {
				viewHolder.tv_crop_breed .setText(agrInfo.breed);
			}
			else {
				viewHolder.tv_crop_breed .setText("");
			}
			if ((agrInfo.growperiod!=null)&&(!"".equals(agrInfo.growperiod))) {
				viewHolder.tv_crop_grow .setText(agrInfo.growperiod);
			}
			else {
				viewHolder.tv_crop_grow .setText("");
			}
			
			viewHolder.tv_time .setText(DateUtiles.getCropTime(agrInfo.uploadTime)+" ");
			if(agrInfo.areacode.length()>6){
				viewHolder.tv_city.setText(mCityDao.queryCityName(agrInfo.areacode.substring(0,6))+mCityDao.queryCityName(agrInfo.areacode));
			}
			else {
				viewHolder.tv_city.setText(mCityDao.queryCityName(agrInfo.areacode));
			}
			
			//查询数据库显示评论和赞数目
//			commentsList = mCityDao.getAgrinfoComments(agrInfo.agrInfoId);
//			praisesList = mCityDao.getAgrPraises(agrInfo.agrInfoId);
			commentsList = agrInfo.mComments;
			praisesList = agrInfo.mAgrPraises;
			
			if (viewHolder.lv_comments.getHeaderViewsCount()!=0) {
				viewHolder.lv_comments.removeHeaderView(viewHolder.headView);	
			}
			viewHolder.lv_comments.setAdapter(null);
			if ((praisesList!=null)&&(praisesList.size()>0)) {
				
				viewHolder.tv_lile_nums.setText(praisesList.size()+"");
				
				TextView username = (TextView) viewHolder.headView.findViewById(R.id.praise_name);
				String nameList ="";
				for(AgrPraise praise:praisesList
						){
					nameList+=praise.userName+" ";
				}
				username.setText(nameList);
				viewHolder.lv_comments.addHeaderView(viewHolder.headView,null,false);
			}
			else {
				viewHolder.tv_lile_nums.setText("0");
			}
			
			if ((commentsList!=null)&&(commentsList.size()>0)) {
				viewHolder.tv_comment_nums.setText(commentsList.size()+"");
			}
			else {
				viewHolder.tv_comment_nums.setText("0");
			}
			viewHolder.lv_comments.setAdapter(new FarmCropInListAdapter(mContext, commentsList, agrInfo));
			viewHolder.lv_comments.setOnItemClickListener(new CropCommentsItemOnclickListener(commentsList,position,viewHolder.lv_comments.getHeaderViewsCount()));
			
			viewHolder.ib_comment.setOnClickListener(new CommentButtonListenner(position));
			viewHolder.gv_crop_images.setOnItemClickListener(new CropImagesItemOnclickListener(position));
			
			if ((agrInfo.mImgs!=null)&&(agrInfo.mImgs.size()>0)) {
				CropInGridAdapter imageAdpter = new CropInGridAdapter(mContext,agrInfo.mImgs ,position);	
				viewHolder.gv_crop_images.setAdapter(imageAdpter);
				imageAdpter.notifyDataSetChanged();
			}
			else {//viewholder缓存的控件内容也缓存了
				viewHolder.gv_crop_images.setAdapter(null);
			}
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
		
		private class ViewHolder {
			ImageView iv_user_head;
			TextView tv_user;
			TextView tv_user_comment;
			CropInGridview gv_crop_images;
			TextView tv_crop_type;
			TextView tv_crop_breed;
			TextView tv_crop_grow;
			TextView tv_time;
			TextView tv_city;
			ImageButton ib_comment;
			CropInListview  lv_comments;
			TextView tv_lile_nums;
			TextView tv_comment_nums;
			View  headView;
		}
		/**
		 * 点击评论item 弹出回复框，并提示回复谁
		 * @author Administrator
		 *
		 */
		private class CropCommentsItemOnclickListener implements OnItemClickListener{
			private List<AgrinfoComment> list ;
			private int mCommentId = -1;
			private String mCommentUser = null;
			private int mAgrInfoId;
			private int position;
			private int mHeaderCount;
			public CropCommentsItemOnclickListener(List<AgrinfoComment> list,int postion,int headerCount){
				this.list = list;
				mAgrInfoId = mDataList.get(postion).agrInfoId;
				this.position = postion;
				this.mHeaderCount = headerCount;
				
			}
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				mOut_position = this.position;
				
				if (UserDao.getInstance().get()==null) {
					CommonDialog.setLoginDialog(mContext);
				}
				else {
					AgrinfoComment agrinfoComment = list.get(position-mHeaderCount);//添加header以后: 0是header
					mCommentId = agrinfoComment.commentId;
					mCommentUser = agrinfoComment.username;
					//mContext.setCommentLayoutVisible(true,mAgrInfoId,mCommentId,mCommentUser);
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
//				List<AgrImgs> mList  = mCityDao.getAgrImgs(mDataList.get(mOut_positon).agrInfoId) ;
				List<AgrImgs> mList  = mDataList.get(mOut_positon).mImgs;
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
		
		private class PriaseTask extends AsyncTask<Integer, Integer,Integer>
		{
			private Integer agrInfoID;
			private Integer userId;
			public PriaseTask(Integer agrInfoID,Integer userId)
			{
				this.agrInfoID=agrInfoID;
				this.userId = userId;
			}
			
			@Override
			protected Integer doInBackground(Integer... params) {
				CropUploadDao uploadDao = new CropUploadDao();
				 int id = uploadDao.addAgrPraise(this.agrInfoID, this.userId);
				 return id;
			}
			@Override
			protected void onPostExecute(Integer result) 
			{
				if (result!=0) {
					new GetPriaseTask(result).execute();
			 }
			else {
				CommonUtil.showToask(mContext, "赞失败");
			}
			}
		}
		
		private class GetPriaseTask extends AsyncTask<Integer, Integer,AgrPraise>
		{
			private Integer praiseID;

			public GetPriaseTask(Integer praiseID)
			{
				this.praiseID=praiseID;
			}
			
			@Override
			protected AgrPraise doInBackground(Integer... params) {
				AgrPraise praise = mCropDao.getAgrPraisebyid(this.praiseID);
				 return praise;
			}
			@Override
			protected void onPostExecute(AgrPraise result) 
			{
				if (result!=null) {
					mDataList.get(mOut_position).mAgrPraises.add(result);
					reFresh();
				}
			}
		}
		
		
		/**
		 * 显示评论和赞的popwindow
		 * @param view
		 */
		 private void showPopupWindow(View view, int position) {
			 
			 
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
		        like.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(UserDao.getInstance().get()==null){
		            		CommonDialog.setLoginDialog(mContext);
		            		return;
		            	}
						
						dismisPopWindow();
						
						 if (isPraised(mOut_position)) {
							CommonUtil.showToask(mContext, "你已赞过");
							return;
						 }
						 new PriaseTask(mAgrInfoID,UserDao.getInstance().get().id).execute();
						/* CropUploadDao uploadDao = new CropUploadDao();
						 int id = uploadDao.addAgrPraise(mAgrInfoID, UserDao.getInstance().get().id);
						 if (id!=0) {
								AgrPraise praise = mCropDao.getAgrPraisebyid(id);
								if (praise!=null) {
									mDataList.get(mOut_position).mAgrPraises.add(praise);
									reFresh();
								}
						 }
						else {
							CommonUtil.showToask(mContext, "赞失败");
						}*/
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
		            		// mContext.setCommentLayoutVisible(true,mAgrInfoID,-1,null);
						}
		            }
		        });
//		        final PopupWindow popupWindow = new PopupWindow(contentView,
//		        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		        int commentWith = dm.widthPixels/2;
		        mPopupWindow= new PopupWindow(contentView,
		        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
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
		        
		        AnimationSet animationSet = new AnimationSet(true);
		           //参数1：x轴的初始值
		           //参数2：x轴收缩后的值
		           //参数3：y轴的初始值
		           //参数4：y轴收缩后的值
		           //参数5：确定x轴坐标的类型
		           //参数6：x轴的值，0.5f表明是以自身这个控件的一半长度为x轴
		           //参数7：确定y轴坐标的类型
		           //参数8：y轴的值，0.5f表明是以自身这个控件的一半长度为x轴
//		           ScaleAnimation scaleAnimation = new ScaleAnimation(
//		                  0, 0.1f,0,0.1f,
//		                  Animation.RELATIVE_TO_SELF,0.5f,
//		                  Animation.RELATIVE_TO_SELF,0.5f);
//		           scaleAnimation.setDuration(1000);
//		           animationSet.addAnimation(scaleAnimation);
////		          mPopupWindow.setAnimationStyle(animationSet)
//		           
		        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		        // 设置好参数之后再show
//		        popupWindow.showAsDropDown(view,-1,0);
		       
//				int x = location[0]-contentView.getMeasuredWidth();
//				int y = location[1]-(contentView.getMeasuredHeight()-view.getMeasuredHeight());
		        commentWith = contentView.getMeasuredWidth();
				int x = location[0]-commentWith;
				int y = location[1]-(contentView.getMeasuredHeight()-view.getMeasuredHeight());
				mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x-10,y);
//				((View) mPopupWindow).startAnimation(animationSet);
				isEditing = true;
		    }
		 
		 private boolean isPraised(int position){
//			 List<AgrPraise> praisesList = mCityDao.getAgrPraises(agrinfoId);
			 List<AgrPraise> praisesList = mDataList.get(position).mAgrPraises;
			 if ((praisesList==null)||(praisesList.size()==0)) {
				return false;
			 }
			 for(AgrPraise agrPraise :praisesList){
				 if (agrPraise.userId==UserDao.getInstance().get().id) {
					return true;
				 }
			 }
			 return false;
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
				mAgrInfoID = mDataList.get(postion).agrInfoId;
				mOut_position = postion;
				if ((mPopupWindow!=null)&&(mPopupWindow.isShowing())) {
					mPopupWindow.dismiss();
				}else {
					showPopupWindow(v,postion);
				}
				
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
		 
		 public void updateView(int index){
			 View view = listView.getChildAt(index);
			 ViewHolder holder = (ViewHolder)view.getTag();
//			 holder.lv_comments.setAdapter(adapter)
		 }
		 
		 public void setListView(ListView view)
		    {
		        this.listView = view;
		    }
		 
		 public void reFresh(){
			 this.notifyDataSetChanged();
		 }
		 
		 public int getAgrinfoPosition(){
			 return mOut_position;
		 }
		 
}

