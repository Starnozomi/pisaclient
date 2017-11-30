package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CitySetAdapter;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.Weather;

/**
 * 已知bug: 1:关注地区 -选择，可以多选，不去与天气地区的重复，会出现选择相同地区几次 n/a 2：没有做点击已选地区，取消该地区的逻辑和UI效果
 * @author kael
 * 
 */

public class CitySetActivity extends BaseActivity implements OnClickListener {

	private SharedPreferences preferences;
	private View mContent;
	private ImageButton mSerchButton;
	private GridView mGridView;
	private List<Weather> mWeathers;
	private List<String> mCityName;	
	private String[] citys;        //用于搜索栏
	private CitySetAdapter mCitySetAdapter;
	private AutoCompleteTextView mAutoCompleteTextView;
	
	public List<City> mCities; //所有城市列表
	List<City> list = new ArrayList<City>();  
	List<City> listLevel_1 = new ArrayList<City>();  //一级城市列表
	List<City> listLevel_2 = new ArrayList<City>();  //二级城市列表
	List<City> allCityList = new ArrayList<City>();  //所有城市列表
	List<City> mSelectedCityList = new ArrayList<City>(); //已被选中的城市列表
	private int mSelctedNums = 0;
	private CityDao cityDao;
	private int cityLevel = 1;
	private CustomProgressDialog mPdDialog;
	private LocatedAreaTask mLocatedAreaTask;
	public AreaSelectParameter mParameter = null;

	public void initData() { //初始化城市列表的一些信息想

		ExitApplication.getInstance().addActivity(this);
		cityDao = new CityDao(CitySetActivity.this);
		mCities = cityDao.getAllCity();
		// getExtras可能为null
		mParameter = (AreaSelectParameter) getIntent().getExtras().get("parameter");

		//allCityList = cityDao.getAllCity(-1);
		
		citys = new String[allCityList.size()]; 				
		for (int i = 0; i < allCityList.size(); i++) {
			citys[i] = allCityList.get(i).name;   
		}

		// 添加  自动定位 
		City city = new City();
		city.name = "自动定位";
		listLevel_1.add(city);

		city = new City();
		city.areaid = 1;
		city.name = "内蒙古自治区";
		city.prarentId = 0;
		city.areacode = 53 + "";
		listLevel_1.add(city); 
		
 		listLevel_1.addAll(cityDao.getAllCity(1));   /*getAllCity(2)读取里面第二列 为城市名字*/
		list = listLevel_1;
		
	}

		private void initView() {  //初始化界面 控件
			
			setTvTitle(Utils.getString(this, R.string.weather_city_set));
			setIsBack(true);
			setIsNavigator(false);
			setBackOnClickListener(this);
			//setIsSave(true);
			//setSaveOnClickListener(this);
			mContent = inflater(R.layout.city_set_main);
	
			mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actv_city);
		    //citys = getResources().getStringArray(R.array.citys);
	
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_item, citys);
			mAutoCompleteTextView.setAdapter(adapter);
			mAutoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					TextView txt = (TextView) arg1;
					String cityName = (String) txt.getText();
					for (int i = 0; i < allCityList.size(); i++) {
						if (cityName.equals(allCityList.get(i).name)) {
							addArea(allCityList.get(i));
							break;
						}
					}
					mCitySetAdapter.notifyDataSetChanged();
	
				}
			});
		mSerchButton = (ImageButton) mContent.findViewById(R.id.ib_serch);
		mSerchButton.setOnClickListener(this);

		mGridView = (GridView) mContent.findViewById(R.id.gv_city_set);
		mCitySetAdapter = new CitySetAdapter(this, list);
		mGridView.setAdapter(mCitySetAdapter);
		mGridView.setOnItemClickListener(new GridViewOnItemClickListener());

		mPdDialog = CustomProgressDialog.createDialog(CitySetActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.locating_area_data));
		LocalHelper.getInstance(CitySetActivity.this).init();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initData();
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			//如果为空 则设置默认城市重庆市 (防止退出为空 程序崩溃)
			/*if(mCities == null || mCities.size() ==0){
				mCities.add(listLevel_1.get(1));
			}*/
			closeAcitity();
			break;
