/* 
 * Copyright 2013 Share.Ltd  mAllProducts rights reserved.
 * 
 * @ProductActivity.java - 2014-3-21 下午2:35:02
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.BaseProductListAdapter;
import com.supermap.pisaclient.adapter.CategoryAdapter;
import com.supermap.pisaclient.adapter.ExpertProductListAdapter;
import com.supermap.pisaclient.adapter.ProductListAdapter;
import com.supermap.pisaclient.biz.BaseProductDao;
import com.supermap.pisaclient.biz.CategoryDao;
import com.supermap.pisaclient.biz.ExpertProductDao;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CategoryDialog;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.ProgressWebView;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.BaseProduct;
import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.User;

/**
 * @author Administrator
 * 
 */
public class ProductActivity extends BaseActivity implements OnClickListener {

	private ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private TextView t1, t2, t3, t4;// 菜单栏选项
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int count = 0;
	private int lastItem;
	private int max = 1000;
	private Product mOneProduct = null;

	private ProductDao mDao;
	private ExpertProductDao mExpertDao;
	private BaseProductDao mBaseDao;
	private CategoryDialog mDialog;
	private List<CategoryItem> mProfCategoryData = null;
	private List<CategoryItem> mCategoryData = null;
	private ListView mProducts;
	private ListView mProfProducts;
	private ListView mExpProfProducts;
	private ListView mBaseProfProducts;
	private LinearLayout mLlNo1;
	private ImageView mIvNo1;
	private LinearLayout mLlNoData;
	private TextView mTvNo1;
	private static final int CHANGE = 102;
	private List<Product> mAllProducts = new ArrayList<Product>();
	private ProductListAdapter mAdapter;
	private BaseProductListAdapter mBaseProductAdapter;
	private ExpertProductListAdapter mExpertProductAdapter;
	private CustomProgressDialog mPdDialog;
	private CategoryAdapter mCustomBaseAdapter;
	private int menu = 1;
	private int mPageSize = 6;
	private int mPageIndex = 1;
	private String mProfCategoryType = "0";
	private String mCategoryType = "1";
	private int mUserType = 1; // 1 普通 2 vip
	private String mUserId = "";
	private CategoryDao mCategoryDao;
	private long firstTime = 0;
	private boolean isFirstProduct = false;
	private boolean isFirstProProduct = false;
	private boolean isFirstExportProduct = false;
	private boolean isFirstBaseProduct = false;
	private View currentView = null;
	private boolean isLoadProf = false;
	private boolean isLoadProduct = false;
	private CityDao mCityDao;
	private String mAreaCode = "500234";

