package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.WarningInfo;

public class WaringInfoAdapter extends BaseAdapter {
	private List<WarningInfo> mList ;
	private Context mContext;
	private LayoutInflater mInflater;
	public WaringInfoAdapter(Context context,List<WarningInfo> list){
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
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int postion, View content, ViewGroup arg2) {
		
		content = mInflater.inflate(R.layout.warning_list_item, null);
//		TextView tv = (TextView)(content.findViewById(R.id.tv_warning_content));
//		tv.setText(mList.get(postion).content);
		return content;
	}

}
