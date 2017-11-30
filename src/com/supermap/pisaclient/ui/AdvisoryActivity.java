/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @AdvisoryActivity.java - 2014-3-21 下午2:38:22
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryTypeAdapter;
import com.supermap.pisaclient.biz.AdvQueryDao;
import com.supermap.pisaclient.biz.SubjectDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ToastUtils;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CommonPager;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.User;

public class AdvisoryActivity extends BaseActivity implements OnItemClickListener {

	private View mContent;
	private View mAakView;
	private View mAnswerView;
	private EditText mEtSearch;
	private ImageButton mBtnAskAnswer;
	private GridView mGvTypes;
	private AdvisoryTypeAdapter mAdapter;
	private CommonPager mPager;
	private List<View> listViews;
	private ImageButton mAsk;
	private ImageButton mAnswer;
	private FrameLayout mSerch;
	private boolean isPaging = true;
	private boolean isAsk = true;
	private List<Subject> mParentSubjectList = new ArrayList<Subject>();
	private HashMap<Integer, List<Subject>> mSubjectMap = new HashMap<Integer, List<Subject>>();
	private User mUser = null;
	private String mSerchContent;
	private long firstTime = 0;
	Intent intent;
	private ImageButton	mAdv_ask;
	private Button		mAdv_hot;
	private Button		mAdv_new;
	private Button		mAdv_my;
	String aaaa;
	

	private void InitViewPager() {
		mUser = UserDao.getInstance().get();
		mPager = (CommonPager) mContent.findViewById(R.id.vp_ad_ask_and_answer);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		mAakView = mInflater.inflate(R.layout.advisory_ask_layout, null);
		mAnswerView = mInflater.inflate(R.layout.advisory_answer_layout, null);
		listViews.add(mAakView);
		listViews.add(mAnswerView);
		mAsk = (ImageButton) listViews.get(0).findViewById(R.id.ib_ask_left);
		mAnswer = (ImageButton) listViews.get(1).findViewById(R.id.ib_answer_right);
		mAsk.setOnClickListener(this);
		mAnswer.setOnClickListener(this);
		mPager.setAdapter(new MyPagerAdapter());
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		if ((mUser == null) || (!mUser.isExpert)) {
			mPager.setCurrentItem(0);
			mPager.setPagingEnabled(false);
		} else {
			mPager.setCurrentItem(0);
			mAakView.findViewById(R.id.bn_ask_right).setVisibility(View.VISIBLE);
			mPager.setPagingEnabled(true);
		}

	}

	public void initSubjectSelectView() {
		mAdapter = new AdvisoryTypeAdapter(this, mParentSubjectList);
		mGvTypes.setAdapter(mAdapter);
		mGvTypes.setOnItemClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.advisory_title));
		setRgNavigator(R.id.rad_advisory);
		setIsBack(true);
		mContent = inflater(R.layout.advisory_main);

		intent = getIntent();
		aaaa = intent.getStringExtra("jugg");
		
		mEtSearch = (EditText) mContent.findViewById(R.id.et_search);
		mGvTypes = (GridView) mContent.findViewById(R.id.gv_types);
		mSerch = (FrameLayout) mContent.findViewById(R.id.fl_advisory_serch);
		mSerch.setOnClickListener(this);
		
		mAdv_ask = (ImageButton) mContent.findViewById(R.id.adv_ask);
		mAdv_hot = (Button) mContent.findViewById(R.id.adv_hot);
		mAdv_new = (Button) mContent.findViewById(R.id.adv_new);
		mAdv_my = (Button) mContent.findViewById(R.id.adv_my);
		mUser = UserDao.getInstance().get();
		if ((mUser!=null)&&(mUser.isExpert)) {
			mAdv_ask.setBackgroundResource(R.drawable.adv_answer_selector);
		}
		
		mAdv_ask.setOnClickListener(this);
		mAdv_hot.setOnClickListener(this);
		mAdv_new.setOnClickListener(this);
		mAdv_my.setOnClickListener(this);

//		InitViewPager();
		new LoadSubjectTask().execute();
