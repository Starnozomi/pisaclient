package com.supermap.pisaclient.common.views;

import java.io.ObjectOutputStream.PutField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.baidu.a.a.a.c;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryQuestionAdapter;
import com.supermap.pisaclient.adapter.CropSituationAdapter;
import com.supermap.pisaclient.adapter.FarmAdvisoryQuestionAdapter;
import com.supermap.pisaclient.adapter.FarmSituationAdapter;
import com.supermap.pisaclient.adapter.FarmSuggestAdapter;
import com.supermap.pisaclient.adapter.FarmWarnAdapter;
import com.supermap.pisaclient.biz.AdvMaxNumDao;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.CropsCache;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ElapseTime;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.Climate;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmSuggest;
import com.supermap.pisaclient.entity.FarmWarnInfo;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProfProduct;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.pullrefresh.PullToRefreshListView;
import com.supermap.pisaclient.service.PlaceManager;
import com.supermap.pisaclient.service.ProductPoint;
import com.supermap.pisaclient.ui.AdvisoryAskActivity;
import com.supermap.pisaclient.ui.CropUploadActivity;
import com.supermap.pisaclient.ui.SituationActivity;
import com.supermap.pisaclient.ui.FarmActivity.MyOnClickListener;
import com.supermap.pisaclient.ui.FarmActivity.MyOnPageChangeListener;
import com.supermap.pisaclient.ui.FarmActivity.MyPagerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MeteoMapFarmWindow extends PopupWindow implements OnClickListener {
	private MeteoMapFarmWindow oThis = this;
	private View mMenuView;
	private ProgressWebView mMonitorWebView=null;
	private ImageView ib_close;
	private Farm info;
	private User farmUser;
	private TextView tvFarmName;
	private FarmDao mFarmDao;
	private Context context;
	
	private ViewPager mPager;
	private List<View> listViews;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int count = 0;
	private int lastItem;
	private int max = 1000;
	private Product mOneProduct = null;
	private View currentView = null;
	private ImageView cursor;
	private int menu = 1;
	private int mPageSize = 6;
	private int mPageIndex = 1;
	
	//private TextView tvWarn;
	private TextView tvInfo;
	private TextView tvWeather;
	private TextView tvProduct;
	private TextView tvAgrinfo;
	private TextView tvAdvisory;
	private TextView tvAddetail;
	

	public MeteoMapFarmWindow(Activity context,Farm info)
	{
		super(context);
		this.info = info;
		this.context = context;
		this.mFarmDao = new FarmDao();
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.farm, null);
			
		//设置OneMapPupupWindow的View  
		
		aniShow();
		InitTextView();
		InitImageView();
		InitViewPager();
		
		
		//new FirstLoadTask(this.info.id).execute();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		if(v.getId() == R.id.ib_close){
//			oThis.dismiss();
//		}
	}
	
	class FirstLoadTask extends AsyncTask<Integer,Integer,Farm> {
		private Integer farmId;
		public FirstLoadTask(Integer farmId) {
			this.farmId = farmId;
		}
		@Override
		protected Farm doInBackground(Integer... params) {
			return mFarmDao.getFarmAttributeByIdNew(farmId);
		}
		@Override
		protected void onPostExecute(Farm result) {
			// TODO Auto-generated method stub
			info = result;
			super.onPostExecute(result);
			
		}
		
		
	}
	
	//加载和显示详情数据
		class LoadFarmTask extends AsyncTask<Integer,Integer,Farm>
		{
			private Integer farmId;
			public LoadFarmTask(Integer farmId){
				this.farmId = farmId;
			}
			@Override
			protected Farm doInBackground(Integer... params) 
			{
				return mFarmDao.getFarmAttributeByIdNew(farmId);
			}
			
			@Override
			protected void onPostExecute(Farm result) 
			{
				info = result;
				Farm_name.setText(info.descript); //农田名称
				
				Workstation_apply.setText(""+((info.workStationName  == null  || info.cropTypeName == "null") ? "未指定基地":info.workStationName));//
				if(Workstation_apply.getText().toString().equals("null") || Workstation_apply.getText().toString().equals(""))   //隶属基地
				{
					Workstation_apply.setText("未指定基地");
				}
				
	            Region.setText(""+((info.areaName  == null  || info.areaName == "null") ? "暂无数据":info.areaName));//地区	
	            if(Region.getText().toString().equals("null") || Region.getText().toString().equals("")) 
				{
					Type.setText("暂无数据");
				}
	            
				Type.setText(""+((info.cropVarietyName  == null  || info.cropVarietyName == "null") ? "暂无数据":info.cropVarietyName));  //种养类型;
				if(Type.getText().toString().equals("null") || Type.getText().toString().equals(""))  
				{
					Type.setText("暂无数据");
				}
				Variety.setText(""+((info.cropTypeName == null  || info.cropTypeName == "null") ? "未指定作物":info.cropTypeName)); //种养品种
				Lng.setText(info.longitude);  //经度
	            Lat.setText(info.latitude);   //纬度
	            Height.setText(info.height); //海拔高度
				FarmArea.setText(""+ Math.round(((Double.parseDouble(info.area)/1000)*2)/3*1000)*0.001 ); //农田面积
			
				new LoadFarmUserTask().execute();	
			}
		}
	
	private class LoadFarmUserTask extends AsyncTask<Integer,Integer,User> {
		@Override
		protected User doInBackground(Integer... arg0) {
			if(info.userId > 0){
				farmUser = UserDao.getInstance().searchById(info.userId);
			}
			return null;
		}
	}
	
	
	private void InitTextView(){
		
		//tvWarn = (TextView)mMenuView.findViewById(R.id.tvWarn);
		tvAddetail =  (TextView)mMenuView.findViewById(R.id.tvADetails);
		tvWeather =  (TextView)mMenuView.findViewById(R.id.tvWeather);
		tvInfo = (TextView)mMenuView.findViewById(R.id.tvInfo);		
		tvProduct =  (TextView)mMenuView.findViewById(R.id.tvProduct);
		tvAgrinfo =  (TextView)mMenuView.findViewById(R.id.tvAgrinfo);
		tvAdvisory =  (TextView)mMenuView.findViewById(R.id.tvAdvisory);
		
		
		
		tvAddetail.setOnClickListener(new MyOnClickListener(0));
		tvWeather.setOnClickListener(new MyOnClickListener(1));
		tvInfo.setOnClickListener(new MyOnClickListener(2));
		tvProduct.setOnClickListener(new MyOnClickListener(3));
		tvAgrinfo.setOnClickListener(new MyOnClickListener(4));
		tvAdvisory.setOnClickListener(new MyOnClickListener(5));
		
	}
	
	private void InitImageView() {
		cursor = (ImageView) mMenuView.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_line).getWidth();
		int[] data = Constant.getDisplay((Activity)context);
		offset = (data[0] - bmpW) / 8;
		Matrix matrix = new Matrix();
		float sx = 1.0f * data[0] / 4 / bmpW;
		matrix.postScale(sx, 1.0f);
		cursor.setImageMatrix(matrix);
	}
	
	private void InitViewPager() {
		mPager = (ViewPager) mMenuView.findViewById(R.id.id_viewpager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//listViews.add(mInflater.inflate(R.layout.farm_warn, null));
		listViews.add(mInflater.inflate(R.layout.farm_addetail, null));
		listViews.add(mInflater.inflate(R.layout.farm_weather, null));
		listViews.add(mInflater.inflate(R.layout.farm_climate, null));
		listViews.add(mInflater.inflate(R.layout.farm_product, null));
		listViews.add(mInflater.inflate(R.layout.farm_agrinfo, null));
		listViews.add(mInflater.inflate(R.layout.farm_advisory, null));
		
		
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);
		//loadFarmWarn(0);
		//this.loadWeather(0);
		this.loadAddetail(0);
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			currentView = listViews.get(arg0);
			menu = arg0 + 1;
			setContent(arg0,currentView);
			//tvWarn.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvAddetail.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvWeather.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvInfo.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvProduct.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvAgrinfo.setTextColor(context.getResources().getColor(R.color.menu_title));
			tvAdvisory.setTextColor(context.getResources().getColor(R.color.menu_title));
			
//			if (arg0 == 0) {
//				tvWarn.setTextColor(context.getResources().getColor(R.color.menu_title_selected));
//			} else 
			 if(arg0 ==0){
					tvAddetail.setTextColor(context.getResources().getColor(R.color.menu_title_selected));				
			}else if(arg0 == 1) {
				tvWeather.setTextColor(context.getResources().getColor(R.color.menu_title_selected));
			}else if(arg0 == 2){
				tvInfo.setTextColor(context.getResources().getColor(R.color.menu_title_selected));
			}else if(arg0 == 3){
				tvProduct.setTextColor(context.getResources().getColor(R.color.menu_title_selected));
			}else if(arg0 == 4){
				tvAgrinfo.setTextColor(context.getResources().getColor(R.color.menu_title_selected));
			}else if(arg0 == 5){
				tvAdvisory.setTextColor(context.getResources().getColor(R.color.menu_title_selected));				
			}
			Animation animation = null;
			animation = new TranslateAnimation(one * currIndex, (one/2) * arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	private void setContent(int menu,View view) {
		
//		if(menu == 0){
//			loadFarmWarn(menu);
//		}else 
		if(menu == 0){
			loadAddetail(0);
		}else if(menu == 1){
			//loadWeather(menu);
		}else if(menu == 2){
			loadClimate(2);
		}else if(menu == 3){
			loadSuggest(3);
		}else if(menu==4){
			loadArginfo(4);
		}else if(menu==5){
			loadAdvisory(5);
		}
		
	}
	
	private void aniShow(){
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.MATCH_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(1300);  
		//设置OneMapPupupWindow弹出窗体可点击  
		this.setFocusable(true);  
		//设置OneMapPupupWindow弹出窗体动画效果  
		this.setAnimationStyle(R.style.InfoWinAnimationFade);
		//实例化一个ColorDrawable颜色为半透明  
		ColorDrawable dw = new ColorDrawable(0xb0000000);  
		//设置SelectPicPopupWindow弹出窗体的背景  
		//this.setBackgroundDrawable(dw); 
		this.setBackgroundDrawable(new BitmapDrawable());
		
		//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				int height = mMenuView.findViewById(R.id.pup_Window_Layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}

	private PisaListView warnList;
	private void loadFarmWarn(int index){
		if(warnList == null){
			View view = listViews.get(index);
			warnList = (PisaListView)view.findViewById(R.id.lst_warn);
			Calendar c = Calendar.getInstance();
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
			String ct = sdf.format(c.getTime());
			List<FarmWarnInfo> lst = new ArrayList<FarmWarnInfo>();
			FarmWarnInfo info = new FarmWarnInfo();
			info.title = "洪水蓝色预警信号(转发)";
			info.CreateTime =ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(解除)洪水蓝色预警信号";
			info.CreateTime =ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(更新)洪水蓝色预警信号";
			info.CreateTime =ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "洪水蓝色预警信号";
			info.CreateTime = ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(解除)洪水蓝色预警信号";
			info.CreateTime =ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "洪水蓝色预警信号";
			info.CreateTime = ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(解除)洪水蓝色预警信号";
			info.CreateTime = ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(解除)(更新)洪水蓝色预警信号";
			info.CreateTime = ct;
			lst.add(info);
			info = new FarmWarnInfo();
			info.title = "(更新)洪水蓝色预警信号";
			info.CreateTime = ct;
			lst.add(info);
			FarmWarnAdapter adapter = new FarmWarnAdapter(context,lst );
			//warnList.setAdapter(adapter);
		}
	}
		
	private boolean weatherLoaded = false;
	private void loadWeather(int index){
		if(!weatherLoaded){
			FarmWeatherViewPagerItemView itemView = new FarmWeatherViewPagerItemView(context,"璧山区");
			View view = listViews.get(index);
			if(view != null){
				LinearLayout weatherPager = (LinearLayout)view.findViewById(R.id.weatherPager);
				weatherPager.addView(itemView);
				itemView.setData();
				weatherLoaded =true;
			}
		}
	}
		
	//加载气候
	private TextView tvRainYear;
	private TextView tvRainMonth;
	private TextView tvTempYear;
	private TextView tvTempMonth;
	private TextView tvSunYear;
	private TextView tvSunMonth;
	private TextView tvWindYear;
	private TextView tvWindMonth;
	private void loadClimate(int index){
		View view = listViews.get(index);
		tvRainYear = (TextView)view.findViewById(R.id.tv_rain_year);
		tvRainMonth = (TextView)view.findViewById(R.id.tv_rain_month);
		tvTempYear = (TextView)view.findViewById(R.id.tv_temp_year);
		tvTempMonth = (TextView)view.findViewById(R.id.tv_temp_month);
		tvSunYear = (TextView)view.findViewById(R.id.tv_sun_year);
		tvSunMonth = (TextView)view.findViewById(R.id.tv_sun_month);
		tvWindYear = (TextView)view.findViewById(R.id.tv_wind_year);
		tvWindMonth = (TextView)view.findViewById(R.id.tv_wind_month);
		String areaCode = info.code;
		if(areaCode.length()>=6){
			areaCode = areaCode.substring(0,6);
		}
		else{
			areaCode = areaCode.substring(0,2);
		}
		Calendar calendar = Calendar.getInstance();    
		int month = calendar.MONTH+1;
		
		new LoadClimateTask(areaCode, month).execute();
	}
	
	class LoadClimateTask extends AsyncTask<Integer, Integer, List<Climate>>{

		private String areaCode;
		private Integer month;
		public LoadClimateTask(String areaCode,Integer month){
			this.areaCode = areaCode;
			this.month = month;
		}
		
		@Override
		protected List<Climate> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			return OneMapDao.getFarmClimates(areaCode,month);
		}
		@Override
		protected void onPostExecute(List<Climate> result) {
			//climateType:降水：1、温度：2、日照：3、风：4

			Climate c = null;
			for(int i=0;i<result.size();i++){
				c = result.get(i);
				if(c.getClimateType().equals("1")){
					tvRainYear.setText(c.getYear_value());
					tvRainMonth.setText(c.getMonth_value());
				}else if(c.getClimateType().equals("2")){
					tvTempYear.setText(c.getYear_value());
					tvTempMonth.setText(c.getMonth_value());
				}else if(c.getClimateType().equals("3")){
					tvSunYear.setText(c.getYear_value());
					tvSunMonth.setText(c.getMonth_value());
				}else if(c.getClimateType().equals("4")){
					tvWindYear.setText(c.getYear_value());
					tvWindMonth.setText(c.getMonth_value());
				}
			}
		}
		
	}
	
	
	//加载   建议（包括专业产品和专家产品） 
	private PisaListView listSuggest;
	private FarmSuggestAdapter farmSugguestAdapter;
	private TextView tv_nosuggest;
	private void loadSuggest(int index){
		View view = listViews.get(index);
		listSuggest = (PisaListView)view.findViewById(R.id.lst_suggest);
		tv_nosuggest =(TextView)view.findViewById(R.id.tv_nosuggest);
		new LoadSuggestTask(info.id).execute();
	}
	
	class LoadSuggestTask extends AsyncTask<Integer, Integer, List<ProductPoint>>{

		private int farmid;
		public LoadSuggestTask(int farmid){
			this.farmid = farmid;
		}
		@Override
		protected List<ProductPoint> doInBackground(Integer... arg0) {
			Calendar c = Calendar.getInstance();
			//加载最近三天的服务产品
			String stardate = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+(c.get(Calendar.DATE)-3);
			String enddate = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DATE);
			String starttime = stardate +" 00:00:00";
			String endtime = enddate +" 23:59:59";
			return OneMapDao.getProductPointByTimeSpan(starttime, endtime, farmid, PlaceManager.getCurrentArea().areacode);
		}
		@Override
		protected void onPostExecute(List<ProductPoint> result) {
			if(result.size() == 0){
				tv_nosuggest.setVisibility(View.VISIBLE);
			}else{
				tv_nosuggest.setVisibility(View.GONE);
			}
			farmSugguestAdapter = new FarmSuggestAdapter(context, result);
			listSuggest.setAdapter(farmSugguestAdapter);
		}	
	}
	
	class LoadLatestProductTask extends AsyncTask<Integer, Integer, List<ProductPoint>> {

		private String lon;
		private String lat;
		private String cropid;
		
		public LoadLatestProductTask(String lon,String lat,String cropid){
			this.lon = lon;
			this.lat = lat;
			this.cropid = cropid;
		}
		
		@Override
		protected List<ProductPoint> doInBackground(Integer... arg0) {
			return ProductDao.getLatestFarmProduct(lon, lat, cropid);
		}
		
		@Override
		protected void onPostExecute(List<ProductPoint> result) {
		//	List<ProductPoint> resultNew = new ArrayList<ProductPoint>();
			int diffHour = 1;
			for(int i=0;i<result.size();i++){
				Date date = new Date();//获取当前时间    
				Calendar calendar = Calendar.getInstance();    
				calendar.setTime(date);    
				calendar.add(Calendar.HOUR, -diffHour);
				calendar.getTime();
				result.get(i).setProductTime(calendar.getTime().toLocaleString());
				
				String title = result.get(i).getProductTitle();
				result.get(i).setProductTitle(title);
				
				result.get(i).setType(i);
				String content = result.get(i).getContent();
				if(content.indexOf(":") != -1){
					content = content.split(":")[1];
					result.get(i).setContent(content);
				}
				
				diffHour+=8;
				
			}
			farmSugguestAdapter = new FarmSuggestAdapter(context,result);
			listSuggest.setAdapter(farmSugguestAdapter);
		}	
		
	}
	
	
	//加载当前田块作物相关的农情
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private CropsCache mCropsCache;
	private FarmSituationAdapter mAdapter;
	private LinkedList<AgrInfo> mAgrInfos;
	private Button btnSendArginfo;
	private TextView tv_noarginfo;
	private CropDao mCropDao;
	private User mUser;
	private UserDao mUserDao;
	private void loadArginfo(int index){
		View view = listViews.get(index);
		//new LoadOnlyFarm(this.info.id).execute();
		mPullListView = (PullToRefreshListView) view .findViewById(R.id.refreshable_view);
		tv_noarginfo = (TextView)view.findViewById(R.id.tv_noarginfo);
		btnSendArginfo = (Button)view.findViewById(R.id.btn_send);
		mListView = mPullListView.getRefreshableView();
		mListView.setDivider(new ColorDrawable(Color.parseColor("#dcdcdc"))); 
		mListView.setDividerHeight(CommonUtil.dip2px(context, 1));
		mListView.setCacheColorHint(Color.parseColor("#00000000"));
		mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(false);
        
        mCropsCache = new CropsCache();
        mAgrInfos =  new LinkedList<AgrInfo>();
        mCropDao = new CropDao();
        
        mUserDao = UserDao.getInstance();
        mUser = mUserDao.get();
        
        if(farmUser != null){
//        	//加载农情列表
//        	List<AgrInfo> lst = mCropsCache.getCopsList(farmUser.id);
//            if(lst.size() == 0){
//            	tv_noarginfo.setVisibility(View.VISIBLE);
//            }else{
//            	tv_noarginfo.setVisibility(View.GONE);
//            	//mAgrInfos.addAll(lst);
//        		mAdapter = new FarmSituationAdapter(context, mAgrInfos);
//        		
//        		/*if ((mAllAgrInfos==null)||(mAllAgrInfos.size()<mMaxPageSize)) {
//        			mMoreView.setVisibility(View.INVISIBLE);
//        		}*/
//        		mListView.setAdapter(mAdapter);
//        		mAdapter.setListView(mListView);	
//            }
        	new LoadNewDataTaskByCity(mUser.id).execute();
        } else{
        	tv_noarginfo.setVisibility(View.VISIBLE);
        }
        
        //按钮 跳转农情上报
        if(UserDao.getInstance().get()!= null) {
        	btnSendArginfo.setVisibility(View.VISIBLE);
        	btnSendArginfo.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub   				
    				Intent it = new Intent(context, CropUploadActivity.class);
    				it.putExtra("key","farm");
    				it.putExtra("farmname","farm");
    				it.putExtra("croptype", info.cropTypeName);
    				it.putExtra("cropvarietyname", info.cropVarietyName);
    				context.startActivity(it);
    			}
    		});
        }else{
        	btnSendArginfo.setVisibility(View.GONE);
        }
	}
	
     private class LoadNewDataTaskByCity extends AsyncTask<String, Integer, List<AgrInfo>> {
		private int userid;
		private LoadNewDataTaskByCity(int userID){
			this.userid = userID;
		}
		
		protected void onPreExecute() {
		}

		@Override
		protected List<AgrInfo> doInBackground(String... params) {
			return mCropDao.getAgrInfos(userid, 100, 1);
		}

		@Override
		protected void onPostExecute(List<AgrInfo> result) {
			if(result.size() !=0){
				tv_noarginfo.setVisibility(View.GONE);
				mAgrInfos.addAll(result);
        		mAdapter = new FarmSituationAdapter(context, mAgrInfos);
        		mListView.setAdapter(mAdapter);
        		mAdapter.setListView(mListView);	
			} else {
				tv_noarginfo.setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	/*加载农田相关咨询，与 农田作物相关的咨询问题*/
	private AdvQueryDao mAdvQueryDao;
	private int mAdvPageIndex =1;
	private int mAdvMaxPageSize=10;
	private AdvMaxNumDao mAdvNumDao;
	private int advMaxDateNum;
	private int mAdvMaxPageNum;
	private FarmAdvisoryQuestionAdapter mAdvAdapter;
	private PisaListView mAdvListView;
	private Button btn_send;
	private TextView tv_noadvisory;
	private void loadAdvisory(int index){
		
		mAdvQueryDao = new AdvQueryDao();
		mAdvNumDao = new AdvMaxNumDao();
		new LoadMaxDataNumTask().execute();
		 View view = listViews.get(index);
		 btn_send = (Button)view.findViewById(R.id.btn_send);
		 if(UserDao.getInstance().get()!= null){
			 btn_send.setVisibility(View.VISIBLE);
			 btn_send.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent it = new Intent(context,AdvisoryAskActivity.class);
						context.startActivity(it);
					}
					 
				 });
		 }else{
			 btn_send.setVisibility(View.GONE);
		 }
		mAdvListView =(PisaListView)view.findViewById(R.id.lst_advsory);
		tv_noadvisory = (TextView)view.findViewById(R.id.tv_noadvisory);
		//List<AdvisoryInfo> result = new ArrayList<AdvisoryInfo>();
	}
	
	
	/*加载与农田详情有关的数据*/
	private TextView Farm_name, Workstation_apply, Region, Type, Variety, Lng, Lat, Height, FarmArea; //Region_code
	private void loadAddetail(int index) {
		View view = listViews.get(index);
		mFarmDao = new FarmDao();
		
		Farm_name = (TextView) view.findViewById(R.id.et_farm_name);
		Workstation_apply = (TextView) view.findViewById(R.id.btn_workstation_apply);
		Region = (TextView) view.findViewById(R.id.btn_region);
		//Region_code = (TextView) view.findViewById(R.id.btn_region_code);
		Type = (TextView) view.findViewById(R.id.btn_type);
		Variety = (TextView) view.findViewById(R.id.btn_variety);
		Lng = (TextView) view.findViewById(R.id.et_lng);
		Lat = (TextView) view.findViewById(R.id.et_lat);
		Height = (TextView) view.findViewById(R.id.et_height);
		FarmArea = (TextView) view.findViewById(R.id.et_area);
				
		new LoadFarmTask(this.info.id).execute();
	}
	
	
	
	
	class LoadAdvDataTask extends AsyncTask<Integer, Integer, List<AdvisoryInfo>>{

		@Override
		protected List<AdvisoryInfo> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			if(farmUser != null){
				//User user = UserDao.getInstance().searchById(info.userId);
				return mAdvQueryDao.getAdvInfoByPage(farmUser.areaCode,Constant.ADV_TYPE_MY, farmUser.id, "", farmUser.isExpert, (mAdvPageIndex-1)*mAdvMaxPageSize,mAdvMaxPageSize);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<AdvisoryInfo> result) {
			mAdvPageIndex++;
			if(mAdvAdapter == null) 
				mAdvAdapter = new FarmAdvisoryQuestionAdapter(context,result);
			mAdvListView.setAdapter(mAdvAdapter);
		}	
		
	}
	
	class LoadMaxDataNumTask extends AsyncTask<Integer, Integer, Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			int num = 0;
			if(farmUser != null){
				num = mAdvNumDao.getMaxNum(farmUser.areaCode,Constant.ADV_TYPE_MY, farmUser.id, "",farmUser.isExpert);
				if ((num!=-1)&&(num!=0)) {
					advMaxDateNum = num;
					System.out.println("MaxDateNum=="+advMaxDateNum);
					mAdvMaxPageNum = advMaxDateNum/mAdvMaxPageSize +1;
				}
			}
			return num;
		}
		@Override
		protected void onPostExecute(Integer result) {
			if(result == 0){
				tv_noadvisory.setVisibility(View.VISIBLE);
			}else{
				tv_noadvisory.setVisibility(View.GONE);
				new LoadAdvDataTask().execute();
			}
			
		}	
	  }
}
