package com.supermap.pisaclient.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.supermap.android.data.EditFeatureAction;
import com.supermap.android.data.GetFeaturesResult;
import com.supermap.android.maps.DefaultItemizedOverlay;
import com.supermap.android.maps.LineOverlay;
import com.supermap.android.maps.MapView;
import com.supermap.android.maps.Overlay;
import com.supermap.android.maps.OverlayItem;
import com.supermap.android.maps.Point2D;
import com.supermap.android.maps.PolygonOverlay;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.FarmDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.FarmCache;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DataUtil;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.FileHelper;
import com.supermap.pisaclient.common.FileManager;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.PisaLayerView;
import com.supermap.pisaclient.entity.Address;
import com.supermap.pisaclient.entity.Farm;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.service.PreferencesService;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.GeometryType;

public class FarmManagerActivity extends BaseActivity implements OnClickListener {
	
	private static final int EDITFEATURE_DIALOG = 0;
	private static final int README_DIALOG = 9;
	protected MapView mapView;
	private PisaLayerView satteliteView;
	private PisaLayerView map2dView;
	private PisaLayerView labelView;
	private PisaLayerView terrainLayerView;
	private PreferencesService service;
	private FarmDao dao;
	private List<Point2D> geoPoints = new ArrayList<Point2D>();
	private PolygonOverlay polygonOverlay;
	private List<PolygonOverlay> polygonOverlays = new ArrayList<PolygonOverlay>();
	private AddTouchOverlay touchOverlay;
	private SelectedTouchOverlay selectedtouchOverlay;
	private PolygonOverlay selectedolygonOverlay;
	private int touchX;
	private int touchY;
	private Feature selectFeature;
	private List<Feature> allFeatures = new ArrayList<Feature>();
	private int editStatus = -1;// 0代表添加一个农田，1代表更新一个要素农田，2代表删除一个要素，3代表更新一个要素属性，-1代表未编辑一个农田
	private Map<String, List<Point2D>> featureMap = new HashMap<String, List<Point2D>>();
	private List<EditFeatureAction> actionList = new ArrayList<EditFeatureAction>();
	private Button helpBtn;
	protected int titleBarHeight;
	private FarmCache mFarmCache;

	protected RadioGroup mRgNavigator;
	private LinearLayout mLlSelect; // 7 选择
	private LinearLayout mLlAdd; // 0 添加
	private LinearLayout mLlEdit; // 1 编辑
	private LinearLayout mLlDelete; // 2 删除
	private LinearLayout mLlSelectMap;
	private LinearLayout mLlSave; // 5 完成
	private LinearLayout mLlCancel; // 6 撤销
	private LinearLayout mLlEditBorder; // 5 完成

	private RadioButton mRadSelect;
	private RadioButton mRadAdd;
	private RadioButton mRadEdit;
	private RadioButton mRadDelete;
	private RadioButton mRadSave;
	private RadioButton mRadCancel;
	private RadioButton mRadEditBorder;

	private ImageView mIvSelect;
	private ImageView mIvAdd;
	private ImageView mIvEdit;
	private ImageView mIvDelete;
	private ImageView mIvSave;
	private ImageView mIvCancel;
	private ImageView mIvEditBorder;

	private ImageView mIvSattelite;
	private ImageView mIvTerrain;
	private Button mBtnLocation;
	private Button mBtnFarmList;
	private Button mBtnChangeMap;
	private View mContent;
	private CustomProgressDialog mPdDialog;
	private int mUserId = 0;
	private String[] mFarmList;
	private DefaultItemizedOverlay mLocationFarm;
	private Feature mFirstFeature;
	private boolean mLoad = false;
	private LocalHelper mLocal;
	private Address mAddress;
	private boolean mIsMap = false;
	private Button mBtnZoomOut;
	private Button mBtnZoomIn;

