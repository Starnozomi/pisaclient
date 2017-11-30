package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.views.MeteoMapAdvInfoWindow;
import com.supermap.pisaclient.common.views.MeteoMapFarmWindow;
import com.supermap.pisaclient.common.views.MeteoMapVipInfoWindow;
import com.supermap.pisaclient.common.views.OneMapPupupWindow;
import com.supermap.pisaclient.entity.AdvInfo;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmPoint;
import com.supermap.pisaclient.entity.MarkerExtraInfo;
import com.supermap.pisaclient.entity.SituationInfo;
import com.supermap.pisaclient.entity.SituationPoint;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.ui.CropImageActivity;
import com.supermap.pisaclient.ui.FarmActivity;


/********************************
 * 矢量对象管理器
 * 管理底图上所有点数据的加载和事件
 * @author Heyao
 *
 */
public class VectorManager {
	private VectorManager oThis = this;
	private Context context = null;
	private List<VipUser> vipUsers=null;
	private List<FarmPoint> farms=null;
	private List<SituationPoint> situations=null;
	private List<AdvInfo> advInfos=null;

	private List<MarkerItem> vipMarkers=null;
	private List<MarkerItem> farmMarkers=null;
	private List<MarkerItem> situationMarkers=null;
	private List<MarkerItem> advInfoMarkers = null;
	private ClusterManager<MarkerItem> mClusterManager = null;
	
	public boolean vFarmLandCheckState = false;
	public boolean vVipCheckState = false;
	public boolean vArgInfoCheckState = false;
	public boolean vAdvisoryCheckState = false;
	public boolean vProductCheckState = false;
	private MapView mMapView = null;//地图
	//初始化大户的图标点
	BitmapDescriptor vipPoint = BitmapDescriptorFactory.fromResource(R.drawable.ico_vip_user);
	//初始化农田的图标点
	BitmapDescriptor farm = BitmapDescriptorFactory.fromResource(R.drawable.icon_farm);
	//初始化农情的图标点
	BitmapDescriptor situation = BitmapDescriptorFactory.fromResource(R.drawable.ico_nq);
	
	BitmapDescriptor advinfo=BitmapDescriptorFactory.fromResource(R.drawable.question_small);
	BaiduMap mBaiduMap = null;
	private InfoWindow mInfoWindow=null;
	

