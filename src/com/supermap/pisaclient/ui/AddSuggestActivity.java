/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @AddSuggestActivity.java - 2014-7-18 上午11:52:29
 */

package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.FactorRangeAdapter;
import com.supermap.pisaclient.biz.CropPeriodDao;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.biz.MenuDao;
import com.supermap.pisaclient.biz.MessageUploadDao;
import com.supermap.pisaclient.biz.SuggestDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.FactorIndexRangeMsg;
import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.entity.User;
import com.umeng.common.net.e;
import com.umeng.common.net.l;

public class AddSuggestActivity extends BaseActivity {

	private View mContent;
	private TextView mTvType;
	private TextView mTvCropType;
	private TextView mTvCropGrowType;
	private TextView mTvArea;
	private EditText mEtTitle;
	private EditText mEtInfo;
	private LinearLayout mLlCropType;
	private LinearLayout mLlCropGrowType;
	private LinearLayout mLlArea;
	private LinearLayout mLlType;
	private ListView mListView;
	
	private MenuItem mType;
	private MenuDao mTypeDao;
	private List<MenuItem> mScienceTypes;
	private List<City> mCities;
	private String[] data;
	private String[] crop_types;
	private String[] crop_grow_tpyes;
	private List<CropType> mCropTypeList;
	private List<CropPeriod> mCropPeroids;
	private CropType mCropType;
	private CropPeriod mCropPeriod;
	private List<List<FactorIndexRangeMsg>> mFactorList;
	private List<String[]> dataList;
	private List<FactorIndexRangeMsg> mSelectFactor =new ArrayList<FactorIndexRangeMsg>();
	private List<HashMap<String, String>> mFactorNamelist;
	private Button mBtnSend;
	private SuggestDao mSuggestDao;
	private String mTitle;
	private String mInfo;
	private AreaSelectParameter mAreaSelectParameter = null;
	private CropTypeDao mCropTypeDao ;
	private CropPeriodDao mCropPeriodDao;
	private City mCity;
	private FactorRangeAdapter mAdapter;
	private KnowledgeAdapter  mKnowledgeAdapter;
	private boolean isLoading = false;
	private List<String> mKnowledge = new ArrayList<String>();
	private String strKnowledge ;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constant.CITY_SET_RESULT) {
			mCities = (List<City>) data.getSerializableExtra("city");
			if ((mCities != null)&&(mCities.size()>0)) {
				mCity = mCities.get(0);
				StringBuffer sb = new StringBuffer();
				for (City city : mCities) {
					sb.append(city.name + " ");
				}
				mTvArea.setText(sb.toString());
				setEditInfo();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.professional_advice));
		setRgNavigator(R.id.rad_more);
		setIsNavigator(false);

		mContent = inflater(R.layout.add_suggest);
		mTvType = (TextView) mContent.findViewById(R.id.tv_type);
		mTvCropType = (TextView) mContent.findViewById(R.id.tv_crop_type);
		mTvCropGrowType = (TextView) mContent.findViewById(R.id.tv_crop_grow_type);
		mTvArea = (TextView) mContent.findViewById(R.id.tv_area);
		mListView = (ListView) mContent.findViewById(R.id.lv_crop_factor);
		
		mTvCropType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvCropGrowType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvArea.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTvType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		mLlType = (LinearLayout) mContent.findViewById(R.id.ll_type);
		mLlArea = (LinearLayout) mContent.findViewById(R.id.ll_area);
		mLlCropType = (LinearLayout) mContent.findViewById(R.id.ll_crop_type);
		mLlCropGrowType = (LinearLayout) mContent.findViewById(R.id.ll_crop_grow_type);
		
		mBtnSend = (Button) mContent.findViewById(R.id.btn_send);
		mEtTitle = (EditText) mContent.findViewById(R.id.et_title);
		mEtInfo = (EditText) mContent.findViewById(R.id.et_info);
		mTvType.setOnClickListener(this);
		mTvCropType.setOnClickListener(this);
		mTvCropGrowType.setOnClickListener(this);
		mTvArea.setOnClickListener(this);
		mLlType.setOnClickListener(this);
		mLlCropType.setOnClickListener(this);
		mLlCropGrowType.setOnClickListener(this);
		mLlArea.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		
		
		mSuggestDao = new SuggestDao();
		mCropTypeDao = new CropTypeDao();
		mCropPeriodDao = new CropPeriodDao();
		new LoadType().execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_type:
		case R.id.ll_type:
			if (data == null) {
				new LoadType().execute();
			} else {
				new AlertDialog.Builder(AddSuggestActivity.this).setItems(data, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mType = mScienceTypes.get(which);
						mTvType.setText(mType.name);
					}
				}).show();
			}
			break;
		case R.id.tv_crop_type:
		case R.id.ll_crop_type:
			if (!isLoading) {
				isLoading = true;
				new LoadAgrTypeInfoTask(1).execute();
			}
			else {
				CommonUtil.showToask(AddSuggestActivity.this, "正在加载农作物类别...");
			}
			break;
		case R.id.tv_crop_grow_type:
		case R.id.ll_crop_grow_type:
			if (mCropType==null) {
				CommonUtil.showToask(AddSuggestActivity.this, "请先选择农作物类别");
			}
			else {
				if (!isLoading) {
					isLoading = true;
					new LoadAgrTypeInfoTask(2).execute();
				}
				else {
					CommonUtil.showToask(AddSuggestActivity.this, "正在加载农作物种养类型...");
				}
			}
			break;
		case R.id.tv_area:
		case R.id.ll_area:
			Intent intent = new Intent(this, CitySetActivity.class);
			if (mAreaSelectParameter==null) {
				mAreaSelectParameter= new AreaSelectParameter();
				mAreaSelectParameter.flag = Constant.SELECT_COUNTRY_REQUEST;
				mAreaSelectParameter.isWeatherArea = false;
				mAreaSelectParameter.isSelectMore = false;
				mAreaSelectParameter.isShowRemind = false;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("parameter", mAreaSelectParameter);
			intent.putExtras(bundle);
			startActivityForResult(intent, Constant.SELECT_COUNTRY_REQUEST);
			break;
		case R.id.btn_send:
			if (valid()) {
				mBtnSend.setClickable(false);
				Suggest suggest = new Suggest();
				StringBuffer sb = new StringBuffer();
				if ((mCities!=null)&&(mCities.size()>0)) {
					for (City city : mCities) {
						sb.append(city.areacode + ",");
					}
					String areacode = sb.toString();
					suggest.areacode = areacode.substring(0, areacode.length() - 1);
				}
				if (suggest.areacode==null) {
					suggest.areacode = mCity.areacode;
				}
				if (mCropPeriod==null) {
					suggest.pubertyId = 0;
				}
				else {
					suggest.pubertyId = mCropPeriod.id;
				}
				suggest.typeid = mType.id;
				suggest.title = mTitle;
				suggest.info =mCity.name+mEtInfo.getText().toString().replaceAll("[\\t\\n\\r]", "");;
				User user = UserDao.getInstance().get();
				
				if (user != null) {
					suggest.userid = user.id;
					int expertid = UserDao.getInstance().getExpertIdByUserId(user.id);
					String expertname = UserDao.getInstance().getUserName(user.id);
					List<String> expertSubject = UserDao.getInstance().getExpertSubject(expertid);
					if((expertSubject!=null)&&(expertSubject.size()>0)) {
						suggest.info = "["+expertSubject.get(0)+"专家:"+expertname+"]"+suggest.info;
					}
					new Thread(new SuggestThread(suggest)).start();
				}
			}
			break;
		}
		super.onClick(v);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				CommonUtil.showToask(AddSuggestActivity.this, getResources().getString(R.string.suggest_send_success));
				mBtnSend.setClickable(true);
				AddSuggestActivity.this.finish();
			} else {
				mBtnSend.setClickable(true);
				CommonUtil.showToask(AddSuggestActivity.this, getResources().getString(R.string.suggest_send_fail));
			}
		}

	};
	
	private void setEditInfo(){
		String cityname , croptype, cropgrow, knowledge;
		if(mCity==null){
			cityname="";
		}
		else {
			cityname = mCity.name;
		}
		if (mCropType==null) {
			croptype="";
		}
		else {
			croptype=mCropType.name;
		}
		if (mCropPeriod==null) {
			cropgrow="";
		}
		else {
			cropgrow=mCropPeriod.growthperiod;
		}
		if ((strKnowledge==null)||(strKnowledge.equals(""))) {
			knowledge =""	;
		}
		else {
			knowledge =strKnowledge;
		}
		String info = cityname +croptype+cropgrow+knowledge;
		mEtInfo.setText(info);
	}

	private boolean valid() {
		mInfo = mEtInfo.getText().toString();
		mTitle = mEtTitle.getText().toString();
		if (mType==null) {
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_please_type));
			return false;
		} else if(mCropType == null) {
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_crop_please_type));
			return false;
