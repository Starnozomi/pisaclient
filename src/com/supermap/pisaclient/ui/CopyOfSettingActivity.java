/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SettingActivity.java - 2014-3-21 下午2:38:50
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.supermap.pisaclient.CommissionActivity;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
//import com.supermap.pisaclient.ui.AboutappActivity.InstallTask;
import com.supermap.pisaclient.entity.User;

public class CopyOfSettingActivity extends BaseActivity {

	private View mContent;
	private User mUser;
	GridView gridView;
	Intent intent;
	int jugg = 0; //用于判定用户角色  0.大户   1.专家   2.高级领导
	private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private int[] icon = { R.drawable.set_farm_msg_ic_selector, R.drawable.set_farm_manager_ic_selector, R.drawable.set_suggest_ic_selector, 
    		               R.drawable.set_guide_ic_selector, R.drawable.one_map_selector, R.drawable.set_guide_ic_selector, 
    		               R.drawable.set_login_ic_selector, R.drawable.set_msg_ic_selector };
    private String[] iconName = { "咨询", "农田管理", "专家建议", "产品中心", "农情", "物联设备", "用户定制", "关于"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.more));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.copy_of_app_setting);
		
		gridView = (GridView) findViewById(R.id.setting_list);
        data_list = new ArrayList<Map<String, Object>>();  //新建List
        getData(); //获取数据
        
        String [] from ={"image","text"};   //新建适配器
        int [] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.girview_item, from, to);
        
        gridView.setAdapter(sim_adapter);  //配置适配器
        
//      sim_adapter.notifyDataSetChanged(); //刷新适配器
        
        User user = UserDao.getInstance().get();
		 if(user.isExpert) {
			 jugg = 1;
			 // iconName = { "咨询", "农田管理", "专家建议", "产品中心", "农情", "物联设备", "用户定制", "关于"};
		 } 
//		 else if(mUser.loginName.equals("17783199652") || mUser.loginName.equals("18996758296")) {
//			 jugg = 2;
//		 }
         gridView.setOnItemClickListener(itemClick);
	}

	 Boolean bool = false;	
	 public List<Map<String, Object>> getData() {        
	        //icon和iconName的长度是相同的，这里任选其一都可以
	        for(int i=0;i<icon.length;i++){
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("image", icon[i]);
	            map.put("text", iconName[i]);
	            data_list.add(map);
	        }
	        return data_list;
	  }
	 
	 public boolean checkNet() {
			if (!CommonUtil.checkNetState(this)) {
				CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
				return false;
			}
			return true;
	 }
	
	 private OnItemClickListener itemClick = new OnItemClickListener() {
		 
		 public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		 if(jugg == 0){
		     switch (position){
		     case 0:  //咨询
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, AdvisoryActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 1:  //农田管理
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, FarmManagerActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 2:  //产品中心
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, ProductActivity.class); 
						startActivity(intent);
					}
		    	 break;
		     case 3:  //农情
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, SituationActivity.class);
						intent.putExtra("key", "point");//farm则加载显示农田选项,point则加载显示地区选项
						startActivity(intent);
				 }
		    	 break;
		     case 4:  //用户定制
		    	 intent = new Intent(CopyOfSettingActivity.this, CommissionActivity.class); 
				 startActivity(intent);
		    	 break;
		     case 5:  //关于
		    	 if (checkNet()) {				
						intent = new Intent();
						intent.putExtra("Visibility", bool);
						intent.setClass(CopyOfSettingActivity.this, AboutappActivity.class);			
						startActivity(intent);
				 }
		    	 break;
		     }
		 }
		 if(jugg == 1){
		     switch (position){
		     case 0:  //咨询
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, AdvisoryActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 1:  //农田管理
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, FarmManagerActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 2:  //专家建议
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, SuggestActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 3:  //产品中心
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, ProductActivity.class); //原来是使用指南
						startActivity(intent);
				 }
		    	 break;
		     case 4:  //农情
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, SituationActivity.class);
						intent.putExtra("key", "point");//farm则加载显示农田选项,point则加载显示地区选项
						startActivity(intent);
				 }
		    	 break;
		     case 5:  //用户定制
		    	 intent = new Intent(CopyOfSettingActivity.this, CommissionActivity.class); //原来是使用指南
				 startActivity(intent);
		    	 break;
		     case 6:  //关于
		    	 if (checkNet()) {				
						intent = new Intent();
						intent.putExtra("Visibility", bool);
						intent.setClass(CopyOfSettingActivity.this, AboutappActivity.class);			
						startActivity(intent);
				 } 
		    	 break; 
		     }
		 }
		 if(jugg == 2){
		     switch (position){
		     case 0:  //咨询
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, AdvisoryActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 1:  //农田管理
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, FarmManagerActivity.class);
						startActivity(intent);
				 }
		    	 break;
		     case 2:  //产品中心
		    	 if (UserDao.getInstance().get() == null) {
						CommonDialog.setLoginDialog(CopyOfSettingActivity.this);
					} else {
						intent = new Intent(CopyOfSettingActivity.this, ProductActivity.class); //原来是使用指南
						startActivity(intent);
				 }
		    	 break;
		     case 3:  //农情
		    	 if (checkNet()) {
						intent = new Intent(CopyOfSettingActivity.this, SituationActivity.class);
						intent.putExtra("key", "point");//farm则加载显示农田选项,point则加载显示地区选项
						startActivity(intent);
				 }
		    	 break;
		     case 4:  //物联设备
		  
		    	 break;
		     case 5:  //用户定制
		    	 intent = new Intent(CopyOfSettingActivity.this, CommissionActivity.class); //原来是使用指南
				 startActivity(intent);
		    	 break;
		     case 6:  //关于
		    	 if (checkNet()) {				
						intent = new Intent();
						intent.putExtra("Visibility", bool);
						intent.setClass(CopyOfSettingActivity.this, AboutappActivity.class);			
						startActivity(intent);
				 } 
		    	 break; 	 
		     }
		 }
		}
	};
}
