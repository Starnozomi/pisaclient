package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.entity.CropType;

public class CommisCropAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<CropType> mDataList;
	private LayoutInflater mInflater;
	private ViewHolder viewHolder = null;
	private ListView listView;
	private AdvisoryQuestionPictureAdapter mImageAdapter;
	
	
	public CommisCropAdapter(Context context, List<CropType> list){
		this.mContext =context;
		this.mDataList = list;
		mInflater = LayoutInflater.from(context);
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
		CropType categoryItem = mDataList.get(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.croplist_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.crop_name = (TextView) convertView.findViewById(R.id.crop_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//控件添加数据
		String aaaaa = categoryItem.name;
		viewHolder.crop_name.setText(categoryItem.name);
		
		mImageAdapter.notifyDataSetChanged();
		return convertView;
	}
	
	protected void resetViewHolder(ViewHolder p_ViewHolder) {
	p_ViewHolder.crop_name.setText(null);
	}
	
	public class ViewHolder {
		public TextView crop_name;
	}
	 
	 
	 public void updateView(int index){
		 View view = listView.getChildAt(index);
		 ViewHolder holder = (ViewHolder)view.getTag();
	 }
	 
	 public void setListView(ListView view)
    {
        this.listView = view;
    }
	 
	 
		 
}

