/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @CategoryDao.java - 2014-4-2 下午2:38:32
 */

package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.CategoryItem;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.http.HttpHelper;

public class CategoryDao implements Serializable {

	private static final long serialVersionUID = 1006L;
	private Context ctx;

	public CategoryDao(Context ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * 获取精细化资讯和公众咨询的目录信息
	 * @param menu 1：精细化资讯 2：公众资讯
	 * @param areaCode 区域代码（根据区域代码获取该区域的关注作物）
	 * @return 
	 */
	public List<CategoryItem> getAllCategoryItems(int menu,String areaCode)
	{
		List<CategoryItem> all = new ArrayList<CategoryItem>();
		HashMap<String, String> params = new HashMap<String, String>();
		if (menu == 2) {
			params.put("param", "{\"Function\":\"cq.get.jcproductcategory\",\"CustomParams\":{},\"Type\":2}");
		} else {
			params.put("param", "{\"Function\":\"cq.get.profproductcategorybyarea\",\"CustomParams\":{\"areaCode\":\""+areaCode+"\"},\"Type\":2}");
		}
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();
				categoryItem.Code = obj.getString("Code");
				categoryItem.CategoryID = obj.getInt("CategoryId");
				categoryItem.CategoryName = obj.getString("CategoryName");
				all.add(categoryItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
	/**
	 * 获取产品分类
	 * @param menu 1.精细化产品分类 2.公众服务产品分类
	 * @return
	 */
	public List<CategoryItem> getAllCategoryItems(int menu) {
		List<CategoryItem> all = new ArrayList<CategoryItem>();
		HashMap<String, String> params = new HashMap<String, String>();
		if (menu == 2) {
			params.put("param", "{\"Function\":\"cq.get.jcproductcategory\",\"CustomParams\":{},\"Type\":2}");
		} else {
			params.put("param", "{\"Function\":\"cq.get.profproductcategory\",\"CustomParams\":{},\"Type\":2}");
		}
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();
				categoryItem.Code = obj.getString("Code");
				categoryItem.CategoryID = obj.getInt("CategoryID");
				categoryItem.CategoryName = obj.getString("CategoryName");
				all.add(categoryItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
	
	public List<Category> getProductCategories(String parent,String areaCode)
	{
		List<Category> all = new ArrayList<Category>();
		ProductDao productDao = new ProductDao(ctx);
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"cq.get.product.category.by.parent\",\"CustomParams\":{\"parent\":" + parent
					+ "},\"Type\":2}");
			/**获得公众资讯中的产品的目录信息**/
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();
				categoryItem.Code = obj.getString("Code");
				categoryItem.CategoryID = obj.getInt("CategoryID");
				categoryItem.CategoryName = obj.getString("CategoryName");
				Category category = new Category(categoryItem);
				/**获得某一个目录下的产品信息**/
				List<Product> products = productDao.getAllByArea(200, 1, categoryItem.CategoryID + "", areaCode);
				for (Product product : products) {
					category.addItem(product);
				}
				if (products.size() > 0)
					all.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
	/**
	 * 根据 parent 获取产品分类，并获取每种分类的产品
	 * @param parent
	 * @return
	 */
	public List<Category> getProductCategories(String parent) {
		List<Category> all = new ArrayList<Category>();
		ProductDao productDao = new ProductDao(ctx);
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"cq.get.product.category.by.parent\",\"CustomParams\":{\"parent\":" + parent
					+ "},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();
				categoryItem.Code = obj.getString("Code");
				categoryItem.CategoryID = obj.getInt("CategoryID");
				categoryItem.CategoryName = obj.getString("CategoryName");
				Category category = new Category(categoryItem);
				List<Product> products = productDao.getAll(2, 2, 200, 1, categoryItem.CategoryID + "", "");
				for (Product product : products) {
					category.addItem(product);
				}
				if (products.size() > 0)
					all.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}

		@Deprecated
	public List<Category> getCategories(int menu, int type, String userId,String areacode) {
		List<Category> all = new ArrayList<Category>();
		ProductDao productDao = new ProductDao(ctx);
		String url = null;
		if (menu == 2) {
			url = HttpHelper.GET_PRODUCT_TYPES_URL;
		} else {
			url = HttpHelper.GET_PROFTYPES_URL;
		}
		userId = userId == null ? "" : userId;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("para", "{\"userId\":\"" + userId + "\"}");
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();
				categoryItem.Code = obj.getString("Code");
				categoryItem.CategoryID = obj.getInt("CategoryID");
				categoryItem.CategoryName = obj.getString("CategoryName");
				Category category = new Category(categoryItem);
				List<Product> products = productDao.getAll(menu, type, 200, 1, categoryItem.CategoryID + "", userId);
				for (Product product : products) {
					category.addItem(product);
				}
				if (products.size() > 0)
					all.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
}