	public boolean checkNet() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
			return false;
		}
		return true;
	}

	public void clearIco() {
		helpBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_help_up));
		mBtnFarmList.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_list_up));
		mBtnLocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_location_up));
		mBtnZoomOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_zoomout_up));
		mBtnZoomIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_zoomin_up));
		if (mLocationFarm != null) {
			mLocationFarm.clear();
		}
		if (mapView != null)
			mapView.invalidate();
	}

	public void initLocation(Feature feature) {
		if (feature != null) {
			String title = feature.fieldValues[0];
			double lng = Double.valueOf(feature.fieldValues[2]);
			double lat = Double.valueOf(feature.fieldValues[3]);
			Drawable drawableBlue = getResources().getDrawable(R.drawable.local_up);
			mLocationFarm = new DefaultItemizedOverlay(drawableBlue);
			OverlayItem overlayFarm = new OverlayItem(new Point2D(lng, lat), title, title);
			mLocationFarm.addItem(overlayFarm);
			mapView.getOverlays().add(mLocationFarm);
			mapView.getController().setCenter(new Point2D(lng, lat));
			mapView.invalidate();
		}
	}

	public void initLocation(String title, double lng, double lat) {
		Drawable drawableBlue = getResources().getDrawable(R.drawable.local_up);
		mLocationFarm = new DefaultItemizedOverlay(drawableBlue);
		OverlayItem overlayFarm = new OverlayItem(new Point2D(lng, lat), title, title);
		mLocationFarm.addItem(overlayFarm);
		mapView.getOverlays().add(mLocationFarm);
		mapView.getController().setCenter(new Point2D(lng, lat));
		mapView.getController().setZoom(Constant.LOCATION_ZOOM_VIP);
		mapView.invalidate();
	}

	protected void setRgNavigator(int id) {
		mNavigator = id;
		this.mRgNavigator.check(id);
	}

	@Override
	public void onClick(View v) {
		if (checkNet()) {
			clearIco();
			switch (v.getId()) {
			case R.id.iv_sattelite:
				changeMap(Constant.SATTELITE);
				break;
			case R.id.iv_terrain:
				changeMap(Constant.MAP2D);
				break;
			case R.id.ll_select: // 选择
			case R.id.iv_select:
			case R.id.rad_select:
				refresh();
				clearIco();
				editStatus = -1;
				if (allFeatures != null)
					initAllFeatures(0);
				selectFeature = null;
				selectGeometry();
				setRgNavigator(R.id.rad_select);
				break;
			case R.id.ll_add: // 添加
			case R.id.iv_add:
			case R.id.rad_add:
				if (actionList != null && actionList.size() > 0) {
					clearEditAction();
				}
				if (allFeatures != null)
					initAllFeatures(0);
				selectFeature = null;
				editStatus = 0;
				setRgNavigator(R.id.rad_add);
				addGeometry();
				break;
			case R.id.ll_delete: // 删除
			case R.id.iv_delete:
			case R.id.rad_delete:
				if (selectFeature != null) {
					setRgNavigator(R.id.rad_delete);
					comfirmDialog();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.please_select_farm));
				}
				break;
			case R.id.ll_edit_border: // 1 编辑边框
			case R.id.iv_edit_border:
			case R.id.rad_edit_border:
				if (selectFeature != null) {
					setRgNavigator(R.id.rad_edit_border);
					if (editStatus!=1) {
						editStatus = 1;
						editGeometry();
					}
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.please_select_farm));
				}
				break;
			case R.id.ll_edit: // 编辑属性
			case R.id.iv_edit:
			case R.id.rad_edit:
				if (selectFeature != null) {
					setRgNavigator(R.id.rad_edit);
					editStatus = 2;
					editField();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.please_select_farm));
				}
				break;
			case R.id.button_farm_location: // 4 自动定位
				selectFeature = null;
				if (actionList != null && actionList.size() > 0) {
					clearEditAction();
				}
				if (allFeatures != null)
					initAllFeatures(0);
				mBtnLocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_location_down));
				if (mAddress == null) {
					mAddress = mLocal.getLocation();
				}
				initLocation("自动定位", mAddress.lng, mAddress.lat);
				break;
			case R.id.ll_save: // 5 保存
			case R.id.iv_save:
			case R.id.rad_save:
				if (editStatus != 0 && editStatus != 1) {
					CommonUtil.showToask(this, getResources().getString(R.string.please_add_select_farm));
				} else {
					setRgNavigator(R.id.rad_save);
					commit();
				}
				break;
			case R.id.ll_cancel:
			case R.id.iv_cancel:
			case R.id.rad_cancel:
				setRgNavigator(R.id.rad_cancel);
				reset();
				setRgNavigator(-1);
				break;
			case R.id.button_farm_list:
				if (mFarmList != null && mFarmList.length > 0) {
					editStatus = -1;
					mBtnFarmList.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_list_down));
					new AlertDialog.Builder(this).setItems(mFarmList, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (allFeatures != null) {
								Feature feature = allFeatures.get(which);
								initLocation(feature);
							}
						}

					}).show();
				} else {
					CommonUtil.showToask(this, getResources().getString(R.string.farm_no));
				}
				break;
			case R.id.button_zoomout:
				mBtnZoomOut.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_zoomout_down));
				mapView.zoomIn();
				System.out.println(mapView.getZoomLevel());
				break;
			case R.id.button_zoomin:
				mBtnZoomIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_zoomin_down));
				mapView.zoomOut();
				System.out.println(mapView.getZoomLevel());
				break;
			case R.id.button_change_map:
				if (!mIsMap) {
					mBtnChangeMap.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_change_down));
					mLlSelectMap.setVisibility(View.VISIBLE);
					mIsMap = true;
				} else {
					mBtnChangeMap.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_change_up));
					mLlSelectMap.setVisibility(View.GONE);
					mIsMap = false;
				}
				break;
			}
		}
		super.onClick(v);
	}

	private void reset() {
		CommonUtil.showToask(this, getResources().getString(R.string.reset_success));
		geoPoints.clear();
		refresh();
		clearIco();
	}

	private void initMapData() {
		mLocal.initLocation();
		User user = UserDao.getInstance().get();
		if (user != null) {
			mUserId = user.id;
			new LoadFarm().execute(mUserId);
		}
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.ADD_FARM_REQUEST) {
			if (resultCode == Constant.ADD_FARM_RESULT) {
				String res = data.getStringExtra("farmid");
				if (!"-1".equals(res)) {
					try {
						initMapData();
					} catch (Exception e) {

					}
				} else {
					if (allFeatures != null) {
						int index = -1;
						for (int i = 0; i < allFeatures.size(); i++) {
							Feature feature = allFeatures.get(i);
							if (feature.getID() == 10000) {
								index = i;
								break;
							}
						}
						allFeatures.remove(index);
					}
					refresh();
					clearIco();
				}
			}
		}
	}

	protected void comfirmDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确认删除此农田吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				deleteGeometry();
				selectFeature = null;
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void changeMap(int type) {
		Constant.CURRENT_CENTER = mapView.getCenter();
		int zoom = 0;
		if (type == Constant.SATTELITE) {
			if (mapView.getLayers().length > 1) {
				mapView.removeAllLayers();
			}
			zoom = CommonUtil.getZoom(Constant.mSattelites);
			mapView.addLayer(satteliteView, 0);
			mapView.addLayer(labelView, 1);
			Constant.MAP_TYPE = Constant.SATTELITE;
		} else if (type == Constant.TERRAIN) {
			if (mapView.getLayers().length > 0)
				mapView.removeLayer(0);
			zoom = CommonUtil.getZoom(Constant.mTerrains);
			mapView.addLayer(terrainLayerView, 0);
			mapView.addLayer(labelView, 1);
			Constant.MAP_TYPE = Constant.TERRAIN;
		}else if(type == Constant.MAP2D){
			if (mapView.getLayers().length > 0)
				mapView.removeLayer(0);
			zoom = CommonUtil.getZoom(Constant.map2d);
			mapView.addLayer(map2dView, 0);
			mapView.addLayer(labelView, 1);
			Constant.MAP_TYPE = Constant.MAP2D;
		}
		
		if (Constant.CURRENT_CENTER != null) {
			mapView.getController().setCenter(Constant.CURRENT_CENTER);
			mapView.getController().setZoom(zoom);
			mapView.invalidate();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.farm_manager));
		setIsNavigator(false);
		setIsBack(true);

		mContent = inflater(R.layout.farm_manager);
		mLocal = LocalHelper.getInstance(this);
		mLlSelect = (LinearLayout) mContent.findViewById(R.id.ll_select);
		mLlSelect.setOnClickListener(this);
		mLlAdd = (LinearLayout) mContent.findViewById(R.id.ll_add);
		mLlAdd.setOnClickListener(this);
		mLlEdit = (LinearLayout) mContent.findViewById(R.id.ll_edit);
		mLlEdit.setOnClickListener(this);
		mLlDelete = (LinearLayout) mContent.findViewById(R.id.ll_delete);
		mLlDelete.setOnClickListener(this);
		mLlSelectMap = (LinearLayout) mContent.findViewById(R.id.ll_select_map);

		mRadSelect = (RadioButton) mContent.findViewById(R.id.rad_select);
		mRadSelect.setOnClickListener(this);
		mRadAdd = (RadioButton) mContent.findViewById(R.id.rad_add);
		mRadAdd.setOnClickListener(this);
		mRadEdit = (RadioButton) mContent.findViewById(R.id.rad_edit);
		mRadEdit.setOnClickListener(this);
		mRadDelete = (RadioButton) mContent.findViewById(R.id.rad_delete);
		mRadDelete.setOnClickListener(this);
		mRadSave = (RadioButton) mContent.findViewById(R.id.rad_save);
		mRadSave.setOnClickListener(this);
		mRadCancel = (RadioButton) mContent.findViewById(R.id.rad_cancel);
		mRadCancel.setOnClickListener(this);
		mRadEditBorder = (RadioButton) mContent.findViewById(R.id.rad_edit_border);
		mRadEditBorder.setOnClickListener(this);

		mIvSelect = (ImageView) mContent.findViewById(R.id.iv_select);
		mIvSelect.setOnClickListener(this);
		mIvAdd = (ImageView) mContent.findViewById(R.id.iv_add);
		mIvAdd.setOnClickListener(this);
		mIvEdit = (ImageView) mContent.findViewById(R.id.iv_edit);
		mIvEdit.setOnClickListener(this);
		mIvDelete = (ImageView) mContent.findViewById(R.id.iv_delete);
		mIvDelete.setOnClickListener(this);
		mIvSave = (ImageView) mContent.findViewById(R.id.iv_save);
		mIvSave.setOnClickListener(this);
		mIvCancel = (ImageView) mContent.findViewById(R.id.iv_cancel);
		mIvCancel.setOnClickListener(this);
		mIvEditBorder = (ImageView) mContent.findViewById(R.id.iv_edit_border);
		mIvEditBorder.setOnClickListener(this);

		mIvSattelite = (ImageView) mContent.findViewById(R.id.iv_sattelite);
		mIvSattelite.setOnClickListener(this);
		mIvTerrain = (ImageView) mContent.findViewById(R.id.iv_terrain);
		mIvTerrain.setOnClickListener(this);
		mBtnLocation = (Button) mContent.findViewById(R.id.button_farm_location);
		mBtnLocation.setOnClickListener(this);
		mBtnChangeMap = (Button) mContent.findViewById(R.id.button_change_map);
		mBtnChangeMap.setOnClickListener(this);
		mBtnFarmList = (Button) mContent.findViewById(R.id.button_farm_list);
		mBtnFarmList.setOnClickListener(this);
		mBtnZoomOut = (Button) mContent.findViewById(R.id.button_zoomout);
		mBtnZoomOut.setOnClickListener(this);
		mBtnZoomIn = (Button) mContent.findViewById(R.id.button_zoomin);
		mBtnZoomIn.setOnClickListener(this);
		mLlCancel = (LinearLayout) mContent.findViewById(R.id.ll_cancel);
		mLlCancel.setOnClickListener(this);
		mLlEditBorder = (LinearLayout) mContent.findViewById(R.id.ll_edit_border);
		mLlEditBorder.setOnClickListener(this);
		mLlSave = (LinearLayout) mContent.findViewById(R.id.ll_save);
		mLlSave.setOnClickListener(this);
		mRgNavigator = (RadioGroup) mContent.findViewById(R.id.rg_navigator);

		Constant.MAP_TYPE = -1;
		mPdDialog = CustomProgressDialog.createDialog(this);
		mPdDialog.setMessage(getResources().getString(R.string.loading_data));
		
		// mapview  添加layerview图层，overlay覆盖物
		mapView = (MapView) this.findViewById(R.id.mapview);
		satteliteView = new PisaLayerView(this);
		satteliteView.init(HttpHelper.getMapUrl(), "Map", "pisa_Sattelite", Constant.mSattelites, "jpg");
		satteliteView.clearCache(true);
		map2dView = new PisaLayerView(this);
		map2dView.init(HttpHelper.getMapUrl(), "Map", "pisa_Map2d", Constant.map2d, "png");
		map2dView.clearCache(false);
		
		/*terrainLayerView = new PisaLayerView(this);
		terrainLayerView.init(HttpHelper.getMapUrl(), "Terrain", "pisa_Terrain", Constant.mTerrains, "jpg");
		terrainLayerView.clearCache(false);*/
		labelView = new PisaLayerView(this);
		labelView.init(HttpHelper.getMapUrl(), "Labels", "pisa_Label", Constant.mLabels, "png");
		labelView.clearCache(false);
		mFarmCache = new FarmCache();

		changeMap(Constant.MAP2D);
		mapView.getController().setZoom(Constant.DEFAULT_ZOOM);
		mapView.getController().setCenter(new Point2D(105.8405387503, 29.485119946399999));
		mapView.setClickable(true);
		helpBtn = (Button) findViewById(R.id.button_help);
		helpBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				helpBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.farm_manager_help_down));
				showDialog(README_DIALOG);
			}
		});

		dao = new FarmDao();
		service = new PreferencesService(this);
		Map<String, Boolean> params = service.getReadmeEnable("editFeature");
		boolean isReadmeEnable = params.get("readme");
		if (isReadmeEnable) {
			showDialog(README_DIALOG);
		}
		init();
	}

	private class LoadFarm extends AsyncTask<Integer, Integer, List<Feature>> {

		@Override
		protected List<Feature> doInBackground(Integer... params) {
			if (CommonUtil.checkNetState(FarmManagerActivity.this)) {
				return dao.getFarmFeaturesByUserId(params[0]);
			}else {
				return mFarmCache.getFarmList(mUserId);
			}
			
		}

		@Override
		protected void onPostExecute(List<Feature> result) {
			if (result != null) {
				if (result != null && result.size() > 0) {
					allFeatures = result;
					initAllFeatures(0);
					if (!mLoad) {
						mFirstFeature = allFeatures.get(0);
						initLocation(mFirstFeature);
						mLoad = true;
					}
				} else {
					mFirstFeature = null;
				}
			}
			if (mPdDialog.isShowing())
				mPdDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mPdDialog.show();
		}

	}

	private void initAllFeatures(int id) {
		clearPolygonOverlays();
		
		if (id != 0) {
			int index = -1;
			if (allFeatures != null) {
				for (int i = 0; i < allFeatures.size(); i++) {
					Feature f = allFeatures.get(i);
					if (f.getID() == id) {
						index = i;
						break;
					}
				}
				if (index != -1){
					
					List<Feature> tmpFeatures = new ArrayList<Feature>();
					//allFeatures.remove(index); //移除添加的农田 但是没有上传成功的
					for(int i=0;i<allFeatures.size();i++){
						if(i == index) continue;
						tmpFeatures.add(allFeatures.get(i));
					}
					
					allFeatures = tmpFeatures;
				}
					
			}
		}
		if (allFeatures != null && allFeatures.size() > 0) {
			showPolygonOverlay(allFeatures);
			int size = allFeatures.size();
			mFarmList = new String[size];
			for (int i = 0; i < size; i++) {
				Feature feature = allFeatures.get(i);
				mFarmList[i] = feature.fieldValues[0];
			}
		}
	}

	// 增加农田
	public void addGeometry() {
		editStatus = 0;
		polygonOverlay.setData(new ArrayList<Point2D>());
		if (geoPoints != null)
			geoPoints.clear();
		mapView.setUseScrollEvent(false);

		if (!mapView.getOverlays().contains(touchOverlay)) {
			mapView.getOverlays().add(touchOverlay);
		}
		if (mapView.getOverlays().contains(selectedtouchOverlay)) {
			mapView.getOverlays().remove(selectedtouchOverlay);
		}
		// mapView.invalidate();
	}

	// 结束编辑农田事件，向服务端提交请求
	public void commit() {
		// 提交增加农田事件请求
		if (editStatus == 0) { // 添加或编辑属性
			mapView.setUseScrollEvent(true);
			if (geoPoints.size() > 2) {
				com.supermap.services.components.commontypes.Point2D[] pts = new com.supermap.services.components.commontypes.Point2D[geoPoints
						.size()];
				for (int j = 0; j < geoPoints.size(); j++) {
					pts[j] = new com.supermap.services.components.commontypes.Point2D(geoPoints.get(j).x, geoPoints.get(j).y);
				}
				Geometry geometry = new Geometry();
				geometry.points = pts;
				geometry.parts = new int[] { pts.length };
				geometry.type = GeometryType.REGION;
				Feature feature = new Feature();
				feature.setID(10000);
				feature.geometry = geometry;
				feature.geometry.id = 10000;
				feature.fieldValues = new String[4];
				feature.fieldValues[0] = "";
				allFeatures.add(feature);

				Intent intent = new Intent(this, FarmDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("feature", feature);
				bundle.putInt("op", 0);
				intent.putExtras(bundle);
				startActivityForResult(intent, Constant.ADD_FARM_REQUEST);
				editStatus = -1;
			} else {
				CommonUtil.showToask(this, "添加的农田至少包含三个点!");
			}
		} else if (editStatus == 1) { // 编辑边框
			if (featureMap != null && featureMap.size() > 0) {
				Farm farm = new Farm();
				farm.coordinates = "";
				farm.id = selectFeature.getID();
				List<Point2D> pts = featureMap.get(String.valueOf(farm.id));
				com.supermap.services.components.commontypes.Point2D[] all = new com.supermap.services.components.commontypes.Point2D[pts
						.size()];
				for (int i = 0; i < pts.size(); i++) {
					Point2D p = pts.get(i);
					com.supermap.services.components.commontypes.Point2D pd = new com.supermap.services.components.commontypes.Point2D();
					pd.x = p.x;
					pd.y = p.y;
					all[i] = pd;
				}
				List<Point2D> data = new ArrayList<Point2D>();
				StringBuffer sb = new StringBuffer();
				for (com.supermap.services.components.commontypes.Point2D p : all) {
					Point2D p2 = new Point2D();
					p2.x = p.x;
					p2.y = p.y;
					data.add(p2);
					sb.append(p.x + "," + p.y + " ");
				}
				sb.append(data.get(0).x + "," + data.get(0).y + " ");
				data.add(data.get(0));
				farm.area = CommonUtil.getMetreSquare(Constant.selectedScale, DataUtil.getArea(data)) + "";
				String coordinates = sb.toString().trim();
				farm.coordinates = coordinates.substring(0, coordinates.length() - 1);
				com.supermap.services.components.commontypes.Point2D point = CommonUtil.getCenterPoint(all);
				farm.latitude = point.y + "";
				farm.longitude = point.x + "";
				if (dao.updateFarmlandCoordinate(farm)) {
					CommonUtil.showToask(this, "更新农田成功!");
					selectFeature.geometry.points = all;
					refresh();
				} else {
					mapView.invalidate();
					CommonUtil.showToask(this, "更新农田失败!");
				}
			} else {
				CommonUtil.showToask(this, "请先选择农田!");
			}
			editStatus = -1;
		}
	}

	private void refresh() {
		clearCache();
		clearPolygonOverlays();
		polygonOverlay.setData(new ArrayList<Point2D>());
		clearEditAction();
		if (allFeatures != null)
			initAllFeatures(0);
		init();
	}

	private void init() {
		polygonOverlay = new PolygonOverlay(getPolygonPaint());
		mapView.getOverlays().add(polygonOverlay);
		touchOverlay = new AddTouchOverlay();
		selectedtouchOverlay = new SelectedTouchOverlay();
		mapView.invalidate();
	}

	private void clearCache() {
		if (satteliteView != null)
			satteliteView.clearCache(false);
		if (map2dView != null)
			map2dView.clearCache(false);
		if (terrainLayerView != null)
			terrainLayerView.clearCache(false);
		if (labelView != null)
			labelView.clearCache(false);
		mapView.invalidate();
	}

	/**
	 * 结束编辑状态，并清空编辑控件
	 */
	private void clearEditAction() {
		for (int i = 0; i < actionList.size(); i++) {
			EditFeatureAction editFeatureAction = actionList.get(i);
			editFeatureAction.stopEditFeature();
		}
		featureMap.clear();
		actionList.clear();
		mapView.invalidate();
	}

	/**
	 * 结束编辑状态，并清空处理触碰事件Overlay
	 */
	public void clearTouchOverlay() {
		mapView.setUseScrollEvent(true);
		mapView.getOverlays().remove(selectedtouchOverlay);
		mapView.getOverlays().remove(touchOverlay);
	}

	// 选择农田
	public void selectGeometry() {
		if (!mapView.getOverlays().contains(selectedtouchOverlay)) {
			mapView.getOverlays().add(selectedtouchOverlay);
		}
		if (mapView.getOverlays().contains(touchOverlay)) {
			mapView.getOverlays().remove(touchOverlay);
		}
		clearEditAction();
		editStatus = -1;
		mapView.setUseScrollEvent(true);
	}

	private void clearPolygonOverlays() {
		if (polygonOverlays != null) {
			mapView.getOverlays().removeAll(polygonOverlays);
			polygonOverlays.clear();
			mapView.invalidate();
		}
	}

	// 编辑农田
	public void editGeometry() {
		if (selectFeature != null) {
			Feature feature = selectFeature;
			if (feature != null && feature.geometry != null && feature.geometry.points != null) {
				actionList.clear();
				List<Point2D> points = DataUtil.getPiontsFromGeometry(feature.geometry);
				featureMap.put(String.valueOf(feature.getID()), points);
				EditFeatureAction editFeatureAction = new EditFeatureAction(mapView);
				editFeatureAction.doEditFeature(points);
				actionList.add(editFeatureAction);
			}
			mapView.invalidate();
		}
	}

	// 属性编辑
	public void editField() {
		if (selectFeature != null) {
			Intent intent = new Intent(this, FarmDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("feature", selectFeature);
			bundle.putInt("op", 1);
			intent.putExtras(bundle);
			startActivity(intent);
			editStatus = -1;
		}
	}

	// 删除农田
	public void deleteGeometry() {
		if (selectFeature != null) {
			int id = selectFeature.getID();
			CommonUtil.showToask(FarmManagerActivity.this, String.valueOf(id));
			new DeleteGeoMetry(id).execute();
			editStatus = -1;
		}
	}

	class DeleteGeoMetry extends AsyncTask<String,String,Boolean>{

		private Integer farmId;
		public DeleteGeoMetry(Integer farmId){
			this.farmId = farmId;
		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			return dao.deleteFarmland(farmId);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (result) {
				CommonUtil.showToask(FarmManagerActivity.this, "删除农田成功");
				initAllFeatures(farmId);
				if (mapView.getOverlays().contains(touchOverlay)) {
					mapView.getOverlays().remove(touchOverlay);
				}
				refresh();
				clearIco();
			} else {
				CommonUtil.showToask(FarmManagerActivity.this, "删除农田失败");
			}

		}
		
		
		
	}
	
	
	// 初始化农田
	public void showPolygonOverlay(List<Feature> result) {
		if (result == null || result == null || result.size() == 0) {
			return;
		}
		List<List<Point2D>> pointsLists = new ArrayList<List<Point2D>>();
		for (int i = 0; i < result.size(); i++) {
			Feature feature = result.get(i);
			if (feature != null) {
				Geometry geometry = feature.geometry;
				List<Point2D> geoPoints = DataUtil.getPiontsFromGeometry(geometry);
				pointsLists.add(geoPoints);
			}
		}
		if (pointsLists.size() > 0) {
			if (polygonOverlays == null) {
				polygonOverlays = new ArrayList<PolygonOverlay>();
			}
			for (int m = 0; m < pointsLists.size(); m++) {
				List<Point2D> geoPointList = pointsLists.get(m);
				PolygonOverlay polygonOverlay = new PolygonOverlay(getPolygonPaint());
				polygonOverlay.setData(geoPointList);
				mapView.getOverlays().add(polygonOverlay);
				polygonOverlays.add(polygonOverlay);
			}
			this.mapView.getOverlays().remove(selectedtouchOverlay);
			this.mapView.invalidate();
		} else {
			CommonUtil.showToask(this, "选择农田失败!");
		}
	}

	// 将选中的农田高亮显示
	public void showSelectPolygonOverlay(Point2D touchPoint) {
		Feature search = null;
		Geometry geometry = null;
		for (Feature feature : allFeatures) {
			geometry = feature.geometry;
			if (DataUtil.ptInPolygon(touchPoint, DataUtil.getPiontsFromGeometry(geometry))) {
				search = feature;
				break;
			}
		}
		if (search != null) {
			selectFeature = search;
			List<Point2D> geoPoints = DataUtil.getPiontsFromGeometry(geometry);
			if (geoPoints.size() > 0) {
				if (polygonOverlays == null) {
					polygonOverlays = new ArrayList<PolygonOverlay>();
				}
				PolygonOverlay polygonOverlay = new PolygonOverlay(getSelectPolygonPaint());
				selectedolygonOverlay = polygonOverlay;
				polygonOverlay.setData(geoPoints);
				mapView.getOverlays().add(polygonOverlay);
				polygonOverlays.add(polygonOverlay);
				this.mapView.invalidate();
			} else {
				CommonUtil.showToask(this, "选择农田失败!");
			}
		}
	}
	
	@Deprecated
	// 将选中的农田高亮显示
	public void showPolygonOverlay(GetFeaturesResult result) {
		if (result == null || result.features == null || result.features.length == 0) {
			CommonUtil.showToask(this, "选择农田失败!");
			return;
		}
		List<List<Point2D>> pointsLists = new ArrayList<List<Point2D>>();
		for (int i = 0; i < result.features.length; i++) {
			Feature feature = result.features[i];
			Geometry geometry = feature.geometry;
			List<Point2D> geoPoints = DataUtil.getPiontsFromGeometry(geometry);
			if (geometry.parts.length > 1) {
				int num = 0;
				for (int j = 0; j < geometry.parts.length; j++) {
					int count = geometry.parts[j];
					List<Point2D> partList = geoPoints.subList(num, num + count);
					pointsLists.add(partList);
					num = num + count;
				}
			} else {
				pointsLists.add(geoPoints);
			}
		}
		// 把所有查询的几何对象都高亮显示
		if (pointsLists.size() > 0) {
			if (polygonOverlays == null) {
				polygonOverlays = new ArrayList<PolygonOverlay>();
			}
			for (int m = 0; m < pointsLists.size(); m++) {
				List<Point2D> geoPointList = pointsLists.get(m);
				PolygonOverlay polygonOverlay = new PolygonOverlay(getPolygonPaint());
				polygonOverlay.setData(geoPointList);
				mapView.getOverlays().add(polygonOverlay);
				polygonOverlays.add(polygonOverlay);
			}
			this.mapView.getOverlays().remove(selectedtouchOverlay);
			this.mapView.invalidate();
		} else {
			CommonUtil.showToask(this, "选择农田失败!");
		}
	}

	private Paint getPolygonPaint() {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
		paint.setAlpha(50);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		return paint;
	}

	private Paint getSelectPolygonPaint() {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);
		paint.setAlpha(50);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		return paint;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			clearTouchOverlay();
			showDialog(EDITFEATURE_DIALOG);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 给Overlay设置点集合，开始绘制对象，并刷新地图
	 */
	private void setOverlayData(Overlay overlay, List<Point2D> gps) {
		if (overlay == null) {
			return;
		}
		List<Point2D> geoPointList = new ArrayList<Point2D>();
		if (gps != null && gps.size() > 0) {
			copyList(gps, geoPointList);
		} else if (geoPoints.size() > 0) {
			copyList(geoPoints, geoPointList);
		}
		if (geoPointList.size() > 0) {
			if (overlay instanceof LineOverlay) {
				((LineOverlay) overlay).setData(geoPointList);
			} else if (overlay instanceof PolygonOverlay) {
				((PolygonOverlay) overlay).setData(geoPointList);
				polygonOverlays.add((PolygonOverlay) overlay);
			}
			mapView.invalidate();
		}
	}

	private void copyList(List<Point2D> sourcegps, List<Point2D> targetgps) {
		for (int i = 0; i < sourcegps.size(); i++) {
			targetgps.add(new Point2D(sourcegps.get(i)));
		}
	}

	private void setOverlayData(Overlay overlay) {
		setOverlayData(overlay, null);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case README_DIALOG:
			Dialog dialog = new ReadmeDialog(this, R.style.readmeDialogTheme, "editFeature");
			return dialog;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case README_DIALOG:
			ReadmeDialog readmeDialog = (ReadmeDialog) dialog;
			readmeDialog.setReadmeText(getResources().getString(R.string.editfeaturedemo_readme));
			break;
		default:
			break;
		}	
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mFarmCache.saveFarms(allFeatures, mUserId);
				
			}
		}).start();
		if(satteliteView != null)
			satteliteView.destroy();
		if(map2dView != null)
			map2dView.destroy();
		if(terrainLayerView != null)
			terrainLayerView.destroy();
		if(labelView != null)
			labelView.destroy();
		
		mapView.stopClearCacheTimer();
		geoPoints.clear();
		clearEditAction();
		exit();
		super.onDestroy();
	}
	
	private void exit(){
		if(satteliteView != null)
			satteliteView.exit();
		if(map2dView != null)
			map2dView.exit();
		if(terrainLayerView != null)
			terrainLayerView.exit();
		if(labelView != null)
			labelView.exit();
		try {
			checkLocalCache();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void checkLocalCache() throws Exception{
		FileHelper helper = new FileHelper();
		File cacheFile = new File(FileManager.getSaveFilePath());
		Long  cacheSize = helper.getFileSize(cacheFile);
		System.out.println("file cache size is "+helper.FormetFileSize(cacheSize));
		File sattelitePaht = new File(FileManager.getSaveMapCacheDir()+"Sattelite");
		if (cacheSize>FileHelper.MAX_FILE_CACHE) {//M
			if (sattelitePaht.isDirectory()) {
				 File flist[] = sattelitePaht.listFiles();
				 for (int i = 0; i < flist.length; i++) {
					if (Integer.valueOf(flist[i].toString())<80000) {
						FileHelper.deleteDirectory(flist[i].toString());
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		initMapData();//初始化地图数据，和农田数据
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		geoPoints.clear();
		clearEditAction();
		super.onBackPressed();
	}

	/**
	 * 选择农田触屏Overlay
	 */
	class SelectedTouchOverlay extends Overlay {
		@Override
		public boolean onTouchEvent(MotionEvent event, final MapView mapView) {
			if (editStatus < 0 && event.getAction() == MotionEvent.ACTION_UP) {
				if (selectedolygonOverlay != null)
					selectedolygonOverlay.setData(new ArrayList<Point2D>());
				touchX = Math.round(event.getX());
				touchY = Math.round(event.getY());
				Point2D touchPoint = mapView.getProjection().fromPixels(touchX, touchY);
				showSelectPolygonOverlay(touchPoint);
			}
			return false;
		}
	}

	/**
	 * 增加农田触屏Overlay
	 */
	class AddTouchOverlay extends Overlay {
		@Override
		public boolean onTouchEvent(MotionEvent event, final MapView mapView) {
			if (editStatus == 0) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					touchX = Math.round(event.getX());
					touchY = Math.round(event.getY());
					Point2D touchGeoPoint = mapView.getProjection().fromPixels(touchX, touchY);
					if (!geoPoints.contains(touchGeoPoint)) {
						geoPoints.add(touchGeoPoint);
					}
					setOverlayData(polygonOverlay);
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					List<Point2D> geoPointList = new ArrayList<Point2D>();
					if (geoPoints.size() > 0) {
						copyList(geoPoints, geoPointList);
					}
					int x = Math.round(event.getX());
					int y = Math.round(event.getY());
					Point2D touchGeoPoint = mapView.getProjection().fromPixels(x, y);
					updatePoint(geoPointList, touchGeoPoint);
					setOverlayData(polygonOverlay, geoPointList);
				}
				return true;
			}
			return false;
		}

		private void updatePoint(List<Point2D> geoPointList, Point2D touchGeoPoint) {
			if (geoPointList.size() == geoPoints.size()) {
				geoPointList.add(touchGeoPoint);
			} else if (geoPointList.size() > geoPoints.size()) {
				geoPointList.remove(geoPointList.size() - 1);
				geoPointList.add(touchGeoPoint);
			}
		}
	}

}
