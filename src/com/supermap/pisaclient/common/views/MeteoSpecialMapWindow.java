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
import com.supermap.pisaclient.service.PlaceManager;
import com.supermap.pisaclient.service.ProductPoint;
import com.supermap.pisaclient.service.RasterProductManager;
import com.supermap.pisaclient.service.SpecialMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MeteoSpecialMapWindow extends PopupWindow implements OnClickListener{
	private MeteoSpecialMapWindow oThis = this;
	private View mMenuView;
	private ProgressWebView mMonitorWebView=null;
	private ImageView ib_close;
	private ListView serviceProductList = null;
	private Context context = null;
	private TextView tvVipName;
	private ServiceProductListAdapter adapter;
	private BaiduMap mBaiduMap;
	private MapView mMapView;
	private List<Overlay> overLays;
	BitmapDescriptor vipPoint=BitmapDescriptorFactory.fromResource(R.drawable.product_0);
	private List<ProductPoint> vipProductPoint=null;
	private List<MarkerItem> vipProductPointsMakers=null;
	private RasterProductManager rasterProductManager= null;
	@SuppressWarnings("deprecation")
	public MeteoSpecialMapWindow(Activity context,BaiduMap mBaiduMap,MapView mMapView,RasterProductManager rasterProductManager)
	{
		super(context);
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		this.mMapView = mMapView;
		this.rasterProductManager = rasterProductManager;
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView=inflater.inflate(R.layout.meteomap_specialmap_menu, null);
		serviceProductList =  (ListView)mMenuView.findViewById(R.id.serviceProductList);
		serviceProductList.setOnItemClickListener(listener);
		ib_close = (ImageView) mMenuView.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);
		vipProductPointsMakers = new ArrayList<MarkerItem>();
		overLays = new ArrayList<Overlay>();
		
		
		
		loadProductList();
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ib_close){
			oThis.dismiss();
		}
	}
	
	
	
	private void loadProductList(){
		List<SpecialMap> products = new ArrayList<SpecialMap>();
		SpecialMap p = new SpecialMap();
		p.title  = "年平均降水量";
		p.ptype="ziliao";
		p.pname = "jiangshui";
		products.add(p);
		p = new SpecialMap();
		p.title  = "年平均温度";
		p.ptype = "ziliao";
		p.pname = "temp";
		products.add(p);
		p = new SpecialMap();
		p.title  = "年日照";
		p.ptype="ziliao";
		p.pname = "rizhao";
		products.add(p);
		p = new SpecialMap();
		p.title  = "年大于等于10摄氏度积温";
		products.add(p);
		p = new SpecialMap();
		p.title  = "年平均相对湿度";
		p.ptype="ziliao";
		p.pname = "humidity";
		products.add(p);
		p = new SpecialMap();
		p.title  = "年平均风速";
		products.add(p);
		p.ptype="ziliao";
		p.pname = "wind";
		p = new SpecialMap();
		p.title  = "年伏旱频率";
		p.ptype = "zaihai";
		p.pname = "fdrou";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "年暴雨日数";
		p.ptype = "zaihai";
		p.pname = "baoyu";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "年大于35度日数";
		p.ptype = "zaihai";
		p.pname = "over35";
		products.add(p);
		
		/*p = new SpecialMap();
		p.title  = "极端低温";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "极端高温";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "玉米气候区划";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "马铃薯候区划";
		products.add(p);
		
		p = new SpecialMap();
		p.title  = "宽皮桔气候区划";
		products.add(p);*/
		adapter = new ServiceProductListAdapter(context,products);
		serviceProductList.setAdapter(adapter);
	}
	
	
	OnItemClickListener listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			 if(id == -1) {
			        return;
			    }
			    int realPosition=(int)id;
			    SpecialMap specialMap = (SpecialMap)arg0.getItemAtPosition(realPosition);
			    if(rasterProductManager != null){
			    	rasterProductManager.showSpecialMap(specialMap.getPtype(),specialMap.getPname());
			    }
			    oThis.dismiss();
		}
	};
}
