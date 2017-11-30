package com.supermap.pisaclient.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.ObjectUtils.Null;

import android.R.color;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.CropSituationAdapter;
import com.supermap.pisaclient.biz.CommonDao;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.CropPeriodDao;
import com.supermap.pisaclient.biz.CropTypeDao;
import com.supermap.pisaclient.biz.CropUploadDao;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.LineEditText;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.Address;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CommonHeight;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.FarmCrop;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.http.HttpHelper;



public class FarmlandCreateActivity extends BaseActivity {
	private FarmlandCreateActivity oThis;
	private View mContent;
	private ImageButton mib;
	private ImageView miv_upload1;
	private ImageView miv_upload2;
	private ImageView miv_upload3;
	private Button mbtn_Upload;
	private Button mbtn_Cancel;
	private ListView mListView;
	private ImageView[] miv_upload = new ImageView[3];
	// private String[] mFilePath = new String[3];
	private List<String> mFilePathList = new ArrayList<String>();
	private String[] mCropDes = { "地区（自动定位）", "种养类型", "种养品种", "生长发育期" };
	private String[] mPlease = { "正在定位...", "请选择种养类型", "请选择种养品种", "请选择发育期", "" };
	private int mIndex = 0;
	private String FILE_PATH;
	private Bitmap bmp;
	private boolean isEdit = false;
	boolean isExit = false;
	private CropType mCropType;
	private List<CropType> mCropTypeList;
	private List<CropType> mGrowTypes;
	private List<CropPeriod> mCropPeroids;

	private String mCropGrowTpye;
	private String mCropGrowTime;
	private CropDao mCropDao;
	private CropTypeDao mCropTypeDao;
	private CropPeriodDao mCropPeriodDao;
	private CropPeriod mCropPeriod;
	private CropUploadDao mCropUploadDao;
	private CommonDao mCommonDao;
	private CustomProgressDialog mPdDialog;
	private String[] mCropTypes;
	private TextView mtv_content;
	// 农情上报信息
	private int mUserID;
	private String mAreaCode;// 地区
	private int mCrop_Type = 0; // 种类
	private int mBreed = 0; // 品种
	private int mGrowPeriod = 0; // 生长期
	private String mDescript;
	private String mArea;
	private String mImDes[];

	// 提示数字
	private TextView mtv1;
	private TextView mtv2;
	private TextView mtv3;
	private TextView mtv4;
	private TextView mtv5;
	
	
	private AreaSelectParameter mAreaSelectParameter = null;
	private String mAddress;
	private double lat;
	private double lng;
	private boolean isSelectCity = false;
	private int mDeleteIndex = -1;
	
	// 提示圆圈
	private ImageView miv1;
	private ImageView miv2;
	private ImageView miv3;
	private ImageView miv4;
	//4个大背景
	
	private LinearLayout mll_1;
	private LinearLayout mll_2;
	private LinearLayout mll_3;
	private LinearLayout mll_4;
	
	private LinearLayout mLL_crop_area ;
	private TextView mtv_area_name;
	private TextView mtv_area_content;
	
	private LinearLayout mLL_crop_type ;
	private TextView mtv_type;
	
	private LinearLayout mLL_crop_breed ;
	private TextView mtv_breed;
	
	private LinearLayout mLL_crop_grow ;
	private TextView mtv_grow;
	
	private EditText mev_content;
	private EditText mev_area;
	private boolean isLoading = false;
	private boolean isUploading = false;
	
	
	private CreateFarmlandTask mCreateFarmlandTask;
	
	private ImageLoader mLoader;
	
	private List<CropPeriod> list = new ArrayList<CropPeriod>();
	private int workStationId=0;
	
	//GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private FarmDao mFarmDao;
	private Farm mFarm;
	private FarmCrop mFarmCrop;
	
	//增加地区选择
	private LinearLayout mLL_crop_cityset ;
	private TextView mtv_cityset_name;
	private TextView mtv_cityset_content;
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("address", mAddress);
		outState.putString("areacode", mAreaCode);
		outState.putInt("mCrop_Type", mCrop_Type);
		outState.putInt("mBreed", mBreed);
		outState.putInt("mGrowPeriod", mGrowPeriod);
		outState.putString("mDescript", mDescript);
		outState.putString("mArea", mArea);
		
