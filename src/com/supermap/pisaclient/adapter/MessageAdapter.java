/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CommentAdapter.java - 2014-4-4 下午5:31:27
 */

package com.supermap.pisaclient.adapter;

import java.util.List;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.Science;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.ui.AdvDetailActivity;
import com.supermap.pisaclient.ui.CropUploadActivity;
import com.supermap.pisaclient.ui.MessageCenterActivity;
import com.supermap.pisaclient.ui.ScienceDetailActivity;
import com.supermap.pisaclient.ui.SuggestDetailActivity;
import com.supermap.pisaclient.ui.WeatherActivity;

public class MessageAdapter extends BaseAdapter {

	private List<ClientMsg> all;
	private LayoutInflater mInflater;
	private Context mContext;
	private CityDao mCityDao;
	private int mMsgid = 0;
	private MessageUploadDao messageUploadDao;
	public MessageAdapter(Context context, List<ClientMsg> all) {
		this.mContext = context;
		this.all = all;
		this.mCityDao = new CityDao(context);
		messageUploadDao = new MessageUploadDao();
	}

	@Override
	public int getCount() {
		return all.size();
	}

	@Override
	public Object getItem(int position) {
		return all.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.msg_item, null);
			holder.tvInfo = (TextView) convertView.findViewById(R.id.tv_msg_content);
			holder.tvStime = (TextView) convertView.findViewById(R.id.tv_msg_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ClientMsg clientMsg = all.get(position);
		if (clientMsg.ischeck==0) {
			convertView.setBackgroundColor(0xff4294fa);
		}
		if (clientMsg.msgtypeid==6) {
			holder.tvInfo.setText("农气上报消息："+clientMsg.content);
		}else if(clientMsg.msgtypeid==2){
			holder.tvInfo.setText("回复消息："+clientMsg.content);
		}
		else {
			holder.tvInfo.setText(clientMsg.content);
		}
		
		holder.tvStime.setText(clientMsg.msgTime);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMsgid = clientMsg.msgid;
				v.setBackgroundColor(Color.WHITE);
				Intent intent = null;
				 switch (clientMsg.msgtypeid) {
				 	case 1://普通消息
						break;
				 	case 2://专家回复消息
					case 3://用户回复专家消息
					case 4://打分消息
					case 5://提醒 专家回复用户 （用户咨询以后，系统分配专家解答）
						intent  = new Intent(mContext,AdvDetailActivity.class);
						intent.putExtra("msgid", clientMsg.msgid);
						mContext.startActivity(intent);
						break;
					case 6://提醒用户 农情上报
						intent  = new Intent(mContext,CropUploadActivity.class); 
						intent.putExtra("msgid", clientMsg.msgid);
						mContext.startActivity(intent);
						break;
					case 7://提醒用户 农情上报
					case 8://提专家建议
						intent  = new Intent(mContext,SuggestDetailActivity.class); 
						intent.putExtra("msgid", clientMsg.msgid);
						mContext.startActivity(intent);
						break;
					default:
						break;
					}
				//更新本地数据库 消息已查看
				mCityDao.updateMsgChecked(clientMsg);
				//更新服务器数据库消息已查看
				new LoadDataTask().execute();
				
			}

		});
		return convertView;
	}

	private class ViewHolder {
		TextView tvInfo;
		TextView tvStime;
	}
	
	public class LoadDataTask extends AsyncTask<String, Integer,Integer>{
		
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			int result = 0;
			User user = UserDao.getInstance().get();
			if ((mMsgid!=0)&&(user!=null)) {
				result = messageUploadDao.updateCheckMsg(user.id, mMsgid);
			}
			return result;
		}
		@Override
		protected void onPostExecute(Integer result) {
			if (result==0) {
//				CommonUtil.showToask(mContext, "服务器更新数据数据出现问题");
			}
		}
	}

}
