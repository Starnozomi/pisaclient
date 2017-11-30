package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CropInListview  extends ListView{
	public CropInListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CropInListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public CropInListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
