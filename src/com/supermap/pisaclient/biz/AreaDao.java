/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @AreaCodeDao.java - 2014-9-10 下午1:47:51
 */

package com.supermap.pisaclient.biz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.Area;
import com.supermap.pisaclient.http.HttpHelper;

public class AreaDao {

	public Area getParentAreaByCode(String areaCode) {
		if (areaCode != null && areaCode.length() > 9) {
			areaCode = areaCode.substring(0, 9);
		}
		Area area = new Area();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"get.farm.parent.areacodename\",\"CustomParams\":{\"code\":\"" + areaCode + "\"},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			JSONObject obj = array.getJSONObject(0);
			area.areaid = obj.getInt("areaid");
			area.areacode = obj.getString("areacode");
			area.areaname = obj.getString("areaname");
			area.pareacode = obj.getString("pareacode");
			area.pareaname = obj.getString("pareaname");
		} catch (Exception e) {

		}
		return area;
	}

}
