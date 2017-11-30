/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ScienceTypeDao.java - 2014-7-3 下午3:38:02
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.MenuItem;
import com.supermap.pisaclient.http.HttpHelper;

public class MenuDao {

	private String url;

	public MenuDao(String url) {
		this.url = url;
	}

	public List<MenuItem> getAll() {
		List<MenuItem> all = new ArrayList<MenuItem>();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"" + url + "\",\"CustomParams\":{},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				MenuItem m = new MenuItem();
				m.id = obj.getInt("id");
				m.name = obj.getString("name");
				all.add(m);
			}
		} catch (Exception e) {

		}
		return all;
	}

}
