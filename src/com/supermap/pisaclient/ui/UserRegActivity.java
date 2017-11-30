/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @UserRegActivity.java - 2014-5-28 上午11:00:01
 */

package com.supermap.pisaclient.ui;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.User;

/**
 * TODO 把网络操作放到异步线程中去
 * @author kael
 *
 */
public class UserRegActivity extends BaseActivity {

	private View mContent;
	private Button mBtnReg;
	private Button mBtnEdit;
	private LinearLayout ll_prompt4;
	private LinearLayout mLlPwd;
	private LinearLayout mLlRepwd;
	private LinearLayout mLlShowName;
	private LinearLayout mLlReRepwd;
	private LinearLayout mLlTel;
	private LinearLayout mLlCountry;
	private LinearLayout mLlCountries;
	private int mIsEdit = 0;
	private ImageView mIvHeader;
	private EditText mEtUserName;
	private EditText mEtLoginName;
	private EditText mEtPwd;
	private EditText mEtRepwd;
	private EditText mEtRerepwd;
	private EditText mEtTel;
	private String mUserName;
	private String mLoginName;
	private String mPwd;
	private String mRePwd;
	private String mRerePwd;
	private String mTel;
	private User mUser;
	private boolean mIsSelected = false;
	private String mPath = null;
	private Bitmap mBmp = null;
	private ImageLoader mLoader;
	private TextView mTvCountry;
	private LinearLayout mLlAboutArea;
	private List<City> mCities;
	private CityDao mCityDao;
	private UserDao mUserDao;
	private AreaSelectParameter mAreaSelectParameter;
	private TextView mTvPwd;
	private TextView mTvRePwd;
	private int mIsPwd = 0; // 0：注册	   1：编辑资料 	 2：修改密码

	File file = null;
	private CustomProgressDialog mPdDialog;
	
	private RelativeLayout rl_workstation;
	
	private TextView tvSelectWorkStation;
	
