package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.common.views.MeteoMapProductDetailWindow;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.MarkerExtraInfo;
import com.supermap.pisaclient.entity.ProfProduct;
import com.supermap.pisaclient.ui.ProductActivity;


public class ProductLayer {
	
	private ProductLayer oThis= this;
	private Context context;
	private BaiduMap mBaiduMap;
	private List<ProductPoint> productPoints;
	private View menuView;
	private MapView mMapView;
	private boolean selected = false;
	private List<Overlay> overLays;
	private MeteoMapProductDetailWindow detailWindow;
	private BitmapDescriptor profProductPoint=BitmapDescriptorFactory.fromResource(R.drawable.remind);
	
	public enum PTYPE{
		MODULE, //模型产品
		EXPERT //专家产品
	}
	
	
	public ProductLayer(Context context,BaiduMap mBaiduMap,MapView mMapView,View menuView){
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		this.mMapView = mMapView;
		this.menuView = menuView;

		this.overLays = new ArrayList<Overlay>();
		this.menuView.setOnClickListener(listenr);
		this.menuView.setVisibility(View.GONE);
		this.mBaiduMap.setOnMarkerClickListener(productPoinkClickListener);
		this.detailWindow = new MeteoMapProductDetailWindow(context);
		
	}
	
	OnMarkerClickListener productPoinkClickListener=new OnMarkerClickListener()
	{
		@Override
		public boolean onMarkerClick(Marker marker) 
		{
			
			if(marker.getExtraInfo()==null)
			{
				return false;
			}
			else
			{
				Bundle bundle=marker.getExtraInfo();
				if(bundle.containsKey("detail")){
					ProductPoint product = (ProductPoint)bundle.getSerializable("detail");
					detailWindow.setVlaues(product);
					detailWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				return true;
			}
		}
	};
	
	
	OnClickListener listenr = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!oThis.selected){
					/*if(oThis.productPoints == null){
						
					}else{
						oThis.show();
					}*/
				oThis.productPoints = new ArrayList<ProductPoint>();
				Calendar c = Calendar.getInstance();
				String time = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DATE);
				//String time = "2016-10-15";
				String starttime = time+" 00:00:00";
				String endtime = time +" 23:59:59";
				//new ProfProductTask(starttime, endtime, "50", 189).execute();
				new VipProductPointTask(starttime,endtime).execute();
			}else{
				oThis.hide();
			}
			int backGroundId = oThis.selected ? R.drawable.meteomap_menubg_normal : R.drawable.meteomap_menubg_on;
			int fontColorId = oThis.selected ? R.color.black : R.color.white;
			oThis.selected = !oThis.selected;
			v.setBackgroundResource(backGroundId);
			((Button)v).setTextColor(fontColorId);
		}
		
	};
	

	public void show(){
		for(ProductPoint product : oThis.getProductPoints()){
			//定义Maker坐标点  
	    	LatLng point = new LatLng(product.lat,product.lon);  
	    	int iconSource = 0;
	    	if(product.getType() == 1){
	    		iconSource = R.drawable.product_pop_1;
	    	}else if(product.getType() == 2){
	    		iconSource  = R.drawable.product_pop_2;
	    	}
	    	//构建Marker图标  
	    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
	    	    .fromResource(iconSource);  
	    	//构建MarkerOption，用于在地图上添加Marker  
	    	OverlayOptions option = new MarkerOptions()  
	    	    .position(point)  
	    	    .icon(bitmap);  
	    	Marker marker = (Marker) mBaiduMap.addOverlay(option);
	    	
	    	//在地图上添加Marker，并显示  
	    	Bundle bundle = new Bundle();
	        bundle.putSerializable("detail", product);
	        marker.setExtraInfo(bundle);
	        this.overLays.add(marker);
		}
	}
	
	public void hide(){
		for(Overlay lay : this.overLays){
			lay.remove();
		}
		
	}
	
	
	private class VipProductPointTask extends AsyncTask<Integer,Integer,List<ProductPoint>>
	{

		private String starttime;
		private String endtime;
		public VipProductPointTask(String starttime,String endtime){
			this.starttime = starttime;
			this.endtime = endtime;
		
		}
		@Override
		protected List<ProductPoint> doInBackground(Integer... params) 
		{
			Area a = PlaceManager.getCurrentArea();
			if(a != null){
				return OneMapDao.getProductPointByTimeSpan(starttime,endtime,0,a.areacode);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<ProductPoint> result) 
		{
			productPoints = result;
			oThis.show();
			Toast.makeText(context, "服务产品加载成功", Toast.LENGTH_SHORT).show();
			
		}
	}
	
	
	private class ProfProductTask extends AsyncTask<Integer,Integer,List<ProductPoint>>
	{
		private String areacode;
		private String starttime;
		private String endtime;
		private int crop;
		
		public ProfProductTask(String starttime,String endtime,String areacode,int crop){
			this.areacode = areacode;
			this.starttime = starttime;
			this.endtime = endtime;
			this.crop = crop;
		}
		
		
		@Override
		protected List<ProductPoint> doInBackground(Integer... params) 
		{
			return OneMapDao.getProfProducts(starttime,endtime,areacode,crop);
		}
		
		@Override
		protected void onPostExecute(List<ProductPoint> result) 
		{
			productPoints=result;
			oThis.show();
			Toast.makeText(context, "专业产品数据加载完成", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showToast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public List<ProductPoint> getProductPoints() {
		return productPoints;
	}
	
}