//		case R.id.ibtn_save:
//			//点击确定要执行添加区域
//			closeAcitity();
//			break;
		case R.id.ib_serch:
			if (!isHaveTheSerchArea()) {
				CommonUtil.showToask(this, "没有你要查询的地区");
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	
	//查询是否存在区域
	private boolean isHaveTheSerchArea() {
		String cityName = mAutoCompleteTextView.getText().toString();
		if ((cityName == null) || (cityName.equals(""))) {
			CommonUtil.showToask(this, "请输入地区");
			return true;
		}
		City city = cityDao.queryAreaByAreaName(cityName);
		if (city != null) {
			return true;
		}
		return false;
	}

	
	/**
	 * 
	 * 添加区域
	 * 
	 **/
	private boolean addArea(City data) {

		if (mParameter.isWeatherArea) {
			if (isExistTheArea(data, mCities)) {
				deleteArea(data, mCities);
				cityDao.deleteCity(data.name);
//				CommonUtil.showToask(CitySetActivity.this, "抱歉，不能重复添加城市");
				return false;
			} else {
					mCities.add(data);
					mSelectedCityList.add(data);
					mSelctedNums ++;
			}
		} 
		else {
			if (isExistTheArea(data, mSelectedCityList)) {
				deleteArea(data, mSelectedCityList);
//				CommonUtil.showToask(CitySetActivity.this, "抱歉，不能重复添加城市");
				return false;
			} else {
				mSelectedCityList.add(data);
				mSelctedNums++;
			}
		}
		if (!mParameter.isSelectMore) {
			closeAcitity();
		}
		return true;
	}
	
    /**
     * 
     * 删除地区
     * 
     **/
	private boolean deleteArea(City city, List<City> list) {

		if ((city == null) || (city.name == null)) {
			return true;
		}
		for (int i = 0; i<list.size(); i++) {
			if (city.name.equals(list.get(i).name)) {
				list.remove(i);
				return true;
			}
		}
		return false;
	}
	
	private boolean isExistTheArea(City city, List<City> list) {

		if ((city == null) || (city.name == null)) {
			return true;
		}
		for (City city2 : list) {
			if (city.name.equals(city2.name)) {
				return true;
			}
		}
		return false;
	}

	
	
	
	@Override
	public void onBackPressed() {
		backPressed();
	}
	
	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(CitySetActivity.this).close();
		super.onDestroy();
	}

	private void backPressed() {
		if ((mSelctedNums == 0) && (cityLevel == 1)) {
			super.onBackPressed();
		}
		if (cityLevel == 2) {
			mCitySetAdapter = new CitySetAdapter(CitySetActivity.this, listLevel_1);
			mGridView.setAdapter(mCitySetAdapter);
			cityLevel = 1;
		}
		else {
			
			closeAcitity();
		}
	
	}

	//关闭选择城市页面
	private void closeAcitity() {
		Intent intent = new Intent();
		if (mSelctedNums == 0) {
			// CommonUtil.showToask(this, "没有选择任何地区");
			if (mParameter.isWeatherArea) {
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("city", (ArrayList<? extends Parcelable>) mSelectedCityList);
				intent.putExtras(bundle);
				setResult(Constant.CITY_SET_RESULT, intent);
				//cityDao.addAllCity(mCities);
				finish();
			}
			else {
				//createExitDialog();
				CitySetActivity.this.finish();
				setResult(RESULT_CANCELED, intent);
			}

		} else {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("city", (ArrayList<? extends Parcelable>) mSelectedCityList);
			intent.putExtras(bundle);
			setResult(Constant.CITY_SET_RESULT, intent);
			finish();
		}

	}

	
	//自动定位功能
	private class LocatedAreaTask extends AsyncTask<String, Integer, City> {
		private boolean mLocated = false;
		private String mName = null;

		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.show();
			}
		}

		@Override
		protected City doInBackground(String... arg0) {
			City result = null;
			
			sleep(2*1000);
			mName = LocalHelper.getInstance(CitySetActivity.this).getCity();
			if (mName==null) {
				sleep(2*1000);
				mName = LocalHelper.getInstance(CitySetActivity.this).getCity();
			}
			if (mName!=null) {// 定位成功
				mLocated = true;
				result = cityDao.queryAreaByAreaName(mName);
			}
			return result;
		}

		@Override
		protected void onPostExecute(City result) {
//			if (mLocated && (result == null)) {
//				CommonUtil.showToask(CitySetActivity.this, "地区" + mName + "不在服务范围，请从新选择地区");
//			}
			if (result==null) {
				CommonUtil.showToask(CitySetActivity.this, "定位失败");
			}
			if (result != null) {
				if (mParameter.flag == Constant.ADD_NET_AREA) {
					result.name = "自动定位";
				}
				addArea(result);
			}
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
			}

		}
		
		private void sleep(long time){
			
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	private class GridViewOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int postion, long arg3) {	
			if (cityLevel == 1) {
				if (postion == 0) {
					mLocatedAreaTask = new LocatedAreaTask();
					mLocatedAreaTask.execute();
					return;
				}
				if (postion == 1) {
					if (addArea(listLevel_1.get(postion))) {
							ImageView iView = (ImageView) (view.findViewById(R.id.iv_cityset_isselected));
							iView.setVisibility(View.VISIBLE);
					}else {
						ImageView iView = (ImageView) (view.findViewById(R.id.iv_cityset_isselected));
						iView.setVisibility(View.INVISIBLE);
						//deleteArea(listLevel_1.get(postion), mSelectedCityList);
					}
					
					return;
				}
				listLevel_2.clear();
				listLevel_2.add(listLevel_1.get(postion));   //通过获取第一级城市ID 显示一级城市下属的二级城市列表
				listLevel_2.addAll(cityDao.getAllCity(listLevel_1.get(postion).areaid));
				mCitySetAdapter = new CitySetAdapter(CitySetActivity.this, listLevel_2);
				mGridView.setAdapter(mCitySetAdapter);
				// mGridView.scrollTo(10, 10);
				// mGridView.smoothScrollToPosition(0,0);
				cityLevel++;
			} else {
				if (mSelctedNums >= 1) {
					CommonUtil.showToask(CitySetActivity.this, "最多只能添加1个地区");
					return;
				}
				if (addArea(listLevel_2.get(postion))) {
					ImageView iView = (ImageView) (view.findViewById(R.id.iv_cityset_isselected));
					iView.setVisibility(View.VISIBLE);
				}else {
					ImageView iView = (ImageView) (view.findViewById(R.id.iv_cityset_isselected));
					iView.setVisibility(View.INVISIBLE);
					deleteArea(listLevel_2.get(postion), mSelectedCityList);
				}
			}

		}

	}
	
}
