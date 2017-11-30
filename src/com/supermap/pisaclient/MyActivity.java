package com.supermap.pisaclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ToastUtils;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.MeteoMapAdvInfoWindow;
import com.supermap.pisaclient.common.views.MeteoMapFarmWindow;
import com.supermap.pisaclient.common.views.MeteoMapFarmWindowOfFarm;
import com.supermap.pisaclient.entity.AdvInfo;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.MarkerExtraInfo;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.service.PlaceManager;
import com.supermap.pisaclient.ui.BaseActivity;
import com.supermap.pisaclient.ui.FarmActivity;
import com.supermap.pisaclient.ui.MyAgricultureActivity;
import com.supermap.pisaclient.ui.SituationActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyActivity extends BaseActivity {

	private MyActivity oThis = this;
	private View mContent;
	Intent intent;
	private ListView my_list;
	private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private int[] icon = { R.drawable.set_farm_manager_ic_selector, R.drawable.one_map_selector, R.drawable.set_feedback_up_bg, R.drawable.my};
	private String[] iconName = { "我的农田", "我的农情", "我的咨询", "我的定制" };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.my));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.activity_my);
		
		initview();
	}
	
	public boolean checkNet() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
			return false;
		}
		return true;
	}
	
	private List<Farm> myFarms = new ArrayList<Farm>();
	private Farm selectFarm = null;
	private void initview() {
		
		my_list = (ListView) findViewById(R.id.my_list);
		my_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0: //我的农田
					User u = UserDao.getInstance().get();
			        if(u != null && u.isSave){
			        	new UserFarmTask(u.id).execute();
			        } else {  //用户农田判定为空
			        	ToastUtils.showToast(MyActivity.this, "您暂无农田");
			        }
					break;
                case 1: //我的农情
                	if (checkNet()) {
                		intent = new Intent(MyActivity.this, MyAgricultureActivity.class);
                    	startActivity(intent);
        			}
					break;
                case 2: //我的咨询
                	intent = new Intent(MyActivity.this, MyadvisoryActivity.class);
                	startActivity(intent);
					break;
                case 3: //我的定制
					intent = new Intent(MyActivity.this, CommissionActivity.class);
					startActivity(intent);
					break;	
				}
				//startActivity(intent);
			}
		});
		
		data_list = new ArrayList<Map<String, Object>>();
        
        getData();   //获取数据
        
        String [] from ={ "image","text"};
        int [] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.my_listview_item, from, to);
        //配置适配器
        my_list.setAdapter(sim_adapter);
	}
	
	 public List<Map<String, Object>> getData() {        
	        for(int i=0;i<iconName.length;i++){
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("image", icon[i]);
	            map.put("text", iconName[i]);
	            data_list.add(map);
	        }
	        return data_list;
	    }
	 
	 private FarmDao mFarmDao = new FarmDao();
	 private View view;
	 private Farm f1 = new Farm();
	 /**
	  *  用户农田加载 
	  */
	 private class UserFarmTask extends AsyncTask<Integer,Integer,List<Farm>> {
			int userid;
			public UserFarmTask(int userid) {
				this.userid = userid;
			}
			@Override
			protected List<Farm> doInBackground(Integer... params) {
				return mFarmDao.getFarmByUserId(userid);
			}
			
			@Override
			protected void onPostExecute(List<Farm> farms) {
				   myFarms = farms;
		    	   if(farms.size() > 0) { 
		    		   String [] farmNameArray = new String[myFarms.size()];
					   Integer [] farmIdArray = new Integer[myFarms.size()];
		    		   if(selectFarm == null) {
	    				   selectFarm = myFarms.get(0);
	    			   }		   
						for(int i=0;i<myFarms.size();i++){
							farmIdArray[i] = myFarms.get(i).id;
							farmNameArray[i] = myFarms.get(i).descript;
						}
						Dialog alertDialog = new AlertDialog.Builder(MyActivity.this).   
		                setTitle("您的农田")  
		                .setItems(farmNameArray, new DialogInterface.OnClickListener() {   
		                    @Override   
		                    public void onClick(DialogInterface dialog, int which) {   
		                        selectFarm = myFarms.get(which);
		                    	for(Farm f : myFarms) {
		 		    			   if(f.id == selectFarm.id) {
		 		    			   f1 = f;
		 		    			   continue;
		 		    			   }
		 		    		   }
		            			view = oThis.getWindow().getDecorView();
		            			MeteoMapFarmWindowOfFarm farmWindow = new MeteoMapFarmWindowOfFarm((Activity)oThis, f1);
		            			if(!farmWindow.isShowing()) { //已经显示的情况下就不再显示
									farmWindow.showAtLocation(view, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
								}
		                    }   
		                }).create();   
		        		alertDialog.show();
		    	   } else {
		    		   if(myFarms.size() == 0) return;
		    	   }
			}
		}
}
