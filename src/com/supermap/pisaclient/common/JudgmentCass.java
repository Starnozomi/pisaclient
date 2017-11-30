package com.supermap.pisaclient.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

public class JudgmentCass {

	public static boolean IsChinese(String text)
	{
		Pattern p =Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(text);   		
		return m.matches();//如果是汉字(设置长度)
	}
	
	public static int DipToPixels(Context context, int dip) {  
	    final float SCALE = context.getResources().getDisplayMetrics().density;  
	    float valueDips = dip;  
	    int valuePixels = (int) (valueDips * SCALE + 0.5f);  
	    return valuePixels;  
	  
	} 

	public static int CharTodp(String text)
	{
		Paint pFont = new Paint();  
		Rect rect = new Rect();  
		pFont.getTextBounds(text, 0, 1, rect);  
		return rect.width();
	}
}
