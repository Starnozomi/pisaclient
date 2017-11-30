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

public class MeteoMapFarmWindowOfFarm extends PopupWindow {
	private MeteoMapFarmWindowOfFarm oThis = this;
	private View mMenuView;
	private ImageView ib_close;
	private Farm info;
	private User farmUser;
	private TextView tvFarmName;
	private FarmDao mFarmDao;
	private Context context;
	
	private ViewPager mPager;
	private TextView tvAddetail;
	

	public MeteoMapFarmWindowOfFarm(Activity context,Farm info) {
		super(context);
		this.info = info;
		this.context = context;
		this.mFarmDao = new FarmDao();
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.farm_addetail, null);
			
		//设置OneMapPupupWindow的View  
        mFarmDao = new FarmDao();
		
		Farm_name = (TextView) mMenuView.findViewById(R.id.et_farm_name);
		Workstation_apply = (TextView) mMenuView.findViewById(R.id.btn_workstation_apply);
		Region = (TextView) mMenuView.findViewById(R.id.btn_region);
		Type = (TextView) mMenuView.findViewById(R.id.btn_type);
		Variety = (TextView) mMenuView.findViewById(R.id.btn_variety);
		Lng = (TextView) mMenuView.findViewById(R.id.et_lng);
		Lat = (TextView) mMenuView.findViewById(R.id.et_lat);
		Height = (TextView) mMenuView.findViewById(R.id.et_height);
		FarmArea = (TextView) mMenuView.findViewById(R.id.et_area);
				
		new LoadFarmTask(this.info.id).execute();
		aniShow();
		InitTextView();
		new LoadFarmTask(this.info.id).execute();
	}
	
//	class FirstLoadTask extends AsyncTask<Integer,Integer,Farm> {
//		private Integer farmId;
//		public FirstLoadTask(Integer farmId) {
//			this.farmId = farmId;
//		}
//		@Override
//		protected Farm doInBackground(Integer... params) {
//			return mFarmDao.getFarmAttributeByIdNew(farmId);
//		}
//		
//		@Override
//		protected void onPostExecute(Farm result) {
//			info = result;
//			new LoadFarmUserTask().execute();	
//		}
//	}
	
	//加载和显示详情数据
	private TextView Farm_name, Workstation_apply, Region, Type, Variety, Lng, Lat, Height, FarmArea; //Region_code
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
		tvAddetail =  (TextView)mMenuView.findViewById(R.id.tvADetails);
	}
	
	private void aniShow(){
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.MATCH_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(1000);  
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
				int height = mMenuView.findViewById(R.id.farm_window).getTop();
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
	
}