		outState.putString("type", mPlease[1]);
		outState.putString("croptype", mPlease[2]);
		outState.putString("grow", mPlease[3]);
		outState.putString("crpdes", mPlease[4]);
		outState.putInt("mIndex", mIndex);
		if (mIndex > 0) {
			for (int i = 0; i < mIndex; i++) {
				outState.putString("str" + i, mFilePathList.get(i));
			}
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mAddress = savedInstanceState.getString("address");
			mAreaCode = savedInstanceState.getString("areacode");
			mCrop_Type = savedInstanceState.getInt("mCrop_Type");
			mBreed = savedInstanceState.getInt("mBreed");
			mGrowPeriod = savedInstanceState.getInt("mGrowPeriod");
			mDescript = savedInstanceState.getString("mDescript");
			mArea = savedInstanceState.getString("mArea");
			
			mPlease[1] = savedInstanceState.getString("type");
			mPlease[2] = savedInstanceState.getString("croptype");
			mPlease[3] = savedInstanceState.getString("grow");
			mPlease[4] = savedInstanceState.getString("crpdes");
			mIndex = savedInstanceState.getInt("mIndex");
			if (mIndex > 0) {
				for (int i = 0; i < mIndex; i++) {
					mFilePathList.add(savedInstanceState.getString("str" + i));
				}
				setImg();
			}
		}

		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mAddress = savedInstanceState.getString("address");
			mAreaCode = savedInstanceState.getString("areacode");
			mCrop_Type = savedInstanceState.getInt("mCrop_Type");
			mBreed = savedInstanceState.getInt("mBreed");
			mGrowPeriod = savedInstanceState.getInt("mGrowPeriod");
			mDescript = savedInstanceState.getString("mDescript");
			mArea = savedInstanceState.getString("mArea");
			mPlease[1] = savedInstanceState.getString("type");
			mPlease[2] = savedInstanceState.getString("croptype");
			mPlease[3] = savedInstanceState.getString("grow");
			mPlease[4] = savedInstanceState.getString("crpdes");
			mIndex = savedInstanceState.getInt("mIndex");
			if (mIndex > 0) {
				for (int i = 0; i < mIndex; i++) {
					mFilePathList.add(savedInstanceState.getString("str" + i));
				}
			}
		}

		ExitApplication.getInstance().addActivity(this);
		setIsBack(true);
		setIsNavigator(false);
		setTvTitle(Utils.getString(this, R.string.farmland_create));

		mContent = inflater(R.layout.farmland_create_main);
		mLL_crop_area = (LinearLayout)mContent.findViewById(R.id.ll_area);
		mLL_crop_cityset = (LinearLayout)mContent.findViewById(R.id.ll_cityset);//add by heyao
		mLL_crop_type = (LinearLayout)mContent.findViewById(R.id.ll_type);
		mLL_crop_breed = (LinearLayout)mContent.findViewById(R.id.ll_breed);
		mLL_crop_grow = (LinearLayout)mContent.findViewById(R.id.ll_grow);
		
		mll_1 = (LinearLayout) mContent.findViewById(R.id.ll_1);
		mll_2 = (LinearLayout) mContent.findViewById(R.id.ll_2);
		mll_3 = (LinearLayout) mContent.findViewById(R.id.ll_3);
		mll_4 = (LinearLayout) mContent.findViewById(R.id.ll_4);
		
		
		mtv_area_name = (TextView) mContent.findViewById(R.id.tv_area_name);
		mtv_cityset_name = (TextView) mContent.findViewById(R.id.tv_cityset_name);//add by heyao
		mtv_area_content = (TextView) mContent.findViewById(R.id.tv_area_content);
		mtv_cityset_content = (TextView) mContent.findViewById(R.id.tv_cityset_content);//add by heyao
		mtv_type = (TextView) mContent.findViewById(R.id.tv_crop_type);
		mtv_breed = (TextView) mContent.findViewById(R.id.tv_crop_breed);
		mtv_grow = (TextView) mContent.findViewById(R.id.tv_crop_grow);
		
		mev_content = (EditText)mContent.findViewById(R.id.et_crop_content);
		mev_area = (EditText)mContent.findViewById(R.id.et_farm_area);
		mtv_area_content.setText(mPlease[0]);
		
		mbtn_Upload = (Button) mContent.findViewById(R.id.bn_crop_upload);
		mbtn_Cancel = (Button) mContent.findViewById(R.id.bn_crop_cancel);
		miv_upload1 = (ImageView) mContent.findViewById(R.id.iv_upload_1);
		miv_upload2 = (ImageView) mContent.findViewById(R.id.iv_upload_2);
		miv_upload3 = (ImageView) mContent.findViewById(R.id.iv_upload_3);

		
		miv_upload[0] = miv_upload1;
		miv_upload[1] = miv_upload2;
		miv_upload[2] = miv_upload3;
		
		mLL_crop_area.setOnClickListener(this);
		mLL_crop_cityset.setOnClickListener(this);
		mLL_crop_type.setOnClickListener(this);
		mLL_crop_breed .setOnClickListener(this);
		mLL_crop_grow.setOnClickListener(this);
		
		mtv_area_name .setOnClickListener(this);
		mtv_area_content .setOnClickListener(this);
		mtv_type .setOnClickListener(this);
		mtv_breed.setOnClickListener(this);
		mtv_grow.setOnClickListener(this);
		
		miv_upload1.setOnClickListener(this);
		miv_upload2.setOnClickListener(this);
		miv_upload3.setOnClickListener(this);
		
		miv_upload1.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv_upload2.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv_upload3.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv1 = (ImageView) mContent.findViewById(R.id.iv_crop_area);
		miv2 = (ImageView) mContent.findViewById(R.id.iv_crop_type);
		miv3 = (ImageView) mContent.findViewById(R.id.iv_crop_breed);
		miv4 = (ImageView) mContent.findViewById(R.id.iv_crop_grow);

		mCropDao = new CropDao();
		mCropTypeDao = new CropTypeDao();
		mCropPeriodDao = new CropPeriodDao();
		mCropUploadDao = new CropUploadDao();
		mFarmDao = new FarmDao();
		mCommonDao = CommonDao.getInstance();
		mbtn_Upload.setOnClickListener(this);
		mbtn_Cancel.setOnClickListener(this);
		setBackOnClickListener(this);

		mPdDialog = CustomProgressDialog.createDialog(FarmlandCreateActivity.this);
		mPdDialog.setMessage(getResources().getString(R.string.upload_crop_data));
		LocalHelper.getInstance(this).init();
		if (mIndex > 0) {
			setImg();
		}
		
		mLoader = new ImageLoader(this);
		mCropTypeList = new ArrayList<CropType>();
		mCropPeroids  = new ArrayList<CropPeriod>();
		mFarm = new Farm();
		new LoadLocalArea().execute();
		new LoadCropTypeInfo().execute();
		
		// 初始化搜索模块，注册事件监听
		//mSearch = GeoCoder.newInstance();
		//mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
	}

	OnGetGeoCoderResultListener onGetGeoCoderResultListener=new OnGetGeoCoderResultListener()
	{
		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			//地理编码
		}
		@Override// 反向地理编码
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			mAddress= result.getAddress();	
		}		
	};
	
	class DeleteImgOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View view) {
			switch (view.getId()) {
			case R.id.iv_upload_1:
				mDeleteIndex = 0;
				break;
			case R.id.iv_upload_2:
				mDeleteIndex = 1;
				break;
			case R.id.iv_upload_3:
				mDeleteIndex = 2;
				break;
			default:
				break;
			}
			if (mDeleteIndex < mIndex) {
				showDeleteImgDia();
			}
			return false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		LocalHelper.getInstance(this).close();
		super.onDestroy();
	}
	
	private void getPointFromMap()
	{
		Intent intent=new Intent(FarmlandCreateActivity.this,MapActivity.class);
		Bundle bundle = new Bundle();
		double[] lonlat={lng,lat};
		bundle.putDoubleArray("lonlat", lonlat);
		intent.putExtras(bundle);
		this.startActivityForResult(intent, Constant.MAP_POINT_REQUEST);
	}
	
	private void seletArea(){
		Intent intent = new Intent(FarmlandCreateActivity.this, CitySetActivity.class);
		if (mAreaSelectParameter == null) {
			mAreaSelectParameter = new AreaSelectParameter();
			mAreaSelectParameter.flag = Constant.CROPS_UPLOAD_REQUEST;
			mAreaSelectParameter.isWeatherArea = false;
			mAreaSelectParameter.isSelectMore = false;
			mAreaSelectParameter.isShowRemind = false;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("parameter", mAreaSelectParameter);
		intent.putExtras(bundle);
		startActivityForResult(intent, Constant.CROPS_UPLOAD_REQUEST);
	}

	private void setNoBreed(){
		mBreed = 0;
		mGrowTypes = null;
		miv3.setImageResource(R.drawable.crop_no_select);
		mll_3.setBackgroundColor(Color.parseColor("#f6f6f6"));
		mPlease[2] = "请选择种养品种";
		mtv_breed.setText(mPlease[2]);
	}
	
	private void setNoGrowPeriod(){
		mGrowPeriod = 0;
//		mCropPeroids = null; 
		miv4.setImageResource(R.drawable.crop_no_select);
		mll_4.setBackgroundColor(Color.parseColor("#f6f6f6"));
		mPlease[3] = "请选择发育期";
		//mtv_grow.setText(mPlease[3]);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_cityset:
			toCitySet();
			break;
		case R.id.ll_area://区域选择
		case R.id.tv_area_content:
				/*if (isSelectCity) {
					seletArea();
				}*/
				//seletArea();
				getPointFromMap();
				break;
		case R.id.ll_type://种养类型
		case R.id.tv_crop_type:
			
			
			if ((mAreaCode == null)||("null".equals(mAreaCode))) {
				CommonUtil.showToask(FarmlandCreateActivity.this, "请先选择地区");
			} else {
				if (isLoading) {
					CommonUtil.showToask(FarmlandCreateActivity.this, "正在加载中...");
				}
				else {
					new LoadAgrTypeInfoTask(1).execute();
					isLoading = true;
				}
				
			}
			break;
		case R.id.ll_breed://种养品种
		case R.id.tv_crop_breed:
			
			if (mCrop_Type == 0) {
				CommonUtil.showToask(FarmlandCreateActivity.this, "请先选择农作物种养类型");
			} else {
				if (isLoading) {
					CommonUtil.showToask(FarmlandCreateActivity.this, "正在加载中...");
				}
				else {
					new LoadAgrTypeInfoTask(2).execute();
					isLoading = true;
				}
			}
			break;
		case R.id.ll_grow://生长发育期
		case R.id.tv_crop_grow:
			List<String> areaCodes =new ArrayList<String>();
			areaCodes.add(UserDao.getInstance().get().areaCode);
			 Intent  intent = new Intent(this, WorkStationSelectActivity.class);
			 Bundle bundle = new Bundle();
			 bundle.putSerializable("areas",(Serializable) areaCodes );

			intent.putExtra("parameter",bundle);
			startActivityForResult(intent, Constant.SELECT_WORKSTATION);
			
			break;
		case R.id.iv_upload_1:
		case R.id.iv_upload_2:
		case R.id.iv_upload_3:
			CommonImageUtil.getInstance(FarmlandCreateActivity.this).getPicture(mIndex + "");
			break;
		case R.id.ibtn_back:
			finish();
			break;
		case R.id.bn_crop_upload:
			if (vaild()) {
				//通知上报农情，跟主动上报农情分别处理
//				com.supermap.pisaclient.entity.Address address = LocalHelper.getInstance(this).getAddress();
//				lat = address.lat;
//				lng = address.lng;
				if (!isUploading) {
					isUploading = true;
					
					mCreateFarmlandTask =new CreateFarmlandTask();
					mCreateFarmlandTask.execute(new String[]{
							
					});
					
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							try {
//								mUploadAgrTask.get(1000*5, TimeUnit.MILLISECONDS);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (ExecutionException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (TimeoutException e) {
////								CommonUtil.showToask(FarmlandCreateActivity.this, "上传超时");
//								System.out.println("上传超时");
//								mUploadAgrTask.cancel(true);
//							}
//							
//						}
//					}).start();
				}
				else {
					CommonUtil.showToask(this, "农田创建中...");
				}
			}
			break;
			
		case R.id.bn_crop_cancel:
			cancel();
			break;
		}
		super.onClick(v);
	}

	
	private void cancel(){
		
		isEdit = false;
		
//		if (isSelectCity) {
//			mAreaCode = null;
//			mtv_area_content.setText("请选择地区");
//			miv1.setImageResource(R.drawable.crop_no_select);
//			mll_1.setBackgroundColor(Color.parseColor("#f6f6f6"));
//		}
		miv2.setImageResource(R.drawable.crop_no_select);
		mll_2.setBackgroundColor(Color.parseColor("#f6f6f6"));
		miv3.setImageResource(R.drawable.crop_no_select);
		mll_3.setBackgroundColor(Color.parseColor("#f6f6f6"));
		//miv4.setImageResource(R.drawable.crop_no_select);
		mll_4.setBackgroundColor(Color.parseColor("#f6f6f6"));
		
		
		
		mtv_type.setText("请选择种养类型");
		mtv_breed.setText("请选择种养品种");
	//	mtv_grow.setText("请选择生长发育期");
		mev_content.setText("");
		mev_content.setHint("请输入农田名称");
		mev_area.setText("");
		mev_area.setHint("请输入农田面积（亩）");
		mCrop_Type =0;
		mBreed = 0;
		mGrowPeriod =0;
		mDescript =null;
		mArea=null;
		
		mCropType = null;
		mCropPeriod =null;
		mDescript = null;
		
		mIndex =0;
		setImg();
		mFilePathList.clear();
				
		
	}
	
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
	
	private boolean vaild() {
		//地区，作物类型，农情描述是必须的
		mDescript = mev_content.getEditableText().toString();
		mDescript = mDescript.replaceAll("[\\t\\n\\r]", "");
		mArea = mev_area.getEditableText().toString();
		
		if ((mAreaCode == null)||("null".equals(mAreaCode)) ){
			CommonUtil.showToask(this, "自动定位地区不在服务范围内，请手动点击选择");
			return false;
		} else if (mCrop_Type == 0) {
			CommonUtil.showToask(this, "请选择农作物类型");
			return false;
//		} else if (mBreed == 0) {
//			CommonUtil.showToask(this, "请选择农作物品种");
//			return false;
		} 
		/*else if ((mGrowPeriod == 0) && (mBreed != 74)) {//必须上传发育期 水产不用上传发育期
			CommonUtil.showToask(this, "请选择作物生成发育期");
			return false;
		} */
		else if ((mDescript == null) || ("".equals(mDescript))) {
			CommonUtil.showToask(this, "请添加农田描述");
			return false;
		} else if(isUploading){
			CommonUtil.showToask(this, "正在上传农田信息...");
			return false; 
		} else if ((mArea == null) || ("".equals(mArea))) {
			CommonUtil.showToask(this, "请填写农田面积");
			return false;
		} 
		
		return true;
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
                    Uri uri_temp = Uri    
                            .parse("content://media/external/images/media/"    
                                    + index);    
                    if (uri_temp != null) {    
                        uri = uri_temp;    
                    }    
                }    
            }    
        }    
        return uri;    
    }    

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((resultCode == RESULT_OK) && (requestCode == Constant.IMAGE_CAPTURE_REQUEST)) {
			FILE_PATH = CommonImageUtil.getInstance(FarmlandCreateActivity.this).getPicturePath();
			if (FILE_PATH != null) {
				/*int degree=CommonImageUtil.getBitmapDegree(FILE_PATH);
				if(degree!=0)
				{
					Bitmap bm= BitmapFactory.decodeFile(FILE_PATH);
					//Bitmap resizeBitmap=CommonImageUtil.rotateBitmapByDegree(bm, degree);
					Bitmap resizeBitmap= CommonImageUtil.rotateBitmap(bm, degree);
					CommonImageUtil.saveNewBitmap(FILE_PATH,resizeBitmap);
					degree=CommonImageUtil.getBitmapDegree(FILE_PATH);
				}		*/		
				mFilePathList.add(FILE_PATH);
				mIndex++;
				isEdit = true;
				setImg();
			}

		}
		if ((resultCode == RESULT_OK) && (requestCode == Constant.IMAGE_GET_REQUEST)) {
			Uri uri = data.getData();
			uri = geturi(data);//解决方案  
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			//Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			if (path != null) {
				/*int degree=CommonImageUtil.getBitmapDegree(path);
				if(degree!=0)
				{
					Bitmap bm= BitmapFactory.decodeFile(path);
					//Bitmap resizeBitmap= CommonImageUtil.rotateBitmapByDegree(bm, degree);
					Bitmap resizeBitmap= CommonImageUtil.rotateBitmap(bm, degree);
					CommonImageUtil.saveNewBitmap(path,resizeBitmap);
					degree=CommonImageUtil.getBitmapDegree(path);
				}	*/
				mFilePathList.add(path);
				mIndex++;
				isEdit = true;
				setImg();
			}

		}

		if ((resultCode == Constant.CITY_SET_RESULT) && (requestCode == Constant.CROPS_UPLOAD_REQUEST)) {
			// City city = (City) data.getSerializableExtra("city");
			List<City> list = (List<City>) data.getSerializableExtra("city");
			if ((list != null) && (list.size() > 0)) {
				City city = list.get(0);
//				mtv_content.setText(city.name);
				mtv_area_content.setText(city.name);
				miv1.setImageResource(R.drawable.crop_selected);
				mll_1.setBackgroundColor(Color.parseColor("#eff0f2"));
				
				mAreaCode = city.areacode;
				mAddress = city.name;
				mPlease[0] = mAddress;
			}
		}

		if((resultCode==Constant.MAP_POINT_RESULT)&&(requestCode==Constant.MAP_POINT_REQUEST))
		{
			double[] lnglat= data.getDoubleArrayExtra("LngLat");
			 lng=lnglat[0];
			 lat=lnglat[1];
			 
			 Toast.makeText(FarmlandCreateActivity.this, "坐标已修改为："+getNewValue(lng)+","+getNewValue(lat), Toast.LENGTH_SHORT).show();
			 new LoadHeightTask().execute(new String[] { String.valueOf(lng), String.valueOf(lat) });
			 new ReloadLocalArea().execute();
		}
	if(resultCode == Constant.SELECT_WORKSTATION){
			Bundle b = data.getExtras();
			workStationId = b.getInt("workstationid");
			String workStationName = b.getString("workstationname");
			mtv_grow.setText(workStationName);
			
		}
		if(resultCode==Constant.CITY_SET_RESULT){
		List<City> list = (List<City>) data.getSerializableExtra("city");
		if ((list != null) && (list.size() > 0)) {
			City city = list.get(0);
			mtv_cityset_content.setText(city.name);
			miv1.setImageResource(R.drawable.crop_selected);
			mll_1.setBackgroundColor(Color.parseColor("#eff0f2"));
			mAreaCode = city.areacode;
			mAddress = city.name;
			mPlease[0] = mAddress;
		}
	}
	
	}
	
	private double getNewValue(double value)
	{
		long tempValue=  Math.round(value*10000);   //四舍五入
		double mValue=tempValue/10000.0;
		return mValue;
	}

	private void setImg() {

		for (int i = 1; i <= mIndex; i++) {// 设置图片
			//bmp = CommonImageUtil.getInstance(FarmlandCreateActivity.this).decodeFile(mFilePathList.get(i - 1), 60, 60);
			//bmp = CommonImageUtil.getInstance(FarmlandCreateActivity.this).cutBmp(bmp);
			bmp=CommonImageUtil.getInstance(FarmlandCreateActivity.this).getImageThumbnail(mFilePathList.get(i - 1), 60, 60);
			miv_upload[i - 1].setImageBitmap(bmp);
			miv_upload[i - 1].setVisibility(View.VISIBLE);
		}
		if (mIndex < 3) {// 设置加图
			miv_upload[mIndex].setImageResource(R.drawable.adv_imge);
			miv_upload[mIndex].setVisibility(View.VISIBLE);
		}
		if (mIndex + 1 < 3) {// 隐藏view
			for (int i = mIndex + 1; i < 3; i++) {
				miv_upload[i].setVisibility(View.INVISIBLE);
			}
		}
	}

	private boolean deleteImag() {
		// mFilePath[mDeleteIndex] = null;
		mFilePathList.remove(mDeleteIndex);
		mIndex--;
		setImg();
		return true;
	}

	private void showDeleteImgDia() {
		new AlertDialog.Builder(FarmlandCreateActivity.this).setTitle("删除图片").setMessage("是否删除此图片?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						deleteImag();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();//
	}

	@Override
	public void onBackPressed() {

		if (isEdit) {
			new AlertDialog.Builder(FarmlandCreateActivity.this).setTitle("农情上报").setMessage("放弃此次编辑?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							FarmlandCreateActivity.this.finish();
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();//
		} else {
			FarmlandCreateActivity.this.finish();
		}
	}



	private class LoadAgrTypeInfoTask extends AsyncTask<Integer, Integer, Boolean> {

		private int mType = 0;
		private boolean mLoadPass = false;

		public LoadAgrTypeInfoTask(int type) {
			this.mType = type;
		}

		@Override
		protected Boolean doInBackground(Integer... arg0) {
			switch (mType) {
			case 1:// 获取种养类型
				/*if ((mCropTypeList!=null)&&(mCropTypeList.size()>0)) {
					mLoadPass = true;
				}
				else {
					mCropTypeList = mCropTypeDao.getCropTypes(0,mAreaCode);
					if (mCropTypeList != null) {
						mLoadPass = true;
					}
				}*/
				//mCropTypeList = mCropTypeDao.getCropTypes(mAreaCode.substring(0,6));
				mCropTypeList = mCropTypeDao.getCropTypes("50");
				if (mCropTypeList != null) {
					mLoadPass = true;
				}
				break;
			case 2:// 获取品种类型
				if ((mGrowTypes!=null)&&(mGrowTypes.size()>0)) {
					mLoadPass = true;
				}
				else {
					mGrowTypes = mCropTypeDao.getCropTypes(mCropType.id);
					if (mGrowTypes != null) {
						mLoadPass = true;
					}
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
				// CommonUtil.showToask(FarmlandCreateActivity.this, "服务器提出了一个问题");
			}
			isLoading = false;
			switch (mType) {
			case 1:// 种养类型选择
				if (mCropTypeList != null && mCropTypeList.size() > 0) {
					String[] data = new String[mCropTypeList.size()];
					for (int i = 0; i < mCropTypeList.size(); i++) {
						data[i] = mCropTypeList.get(i).name;
					}
					new AlertDialog.Builder(FarmlandCreateActivity.this).setItems(data, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropType = mCropTypeList.get(which);
							mCrop_Type = mCropType.id;
							mtv_type.setText(mCropType.name);
							miv2.setImageResource(R.drawable.crop_selected);
							mll_2.setBackgroundColor(Color.parseColor("#eff0f2"));
							mPlease[1] = mCropType.name;
							setNoBreed();
							//setNoGrowPeriod();
//							mtv2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_circle_pressed));
						}

					}).show();
				}
				break;
			case 2:
				if (mGrowTypes != null && mGrowTypes.size() > 0) {
					String[] data = new String[mGrowTypes.size()];
					for (int i = 0; i < mGrowTypes.size(); i++) {
						data[i] = mGrowTypes.get(i).name;
					}
					new AlertDialog.Builder(FarmlandCreateActivity.this).setItems(data, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropType = mGrowTypes.get(which);
							mBreed = mCropType.id;//种养品种的ID，即为生长发育期的父ID
//							mtv_content.setText(mCropType.name);
							mtv_breed.setText(mCropType.name);
							miv3.setImageResource(R.drawable.crop_selected);
							mll_3.setBackgroundColor(Color.parseColor("#eff0f2"));
							mPlease[2] = mCropType.name;
//							mtv3.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_circle_pressed));
						}

					}).show();
				}
				break;
			case 3:

				if (mCropPeroids != null && mCropPeroids.size() > 0) {
//					String[] data = new String[mCropPeroids.size()];
//					for (int i = 0; i < mCropPeroids.size(); i++) {
//						data[i] = mCropPeroids.get(i).growthperiod;
//					}
					list.clear();
					for (int i = 0; i < mCropPeroids.size(); i++) {
						//if (mCropPeroids.get(i).cropsType==mCrop_Type) {
						if (mCropPeroids.get(i).cropsType==mBreed) {
							list.add(mCropPeroids.get(i));
						}
					}
					String[] data = new String[list.size()];
					for (int i = 0; i < list.size(); i++) {
						data[i] = list.get(i).growthperiod;
					}
					
					
					new AlertDialog.Builder(FarmlandCreateActivity.this).setItems(data, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mCropPeriod = list.get(which);
							mGrowPeriod = mCropPeriod.id;
//							mtv_content.setText(mCropPeriod.growthperiod);
//                          mtv_grow.setText(mCropPeriod.growthperiod);
							miv4.setImageResource(R.drawable.crop_selected);
							mll_4.setBackgroundColor(Color.parseColor("#eff0f2"));
							mPlease[3] = mCropPeriod.growthperiod;
//							mtv4.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_circle_pressed));
						}
					}).show();
				}
				break;
			default:
				break;
			}
		}
	}

	
	private class LoadCropTypeInfo extends AsyncTask<String, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			mCropDao.getCropTypeInfoInCurrentTime(mCropTypeList, mCropPeroids);
			return true;
		}
		
	}
	
	/**
	 * 重新获取位置信息
	 * @author trq
	 *
	 */
	private class ReloadLocalArea extends AsyncTask<String,Integer,Boolean>
	{
		@Override
		protected Boolean doInBackground(String... arg0) {
			Address mAddress= LocalHelper.getInstance(FarmlandCreateActivity.this).getAddress(lng,lat);
			if (mAddress!=null) {
				mAreaCode =mAddress.code;
				lat = mAddress.lat;
				lng = mAddress.lng;
				FarmlandCreateActivity.this.mAddress = mAddress.address;
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if ((mAreaCode == null)||("null".equals(mAreaCode))) {
					mPlease[0] =  "(不在服务范围内)";
					isSelectCity = true;
				} else {
					//mPlease[0] = mAddress;
					mPlease[0] = lng+","+lat;
				}
			}
			else {
				isSelectCity = true;
				mPlease[0] =  "请选择地区";
			}
			
			if (!isSelectCity) {	//不需要手动选地区
				mtv_area_content.setText(mPlease[0]);
				miv1.setImageResource(R.drawable.crop_selected);
				mll_1.setBackgroundColor(Color.parseColor("#eff0f2"));
			}
			else {
				mtv_area_content.setText(mPlease[0]);
			}
			

			//LatLng ptCenter = new LatLng(lat,lng);
			// 反Geo搜索
			//mSearch.reverseGeoCode(new ReverseGeoCodeOption()
			//		.location(ptCenter));
			
		}
		
	}
	
	private class LoadLocalArea extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			double [] lnglat= LocalHelper.getInstance(FarmlandCreateActivity.this).getLngLat();
			Address mAddress= LocalHelper.getInstance(FarmlandCreateActivity.this).getAddress(lnglat[0],lnglat[1]);
			//Address mAddress= LocalHelper.getInstance(FarmlandCreateActivity.this).getAddress();
			if (mAddress!=null) {
				mAreaCode =mAddress.code;
				lat = mAddress.lat;
				lng = mAddress.lng;
				FarmlandCreateActivity.this.mAddress = mAddress.address;
				new LoadHeightTask().execute(new String[] {String.valueOf(lng), String.valueOf(lat) });
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if ((mAreaCode == null)||("null".equals(mAreaCode))) {
					mPlease[0] =  "(不在服务范围内)";
					isSelectCity = true;
				} else {
					//mPlease[0] = mAddress;
					mPlease[0] = lng+","+lat;
				}
			}
			else {
				isSelectCity = true;
				mPlease[0] =  "请选择地区";
			}
			
			if (!isSelectCity) {	//不需要手动选地区
				mtv_area_content.setText(mPlease[0]);
				miv1.setImageResource(R.drawable.crop_selected);
				mll_1.setBackgroundColor(Color.parseColor("#eff0f2"));
			}
			else {
				mtv_area_content.setText(mPlease[0]);
			}
			
		}
	}
	
	/**
	 * 1.新增农田
	 * 2.上传失败，重传处理待优化
	 * @author kael
	 *
	 */
	private class CreateFarmlandTask extends AsyncTask<String, Integer, Boolean> {
		
		private int farmLandId = 0;
	
		@Override
		protected void onPreExecute() {
			if (!mPdDialog.isShowing()) {
				mPdDialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			
			File file = null;
			String imageURLContent = null;
			int userid = -1;	//-1默认为匿名用户
			User user  = UserDao.getInstance().get();
			if (user!=null) {
				userid = user.id;
			}
			else
			{
				return false;
			}
			
			
			List<String> mImagPathList = new ArrayList<String>();
			for (String string : mFilePathList) {
				if (string != null) {
					file = new File(string);
					// 上传图片
					imageURLContent = HttpHelper.loadPic(mLoader.getPicInputStream(file), HttpHelper.ADD_PIC_URL);
					if (!imageURLContent.equals(HttpHelper.FAILURE)) {
						mImagPathList.add(imageURLContent);
					}
					else {
						return false;
					}
				}
			}
			
			mFarm.latitude = String.valueOf(lat);
			mFarm.longitude  = String.valueOf(lng);
			 double lat3len = round(lat, 3);
			 double lng3len = round(lng, 3);
			 
			 
			 
			mFarm.coordinates = ""+ (lng3len-0.001) +","+ (lat3len+0.001) +" "+(lng3len+0.001)+","+(lat3len+0.001)+" "+ (lng3len+0.001) +","+(lat3len-0.001)+" "+ (lng3len-0.001) +","+ (lat3len-0.001) +"";
			
			mFarm.areaCode  = mAreaCode;
			mFarm.cropTypeId = String.valueOf(mBreed);
			mFarm.userId = user.id;
			mFarm.descript = mDescript;
			mFarm.workStationId = workStationId;
			mFarm.area = String.valueOf(Double.parseDouble(mArea) *3000/2);
	
			/*mFarm = new Farm();
			mFarm.latitude = String.valueOf(lat);
			mFarm.longitude = String.valueOf(lon);
			
			mFarm.descript = farm.descript;
			String code = farm.areaCode;
			if (code != null & !"".equals(code) && code.length() == 12) {
				mFarm.areaCode = code.substring(0, code.length() - 3);
			} else {
				mFarm.areaCode = code;
			}*/
			
			//添加农田信息
			String res = mFarmDao.addFarmland(mFarm);
		
			if (!"0".equals(res)) {
				mFarmCrop = new FarmCrop();
				mFarmCrop.crop = String.valueOf(mBreed);
				mFarmCrop.agrweaquota = String.valueOf(0);
				
				if (mFarmCrop.crop != null && !"".equals(mFarmCrop.crop) && !"null".equals(mFarmCrop.crop)
						&& mFarmCrop.agrweaquota != null && !"".equals(mFarmCrop.agrweaquota)
						&& !"null".equals(mFarmCrop.agrweaquota)) {
					mFarmCrop.farmland = res;
					//添加农田作物关联信息
					mFarmDao.addFarmCrops(mFarmCrop);
				}
				
			} else {
				CommonUtil.showToask(oThis, getResources().getString(R.string.insert_farm_scrop_bad));
			}
			
			//上传农田信息
			/*agrInfoId = mCropUploadDao.addAgrInfo(userid, mAreaCode, mCrop_Type, mBreed, mGrowPeriod, mDescript, lng,
					lat);
			if (agrInfoId == 0) {
				return false;
			} */
			//to-do 优化
			for(String imagpath :mImagPathList){
//				 上传图片的位置信息
				/*int result = mFarmDao.addFarmImg(mDescript, Integer.parseInt(mFarmCrop.farmland), imagpath);
				if (result==0) {
					return false;
				}*/
			}
			
			
			return true;
		}
		public  double  round(double   v,int   scale){      
		       if(scale<0){      
		               throw   new   IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");      
		       }      
		       BigDecimal   b   =   new   BigDecimal(Double.toString(v));      
		       BigDecimal   one   =   new   BigDecimal("1");      
		       return   b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();      
		}  

		@Override
		protected void onPostExecute(Boolean result) {
			if (mPdDialog.isShowing()) {
				mPdDialog.dismiss();
			}
			if (result) {
//				CommonUtil.showToask(FarmlandCreateActivity.this, "农情信息上报成功 农情ID为：" + agrInfoId);
				CommonUtil.showToask(FarmlandCreateActivity.this, "已为您成功添加农田 ");
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				//CommonUtil.showToask(oThis, getResources().getString(R.string.insert_farm_scrop_sucess));
				bundle.putInt("farmid", mFarm.id);
				bundle.putString("descript", mFarm.descript);
				bundle.putString("lat", mFarm.latitude);
				bundle.putString("lon", mFarm.longitude);
				intent.putExtras(bundle);
				setResult(Constant.ADD_FARM_RESULT, intent);
				//finish();
				//FarmlandCreateActivity.this.cancel();
				FarmlandCreateActivity.this.finish();
			} else {
				if(UserDao.getInstance().get()==null)
				{
					CommonUtil.showToask(FarmlandCreateActivity.this, "请先登录再进行农情上报");
				}
				else
				{
					CommonUtil.showToask(FarmlandCreateActivity.this, "农情上报失败，请重新上传");
				}				
			}
			isUploading = false;
		}

	}
	
	
	private class LoadHeightTask extends AsyncTask<String, Integer, CommonHeight> {

		@Override
		protected CommonHeight doInBackground(String... params) {
			return mCommonDao.getHeight(Double.valueOf(params[0]), Double.valueOf(params[1]));
		}

		@Override
		protected void onPostExecute(CommonHeight result) {
			if (result != null) {
				mFarm.height = result.height + "";
			}
		}
	}

}
