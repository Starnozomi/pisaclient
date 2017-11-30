/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @LineEditText.java - 2014-6-17 上午10:32:26
 */

package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {

	private Paint mPaint;

	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.GRAY);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, mPaint);
	}
}
