package com.supermap.pisaclient.common.views;

import java.util.ArrayList;
import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.common.fragments.FarmAdvisoryFragment;
import com.supermap.pisaclient.common.fragments.FarmAgrinfoFragment;
import com.supermap.pisaclient.common.fragments.FarmInfoFragment;
import com.supermap.pisaclient.common.fragments.FarmProductFragment;
import com.supermap.pisaclient.common.fragments.FarmWarnFragment;
import com.supermap.pisaclient.common.fragments.FarmWeatherFragment;
import com.supermap.pisaclient.common.fragments.FarmddetailFragment;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MeteoMapFarmWindowFragment extends PopupWindow implements OnClickListener
{
	private MeteoMapFarmWindowFragment oThis = this;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	private View mMenuView;
	private ImageView ib_close;
	
	//private TextView tvWarn;
	private TextView tvInfo;
	private TextView tvWeather;
	private TextView tvProduct;
	private TextView tvAgrinfo;
	private TextView tvAdvisory;
	private TextView tvAddetail;
	private Context context;
	
	private LayoutInflater inflater;
	private Farm info;
	
	@SuppressWarnings("deprecation")
	public MeteoMapFarmWindowFragment(Context context,Farm info)
	{
		super(context);
		
		this.context =context;
		this.info = info;
		inflater= LayoutInflater.from(context);
		mMenuView=inflater.inflate(R.layout.farm, null);
		mViewPager = (ViewPager) mMenuView.findViewById(R.id.id_viewpager);
		initView();
		aniShow();
		mAdapter = new FragmentPagerAdapter(((FragmentActivity)context).getSupportFragmentManager())
		{

			@Override
			public int getCount()
			{
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return mFragments.get(arg0);
			}
		};
		
		mViewPager.setAdapter(mAdapter);
		
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			private int currentIndex;

			@Override
			public void onPageSelected(int position)
			{
				resetTabBtn();
				switch (position)
				{
//				case 0:
//					tvWarn.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
//					break;
				case 0:
					tvWeather.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;
				case 1:
					tvInfo.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;
				case 2:
					tvProduct.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;
				case 3:
					tvAgrinfo.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;
				case 4:
					tvAdvisory.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;
				case 5:
					tvAddetail.setTextColor(oThis.context.getResources().getColor(R.color.menu_title_selected));
					break;	
				}
				
				currentIndex = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
	}
	
	private void resetTabBtn(){
		//重置所有TAB框
		//tvWarn.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvWeather.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvInfo.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvProduct.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvAgrinfo.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvAdvisory.setTextColor(context.getResources().getColor(R.color.menu_title));
		tvAddetail.setTextColor(context.getResources().getColor(R.color.menu_title));
	}
	
	private void initView(){
		//tvWarn = (TextView)mMenuView.findViewById(R.id.tvWarn);		
		tvWeather =  (TextView)mMenuView.findViewById(R.id.tvWeather);
		tvInfo = (TextView)mMenuView.findViewById(R.id.tvInfo);
		tvProduct =  (TextView)mMenuView.findViewById(R.id.tvProduct);
		tvAgrinfo =  (TextView)mMenuView.findViewById(R.id.tvAgrinfo);
		tvAdvisory =  (TextView)mMenuView.findViewById(R.id.tvAdvisory);
		tvAddetail =  (TextView)mMenuView.findViewById(R.id.tvADetails);
		
		FarmAdvisoryFragment farmAdvisoryFragment = new FarmAdvisoryFragment();
		FarmAgrinfoFragment farmAgrinfoFragment = new FarmAgrinfoFragment();
		FarmInfoFragment farmInfoFragment = new FarmInfoFragment();
		FarmProductFragment farmProductFragment = new FarmProductFragment();
		//FarmWarnFragment farmWarnFragment = new FarmWarnFragment();
		FarmWeatherFragment farmWeatherFragment = new FarmWeatherFragment();
		FarmddetailFragment farmddetailFragment = new FarmddetailFragment();
		
		mFragments.add(farmAdvisoryFragment);
		mFragments.add(farmAgrinfoFragment);
		mFragments.add(farmInfoFragment);
		mFragments.add(farmProductFragment);
		//mFragments.add(farmWarnFragment);
		mFragments.add(farmWeatherFragment);
		mFragments.add(farmddetailFragment);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ib_close){
			oThis.dismiss();
		}
	}
	
	private void aniShow(){
		ib_close = (ImageView) mMenuView.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);
		//设置OneMapPupupWindow的View  
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
	
}
