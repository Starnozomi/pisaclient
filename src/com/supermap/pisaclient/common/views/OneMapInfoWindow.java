package com.supermap.pisaclient.common.views;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.entity.AreaPoint;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;

import android.widget.PopupWindow;

public class OneMapInfoWindow extends PopupWindow 
{
	private View mMenuView;
	private ProgressWebView mMonitorWebView=null;
	
	@SuppressWarnings("deprecation")
	public OneMapInfoWindow(Activity context,AreaPoint areaPoint)
	{
		super(context);
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.product_monitor_list, null);
		mMonitorWebView=(ProgressWebView) mMenuView.findViewById(R.id.webViewMonitor);
		new LoadMonitorAreaName(mMonitorWebView,areaPoint.getCode()).execute();

		//设置OneMapPupupWindow的View  
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.FILL_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(LayoutParams.WRAP_CONTENT);  
		//设置OneMapPupupWindow弹出窗体可点击  
		this.setFocusable(true);  
		//设置OneMapPupupWindow弹出窗体动画效果  
		this.setAnimationStyle(R.style.MenuAnimationFade);
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
	
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void loadMonitorProduct(ProgressWebView webView,String areaName)
	{
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		
		webView.loadUrl("http://183.230.183.18:8088/MobileMonitor/monitor.html?areaname="+areaName);
	}
	
	private class LoadMonitorAreaName extends AsyncTask<Integer, Integer, String>
	{

		private String areaCode;
		private ProgressWebView webView;
		
		public LoadMonitorAreaName(ProgressWebView webView,String areaCode)
		{
			this.areaCode=areaCode;
			this.webView=webView;
		}
		
		@Override
		protected String doInBackground(Integer... params) 
		{
			return ProductDao.getMonitorAreaName(this.areaCode);
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			loadMonitorProduct(webView,result);
		}
	}
	
}
