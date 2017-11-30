/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ScienceDao.java - 2014-7-3 下午3:39:10
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.Science;
import com.supermap.pisaclient.http.HttpHelper;

public class ScienceDao {

	public List<Science> getAll(int pageSize, int pageIndex, int sciencetype) {
		List<Science> all = new ArrayList<Science>();
		try {
			int num = pageSize * (pageIndex - 1);
			HashMap<String, String> params = new HashMap<String, String>();
			if (sciencetype == 0)
				params.put("param", "{\"Function\":\"cq.get.scienceall\",\"CustomParams\":{\"pagesize\":" + pageSize + ",\"num\":" + num
						+ "},\"Type\":2}");
			else
				params.put("param", "{\"Function\":\"cq.get.science\",\"CustomParams\":{\"sciencetype\":" + sciencetype + ",\"pagesize\":"
						+ pageSize + ", \"num\":" + num + "},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Science s = new Science();
				s.id = obj.getInt("id");
				s.title = obj.getString("title");
				s.pic = obj.getString("pic");
				s.stime = obj.getString("stime");
				s.info = obj.getString("info");
				all.add(s);
			}
		} catch (Exception e) {

		}
		return all;
	}

	public Science getById(int id) {
		Science s = new Science();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"cq.get.scienceid\",\"CustomParams\":{\"id\":" + id + "},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				s.id = obj.getInt("id");
				s.title = obj.getString("title");
				s.pic = obj.getString("pic");
				s.stime = obj.getString("stime");
				s.info = obj.getString("info");
			}
		} catch (Exception e) {

		}
		return s;
	}
}
