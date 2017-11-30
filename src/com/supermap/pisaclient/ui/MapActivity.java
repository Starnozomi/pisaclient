package com.supermap.pisaclient.ui;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MapActivity extends BaseActivity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	private Marker mMarker;
	
	private double currentLongitude;
	private double currentLatitude;
	
	private View mContent;
	private RadioGroup radioGroup;
	private Button btnOK;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SDKInitializer.initialize(getApplicationContext());
		setIsNavigator(false);
		setIsBack(true);
		setTvTitle(Utils.getString(this, R.string.map_point_info));
		
		mContent = inflater(R.layout.map_point);
		mMapView = (MapView) mContent.findViewById(R.id.bmapView);		
		btnOK=(Button) mContent.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(btnOnClickListener);
		mBaiduMap = mMapView.getMap();
		
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		
		initOverlay();
		
		mBaiduMap.setOnMarkerDragListener(onMarkerDragListener);
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
		
		// 开启定位图层
		//mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
//		mLocClient = new LocationClient(this);
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(1000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();
		
		radioGroup=(RadioGroup) mContent.findViewById(R.id.RadioGroup);	
		radioGroup.setOnCheckedChangeListener(listener);
	}
	
	public void initOverlay()
	{
		double [] lonlat= this.getIntent().getDoubleArrayExtra("lonlat");
		double longitude=lonlat[0];
		double latitude=lonlat[1];
		currentLongitude=longitude;
		currentLatitude=latitude;
		
		LatLng markerPoint=new LatLng(latitude,longitude);
		MarkerOptions ooA = new MarkerOptions().position(markerPoint).icon(BitmapDescriptorFactory
    			.fromResource(R.drawable.icon_current_location)).draggable(true);//设置位置，图标，可拖动性
		ooA.animateType(MarkerAnimateType.drop);//掉下动画
		 mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
		 MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(markerPoint);
         mBaiduMap.animateMapStatus(u);
	}
	
	OnClickListener btnOnClickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			closeActivity();
		}	
	};
	
	OnMarkerClickListener onMarkerClickListener=new OnMarkerClickListener()
	{
		@Override
		public boolean onMarkerClick(Marker marker)
		{
			LatLng ll = marker.getPosition();
			Toast.makeText(
					MapActivity.this,
					"当前位置：" + getNewValue(marker.getPosition().longitude) + ", "
							+getNewValue(marker.getPosition().latitude) ,
					Toast.LENGTH_SHORT).show();
			return true;
		}	
	};
	
	BaiduMap.OnMarkerDragListener onMarkerDragListener=new BaiduMap.OnMarkerDragListener()
	{
		@Override
		public void onMarkerDrag(Marker marker) 
		{
			
		}

		@Override
		public void onMarkerDragEnd(Marker marker) 
		{
			currentLongitude=marker.getPosition().longitude;
			currentLatitude=marker.getPosition().latitude;
			
			Toast.makeText(
					MapActivity.this,
					"拖拽结束，新位置：" +getNewValue(marker.getPosition().longitude)  + ", "
							+getNewValue(marker.getPosition().latitude) ,
					Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onMarkerDragStart(Marker marker) 
		{
			
		}	
	};
	
	OnCheckedChangeListener listener=new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) 
		{
			if(checkedId==R.id.normal)
			{
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			}
			else
			{
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			}
		}		
	};

	private double getNewValue(double value)
	{
		long tempValue=  Math.round(value*10000);   //四舍五入
		double mValue=tempValue/10000.0;
		return mValue;
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                            // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
            currentLongitude=location.getLongitude();
			currentLatitude=location.getLatitude();
            MarkerOptions ooA = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory
        			.fromResource(R.drawable.icon_current_location))
    				.zIndex(9).draggable(true);
            
            mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
                
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            
            mLocClient.stop();//只需定位一次即可，防止多次定位
        }

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	private void closeActivity()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		double [] lnglat={currentLongitude,currentLatitude};
		bundle.putDoubleArray("LngLat", lnglat);	
		intent.putExtras(bundle);
		setResult(Constant.MAP_POINT_RESULT, intent);
		finish();
	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
	
}
