package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


public class PisaListView extends ListView {  
	  
    public PisaListView(Context context) {  
        // TODO Auto-generated method stub  
        super(context);  
    }  
  
    public PisaListView(Context context, AttributeSet attrs) {  
        // TODO Auto-generated method stub  
        super(context, attrs);  
    }  
  
    public PisaListView(Context context, AttributeSet attrs, int defStyle) {  
        // TODO Auto-generated method stub  
        super(context, attrs, defStyle);  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO Auto-generated method stub  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }  
}  