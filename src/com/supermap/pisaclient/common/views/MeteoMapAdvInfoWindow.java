package com.supermap.pisaclient.common.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryQuestionAdapter;
import com.supermap.pisaclient.adapter.CropSituationAdapter;
import com.supermap.pisaclient.adapter.FarmAdvisoryQuestionAdapter;
import com.supermap.pisaclient.adapter.FarmSituationAdapter;
import com.supermap.pisaclient.adapter.FarmSuggestAdapter;
//import com.supermap.pisaclient.adapter.FarmWarnAdapter;
import com.supermap.pisaclient.biz.AdvMaxNumDao;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.CropsCache;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.entity.AdvInfo;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmSuggest;
import com.supermap.pisaclient.entity.FarmWarnInfo;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.pullrefresh.PullToRefreshListView;
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

public class MeteoMapAdvInfoWindow extends PopupWindow implements OnClickListener
{
	private MeteoMapAdvInfoWindow oThis = this;
	private View mMenuView;
	private ProgressWebView mMonitorWebView=null;
	private ImageView ib_close;
	private int infoid;
	private TextView tvFarmName;
	private FarmDao mFarmDao;
	private Context context;

	@SuppressWarnings("deprecation")
	public MeteoMapAdvInfoWindow(Context context,int infoid)
	{
		super(context);
		this.infoid = infoid;
		this.context = context;
		this.mFarmDao = new FarmDao();
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.advinfo, null);
	
		ib_close = (ImageView) mMenuView.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);
		aniShow();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ib_close){
			oThis.dismiss();
		}
	}
	
	
	private class LoadAdvInfoTask extends AsyncTask<Integer,Integer,AdvInfo>{
		

		@Override
		protected AdvInfo doInBackground(Integer... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(AdvInfo result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
	
	private void aniShow(){
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.FILL_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(LayoutParams.WRAP_CONTENT);  
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
