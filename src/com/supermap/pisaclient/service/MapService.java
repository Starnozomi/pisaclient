package com.supermap.pisaclient.service;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.FileTileProvider;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.Tile;
import com.baidu.mapapi.map.TileOverlay;
import com.baidu.mapapi.map.TileOverlayOptions;
import com.baidu.mapapi.map.TileProvider;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.UrlTileProvider;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.common.ReadBorldpolygonData;
import com.supermap.pisaclient.common.ReadBorldpolygonData.OnOverlayOptionsReadListener;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.http.JsonHelper;

/*********************************
 * 底图服务，提供地图基础功能
 * @author Administrator
 *
 */
public class MapService {
	
	private MapService oThis =this;
	private Context context = null;
	private BaiduMap mBaiduMap = null;
	private TileOverlay tileOverlay;
	private TileProvider tileProvider;
	private Tile offlineTile;
	private List<Overlay> borders =null;
	
	private boolean mapLoaded = false;
    // 设置瓦片图的在线缓存大小，默认为20 M
    private static final int TILE_TMP = 20 * 1024 * 1024;
    private static final int MAX_LEVEL = 21;
    private static final int MIN_LEVEL = 3;
	private  final String onlineUrl = "http://183.230.183.18:8088/pisatiles/{z}/tile{x}_{y}.png";
	//http://183.230.183.18:8088/pisatiles/8/tile44_11.png 
	public MapService(Context context,MapView mMapView){
		this.context = context;
		View child = mMapView.getChildAt(1);
        /*if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){            
             child.setVisibility(View.INVISIBLE);           
        } */
        mMapView.showScaleControl(false);//地图上比例尺        
        mMapView.showZoomControls(false);// 隐藏缩放控件
        
       // mMapView.setRotationGesturesEnabled(false);
		this.mBaiduMap =  mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		UiSettings mUiSettings = this.mBaiduMap.getUiSettings();
		mUiSettings.setRotateGesturesEnabled(false);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		
//		 offlineTile();
	//	borders = new ArrayList<Overlay>();
//		new OnlineTileTask().execute();
			//onlineTile();
		//new LoadCQBorldDataThread().start();//面数据暂时不要了
		 // loadAreaBorder(PlaceManager.getCurrentArea().enname);//加载当前区域边界
	    // new LoadAreaNameThread().start();
	}

	
	
