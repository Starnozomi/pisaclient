package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.common.views.HorizontalListView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.adapter.LegendAdapter;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.Legend;
import com.supermap.pisaclient.http.JsonHelper;
import com.supermap.pisaclient.http.JsonHelper.RenderModle;
import com.supermap.pisaclient.interfaces.OnAreaChangListener;
import com.supermap.pisaclient.interfaces.OnRasterProductListener;
import com.supermap.pisaclient.service.RasterProduct.OnRasterProductLoadListener;


public class RasterProductManager implements OnAreaChangListener {
	
	private RasterProductManager oThis = this;
	private BaiduMap mBaiduMap;
	private Context context;
	private String ptype;
	private String pname;
	private LayoutInflater mInflater;

	private Map<String,RasterProduct> rasterProducts;
 	private RasterProduct currentRasterProduct;
	private List<Overlay>lstAllOverLay =null;
	private View[] views;
	private boolean isTempShowing = false;
	private boolean isPreShowing = false;
	private boolean isSoilShowing = false;
	private OnRasterProductListener listener ;
	
	public RasterProductManager(Context context,BaiduMap mBaiduMap,View[] views,OnRasterProductListener l){
		this.context = context;
		this.mBaiduMap = mBaiduMap;
	    this.rasterProducts = new HashMap<String,RasterProduct>();
		this.lstAllOverLay = new ArrayList<Overlay>();
		this.listener = l;
		this.views = views;//视图 0=title,1=legend,2=ListView
		mInflater = LayoutInflater.from(context);
	}
	
	public void reset(){
		isTempShowing =false;
		isPreShowing = false;
		isSoilShowing =false;
		this.hide();
		this.rasterProducts =null;
		this.lstAllOverLay = null;
		this.rasterProducts = new HashMap<String,RasterProduct>();
		this.lstAllOverLay = new ArrayList<Overlay>();
	}
	
	/******************************
	 * 显示最近的温度
	 */
	public void showLatestTemp(){
		if(!isTempShowing){
			if(PlaceManager.getCurrentArea()!= null){
				load("temp",this.getLatestProductName(),PlaceManager.getCurrentArea().enname);
				isTempShowing = true;
			}
		}else{
			oThis.hide();
			isTempShowing = false;
		}
		
	}
	
	/*********************
	 * 显示最近的降水
	 */
	public void showLatestPre(){
		if(!isPreShowing){
			if(PlaceManager.getCurrentArea() != null){
				load("pre",this.getLatestProductName(),PlaceManager.getCurrentArea().enname);
				isPreShowing = true;
			}
			
		}else{
			oThis.hide();
			isPreShowing = false;
		}
		
	}
	/**************************************
	 * 显示最近的日照
	 * @param areaName
	 */
	public void showLatestSunlight(String areaName){
		if(!isSoilShowing){
			load("soil",this.getLatestProductName(),PlaceManager.getCurrentArea().enname);
			isSoilShowing = true;
		}else{
			oThis.hide();
			isSoilShowing = false;
		}
	}
	
	//显示专题图
	public void showSpecialMap(String ptype,String pname){
		//分区县
		if(PlaceManager.getCurrentArea() != null){
			load(ptype,pname,PlaceManager.getCurrentArea().enname);
		}
		
	}
	
	public String getLatestProductName(){
		Calendar  calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH)+1;
		int d = calendar.get(Calendar.DATE);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int rh = h-1;
		int rd = d;
		String pName = y+ "" + (m < 10 ? "0" + m : m) + "" + (rd < 10 ? "0" + rd : rd) + (rh < 10 ? "0" + rh : rh) ;
		return pName;
	}
	
	
	public void load(String ptype,String pname,String area){
		final String pKey = ptype+"_"+pname+"_"+area;
		if(!oThis.rasterProducts.containsKey(pKey)){
			OnRasterProductLoadListener l = new OnRasterProductLoadListener(){
	
				@Override
				public void Loaded(RasterProduct rp) {
					// TODO Auto-generated method stub
					if(!oThis.rasterProducts.containsKey(pKey)){
						oThis.rasterProducts.put(pKey, rp);
					}
					oThis.show(pKey);
				}
				
			};
			RasterProduct rp = new RasterProduct(this.context,ptype,pname,PlaceManager.getCurrentArea().enname, l);
		}else{
			oThis.show(pKey);
		}
	}
	
	public void clear(){
		this.hide();
	}
	
	public void show(String pKey){
		for(View v : views){
			if(v != null) v.setVisibility(View.GONE);
		}
		for(Overlay lay :lstAllOverLay)
		{
			if(lay!=null) lay.remove();
		}
		lstAllOverLay.clear(); 
	
		this.currentRasterProduct = this.rasterProducts.get(pKey);
		
		if(this.currentRasterProduct.getOverlayOptionsList().size() > 0){
			for(View v : views){
				if(v != null) v.setVisibility(View.VISIBLE);
			}
			((TextView)views[0]).setText(this.currentRasterProduct.getTitle());
			LegendAdapter legendAdapter = new LegendAdapter(oThis.context,
					this.currentRasterProduct.getLegends());
			((ListView)views[2]).setAdapter(legendAdapter);
			((TextView)views[3]).setText("单位("+this.currentRasterProduct.getUnit()+")");
			this.lstAllOverLay=this.mBaiduMap.addOverlays(this.currentRasterProduct.getOverlayOptionsList());
		
			if(this.listener!= null)
				this.listener.AfterProductShow(this.currentRasterProduct);
		}else{
			Toast.makeText(oThis.context, "产品数据不完整", 500).show();
		}
	}
	
	public void hide(){
		for(View v : views){
			if(v != null) v.setVisibility(View.GONE);
		}
		for(Overlay lay :lstAllOverLay)
		{
			if(lay!=null) lay.remove();
		}
		lstAllOverLay.clear(); 
		this.currentRasterProduct = null;
		if(this.listener != null) this.listener.AfterProductHide();
	}

	public RasterProduct getCurrentRasterProduct() {
		return currentRasterProduct;
	}

	public void setCurrentRasterProduct(RasterProduct currentRasterProduct) {
		this.currentRasterProduct = currentRasterProduct;
	}

	@Override
	public void AfterAreaChanged(Area area) {
		// TODO Auto-generated method stub
		 if(oThis.getCurrentRasterProduct() != null){
			 String t = oThis.getCurrentRasterProduct().getPtype();
			 String n = oThis.getCurrentRasterProduct().getPname();
			 oThis.reset();
			 oThis.load(t,n,area.enname);
		 }	
		 
	}

	
}
