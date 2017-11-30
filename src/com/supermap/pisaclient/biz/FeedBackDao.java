/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @FeedBackDao.java - 2014-7-3 下午2:48:47
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.entity.FeedBack;
import com.supermap.pisaclient.http.HttpHelper;

public class FeedBackDao {

	public boolean addFeedBack(FeedBack feed) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"name\":\"t_feedback.add\", \"values\":\"[{\\\"info\\\":\\\"" + feed.info + "\\\",\\\"imei\\\":\\\""
					+ feed.imei + "\\\",\\\"userid\\\":" + feed.userId + "}]\"}");
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			boolean flag = false;
			try {
				Integer.parseInt(num);
				flag = true;
			} catch (Exception e) {
				flag = false;
			}
			return flag;
		} catch (Exception e) {
			return false;
		}
	}

	public List<FeedBack> getAll(Context context) {
		List<FeedBack> all = new ArrayList<FeedBack>();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"cq.get.feedback\",\"CustomParams\":{\"imei\":\"" + CommonUtil.getImei(context)
					+ "\"},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				FeedBack f = new FeedBack();
				f.id = obj.getInt("id");
				f.info = obj.getString("info");
				f.stime = obj.getString("stime");
				all.add(f);
			}
		} catch (Exception e) {

		}
		return all;
	}
}
