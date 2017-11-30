package com.supermap.pisaclient.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.FileHelper;
import com.supermap.pisaclient.common.FileManager;
import com.supermap.services.components.commontypes.Feature;
import com.supermap.services.components.commontypes.Geometry;
import com.supermap.services.components.commontypes.Point2D;

public class FarmCache {
	
	public boolean saveFarms(JSONObject farms, int userID){
		return true;
	}
	
	public boolean saveFarms(List<Feature> frams,int userID){
		JSONArray farmlist = new JSONArray();
		Feature feature = null;
		JSONObject object = null;
		for (int i = 0; i < frams.size(); i++) {
			feature = frams.get(i);
			object = new JSONObject();
			try {
				object.put("id", feature.geometry.id);
				object.put(feature.fieldNames[0], feature.fieldValues[0]);
				object.put(feature.fieldNames[1], feature.fieldValues[1]);
				object.put(feature.fieldNames[2], feature.fieldValues[2]);
				object.put(feature.fieldNames[3], feature.fieldValues[3]);
				String coordinates ="";
				for (int j = 0; j < feature.geometry.points.length; j++) {
					coordinates+=feature.geometry.points[j].x+","+feature.geometry.points[j].y+" ";
				}
				object.put("coordinates", coordinates);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			farmlist.put(object);
		}
		String file = FileManager.getSaveFarmsCacheDir() + userID + ".farm" ;
		FileHelper.writeFile(file, farmlist.toString());
		System.out.println("农田数据缓存成功");
		return true;
	}
	
	
	public JSONObject getFarms(int userID){
		JSONObject farms = new JSONObject();
		
		return farms;
	}
	
	public List<Feature> getFarmList(int userID){
		List<Feature> farms = new ArrayList<Feature>();
		String farmpath = FileManager.getSaveFarmsCacheDir() + userID + ".farm" ;
		String strFarms = null;
		try {
			strFarms = FileHelper.readTextFile(farmpath);
			JSONArray array = new JSONArray(strFarms);
			String coordinates = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Feature f = new Feature();
				f.geometry = new Geometry();
				f.fieldNames = new String[] { "descript", "height", "longitude", "latitude" };
				f.fieldValues = new String[4];
				if (obj.opt("id") != null) {
					f.setID(obj.getInt("id"));
					f.geometry.id = obj.getInt("id");
				}
				if (obj.opt("descript") != null)
					f.fieldValues[0] = obj.getString("descript");
				if (obj.opt("coordinates") != null) {
					coordinates = obj.getString("coordinates");
					String[] points = coordinates.split(" ");
					Point2D[] p2d = new Point2D[points.length];
					for (int k = 0; k < points.length; k++) {
						String p = points[k];
						String[] point = p.split(",");
						Point2D pd = new Point2D();
						pd.x = Double.parseDouble(point[0]);
						pd.y = Double.parseDouble(point[1]);
						p2d[k] = pd;
					}
					f.geometry.points = p2d;
				}
				if (obj.opt("height") != null)
					f.fieldValues[1] = obj.getString("height");
				if (obj.opt("longitude") != null)
					f.fieldValues[2] = CommonUtil.getCutString(obj.getString("longitude"), 6);
				if (obj.opt("latitude") != null)
					f.fieldValues[3] = CommonUtil.getCutString(obj.getString("latitude"), 6);
				farms.add(f);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return farms;
	}

}
