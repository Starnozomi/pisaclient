package com.supermap.pisaclient.adapter;

import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.JudgmentCass;
import com.supermap.pisaclient.entity.Legend;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LegendAdapter extends BaseAdapter
{
	private List<Legend> list = null;
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private ViewHolder viewHolder = null;
	public LegendAdapter(Context context, List<Legend> list){
		this.list = list;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}
	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Legend legend = list.get(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.legend_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_color = (TextView)convertView.findViewById(R.id.color);
			viewHolder.tv_caption = (TextView)convertView.findViewById(R.id.caption);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String []  colorRgb = legend.color.split(",");
		int colorInt = Color.rgb(Integer.parseInt(colorRgb[0]),Integer.parseInt(colorRgb[1]),Integer.parseInt(colorRgb[2]));
		viewHolder.tv_color.setBackgroundColor(colorInt);
		//viewHolder.tv_color.setText(legend.caption);
//	    if(!JudgmentCass.IsChinese(legend.caption))
//	    	viewHolder.tv_color.setWidth(JudgmentCass.DipToPixels(mContext,JudgmentCass.CharTodp(legend.caption)));
		viewHolder.tv_caption.setText(legend.caption);
		return convertView;
	}
	
	public class ViewHolder {
		public TextView tv_color;
		public TextView tv_caption;
		
	}

}
