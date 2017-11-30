package com.supermap.pisaclient.common.views;
import junit.framework.Test;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;

/**
 * 给专家打分的button
 * @author Administrator
 *
 */
public class SmallTextButton extends RelativeLayout {

    private TextView buttonTextControl;
    private Button fetchVerifyCodeButton;
    
   private float textSize ;  
   private  int textColor ; 
   private  int background ; 
   private String text;
   
    public SmallTextButton(Context context){
        super(context);
    }
    
    public SmallTextButton(Context context, AttributeSet attrs){
        super(context, attrs);
        getAttrs(context, attrs);
        init(context, "打分");
    }

    public SmallTextButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
    }
    
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.myView);
         textSize = t.getDimension(R.styleable.myView_textSize, 12);  
         textColor = t.getColor(R.styleable.myView_textColor, 0xffffffff); //白色
         background = t.getResourceId(R.styleable.myView_background, 0xff8ad044);//背景是是自定义的资源背景 
         t.recycle();
    }

    public SmallTextButton(Context context, String text) {
        super(context);
        init(context, text);
        this.setBackgroundColor(0xff4294fa);
    }

    private void init(Context context, String text) {
    	this.setBackgroundResource(R.drawable.adv_score_button_broder);
        buttonTextControl = new TextView(context);
        buttonTextControl.setBackgroundColor(0);
        buttonTextControl.setText(text);
//        buttonTextControl.setTextColor(0xffff0000);//红色
//        buttonTextControl.setTextSize(12);
        buttonTextControl.setTextColor(textColor);
        buttonTextControl.setTextSize(textSize);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        buttonTextControl.setLayoutParams(params);
        addView(buttonTextControl);

        fetchVerifyCodeButton = new Button(context);
//        Drawable drawable = ResourceUtils.createCompoundBgDrawable(context,
//                ResourceUtils.SMALL_BUTTON_RES_IDS);
        Drawable drawable = new BitmapDrawable();
        
        fetchVerifyCodeButton.setBackgroundDrawable(drawable);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.width = drawable.getIntrinsicWidth();
        params.height = drawable.getIntrinsicHeight();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        fetchVerifyCodeButton.setLayoutParams(params);
        addView(fetchVerifyCodeButton);
        buttonTextControl.bringToFront();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        fetchVerifyCodeButton.setOnClickListener(l);
    }
    
    public void setText(String text){
    	 buttonTextControl.setText(text);
    }
}
