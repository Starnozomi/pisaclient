/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @ProductCommentDao.java - 2014-4-11 上午9:32:59
 */

package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.ProductComment;
import com.supermap.pisaclient.http.HttpHelper;

public class ProductCommentDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1954083871108373420L;

	public boolean addComment(int type, int productId, String userId, String comment) {
		type = type == 3 ? 2 : 1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			if (!"".equals(userId))
				params.put("para", "{\"type\":" + type + ",\"productId\":" + productId + ",\"userId\":\"" + userId
						+ "\", \"parentId\":\"\", \"comment\":\"" + comment + "\"}");
			else
				params.put("para", "{\"type\":" + type + ",\"productId\":" + productId
						+ ",\"userId\":\"\", \"parentId\":\"\", \"comment\":\"" + comment + "\"}");
			String res = HttpHelper.loadString(HttpHelper.ADD_COMMENT_URL, params, HttpHelper.METHOD_POST);
			return "1".equals(res);
		} catch (Exception e) {
			return false;
		}
	}

	public List<ProductComment> getAllComments(int type, int productId, int pageSize, int pageIndex) {
		List<ProductComment> all = new ArrayList<ProductComment>();
		type = type == 3 ? 2 : 1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("para", "{\"pageSize\":" + pageSize + ",\"pageIndex\":" + pageIndex + ",\"type\":" + type + ", \"productId\":"
					+ productId + "}");
			JSONArray array = HttpHelper.load(HttpHelper.GET_PRODUCT_COMMENT_URL, params, HttpHelper.METHOD_POST);
			String name = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				ProductComment comment = new ProductComment();
				comment.parentId = obj.getInt("parentId");
				comment.productId = obj.getInt("productId");
				comment.comment = obj.getString("comment");
				comment.userId = obj.getInt("userId");
				name = obj.getString("username");
				if (name != null && "null".equals(name.toLowerCase())) {
					name = "匿名";
				} else {
					comment.userName = name;
				}
				comment.commentTime = obj.getString("commentTime");
				all.add(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
}
