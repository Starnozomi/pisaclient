package com.supermap.pisaclient.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.supermap.pisaclient.http.JsonHelper;
import com.supermap.pisaclient.http.JsonHelper.RenderModle;

public class ReadBorldpolygonData {

	public static ReadBorldpolygonData readObj=null;
	public Boolean isComplet =false;
	public List<OverlayOptions> lstOptionGon = new ArrayList<OverlayOptions>();
	public List<OverlayOptions> lstOptionLine = new ArrayList<OverlayOptions>();
	
	private void ReadBorldpolygonData()
	{
		
	}
	
	public static ReadBorldpolygonData InitReadPolgonObj()
	{
		if(readObj==null)
			readObj = new ReadBorldpolygonData();
		return readObj;
	}
	
	public interface OnOverlayOptionsReadListener{
			void ReadDoneListening(List<OverlayOptions> lstOptionGon);
	}
	OnOverlayOptionsReadListener listener = null;
	public void setListener(OnOverlayOptionsReadListener listener) {
		listener = listener;
    }
	
	
	public void ReadBordData(Context context,OnOverlayOptionsReadListener listener)
	{
		JsonHelper help = new JsonHelper();
		List<RenderModle> lstAllDataBrold = help.getRenderataFromJson(context,"dcPolygon.json","color"); 
		for (RenderModle rendr : lstAllDataBrold) {
			List<LatLng> lstData = new ArrayList<LatLng>();
			List<String> lst = rendr.GetValue();
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

		String[] strRGB =rendr.Getkey().split(",");
		if(strRGB.length>2){
		int ranColor =Color.rgb(Integer.valueOf(strRGB[0]),Integer.valueOf(strRGB[1]),Integer.valueOf(strRGB[2]));
        Bundle bundle=new Bundle();
		bundle.putSerializable("info", "色斑图");
		PolygonOptions ooPolygon =  new PolygonOptions();
		ooPolygon.points(lstData);
        ooPolygon.stroke(new Stroke(5,ranColor)).fillColor(ranColor).extraInfo(bundle);
		lstOptionGon.add(ooPolygon );
			}
		}
		isComplet =true;
		if(listener != null){
			listener.ReadDoneListening(lstOptionGon);
		}
	}
	
	public void ReadBordLineData(Context context,String borderAreaname,OnOverlayOptionsReadListener listener)
	{
		lstOptionLine = new ArrayList<OverlayOptions>();
		List<List<String>> lstAllData = JsonHelper.getBorderlineDataFormJson(context, "arealine/"+ borderAreaname +".json");
		for(List<String> lst:lstAllData)
		{
		List<LatLng> lstData = new ArrayList<LatLng>();
		for (int i = 0; i < lst.size() ;) {
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
		//dd4d41
		int intColor = Color.parseColor("#FFFF33");
		OverlayOptions ooPolyline = new PolylineOptions().width(4)
				.color(intColor).points(lstData).focus(true).zIndex(1);
		lstOptionLine.add(ooPolyline);
		}
//		isComplet =true;
		if(listener != null){
			listener.ReadDoneListening(lstOptionGon);
		}
	}
}
