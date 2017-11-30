package com.supermap.pisaclient.ui;

import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.FileTileProvider;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.ExpertProductListAdapter;
import com.supermap.pisaclient.adapter.LegendAdapter;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.ReadBorldpolygonData;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.OneMapInfoWindow;
import com.supermap.pisaclient.common.views.OneMapPupupWindow;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmPoint;
import com.supermap.pisaclient.entity.Legend;
import com.supermap.pisaclient.entity.LivePoint;
import com.supermap.pisaclient.entity.MarkerExtraInfo;
import com.supermap.pisaclient.entity.SituationInfo;
import com.supermap.pisaclient.entity.SituationPoint;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.http.JsonHelper;
import com.supermap.pisaclient.http.JsonHelper.RenderModle;
import com.supermap.pisaclient.service.PlaceManager;

import Decoder.BASE64Encoder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OneMapActivity extends BaseActivity
{
	
	private OneMapActivity oThis = this;
	private View mContent=null;
	private ListView listview =null;
	private LinearLayout legend = null;
	private LegendAdapter legendAdapter = null;
	private TextView tvTitle = null;
	private MapView mMapView = null;
	private ImageView imgView = null;
	private ImageView imgViewPrePic =null;
	private ImageView imgViewNextPic =null;
	private BaiduMap mBaiduMap=null;
	private InfoWindow mInfoWindow=null;
	
	private Animation rotateAnim=null;
	
	private boolean[] isLayerSelected=null;
	
	private List<MarkerItem> vipMarkers=null;
	
	private List<MarkerItem> farmMarkers=null;
	
	private List<MarkerItem> situationMarkers=null;
	

	
	private List<MarkerItem> AreaNamePointMarkers = null;
	
	List<RenderModle>lstAllData =null;
	
	List<Overlay>lstAllOverLay =null;
	
	private List<VipUser> vipUsers=null;
	
	private List<FarmPoint> farms=null;
	
	private List<SituationPoint> situations=null;
	

	//private ClusterManager<VipUserItem> mClusterManager;
	private ClusterManager<MarkerItem> mClusterManager;
	private int iTime =0;
	private OneMapPupupWindow pupup=null;
	
	private OneMapInfoWindow infoWindow = null;
	private String strtime ="";
	private enum etype { 
		 temp(1), water(2),Moisture(3); 
		    private final int val; 
		 
		    private etype(int value) { 
		        val = value; 
		    } 
		 
		    public int getValue() { 
		        return this.val; 
		    } 
		} 
	
	//初始化大户的图标点
	BitmapDescriptor vipPoint=BitmapDescriptorFactory.fromResource(R.drawable.ico_vip_user);
	//初始化农田的图标点
	BitmapDescriptor farm=BitmapDescriptorFactory.fromResource(R.drawable.ico_farm);
	//初始化农情的图标点
	BitmapDescriptor situation=BitmapDescriptorFactory.fromResource(R.drawable.ico_nq);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setIsNavigator(false);
		setIsBack(true);
		//setIsMenu(true);
		//setMenuOnClickListener(OneMapActivity.this);
		setTvTitle(Utils.getString(this, R.string.one_map_info));
		
		mContent = inflater(R.layout.one_map_main);
		mMapView = (MapView) mContent.findViewById(R.id.bmapView);	
		imgView=(ImageView) mContent.findViewById(R.id.imgView);
		imgViewPrePic=(ImageView)mContent.findViewById(R.id.imageButtonPrePic);
		imgViewNextPic=(ImageView)mContent.findViewById(R.id.imageButtonNextPic);
		listview =(ListView) mContent.findViewById(R.id.list_legend);
		legend = (LinearLayout)mContent.findViewById(R.id.ll_legend);
		tvTitle = (TextView)mContent.findViewById(R.id.tv_title);
		//获取旋转图片的动画
		rotateAnim=AnimationUtils.loadAnimation(this, R.anim.rotate_image);
		//设置图层按钮的点击事件
		imgView.setOnClickListener(imgViewClickListener);
		imgViewPrePic.setOnClickListener(imgViewPrePicClickListener);
		imgViewNextPic.setOnClickListener(imgViewNextPicClickListener);
		
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		//106.55394,29.55609
		MapStatusUpdate cqCenter = MapStatusUpdateFactory.newLatLng(new LatLng(29.55609,107.83394));
        mBaiduMap.animateMapStatus(cqCenter);//移动地图到重庆
        MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(8.5f);
        mBaiduMap.animateMapStatus(zoom);//缩放到一定范围
        
        // 定义点聚合管理类ClusterManager
        //mClusterManager = new ClusterManager<VipUserItem>(this, mBaiduMap);
        mClusterManager = new ClusterManager<MarkerItem>(this, mBaiduMap);
        //初始化图层选中
		isLayerSelected=new boolean[]{false,false,false,false,false,false};
		//初始化覆盖图层
		vipMarkers=new ArrayList<MarkerItem>();
		farmMarkers=new ArrayList<MarkerItem>();
		situationMarkers=new ArrayList<MarkerItem>();

		AreaNamePointMarkers =new ArrayList<MarkerItem>();
		lstAllData = new ArrayList<RenderModle>();
		lstAllOverLay = new ArrayList<Overlay>();
		
//		DawBroldData();
		//执行VIP大户数据获取线程
		new VipUserTask().execute();
		//执行农田数据获取线程
		new FarmTask().execute();
		//执行农情数据获取线程
		new SituationTask().execute();
		//获取面数据
		
		// 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        //设置Marker的点击事件
        mBaiduMap.setOnMarkerClickListener(markerClickListener);
        mBaiduMap.setMapType(mBaiduMap.MAP_TYPE_NORMAL);
        new LoadBorderlineThread().start();
        new LoadBorderGonThread().start();
        new LoadAreaNameThread().start();
	}
	//得到重庆市边界线
	private void DrawBroldLineData()
	{
		List<OverlayOptions> lstOptionGon = ReadBorldpolygonData.InitReadPolgonObj().lstOptionLine;
		mBaiduMap.addOverlays(lstOptionGon);
	}
	//得到重庆市边界面
	private void DrawBroldData()
	{
		List<OverlayOptions> lstOptionGon = ReadBorldpolygonData.InitReadPolgonObj().lstOptionGon;
		mBaiduMap.addOverlays(lstOptionGon);
	}
		

	private class LoadProductTask extends AsyncTask<Integer,Integer,Object[]>{

		private List<RenderModle>  renderModleList;
		public LoadProductTask(List<RenderModle>  renderModleList ){
			 this.renderModleList = renderModleList;
		}
		@Override
		protected Object []  doInBackground(Integer... arg0) {
			Object [] objects = new Object[2];
			// TODO Auto-generated method stub
			List<Legend> lstColor = new ArrayList<Legend>();
			String strTile ="";
			if(this.renderModleList.size()>1)
			{
			
			List<OverlayOptions> lstOption = new ArrayList<OverlayOptions>();
			
			for (RenderModle rendr : this.renderModleList) {
				String col = rendr.GetColorCap();
				List<String> lst = rendr.GetValue();
				if(col!=null && col!="")
				{
					Legend leg = new Legend();
					leg.color = rendr.Getkey();
					leg.caption = col;
					lstColor.add(leg);
				}
				else if(lst.size()>0)
				{
				List<LatLng> lstData = new ArrayList<LatLng>();
				for (int i = 0; i < lst.size();) {
					try {
						double y = Double.valueOf(lst.get(i));
						i++;
						double x = Double.valueOf(lst.get(i));
						i++;
						lstData.add(new LatLng(x, y));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				String[] strRGB =rendr.Getkey().split(",");
				if(strRGB.length>2){
				int ranColor =Color.rgb(Integer.valueOf(strRGB[0]),Integer.valueOf(strRGB[1]),Integer.valueOf(strRGB[2]));
				Bundle bundle=new Bundle();
				bundle.putSerializable("info", "色斑图");
				OverlayOptions ooPolygon =  new PolygonOptions().points(lstData)
	                .stroke(new Stroke(5, ranColor)).fillColor(ranColor).extraInfo(bundle);
				lstOption.add(ooPolygon );
				}
				}
				else
				{
					strTile =rendr.Getkey();
				}
			}
			
				lstAllOverLay=mBaiduMap.addOverlays(lstOption);//渲染色斑图
			}
			else
			{
				
			}
			objects[0] = strTile;
			objects[1] = lstColor;
			return objects;
		}
		
		@Override
		protected void onPostExecute(Object[] result) {
			
			if (result != null && result.length > 0) {
				String title = (String)result[0];
				 List<Legend> lstColor =(List<Legend>)result[1];
				//绘制图例和标题
				legend.setVisibility(View.VISIBLE);
				tvTitle.setVisibility(View.VISIBLE);
				tvTitle.setText(title);
				legendAdapter = new LegendAdapter(oThis,lstColor);
				listview.setAdapter(legendAdapter);
			}
		}
		
	}
	
	
	// 绘制区县地名
	private void DrawAreaName() {
		HashMap<String, String> mapstrData = JsonHelper.getAreaDataFromJson(
				this, "Town.json");
		Iterator iter = mapstrData.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String[] strdatas = entry.getValue().toString().split(",");
			try {
				double y = Double.valueOf(strdatas[0]);
				double x = Double.valueOf(strdatas[1]);
//				BitmapDescriptor strbimap = BitmapDescriptorFactory.fromBitmap(convertStringToIcon(entry.getKey().toString()));
//				AreaNamePointMarkers.add(new MarkerItem(new LatLng(x,y)).setBitmapDescriptor(strbimap));
				addPoint(entry.getKey().toString(), new LatLng(x, y));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
//		 mClusterManager.addItems(AreaNamePointMarkers);
//		 mClusterManager.cluster();
	}

	private  Bitmap convertStringToIcon(String st)  
    {  
        Bitmap bitmap = null;  
        try  
        { 
        	String base64Str = (new BASE64Encoder()).encode( st.getBytes());  
            byte[] bitmapArray = Base64.decode(base64Str, Base64.DEFAULT);  
            bitmap =  BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);  
            return bitmap;  
        }  
        catch (Exception e)  
        {  
        	e.printStackTrace();
            return null;  
        }  
    }  
	 
	private void addPoint(String strArea, LatLng point) {
		/*OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
				.fontSize(18).fontColor(0xFFFF00FF).text(strArea).rotate(0)
				.position(point);*/
		int intColor = Color.parseColor("#009966");
		OverlayOptions ooText = new TextOptions()
				.fontSize(16).fontColor(intColor).text(strArea).rotate(0)
				.position(point);
		mBaiduMap.addOverlay(ooText);
	}

	
	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
	
	@Override
	public void onClick(View v) 
    {
    	switch (v.getId())
    	{
	    	case R.id.ibtn_menu:
	    		Intent intent = new Intent(this, OneMapOfflineActivity.class);
	    		startActivity(intent);
	    		break;
    	}
		super.onClick(v);
	}
	

	/**
	 * 加载大户图层
	 * @param vips
	 */
	private void loadVipUserLayer(List<VipUser> vips)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		if(vipMarkers!=null&&vipMarkers.size()>0)
		{
			return;
		}
		else if(isLayerSelected[0])//图层被选中才进行加载
		{
			vipMarkers=new ArrayList<MarkerItem>();
			MarkerExtraInfo info=null;
			Bundle bundle=null;
			for(VipUser user:vips)
			{
				info=new MarkerExtraInfo();
				info.setId(user.getId());
				info.setType(1);
				bundle=new Bundle();
				bundle.putSerializable("info", info);
				vipMarkers.add(new MarkerItem(new LatLng(user.getLatitude(),user.getLongitude())).setBitmapDescriptor(vipPoint).setExtraInfo(bundle));
			}
			mClusterManager.addItems(vipMarkers);
			mClusterManager.cluster();
		}
	}
	
	private void loadFarmLayer(List<FarmPoint> farms)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		if(farmMarkers!=null&&farmMarkers.size()>0)
		{
			return;
		}
		else if(isLayerSelected[1])//图层被选中才进行加载
		{
			farmMarkers=new ArrayList<MarkerItem>();
			MarkerExtraInfo info=null;
			Bundle bundle=null;
			for(FarmPoint farm:farms)
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
	}
	
	/**************
	 * 加载站点数据
	 * @param situations
	 */
	private void loadSituationLayer(List<SituationPoint> situations)
	{
		//不为空，且数量大于0，说明地图上已经加载了该类型的数据，不需要再重复加载
		if(situationMarkers!=null&&situationMarkers.size()>0)
		{
			return;
		}
		else if(isLayerSelected[2])//图层被选中才进行加载
		{
			situationMarkers = new ArrayList<MarkerItem>();
			MarkerExtraInfo info=null;
			Bundle bundle=null;
			for(SituationPoint situation:situations)
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
	}
	
	/**
	 * 加载大户数据
	 * @author TanRq
	 *
	 */
	private class VipUserTask extends AsyncTask<Integer,Integer,List<VipUser>>
	{
		@Override
		protected List<VipUser> doInBackground(Integer... params) 
		{
			return OneMapDao.getVipUser(PlaceManager.getCurrentArea().areacode);
		}
		
		@Override
		protected void onPostExecute(List<VipUser> result) 
		{
			vipUsers=result;
			Toast.makeText(OneMapActivity.this, "大户数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class RenderTask extends AsyncTask<Integer,Integer,List<RenderModle>>
	{
		private String ptype;
		private String pname;
		public RenderTask(String ptype,String pname){
			this.ptype = ptype;
			this.pname = pname;
		}
		@Override
		protected List<RenderModle> doInBackground(Integer... params) {
			JsonHelper help = new JsonHelper();
			return help.getRenderataFromWebJson("color",this.ptype,this.pname);

		}

		@Override
		protected void onPostExecute(List<RenderModle> result) 
		{
			//lstAllData=result;
			Toast.makeText(OneMapActivity.this, "色斑图数据加载完毕", Toast.LENGTH_SHORT).show();
			new LoadProductTask(result).execute();
		}
	}
	/**
	 * 加载农田数据
	 * @author TanRq
	 *
	 */
	private class FarmTask extends AsyncTask<Integer,Integer,List<FarmPoint>>
	{
		@Override
		protected List<FarmPoint> doInBackground(Integer... params) 
		{
			return OneMapDao.getFarmPoint(PlaceManager.getCurrentArea().areacode);
		}
		
		@Override
		protected void onPostExecute(List<FarmPoint> result) 
		{
			farms=result;
			Toast.makeText(OneMapActivity.this, "农田数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}
	

	/**
	 * 加载农情数据
	 * @author TanRq
	 *
	 */
	private class SituationTask extends AsyncTask<Integer,Integer,List<SituationPoint>>
	{
		@Override
		protected List<SituationPoint> doInBackground(Integer... params) 
		{
			return OneMapDao.getSituationPoint(PlaceManager.getCurrentArea().areacode);
		}
		
		@Override
		protected void onPostExecute(List<SituationPoint> result) 
		{
			situations=result;
			Toast.makeText(OneMapActivity.this, "农情数据加载完毕", Toast.LENGTH_SHORT).show();
		}
	}
	
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
		
		public PointInfoTask(int id,int type,LatLng position)
		{
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
					AreaPoint areaPoint = (AreaPoint) result;
					showAreaPointInfo(areaPoint,position);
					break;
			}
		}
	}
	
	private void showAreaPointInfo(AreaPoint areaPoint,LatLng position){
		infoWindow = new OneMapInfoWindow(this,areaPoint);
		if(infoWindow.isShowing()) return;
		infoWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	private void showVipUserInfo(VipUserInfo info,LatLng position)
	{
		View view=LayoutInflater.from(OneMapActivity.this).inflate(R.layout.vip_user_infowindow, null);
		
		TextView txtViewName= (TextView) view.findViewById(R.id.textViewName);
		txtViewName.setText(info.getLeaderName());
		TextView textViewTel=(TextView) view.findViewById(R.id.textViewTel);
		textViewTel.setText(info.getLeaderPhone());
		TextView textViewAddr=(TextView) view.findViewById(R.id.textViewAddr);
		textViewAddr.setText(info.getCountyName()+info.getBelongsTownship()+info.getBelongsVillage());
		TextView textViewIntroduce=(TextView) view.findViewById(R.id.textViewIntroduce);
		textViewIntroduce.setText(info.getBusinessScope());
		
		ImageButton imgBtn=(ImageButton) view.findViewById(R.id.imgBtnClose);
		imgBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				 mBaiduMap.hideInfoWindow();
			}
		});
		
		mInfoWindow = new InfoWindow(view, position, 0);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	
	private void showFarmInfo(FarmInfo info,LatLng position)
	{
		View view=LayoutInflater.from(OneMapActivity.this).inflate(R.layout.farm_infowindow, null);
		
		TextView textViewCrop=(TextView) view.findViewById(R.id.textViewCrop); 
		textViewCrop.setText(info.getCrop());
		TextView textViewCoord=(TextView) view.findViewById(R.id.textViewCoord);
		textViewCoord.setText(info.getLongitude()+","+info.getLatitude());
		TextView textViewCountry=(TextView) view.findViewById(R.id.textViewCountry);
		textViewCountry.setText(info.getAreaname());
		TextView textViewDescribe=(TextView) view.findViewById(R.id.textViewDescribe);
		textViewDescribe.setText(info.getDescript());
		
		ImageButton imgBtn=(ImageButton) view.findViewById(R.id.imgBtnClose);
		imgBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				 mBaiduMap.hideInfoWindow();
			}
		});
		
		mInfoWindow = new InfoWindow(view, position, 0);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	
	private void showSituationInfo(SituationInfo info,LatLng position)
	{
		View view=LayoutInflater.from(OneMapActivity.this).inflate(R.layout.situation_infowindow, null);
		
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
		
		ImageLoader mImageLoader=new ImageLoader(OneMapActivity.this);
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
		intent.setClass(this, CropImageActivity.class);
		this.startActivity(intent);
	}
	
	OnMarkerClickListener markerClickListener=new OnMarkerClickListener()
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
				MarkerExtraInfo info= (MarkerExtraInfo) bundle.getSerializable("info");
				new PointInfoTask(info.getId(),info.getType(),marker.getPosition()).execute();
				return true;
			}
		}
	};
	
	
	
	OnClickListener imgViewClickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(rotateAnim!=null)
			{
				imgView.startAnimation(rotateAnim);
			}
			//showDialog();
			showPupWindow();
		}
	};
	
	

	OnClickListener imgViewPrePicClickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(rotateAnim!=null)
			{
				imgView.startAnimation(rotateAnim);
			}
			iTime--;
			Time time = new Time("GMT+8");  
			int hour =time.hour + iTime;
			time.set(0, 0, hour, time.monthDay, time.month, time.year);
			strtime =time.format("yyyymmddhh");
		}
	};
	
	OnClickListener imgViewNextPicClickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(rotateAnim!=null)
			{
				imgView.startAnimation(rotateAnim);
			}

			if(iTime > 0)
			{
				Toast.makeText(OneMapActivity.this, "已是最新的", Toast.LENGTH_SHORT).show();
			}
			else 
			{
			iTime++;
			Time time = new Time("GMT+8");  
			int hour =time.hour + iTime;
			time.set(0, 0, hour, time.monthDay, time.month, time.year);
			strtime =time.format("yyyymmddhh");
			}
		}
	};
	
	private void showPupWindow()
	{
		pupup=new OneMapPupupWindow(this,pupupOKClickListener);
		pupup.setCheckedArray(isLayerSelected);
		if(pupup.isShowing())//已经显示的情况下就不再显示
			return;
		pupup.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	OnClickListener pupupOKClickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			isLayerSelected= pupup.getCheckedArray();
			pupup.dismiss();
			LoadLayer();
		}
	};
	
	private void showDialog()
	{
		AlertDialog dialog= new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
			//.setTitle(R.string.one_map_dialog_titile)
			//.setIcon(R.drawable.layers_button)
			.setMultiChoiceItems(R.array.one_map_layers_list, isLayerSelected, multiChoiceClickListener)
			.setPositiveButton("确定", okClickListener)
			.setNegativeButton("取消", null)
			.create();
		dialog.show();
		dialog.getWindow().setLayout(400, dialog.getWindow().getAttributes().height);
	}
	
	android.content.DialogInterface.OnClickListener okClickListener=new android.content.DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			LoadLayer();
		}
	};
	
	/**
	 * 加载被选择的图层
	 */
	private void LoadLayer()
	{
		legend.setVisibility(View.GONE);
		tvTitle.setVisibility(View.GONE);
		mClusterManager.clearItems();//清除所有的，然后重新加载
		//mBaiduMap.clear();//清除地图上所有的覆盖物
		vipMarkers.clear();
		farmMarkers.clear();
		situationMarkers.clear();
		
		new LoadVipUserThread().start();
		new LoadFarmThread().start();
		new LoadSituationThread().start();
		
		for(Overlay lay :lstAllOverLay)
		{
			
			if(lay!=null)
			lay.remove();
		}
		lstAllOverLay.clear();
        if(lstAllData!=null)
		{    	
        	if(isLayerSelected[3]){
        		new RenderTask("temp","2016083009").execute();
        	}
        	if(isLayerSelected[4]){
        		new RenderTask("pre","2016083009").execute();
        	}
        	if(isLayerSelected[5]){
        		new RenderTask("soil","2016083009").execute();
        	}
		}
	}
	
	
	
	
	class LoadVipUserThread extends Thread
	{
		@Override
		public void run() 
		{
			if(vipUsers!=null)
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
			if(farms!=null)
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
			if(situations!=null)
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
	
	
	class LoadBorderGonThread extends Thread
	{
		@Override
		public void run() 
		{
			DrawBroldData();
		}
	}
	
	class LoadBorderlineThread extends Thread
	{
		@Override
		public void run() 
		{
			DrawBroldLineData();
		}
	}
	
	class LoadAreaNameThread extends Thread
	{
		@Override
		public void run() 
		{
			DrawAreaName();
		}
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
	
	private void showToast(String text)
	{
		Toast.makeText(OneMapActivity.this,text, Toast.LENGTH_SHORT).show();
	}
	
	OnMultiChoiceClickListener multiChoiceClickListener=new OnMultiChoiceClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) 
		{
			isLayerSelected[which]=isChecked;
		}
	};
	
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
	
	public class MyView extends View {

		List<Integer> lstCor = new ArrayList<Integer>();
		public MyView(Context context, List<Integer> lstCor) {
			super(context);
			this.lstCor = lstCor;
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// 设置画布的背景颜色
			canvas.drawColor(Color.WHITE);
			// 定义画笔2
			Paint paint2 = new Paint();
			paint2.setStrokeWidth(2);
			// 消除锯齿
			paint2.setAntiAlias(true);
			int i=0;
			for (int cor : lstCor) {
				// 设置画笔的颜色
				paint2.setColor(cor);
				// 画一个正方形
				canvas.drawRect(20, 70+i, 90, 100+i, paint2);
				canvas.drawText("红色", 100, 80+i, paint2);
				i+=20;
			}
		}
	}
}
