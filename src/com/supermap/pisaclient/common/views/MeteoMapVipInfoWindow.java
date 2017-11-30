package com.supermap.pisaclient.common.views;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MeteoMapVipInfoWindow extends PopupWindow implements OnClickListener
{
	private MeteoMapVipInfoWindow oThis = this;
	private View mMenuView;
	private ProgressWebView mMonitorWebView=null;
	private ImageView ib_close;
	private VipUserInfo info;
	private TextView tvVipName;
	private ViewHolder viewHolder;
	
	@SuppressWarnings("deprecation")
	public MeteoMapVipInfoWindow(Activity context,VipUserInfo info)
	{
		super(context);
		this.info = info;
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.meteomap_vip_info, null);
//		tvVipName = (TextView)mMenuView.findViewById(R.id.tv_vipname);
//		tvVipName.setText(info.getName());
		
		ib_close = (ImageView) mMenuView.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);
		//设置OneMapPupupWindow的View  
		
		aniShow();
		initView();
		initData();
		
	}
	
	private void initView(){
		viewHolder = new ViewHolder();
		viewHolder.tv_vipname = (TextView)mMenuView.findViewById(R.id.tv_vipname);
		viewHolder.tvName = (TextView)mMenuView.findViewById(R.id.tvName);
		viewHolder.CountyName = (TextView)mMenuView.findViewById(R.id.CountyName);
		viewHolder.BelongsTownship = (TextView)mMenuView.findViewById(R.id.BelongsTownship);
		viewHolder.BelongsVillage = (TextView)mMenuView.findViewById(R.id.BelongsVillage);
		viewHolder.BusinessScope = (TextView)mMenuView.findViewById(R.id.BusinessScope);
		viewHolder.AreaScale =  (TextView)mMenuView.findViewById(R.id.AreaScale);
		viewHolder.AnnualProductionValue =(TextView)mMenuView.findViewById(R.id.AnnualProductionValue);
		viewHolder.ProdOperaCount =(TextView)mMenuView.findViewById(R.id.ProdOperaCount);
		viewHolder.TechCount =(TextView)mMenuView.findViewById(R.id.TechCount);
		viewHolder.TechAverageAge = (TextView)mMenuView.findViewById(R.id.TechAverageAge);
	}
	
	private void initData(){
		viewHolder.tv_vipname.setText(info.getName());
		viewHolder.tvName.setText("经营主体名称："+info.getName());
		viewHolder.CountyName.setText("所在区县："+info.getCountyName());
		viewHolder.BelongsTownship.setText("所属乡镇："+info.getBelongsTownship());
		viewHolder.BelongsVillage.setText("所属村社："+info.getBelongsVillage());
		viewHolder.BusinessScope.setText("经营范围："+info.getBusinessScope());
		viewHolder.AreaScale.setText("面积或生产规模："+info.getAreaScale());
		viewHolder.AnnualProductionValue.setText("年产值（万元）："+info.getAnnualProductionValue());
		viewHolder.ProdOperaCount.setText("从事生产经营人数："+info.getProdOperaCount());
		viewHolder.TechCount.setText("技术人员人数："+info.getTechCount());
		viewHolder.TechAverageAge.setText("技术人员平均年龄："+info.getTechAverageAge());
	}
	
	
	private class ViewHolder{
		TextView tv_vipname;
		TextView tvName;
		TextView CountyName;
		TextView BelongsTownship;
		TextView BelongsVillage;
		TextView BusinessScope;
		TextView AreaScale;
		TextView AnnualProductionValue;
		TextView ProdOperaCount;
		TextView TechCount;
		TextView TechAverageAge;
		
	}
	
	
	private class LoadVipInfoTask extends AsyncTask<Integer,Integer,VipUserInfo>{

		@Override
		protected VipUserInfo doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ib_close){
			oThis.dismiss();
		}
	}
	
	private void aniShow(){
		this.setContentView(mMenuView);  
		//设置OneMapPupupWindow弹出窗体的宽  
		this.setWidth(LayoutParams.FILL_PARENT);  
		//设置OneMapPupupWindow弹出窗体的高  
		this.setHeight(LayoutParams.WRAP_CONTENT);  
		//设置OneMapPupupWindow弹出窗体可点击  
		this.setFocusable(true);  
		//设置OneMapPupupWindow弹出窗体动画效果  
		this.setAnimationStyle(R.style.InfoWinAnimationFade);
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
	
}
