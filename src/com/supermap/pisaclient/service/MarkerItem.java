package com.supermap.pisaclient.service;

import android.os.Bundle;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;


public class MarkerItem implements ClusterItem
{
	private LatLng mPosition;
	private BitmapDescriptor bitmap;
	private Bundle mBundle;
	
	public MarkerItem(LatLng latLng)
	{
		mPosition = latLng;
	}
	
	@Override
	public LatLng getPosition() 
	{
		return mPosition;
	}

	@Override
	public BitmapDescriptor getBitmapDescriptor() 
	{
		return bitmap;
	}
	
	public MarkerItem setBitmapDescriptor(BitmapDescriptor bitmapDescriptor)
	{
		bitmap=bitmapDescriptor;
		return this;
	}
	
	/**
	 * 获取额外的信息
	 * @return
	 */
	@Override
	public Bundle getExtraInfo()
	{
		return mBundle;
	}
	
	/**
	 * 额外的信息
	 * @param bundle
	 * @return
	 */
	public MarkerItem setExtraInfo(Bundle bundle)
	{
		mBundle=bundle;
		return this;
	}
}
