/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @HelpActivity.java - 2014-7-3 上午10:20:52
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.ViewPagerAdapter;
import com.supermap.pisaclient.common.CommonUtil;

public class HelpActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	private static final int[] pics = { R.drawable.weather_help, R.drawable.product_help, R.drawable.advisory_login, R.drawable.advisory_ask,
			R.drawable.arg_help };

	private ImageView[] dots;

	private int currentIndex;
	private View mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setIsRlMenu(false);
		setIsDivTop(false);
		setIsDivBottom(false);
		setIsNavigator(false);
		setBackOnClickListener(this);

		mContent = inflater(R.layout.app_help);

		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setImageBitmap(CommonUtil.readBitMap(this, pics[i]));
			iv.setLayoutParams(mParams);
			views.add(iv);
		}
		vp = (ViewPager) mContent.findViewById(R.id.viewpager);
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);

		initDots();

	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) mContent.findViewById(R.id.ll);

		dots = new ImageView[pics.length];

		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);
	}

	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ibtn_back) {
			this.finish();
		}
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
		super.onClick(v);
	}
}
