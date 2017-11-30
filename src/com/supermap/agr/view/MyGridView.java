package com.supermap.agr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView {
 
    public boolean hasScrollBar = true;
    /**
     * @param context
     */
    public MyGridView(Context context) {
        this(context, null);
    }
 
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
 
    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 
        int expandSpec = heightMeasureSpec;
        if (hasScrollBar) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);// ע������,�������˼��ֱ�Ӳ�����GridView�ĸ߶�
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
