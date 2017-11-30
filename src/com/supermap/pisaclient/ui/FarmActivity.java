package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.ProgressWebView;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.ui.ProductActivity.MyOnClickListener;
import com.supermap.pisaclient.ui.ProductActivity.MyOnPageChangeListener;
import com.supermap.pisaclient.ui.ProductActivity.MyPagerAdapter;

public class FarmActivity extends BaseActivity {
	
	private FarmActivity oThis = this;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.farm_title));
		setIsMenu(false);
		setIsBack(true);
		inflater(R.layout.farm);
		
		InitTextView();
		InitImageView();
		InitViewPager();
	}
	
	private void InitTextView(){
		//tvWarn = (TextView)findViewById(R.id.tvWarn);
		tvWeather =  (TextView)findViewById(R.id.tvWeather);
		tvInfo = (TextView)findViewById(R.id.tvInfo);	
		tvProduct =  (TextView)findViewById(R.id.tvProduct);
		tvAgrinfo =  (TextView)findViewById(R.id.tvAgrinfo);
		tvAdvisory =  (TextView)findViewById(R.id.tvAdvisory);
		tvAddetail =  (TextView)findViewById(R.id.tvADetails);
		
		
		//tvWarn.setOnClickListener(new MyOnClickListener(0));
		tvWeather.setOnClickListener(new MyOnClickListener(0));
		tvInfo.setOnClickListener(new MyOnClickListener(1));
		tvProduct.setOnClickListener(new MyOnClickListener(2));
		tvAgrinfo.setOnClickListener(new MyOnClickListener(3));
		tvAdvisory.setOnClickListener(new MyOnClickListener(4));
		tvAddetail.setOnClickListener(new MyOnClickListener(5));
	}
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.menu_line).getWidth();
		int[] data = Constant.getDisplay(this);
		offset = (data[0] - bmpW) / 8;
		Matrix matrix = new Matrix();
		float sx = 1.0f * data[0] / 4 / bmpW;
		matrix.postScale(sx, 1.0f);
		cursor.setImageMatrix(matrix);
	}
	
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.id_viewpager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		//listViews.add(mInflater.inflate(R.layout.farm_warn, null));
		listViews.add(mInflater.inflate(R.layout.farm_weather, null));
		listViews.add(mInflater.inflate(R.layout.farm_climate, null));
		listViews.add(mInflater.inflate(R.layout.farm_product, null));
		listViews.add(mInflater.inflate(R.layout.farm_agrinfo, null));
		listViews.add(mInflater.inflate(R.layout.farm_advisory, null));
		listViews.add(mInflater.inflate(R.layout.farm_addetail, null));
		
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);
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
			setContent(currentView);
//			if (arg0 == 0) {
//				tvWarn.setTextColor(getResources().getColor(R.color.menu_title_selected));
//				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
//				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
//				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
//				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
//				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
//				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));				
//			} else 
			if(arg0 == 0) {
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title_selected));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));
				
			}else if(arg0 == 1){
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));				
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title_selected));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));
				
			}else if(arg0 == 2){
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title_selected));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));
				
			}else if(arg0 == 3){
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title_selected));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));
				
			}else if(arg0 == 4){
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title_selected));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title));
				
			}else if(arg0 == 5){
				//tvWarn.setTextColor(getResources().getColor(R.color.menu_title));
				tvInfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvWeather.setTextColor(getResources().getColor(R.color.menu_title));
				tvProduct.setTextColor(getResources().getColor(R.color.menu_title));
				tvAgrinfo.setTextColor(getResources().getColor(R.color.menu_title));
				tvAdvisory.setTextColor(getResources().getColor(R.color.menu_title));
				tvAddetail.setTextColor(getResources().getColor(R.color.menu_title_selected));
				
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
	
	private void setContent(View view) {
		
	}
	
}