	private ProgressWebView mMonitorWebView = null;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHANGE:
				if (msg.arg2 == 1) {
					mAllProducts.clear();
					CommonUtil.CURRENT_PROF.clear();
					mProfCategoryType = mProfCategoryData.get(msg.arg1).CategoryID
							+ "";
				} else if (menu == 2) {
					mCategoryType = mCategoryData.get(msg.arg1).CategoryID + "";
				}
				refresh(0);
				mDialog.hide();
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(ProductActivity.this).close();
		// if (mAdapter != null) {
		// ImageLoader imageLoader = mAdapter.getImageLoader();
		// if (imageLoader != null) {
		// imageLoader.clearCache();
		// }
		// }
		super.onDestroy();
	}

	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		t4 = (TextView) findViewById(R.id.text4);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.product_pro_list, null));
		listViews.add(mInflater.inflate(R.layout.product_policy_list, null));
		listViews.add(mInflater.inflate(R.layout.product_expert_list, null));
		listViews.add(mInflater.inflate(R.layout.product_base_list,null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.menu_line).getWidth();
		int[] data = Constant.getDisplay(this);
		offset = (data[0] - bmpW) / 8;
		Matrix matrix = new Matrix();
		float sx = 1.0f * data[0] / 4 / bmpW;
		matrix.postScale(sx, 1.0f);
		cursor.setImageMatrix(matrix);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		LocalHelper.getInstance(ProductActivity.this).init();

		setTvTitle(Utils.getString(this, R.string.product_title));
		setRgNavigator(R.id.rad_product);
		setIsMenu(false);
		setIsBack(true);
		inflater(R.layout.product_list);

		mCategoryDao = new CategoryDao(this);
		mCityDao = new CityDao(this);
		User user = UserDao.getInstance().get();
		if (user != null && user.id != 0) {
			mUserId = user.id + "";
			mAreaCode = user.areaCode + "";// add by trq
			mUserType = 2;
			setIsMenu(true);
		} else {
			mUserId = "";
			mUserType = 1;
			setIsMenu(false);// 如果用户名为空，则不显示精细化咨询的菜单栏
		}

		InitImageView();
		InitTextView();
		InitViewPager();
		setMenuOnClickListener(ProductActivity.this);

		new LoadProfCategory().execute();// 精细化资讯
		new LoadCategory().execute();// 公众资讯
		mExpertDao = new ExpertProductDao(this);
		mBaseDao = new BaseProductDao(this);
		currentView = listViews.get(0);
		bindList();
		setContent(currentView);
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

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Product product = mCustomBaseAdapter.getObject(position);
			if (product != null) {
				Intent intent = new Intent(ProductActivity.this,
						ProductDetailActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putInt("type", 3);
				mBundle.putParcelable("product", product);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
		}

	}


	private void setContent(View view) {
		mPdDialog = CommonUtil.createProgressDialog(ProductActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		mDao = new ProductDao(this);
		if (mProducts != null)
			mProducts.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (lastItem == count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						if (view.getLastVisiblePosition() == view.getCount() - 1) {
							mPageIndex++;
							if (mPageIndex < max) {
								refresh(1);
							}
						}
					}

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					lastItem = firstVisibleItem + visibleItemCount;
				}

			});
		if (mProfProducts != null)
			mProfProducts.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (lastItem == count
							&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
						if (view.getLastVisiblePosition() == view.getCount() - 1) {
							mPageIndex++;
							if (mPageIndex < max) {
								refresh(1);
							}
						}
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					lastItem = firstVisibleItem + visibleItemCount;
				}

			});
		mLlNoData = (LinearLayout) view.findViewById(R.id.ll_no_data);
		mLlNo1 = (LinearLayout) view.findViewById(R.id.ll_no1);
		mIvNo1 = (ImageView) view.findViewById(R.id.iv_no1);
		mTvNo1 = (TextView) view.findViewById(R.id.tv_no1);
		if (!isFirstProduct) {
			isFirstProduct = true;
			refresh(1);
		} else if (!isFirstProProduct) {
			isFirstProProduct = true;
			refresh(0);
		} else if (!isFirstExportProduct) {
			isFirstExportProduct = true;
			refresh(2);
		} else if (!isFirstBaseProduct) {
			isFirstBaseProduct = true;
			refresh(3);
		}
	}

	private void bindList() {
		if (currentView != null) {
			if (mProducts == null)
				mProducts = (ListView) currentView
						.findViewById(R.id.lvProducts);
			if (mProfProducts == null)
				mProfProducts = (ListView) currentView
						.findViewById(R.id.lvProfProducts);
			if (mExpProfProducts == null)
				mExpProfProducts = (ListView) currentView.findViewById(R.id.lvExpertProduct);
			if (mBaseProfProducts == null)
				mBaseProfProducts = (ListView) currentView
						.findViewById(R.id.lvBaseProduct);
		}
	}

	private void refresh(int change) {
		if (change == 0) {
			max = 1000;
			mPageIndex = 1;
		}
		bindList();
		if (menu == 1)// 精细化产品
			new LoadProfTask().execute();
		else if (menu == 2)// 公众咨询
			new LoadTask().execute();
		else if (menu == 3)
			new LoadExpTask().execute();
		else if(menu == 4)
			new LoadBaseProductTask().execute();
	}

	// 沉睡线程，等待其他线程进行完再进入
	private void waitTime() {
		while (mProfCategoryData == null) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {

			}
		}
		while (mCategoryData == null) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 精细化资讯任务的线程沉睡
	 */
	private void personalTaskThread() {
		while (mProfCategoryData == null) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 公众资讯任务的线程沉睡
	 */
	private void publicTaskThread() {
		while (mCategoryData == null) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
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
			if (arg0 == 0) {
				t1.setTextColor(getResources().getColor(R.color.menu_title_selected));
				t2.setTextColor(getResources().getColor(R.color.menu_title));
				t3.setTextColor(getResources().getColor(R.color.menu_title));
				 t4.setTextColor(getResources().getColor(R.color.menu_title));
				if (!mUserId.equals("") && !mAreaCode.equals("")) {
					setIsMenu(true);
				} else {
					setIsMenu(false);
				}
			} else if (arg0 == 1) {
				t1.setTextColor(getResources().getColor(R.color.menu_title));
				t2.setTextColor(getResources().getColor(R.color.menu_title_selected));
				t3.setTextColor(getResources().getColor(R.color.menu_title));
				t4.setTextColor(getResources().getColor(R.color.menu_title));
				setIsMenu(true);
			} else if (arg0 == 2) {
				t1.setTextColor(getResources().getColor(R.color.menu_title));
				t2.setTextColor(getResources().getColor(R.color.menu_title));
				t3.setTextColor(getResources().getColor(R.color.menu_title_selected));
				t4.setTextColor(getResources().getColor(R.color.menu_title));
				setIsMenu(false);

			} else if (arg0 == 3) {
				t1.setTextColor(getResources().getColor(R.color.menu_title));
				t2.setTextColor(getResources().getColor(R.color.menu_title));
				t3.setTextColor(getResources().getColor(R.color.menu_title));
				t4.setTextColor(getResources().getColor(R.color.menu_title_selected));
				setIsMenu(false);

				/*
				 * mMonitorWebView=(ProgressWebView)
				 * currentView.findViewById(R.id.webViewMonitor); new
				 * LoadMonitorAreaName(mMonitorWebView,mAreaCode).execute();
				 */
			}
			Animation animation = null;
			animation = new TranslateAnimation(one * currIndex, (one / 2) * arg0, 0, 0);
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

	@SuppressLint("SetJavaScriptEnabled")
	private void loadMonitorProduct(ProgressWebView webView, String areaName) {
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setAllowFileAccess(true);

		webView.loadUrl("http://183.230.183.18:8088/MobileMonitor/monitor.html?areaname=" + areaName);
	}

	@SuppressWarnings("unused")
	private class LoadMonitorAreaName extends AsyncTask<Integer, Integer, String> {

		private String areaCode;
		private ProgressWebView webView;

		public LoadMonitorAreaName(ProgressWebView webView, String areaCode) {
			this.areaCode = areaCode;
			this.webView = webView;
		}

		@Override
		protected String doInBackground(Integer... params) {
			return ProductDao.getMonitorAreaName(this.areaCode.substring(0, 6));
		}

		@Override
		protected void onPostExecute(String result) {
			loadMonitorProduct(webView, result);
		}
	}

	private class LoadProfCategory extends AsyncTask<Integer, Integer, List<CategoryItem>> {
		@Override
		protected List<CategoryItem> doInBackground(Integer... params) {
			// return mCategoryDao.getAllCategoryItems(1);
			String areaCode = "";
			if (!mUserId.equals("") && !mAreaCode.equals("")) {
				areaCode = mAreaCode.substring(0, 6);
				return mCategoryDao.getAllCategoryItems(1, areaCode);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryItem> result) {
			if (result != null) {
				if (result.size() > 0) {
					mProfCategoryData = result;
					// mProfCategoryType = result.get(0).CategoryID + "";
				}
			}
		}

	}

	private class LoadCategory extends
			AsyncTask<Integer, Integer, List<CategoryItem>> {

		@Override
		protected List<CategoryItem> doInBackground(Integer... params) {
			return mCategoryDao.getAllCategoryItems(2);
		}

		@Override
		protected void onPostExecute(List<CategoryItem> result) {
			if (result != null) {
				mCategoryData = result;
				mCategoryType = result.get(1).CategoryID + "";
			}
		}

	}

	private class LoadExpTask extends AsyncTask<Integer, Integer, List<ExpertProduct>> {
		@Override
		protected List<ExpertProduct> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			User user = UserDao.getInstance().get();
			return mExpertDao.getAll(user.id);
			// mExpertProductAdapter
		}
   
		@Override
		protected void onPostExecute(List<ExpertProduct> result) {
			if (result != null && result.size() > 0) {
				if (mExpertProductAdapter == null) {
					mExpertProductAdapter = new ExpertProductListAdapter(
							ProductActivity.this, result);
					mExpProfProducts.setAdapter(mExpertProductAdapter);
				} else {
					mExpertProductAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	class LoadBaseProductTask  extends AsyncTask<Integer, Integer, List<BaseProduct>>{

		@Override
		protected List<BaseProduct> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			User user = UserDao.getInstance().get();
			return mBaseDao.getAll(user.id);
			// mExpertProductAdapter
		
		}

		@Override
		protected void onPostExecute(List<BaseProduct> result) {
			if (result != null && result.size() > 0) {
				if (mBaseProductAdapter == null) {
					mBaseProductAdapter = new BaseProductListAdapter(
							ProductActivity.this, result);
					mBaseProfProducts.setAdapter(mBaseProductAdapter);
				} else {
					mBaseProductAdapter.notifyDataSetChanged();
				}
			}
		}
		
	}
	
	
	private class LoadProfTask extends AsyncTask<Integer, Integer, List<Product>> {

		@Override
		protected List<Product> doInBackground(Integer... params) {
			if (CommonUtil.CURRENT_PROF.size() != 0 && !isLoadProf) {
				return CommonUtil.CURRENT_PROF;
			} else {
				// String city =
				// LocalHelper.getInstance(ProductActivity.this).getCity();
				// mAreaCode = mCityDao.queryAreacode(city);
				// return mDao.getAll(menu, mUserType, mPageSize, mPageIndex,
				// mProfCategoryType, mUserId,mAreaCode);
				return mDao.getAll(menu, mUserType, mPageSize, mPageIndex,
						mProfCategoryType, mUserId);
				// return mDao.getAll(menu, mUserType, mPageSize, mPageIndex,
				// "3", mUserId);
			}
		}

		@Override
		protected void onPostExecute(List<Product> result) {
			if (result != null && result.size() > 0) {
				if (isLoadProf || CommonUtil.CURRENT_PROF.size() == 0) {
					CommonUtil.CURRENT_PROF.addAll(result);
				}
				List<Product> data = new ArrayList<Product>();
				data.addAll(CommonUtil.CURRENT_PROF);
				isLoadProf = true;
				int size = data.size();
				mOneProduct = data.get(0);
				if (mOneProduct != null) {
					if (mTvNo1 != null) {
						if (mOneProduct.BigUrl != null)
							new ImageLoader(ProductActivity.this).DisplayImage(
									CommonImageUtil
											.getImageUrl(mOneProduct.BigUrl),
									mIvNo1, false);
						else
							mIvNo1.setVisibility(View.GONE);
						if (mOneProduct.ProductTitle != null)
							mTvNo1.setText(mOneProduct.ProductTitle);
						mLlNo1.setOnClickListener(ProductActivity.this);
						mIvNo1.setOnClickListener(ProductActivity.this);
					}
				}
				if (mTvNo1 != null) {
					// mTvNo1.setVisibility(View.VISIBLE);
					// mIvNo1.setVisibility(View.VISIBLE);
				}
				// data.remove(0);
				if (result.size() > 0) {
					count = data.size();
					addList(mAllProducts, data);
					CommonUtil.CURRENT_PROF.addAll(mAllProducts);
					if (mAdapter == null) {
						mAdapter = new ProductListAdapter(ProductActivity.this,
								mAllProducts, mUserType);
						mProfProducts.setAdapter(mAdapter);
					} else {
						mAdapter.notifyDataSetChanged();
					}
				}
				if (size < mPageSize) {
					max = mPageIndex - 1;
				}
				mProfProducts.setVisibility(View.VISIBLE);
				mLlNoData.setVisibility(View.GONE);
			} else {
				mProfProducts.setVisibility(View.GONE);
				mLlNoData.setVisibility(View.VISIBLE);
				mTvNo1.setVisibility(View.GONE);
				mIvNo1.setVisibility(View.GONE);
				if (mAdapter != null)
					mAdapter.notifyDataSetChanged();
			}

			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing())
				mPdDialog.show();
		}

	}

	private void addList(List<Product> products, List<Product> result) {
		if (result != null && result.size() > 0) {
			for (Product p : result) {
				if (!isExists(products, p)) {
					products.add(p);
				}
			}
		}
	}

	private boolean isExists(List<Product> products, Product product) {
		for (Product p : products) {
			if (p.ProductID == product.ProductID) {
				return true;
			}
		}
		return false;
	}

	private class LoadTask extends AsyncTask<Integer, Integer, List<Category>> {

		@Override
		protected List<Category> doInBackground(Integer... params) {
			System.out.println(isLoadProduct);
			if (CommonUtil.CURRENT_CATEGORY.size() != 0 && !isLoadProduct) {
				return CommonUtil.CURRENT_CATEGORY;
			} else {
				String areaCode = "";
				if (!mUserId.equals("") && !mAreaCode.equals("")) {
					areaCode = mAreaCode.substring(0, 6);
				} else {
					// 用户ID或者区域编码任何一个为空都选择查询重庆市的数据
					areaCode = "150000";
				}
				// return mCategoryDao.getProductCategories(mCategoryType);
				return mCategoryDao.getProductCategories(mCategoryType,areaCode);
			}

		}

		@Override
		protected void onPostExecute(List<Category> result) {
			if (result != null && result.size() > 0) {
				if (isLoadProduct) {
					CommonUtil.CURRENT_CATEGORY.clear();
					CommonUtil.CURRENT_CATEGORY.addAll(result);
				}
				isLoadProduct = true;
				mCustomBaseAdapter = new CategoryAdapter(getBaseContext(),
						result);
				mProducts.setAdapter(mCustomBaseAdapter);
				mProducts.setOnItemClickListener(new ItemClickListener());
				mProducts.setOnScrollListener(mScrollListener);
				mLlNoData.setVisibility(View.GONE);
				mProducts.setVisibility(View.VISIBLE);
			} else {
				mProducts.setVisibility(View.GONE);
				mLlNoData.setVisibility(View.VISIBLE);
			}

			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

		@Override
		protected void onPreExecute() 
		{
			if (!mPdDialog.isShowing())
				mPdDialog.show();
		}

	}

	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				if (mAdapter != null)
					mAdapter.setFlagBusy(true);// 滑动过程中不去加载图片
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (mAdapter != null)
					mAdapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				if (mAdapter != null)
					mAdapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			if (mAdapter != null)
				mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_no1:
		case R.id.tv_no1:
		case R.id.iv_no1:
			Intent intent = new Intent(this, ProductDetailActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putInt("type", mUserType);
			mBundle.putParcelable("product", mOneProduct);
			intent.putExtras(mBundle);
			startActivity(intent);
			break;
		case R.id.ibtn_menu:
			if (mDialog != null)
				mDialog.destroy();
			// waitTime();
			if (menu == 1) {
				// personalTaskThread();
				if (mProfCategoryData.size() > 0) {
					mDialog = new CategoryDialog(ProductActivity.this,
							getScreen()[0], getMenuHeight());
					mDialog.setData(mProfCategoryData);
				} else {
					Toast.makeText(ProductActivity.this, "未获取到目录信息",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				publicTaskThread();
				mDialog = new CategoryDialog(ProductActivity.this,
						getScreen()[0], getMenuHeight());
				mDialog.setData(mCategoryData);
			}

			mDialog.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Message msg = mHandler.obtainMessage();
					msg.what = CHANGE;
					msg.arg1 = arg2;
					msg.arg2 = menu;
					mHandler.sendMessage(msg);
				}
			});
			mDialog.show();

			break;
		}
		super.onClick(v);
	}

	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if (keyCode ==
	 * KeyEvent.KEYCODE_BACK) { long secondTime = System.currentTimeMillis(); if
	 * (secondTime - firstTime > 2000) {
	 * CommonUtil.showToask(ProductActivity.this,
	 * getResources().getString(R.string.exit_wait)); firstTime = secondTime;
	 * return true; } else { ExitApplication.getInstance().exit(0);
	 * System.exit(0); } } return super.onKeyDown(keyCode, event); }
	 */

}
