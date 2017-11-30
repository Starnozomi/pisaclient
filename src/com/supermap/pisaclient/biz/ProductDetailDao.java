/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ProductDetailDao.java - 2014-4-4 上午11:25:13
 */

package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONObject;

import com.supermap.pisaclient.entity.ProductDetail;
import com.supermap.pisaclient.http.HttpHelper;

public class ProductDetailDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6118694885324901662L;

	public ProductDetail getById(int type, int id, String userId, int pageSize, int pageIndex) {
		ProductDetail p = new ProductDetail();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("para", "{\"productId\":" + id + "}");
			String url = null;
			switch (type) {
			case 1: // 公众服务 普通用户
				url = HttpHelper.GET_PUBLIC_CONTENT_BY_ID_URL;
				break;
			case 2: // Vip服务 vip
				url = HttpHelper.GET_VIP_CONTENT_BY_ID_URL;
				if (!"".equals(userId))
					params.put("para", "{\"productId\":" + id + ", \"userId\":" + userId + "}");
				else
					params.put("para", "{\"productId\":" + id + ", \"userId\":\"\"}");
				break;
			case 3: // 决策产品
				url = HttpHelper.GET_CONTENT_BY_ID_URL;
				break;
			}
			JSONObject obj = HttpHelper.loadOne(url, params, HttpHelper.METHOD_POST);
			String info = null;
			if (obj.opt("ProductID") != null)
				p.productID = obj.getInt("productID");
			if (obj.opt("htmlProductURL") != null)
				p.htmlProductURL = obj.getString("htmlProductURL");
			if (obj.opt("praiseCount") != null)
				p.praiseCount = obj.getInt("praiseCount");
			if (obj.opt("publicInfo") != null) {
				info = obj.getString("publicInfo");
				if (info != null && !"null".equals(info.toLowerCase())) {
					p.publicInfo = info;
				}
			}
			if (obj.opt("content") != null) {
				info = obj.getString("content");
				if (info != null && !"null".equals(info.toLowerCase())) {
					p.publicInfo = info;
				}
			}
			if (obj.opt("productImage") != null)
				p.productImage = obj.getString("productImage");
			if (obj.opt("commentCnt") != null)
				p.commentCnt = obj.getInt("commentCnt");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return p;
	}
}
