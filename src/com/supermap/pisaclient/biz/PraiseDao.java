/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @PraiseDao.java - 2014-4-11 下午3:46:49
 */

package com.supermap.pisaclient.biz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.http.HttpHelper;

public class PraiseDao {

	public boolean addPraise(int type, int productId, String userId) {
		type = type == 3 ? 2 : 1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			if (!"".equals(userId))
				params.put("para", "{\"type\":" + type + ",\"productId\":" + productId + ",\"userId\":\"" + userId + "\"}");
			else
				params.put("para", "{\"type\":" + type + ",\"productId\":" + productId + ",\"userId\":\"\"}");
			String res = HttpHelper.loadString(HttpHelper.ADD_PRAISE_URL, params, HttpHelper.METHOD_POST);
			return "1".equals(res);
		} catch (Exception e) {
			return false;
		}
	}

	public int getPraiseCount(int type, int productid, String userid) {
		type = type == 3 ? 2 : 1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = null;
			if (type == 2)//专业 or精细化
				param = "{\"Function\":\"cq.get.pro.product.praisecount\",\"CustomParams\":{\"userid\":\"" + userid + "\",\"productid\":\""
						+ productid + "\" },\"Type\":2}";
			else//公众 or 决策
				param = "{\"Function\":\"cq.get.product.praisecount\",\"CustomParams\":{\"userid\":\"" + userid + "\",\"productid\":\""
						+ productid + "\" },\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			return obj.getInt("total");
		} catch (Exception e) {

		}
		return -1;
	}
}
