package com.supermap.pisaclient.ui;

import java.util.Calendar;
import java.util.List;

import com.baidu.mapapi.map.MapView;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CityChangePagerAdapter;
import com.supermap.pisaclient.adapter.LegendAdapter;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Lunar;
import com.supermap.pisaclient.common.WeatherDataUtil;
import com.supermap.pisaclient.common.views.MeteoMapOpers;
import com.supermap.pisaclient.common.views.MeteoSpecialMapWindow;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.Weather;
import com.supermap.pisaclient.service.PlaceManager;
import com.supermap.pisaclient.service.PlaceManager.OnPlaceManagerListener;
import com.supermap.pisaclient.service.ProductLayer;
import com.supermap.pisaclient.service.VectorManager;
import com.supermap.pisaclient.service.MapService;
import com.supermap.pisaclient.service.RasterProductManager;
import com.supermap.pisaclient.service.WeatherManager;
import com.supermap.pisaclient.service.WeatherManager.OnLiveWeatherLoadListener;
import com.supermap.pisaclient.service.WeatherProductManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

public class MeteoMapActivity extends FragmentActivity implements OnClickListener
{
	private MeteoMapActivity oThis = this;
	private MeteoMapOpers operPanel = null;//操作栏面板
	private MapView mMapView = null;//地图
	private Animation rotateAnim=null;
	private RelativeLayout rlTopBar = null;//顶部栏
	private LinearLayout llTopMenu = null;//顶部菜单，气象菜单
	private HorizontalScrollView llBottomMenu = null;//底部菜单，农气菜单
	User user;	

	private ImageView ivAccount = null;//个人设置
	private ImageView ivMsg = null;//消息
	private ImageView ivSetting = null;//设置
	private LinearLayout llLocationBox = null; //位置
	private TextView tvLocationName = null;//所在位置名称
	//气象菜单
	private View vAlarm = null;//预警
	private View vWeather = null;//天气
	private View vTemp = null;//温度
	private View vPrecipitation = null;//降水
	private View vSunLight = null;//日照
	//农气菜单
	private Button vFarmland = null;//农田
	private Button vVip = null;//大户
	private Button vArgInfo = null;//农情
	private Button vAdvisory = null;//咨询
	private Button vServiceProduct = null;//服务产品
	private Button vSpecialMap = null;//专题图
	private ImageView ivAdd = null;
	private long firstTime = 0;

	//实况天气面板
	private RelativeLayout rlWeatherPanel = null;
	private TextView tvLiveTemp = null;
	private TextView tvLiveWeather  = null;
	private TextView tvLiveWind = null;
	private TextView tvLiveDate = null;
	

	private List<City> mCitys;
	private List<Weather> mWeathers;
	private String mDefaultCity;
	private String mCurrentCity;
	public CityDao mCityDao;
	private CityChangePagerAdapter mAdapter = null;
	private WeatherDao mWeatherDao = null;
	private FarmDao mFarmDao = null;
	private WeatherDataUtil mWeatherDataUtil;
	private LegendAdapter legendAdapter = null;
	private TextView tvTitle = null;
	private ListView listview =null;
	private LinearLayout legend = null;
	private TextView tvUnit = null;
	MapService mapService = null;
	WeatherManager weatherManager = null;
	PlaceManager placeManager = null;

	//栅格产品管理器，集中管理所有栅格产品数据
	private RasterProductManager rasterProductManager = null;
	private WeatherProductManager weatherProductManager =null; 
	private ProductLayer serviceProductLayer= null;
	//农业基础数据管理器，集中管理大户、农田、农情、咨询等基础和业务数据
	private VectorManager vectorManager = null;
	private MeteoSpecialMapWindow  meteoSpecialMapWindow = null;
	private boolean isFirstLoad = true;
	private AlertDialog myDialog;
	