//		} else if ((mCropPeriod == null)&&(mCropType.id!=80)&&(mCropType.id!=89)) {
//			//80 其他 89麻类
//			CommonUtil.showToask(this, getResources().getString(R.string.suggest_crop_grow_please_type));
//			return false;
		} else if(mCity==null){
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_please_area));
			return false;
		} else if ("".equals(mTitle)) {
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_please_title));
			mEtTitle.setFocusable(true);
			mEtTitle.setFocusableInTouchMode(true);
			mEtTitle.clearFocus();
			mEtTitle.requestFocus();
			return false;
		} else if ("".equals(mInfo)) {
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_please_info));
			mEtInfo.setFocusable(true);
			mEtInfo.setFocusableInTouchMode(true);
			mEtInfo.clearFocus();
			mEtInfo.requestFocus();
			return false;
		}
		return true;
	}

	private class SuggestThread implements Runnable {
		private Suggest suggest;
		public SuggestThread(Suggest suggest) {
			this.suggest = suggest;
		}
		@Override
		public void run() {
			boolean flag = mSuggestDao.addSuggest(suggest);
			Message msg = mHandler.obtainMessage();
			msg.what = flag ? 1 : 0;
			mHandler.sendMessage(msg);
		}
	}
	
	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean>{
		 
		 private int mType = 0;
		 private boolean mLoadPass = false;
		 
		 public LoadAgrTypeInfoTask(int type){
			 this.mType = type;
		 }

		@Override
		protected Boolean doInBackground(Integer... arg0) {
			switch (mType) {
			case 1://获取种养类型
					mCropTypeList = mCropTypeDao.getCropTypes(0);
					if (mCropTypeList!=null) {
						mLoadPass = true;
					}
					break;
			case 2://获取生长发育期
					if (mCropType==null) {
						mLoadPass = false;
						break;
					}
					mCropPeroids = mCropPeriodDao.getCropGrowPeriod(mCropType); 
					if (mCropPeriod!=null) {
						mLoadPass = true;
					}
					break;
			default:
					break;
			}
				
			return mLoadPass;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
			}
			switch (mType) {
			case 1://种养类型选择
				if (mCropTypeList != null && mCropTypeList.size() > 0) {
					String[] data = new String[mCropTypeList.size()];
					for (int i = 0; i < mCropTypeList.size(); i++) {
						data[i] = mCropTypeList.get(i).name;
					}
					new AlertDialog.Builder(AddSuggestActivity.this).setItems(data, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropType = mCropTypeList.get(which);
							mTvCropType.setText(mCropType.name);
							setEditInfo();
						}

					}).show();
				}
				break;
			case 2:
				
				if (mCropPeroids != null && mCropPeroids.size() > 0) {
					String[] data = new String[mCropPeroids.size()];
					for (int i = 0; i < mCropPeroids.size(); i++) {
						data[i] = mCropPeroids.get(i).growthperiod;
					}
					new AlertDialog.Builder(AddSuggestActivity.this).setItems(data, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropPeriod = mCropPeroids.get(which);
							mTvCropGrowType.setText(mCropPeriod.growthperiod);
//							new LoadFactorType().execute();
							new LoadExpertKnowledge().execute();
							setEditInfo();
						}
					}).show();
				}
					break;
			default:
				break;
			}
			
			isLoading = false;
		}
	 }

	private void initFactorList(){
		mFactorNamelist=new ArrayList<HashMap<String,String>>();
		dataList = new ArrayList<String[]>();
		HashMap<String, String> item = null;
		for(List<FactorIndexRangeMsg> temp :mFactorList){
			item = new HashMap<String, String>();
			item.put("name", temp.get(0).descript);
			item.put("range",temp.get(0).minValue+"~"+temp.get(0).maxValue);
			mFactorNamelist.add(item);//存放因子名字和因子第一个范围区间
			String data[] = new String[temp.size()];
			for (int i = 0; i < data.length; i++) {
				data[i] = temp.get(i).minValue+"~"+temp.get(i).maxValue+" "+temp.get(i).levelName;
			}
			dataList.add(data);//存放因子范围区间数组
		}
		mAdapter = new FactorRangeAdapter(AddSuggestActivity.this, mFactorNamelist);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				
				final TextView range  = 	(TextView)(arg0.getChildAt(position).findViewById(R.id.tv_factor_range));
				final String[] data = dataList.get(position);
				final List<FactorIndexRangeMsg> tempFactorIndexRangeMsgs  = mFactorList.get(position);
				
				new AlertDialog.Builder(AddSuggestActivity.this).setItems(dataList.get(position), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						range.setText(data[which]);
						boolean isExist = false;
						FactorIndexRangeMsg msg = tempFactorIndexRangeMsgs.get(which);
						
						for(int i = 0;i<mSelectFactor.size();i++){
							if (mSelectFactor.get(i).descript.equals(msg.descript)) {
								isExist = true;
								mSelectFactor.remove(i);
								mSelectFactor.add(i,msg);
								break;
							}
						}
						if (!isExist) {
							mSelectFactor.add(msg);	
						}
						
						String suggest = "";
						for(FactorIndexRangeMsg temp :mSelectFactor){
							suggest +=temp.suggest+",";
						}
						String suggestInfo = mCropType.name+suggest;
						mEtInfo.setText(suggestInfo);
					}
				}).show();
			}
		});
	}
	
	private class LoadFactorType extends AsyncTask<Integer, Integer, List<List<FactorIndexRangeMsg>>> {

		@Override
		protected List<List<FactorIndexRangeMsg>> doInBackground(Integer... params) {
			if ((mCropType!=null)&&(mCropPeriod!=null)) {
				List<Integer> list = mSuggestDao.getfactoryId( mCropPeriod.id);
				if (list==null) {
					return null;
				}
				mFactorList = new ArrayList<List<FactorIndexRangeMsg>>();
				for(Integer integer :list){
					List<FactorIndexRangeMsg> factor = mSuggestDao.getfactoryList(integer);
					if ((factor!=null)&&(factor.size()>0)) {
						mFactorList.add(factor);
					}
				}
			}
			return mFactorList;
		}

		@Override
		protected void onPostExecute(List<List<FactorIndexRangeMsg>> result) {
			if (result != null) {
				initFactorList();
			}
		}

	}
	
	private class LoadType extends AsyncTask<Integer, Integer, List<MenuItem>> {

		@Override
		protected List<MenuItem> doInBackground(Integer... params) {
			mTypeDao = new MenuDao("cq.get.sciencetype");
			return mTypeDao.getAll();
		}

		@Override
		protected void onPostExecute(List<MenuItem> result) {
			if (result != null) {
				mScienceTypes = result;
				data = new String[result.size()];
				for (int i = 0; i < data.length; i++) {
					data[i] = result.get(i).name;
				}
			}
		}

	}
	
	
	private void initKnowledgeList() {
		TextView tv = (TextView) mContent.findViewById(R.id.tv_please_know);
		tv.setVisibility(View.VISIBLE);
		mKnowledgeAdapter = new KnowledgeAdapter(this, mKnowledge);
		mListView.setAdapter(mKnowledgeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
//					mEtInfo.setText(mKnowledge.get(position));
					strKnowledge = mKnowledge.get(position);
					setEditInfo();
				}
			}
			
		);
		
		
	}
	
	private class KnowledgeAdapter extends BaseAdapter {

		private List<String> all;
		private LayoutInflater mInflater;
		private Context mContext;
		private CityDao mCityDao;
		private int mMsgid = 0;
		private MessageUploadDao messageUploadDao;
		public KnowledgeAdapter(Context context, List<String> all) {
			this.mContext = context;
			this.all = all;
			this.mCityDao = new CityDao(context);
			messageUploadDao = new MessageUploadDao();
		}

		@Override
		public int getCount() {
			return all.size();
		}

		@Override
		public Object getItem(int position) {
			return all.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.suggest_knowledge_item, null);
				holder.content = (TextView) convertView.findViewById(R.id.tv_knowledge);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.content.setText(all.get(position).substring(0,10)+"...");
			return convertView;
		}

		private class ViewHolder {
			TextView content;
		}
		

	}
	private class LoadExpertKnowledge extends AsyncTask<Integer, Integer,Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			User user = UserDao.getInstance().get();
			int expertid = UserDao.getInstance().getExpertIdByUserId(user.id);
			mKnowledge = mSuggestDao.getExpertKnowleage(expertid,mCropPeriod.id);
			if ((mKnowledge!=null)&&(mKnowledge.size()>0)) {
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				initKnowledgeList();
			}
		}

	}
}