//		new LoadDataTask().execute();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.adv_hot:
			startAdvisoryQuestionList(null, Constant.ADV_TYPE_HOT);
			break;
		case R.id.adv_new:
			startAdvisoryQuestionList(null, Constant.ADV_TYPE_NEW);
			break;
		case R.id.adv_my:
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				startAdvisoryQuestionList(null, Constant.ADV_TYPE_MY);
			}
			break;
		case R.id.adv_ask:
			if (UserDao.getInstance().get() == null) {
				CommonDialog.setLoginDialog(this);
			} else {
				if (mUser.isExpert) {//专家显示跳转到最新咨询
					startAdvisoryQuestionList(null, Constant.ADV_TYPE_NEW);
				}
				else {
					startAdvisoryAsk(null);
				}
			}
			break;
		case R.id.ib_answer_right:
			startAdvisoryQuestionList(null, Constant.ADV_TYPE_NO);
			break;
		case R.id.fl_advisory_serch:
			mSerchContent = mEtSearch.getText().toString();
			if ("".equals(mSerchContent)) {
				CommonUtil.showToask(this, "搜索内容不能为空");
				break;
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
			imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0); // 强制隐藏键盘
			startAdvisoryQuestionList(null, Constant.ADV_TYPE_SERCH);
		default:
			break;
		}
		super.onClick(v);
	}

	/**
	 * 既可以根据学科分类预览问题 也可以根据热门，最新，我的分类预览
	 * 
	 * @param subject
	 * @param type
	 *        -1:不分类预览 0:按学科分类 1：热门分类 2：最新分类 3：我的分类 4:根据内容搜索
	 */
	private void startAdvisoryQuestionList(Subject subject, int type) {
		Intent intent = new Intent();
		if (type == 0) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("subject", subject);
			intent.putExtras(bundle);
		}
		if (type == 4) {
			intent.putExtra("content", mSerchContent);
		}
		intent.putExtra("type", type);
		aaaa = aaaa;
		intent.putExtra("jugg", aaaa);
		intent.setClass(this, AdvisoryQuestionActivity.class);
		startActivity(intent);
	}

	/**
	 * 根据subject 分类不同提问
	 * @param list
	 */
	private void startAdvisoryAsk(List<Subject> list) {

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		if ((list == null) || (list.size() == 0)) {
			bundle.putParcelableArrayList("subjects", null);
		} else {
			bundle.putParcelableArrayList("subjects", (ArrayList<? extends Parcelable>) list);
		}
		intent.putExtras(bundle);
		intent.setClass(this, AdvisoryAskActivity.class);
		startActivity(intent);
	}

	private class MyPagerAdapter extends PagerAdapter {

		public MyPagerAdapter() {

		}

		@Override
		public int getCount() {
			return listViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(listViews.get(position));
			return listViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == (arg1);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (UserDao.getInstance().get() == null) {
			CommonDialog.setLoginDialog(this);
			return;
		}
		if (isAsk) {
			startAdvisoryAsk(mSubjectMap.get(mParentSubjectList.get(position).subjectId));
		} else {
			startAdvisoryQuestionList(mParentSubjectList.get(position), 0);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			if (position == 0) {
				isAsk = true;
			} else {
				isAsk = false;
			}
		}
	}

	private class LoadSubjectTask extends AsyncTask<String, Integer, List<Subject>> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<Subject> doInBackground(String... arg0) {

			SubjectDao subjectDao = new SubjectDao();
			mParentSubjectList = subjectDao.getSubjects(0);
			if (mParentSubjectList == null) {
				return null;
			}
			for (Subject subject : mParentSubjectList) {
				List<Subject> tempList = null;
				tempList = subjectDao.getSubjects(subject.subjectId);
				mSubjectMap.put(subject.subjectId, tempList);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Subject> result) {
//			initSubjectSelectView();
		}

	}
	


//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			long secondTime = System.currentTimeMillis();
//			if (secondTime - firstTime > 2000) {
//				CommonUtil.showToask(AdvisoryActivity.this, getResources().getString(R.string.exit_wait));
//				firstTime = secondTime;
//				return true;
//			} else {
//				ExitApplication.getInstance().exit(0);
//				System.exit(0);
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
