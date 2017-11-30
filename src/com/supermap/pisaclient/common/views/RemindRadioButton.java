package com.supermap.pisaclient.common.views;

import com.supermap.pisaclient.common.Constant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RemindRadioButton extends RadioButton {
	
	private int h;
	private int w;
	private int x;//文字中间位子
	private int y;
	private int r;//文字背景圆半径
	private int num = 0;

	public RemindRadioButton(Context context) {
		super(context);
	}
	public RemindRadioButton(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
    }
	@Override
	protected void onDraw(Canvas canvas) {
		h = this.getMeasuredHeight();
		w = this.getMeasuredWidth();
		x = (int) (w*0.75);
		y = (int) (h*0.25);	
		r = (int) (w*0.3);
		if(num!=Constant.NO_REMIND_FLAG){
			//画红色背景
			Paint paint = new Paint();
			paint.reset();
		    paint.setStyle(Paint.Style.FILL); //实心
		    paint.setColor(Color.RED); 
		    paint.setAntiAlias(true);//去掉锯齿效果
		    paint.setStrokeWidth(4); 
		    canvas.drawCircle(x, y, r, paint);
		    //画白色数字
			paint.reset();
		    paint.setStyle(Paint.Style.FILL); //实心
		    paint.setColor(Color.WHITE);    
		    paint.setStrokeWidth(4);
		    paint.setTextSize(25);
		    if (num>=10) {
		    	canvas.drawText(9+"+", x-8,  y+10, paint);
			}
		    else {
				 canvas.drawText(num+"", x-8,  y+10, paint);
			}
		  
		}
		
		
		super.onDraw(canvas);
	}
	
	public void drawNum(int num){
		this.num = num;
		this.invalidate();
	}

}