	private MapService mapService;
	private List<Overlay> overLays; 
	private FarmDao mFarmDao;
	private View[] relateViews;
	private View currentView;
	public VectorManager(Context context, BaiduMap mBaiduMap, MapView mMapView, MapService mapService, final View [] relateViews){
			this.context = context;
			this.mBaiduMap = mBaiduMap;
			this.mMapView = mMapView;
			this.mapService = mapService;
			this.mFarmDao = new FarmDao();
			 //初始化聚合管理器
	        this.mClusterManager = new ClusterManager<MarkerItem>(context, mBaiduMap);
	        this.overLays = new ArrayList<Overlay>();
	     // 设置地图监听，当地图状态发生改变时，进行点聚合运算
	        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
	        //设置Marker的点击事件
	        mBaiduMap.setOnMarkerClickListener(markerClickListener);
	        mBaiduMap.setMapType(mBaiduMap.MAP_TYPE_NORMAL);
	        
	        vipMarkers = new ArrayList<MarkerItem>();
	        farmMarkers = new ArrayList<MarkerItem>();
	        situationMarkers = new ArrayList<MarkerItem>();
	        advInfoMarkers = new ArrayList<MarkerItem>();
	        this.relateViews = relateViews;
	       // loadVectors();
	        for(View v :relateViews){
	        	v.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						if(currentView != null && currentView.getId() == arg0.getId()){
							arg0.setBackgroundResource(R.drawable.meteomap_menubg_normal);
							currentView = null;
							oThis.reClustData();
						} else {
							for(View vv : relateViews) {
								vv.setBackgroundResource(R.drawable.meteomap_menubg_normal);
							}
			  				arg0.setBackgroundResource(R.drawable.meteomap_menubg_on);
							currentView = arg0;
							oThis.reClustData();
						}
					}
	        	});
	        }
	}
	
	
	public void loadVipAdvs(){
		
	}
	
	
	private View vipFarmRelateView; //右边打开大户农田列表
	private List<Farm> myFarms = new ArrayList<Farm>();
	private Farm selectFarm = null;
	public void loadVipFarm(final View vipFarmRelateView){
		    this.vipFarmRelateView =  vipFarmRelateView;
		    vipFarmRelateView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//loadFarm(oThis.vipFarmRelateView,2);
				if(myFarms.size() == 0) return;
				String [] farmNameArray = new String[myFarms.size()];
				Integer [] farmIdArray = new Integer[myFarms.size()];
				for(int i=0;i<myFarms.size();i++){
					farmIdArray[i] = myFarms.get(i).id;
					farmNameArray[i] = myFarms.get(i).descript;
				}
				Dialog alertDialog = new AlertDialog.Builder(context).   
		                setTitle("选择您的农田？").   
		                setIcon(R.drawable.loc_farm_small)   
		                .setItems(farmNameArray, new DialogInterface.OnClickListener() {   
		                    @Override   
		                    public void onClick(DialogInterface dialog, int which) {   
		                        selectFarm = myFarms.get(which);
		                        double lat = Double.parseDouble(selectFarm.latitude);
		     	    		    double lon = Double.parseDouble(selectFarm.longitude);
		                        if(selectFarm != null)  loadVipFarm(vipFarmRelateView);
		                        oThis.locateFarmOther(selectFarm.id, Double.parseDouble(selectFarm.longitude), Double.parseDouble(selectFarm.latitude), 1);
		     	    		    mapService.setCenter(lat, lon, 13f);
		                        
		                    }   
		                }).   
		                setNegativeButton("取消", new DialogInterface.OnClickListener() {   
		                    @Override   
		                    public void onClick(DialogInterface dialog, int which) {   
		                        // TODO Auto-generated method stub    
		                    }   
		                }).   
		                create();   
		        		alertDialog.show(); 
			}
			
		});
		
		
		for(Overlay ol : oThis.overLays){
    		ol.remove();
    	}
		User u = UserDao.getInstance().get();
        if(u != null && u.isSave){
        	new UserFarmTask(u.id).execute();
        } else {
          //用户农田判定为空
          Area a = PlaceManager.getCurrentArea();
          if(a != null){
        	  mapService.setCenter(a.centerlat,a.centerlon, a.maplevel);
         	  vipFarmRelateView.setVisibility(View.GONE);
          }
        }
	}
	
	
	

	private class UserFarmTask extends AsyncTask<Integer,Integer,List<Farm>> {
		int userid;
		public UserFarmTask(int userid) {
			this.userid = userid;
		}
		@Override
		protected List<Farm> doInBackground(Integer... params) {
			return mFarmDao.getFarmByUserId(userid);
		}
		
		@Override
		protected void onPostExecute(List<Farm> farms) {
			   myFarms = farms;	
	    	   if(farms.size() > 0) { 
	    		   if(selectFarm == null) {
    				   selectFarm = myFarms.get(0);
    			   }		   
	    		   for(Farm f : myFarms) {
	    			   if(f.id == selectFarm.id) continue;
	    			   oThis.locateFarm(f.id, Double.parseDouble(f.longitude), Double.parseDouble(f.latitude),2);
	    		   }
	    		   
	    		   double lat = Double.parseDouble(selectFarm.latitude);
	    		   double lon = Double.parseDouble(selectFarm.longitude);
	    		   oThis.locateFarm(selectFarm.id, Double.parseDouble(selectFarm.longitude), Double.parseDouble(selectFarm.latitude), 1);
	    		  // mapService.setCenter(lat, lon, 13f);
	    		   
	    		   vipFarmRelateView.setVisibility(View.VISIBLE);
	    	   }else{
	    		   for(Overlay ol : oThis.overLays){
	           			ol.remove();
	    		   }
	    		   vipFarmRelateView.setVisibility(View.GONE);
	    		   Area a =PlaceManager.getCurrentArea();
	    		   if(a != null){
	    			   mapService.setCenter(a.centerlat,a.centerlon, a.maplevel);
	    		   }
	    		   
	    	   }
		}
	}
	
	/*******************************************
	 * 定位农田
	 * @param farmid
	 * @param lon
	 * @param lat
	 */
	public void locateFarm(int farmid,double lon,double lat,int type){
    	LatLng point = new LatLng(lat,lon);  
    	Farm mFarm =new Farm();
    	int iconSource = 0;
    	if(type == 1) {
    		iconSource = R.drawable.loc_farm;
    	}else if(type== 2) {
    		iconSource  = R.drawable.loc_farm_small;
    	}
    	//构建Marker图标  
    	BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconSource);  
    	//构建MarkerOption，用于在地图上添加Marker  
    	OverlayOptions option = new MarkerOptions()  
    	    .position(point)  
    	    .icon(bitmap);  
    	Marker marker = (Marker) oThis.mapService.getmBaiduMap().addOverlay(option);
    	
    	//在地图上添加Marker，并显示
    	Bundle bd = new Bundle();
    	bd.putInt("farmid", farmid);
        marker.setExtraInfo(bd);
        oThis.overLays.add(marker);
    	//mapService.addOverlay(Double.parseDouble(lat),Double.parseDouble(lon),R.drawable.local_up);
    	//mapService.setCenter(lat,lon, 13);
	}
	
	/*******************************************
	 * 定位并显示农田信息
	 * @param farmid
	 * @param lon
	 * @param lat
	 */
	public void locateFarmOther(int farmid,double lon,double lat,int type){
    	LatLng point = new LatLng(lat,lon);  
    	Farm mFarm =new Farm();
    	int iconSource = 0;
    	if(type == 1) {
    		iconSource = R.drawable.loc_farm;
    	}else if(type== 2) {
    		iconSource  = R.drawable.loc_farm_small;
    	}
    	//构建Marker图标  
    	BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconSource);  
    	//构建MarkerOption，用于在地图上添加Marker  
    	OverlayOptions option = new MarkerOptions()  
    	    .position(point)  
    	    .icon(bitmap);  
    	Marker marker = (Marker) oThis.mapService.getmBaiduMap().addOverlay(option);
    	
    	//在地图上添加Marker，并显示
    	Bundle bd = new Bundle();
    	bd.putInt("farmid", farmid);
        marker.setExtraInfo(bd);
        oThis.overLays.add(marker);
        Bundle bundle=marker.getExtraInfo();
		if(bundle.containsKey("info")){
			MarkerExtraInfo info= (MarkerExtraInfo) bundle.getSerializable("info");
			if(info.getType() == 2){
				Farm f = new Farm();
				f.id = info.getId();
				MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, f);
				if(!farmWindow.isShowing()) {//已经显示的情况下就不再显示
					farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			}else if(info.getType() == 4) {
				MeteoMapAdvInfoWindow advInfoWindow= new MeteoMapAdvInfoWindow((Activity)oThis.context,info.getId());
				if(!advInfoWindow.isShowing()) {//已经显示的情况下就不再显示
					advInfoWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}
			} else {
				new PointInfoTask(info.getId(),info.getType(),marker.getPosition()).execute();
			}
			
			
		} else if(bundle.containsKey("farmid")) {
			Farm info = new Farm();
			info.id = bundle.getInt("farmid");
			MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, info);
			if(!farmWindow.isShowing()) {//已经显示的情况下就不再显示
				farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		}
    	//mapService.addOverlay(Double.parseDouble(lat),Double.parseDouble(lon),R.drawable.local_up);
    	//mapService.setCenter(lat,lon, 13);
	}
	                                                                                          
	
	public void loadVectors(){
		if(currentView == null) return;
		switch(currentView.getId()){
			case R.id.vFarmland:
				//执行农田数据获取线程
				if(PlaceManager.getCurrentArea() != null)
				new FarmTask(PlaceManager.getCurrentArea().areacode).execute();
				break;
			case R.id.vVip:
				//执行VIP大户数据获取线程
				if(PlaceManager.getCurrentArea() != null)
				new VipUserTask(PlaceManager.getCurrentArea().areacode).execute();
				break;
			case R.id.vArgInfo:
				//执行农情数据获取线程
				if(PlaceManager.getCurrentArea() != null)
				new SituationTask(PlaceManager.getCurrentArea().areacode).execute();
				break;
			case R.id.vAdvisory:
				if(PlaceManager.getCurrentArea() != null)
				new AdvInfoTask(PlaceManager.getCurrentArea().areacode).execute();
				break;
		}
		
	}
	
	OnMarkerClickListener markerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			if(marker.getExtraInfo()==null) {
				return false;
			} else {
				Bundle bundle=marker.getExtraInfo();
				if(bundle.containsKey("info")){
					MarkerExtraInfo info= (MarkerExtraInfo) bundle.getSerializable("info");
					if(info.getType() == 2){
						Farm f = new Farm();
						f.id = info.getId();
						MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, f);
						if(!farmWindow.isShowing()) {//已经显示的情况下就不再显示
							farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
						}
					}else if(info.getType() == 4) {
						MeteoMapAdvInfoWindow advInfoWindow= new MeteoMapAdvInfoWindow((Activity)oThis.context,info.getId());
						if(!advInfoWindow.isShowing()) {//已经显示的情况下就不再显示
							advInfoWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
						}
					} else {
						new PointInfoTask(info.getId(),info.getType(),marker.getPosition()).execute();
					}
					
					
				} else if(bundle.containsKey("farmid")) {
					Farm info = new Farm();
					info.id = bundle.getInt("farmid");
					MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, info);
					if(!farmWindow.isShowing()) {//已经显示的情况下就不再显示
						farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
					}
				}
				return true;
			}
		}
	};

	/**
	 * 获取点的详细信息
	 * @author TanRq
	 *
	 */
	private class PointInfoTask extends AsyncTask<Integer,Integer,Object>
	{
		private int id;//点的id,可以唯一标识该点
		private int type;//点的类型
		private LatLng position;//点的位置
		
		public PointInfoTask(int id,int type,LatLng position) {
			this.id=id;
			this.type=type;
			this.position=position;
		}

		@Override
		protected Object doInBackground(Integer... params) 
		{
			Object result=null;
			
			switch(type)
			{
				case 1:
					result=OneMapDao.getVipUserInfo(id);
					break;
				case 2:
					result=OneMapDao.getFarmInfo(id);
					break;
				case 3:
					result=OneMapDao.getSituationInfo(id);
					break;
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Object result) 
		{
			switch(type)
			{
				case 1:
					VipUserInfo vipUserInfo=(VipUserInfo) result;
					showVipUserInfo(vipUserInfo,position);
					break;
				case 2:
					FarmInfo farmInfo=(FarmInfo) result;
					showFarmInfo(farmInfo,position);
					break;
				case 3:
					SituationInfo situationInfo=(SituationInfo) result;
					showSituationInfo(situationInfo,position);
					break;
				case 4:
					AdvInfo advInfo = (AdvInfo)result;
					showAdvInfo(advInfo,position);
					break;
				
			}
		}
	}
	
	private void showAdvInfo(AdvInfo info,LatLng position)
	{
		Intent intent  = new Intent();
		intent.setClass(oThis.context, FarmActivity.class);
		oThis.context.startActivity(intent);
		Farm f = new Farm();
		
		/*MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, info);
		if(!farmWindow.isShowing())//已经显示的情况下就不再显示
		{
			farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		}*/
	}
	
	private void showVipUserInfo(VipUserInfo info,LatLng position)
	{
		/*View view=LayoutInflater.from(context).inflate(R.layout.vip_user_infowindow, null);
		
		TextView txtViewName= (TextView) view.findViewById(R.id.textViewName);
		txtViewName.setText(info.getLeaderName());
		TextView textViewTel=(TextView) view.findViewById(R.id.textViewTel);
		//textViewTel.setText(info.getLeaderPhone());
		TextView textViewAddr=(TextView) view.findViewById(R.id.textViewAddr);
		textViewAddr.setText(info.getCountyName()+info.getBelongsTownship()+info.getBelongsVillage());
		TextView textViewIntroduce=(TextView) view.findViewById(R.id.textViewIntroduce);
		textViewIntroduce.setText(info.getBusinessScope());
		
		ImageButton imgBtn=(ImageButton) view.findViewById(R.id.imgBtnClose);
		imgBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				 oThis.mBaiduMap.hideInfoWindow();
			}
		});
		
		mInfoWindow = new InfoWindow(view, position, 0);
		mBaiduMap.showInfoWindow(mInfoWindow);*/
		MeteoMapVipInfoWindow vipInfoWindow=new MeteoMapVipInfoWindow((Activity)oThis.context, info);
		if(vipInfoWindow.isShowing())//已经显示的情况下就不再显示
			return;
		vipInfoWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	private void showFarmInfo(FarmInfo info,LatLng position)
	{
		Intent intent  = new Intent();
		intent.setClass(oThis.context, FarmActivity.class);
		oThis.context.startActivity(intent);
		
		Farm f = new Farm();
		
		/*MeteoMapFarmWindow farmWindow=new MeteoMapFarmWindow((Activity)oThis.context, info);
		if(!farmWindow.isShowing())//已经显示的情况下就不再显示
		{
			farmWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		}*/
	}
	
	private void showSituationInfo(SituationInfo info,LatLng position)
	{
		View view=LayoutInflater.from(context).inflate(R.layout.situation_infowindow, null);
		TextView textViewCrop=(TextView) view.findViewById(R.id.textViewCrop);
		textViewCrop.setText(info.getCropName());
		TextView textViewPeriod=(TextView) view.findViewById(R.id.textViewPeriod);
		textViewPeriod.setText(info.getPubertyName());
		TextView textViewPublishName=(TextView) view.findViewById(R.id.textViewPublishName);
		textViewPublishName.setText(info.getPublishName());
		TextView textViewPublishTime=(TextView) view.findViewById(R.id.textViewPublishTime);
		textViewPublishTime.setText(info.getUploadTime());
		
		List<ImageView> images=new ArrayList<ImageView>();
		images.add((ImageView) view.findViewById(R.id.imageView1));
		images.add((ImageView) view.findViewById(R.id.imageView2));
		images.add((ImageView) view.findViewById(R.id.imageView3));
		
		ImageLoader mImageLoader=new ImageLoader(context);
		for(int i=0;i<info.getImages().size();i++)
		{
			if(i<3)
			{
				mImageLoader.DisplayImage(info.getImages().get(i),images.get(i) , false);
			}
		}
		
		ImageButton imgBtn=(ImageButton) view.findViewById(R.id.imgBtnClose);
		imgBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				 mBaiduMap.hideInfoWindow();
			}
		});
		//如果存在农情图片，则设置图片的点击事件
		if(info.getImages().size()>0)
		{
			images.get(0).setOnClickListener(new SituationImageOnClickListener(info.getImages()));
			images.get(1).setOnClickListener(new SituationImageOnClickListener(info.getImages()));
			images.get(2).setOnClickListener(new SituationImageOnClickListener(info.getImages()));
		}
		
		mInfoWindow = new InfoWindow(view, position, 0);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	private class SituationImageOnClickListener implements OnClickListener
	{
		private List<String> imgUrls;
		
		public SituationImageOnClickListener(List<String> imgUrls)
		{
			this.imgUrls=imgUrls;
		}
		
		@Override
		public void onClick(View v) 
		{
			int position=0;
			
			switch(v.getId())
			{
				case R.id.imageView1:
					position=0;	
					break;
				case R.id.imageView2:
					position=1;	
					break;
				case R.id.imageView3:
					position=2;
					break;
			}
			showBigImage(imgUrls,position);
		}
	}
	
	private void showBigImage(List<String> imgUrls,int position)
	{
		//position比imgUrls.size()-1要大，说明点击的不是有图片的那个ImageView,需要对position重置值
		if(imgUrls.size()-1<position)
		{
			position=0;
		}
		String[] images=new String[imgUrls.size()];
		Intent intent = new Intent();
		intent.putExtra("image_position", position);
		intent.putExtra("image_deses", imgUrls.toArray(images));
		intent.setClass(context, CropImageActivity.class);
		context.startActivity(intent);
	}
	
	private class AdvInfoTask extends AsyncTask<Integer,Integer,List<AdvInfo>>
	{
		private String areaCode;
		public AdvInfoTask(String areaCode){
			this.areaCode = areaCode;
		}
		@Override
		protected List<AdvInfo> doInBackground(Integer... params) 
		{
			return OneMapDao.getAdvInfos(areaCode);
		}
		
		@Override
		protected void onPostExecute(List<AdvInfo> result) 
		{
			advInfos=result;
			new LoadAdvInfoThread().start();
			Toast.makeText(context, "加载咨询数据完成", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private class VipUserTask extends AsyncTask<Integer,Integer,List<VipUser>>
	{
		private String areaCode;
		public VipUserTask(String areaCode){
			this.areaCode = areaCode;
		}
		@Override
		protected List<VipUser> doInBackground(Integer... params) 
		{
			return OneMapDao.getVipUser(areaCode);
		}
		
		@Override
		protected void onPostExecute(List<VipUser> result) 
		{
			vipUsers=result;
			new LoadVipUserThread().start();
			Toast.makeText(context, "大户数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 加载农田数据
	 * @author TanRq
	 *
	 */
	private class FarmTask extends AsyncTask<Integer,Integer,List<FarmPoint>>
	{
		private String areaCode;
		public FarmTask(String areaCode){
			this.areaCode = areaCode;
		}
		@Override
		protected List<FarmPoint> doInBackground(Integer... params) 
		{
			return OneMapDao.getFarmPoint(areaCode);
		}
		
		@Override
		protected void onPostExecute(List<FarmPoint> result) 
		{
			farms=result;
			new LoadFarmThread().start();
			Toast.makeText(context, "农田数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 加载农情数据
	 * @author TanRq
	 *
	 */
	private class SituationTask extends AsyncTask<Integer,Integer,List<SituationPoint>>
	{
		private String areaCode;
		public SituationTask(String areaCode){
			this.areaCode = areaCode;
		}
		@Override
		protected List<SituationPoint> doInBackground(Integer... params) 
		{
			return OneMapDao.getSituationPoint(areaCode);
		}
		
		@Override
		protected void onPostExecute(List<SituationPoint> result) 
		{
			situations=result;
			new LoadSituationThread().start();
			Toast.makeText(context, "农情数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}
	
	class LoadAdvInfoThread extends Thread
	{
		@Override
		public void run() 
		{
			if(oThis.getAdvInfos()!=null)
			{
				loadAdvInfoLayer(advInfos);
			}
			else
			{
				Message msg=new Message();
				msg.arg1=1;
				toastHandler.sendMessage(msg);
			}
		}
	};
	
	class LoadVipUserThread extends Thread
	{
		@Override
		public void run() 
		{
			if(oThis.getVipUsers()!=null)
			{
				loadVipUserLayer(vipUsers);
			}
			else
			{
				Message msg=new Message();
				msg.arg1=1;
				toastHandler.sendMessage(msg);
			}
		}
	};
	

	class LoadFarmThread extends Thread
	{
		@Override
		public void run() 
		{
			if(oThis.getFarms()!=null)
			{
				loadFarmLayer(farms);
			}
			else
			{
				Message msg=new Message();
				msg.arg1=2;
				toastHandler.sendMessage(msg);
			}
		}
	}
	
	class LoadSituationThread extends Thread
	{
		@Override
		public void run() 
		{
			if(oThis.getSituations()!=null)
			{
				loadSituationLayer(situations);
			}
			else
			{
				Message msg=new Message();
				msg.arg1=3;
				toastHandler.sendMessage(msg);
			}
		}
	}
	
	
	private void loadAdvInfoLayer(List<AdvInfo> advInfos)
	{
		advInfoMarkers=new ArrayList<MarkerItem>();
		MarkerExtraInfo info=null;
		Bundle bundle=null;
		for(AdvInfo adv:oThis.getAdvInfos())
		{
			info=new MarkerExtraInfo();
			info.setId(adv.getId());
			info.setType(4);
			bundle=new Bundle();
			bundle.putSerializable("info", info);
			advInfoMarkers.add(new MarkerItem(new LatLng(adv.getLatitude(),adv.getLongitude())).setBitmapDescriptor(advinfo).setExtraInfo(bundle));
		}
		oThis.mClusterManager.addItems(advInfoMarkers);
		oThis.mClusterManager.cluster();
	}
	
	/**
	 * 加载大户图层
	 * @param vips
	 */
	private void loadVipUserLayer(List<VipUser> vips)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		/*if(vipMarkers!=null&&vipMarkers.size()>0)
		{
			return;
		}*/
		/*else if(vVipCheckState)
		{
			
		}*/
		vipMarkers=new ArrayList<MarkerItem>();
		MarkerExtraInfo info=null;
		Bundle bundle=null;
		for(VipUser user:oThis.getVipUsers())
		{
			info=new MarkerExtraInfo();
			info.setId(user.getUserid());
			info.setType(1);
			bundle=new Bundle();
			bundle.putSerializable("info", info);
			vipMarkers.add(new MarkerItem(new LatLng(user.getLatitude(),user.getLongitude())).setBitmapDescriptor(vipPoint).setExtraInfo(bundle));
		}
		oThis.mClusterManager.addItems(vipMarkers);
		oThis.mClusterManager.cluster();
	}
	
	private void loadFarmLayer(List<FarmPoint> farms)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		/*if(farmMarkers!=null&&farmMarkers.size()>0)
		{
			return;
		}*/
		/*else if(vFarmLandCheckState)
		{
			
		}*/
		farmMarkers=new ArrayList<MarkerItem>();
		MarkerExtraInfo info=null;
		Bundle bundle=null;
		for(FarmPoint farm:oThis.getFarms())
		{
			info=new MarkerExtraInfo();
			info.setId(farm.getId());
			info.setType(2);
			bundle=new Bundle();
			bundle.putSerializable("info", info);
			farmMarkers.add(new MarkerItem(new LatLng(farm.getLatitude(),farm.getLongitude())).setBitmapDescriptor(this.farm).setExtraInfo(bundle));
		}
		mClusterManager.addItems(farmMarkers);
		mClusterManager.cluster();
	}
	
	/**************
	 * 加载站点数据
	 * @param situations
	 */
	private void loadSituationLayer(List<SituationPoint> situations)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		/*if(situationMarkers!=null&&situationMarkers.size()>0)
		{
			return;
		}*/
		/*else if(vArgInfoCheckState)
		{
			
		}*/
		situationMarkers = new ArrayList<MarkerItem>();
		MarkerExtraInfo info=null;
		Bundle bundle=null;
		for(SituationPoint situation:oThis.getSituations())
		{
			info=new MarkerExtraInfo();
			info.setId(situation.getId());
			info.setType(3);
			bundle=new Bundle();
			bundle.putSerializable("info", info);
			situationMarkers.add(new MarkerItem(new LatLng(situation.getLatitude(),situation.getLongitude())).setBitmapDescriptor(this.situation).setExtraInfo(bundle));
		}
		mClusterManager.addItems(situationMarkers);
		mClusterManager.cluster();
	}
	
	
	/**
	 * 处理Toast消息
	 */
	@SuppressLint("HandlerLeak")
	private Handler toastHandler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.arg1)
			{
				case 1:
					showToast("大户数据正在加载，请稍后");
					break;
				case 2:
					showToast("农田数据正在加载，请稍后");
					break;
				case 3:
					showToast("农情数据正在加载，请稍后");
					break;
				case 4:
					showToast("区县数据整在加载，请稍后");
					break;
				case 5:
					showToast("实景监控数据整在加载，请稍后");
					break;
				case 6:
					showToast("色斑图数据整在加载，请稍后");
					break;
				default:
					break;
			}
		}
	};
	
	public void reClustData(){
		mClusterManager.clearItems();
		vipMarkers.clear();
		farmMarkers.clear();
		situationMarkers.clear();
		advInfoMarkers.clear();
		/*vipMarkers = new ArrayList<MarkerItem>();
		farmMarkers = new  ArrayList<MarkerItem>();
		situationMarkers = new ArrayList<MarkerItem>();*/
		vipUsers = new ArrayList<VipUser>();
		farms=new ArrayList<FarmPoint>();
		situations=new ArrayList<SituationPoint>();
		advInfos = new  ArrayList<AdvInfo>();
		loadVectors();
		//new LoadVipUserThread().start();
		//new LoadFarmThread().start();
		//new LoadSituationThread().start();
	}
	
	
	
	private void showToast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public List<VipUser> getVipUsers() {
		return vipUsers;
	}


	public List<FarmPoint> getFarms() {
		return farms;
	}


	public List<SituationPoint> getSituations() {
		return situations;
	}


	public List<Farm> getMyFarms() {
		return myFarms;
	}


	public List<AdvInfo> getAdvInfos() {
		return advInfos;
	}
	
	
	
	
	
	
	
	
	
}
