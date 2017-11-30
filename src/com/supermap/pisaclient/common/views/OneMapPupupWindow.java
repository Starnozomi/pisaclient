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

public class OneMapPupupWindow extends PopupWindow 
{
	private View mMenuView;
	private Button mCancel;
	private Button mOK;
	
	private CheckBox mCBVipUser;
	private CheckBox mCBFarm;
	private CheckBox mCBSituation;
	private CheckBox mCBTemp;
	private CheckBox mCBWater;
	private CheckBox mCBSq;
	
	private boolean[] mCheckedArray;
	
	@SuppressWarnings("deprecation")
	public OneMapPupupWindow(Activity context,OnClickListener okOnClickListener)
	{
		super(context);
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.one_map_layers_menu, null);
		
		mOK=(Button) mMenuView.findViewById(R.id.btn_OK);
		mCancel=(Button) mMenuView.findViewById(R.id.btn_Cancel);
		
		mCBVipUser=(CheckBox)mMenuView.findViewById(R.id.cb_Vip_User);
		mCBFarm=(CheckBox)mMenuView.findViewById(R.id.cb_Farm);
		mCBSituation=(CheckBox)mMenuView.findViewById(R.id.cb_Situation);
		mCBTemp=(CheckBox)mMenuView.findViewById(R.id.cb_temp);
		mCBWater=(CheckBox)mMenuView.findViewById(R.id.cb_water);
		mCBSq=(CheckBox)mMenuView.findViewById(R.id.cb_sq);
		
		mCBVipUser.setOnClickListener(clickListener);
		mCBFarm.setOnClickListener(clickListener);
		mCBSituation.setOnClickListener(clickListener);
		mCBTemp.setOnClickListener(clickListener);
		mCBWater.setOnClickListener(clickListener);
		mCBSq.setOnClickListener(clickListener);
		
		mOK.setOnClickListener(okOnClickListener);
		mCancel.setOnClickListener(clickListener);
		
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
				int height = mMenuView.findViewById(R.id.pup_Window_Layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}
	
	OnClickListener clickListener=new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(mCheckedArray==null)
			{
				mCheckedArray=new boolean[6];
			}
			
			switch(v.getId())
			{
				case R.id.cb_Vip_User:
					mCheckedArray[0]=mCBVipUser.isChecked();
					break;
				case R.id.cb_Farm:
					mCheckedArray[1]=mCBFarm.isChecked();
					break;
				case R.id.cb_Situation:
					mCheckedArray[2]=mCBSituation.isChecked();
					break;
				case R.id.cb_temp:
					mCheckedArray[3]=mCBTemp.isChecked();
					checkBoxStateControl(3);
					break;
				case R.id.cb_water:
					mCheckedArray[4]=mCBWater.isChecked();
					checkBoxStateControl(4);
					break;
				case R.id.cb_sq:
					mCheckedArray[5]=mCBSq.isChecked();
					checkBoxStateControl(5);
					break;
				case R.id.btn_Cancel:
					dismiss();  
					break;
			}
		}
	};

	private void checkBoxStateControl(int index){
		if(index == 3){
			mCBWater.setChecked(false);
			mCBSq.setChecked(false);
		}else if(index == 4){
			mCBTemp.setChecked(false);
			mCBSq.setChecked(false);
		}else if(index == 5){
			mCBTemp.setChecked(false);
			mCBWater.setChecked(false);
		}
		mCheckedArray[3] = mCBTemp.isChecked();
		mCheckedArray[4] = mCBWater.isChecked();
		mCheckedArray[5] = mCBSq.isChecked();
	}
	
	public boolean[] getCheckedArray() 
	{
		return mCheckedArray;
	}

	/**
	 * 设置哪些选项被选中
	 * @param mCheckedArray
	 */
	public void setCheckedArray(boolean[] mCheckedArray) 
	{
		this.mCheckedArray = mCheckedArray;
		
		this.mCBVipUser.setChecked(mCheckedArray[0]);
		this.mCBFarm.setChecked(mCheckedArray[1]);
		this.mCBSituation.setChecked(mCheckedArray[2]);
		this.mCBTemp.setChecked(mCheckedArray[3]);
		this.mCBWater.setChecked(mCheckedArray[4]);
		this.mCBSq.setChecked(mCheckedArray[5]);
	}
}
