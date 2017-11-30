package com.supermap.pisaclient.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PisaGridView extends GridView{  
    public PisaGridView(Context context, AttributeSet attrs) {   
          super(context, attrs);   
      }   
     
      public PisaGridView(Context context) {   
          super(context);   
      }   
     
      public PisaGridView(Context context, AttributeSet attrs, int defStyle) {   
          super(context, attrs, defStyle);   
      }   
     
      @Override   
      public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
     
          int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,    MeasureSpec.AT_MOST);   
          super.onMeasure(widthMeasureSpec, expandSpec);   
      }   
}  