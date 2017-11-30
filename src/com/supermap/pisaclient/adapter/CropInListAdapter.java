package com.supermap.pisaclient.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.RoleType;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.ui.SituationActivity;


class CropInListAdapter extends BaseAdapter {

	private List<AgrinfoComment> mDataList;
	private LayoutInflater mInflater;
	private SituationActivity mContext;
	private AgrInfo mAgrInfo;
	private int mCommentPageSize = 6;
	private boolean isNeedMoreView =false;
	private int mCommentPageIndex = 1;
	private int mPageNum = 0;

	public CropInListAdapter(Context context, List<AgrinfoComment> list,AgrInfo agrInfo) {
		this.mContext = (SituationActivity) context;
		this.mDataList = list;
		mAgrInfo = agrInfo;
		if ((list!=null)) {
			mPageNum = mDataList.size()/mCommentPageSize+1;
		}
		
	}

	@Override
	public int getCount() {
		if (mDataList==null) {
			return 0;
		}
		if (mCommentPageIndex<mPageNum) {
			isNeedMoreView = true;
			return mCommentPageIndex*mCommentPageSize;
		}
		isNeedMoreView = false;
		return mDataList.size();
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
		AgrinfoComment agrinfoComment = mDataList.get(position);
		inViewHolder holder = null;
		if (convertView == null) {
			holder = new inViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView =  mInflater.inflate(R.layout.crop_comment_item, null);
			holder.tvComment = (TextView) convertView.findViewById(R.id.tv_crop_comment);
			convertView.setTag(holder);
		} else {
			holder = (inViewHolder) convertView.getTag();
		}
				
		if (agrinfoComment.parentId==-1) {
//			holder.tvComment.setText(Html.fromHtml("<font color=#6797d3>"+agrinfoComment.username+":"+"</font>"+
//							"<font color=#000000>"+agrinfoComment.comment+"</font>"));
			holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(agrinfoComment.userId)+">"+agrinfoComment.username+":"+"</font>"+
					"<font color=#000000>"+agrinfoComment.comment+"</font>"));
		}
		else {
//			holder.tvComment.setText(Html.fromHtml("<font color=#6797d3>"+agrinfoComment.username+"</font>"+
//					"<font color=#000000>"+"回复"+"</font>"+
//					"<font color=#6797d3>"+agrinfoComment.parentusername+":"+"</font>"+
//					"<font color=#000000>"+agrinfoComment.comment+"</font>"));
			holder.tvComment.setText(Html.fromHtml("<font color="+Utils.getRoleTypeColor(agrinfoComment.userId)+">"+agrinfoComment.username+"</font>"+
					"<font color=#000000>"+"回复"+"</font>"+
					"<font color="+Utils.getRoleTypeColor(agrinfoComment.parentId)+">"+agrinfoComment.parentusername+":"+"</font>"+
					"<font color=#000000>"+agrinfoComment.comment+"</font>"));
		}
		if (isNeedMoreView&&(position==(mCommentPageIndex*mCommentPageSize-1))) {//点击加载更多评论
			holder.tvComment.setText(Html.fromHtml("<font color=#6797d3>"+"更多评论"+"</font>"
					));
			holder.tvComment.setOnClickListener(new SecondLvOnclickListener(position));
		}
//		holder.tvComment.setOnClickListener(new SecondLvOnclickListener(position));
		return convertView;
	}

	private class inViewHolder {
		TextView tvComment;
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
		public SecondLvOnclickListener(int position){
			this.position = position;
			AgrinfoComment agrinfoComment = mDataList.get(position);
			mCommentId = agrinfoComment.commentId;
			mCommentUser = agrinfoComment.username;
		}
		@Override
		public void onClick(View v) {
			if (isNeedMoreView&&(position==(mCommentPageIndex*mCommentPageSize-1))) {
				mCommentPageIndex++;
				notifyDataSetChanged();
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


}