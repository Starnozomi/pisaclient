package com.supermap.pisaclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.pisaclient.R.array;
import com.supermap.pisaclient.adapter.CategoryAdapter;
import com.supermap.pisaclient.adapter.CommisCropAdapter;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ToastUtils;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.ui.BaseActivity;
import com.supermap.pisaclient.ui.CropUploadActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DetailedcropActivity extends BaseActivity {

	private View mContent;
	private SimpleAdapter adapter;
	private TextView crop_lv1;
	private CropDao mCropDao;
	private CropType mCropType;
	
	private List<CropType> mCroplist; //用于提交的 作物列表
	private String[] dataname;  //临时保存 定制作物名字 数组
	private int[] dataid;  //临时保存 定制作物名字 数组
	private Button crop_add, crop_submit;
	private ListView crop_list;
	Intent intent;
	User mUser;
	private int Maxlist = 0;
	private CommisCropAdapter commisCropAdapter;
	private int pos = 0; //记录被点击的item
	List<CropType> cropTypelist = null;
	List<HashMap<String, Object>> cropdata; //定制作物 本地列表(仅用于设置listview)
	HashMap<String, Object> map1; //= new HashMap<String, Object>();
	private String[] cropname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.crop_commis));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);

		mContent = inflater(R.layout.activity_detailedcrop);
		
		mCropTypeList = new ArrayList<CropType>();
		
		initView();
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(DetailedcropActivity.this).setTitle("提示").setMessage("是否放弃当前作物定制编辑?")
			.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					intent = new Intent(DetailedcropActivity.this, CommissionActivity.class);
				    startActivity(intent);
				    DetailedcropActivity.this.finish();
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			    
		}
		return false;
	}

	private void initView() {
		
		mCropDao = new CropDao();
		mCropTypeDao = new CropTypeDao();
		
		crop_lv1 = (TextView) findViewById(R.id.crop_lv1);
		crop_add = (Button) findViewById(R.id.crop_add);
		crop_submit = (Button) findViewById(R.id.crop_submit);
		
		crop_list = (ListView) findViewById(R.id.crop_list);
		crop_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 pos = position;
				 AlertDialog.Builder builder = new AlertDialog.Builder(DetailedcropActivity.this);
	                final String[] cities = { "删除" }; //指定下拉列表的显示数据
	                //    设置一个下拉的列表选择项
	                builder.setItems(cities, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which)  {
	                    	cropdata.remove(pos); 
	                    	adapter.notifyDataSetChanged();
	                    }
	                });
	                builder.show();
			}
		});
		
		//提交定制作物
		crop_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(cropdata.size() !=0) {
					    JSONArray array = new JSONArray();
					    cropTypelist = new ArrayList<CropType>();
						for(int i = 0; i<cropdata.size(); i++) {
							for(int j = 0; j<Common.dataname.length; j++){ //用于设置 作物ID数组
								if(cropdata.get(i).get("cropname").toString().equals(Common.dataname[j])) {
									map1.put("cropid", Common.dataid[j]);
									break;
								}
							}
//							try {
//								mCropType = new CropType();
//								
//								JSONObject obj = array.getJSONObject(i);
//								mCropType.id = obj.getInt("cropid");
//								mCropType.name = obj.getString("cropname");
//								mCropType.parentId = 0;
//								cropTypelist.add(mCropType);
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}
					ToastUtils.showToast(DetailedcropActivity.this, "提交成功");
//					intent = new Intent(DetailedcropActivity.this, CommissionActivity.class);
//					startActivity(intent);
				} else {
					ToastUtils.showToast(DetailedcropActivity.this, "您的定制内容为空，请选择后再上传");
				}
		    }
	     });
		
		cropdata = new ArrayList<HashMap<String, Object>>();
		
		new LoadAgrTypeInfoTask(2).execute();
		
		//添加作物
		crop_add.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {
				if("".equals(crop_lv1.getText().toString()) || "点击选择作物类型".equals(crop_lv1.getText().toString())){
					ToastUtils.showToast(DetailedcropActivity.this, "请先选择作物品种");
				} else {
					map1 = new HashMap<String,Object>();
					Boolean bol = true;
					for(int i = 0; i< cropdata.size(); i++){
						if(cropdata.get(i).get("cropname").toString().equals(crop_lv1.getText().toString())){
							bol = false;
						} else {
						}
					}
					if(bol) {
						map1.put("cropname", crop_lv1.getText().toString());
					    cropdata.add(map1);
					    crop_list.setAdapter(adapter);
					    crop_lv1.setText("点击选择作物类型");
					} else {
						ToastUtils.showToast(DetailedcropActivity.this, "不可重复添加作物");
					}
					
				}
		    }
	   });
		
		//选择作物
		crop_lv1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					new LoadAgrTypeInfoTask(1).execute();
					isLoading = true;
			}
		});
		
	}
	
	private List<CropType> mCropTypeList;
	private Boolean isLoading = false;
	private CropTypeDao mCropTypeDao;
	private String mAreaCode = "500112011"; // 地区id
	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean> {
		private int mType = 0;
		private boolean mLoadPass = false;

		public LoadAgrTypeInfoTask(int type) {
			this.mType = type;
		}

		protected Boolean doInBackground(Integer... arg0) {
			switch (mType) {
			case 1:
			case 2:
				mCropTypeList = mCropTypeDao.getCropTypes(mAreaCode.substring(0,6));
				if (mCropTypeList != null) {
					mLoadPass = true;
				}
				break;
			default:
				break;
			}
			return mLoadPass;
			
		}

	    protected void onPostExecute(Boolean result) {
	    	if (!result) {
	    	}
	    	isLoading = false;
	    	switch (mType) {
	    		case 1:// 种养类型选择
	    			if (mCropTypeList != null && mCropTypeList.size() > 0) {
	    				final String[] data = new String[mCropTypeList.size()];
	    				for (int i = 0; i < mCropTypeList.size(); i++) {
	    					data[i] = mCropTypeList.get(i).name;
	    				}
	    				new AlertDialog.Builder(DetailedcropActivity.this).setItems(data, new DialogInterface.OnClickListener() {
	    					public void onClick(DialogInterface dialog, int which) {
	    						crop_lv1.setText(data[which]);
	    					}
	    				}).show();
	    			}
	    			break;
	    		case 2:// 种养类型选择
	    			if (mCropTypeList != null && mCropTypeList.size() > 0) {
	    				dataname = new String[mCropTypeList.size()];
	    				Maxlist = mCropTypeList.size();
	    				dataname = new String[Maxlist];
	    				dataid = new int[Maxlist];
	    				cropdata = new ArrayList<HashMap<String, Object>>();
	    				for (int i = 0; i < mCropTypeList.size() -63 ; i++) {
	    					dataname[i] = mCropTypeList.get(i).name;
	    					dataid[i] = mCropTypeList.get(i).id;
	    					map1 = new HashMap<String,Object>();
	    					map1.put("cropname", dataname[i]);
	    					cropdata.add(map1);
	    				}
	    				adapter = new SimpleAdapter(DetailedcropActivity.this, cropdata, R.layout.croplist_item, new String[] { "cropname" }, new int[] { R.id.crop_name});
	    				crop_list.setAdapter(adapter);
	    			}
	    			break;	
		 }
	  }
	}
	
	 
//	private List<CropType> cropTypeList;
//	@SuppressWarnings("unused")
//	private class loadCropTask extends AsyncTask<String, Integer, List<CropType>> {
//		private int mUserId = 0;
//		private boolean mLoadPass = false;
//
//		public loadCropTask(int userId) {
//			this.mUserId = userId;
//		}
//
//		protected List<CropType> doInBackground(String... params) {
//			cropTypeList = mCropDao.getCropByUserId(mUserId);
//			if (cropTypeList != null) {
//				mLoadPass = true;
//			}
//			return cropTypeList;
//		}
//
//	    protected void onPostExecute(Boolean result) {
//	    	if (!result) {
//	    	}
//		}
//	  }

}
