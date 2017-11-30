package com.supermap.pisaclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.http.HttpHelper;

public class CropInGridAdapter extends BaseAdapter {
	private Context mContext;
	private List<AgrImgs> mDataList;
	private LayoutInflater mInflater;
	private int mPosition;
	private ImageLoader mImageLoader;
	
	public CropInGridAdapter(Context context,List<AgrImgs> list,int position){
		
		this.mContext = context;
		this.mDataList = list;
		mInflater = LayoutInflater.from(context);
		this.mPosition = position;
		mImageLoader = new ImageLoader(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDataList.get(arg0);
	}

	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.crop_gridview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.contentIcon = (ImageView) convertView.findViewById(R.id.iv_crop_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = CommonImageUtil.getThumbnailImageUrl(mDataList.get(position).URLcontent);
		mImageLoader.DisplayImage(url, viewHolder.contentIcon, false);
		return convertView;
	}
	

	private  String getThumbnailName(String name){
		int len = name.length();
		int index = name.indexOf('.');
		String temp = name.substring(0, index);
		String temp1 = temp+"_thumbnail";
		String temp2 = name.substring(index,len-index);
		return temp1+temp2;
	}
	
	private class ViewHolder {
		ImageView contentIcon;
	}

}
