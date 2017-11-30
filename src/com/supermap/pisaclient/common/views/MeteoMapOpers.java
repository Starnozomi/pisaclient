package com.supermap.pisaclient.common.views;

import com.supermap.pisaclient.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;

public class MeteoMapOpers extends PopupWindow 
{
	private View mMenuView;
	private Button btnAddVip;
	private Button btnAddFarmland;
	private Button btnAddArgInfo;
	private Button btnAddAdvisory;
	
	
	@SuppressWarnings("deprecation")
	public MeteoMapOpers(Activity context,OnClickListener operClickListener)
	{
		super(context);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.meteomap_opers, null);
		btnAddVip = (Button)mMenuView.findViewById(R.id.btnAddVip);
		btnAddFarmland = (Button)mMenuView.findViewById(R.id.btnAddFarmland);
		btnAddArgInfo = (Button)mMenuView.findViewById(R.id.btnAddArgInfo);
		btnAddAdvisory = (Button)mMenuView.findViewById(R.id.btnAddVis);
		
		
	

		btnAddVip.setOnClickListener(operClickListener);
		btnAddFarmland.setOnClickListener(operClickListener);
		btnAddArgInfo.setOnClickListener(operClickListener);
		btnAddAdvisory.setOnClickListener(operClickListener);
		
		
		//设置OneMapPupupWindow的View  
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.FILL_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(LayoutParams.WRAP_CONTENT);  
		//设置OneMapPupupWindow弹出窗体可点击  
		this.setFocusable(true);  
		//设置OneMapPupupWindow弹出窗体动画效果  
		this.setAnimationStyle(R.style.MenuAnimationFade);
		//实例化一个ColorDrawable颜色为半透明  
		ColorDrawable dw = new ColorDrawable(0xb0000000);  
		//设置SelectPicPopupWindow弹出窗体的背景  
		//this.setBackgroundDrawable(dw); 
		this.setBackgroundDrawable(new BitmapDrawable());
		
		//mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				/*int height = mMenuView.findViewById(R.id.pup_Window_Layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				*/
				return true;
			}
		});
	}
	
	

	
}
