package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.FarmWarnInfo;
public class FarmWarnAdapter extends BaseAdapter {
	private List<FarmWarnInfo> mList ;
	private Context mContext;
	private LayoutInflater mInflater;
	public FarmWarnAdapter(Context context,List<FarmWarnInfo> list){
		this.mContext = context;
		this.mList  = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = ( LinearLayout)mInflater.inflate(R.layout.farm_warn_item, null);
			holder.rlItem = (RelativeLayout)convertView.findViewById(R.id.rl_warn_item);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_warn_title);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_warn_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FarmWarnInfo info = mList.get(position);
		holder.tvTitle.setText(info.title);
		holder.tvTime.setText(info.CreateTime);
		if (position % 2 == 0) {
			holder.rlItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_odd_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
			holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.feed_odd_text_color));
		} else {
			holder.rlItem.setBackgroundColor(mContext.getResources().getColor(R.color.feed_even_color));
			holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
			holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.feed_even_text_color));
		}
		return convertView;
	}

	
	class ViewHolder{
		RelativeLayout rlItem;
		TextView tvTitle;
		TextView tvTime;
	}
	
}
