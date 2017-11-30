package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.entity.Legend;
import com.supermap.pisaclient.http.JsonHelper;
import com.supermap.pisaclient.http.JsonHelper.RenderModle;

public class RasterProduct {
	private RasterProduct oThis = this;
	private String ptype;
	private String pname;
	private String title;
	private String area;
	private String unit;
	private List<Legend> legends;
	private List<OverlayOptions> overlayOptionsList;
	private boolean isDataLoaded = false;
	private boolean isDataAnalyzed = false;
	private Context context;
	private OnRasterProductLoadListener listener;

	public RasterProduct(Context context, String ptype, String pname,
			String area, OnRasterProductLoadListener listener) {
		this.ptype = ptype;
		this.pname = pname;
		this.area = area;
		this.context = context;
		this.legends = new ArrayList<Legend>();
		this.overlayOptionsList = new ArrayList<OverlayOptions>();
		new LoadTask(ptype, pname + "_" + area).execute();// 这里吧文件和地区组合
		this.listener = listener;
	}

	/******************************
	 * 产品加载任务
	 * 
	 * @author heyao
	 * 
	 ******************************/
	private class LoadTask extends
			AsyncTask<Integer, Integer, List<RenderModle>> {
		private String ptype;
		private String pname;
		private LoadTask that = this;

		public LoadTask(String ptype, String pname) {
			this.ptype = ptype;
			this.pname = pname;
		}

		@Override
		protected List<RenderModle> doInBackground(Integer... params) {
			JsonHelper help = new JsonHelper();
			return help
					.getRenderataFromWebJson("color", this.ptype, this.pname);
		}

		@Override
		protected void onPostExecute(List<RenderModle> result) {
			oThis.isDataLoaded = true;
			Toast.makeText(oThis.context, "色斑图数据加载完毕", Toast.LENGTH_SHORT)
					.show();
			new AnalyzeTask(that.ptype, result).execute();
		}
	}

	/******************************
	 * 产品解析任务
	 * 
	 * @author heyao
	 * 
	 ******************************/
	private class AnalyzeTask extends AsyncTask<Integer, Integer, Object[]> {
		private String ptype;
		private List<RenderModle> renderModleList;
		private AnalyzeTask that = this;

		public AnalyzeTask(String ptype, List<RenderModle> renderModleList) {
			this.ptype = ptype;
			this.renderModleList = renderModleList;
		}

		@Override
		protected Object[] doInBackground(Integer... arg0) {
			Object[] objects = new Object[4];
			// TODO Auto-generated method stub
			List<Legend> lstColor = new ArrayList<Legend>();
			String strTile = "";
			String strUnit = "";
			List<OverlayOptions> lstOption = new ArrayList<OverlayOptions>();
			if (this.renderModleList.size() > 1) {
				for (RenderModle rendr : this.renderModleList) {
					String col = rendr.GetColorCap();
					String unit = rendr.getUnit();
					String name = rendr.Getkey();
					List<String> lst = rendr.GetValue();
					if (col != null && col != "") {
						Legend leg = new Legend();
						leg.color = rendr.Getkey();
						leg.caption = col;
						lstColor.add(leg);
					} else if (lst.size() > 0) {
						List<LatLng> lstData = new ArrayList<LatLng>();
						for (int i = 0; i < lst.size();) {
							try {
								double y = Double.valueOf(lst.get(i));
								i++;
								double x = Double.valueOf(lst.get(i));
								i++;
								lstData.add(new LatLng(x, y));
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
						String[] strRGB = rendr.Getkey().split(",");
						if (strRGB.length > 2) {
							int ranColor = Color.rgb(
									Integer.valueOf(strRGB[0]),
									Integer.valueOf(strRGB[1]),
									Integer.valueOf(strRGB[2]));
							Bundle bundle = new Bundle();
							bundle.putSerializable("info", "色斑图");
							OverlayOptions ooPolygon = new PolygonOptions()
									.points(lstData)
									.stroke(new Stroke(5, ranColor))
									.fillColor(ranColor).extraInfo(bundle);
							lstOption.add(ooPolygon);
						}
					} else if(rendr.Getkey() != "") {
						strTile = rendr.Getkey();
					}else if(rendr.getUnit() != ""){
						strUnit = rendr.getUnit();
					}
				}
				// lstAllOverLay=mBaiduMap.addOverlays(lstOption);//渲染色斑图
			} else {

			}
			objects[0] = strTile;
			objects[1] = lstColor;
			objects[2] = lstOption;
			objects[3] = strUnit;
			return objects;
		}

		@Override
		protected void onPostExecute(Object[] result) {

			if (result != null && result.length > 0) {
				oThis.title = (String) result[0];
				oThis.legends = (List<Legend>) result[1];
				oThis.overlayOptionsList = (List<OverlayOptions>) result[2];
				oThis.unit = (String)result[3];
			}
			oThis.isDataAnalyzed = true;
			if (oThis.listener != null)
				oThis.listener.Loaded(oThis);
		}

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Legend> getLegends() {
		return legends;
	}

	public void setLegends(List<Legend> legends) {
		this.legends = legends;
	}

	public List<OverlayOptions> getOverlayOptionsList() {
		return overlayOptionsList;
	}

	public void setOverlayOptionsList(List<OverlayOptions> overlayOptionsList) {
		this.overlayOptionsList = overlayOptionsList;
	}

	public boolean isDataLoaded() {
		return isDataLoaded;
	}

	public void setDataLoaded(boolean isDataLoaded) {
		this.isDataLoaded = isDataLoaded;
	}

	public boolean isDataAnalyzed() {
		return isDataAnalyzed;
	}

	public void setDataAnalyzed(boolean isDataAnalyzed) {
		this.isDataAnalyzed = isDataAnalyzed;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public interface OnRasterProductLoadListener {
		void Loaded(RasterProduct rp);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	

}