	private ImageView btnMyFarm;  //
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initViews();
		this.bindEvents();	
		this.initServices();
		isFirstLoad = false;
	}
	
	
	private void initViews(){
		this.setContentView(R.layout.meteo_map_main);
		//地图相关界面
		mMapView = (MapView) findViewById(R.id.bmapView);	
		ivAdd = (ImageView) findViewById(R.id.ivAdd);
		llTopMenu  = (LinearLayout)findViewById(R.id.llTopMenu);
		llBottomMenu = (HorizontalScrollView)findViewById(R.id.llBottomMenu);
		rlTopBar = (RelativeLayout)findViewById(R.id.rlTopBar);
		ivAccount = (ImageView)findViewById(R.id.ivAccount);
		ivMsg = (ImageView)findViewById(R.id.ivMsg);
		ivSetting = (ImageView)findViewById(R.id.ivSetting);
		llLocationBox = (LinearLayout)findViewById(R.id.llLocationBox);
		tvLocationName = (TextView)findViewById(R.id.tvLocationName);
		//栅格产品相关控件
		listview =(ListView)  findViewById(R.id.list_legend);
		legend = (LinearLayout) findViewById(R.id.ll_legend);
		tvUnit = (TextView)findViewById(R.id.tv_unit);
		//legend_list= (HorizontalListView)findViewById(R.id.legend_listview);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		
		vAlarm = (Button)findViewById(R.id.vAlarm);
		vWeather = (Button)findViewById(R.id.vWeather);
		vTemp = (Button)findViewById(R.id.vTemp);
		vPrecipitation = (Button)findViewById(R.id.vPrecipitation);
		vSunLight = (Button)findViewById(R.id.vSunLight);
		vFarmland = (Button)findViewById(R.id.vFarmland);
		vVip = (Button)findViewById(R.id.vVip);
		vArgInfo = (Button)findViewById(R.id.vArgInfo);
		vAdvisory = (Button)findViewById(R.id.vAdvisory);
		vServiceProduct = (Button)findViewById(R.id.vServiceProduct);
		vSpecialMap = (Button)findViewById(R.id.vSpecialMap);
	
		//实况天气面板控件
		rlWeatherPanel = (RelativeLayout)findViewById(R.id.rlWeatherPanel);
		tvLiveTemp = (TextView)findViewById(R.id.tvLiveTemp);
		tvLiveWeather  = (TextView)findViewById(R.id.tvLiveWeather);
		tvLiveWind = (TextView)findViewById(R.id.tvLiveWind);
		tvLiveDate = (TextView)findViewById(R.id.tvLiveDate);
		btnMyFarm = (ImageView)findViewById(R.id.btn_myfarm);
		
	}
	
	private void bindEvents(){
		rotateAnim=AnimationUtils.loadAnimation(this, R.anim.rotate_image);
		ivAdd.setOnClickListener(this);
		ivAccount.setOnClickListener(this);
		ivSetting.setOnClickListener(this);
		ivMsg.setOnClickListener(this);
		llLocationBox.setOnClickListener(this);
		vAlarm.setOnClickListener(this);
		vWeather.setOnClickListener(this);
		vTemp.setOnClickListener(this);
		vPrecipitation.setOnClickListener(this);
		vSunLight.setOnClickListener(this);
		vServiceProduct.setOnClickListener(this);
		vSpecialMap.setOnClickListener(this);
		
	}
	
	
	public boolean checkNet() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
			return false;
		}
		return true;
	}
	
	private void initServices(){
		
		mCityDao = new CityDao(oThis);
	//	mWeatherDao = new WeatherDao();
		mCropTypeDao = new CropTypeDao();
		mFarmDao = new FarmDao();
		 //初始化天气实况
    //    mWeatherDataUtil = new WeatherDataUtil();
		mDefaultCity = ExitApplication.getInstance().mDefaultCity;
		new LoadAllCityTask().execute();
		//初始化区域管理器
		try{
			
		}catch(Exception ee){
			
		}
        placeManager = new PlaceManager(this,onPlaceManagerListener);
        if(mDefaultCity == null) mDefaultCity = "内蒙古自治区";
        Area a = placeManager.getAreaList().get(mDefaultCity);
        if(a != null){
        	PlaceManager.setCurrentArea(a);
    		tvLocationName.setText(mDefaultCity);
        }
		//初始化地图服务对象
        mapService = new MapService(this,mMapView);
        Area curArea = PlaceManager.getCurrentArea();
        if(curArea != null){
        	mapService.setCenter(curArea.centerlat, curArea.centerlon, curArea.maplevel);
        }
        
       //初始化农业数据管理器,农田、大户、农情
        vectorManager = new VectorManager(this,mapService.getmBaiduMap(),this.mMapView,mapService,new View[]{
        	vFarmland,vVip,vArgInfo,vAdvisory
        });
        //判断用户是否登陆，如果是vip，则获取他的农田列表，如果农田存在就选择一个取其经纬度，作为中心点，
        vectorManager.loadVipFarm(btnMyFarm);
        //btnMyFarm.performClick();
       // new LoadAgrTypeInfoTask(1).execute();
	}
	
	private List<CropType> mCropTypeList;
	private Boolean isLoading = false;
	private CropTypeDao mCropTypeDao;
	private String mAreaCode = "150000"; // 地区id
	/**
	 *  获取整个作物列表ID、NAME 
	 */
	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean> {
		private int mType = 0;
		private boolean mLoadPass = false;

		public LoadAgrTypeInfoTask(int type) {
			this.mType = type;
		}

		protected Boolean doInBackground(Integer... arg0) {
			switch (mType) {
			case 1:
				mCropTypeList = mCropTypeDao.getCropTypes(mAreaCode.substring(0,6));
				if (mCropTypeList != null) {
					mLoadPass = true;
				}
				break;
			default:
			    break;
			}
			return mLoadPass;
		}

	    protected void onPostExecute(Boolean result) {
	    	if (!result) {
	    	}
	    	isLoading = false;
	    	switch (mType) {
	    		case 1:// 种养类型选择
	    			if (mCropTypeList != null && mCropTypeList.size() > 0) {
	    				for (int i = 0; i < mCropTypeList.size(); i++) {
	    					String aaaa = mCropTypeList.get(i).name;
	    					Common.dataname[i] = mCropTypeList.get(i).name;
	    					Common.dataid[i] = mCropTypeList.get(i).id;
	    				}
	    			}
	    			break;
		    }
	  }
	}

	boolean alarmImageState = false;
	boolean weatherImageState = false;
	boolean tempImageState = false;
	boolean preImageState = false;
	boolean sunlightImageState = false;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		Bundle bundle;
		switch(v.getId()){
			case R.id.ivAccount:
				
				break;
			case R.id.ivMsg: //消息中心
				intent = new Intent(oThis, MessageCenterActivity.class);
				startActivity(intent);
				break;
			case R.id.ivSetting: //用户功能菜单
				intent = new Intent(oThis, SettingActivity.class);
				startActivity(intent);
				break;
			case R.id.llLocationBox: //定位城市按钮
				//intent = new Intent();
				//bundle = new Bundle();	
				//intent.putExtras(bundle);
				//intent.setClass(oThis, CityAdd.class);
				//startActivityForResult(intent,999);   //之后版本会注释
				Intent cityset = new Intent();
				 bundle = new Bundle();
				 AreaSelectParameter mAreaSelectParameter= new AreaSelectParameter();
				mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
				mAreaSelectParameter.isWeatherArea = true;
				mAreaSelectParameter.isSelectMore = true;
				mAreaSelectParameter.isShowRemind = true;
				bundle.putSerializable("parameter", mAreaSelectParameter);
				cityset.putExtras(bundle);
				cityset.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
				cityset.setClass(oThis, CitySetActivity.class);
				startActivityForResult(cityset, Constant.CITY_ADD_REQUEST); //之后版本会注释
				
				
				break;
			case R.id.vAlarm: //
				// R.drawable.meteomap_menu_icon_alarm
				resetTopMenuImage();
				if(!alarmImageState){
					v.setBackgroundResource(R.drawable.meteomap_menu_icon_alarm);
					alarmImageState = true;
				}else{
					v.setBackgroundResource(R.drawable.meteomap_menu_icon_alarm_on);
					alarmImageState = false;
				}
				break;
			case R.id.vWeather:  //天气按钮
				weatherProductManager.isChecked =!weatherProductManager.isChecked;
		        weatherProductManager.ShowWeatherInfo(weatherProductManager.isChecked);
		        resetTopMenuImage();
		        if(!weatherImageState){
					v.setBackgroundResource(R.drawable.meteomap_menu_icon_weather);
					weatherImageState = true;
				}else{
					v.setBackgroundResource(R.drawable.meteomap_menu_icon_weather_on);
					weatherImageState = false;
				}
				break;
			case R.id.vTemp:  //温度按钮
				rasterProductManager.showLatestTemp();
				resetTopMenuImage();
				 if(!tempImageState){
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_temp);
						tempImageState = true;
					}else{
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_temp_on);
						tempImageState = false;
					}
				break;
			case R.id.vPrecipitation: //降水按钮
				rasterProductManager.showLatestPre();
				resetTopMenuImage();
				 if(!preImageState){
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_pre);
						preImageState = true;
					} else {
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_pre_on);
						preImageState = false;
					}
				break;
			case R.id.vSunLight:  //日照按钮
				//rasterProductManager.showLatestSunlight(PlaceManager.getCurrentArea().enname);//暂时是是墒情数据
				rasterProductManager.showSpecialMap("ziliao", "rizhao");
				resetTopMenuImage();
				 if(!sunlightImageState){
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_sunlight);
						sunlightImageState = true;
					}else{
						v.setBackgroundResource(R.drawable.meteomap_menu_icon_sunlight_on);
						sunlightImageState = false;
				 }
				break;
			case R.id.vSpecialMap: //
				if(meteoSpecialMapWindow == null){
					meteoSpecialMapWindow = new MeteoSpecialMapWindow(oThis,mapService.getmBaiduMap(),oThis.mMapView,oThis.rasterProductManager);
				}
				if(rasterProductManager.getCurrentRasterProduct() == null){	
					meteoSpecialMapWindow.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				}else{
					rasterProductManager.hide();
				}
				break;
			case R.id.ivAdd: //右下角添加按钮
				if(rotateAnim!=null){
					ivAdd.startAnimation(rotateAnim);
				}
				showOPerPanel();
