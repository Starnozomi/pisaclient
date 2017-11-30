/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @TianDiTuLayerView.java - 2014-8-8 下午2:42:45
 */

package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.supermap.android.maps.AbstractTileLayerView;
import com.supermap.android.maps.BoundingBox;
import com.supermap.android.maps.CoordinateReferenceSystem;
import com.supermap.android.maps.Point2D;
import com.supermap.android.maps.Tile;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.LocalTile;
import com.supermap.pisaclient.http.HttpHelper;

public class PisaLayerView extends AbstractTileLayerView {

	private String dir = null;

	private String url = null;

	private long mapScales[] = null;

	private double mapResolutions[] = null;

	private String ext = "jpg";

	private LocalTile localTile;

	public void init(String url) {
		init(url, null, null, null, 0, null);
	}

	public void init(String url, String dir) {
		init(url, dir, null, null, 0, null);
	}

	public void init(String url, String dir, String layerName) {
		init(url, dir, layerName, null, 0, null);
	}

	public void init(String url, String dir, String layerName, long[] scales) {
		init(url, dir, layerName, scales, 0, null);
	}

	public void init(String url, String dir, String layerName, long[] scales, String ext) {
		init(url, dir, layerName, scales, 0, ext);
	}

	public void init(String url, String dir, String layerName, long[] mapScales, int wkid, String ext) {
		
		localTile = new LocalTile();
		if (url != null)
			this.url = url;
		else
			this.url = HttpHelper.getMapUrl();
		if (dir != null)
			this.dir = dir;
		if (mapScales != null)
			this.mapScales = mapScales;
		this.mapResolutions = new double[this.mapScales.length];
		for (int i = 0; i < this.mapScales.length; i++) {
			mapResolutions[i] = CommonUtil.getResolution(this.mapScales[i]);
		}
		if (layerName != null)
			this.layerName = layerName;
		isGCSLayer = true;
		this.layerBounds = new BoundingBox(new Point2D(-180, 90), new Point2D(180, -90));
		this.crs = new CoordinateReferenceSystem();
		this.resolutions = mapResolutions;
		if (isGCSLayer) {
			double radius = this.crs.datumAxis > 1d ? this.crs.datumAxis : 6378137d;
			for (int i = 0; i < resolutions.length; i++) {
				resolutions[i] = resolutions[i] * Math.PI * radius / 180.0;
			}
		}
		if (wkid == 0)
			this.crs.wkid = 4326;
		else
			this.crs.wkid = wkid;
		if (ext != null) {
			this.ext = ext;
		} else {
			this.ext = "jpg";
		}
		isLayerInited = true;
		
		asyncGetTilesFromCache();
	}

	public PisaLayerView(Context context) {
		super(context);
	}

	public PisaLayerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PisaLayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void initTileContext(Tile tile) {
		int index = getResolutionIndex();
		if (index == -1) {
			return;
		}
		tile.setScale(mapScales[index]);
		String result = localTile.getTileFile(url, dir, tile.getX(), tile.getY(), (long) tile.getScale(), ext);
//		Log.e("Tile","***Tile:"+tile.getX()+","+tile.getY()+","+tile.getScale());
		Constant.selectedScale = mapScales[index];
		Constant.SCALE_INDEX = index;
//		Log.e("url","urls:"+result);
		tile.setUrl(result);
	}
	
	public void exit(){
		localTile.setExit();
	}

}
