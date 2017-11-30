/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ProductDetailActivity.java - 2014-3-26 上午11:09:11
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
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
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProductComment;
import com.supermap.pisaclient.entity.ProductDetail;
import com.supermap.pisaclient.entity.User;

public class ProductDetailActivity extends BaseActivity implements OnClickListener {

	private View mContent;
	private CategoryDialog mDialog;
	private List<CategoryItem> mData = null;
	private CategoryDao mCategoryDao;
	private TextView mTvTitle;
	private TextView mTvCreateTime;
	private TextView mTvSource;
	private WebView mWvInfo;
	private TextView mTvInfo;
	private TextView mTvCollects;
	private TextView mTvComments;
	private EditText mEtComment;
	private Button mBtnComment;
	private ProductDetailDao mDao;
	private PraiseDao mPraiseDao;
	private CustomProgressDialog mPdDialog;
	private int menu = 1;
	private String userId = "";
	private int type = 0;
	private Intent intent;
	private Product product;
	private ListView lvComments;
	private CommentAdapter adapter;
	private int count = 0;
	private int lastItem;
	private int max = 0;
	private int pageSize = 5;
	private int pageIndex = 1;
	private List<ProductComment> commentList = new ArrayList<ProductComment>();
	private ProductCommentDao dao = null;
	private ProductDetail detail = null;
	private ImageView mIvHeart;
	private ImageView mIvTitle;
	private ImageLoader mImageLoader;
	private TextView mTvComment;
	private Button mBtnMoreComment;

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

		intent = this.getIntent();
		mDao = new ProductDetailDao();
		mCategoryDao = new CategoryDao(this);
		mPraiseDao = new PraiseDao();
		mImageLoader = new ImageLoader(this);

		mData = mCategoryDao.getAllCategoryItems(menu);
		mContent = inflater(R.layout.product_detail);