	public void setCenter(double lat,double lon,float maplevel){
		//设定中心点坐标   
	    LatLng centerpos = new LatLng(lat,lon);  
	    // 将GPS设备采集的原始GPS坐标转换成百度坐标    
	    CoordinateConverter converter  = new CoordinateConverter(); 
	    converter.from(CoordType.GPS);    
	    // sourceLatLng待转换坐标    
	    converter.coord(centerpos);    
	    LatLng desLatLng = converter.convert();  
	      
	    //地图状态创建者  
	    MapStatus.Builder builder = new MapStatus.Builder();  
	    //设定中心  
	    builder.target(desLatLng).zoom(maplevel);  
	    //改变地图状态  
	    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));  
	    mBaiduMap.setIndoorEnable(true);  
	}
	
	public void loadAreaBorder(String borderFileName){
		new LoadCQBorldLineDataThread(borderFileName).start();
	}
	
	
	class LoadCQBorldDataThread extends Thread {

		@Override
		public void run() {
			OnOverlayOptionsReadListener l = new OnOverlayOptionsReadListener(){

				@Override
				public void ReadDoneListening(List<OverlayOptions> lstOptionGon) {
					// TODO Auto-generated method stub
					
				}
				
			};
			ReadBorldpolygonData.InitReadPolgonObj().ReadBordData(context,l);
		}
	}

	class LoadCQBorldLineDataThread extends Thread {
		
		private String borderAreaName;
		public LoadCQBorldLineDataThread(String borderAreaName){
			this.borderAreaName = borderAreaName;
		}
		@Override
		public void run() {
			OnOverlayOptionsReadListener l = new OnOverlayOptionsReadListener(){

				@Override
				public void ReadDoneListening(List<OverlayOptions> lstOptionGon) {
					// TODO Auto-generated method stub
					new LoadBorderlineThread().start();
				}
				
			};
			ReadBorldpolygonData.InitReadPolgonObj().ReadBordLineData(context,borderAreaName,l);
		}
	}
	
	
	class LoadBorderGonThread extends Thread {
		@Override
		public void run() 
		{
			DrawBroldData();
		}
	}
	
	class LoadBorderlineThread extends Thread {
		@Override
		public void run() 
		{
			DrawBroldLineData();
		}
	}
	
	class LoadAreaNameThread extends Thread {
		@Override
		public void run() 
		{
			DrawAreaName();
		}
	}
	
	
	
	    //得到重庆市边界线
		private void DrawBroldLineData() {
			for(Overlay lay :borders)
			{
				if(lay!=null) lay.remove();
			}
			List<OverlayOptions> lstOptionGon = ReadBorldpolygonData.InitReadPolgonObj().lstOptionLine;
			borders = mBaiduMap.addOverlays(lstOptionGon);
			Area a = PlaceManager.getCurrentArea();
			if(a != null){
				oThis.setCenter(a.centerlat,a.centerlon, a.maplevel);
			}
		}
		//得到重庆市边界面
		private void DrawBroldData()
		{
			List<OverlayOptions> lstOptionGon = ReadBorldpolygonData.InitReadPolgonObj().lstOptionGon;
			mBaiduMap.addOverlays(lstOptionGon);
		}
		// 绘制区县地名
			private void DrawAreaName() {
				HashMap<String, String> mapstrData = JsonHelper.getAreaDataFromJson(
						context, "Town.json");
				Iterator iter = mapstrData.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String[] strdatas = entry.getValue().toString().split(",");
					try {
						double y = Double.valueOf(strdatas[0]);
						double x = Double.valueOf(strdatas[1]);
						addPoint(entry.getKey().toString(), new LatLng(x, y));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			private void addPoint(String strArea, LatLng point) {
				int intColor = Color.parseColor("#ff000000");
				int bgColor = Color.parseColor("#ff5151");
				OverlayOptions ooText = new TextOptions()
						.fontSize(24).fontColor(0xFFFF00FF).text(strArea).rotate(0).bgColor(0xAAFFFF00)
						.position(point);
				mBaiduMap.addOverlay(ooText);
			}
			
			
			/**
		     * 瓦片图的离线添加
		     */
		    private void offlineTile() {
		    if (tileOverlay != null && mBaiduMap != null) {
		        tileOverlay.removeTileOverlay();
		    }

		    /**
		     * 定义瓦片图的离线Provider，并实现相关接口
		     * MAX_LEVEL、MIN_LEVEL 表示地图显示瓦片图的最大、最小级别
		     * Tile 对象表示地图每个x、y、z状态下的瓦片对象
		     */
		    tileProvider = new FileTileProvider() {
		        @Override
		        public Tile getTile(int x, int y, int z) {
		            String filedir = "tiles/" + z + "/tile" + x + "_" + y + ".png";
//		        	 String filedir = "tiles/" + z + "/" + x + "/" + y + ".jpg";
		            Bitmap bm = getFromAssets(filedir);
		            if (bm == null) {
		                return null;
		            }
		            // 瓦片图尺寸必须满足256 * 256
		            offlineTile = new Tile(bm.getWidth(), bm.getHeight(), toRawData(bm));
		            bm.recycle();
		            return offlineTile;
		        }

		        @Override
		        public int getMaxDisLevel() {
		            return 10;
		        }

		        @Override
		        public int getMinDisLevel() {
		            return 10;
		        }

		    };
		    
		    TileOverlayOptions options = new TileOverlayOptions();
		    // 构造显示瓦片图范围，当前为世界范围
		    LatLng northeast = new LatLng(80, 180);
		    LatLng southwest = new LatLng(-80, -180);
		    // 设置离线瓦片图属性option
		    options.tileProvider(tileProvider)
		            .setPositionFromBounds(new LatLngBounds.Builder().include(northeast).include(southwest).build());
		    // 通过option指定相关属性，向地图添加离线瓦片图对象
		    tileOverlay = mBaiduMap.addTileLayer(options);
		    if (mapLoaded) {
		        setMapStatusLimits();
		    }

		}
		    
		    private class OnlineTileTask extends AsyncTask<Integer,Integer,Integer>
			{
				@Override
				protected Integer doInBackground(Integer... params) 
				{
					onlineTile();
					return 0;
				}
				
				@Override
				protected void onPostExecute(Integer result) 
				{
					
				}
			}
		    /**
		     * 使用瓦片图的在线方式
		     */
		    private void onlineTile() {

		        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		        if (tileOverlay != null && mBaiduMap != null) {
		            tileOverlay.removeTileOverlay();
		        }
		        final String urlString = onlineUrl;
		        /**
		         * 定义瓦片图的在线Provider，并实现相关接口
		         * MAX_LEVEL、MIN_LEVEL 表示地图显示瓦片图的最大、最小级别
		         * urlString 表示在线瓦片图的URL地址
		         */
		        TileProvider tileProvider = new UrlTileProvider() {
		            @Override
		            public int getMaxDisLevel() {
		                return MAX_LEVEL;
		            }

		            @Override
		            public int getMinDisLevel() {
		                return MIN_LEVEL;
		            }

		            @Override
		            public String getTileUrl() {
		                return urlString;
		            }
		        };
		        TileOverlayOptions options = new TileOverlayOptions();
		        // 构造显示瓦片图范围，当前为世界范围
		        LatLng northeast = new LatLng(80, 180);
		        LatLng southwest = new LatLng(-80, -180);
		        // 通过option指定相关属性，向地图添加在线瓦片图对象
		        tileOverlay = mBaiduMap.addTileLayer(options.tileProvider(tileProvider).setMaxTileTmp(TILE_TMP)
		                .setPositionFromBounds(new LatLngBounds.Builder().include(northeast).include(southwest).build()));
		        if (mapLoaded) {
		            mBaiduMap.setMaxAndMinZoomLevel(21.0f, 3.0f);
		            mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());
//		            mBaiduMap.setMapStatus(mMapStatusUpdate);
		        }
		    }
		    
		    private void setMapStatusLimits() {
		        LatLngBounds.Builder builder = new LatLngBounds.Builder();
		        builder.include(new LatLng(30.06948, 109.18163))
		        		.include(new LatLng(29.01547, 105.42858));
		        mBaiduMap.setMapStatusLimits(builder.build());
		        mBaiduMap.setMaxAndMinZoomLevel(8.0f, 11.0f);
		        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		    }

		    /**
		     * 瓦片文件解析为Bitmap
		     * @param fileName
		     * @return 瓦片文件的Bitmap
		     */
		    public Bitmap getFromAssets(String fileName) {
		        AssetManager am = context.getAssets();
		        InputStream is = null;
		        Bitmap bm;
		        try {
		            is = am.open(fileName);
		            bm = BitmapFactory.decodeStream(is);
		            return bm;
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }
		    }
		    /**
		     * 解析Bitmap
		     * @param bitmap
		     * @return
		     */
		    byte[] toRawData(Bitmap bitmap) {
		        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth()
		                * bitmap.getHeight() * 4);
		        bitmap.copyPixelsToBuffer(buffer);
		        byte[] data = buffer.array();
		        buffer.clear();
		        return data;
		    }

		    public void addOverlay(double lat,double lon,int res){
		    	//定义Maker坐标点  
		    	LatLng point = new LatLng(lat, lon);  
		    	//构建Marker图标  
		    	BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    	    .fromResource(res);  
		    	//构建MarkerOption，用于在地图上添加Marker  
		    	OverlayOptions option = new MarkerOptions()  
		    	    .position(point)  
		    	    .icon(bitmap);  
		    	//在地图上添加Marker，并显示  
		    	mBaiduMap.addOverlay(option);
		    }

			public BaiduMap getmBaiduMap() {
				return mBaiduMap;
			}
			
			
		    
}