//				if (UserDao.getInstance().get() == null) {
//					CommonDialog.setLoginDialog(this);
//				} else {
//					intent = new Intent(this, FarmManagerActivity.class);
//					startActivity(intent);
//				}
				break;
			case R.id.btnAddVip:  //右下角添加按钮 内菜单 注册
					intent = new Intent(oThis, UserRegActivity.class);
					oThis.startActivity(intent);
				break;
		   case R.id.btnAddFarmland: //右下角添加按钮 内菜单 农田
					askAddFarm();
				break;
			case R.id.btnAddArgInfo: //右下角添加按钮 内菜单 农情
				if(UserDao.getInstance().get() != null){
						intent = new Intent(oThis, CropUploadActivity.class);
						intent.putExtra("key", "farm");//farm则加载显示农田选项,point则加载显示地区选项
						intent.putExtra("croptype", "");
						intent.putExtra("cropvarietyname", "");
						oThis.startActivity(intent);
				}else{
					intent = new Intent(oThis,UserLoginActivity.class);
					oThis.startActivityForResult(intent, Constant.LOGIN_SUCCEED);
				}
				break;
			case R.id.btnAddVis: //右下角添加按钮 内菜单 咨询
				if(UserDao.getInstance().get() != null){
					if(vectorManager!= null && vectorManager.getMyFarms() != null && vectorManager.getMyFarms().size() >0){
						intent = new Intent(oThis,AdvisoryAskActivity.class);
						oThis.startActivity(intent);
					}else{
							AlertDialog.Builder builder = new AlertDialog.Builder(oThis);
							builder.setMessage("需要登记田块才能发起咨询，现在就添加田块吗？");
							builder.setTitle("提示");
							builder.setPositiveButton("是", new android.content.DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0,int arg1) {
									askAddFarm();
								}
							});
							builder.setNegativeButton("否", new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,int arg1) {
								}
							});
							builder.create().show();
					}
				} else {
					intent = new Intent(oThis,UserLoginActivity.class);
					oThis.startActivityForResult(intent, Constant.LOGIN_SUCCEED);
				}
				break;
				
		}
	}
	
	private void resetTopMenuImage(){
		vAlarm.setBackgroundResource(R.drawable.meteomap_menu_icon_alarm_on);
		vWeather.setBackgroundResource(R.drawable.meteomap_menu_icon_weather_on);
		vTemp.setBackgroundResource(R.drawable.meteomap_menu_icon_temp_on);
		vPrecipitation.setBackgroundResource(R.drawable.meteomap_menu_icon_pre_on);
		vSunLight.setBackgroundResource(R.drawable.meteomap_menu_icon_sunlight_on);
	}

	private void askAddFarm(){
		if(UserDao.getInstance().get() != null){
			
			myDialog = new AlertDialog.Builder(oThis).create();  
            myDialog.show();  
            myDialog.getWindow().setContentView(R.layout.farm_add_menu);  
            myDialog.getWindow()  
                .findViewById(R.id.rlFastAdd)  
                .setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) { 
                	Intent it = new Intent();
                	it.setClass(oThis, FarmlandCreateActivity.class);
                	startActivity(it);
                    myDialog.dismiss();  
                }  
            });  
            myDialog.getWindow()  
            .findViewById(R.id.rlWriteFarm)  
            .setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	Intent it = new Intent();
			    Bundle b = new Bundle();	
				it.putExtras(b);			
				it.setClass(oThis, FarmManagerActivity.class);
				startActivityForResult(it,Constant.ADD_FARM_RESULT);	             
                myDialog.dismiss();  
            }  
        });  
            WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
            params.width = 600;
            myDialog.getWindow().setAttributes(params);
		}else{
			Intent intent = new Intent(oThis, UserLoginActivity.class);
			oThis.startActivityForResult(intent, Constant.LOGIN_SUCCEED);
		}
	}
	

	
	OnPlaceManagerListener onPlaceManagerListener = new OnPlaceManagerListener(){
		@Override
		public void Loaded(List<City> citys) {
			if (citys.size() == 0) {
				Intent intent = new Intent();
				AreaSelectParameter mAreaSelectParameter = null;
				if (mAreaSelectParameter == null) {
					mAreaSelectParameter = new AreaSelectParameter();
					mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
					mAreaSelectParameter.isWeatherArea = true;
					mAreaSelectParameter.isSelectMore = true;
					mAreaSelectParameter.isShowRemind = true;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("parameter", mAreaSelectParameter);
				intent.putExtras(bundle);
				intent.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
				intent.setClass(oThis, CitySetActivity.class);
				//startActivityForResult(intent, Constant.CITY_ADD_REQUEST); //之后版本会注释
			}
			//weatherManager.loadWeather();
		}
	};
	

	
	private class LoadAllCityTask extends AsyncTask<String,Integer,List<City>>{
		@Override
		protected List<City> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mCityDao.getAllCity();
		}
		@Override
		protected void onPostExecute(List<City> result) {
			mCitys =result;
			//new LoadWeatherDataTask().execute();
		}
	}

	private class LoadWeatherDataTask extends AsyncTask<String, Integer, List<Weather>> {
		@Override
		protected List<Weather> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return mCityDao.getAllWeathers();
		}
		
		@Override
		protected void onPostExecute(List<Weather> result) {
			mWeathers = result;
			//setCurrentCity();
			if (mCitys.size() == 0) {
				Intent intent = new Intent();
				AreaSelectParameter mAreaSelectParameter = null;
				if (mAreaSelectParameter == null) {
					mAreaSelectParameter = new AreaSelectParameter();
					mAreaSelectParameter.flag = Constant.ADD_NET_AREA;
					mAreaSelectParameter.isWeatherArea = true;
					mAreaSelectParameter.isSelectMore = true;
					mAreaSelectParameter.isShowRemind = true;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("parameter", mAreaSelectParameter);
				intent.putExtras(bundle);
				intent.putExtra(Constant.CITY_SET_ACTIVITY_START_FLAG, Constant.ADD_NET_AREA);
				intent.setClass(oThis, CitySetActivity.class);
				//startActivityForResult(intent, Constant.CITY_ADD_REQUEST); //之后版本会注释
			}
		}
		
	}
	

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode == Constant.ADD_FARM_RESULT){ //添加农田回来
	    	//Bundle bundle =data.getExtras();    
	    	 vectorManager.loadVipFarm(btnMyFarm);
	    }else if (resultCode == Constant.CITY_SET_RESULT) {
			/*List<City> list = (List<City>) data.getSerializableExtra("city");
			mCitys.addAll(list);
			mCityDao.addAllCityTocitys(list);*/
	    	List<City> list  = (List<City>)data.getSerializableExtra("city");
			if(list.size() > 0){
				City c = list.get(0);
				oThis.tvLocationName.setText(c.name);
				if(placeManager.getAreaList().containsKey(c.name)){
					Area a =  placeManager.getAreaList().get(c.name);
					if(a != null){
						PlaceManager.setCurrentArea(a);   
					    mapService.setCenter(a.centerlat, a.centerlon, a.maplevel);
					}
				}
			}
		}else if(resultCode == 999){
			Bundle bundle =data.getExtras();    
			String selectedCityName = bundle.getString("selectedCityName");    
			String selectedCityCode = bundle.getString("selectedCityCode");    
			oThis.tvLocationName.setText(selectedCityName);
			//weatherManager.loadLiveWeather(selectedCityName,onLiveWeatherLoadListener);
			if(placeManager.getAreaList().containsKey(selectedCityName)){
				Area a =  placeManager.getAreaList().get(selectedCityName);
				if(a != null){
					PlaceManager.setCurrentArea(a);   
					//重新定位中心点
					//mapService.loadAreaBorder(a.enname);   
				    mapService.setCenter(a.centerlat, a.centerlon, a.maplevel);
				}

		        //重新载入矢量点数据
			   /* if(vectorManager != null){
		        	vectorManager.reClustData();
		        }*/
			    //重新载入栅格数据
			    //rasterProductManager.AfterAreaChanged(PlaceManager.getCurrentArea());
			}	
		}else if(resultCode == Constant.LOGIN_SUCCEED){
			vectorManager.loadVipFarm(btnMyFarm);
		}
	    
		/*if (mCitys.size() == 0) {
			// ExitApplication.getInstance().exit(0);
			//finish();
		}*/
	}
	
	//地图上 温度、湿度View
	OnLiveWeatherLoadListener onLiveWeatherLoadListener = new OnLiveWeatherLoadListener() {
		@Override
		public void Loaded(Weather weather) {
			// TODO Auto-generated method stub
			if(weather != null){
				oThis.tvLiveTemp.setText(String.valueOf(weather.Temperature)+"℃");
				oThis.tvLiveWeather.setText(WeatherDataUtil.nowToForcastStr(weather.WeatherState));
				oThis.tvLiveWind.setText(String.valueOf(weather.Humidity)+"%  "+mWeatherDataUtil.getWindDirection(Float.parseFloat(weather.WindDirection)) + ""
						+ mWeatherDataUtil.getWindDec(Float.parseFloat(weather.WindSpeed) / 10));
				Calendar today = Calendar.getInstance();
				int y = today.get(Calendar.YEAR);
				int m = today.get(Calendar.MONTH)+1;
				int d = today.get(Calendar.DATE);
				Lunar lunar = new Lunar(today);
				oThis.tvLiveDate.setText(m+"月"+d+"日  "+lunar.toString());
			}
		}
	};
	
	
	/**
	 * 显示操作面板
	 */
	private void showOPerPanel() {
		operPanel = new MeteoMapOpers(this, oThis);
		if(operPanel.isShowing()) return;
		operPanel.showAtLocation(mMapView, Gravity.BOTTOM|Gravity.LEFT, 60, 260);
	}

	/**
	 *  两次退出
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				CommonUtil.showToask(oThis, getResources().getString(R.string.exit_wait));
				firstTime = secondTime;
				return true;
			} else {
				ExitApplication.getInstance().exit(0);
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
	    if(!isFirstLoad){
	    	vectorManager.loadVipFarm(btnMyFarm);
		}
		super.onResume();
	}


	
	
}
