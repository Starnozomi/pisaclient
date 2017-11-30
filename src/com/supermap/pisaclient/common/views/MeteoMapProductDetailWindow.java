package com.supermap.pisaclient.common.views;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.ServiceProductListAdapter;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmPoint;
import com.supermap.pisaclient.entity.MarkerExtraInfo;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.VipProductPoint;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.service.MarkerItem;
import com.supermap.pisaclient.service.ProductPoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MeteoMapProductDetailWindow extends PopupWindow implements OnClickListener
{
	private MeteoMapProductDetailWindow oThis = this;
	private Context context;
	private View mMenuView;
	private ImageView ib_close;
	private DetailView detailView;
	@SuppressWarnings("deprecation")
	public MeteoMapProductDetailWindow(Context context)
	{
		super(context);
		this.context = context;
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.meteomap_product_detail, null);
		ib_close = (ImageView) mMenuView.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);
		
		detailView = new DetailView();
		detailView.tvTitle =  (TextView)mMenuView.findViewById(R.id.tvTitle);
		detailView.tvContent =  (TextView)mMenuView.findViewById(R.id.tvContent);
		detailView.tvTime =  (TextView)mMenuView.findViewById(R.id.tvTime);
		detailView.tvFarmName =  (TextView)mMenuView.findViewById(R.id.tvFarmName);
		detailView.tvType =  (TextView)mMenuView.findViewById(R.id.tvType);
		
		//设置OneMapPupupWindow的View  
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
	
	public void setVlaues(ProductPoint product){
		if(detailView != null){
			detailView.tvTitle.setText(product.getProductTitle());
			TextPaint tp = detailView.tvTitle.getPaint(); 
			tp.setFakeBoldText(true);
			detailView.tvContent.setText("\u3000\u3000"+product.getContent());
			detailView.tvTime.setText(product.getProductTime());
			detailView.tvFarmName.setText(product.getFarmName());
			String ptype = "";
			if(product.getType() == 1){
				ptype = "模型产品";
			}else if(product.getType() == 2){
				ptype ="专家产品";
			}
			detailView.tvType.setText(ptype);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ib_close){
			oThis.dismiss();
		}
	}
	
	class DetailView{
		TextView tvTitle;
		TextView tvContent;
		TextView tvTime;
		TextView tvType;
		TextView tvFarmName;
	}
	
}