	private int selectedWorkStationId;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((resultCode == RESULT_OK) && (requestCode == Constant.IMAGE_CAPTURE_REQUEST)) {
			mPath = CommonImageUtil.getInstance(this).getPicturePath();
			mBmp = CommonImageUtil.getInstance(this).decodeFile(mPath, 100, 100);
			mIvHeader.setImageBitmap(CommonUtil.toRoundBitmap(mBmp));//显示本地的原图
//			if (mUser.headImage != null) {							 //显示服务器的缩略图，存在编辑头像和显示的不一致
//				mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIvHeader, false);
//			}
			mIsSelected = true;
		} else if ((resultCode == RESULT_OK) && (requestCode == Constant.IMAGE_GET_REQUEST)) {
			Uri uri = data.getData();
			uri = geturi(data);//解决方案  
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			mPath = cursor.getString(column_index);
			mBmp = CommonImageUtil.getInstance(this).decodeFile(mPath, 100, 100);
			mIvHeader.setImageBitmap(CommonUtil.toRoundBitmap(mBmp));
			mIsSelected = true;
		} else if (resultCode == Constant.CITY_SET_RESULT) {
			mCities = (List<City>) data.getSerializableExtra("city");
			StringBuffer sb = new StringBuffer();
			for (City city : mCities) {
				sb.append(city.name + " ");
			}
			String result = sb.toString();
			if (!"".equals(result)) {
				mTvCountry.setText(result);
			} else {
				mTvCountry.setText(getResources().getString(R.string.select_country));
			}
		}else if(resultCode == Constant.SELECT_WORKSTATION){
			Bundle b = data.getExtras();
			int workStationId = b.getInt("workstationid");
			String workStationName = b.getString("workstationname");
			tvSelectWorkStation.setText(workStationName);
			selectedWorkStationId = workStationId;
		}
	}
	
	public Uri geturi(android.content.Intent intent) {    
        Uri uri = intent.getData();    
        String type = intent.getType();    
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {    
            String path = uri.getEncodedPath();    
            if (path != null) {    
                path = Uri.decode(path);    
                ContentResolver cr = this.getContentResolver();    
                StringBuffer buff = new StringBuffer();    
                buff.append("(").append(Images.ImageColumns.DATA).append("=")    
                        .append("'" + path + "'").append(")");    
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,    
                        new String[] { Images.ImageColumns._ID },    
                        buff.toString(), null, null);    
                int index = 0;    
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {    
                    index = cur.getColumnIndex(Images.ImageColumns._ID);    
                    // set _id value    
                    index = cur.getInt(index);    
                }    
                if (index == 0) {    
                    // do nothing    
                } else {    
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);    
                    if (uri_temp != null) {    
                        uri = uri_temp;    
                    }    
                }    
            }    
        }    
        return uri;    
    }    

	private class ExistsTask extends AsyncTask<Integer, Integer, Boolean> {

		private String name;

		public ExistsTask(String name) {
			this.name = name;
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			return mUserDao.isExists(name);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				mEtLoginName.setText("");
				mEtLoginName.setHint(getResources().getString(R.string.user_exists));
				focusLoginName();
			}
		}

	}
	
	private void initfocusAcount(){
		mEtTel.setFocusable(true);
		mEtTel.setFocusableInTouchMode(true);
		mEtTel.requestFocus();
		mEtTel.requestFocusFromTouch();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setRgNavigator(R.id.rad_more);
		setIsBack(true);
		setBackOnClickListener(this);
		setIsNavigator(false);
		mContent = inflater(R.layout.user_reg);

		mUserDao = UserDao.getInstance();
		mCityDao = new CityDao(this);
		mLoader = new ImageLoader(this);
		mIsEdit = this.getIntent().getIntExtra("edit", 0);
		mIsPwd = this.getIntent().getIntExtra("pwd", 0);
		mIvHeader = (ImageView) mContent.findViewById(R.id.iv_header);
		mEtLoginName = (EditText) mContent.findViewById(R.id.et_loginname);
		mEtUserName = (EditText) mContent.findViewById(R.id.et_username);
		
		mTvPwd = (TextView) mContent.findViewById(R.id.tv_pwd);
		mTvRePwd = (TextView) mContent.findViewById(R.id.tv_re_pwd);
		mLlPwd = (LinearLayout) mContent.findViewById(R.id.ll_pwd);
		mLlRepwd = (LinearLayout) mContent.findViewById(R.id.ll_repwd);
		ll_prompt4 = (LinearLayout) mContent.findViewById(R.id.ll_prompt4);
		
		rl_workstation = (RelativeLayout)mContent.findViewById(R.id.rl_workstation);
		tvSelectWorkStation = (TextView)mContent.findViewById(R.id.tvSelectWorkStation);
		if (mIsPwd == 0) {
			mEtLoginName.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						String loginName = mEtLoginName.getText().toString().trim();
						if (!"".equals(loginName)) {
							new ExistsTask(loginName).execute();
						}
					}
				}

			});
		}
		mEtPwd = (EditText) mContent.findViewById(R.id.et_pwd);
		mEtRepwd = (EditText) mContent.findViewById(R.id.et_re_pwd);
		mEtRerepwd = (EditText) mContent.findViewById(R.id.et_re_re_pwd);
		mEtTel = (EditText) mContent.findViewById(R.id.et_tel);
		mBtnReg = (Button) mContent.findViewById(R.id.btn_reg);
		mBtnEdit = (Button) mContent.findViewById(R.id.btn_edit);
		mLlAboutArea = (LinearLayout) mContent.findViewById(R.id.ll_about_area);
		mLlShowName = (LinearLayout) mContent.findViewById(R.id.ll_showname);
		mLlReRepwd = (LinearLayout) mContent.findViewById(R.id.ll_rerepwd);
		mLlTel = (LinearLayout) mContent.findViewById(R.id.ll_tel);
		mTvCountry = (TextView) mContent.findViewById(R.id.tv_countries);
		mLlCountry = (LinearLayout) mContent.findViewById(R.id.ll_country);
		mLlCountries = (LinearLayout) mContent.findViewById(R.id.ll_countries);

		mIvHeader.setOnClickListener(this);
		mLlAboutArea.setOnClickListener(this);
		rl_workstation.setOnClickListener(this);
		init();
		
		mPdDialog = CustomProgressDialog.createDialog(UserRegActivity.this);
		mPdDialog.setMessage("正在注册...");
		//
		mEtTel.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					ll_prompt4.setVisibility(View.VISIBLE);
				}else{
					ll_prompt4.setVisibility(View.GONE); //
				}
			}
		});
		
		initfocusAcount(); //初始使手机号注册 EditText获得焦点
	}

	private void disenable(View view) {
		view.setEnabled(false);
		view.setBackgroundColor(getResources().getColor(R.color.feed_odd_color));
	}

	public void init() {
		if (mIsEdit == 0) {
			setTvTitle(Utils.getString(this, R.string.new_user_reg));
			mTvPwd.setText("密　码：");
			mTvRePwd.setText("确认密码：");
			mBtnReg.setOnClickListener(this);
		} else {
			if (mIsPwd == 2) {
				mTvPwd.setText("原密码：");
				mTvRePwd.setText("新密码：");
				setTvTitle(Utils.getString(this, R.string.edit_pwd));
				mBtnReg.setVisibility(View.GONE);
				mBtnEdit.setVisibility(View.VISIBLE);
				mIvHeader.setEnabled(false);
				mBtnEdit.setOnClickListener(this);
				disenable(mEtLoginName);

				mUser = mUserDao.get();
				if (mUser != null) {
					if (mUser.headImage != null) {
						mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIvHeader, false);
					}
					if (mUser.showName != null) {
						mEtUserName.setText(mUser.showName);
						disenable(mEtUserName);
					}
					mEtLoginName.setText(mUser.loginName + "");
					if (!"null".equals(mUser.areaCode.trim()) && !"".equals(mUser.areaCode.trim())) {
						String[] codes = mUser.areaCode.split(",");
						StringBuffer sb = new StringBuffer();
						for (String code : codes) {
							sb.append(mCityDao.queryCityName(code) + " ");
						}
						String res = sb.toString();
						res = res.substring(0, res.length() - 1);
						mTvCountry.setText(res);
						disenable(mTvCountry);
					}
					String mobile = mUser.mobile;
					if (mobile != null && !"null".equals(mobile)) {
						mEtTel.setText(mobile);
						disenable(mEtTel);
					}
				}
				mLlShowName.setVisibility(View.GONE);
				mLlReRepwd.setVisibility(View.VISIBLE);
				mLlTel.setVisibility(View.GONE);
				mLlAboutArea.setVisibility(View.GONE);
				mLlCountry.setVisibility(View.GONE);
				mLlCountries.setVisibility(View.GONE);
			} else if (mIsPwd == 1) {
				mLlPwd.setVisibility(View.GONE);
				mLlRepwd.setVisibility(View.GONE);
				setTvTitle(Utils.getString(this, R.string.mod_user));
				mBtnReg.setVisibility(View.GONE);
				mBtnEdit.setVisibility(View.VISIBLE);
				mBtnEdit.setOnClickListener(this);
				mEtLoginName.setEnabled(false);
				mEtLoginName.setBackgroundColor(getResources().getColor(R.color.feed_odd_color));

				mUser = mUserDao.get();
				if (mUser != null) {
					if (mUser.headImage != null) {
						mLoader.DisplayToRoundBitmap(CommonImageUtil.getThumbnailImageUrl(mUser.headImage), mIvHeader, false);
					}
					if (mUser.showName != null) {
						mEtUserName.setText(mUser.showName);
						mEtUserName.setSelection(mUser.showName.length());
					}
					mEtLoginName.setText(mUser.loginName + "");
					if (!"null".equals(mUser.areaCode.trim()) && !"".equals(mUser.areaCode.trim())) {
						String[] codes = mUser.areaCode.split(",");
						StringBuffer sb = new StringBuffer();
						for (String code : codes) {
							sb.append(mCityDao.queryCityName(code) + " ");
						}
						String res = sb.toString();
						res = res.substring(0, res.length() - 1);
						mTvCountry.setText(res);
					}
					String mobile = mUser.mobile;
					if (mobile != null && !"null".equals(mobile))
						mEtTel.setText(mobile);
				}
			}
		}
	}
	/**
	 * 跳转城市选择界面 
	 */
	private void toCitySet(){
		Intent intent = new Intent(this, CitySetActivity.class);
		if (mAreaSelectParameter == null) {
			mAreaSelectParameter = new AreaSelectParameter();
			mAreaSelectParameter.flag = Constant.SELECT_COUNTRY_REQUEST;
			mAreaSelectParameter.isWeatherArea = true;  //是否可以和天气中的地区重复
			mAreaSelectParameter.isSelectMore = true;    //是否可以多选
			mAreaSelectParameter.isShowRemind = false;   //是否用*显示已选择地区
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("parameter", mAreaSelectParameter);
		intent.putExtras(bundle);
		startActivityForResult(intent, Constant.SELECT_COUNTRY_REQUEST);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		Bundle bundle;
		switch (v.getId()) {
		case R.id.ll_about_area:
			if("".equals(mTvCountry.getText().toString())){
				toCitySet();
			} else{
				new AlertDialog.Builder(UserRegActivity.this) 
			    .setTitle("提示").setMessage("您已选择了城市，是否重新选择?")  
			    .setPositiveButton("是",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mTvCountry.setText("");
						toCitySet();
					}
			    })  
			    .setNegativeButton("否",new DialogInterface.OnClickListener() {  
			                @Override  
			                public void onClick(DialogInterface dialog,int which) { 
			                }  
			            }).show();//
			}
			break;
		case R.id.iv_header:
			CommonImageUtil.getInstance(this).getPicture("tmp_header.jpg");
			break;
		case R.id.btn_reg:
			mBtnReg.setEnabled(false);
			if (isValid()) {
				mUser = new User();
				mUser.userName = mUserName; // mShowName;
				mUser.loginName = mTel; // mLoginName;　这里做了个特殊处理让登陆账号直接等于
				mUser.showName = mUserName; // mUserName;
				mUser.password = mPwd;// mPwd;
				mUser.mobile = mTel;
				if (mCities == null || mCities.size() == 0) {
					//CommonUtil.showToask(this, getResources().getString(R.string.no_city));
					mUser.areaCode = "";
				} else {
					StringBuffer sb = new StringBuffer();
					for (City city : mCities) {
						sb.append(city.areacode + ",");
					}
					String res = sb.toString();
					res = res.substring(0, res.length() - 1);
					mUser.areaCode = res;
				}
				//File file = null;
				if (mIsSelected)
					file = new File(mPath);
				new RegisterTask().execute();
				/*User user = mUserDao.addUser(mUser, file);
				if (user != null) {
					CommonUtil.showToask(this, getResources().getString(R.string.reg_success));
					success(user);
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.reg_failture));
				}*/
			}
			mBtnReg.setEnabled(true);
			break;
		case R.id.btn_edit:
			mBtnEdit.setEnabled(false);
			if (isValid()) {
				mUser.userName = mUserName;
				mUser.loginName = mLoginName;
				mUser.showName = mUserName;
				mUser.mobile = mTel;
				if (mCities != null && mCities.size() != 0) {
					StringBuffer sb = new StringBuffer();
					for (City city : mCities) {
						sb.append(city.areacode + ",");
					}
					String res = sb.toString();
					res = res.substring(0, res.length() - 1);
					mUser.areaCode = res;
				}
				//File file = null;
				if (mIsPwd == 1) {
					if (mIsSelected)
						file = new File(mPath);
					new UpdateUserTask().execute();
					/*if (mUserDao.updateUser(mUser, file)) {
						CommonUtil.showToask(this, getResources().getString(R.string.edit_success));
						success(mUser);
					} else {
						CommonUtil.showToask(this, getResources().getString(R.string.edit_failture));
					}*/
				} else if (mIsPwd == 2) {
					new UpdatePasswordTask().execute();
					/*if (mUserDao.updatePassword(mUser.id, mPwd, mRePwd)) {
						CommonUtil.showToask(this, getResources().getString(R.string.edit_pwd_success));
						success(mUser);
					} else {
						CommonUtil.showToask(this, getResources().getString(R.string.edit_pwd_failture));
					}*/
				}
			}
			mBtnEdit.setEnabled(true);
			break;
			case R.id.rl_workstation:
				if(mCities.size() == 0){
					CommonUtil.showToask(this, getResources().getString(R.string.user_reg_pleaseselectcity));
				}else{
					List<String> areaCodes =new ArrayList<String>();
					for(City c : mCities){
						//areacode += c.areacode+",";
						areaCodes.add(c.areacode);
					}
					 intent = new Intent(this, WorkStationSelectActivity.class);
					 bundle = new Bundle();
					 bundle.putSerializable("areas",(Serializable) areaCodes );
					//bundle.putString("parameter", areaCodes);
					intent.putExtra("parameter",bundle);
					startActivityForResult(intent, Constant.SELECT_WORKSTATION);
				}
				
			break;
		}
		super.onClick(v);
	}

	//注册成功后直接将用户名和密码传递过去快捷登录
	private void success(User user) {
		Intent intent = new Intent(this, UserLoginActivity.class);
		Bundle b = new Bundle();
		b.putString("loginname", user.loginName);
		b.putString("password", user.password);
		intent.putExtras(b);

		startActivity(intent);
		this.finish();
		
	}

	private void focusUserName() {
		mEtUserName.setFocusable(true);
		mEtUserName.setFocusableInTouchMode(true);
		mEtUserName.clearFocus();
		mEtUserName.requestFocus();
		mEtUserName.setSelection(mEtUserName.length());
	}

	private void focusLoginName() {
		mEtLoginName.setFocusable(true);
		mEtLoginName.setFocusableInTouchMode(true);
		mEtLoginName.clearFocus();
		mEtLoginName.requestFocus();
		mEtLoginName.setSelection(mEtLoginName.length());
	}

	private void focusPwd() {
		mEtPwd.setFocusable(true);
		mEtPwd.setFocusableInTouchMode(true);
		mEtPwd.clearFocus();
		mEtPwd.requestFocus();
		mEtPwd.setSelection(mEtPwd.length());
	}

	private void focusRepwd() {
		mEtRepwd.setFocusable(true);
		mEtRepwd.setFocusableInTouchMode(true);
		mEtRepwd.clearFocus();
		mEtRepwd.requestFocus();
		mEtRepwd.setSelection(mEtRepwd.length());
	}

	private void focusRerepwd() {
		mEtRerepwd.setFocusable(true);
		mEtRerepwd.setFocusableInTouchMode(true);
		mEtRerepwd.clearFocus();
		mEtRerepwd.requestFocus();
		mEtRerepwd.setSelection(mEtRerepwd.length());
	}

	private void focusTel() {
		mEtTel.setFocusable(true);
		mEtTel.setFocusableInTouchMode(true);
		mEtTel.clearFocus();
		mEtTel.requestFocus();
		mEtTel.setSelection(mEtTel.length());
	}
	
	private void focusCity() {
		mTvCountry.setFocusable(true);
		mTvCountry.setFocusableInTouchMode(true);
		mTvCountry.clearFocus();
		mTvCountry.requestFocus();
		//mTvCountry.setSelection(mTvCountry.length());
	}

	private boolean isValid() {
		mUserName = mEtUserName.getText().toString().trim();
		//mLoginName = mEtLoginName.getText().toString().trim();
		mLoginName = mEtTel.getText().toString();//这里用手机注册，账号默认为手机号
		mPwd = mEtPwd.getText().toString().trim();
		mRePwd = mEtRepwd.getText().toString();
		mRerePwd = mEtRerepwd.getText().toString();
		if (mIsPwd == 1) {
			mPwd = mUser.password;
			mRePwd = mUser.password;
		}
		mTel = mEtTel.getText().toString();
		if ("".equals(mUserName)) {
			focusUserName();
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_showname));
			return false;
		}
		if ("".equals(mLoginName)) {
			focusLoginName();
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_loginname));
			return false;
		}
		if (mIsEdit == 0) {
			if ("".equals(mPwd)) {
				focusPwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_no_pwd));
				return false;
			}
			if ("".equals(mTvCountry.getText().toString())) {
					focusCity();
					CommonUtil.showToask(this, getResources().getString(R.string.no_city));
					return false;
			}
			if (mPwd.length() < 6) {
				focusPwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_no_pwd_length));
				return false;
			}
			if (!mPwd.equals(mRePwd)) {
				focusRepwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_equal_re_pwd));
				return false;
			}
		} else {
			if (!mPwd.equals(mUser.password)) {
				focusPwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_old_error_pwd));
				return false;
			}			
			if (mRePwd.length() < 6) {
				focusRepwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_new_no_pwd_length));
				return false;
			}
			if ("".equals(mRePwd)) {
				focusRepwd();
				CommonUtil.showToask(this, getResources().getString(R.string.user_new_no_pwd_length));
				return false;
			}
			if (mIsPwd == 2) {
				if (!mRePwd.equals(mRerePwd)) {
					focusRerepwd();
					CommonUtil.showToask(this, getResources().getString(R.string.user_equal_re_pwd));
					return false;
				}
			}
		}
		if (!CommonUtil.isMobileNO(mTel)) {
			focusTel();
			CommonUtil.showToask(this, getResources().getString(R.string.user_no_tel));
			return false;
		}
		return true;
	}
	
	private class RegisterTask extends AsyncTask<Integer, Integer, User>
	{
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.setMessage("正在注册...");
				mPdDialog.show();
			}
		}
		
		@Override
		protected User doInBackground(Integer... params) {
			User user = mUserDao.addUser(mUser, file);		
			return user;
		}
		
		@Override
		protected void onPostExecute(User user) {
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
			}
			if (user != null) {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.reg_success));
				success(user);
			} else {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.reg_failture));
			}
		}
	}
	
	private class UpdateUserTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.setMessage("更新信息中...");
				mPdDialog.show();
			}
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			Boolean isSuccess= mUserDao.updateUser(mUser, file);
			return isSuccess;
		}
		
		@Override
		protected void onPostExecute(Boolean isSuccess) {
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
			}
			if (isSuccess) {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.edit_success));
				success(mUser);
			} else {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.edit_failture));
			}
		}
	}
	
	private class UpdatePasswordTask extends AsyncTask<Integer, Integer, Boolean>
	{
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.setMessage("更新信息中...");
				mPdDialog.show();
			}
		}
	
		@Override
		protected Boolean doInBackground(Integer... params) {
			Boolean isSuccess= mUserDao.updatePassword(mUser.id, mPwd, mRePwd);
			return isSuccess;
		}
		
		@Override
		protected void onPostExecute(Boolean isSuccess) {
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
			}
			if (isSuccess) {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.edit_pwd_success));
				success(mUser);
			} else {
				CommonUtil.showToask(UserRegActivity.this, getResources().getString(R.string.edit_pwd_failture));
			}
		}
	}
}
