package com.supermap.pisaclient.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.pisaclient.DetailedcropActivity;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.common.CategorySharedDeal;
import com.supermap.pisaclient.common.Common;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.ReadBorldpolygonData;
import com.supermap.pisaclient.common.SharedDeal;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.service.PisaService;

public class DefaultActivity extends Activity {

	private SharedPreferences sp;
	private Editor editor;
	private final int SPLASH_DISPLAY_LENGHT = 1000;
	private ImageView mIvLogo;
	private final int LOAD_OVER = 1;
	private SharedDeal mSharedDeal;
	private LoadDataThread mlDataThread;
	private TextView mTvVersion;
	User user;
	private SharedPreferences msp;

	private Handler handler = new Handler() {
		//msp = 
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_OVER:
				sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
				editor = sp.edit();
		        String cityname = sp.getString("CITYNAME", "");
		        if("".equals(cityname) || "内蒙古自治区".equals(cityname)){
		        	editor.putString("CITYNAME", "内蒙古自治区"); //如果本地缓存表无值，赋予初始值
		        	editor.commit(); 
		        } else {
		        	//editor.putString("CITYNAME", findCity(user)); 
		        }
				//new LoadAgrTypeInfoTask(1).execute();
				startPisaService();
				startMain();
				break;
			default:
				break;
			}

		};
	};
	
	public String findCity(User user){
		String city = null;
		CityDao cty = new CityDao(DefaultActivity.this);
		if(user.areaCode.length() <6) {
			city ="内蒙古自治区";
		} else if(user.areaCode.length() ==6) {
			user.areaCode = user.areaCode.substring(0, 6);
			city = cty.queryCityName(user.areaCode);
		} else if(user.areaCode.length() >6) {
			user.areaCode = user.areaCode.substring(0, 9);
			city = cty.queryCityName(user.areaCode);
		}
		return city;
	} 

	private String getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		return packInfo.versionName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_default);
		ExitApplication.getInstance().addActivity(this);
		Constant.mLocalUser = null;
		
		mIvLogo = (ImageView) findViewById(R.id.ivLogo);
		mTvVersion = (TextView) findViewById(R.id.tvVersion);
		try {
			mTvVersion.setText(getResources().getString(R.string.app_name) + getVersionName());
		} catch (NotFoundException e) {

		} catch (Exception e) {

		}
		mSharedDeal = new SharedDeal(DefaultActivity.this);
		mlDataThread = new LoadDataThread();
		mlDataThread.start();
	}
       
	
	
	class LoadDataThread extends Thread {
		@Override
		public void run() {
			importDbFile();
//			ExitApplication.getInstance().initData(getApplicationContext ());// 加载数据库
			Message message = new Message();
			message.what = LOAD_OVER;
			handler.sendMessage(message);

		}
	}

	private void startPisaService() {
		Intent startIntent = new Intent(DefaultActivity.this, PisaService.class);
		startService(startIntent);
	}

	private void startMain() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(DefaultActivity.this, MeteoMapActivity.class);
				//Intent mainIntent = new Intent(DefaultActivity.this, SettingActivity.class);
				DefaultActivity.this.startActivity(mainIntent);
				DefaultActivity.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);
	}

	public void importDbFile() {
		File f = new File(Constant.DB_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		try {
			InputStream is = getAssets().open(Constant.DB_NAME);
			OutputStream os = new FileOutputStream(Constant.DB_PATH + Constant.DB_NAME);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	

//	private Boolean isLoading = false; 
//	private List<CropType> mCropTypeList;
//	private List<CropType> mGrowTypes;
//	private CropTypeDao mCropTypeDao;
//	private String mAreaCode = "500112011"; // 地区
//	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean> {
//		private int mType = 1;
//		private boolean mLoadPass = false;
//
//		public LoadAgrTypeInfoTask(int type) {
//			this.mType = type;
//		}
//
//		protected Boolean doInBackground(Integer... arg0) {
//			mCropTypeList = mCropTypeDao.getCropTypes(mAreaCode.substring(0,6));
//			if (mCropTypeList != null) {
//				mLoadPass = true;
//			}
//		
//
//			return mLoadPass;
//		}
//
//	@Override
//	protected void onPostExecute(Boolean result) {
//		if (!result) {
//			// CommonUtil.showToask(CropUploadActivity.this, "服务器提出了一个问题");
//		}
//		isLoading = false;
//			if (mCropTypeList != null && mCropTypeList.size() > 0) {
//				String[] data = new String[mCropTypeList.size()];
//				for (int i = 0; i < mCropTypeList.size(); i++) {
//					data[i] = mCropTypeList.get(i).name;
//				}
//			}
//		}
//	}

}
