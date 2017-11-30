/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ScienceDetailActivity.java - 2014-7-4 上午9:39:26
 */

package com.supermap.pisaclient.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.ScienceDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.FontSharedDeal;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.entity.Science;

public class ScienceDetailActivity extends BaseActivity implements OnTouchListener {

	private View mContent;
	private ImageView mIvPic;
	private LinearLayout mLlScience;
	private TextView mTvTitle;
	private TextView mTvInfo;
	private TextView mTvStime;;
	private ScienceDao mDao;
	private ImageLoader mLoader;
	private CustomProgressDialog mPd;
	private int mId;
	private float mStart = 0;
	private float mEnd = 0;
	private int mMode = 0;
	private int mPoint = 0;
	private int mCurrentIndex = 0;
	private int mCurrentFont = -1;
	private int mLength = (Constant.MAX_FONT - Constant.MIN_FONT) / Constant.FONT_STEP;
	private int[] mFonts = new int[mLength];
	private FontSharedDeal mFsd;

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			mPoint = event.getPointerCount();
			try {
				if (mMode == 0) {
					mStart = distance(event);
					mMode = 1;
				}
				mEnd = distance(event);
				if (mPoint > 1) {
					float scale = mEnd / mStart;
					if (scale >= 1.6) {
						mCurrentIndex++;
						if (mCurrentIndex >= mLength) {
							mCurrentIndex = mLength - 1;
						}
					} else if (scale < 0.5) {
						mCurrentIndex--;
						if (mCurrentIndex <= 0) {
							mCurrentIndex = 0;
						}
					}
					mCurrentFont = mFonts[mCurrentIndex];
					mTvInfo.setTextSize(mCurrentFont);
					mFsd.setDefaultCity(mCurrentFont);
				}
			} catch (Exception e) {

			}
			break;
		case MotionEvent.ACTION_UP:
			mMode = 0;
			break;
		}
		return false;
	}

	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(dx * dx + dy * dy);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.science));
		setIsNavigator(false);
		setIsBack(true);

		mId = this.getIntent().getIntExtra("id", 1);
		mContent = inflater(R.layout.sciencedetail);
		mPd = CustomProgressDialog.createDialog(this);
		mPd.setMessage(Utils.getString(this, R.string.loading_data));
		mIvPic = (ImageView) mContent.findViewById(R.id.iv_pic);
		mTvTitle = (TextView) mContent.findViewById(R.id.tv_title);
		mTvInfo = (TextView) mContent.findViewById(R.id.tv_info);
		mLlScience = (LinearLayout) mContent.findViewById(R.id.ll_science);
		mTvStime = (TextView) mContent.findViewById(R.id.tv_stime);
		mDao = new ScienceDao();
		mLoader = new ImageLoader(this);

		for (int i = 0; i < mLength; i++) {
			mFonts[i] = Constant.MIN_FONT + i * Constant.FONT_STEP;
		}

		mFsd = new FontSharedDeal(this);
		mCurrentFont = mFsd.getDefaultFont();
		mCurrentIndex = searchIndex(mCurrentFont);
		if (mCurrentIndex == -1) {
			mCurrentIndex = 0;
		}
		mTvInfo.setTextSize(mCurrentFont);

		new LoadTask().execute(mId);
		mLlScience.setOnTouchListener(this);
	}

	private int searchIndex(int size) {
		for (int i = 0; i < mLength; i++) {
			if (size == i * Constant.FONT_STEP + Constant.MIN_FONT) {
				return i;
			}
		}
		return -1;
	}

	private class LoadTask extends AsyncTask<Integer, Integer, Science> {

		@Override
		protected Science doInBackground(Integer... params) {
			return mDao.getById(params[0]);
		}

		@Override
		protected void onPreExecute() {
			mPd.show();
		}

		@Override
		protected void onPostExecute(Science result) {
			if (result != null) {
				mLoader.DisplayImage(CommonImageUtil.getImageUrl(result.pic), mIvPic, false);
				mTvTitle.setText(result.title);
				mTvInfo.setText(result.info);
				mTvStime.setText(result.stime);
			}
			if (mPd.isShowing())
				mPd.dismiss();
		}

	}
}
