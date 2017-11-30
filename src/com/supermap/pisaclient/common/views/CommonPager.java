package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CommonPager extends ViewPager {
	/**
	 * 是否可以滑动
	 */
	private boolean enabled = true;
	
	public CommonPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }
	 
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
