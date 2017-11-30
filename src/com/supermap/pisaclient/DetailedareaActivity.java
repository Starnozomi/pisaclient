package com.supermap.pisaclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ToastUtils;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.ui.BaseActivity;
import com.supermap.pisaclient.ui.CitySetActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DetailedareaActivity extends BaseActivity {

	private View mContent;
	private TextView area_btn;
	private ListView area_list;
	private Button area_add, area_submit;
	
	private List<City> mCities;
	
	private CityDao cityDao;
	private String[] citys; 
	List<City> allCityList = new ArrayList<City>();  //所有城市列表
	List<City> listLevel_1 = new ArrayList<City>();  //一级城市列表
	List<HashMap<String, Object>> citydata;
	HashMap<String, Object> map1 = new HashMap<String, Object>();
	private SimpleAdapter adapter;
	private AreaSelectParameter mAreaSelectParameter;
	Intent intent;
	private int pos = 0; //记录被点击的item
	Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_detailedcrop);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.area_commis));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.activity_detailedarea);
		
		initview();
	}
	private void initview() {
		
		cityDao = new CityDao(DetailedareaActivity.this);
		
		allCityList = cityDao.getAllCity(2);
		
		citys = new String[allCityList.size()]; 				
		for (int i = 0; i < allCityList.size(); i++) {
			if(i == 0){
				citys[i] = "内蒙古自治区";
			} else {
			citys[i] = allCityList.get(i).name; 
			}
		}

		area_btn = (TextView) findViewById(R.id.area_btn);
		//点击显示所有城市列表(一级城市)
		area_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(DetailedareaActivity.this).setItems(citys, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						area_btn.setText(citys[which]);
					}
				}).show();
			}
		});
		
		area_list =  (ListView) findViewById(R.id.area_list);
		//定制城市列表listview
		area_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pos = position;
				 AlertDialog.Builder builder = new AlertDialog.Builder(DetailedareaActivity.this);
	                final String[] cities = { "删除" }; //指定下拉列表的显示数据
	                //    设置一个下拉的列表选择项
	                builder.setItems(cities, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which)  {
	                    	citydata.remove(pos); 
	                    	adapter.notifyDataSetChanged();
	                    }
	                });
	                builder.show();
			}
		});
		
		
		citydata = new ArrayList<HashMap<String, Object>>();
		
		adapter = new SimpleAdapter(DetailedareaActivity.this, citydata, R.layout.croplist_item, new String[] { "areaname" }, new int[] { R.id.crop_name});
		area_list.setAdapter(adapter);
		
		area_add = (Button) findViewById(R.id.area_add);
		//添加定制城市
		area_add.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {
				if("".equals(area_btn.getText().toString()) || "点击选择区域".equals(area_btn.getText().toString())){
					ToastUtils.showToast(DetailedareaActivity.this, "区域不能为空");
				}
				else{
					map1 = new HashMap<String,Object>();
					Boolean bol = true;
					for(int i = 0; i< citydata.size(); i++){
						if(citydata.get(i).get("areaname").toString().equals(area_btn.getText().toString())){
							bol = false;
						} else {
						}
					}
					if(bol) {
						map1.put("areaname", area_btn.getText().toString());
						citydata.add(map1);
						area_list.setAdapter(adapter);
						area_btn.setText("点击选择区域");
					} else {
						ToastUtils.showToast(DetailedareaActivity.this, "不可重复添加城市");
					}
				}
					
		    }
	   });
		
		//提交定制的城市列表
		area_submit = (Button) findViewById(R.id.area_submit);
		area_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
//				intent = new Intent(DetailedareaActivity.this, CommissionActivity.class);
				if(citydata.size() !=0){
					
				} else {
					ToastUtils.showToast(DetailedareaActivity.this, "您的定制内容为空，请选择后再上传");
				}
//				DetailedareaActivity.this.finish();
//				startActivity(intent);
			}
		});
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(DetailedareaActivity.this).setTitle("提示").setMessage("是否放弃当前作物定制编辑?")
			.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					intent = new Intent(DetailedareaActivity.this, CommissionActivity.class);
				    startActivity(intent);
				    DetailedareaActivity.this.finish();
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
		}
		return false;
	}
}
