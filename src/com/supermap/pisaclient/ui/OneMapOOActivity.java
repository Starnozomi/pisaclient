package com.supermap.pisaclient.ui;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.ProgressWebView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;

public class OneMapOOActivity extends BaseActivity 
{
	private View mContent=null;
	private ProgressWebView webView=null;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setIsNavigator(false);
		setIsBack(true);
		setTvTitle(Utils.getString(this, R.string.one_map_info));
		
		mContent = inflater(R.layout.one_map_oo_main);
		webView= (ProgressWebView) mContent.findViewById(R.id.webView);
		
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		
		webView.loadUrl("http://113.108.192.120:8088/AgricOneMap/baiduIndex.html");
	}
}
