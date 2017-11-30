/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ProductDetailActivity.java - 2014-3-26 上午11:09:11
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CommentAdapter;
import com.supermap.pisaclient.biz.CategoryDao;
import com.supermap.pisaclient.biz.PraiseDao;
import com.supermap.pisaclient.biz.ProductCommentDao;
import com.supermap.pisaclient.biz.ProductDetailDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CategoryDialog;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.ProgressWebView;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProductComment;
import com.supermap.pisaclient.entity.ProductDetail;
import com.supermap.pisaclient.entity.User;

public class ProductWebActivity extends BaseActivity implements OnClickListener {

	private View mContent;
	private ProgressWebView mMonitorWebView = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.product_detail));
		
		setIsBack(true);
		setIsRefresh(true);

		setBackOnClickListener(this);
		setMenuOnClickListener(this);
		setRefreshOnClickListener(this);
		mContent = inflater(R.layout.product_monitor_list);
		mMonitorWebView  = (ProgressWebView)mContent.findViewById(R.id.webViewMonitor);
		
		Intent it = this.getIntent();
		String folder = it.getStringExtra("folder");
		String productid = it.getStringExtra("productid");
		loadMonitorProduct(mMonitorWebView,folder,productid);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void loadMonitorProduct(ProgressWebView webView,String folder,String productId) {
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.loadUrl("http://218.62.41.108:8020/PWebService/html/"+ folder +"/"+ productId +".html");
	}

}
