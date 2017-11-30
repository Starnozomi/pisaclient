/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @HelpActivity.java - 2014-7-3 上午10:20:52
 */

package com.supermap.pisaclient.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.MaterialAdapter;
import com.supermap.pisaclient.adapter.ViewPagerAdapter;
import com.supermap.pisaclient.biz.MaterialDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.MenuDialog;
import com.supermap.pisaclient.common.views.MenuDialog2;
import com.supermap.pisaclient.entity.Material;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Zone;

public class MaterialActivity extends BaseActivity implements OnCheckedChangeListener {

	View mContent = null;
	MaterialAdapter adapter = null;
	MaterialActivity oThis= this;
	ListView materialList = null;
	MaterialDao materialDao = null;
	 MenuDialog2 mDialog;
	 List<Zone> mZones;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.material_monitor));
		setIsMenu(true);
		setIsNavigator(false);
		setIsBack(true);
		setBackOnClickListener(this);
		setMenuOnClickListener(this);
	//	setBackOnClickListener(this);
		mContent = inflater(R.layout.material_list);
		
		materialList = (ListView)mContent.findViewById(R.id.lv_material);
		materialDao = new MaterialDao();
		mDialog = new MenuDialog2(MaterialActivity.this, getScreen()[0], getMenuHeight());
		mDialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Zone z = mZones.get(arg2);
				new LoadMaterialTask(z.id).execute();
				
			}
		});
		mDialog.show();
		new LoadZoneTask().execute();
		
	}
	
	
	
	private class LoadZoneTask extends AsyncTask<String,String,List<Zone>>
	{
		@Override
		protected List<Zone> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return materialDao.getZones();
		}
		@Override
		protected void onPostExecute(List<Zone> result) {
			// TODO Auto-generated method stub
		    //super.onPostExecute(result);
			if(result.size() > 0){
				new LoadMaterialTask(result.get(0).id).execute();
			}
			mZones = result;
			mDialog.setData(result);
			
		}
	}
	
	
	/*****************************************************************
	 * 异步获取设备数据
	 * @author Administrator
	 * 
	 ******************************************************************/
	private class LoadMaterialTask extends AsyncTask<String,String,List<Material>>{

		String zone;
		public LoadMaterialTask(String zone){
			this.zone = zone;
		}
		@Override
		protected List<Material> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return materialDao.getAll(zone);
		}

		@Override
		protected void onPostExecute(List<Material> result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			mDialog.hide();
			adapter = new MaterialAdapter(oThis,result);
			materialList.setAdapter(adapter);
		}
		
	}


	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		String flag = arg1 ? "1":"0";
		String deviceId = arg0.getTag().toString();
		new SetStatusTask(deviceId,flag).execute();
	}


	class SetStatusTask extends AsyncTask<String,String,String>{

		private String deviceId;
		private String feed;
		public SetStatusTask(String deviceId,String feed){
			this.deviceId = deviceId;
			this.feed = feed;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return materialDao.doSwitch(deviceId, feed);
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(oThis, "设备状态设置成功"+result, 2000);
		}	
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.ibtn_back:
				finish();
			break;
			case R.id.ibtn_menu:
				mDialog.show();
				break;
		}
	}
	
}
