package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.WeatherDataUtil;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.WarningInfo;

public class WarningActivity  extends BaseActivity{
	
	private View mContent;
	private ListView mListView;
//	private WaringInfoAdapter mAdapter;
	private SimpleCursorAdapter mSimpleAdapter ;
//	private WarningCursorAdapter mAdapter;
	private SimpleAdapter mAdapter;
	private String mCity;
	private List<WarningInfo> mWarningInfos = new ArrayList<WarningInfo>();
	List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private CityDao mCityDao ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.weather_warning));
		setIsBack(true);
		setIsNavigator(false);
		mContent = inflater(R.layout.warning_main);
		mListView = (ListView) mContent.findViewById(R.id.lv_warning);
		mCityDao = new CityDao(WarningActivity.this);
		mCity = getIntent().getStringExtra("city");
//		mWarningInfos = ExitApplication.getInstance().mWarningMap.get(mCity);
		mWarningInfos.addAll(mCityDao.queryWarnings(mCity));
		mWarningInfos.addAll(mCityDao.queryOtherWarnings(mCity));
		if (mWarningInfos!=null) {
			for(WarningInfo warningInfo :mWarningInfos){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("image", WeatherDataUtil.getWarningDrawbleID(warningInfo.type, warningInfo.level));
				map.put("time", warningInfo.time);
				map.put("title", warningInfo.title);
				list.add(map);
			}
		}
		if(list.size()==0){
			CommonUtil.showToask(this, "没有预警信息");
		}
		mAdapter = new SimpleAdapter(this, list,
                R.layout.warning_list_item, new String[] { "image", "time", "title" },
                new int[] { R.id.iv_warning_tpye, R.id.tv_warning_create_time, R.id.tv_warning_title });
		//mAdapter = new WarningCursorAdapter(this, ExitApplication.getInstance().mCityDao.getWarningsCursor(mCity));
//		mAdapter = new WarningCursorAdapter(this, ExitApplication.getInstance().mCityDao.getWarningsCursor(0,5));
//		String[] from = {"time","content"};//列名与控件id一一对应
//		int[] to = {R.id.tv_warning_create_time,R.id.tv_warning_title};
//		mSimpleAdapter = new SimpleCursorAdapter(this, R.layout.warning_list_item, ExitApplication.getInstance().mCityDao.getWarningsCursor(mCity), from, to);
		
		
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				Cursor cursor = (Cursor) arg0.getAdapter().getItem(arg2);
//				int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
//				String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
//				String level = cursor.getString(cursor.getColumnIndexOrThrow("level"));
//				String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
//				String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
//				String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
//				
//				Toast.makeText(WarningActivity.this, "_id = "+id, Toast.LENGTH_SHORT).show();
				
				WarningInfo warningInfo = mWarningInfos.get(arg2);
				String type = warningInfo.type;
				String level = warningInfo.level;
				String time = warningInfo.time;
				String content = warningInfo.content;;
				String title = warningInfo.title;
				Bundle bundle = new Bundle();
				bundle.putString("type", type);
				bundle.putString("level", level);
				bundle.putString("time", time);
				bundle.putString("title", title);
				bundle.putString("content", content);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(WarningActivity.this, WarningDetailActivity.class);
				startActivity(intent);
//				cursor.moveToPosition(arg2);
		           
				
			}
		});
		
		setBackOnClickListener(this);
		
	}
	
	@Override
	protected void onPause() {
		
		mBinder.setNoWarningRemind();
		super.onPause();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			finish();
			break;
		}
		super.onClick(v);
	}
}
