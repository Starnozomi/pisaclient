/* 
 * Copyright 2013 Share.Ltd  mAllProducts rights reserved.
 * 
 * @ProductActivity.java - 2014-3-21 下午2:35:02
 */

package com.supermap.pisaclient.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.BaseProductListAdapter;
import com.supermap.pisaclient.adapter.CategoryAdapter;
import com.supermap.pisaclient.adapter.ExpertProductListAdapter;
import com.supermap.pisaclient.adapter.ProductListAdapter;
import com.supermap.pisaclient.adapter.AdvisoryQuestionAdapter.ViewHolder;
import com.supermap.pisaclient.biz.BaseProductDao;
import com.supermap.pisaclient.biz.CategoryDao;
import com.supermap.pisaclient.biz.ExpertProductDao;
import com.supermap.pisaclient.biz.ProductDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CategoryDialog;
import com.supermap.pisaclient.common.views.CropInGridview;
import com.supermap.pisaclient.common.views.CropInListview;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.ProgressWebView;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.BaseProduct;
import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.User;

/**
 * @author Administrator
 * 
 */
public class ProductNewActivity extends BaseActivity implements OnClickListener {

	private ProductNewActivity othis  = this;
	private View mContent;
	ListView lv = null;
	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(ProductNewActivity.this).close();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		LocalHelper.getInstance(ProductNewActivity.this).init();

		setTvTitle(Utils.getString(this, R.string.product_title));
		
		setIsMenu(true);
		setIsBack(true);
		mContent = inflater(R.layout.product_base_list);
		lv = (ListView)mContent.findViewById(R.id.lvBaseProduct);
		lv.setOnItemClickListener(new  OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				if(id == -1){
					
				}else{
					int realPos = (int)id;
					BaseProduct bp =  (BaseProduct)parent.getItemAtPosition(realPos);
					if(bp != null){
						Intent it = new Intent(ProductNewActivity.this,ProductWebActivity.class);
						it.putExtra("productid", bp.productid);
						it.putExtra("folder", bp.folder);
						startActivity(it);
					}
				}
				
				
				 
			}
			//http://218.62.41.108:8020/PWebService/html/201711/c34f3efd36c442bfbe5121ba30558b3a.html
		});
		new LoadBaseProductTask().execute();
	}
	
	

	 
	
	class LoadBaseProductTask  extends AsyncTask<Integer, Integer, List<BaseProduct>>{

		@Override
		protected List<BaseProduct> doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			return BaseProductDao.getInstance().getAllProduct();
		}

		@Override
		protected void onPostExecute(List<BaseProduct> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			BaseProductAdapter bpa = new BaseProductAdapter(othis,result);
			lv.setAdapter(bpa);
		}
		
	}
	
	
	
	
	
	class BaseProductAdapter extends BaseAdapter{
		private Context context;
		private List<BaseProduct> list;
		private LayoutInflater mInflater;
		
		public BaseProductAdapter(Context context, List<BaseProduct> list){
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null; 
			BaseProduct bp = list.get(position);
			convertView = mInflater.inflate(R.layout.product_base_item, null); //咨询布局
			if( null == viewHolder){
				viewHolder = new ViewHolder();
				viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
				viewHolder.tvSender = (TextView)convertView.findViewById(R.id.tv_sender);
				viewHolder.tvCreateTime = (TextView)convertView.findViewById(R.id.tv_create_time);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tvTitle.setText(bp.title);
			viewHolder.tvSender.setText(bp.sender);
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd/");
			java.util.Date dt = new Date(Long.parseLong(bp.createTime));  
			viewHolder.tvCreateTime.setText (sdf.format(dt));
			return convertView;
		}
	}

	class ViewHolder{
		public TextView tvTitle;
		public TextView tvSender;
		public TextView tvCreateTime;
	}
}
