/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ZoomTextView.java - 2014-8-18 上午11:26:45
 */

package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class ZoomTextView extends TextView implements OnTouchListener {

	private float textSize = 0;
	private float downSize = 0;
	private float upSize = 0;

	public ZoomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);
	}

	public ZoomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}

	public ZoomTextView(Context context) {
		super(context);
		this.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downSize = event.getSize();
			textSize = this.getTextSize();
			break;
		case MotionEvent.ACTION_UP:
			upSize = event.getSize();
			if (upSize > downSize) {
				textSize++;
			} else {
				textSize--;
			}
			this.setTextSize(textSize);
			break;
		}
		return false;
	}

}