		mPdDialog = CustomProgressDialog.createDialog(ProductDetailActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		mTvTitle = (TextView) mContent.findViewById(R.id.tv_title);
		mTvCreateTime = (TextView) mContent.findViewById(R.id.tv_create_time);
		mTvSource = (TextView) mContent.findViewById(R.id.tv_source);
		mWvInfo = (WebView) mContent.findViewById(R.id.wv_info);
		mTvInfo = (TextView) mContent.findViewById(R.id.tv_info);
		mTvCollects = (TextView) mContent.findViewById(R.id.tv_collects);
		mTvComments = (TextView) mContent.findViewById(R.id.tv_comments);
		mIvTitle = (ImageView) mContent.findViewById(R.id.iv_title);
		mIvHeart = (ImageView) mContent.findViewById(R.id.tv_heart);
		mIvHeart.setOnClickListener(this);
		mTvComment = (TextView) mContent.findViewById(R.id.tv_comment);
		lvComments = (ListView) mContent.findViewById(R.id.lvComments);
		mBtnMoreComment = (Button) mContent.findViewById(R.id.btn_more_comment);
		mBtnMoreComment.setOnClickListener(this);
		lvComments.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem == count && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if (pageIndex < max) {
							pageIndex++;
							refresh();
						}
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}

		});
		mEtComment = (EditText) mContent.findViewById(R.id.et_comment);
		mEtComment.setHint("暂不支持表情符号");
		mBtnComment = (Button) mContent.findViewById(R.id.btnComment);
		mBtnComment.setOnClickListener(this);

		dao = new ProductCommentDao();

		type = intent.getIntExtra("type", 0);
		product = intent.getParcelableExtra("product");
		mTvCollects.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		loadProduct();
		super.onResume();
	}

	private void loadProduct() {
		User user = UserDao.getInstance().get();
		if (user != null && user.id != 0) {
			userId = user.id + "";
		} else {
			userId = "";
		}
		loadDetail();
	}

	private void loadDetail() {
		new LoadDetail().execute();
	}

	private class LoadDetail extends AsyncTask<Integer, Integer, ProductDetail> {

		@Override
		protected ProductDetail doInBackground(Integer... params) {
			return mDao.getById(type, product.ProductID, userId, pageSize, pageIndex);
		}

		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing())
				mPdDialog.show();
		}

		@Override
		protected void onPostExecute(ProductDetail result) {
			if (result != null) {
				detail = result;
				if (detail != null) {
					mTvTitle.setText(product.ProductTitle);
					mTvCreateTime.setText(product.CreateTime);
					mTvSource.setText(product.Source);
					System.out.println("产品图片地址 "+product.BigUrl+"detail:"+detail.productImage);
					switch (type) {
					case 1: // 公众
						mImageLoader.DisplayImage(CommonImageUtil.getImageUrl(detail.productImage), mIvTitle, false);
						mTvInfo.setText(detail.publicInfo);
						mTvInfo.setVisibility(View.VISIBLE);
						break;
					case 2: // Vip
						mImageLoader.DisplayImage(CommonImageUtil.getImageUrl(detail.productImage), mIvTitle, false);
						mTvInfo.setText(detail.publicInfo);
						mTvInfo.setVisibility(View.VISIBLE);
						break;
					case 3: // 决策
						
						
						//mWvInfo.getSettings().setBlockNetworkImage(true);
						mWvInfo.getSettings().setJavaScriptEnabled(true);
						mWvInfo.getSettings().setDomStorageEnabled(true);
						//mWvInfo.getSettings().setTextSize(TextSize.NORMAL);
						mWvInfo.getSettings().setUseWideViewPort(true); 
						mWvInfo.getSettings().setLoadWithOverviewMode(true); 
						//mWvInfo.getSettings().setSupportZoom(true);      //支持缩放
						mWvInfo.getSettings().setBuiltInZoomControls(true);   //支持多点触控缩放
						mWvInfo.getSettings().setDisplayZoomControls(false);   //隐藏webview中缩放按钮  
						mWvInfo.getSettings().setUseWideViewPort(true); 
						mWvInfo.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
						
						
						mWvInfo.loadUrl(CommonImageUtil.getImageUrl(detail.htmlProductURL));
						System.out.println(CommonImageUtil.getImageUrl(detail.htmlProductURL));
						mWvInfo.setVisibility(View.VISIBLE);
						break;
					}
					mWvInfo.setWebViewClient(new WebViewClient(){

						@Override
						public void onScaleChanged(WebView view, float oldScale, float newScale) {
							// TODO Auto-generated method stub
							super.onScaleChanged(view, oldScale, newScale);
						}

						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							// view.loadUrl(url);
							return true;
						}

						@Override
						public void onPageFinished(WebView view, String url) {

						}

					    });
					mTvCollects.setText(detail.praiseCount + "");
					mTvComments.setText(detail.commentCnt + "");
					if (detail != null) {
						int count = detail.commentCnt;
						max = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
					}
					if (max > 1) {
						mBtnMoreComment.setVisibility(View.VISIBLE);
					}
					commentList.clear();
					pageIndex = 1;
					refresh();
				}
			}
			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

	}

	private void refresh() {
		new LoadComment().execute();
	}

	private class LoadComment extends AsyncTask<Integer, Integer, List<ProductComment>> {

		@Override
		protected List<ProductComment> doInBackground(Integer... params) {
			return dao.getAllComments(type, product.ProductID, pageSize, pageIndex);
		}

		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing())
				mPdDialog.show();
		}

		@Override
		protected void onPostExecute(List<ProductComment> result) {
			if (result != null && result.size() > 0) {
				if (commentList.size() == 0) {
					commentList = result;
					adapter = new CommentAdapter(ProductDetailActivity.this, commentList);
					lvComments.setAdapter(adapter);
					CommonUtil.setListViewHeightBasedOnChildren(lvComments);
				} else {
					commentList.addAll(result);
					adapter.notifyDataSetChanged();
					CommonUtil.setListViewHeightBasedOnChildren(lvComments);
				}
				count = commentList.size();
				lvComments.setVisibility(View.VISIBLE);
				mTvComment.setVisibility(View.GONE);
				mBtnMoreComment.setVisibility(View.VISIBLE);
			} else {
				lvComments.setVisibility(View.GONE);
				mTvComment.setVisibility(View.VISIBLE);
			}
			if (pageIndex >= max) {
				mBtnMoreComment.setVisibility(View.GONE);
			}
			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_more_comment:
			mBtnMoreComment.setEnabled(false);
			if (pageIndex < max) {
				pageIndex++;
				refresh();
			}
			mBtnMoreComment.setEnabled(true);
			break;
		case R.id.btnComment:
			/**
			 * TODO:评论和赞 的网络操作放到异步线程中去
			 */
			mBtnComment.setEnabled(false);
			String comment = mEtComment.getText().toString().trim();
			if ("".equals(comment)) {
				mEtComment.setHint(getResources().getString(R.string.no_comment));
			} else {
				if (dao.addComment(type, product.ProductID, userId, comment)) {
					mEtComment.setText("");
					mEtComment.setHint(getResources().getString(R.string.enter_comment));
					loadDetail();
					CommonUtil.showToask(ProductDetailActivity.this, getResources().getString(R.string.comment_success));
				}
			}
			mBtnComment.setEnabled(true);
			break;
		case R.id.tv_heart:
		case R.id.tv_collects:
			mTvCollects.setEnabled(false);
			if (UserDao.getInstance().get() == null) {
				mTvCollects.setEnabled(true);
				CommonDialog.setLoginDialog(this);
			} else {
				int count = mPraiseDao.getPraiseCount(type, product.ProductID, userId);
				if (count==-1) {
					CommonUtil.showToask(this, getResources().getString(R.string.praise_fail));
				}
				if (count >= Constant.PRAISE_COUNT) {
					CommonUtil.showToask(this, getResources().getString(R.string.praise_invalid));
				} else {
					if (mPraiseDao.addPraise(type, product.ProductID, userId)) {
						CommonUtil.showToask(ProductDetailActivity.this, getResources().getString(R.string.praise_success));
						if (detail != null) {
							detail.praiseCount++;
							mTvCollects.setText(detail.praiseCount + "");
						}
					}
				}
			}
			mTvCollects.setEnabled(true);
			break;
		case R.id.ibtn_back:
			finish();
			break;
		case R.id.ibtn_refresh:
			loadProduct();
			break;
		case R.id.ibtn_menu:
			mDialog = new CategoryDialog(ProductDetailActivity.this, 1000, -128);
			mDialog.setData(mData);
			mDialog.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				}

			});
			mDialog.show();
			break;
		}
		super.onClick(v);
	}
}
