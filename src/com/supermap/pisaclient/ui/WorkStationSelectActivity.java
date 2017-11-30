/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SettingActivity.java - 2014-3-21 下午2:38:50
 */

package com.supermap.pisaclient.ui;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.WorkStationAdapter;
import com.supermap.pisaclient.biz.OneMapDao;
import com.supermap.pisaclient.biz.WorkStationDao;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.WorkStation;

public class WorkStationSelectActivity extends BaseActivity  {

	private View mContent;
	private ListView listView;
	private List<String> areaCodes;
	private WorkStationAdapter adapter;
	private WorkStationSelectActivity oThis   = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setTvTitle(Utils.getString(this, R.string.selectworkstation));
		//setRgNavigator(R.id.rad_more);
		setIsBack(true);
		mContent =  inflater(R.layout.workstation_select);
		Bundle bundle = getIntent().getBundleExtra("parameter");
		areaCodes = (List<String>)bundle.getSerializable("areas");
		listView = (ListView)mContent.findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
				// TODO Auto-generated method stub
				if(id == -1) {
			        return;
			    }
			    int realPosition=(int)id;
			    WorkStation ws =  (WorkStation)arg0.getItemAtPosition(realPosition);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("workstationid", ws.id);
				bundle.putString("workstationname", ws.stationName);
				intent.putExtras(bundle);
				setResult(Constant.SELECT_WORKSTATION, intent);
				finish();
			}
			
		});
		
		new WorkStationListTask(areaCodes).execute();
	}
	
	
	private class WorkStationListTask extends AsyncTask<Integer,Integer,List<WorkStation>>
	{
		private List<String> areaCode;
		public WorkStationListTask(List<String> areaCode){
			this.areaCode = areaCode;
		}
		@Override
		protected List<WorkStation> doInBackground(Integer... params) 
		{
			return WorkStationDao.getStationListByAreas(areaCode);
		}
		
		@Override
		protected void onPostExecute(List<WorkStation> result) 
		{
			adapter = new WorkStationAdapter(oThis,result);
			listView.setAdapter(adapter);
		}
	}

	
	@Override
	public void onResume() {
		super.onResume();
	}


}
