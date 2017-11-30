package com.supermap.pisaclient.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CropInListview;
import com.supermap.pisaclient.common.views.SmallTextButton;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.ui.AdvDetailActivity;
import com.supermap.pisaclient.ui.AdvisoryQuestionActivity;
import com.supermap.services.components.Map;
import com.umeng.common.net.e;


/**
 * 已知bug:
 * 		1.有时评论完以后，打分button不隐藏，原因是button对应的position不对
 * @author Administrator
 *
 */
public class AdvisoryDeatailQuestionCommentAdapter extends BaseAdapter {

	private List<AdvinfoComment> mDataList;
	private LayoutInflater mInflater;
	private AdvDetailActivity mContext;
	private int mCommentPageSize = 6;
	private boolean isNeedMoreView =false;
	private int mCommentPageIndex = 1;
	private int mPageNum = 0;
	private int mExpertId = 0;
	private int mAdvInfoId = 0;
	private int mCommentID = 0;
	private MessageUploadDao messageUploadDao;
	HashMap<Integer, Float> scores = new HashMap<Integer, Float>();
	//记录有打分button显示的位置
		private List<Integer> num = new ArrayList<Integer>();
	/**
	 * 打分button 
	 * 		1.对于一个专家只显示一个打分button
	 * 		2.打完分后button不再显示
	 * 
	**/
	private int mCommentAdvinfoId ;//点击打分button对应的评论记录的咨询id
	private int mCommentExpertId ;//点击打分button对应的评论记录的专家id
	private HashMap<String,Boolean> isShow = new HashMap<String,Boolean>();
	private float mScore = 0;
	private SmallTextButton mScore_Button;
	private CropInListview mListview;
	private int mItemIndex = 0;
	private AdvisoryQuestionAdapter mOutAapter;

	public AdvisoryDeatailQuestionCommentAdapter(Context context, List<AdvinfoComment> list) {
		this.mContext = (AdvDetailActivity) context;
		this.mDataList = list;
		messageUploadDao = new MessageUploadDao();
		if (mDataList!=null) {
			mPageNum = mDataList.size()/mCommentPageSize+1;
		}
		setShowScore();
		setScoreButtonShow();
	}

	@Override
	public int getCount() {
		if (mCommentPageIndex<mPageNum) {
			isNeedMoreView = true;
			return mCommentPageIndex*mCommentPageSize;
		}
		isNeedMoreView = false;
	    if (mDataList!=null) {
			return mDataList.size();
		}
	    else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AdvinfoComment advinfoComment = mDataList.get(position);
		inViewHolder holder = null;
		if (convertView == null) {
			holder = new inViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.adv_comment_item, null);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tv_crop_comment);
			holder.bnScore = (SmallTextButton) convertView.findViewById(R.id.bn_score);
//			holder.bnScore = new SmallTextButton(mContext, "打分");
//			RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.rl_score);
//			
//			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		    lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		    holder.bnScore.setVisibility(View.GONE);
//		    layout.addView(holder.bnScore,lp1);
			convertView.setTag(holder);
		} else {
			holder = (inViewHolder) convertView.getTag();
		}
		if (advinfoComment.isExpert) {
			//专家的回复
			if (advinfoComment.parentId==-1) {
				holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(advinfoComment.userId)+">"+advinfoComment.expertName+":"+"</font>"+
								"<font color=#000000>"+advinfoComment.comment+"</font>"));
			}
			else {
				holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(advinfoComment.userId)+">"+advinfoComment.expertName+"</font>"+
						"<font color=#000000>"+"回复"+"</font>"+
						"<font color="+Utils.getRoleTypeColor(advinfoComment.parentId)+">"+advinfoComment.parentusername+":"+"</font>"+
						"<font color=#000000>"+advinfoComment.comment+"</font>"));
			}
		}
		else {//普通用户的回复
			if (advinfoComment.parentId==-1) {
				holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(advinfoComment.userId)+">"+advinfoComment.username+":"+"</font>"+
								"<font color=#000000>"+advinfoComment.comment+"</font>"));
			}
			else {
				holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(advinfoComment.userId)+">"+advinfoComment.username+"</font>"+
						"<font color=#000000>"+"回复"+"</font>"+
						"<font color="+Utils.getRoleTypeColor(advinfoComment.parentId)+">"+advinfoComment.parentusername+":"+"</font>"+
						"<font color=#000000>"+advinfoComment.comment+"</font>"));
			}
			
		}
		if (isNeedMoreView&&(position==(mCommentPageIndex*mCommentPageSize-1))) {//点击加载更多评论
			holder.tvComment.setText(Html.fromHtml("<font color=#6797d3>"+"更多评论"+"</font>"
					));
			holder.tvComment.setOnClickListener(new SecondLvOnclickListener(position));
		}
		//我的咨询，才能给专家评论打分，而且对同一专家对于该咨询信息的所有评论只能打一次分
		if (isShowScoreButton(position)){
			holder.bnScore.setVisibility(View.VISIBLE);
			holder.bnScore.setOnClickListener(new AdvScoreForExpertListner(advinfoComment,holder.bnScore,position));
		}
		else if (scores.get(position)!=null) {//显示用户给专家的打分
			holder.bnScore.setVisibility(View.VISIBLE);
			holder.bnScore.setText(String.valueOf(scores.get(position))+"分");
		}
		
