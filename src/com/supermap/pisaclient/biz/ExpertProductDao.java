/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ProductDao.java - 2014-3-25 上午11:01:43
 */

package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.supermap.pisaclient.common.TimeSharedDeal;
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.http.HttpHelper;

/**
 * @author TRQ
 *
 */
public class ExpertProductDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1005L;
	private TimeSharedDeal deal;

	public ExpertProductDao(Context context) {
		this.deal = new TimeSharedDeal(context);
	}
	
	
	/**
	 * @param menu
	 * @param type  usertype
	 * @param pageSize
	 * @param pageIndex
	 * @param categoryId
	 * @param userId
	 * @return
	 */
	public List<ExpertProduct> getAll(int userid) {
		List<ExpertProduct> all = new ArrayList<ExpertProduct>();
		String url = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("para", String.format("{\"userid\":%d}", userid));
			url = HttpHelper.GET_EXPERTPRODUCT_LIST_URL;
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			ExpertProduct p;
			JSONObject jo;
			for(int i=0;i<array.length();i++){
				 jo = array.getJSONObject(i);
				 p = new ExpertProduct();
				 p.expert =jo.getString("username");
				 p.title = jo.getString("title");
				 p.content = jo.getString("content");
				 p.createTime = jo.getString("createTime");
				 p.crop = jo.getString("name");
				 p.farmlandName = jo.getString("farmlandName");
				 all.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}

}
