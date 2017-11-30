package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.AreaPoint;
import com.supermap.pisaclient.entity.FarmInfo;
import com.supermap.pisaclient.entity.FarmPoint;
import com.supermap.pisaclient.entity.LivePoint;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProfProduct;
import com.supermap.pisaclient.entity.SituationInfo;
import com.supermap.pisaclient.entity.SituationPoint;
import com.supermap.pisaclient.entity.VipProductPoint;
import com.supermap.pisaclient.entity.VipUser;
import com.supermap.pisaclient.entity.VipUserInfo;
import com.supermap.pisaclient.entity.WorkStation;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.service.ProductLayer.PTYPE;
import com.supermap.pisaclient.service.ProductPoint;

public class WorkStationDao 
{
	
	public static List<WorkStation> getStationListByAreas(List<String> areaCodeList)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		//用于in的语句，所以需要调整结构
		String areaCodes = "";
		for(String s : areaCodeList){
			areaCodes += s+",";
		}
		if(areaCodes.indexOf(",") != -1){
			areaCodes = areaCodes.substring(0,areaCodes.length()-1);
		}
		params.put("param", "{\"Function\":\"workstation.list.area\",\"CustomParams\":{\"areacodes\":\""+ areaCodes +"\"},\"Type\":2}");
		
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			
			List<WorkStation> workStations=new ArrayList<WorkStation>();
			
			WorkStation ws=null;
			JSONObject jo=null;
			
			for(int i=0;i<array.length();i++)
			{
				jo= array.getJSONObject(i);
				ws = new WorkStation();
				ws.id = jo.getInt("id");
				ws.stationName = jo.getString("stationname");
				ws.areaCode = jo.getString("areacode");
				ws.desc = jo.getString("introduction");
				//ws.lat = jo.getDouble("lat")
				//ws.lon = jo.getDouble("lon");
				ws.managerid = jo.getInt("managerid");
				
				workStations.add(ws);
			
			}
			return workStations;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return new ArrayList<WorkStation>();
		}
	}
	
	

}