//		if ((mContext.mType==3)&&(!UserDao.getInstance().get().isExport)&&(advinfoComment.isExpert)&&(advinfoComment.score==-1)&&(isShow.get(""+advinfoComment.expertid)==null)) {//是专家，但是用户还没有给他打分
//			holder.bnScore.setVisibility(View.VISIBLE);
//			isShow.put(""+advinfoComment.expertid, true);
//			holder.bnScore.setOnClickListener(new AdvScoreForExpertListner(advinfoComment,holder.bnScore,position));
//		}
		holder.tvComment.setOnClickListener(new SecondLvOnclickListener(position));
		
		return convertView;
	}
	
	public boolean isShowScoreButton(int positon){
		for(Integer integer:num){
			if (positon==integer) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置显示分数  position score
	 */
	public void setShowScore(){
		if ((mDataList==null)||(mDataList.size()==0)) {
			return;
		}
		int maxSize = mDataList.size();
		//记录有打分button显示的位置
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for(int i = 0;i<maxSize;i++){
			 AdvinfoComment temComment = mDataList.get(i);
			if ((temComment.isExpert)&&(temComment.score!=-1)&&(temp.get(temComment.expertid)==null)) {
				temp.put(temComment.expertid, i);
				scores.put(i, temComment.score);
			}
		}
	}
	
	public void setScoreButtonShow(){
		if ((mDataList==null)||(mDataList.size()==0)) {
			return;
		}
		int maxSize = mDataList.size();
		//记录有打分button显示的位置
//		List<Integer> num = new ArrayList<Integer>();
		int commentid[] =new int[maxSize];
		for(int i = 0;i<maxSize;i++){
			 AdvinfoComment temComment = mDataList.get(i);
			if ((temComment.isExpert)&&(temComment.score==-1)) {
				commentid[i] =temComment.expertid;
			}
			else {
				commentid[i]=0;//默认值不是专家
			}
		}
		
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for(int i = 0;i<maxSize;i++){
			if (commentid[i]==0) {////默认值不是专家
				continue;
			}
			if(temp.get(commentid[i])==null){
				temp.put(commentid[i], i);
				num.add(i);
			}
			
//			boolean isExit = false;
//			for(Integer integer:num){
//				if (commentid[i]==integer) {
//					isExit =true;
//					break;
//				}
//			}
//			if (!isExit) {
//				num.add(i);//把它的位置保留下来，用于显示打分button
//			}
		}
		
	}
	
	private class inViewHolder {
		TextView tvComment;
		SmallTextButton   bnScore;
	}
	
	
	private class AdvScoreForExpertListner implements OnClickListener{

		public AdvScoreForExpertListner(AdvinfoComment advinfoComment,SmallTextButton view,int itemIndex){
			mExpertId = advinfoComment.expertid;
			mAdvInfoId = advinfoComment.advInfoId;
			mCommentID = advinfoComment.commentId;
			mScore_Button = view;
			mItemIndex = itemIndex;
			
		}
		@Override
		public void onClick(View v) {
//		CommonUtil.showToask(mContext, "给专家打分");
			showScore();
		}
		
	}
	/**
	 * 点击评论textview弹出回复框
	 * @author Administrator
	 *
	 */
	private class SecondLvOnclickListener implements OnClickListener{
		
		private int mCommentId = -1;
		private String mCommentUser = null;
		private int position;
		private int mAdvInfoId;
		public SecondLvOnclickListener(int position){
			this.position = position;
			AdvinfoComment advinfoComment = mDataList.get(position);
			mCommentId = advinfoComment.commentId;
			mCommentUser = advinfoComment.username;
			mAdvInfoId = advinfoComment.advInfoId;
		}
		@Override
		public void onClick(View v) {
			if (isNeedMoreView&&(position==(mCommentPageIndex*mCommentPageSize-1))) {
				mCommentPageIndex++;
				notifyDataSetChanged();
			}
			else {
				
				if (UserDao.getInstance().get()==null) {
					CommonDialog.setLoginDialog(mContext);
				}
				else {
					mContext.setCommentLayoutVisible(true,mAdvInfoId,mCommentId,mCommentUser);
				}
				
			}
			
//			if (isNeedMoreView&&(position==5)) {
//				CommonUtil.showToask(mContext, "加载更多评论");
//				Intent intent = new Intent();
//				intent.setClass(mContext, CropDetailActivity.class);
//				mContext.startActivity(intent);
//			}
//			mContext.setCommentLayoutVisible(true,mAgrInfoID,mCommentId,mCommentUser);
		}
	}
	/**
	 * 
	 * 自定义打分对话框
	 * 
	 */
	
    private void showScore() 
    { 
        AlertDialog.Builder customDia=new AlertDialog.Builder(mContext); 
        final View view=LayoutInflater.from(mContext).inflate(R.layout.adv_scroe, null); 
        customDia.setTitle("给专家打分"); 
        customDia.setView(view); 
        customDia.setPositiveButton("打分", new DialogInterface.OnClickListener() { 
             
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
                // TODO Auto-generated method stub 
            	RatingBar  scroe =(RatingBar) view.findViewById(R.id.rb_adv_score);
            	mScore = scroe.getRating();
            	new uploadScore().execute();
            } 
        }); 
        customDia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
        customDia.create().show(); 
    } 
    
    public void refresh(){
    	notifyDataSetChanged();
    }
    
    private class uploadScore extends AsyncTask<String, integer,Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {//这个参数//返回值
				int scoreid =new AdvUploadDao().addAdvScore(mAdvInfoId, UserDao.getInstance().get().id, mExpertId, mScore);
				if (scoreid==0) {
					return false;	
				}
				
				//用户给专家打分
				ClientMsg scroeMsg = new ClientMsg();
				scroeMsg.fromid = UserDao.getInstance().get().id;
				scroeMsg.toid = UserDao.getInstance().getUserIdByExpertId(mExpertId);
				scroeMsg.msgtypeid = 4;
				scroeMsg.mainid = scoreid;
				scroeMsg.msgsendtypeid =1;
				scroeMsg.content = "用户给我打分了";
				int msgid = messageUploadDao.addMsg(scroeMsg);
				if (msgid==0) {
					return false;
				}
				return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				CommonUtil.showToask(mContext, "专家打分上传成功");
				
				for(AdvinfoComment advinfoComment :mDataList){
					if(advinfoComment.commentId==mCommentID){
						advinfoComment.score = mScore;
						break;
					}
				}
//				updateScoreButton(false, mItemIndex);
				setShowScore();
				for(int i = 0;i<num.size();i++){
					if (num.get(i)==mItemIndex) {
						num.remove(i);
						break;
					}
				}
				AdvisoryDeatailQuestionCommentAdapter.this.notifyDataSetChanged();
//				refresh();
//				没有起作用
//				mScore_Button.setVisibility(View.INVISIBLE);
			}
			super.onPostExecute(result);
		}
	}
    
    private void  updateScoreButton(boolean visible,int itemIndex){
//    	mOutAapter.updateItemView(itemIndex);
    	int visiblePosition = mListview.getFirstVisiblePosition(); 
    	//得到你需要更新item的View
    	View view = mListview.getChildAt(itemIndex - visiblePosition);
//    	mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View convertView = mInflater.inflate(R.layout.adv_comment_item, null);
    	inViewHolder holder = new inViewHolder();
    	holder.bnScore = (SmallTextButton) view.findViewById(R.id.bn_score);
    	holder.bnScore.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
    }
    
    public void setListView(CropInListview listview){
    	this.mListview = listview;
    }
    
    public void setOutAdapter(AdvisoryQuestionAdapter adapter){
    	this.mOutAapter = adapter;
    }

}