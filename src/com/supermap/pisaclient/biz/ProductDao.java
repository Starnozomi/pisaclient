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
import com.supermap.pisaclient.entity.BaseProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.ProfProduct;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.service.ProductPoint;

/**
 * @author TRQ
 *
 */
public class ProductDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1005L;
	private TimeSharedDeal deal;

	public ProductDao(Context context) {
		this.deal = new TimeSharedDeal(context);
	}
	
	
	
	/**
	 * 对公众资讯按区域编码进行查询
	 * @param pageSize
	 * @param pageIndex
	 * @param categoryId
	 * @param areaCode
	 * @return
	 */
	public List<Product> getAllByArea(int pageSize, int pageIndex, String categoryId,String areaCode)
	{
		List<Product> all = new ArrayList<Product>();
		String url = null;
		try 
		{
			HashMap<String, String> params = new HashMap<String, String>();
			
			url = HttpHelper.GET_PRODUCT_LIST_BY_AREA_URL;
			params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s,\"areaCode\":%s}", pageSize, pageIndex, categoryId,areaCode));
				
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) 
			{
				JSONObject obj = array.getJSONObject(i);
				Product p = new Product();
				if (obj.opt("source") != null)
					p.Source = obj.getString("source");
				if ((obj.opt("SmallUrl") != null)&&(!obj.getString("SmallUrl").equals("null"))){
					p.SmallUrl = obj.getString("SmallUrl");
				}
				if (obj.opt("CreateTime") != null)
					p.CreateTime = obj.getString("CreateTime");
				if (obj.opt("ProductID") != null)
					p.ProductID = obj.getInt("ProductID");
				if (obj.opt("ProductTitle") != null)
					p.ProductTitle = obj.getString("ProductTitle");
				if (obj.opt("ProductSummary") != null)
					p.ProductSummary = obj.getString("ProductSummary");
				if (obj.opt("ProductTemplateID") != null)
					p.ProductTemplateID = obj.getInt("ProductTemplateID");
				if ((obj.opt("BigUrl") != null)&&(!obj.getString("BigUrl").equals("null")))
					{p.BigUrl = obj.getString("BigUrl");}
				if (obj.opt("CategoryName") != null)
					p.CategoryName = obj.getString("CategoryName");
				all.add(p);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
		return all;
	}
	
	
	
	
	public static List<ProductPoint> getLatestFarmProduct(String lon,String lat,String cropid){
		HashMap<String, String> params = new HashMap<String, String>();
		String url = HttpHelper.FUNCRION_QUERY;
		params.put("param", "{\"Function\":\"getLatestProduct\",\"CustomParams\":{\"lon\":\"" + lon + "\",\"lat\":\"" + lat + "\",\"cropid\":\"" + cropid + "\"},\"Type\":2}");
		//params.put("para", String.format("{\"lon\":%s,\"lat\":%s,\"cropid\":%s}", lon, lat, cropid));
		try {
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			List<ProductPoint> pList = new ArrayList<ProductPoint>();
			ProductPoint p;
			for(int i=0;i<array.length();i++){
				JSONObject obj = array.getJSONObject(0);
				p = new ProductPoint();
				if (obj.opt("ProductTitle") != null)
					p.setProductTitle(obj.getString("ProductTitle"));
				if (obj.opt("content") != null)
					p.setContent(obj.getString("content"));
				pList.add(p);
			}
			return pList;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 
	 * @param menu
	 * @param type  usertype
	 * @param pageSize
	 * @param pageIndex
	 * @param categoryId
	 * @param userId
	 * @return
	 */
	//public List<Product> getAll(int menu, int type, int pageSize, int pageIndex, String categoryId, String userId,String areaCode) {
	public List<Product> getAll(int menu, int type, int pageSize, int pageIndex, String categoryId, String userId) {
		List<Product> all = new ArrayList<Product>();
		String url = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			if (menu == 2) {
				url = HttpHelper.GET_PRODUCT_LIST_URL;
				//params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s,\"areacode\":%s}", pageSize, pageIndex, categoryId,areaCode));
				params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s}", pageSize, pageIndex, categoryId));
			} else {
				switch (type) {
				case 2:
					url = HttpHelper.GET_PRO_VIP_LIST_URL;
					if (!"".equals(userId))
						//params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s, \"userId\":%s,\"areacode\":%s}", pageSize,
						//		pageIndex, categoryId, userId,areaCode));
						params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s, \"userId\":%s}", pageSize,
								pageIndex, categoryId, userId));
					else
						//params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s, \"userId\":\"\",\"areacode\":%s}", pageSize,
						//		pageIndex, categoryId,areaCode));
						params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s, \"userId\":\"\"}", pageSize,
								pageIndex, categoryId));
					break;
				case 1:
					url = HttpHelper.GET_PRO_NOT_VIP_LIST_URL;
					//params.put("para",
					//		String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":\"%s\",\"areacode\":%s}", pageSize, pageIndex, categoryId,areaCode));
					params.put("para",
							String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":\"%s\"}", pageSize, pageIndex, categoryId));
					break;
				}
			}
			//获取公众咨询信息
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Product p = new Product();
				if (obj.opt("source") != null)
					p.Source = obj.getString("source");
				if ((obj.opt("SmallUrl") != null)&&(!obj.getString("SmallUrl").equals("null"))){
					p.SmallUrl = obj.getString("SmallUrl");
				}
				if (obj.opt("CreateTime") != null)
					p.CreateTime = obj.getString("CreateTime");
				if (obj.opt("ProductID") != null)
					p.ProductID = obj.getInt("ProductID");
				if (obj.opt("ProductTitle") != null)
					p.ProductTitle = obj.getString("ProductTitle");
				if (obj.opt("ProductSummary") != null)
					p.ProductSummary = obj.getString("ProductSummary");
				if (obj.opt("ProductTemplateID") != null)
					p.ProductTemplateID = obj.getInt("ProductTemplateID");
				if ((obj.opt("BigUrl") != null)&&(!obj.getString("BigUrl").equals("null")))
					{p.BigUrl = obj.getString("BigUrl");}
				if (obj.opt("CategoryName") != null)
					p.CategoryName = obj.getString("CategoryName");
				all.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}
	
	
	
	
	public static String getMonitorAreaName(String areaCode)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"area.getbycode\",\"CustomParams\":{\"areacode\":\"" + areaCode + "\"},\"Type\":2}");
		try 
		{
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			JSONObject jo= array.getJSONObject(0);
			
			return jo.getString("enname");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return "zhongqingshi";
		}
	}

}
