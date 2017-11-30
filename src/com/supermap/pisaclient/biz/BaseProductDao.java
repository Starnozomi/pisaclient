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
import com.supermap.pisaclient.entity.ExpertProduct;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.http.HttpHelper;

/**
 * @author TRQ
 *
 */
public class BaseProductDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1005L;
	private TimeSharedDeal deal;

	public BaseProductDao(Context context) {
		this.deal = new TimeSharedDeal(context);
	}
	
	public BaseProductDao(){}
	
	private static BaseProductDao instance = null;
	public static BaseProductDao getInstance(){
		if(instance == null){
			instance = new BaseProductDao();
		}
		return instance;
	}
	
	public List<BaseProduct> getAllProduct(){
		List<BaseProduct> all = new ArrayList<BaseProduct>();
	String url = "http://218.62.41.108:8020/productController.do?getProductsByContentTypeAndAreaCodeAndBrand&page=1&size=30";
		//String url = "http://www.ynpasc.cn/productController.do?getProductsByContentTypeAndAreaCodeAndBrand&brand=YUNYAN&page=1&size=30";
		HashMap<String, String> params = new HashMap<String, String>();
		BaseProduct bp = null;
		JSONObject jo = null;
		//params.put("para", String.format("{\"pageSize\":%d,\"pageIndex\":%d,\"categoryId\":%s,\"areaCode\":%s}", pageSize, pageIndex, categoryId,areaCode));
		try {
			JSONArray array = HttpHelper.load(url, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) 
			{
				jo = array.getJSONObject(i);
				bp = new BaseProduct();
				bp.productid = jo.getString("id");
				bp.title = jo.getString("productTitle");
				bp.createTime =  jo.getString("createDate");
				bp.sender = jo.getString("productMakerName");
				bp.folder = jo.getString("folderName");
				all.add(bp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return all;
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
	public List<BaseProduct> getAll(int userid) {
		List<BaseProduct> all = new ArrayList<BaseProduct>();
		String url = null;
		try {
			/*HashMap<String, String> params = new HashMap<String, String>();
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
			}*/
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"app.getWorkStationProduct\",\"CustomParams\":{\"userid\":\"" + userid + "\"},\"Type\":2}");
			//url = HttpHelper.GET_EXPERTPRODUCT_LIST_URL;
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			BaseProduct  p;
			JSONObject jo;
			for(int i=0;i<array.length();i++){
				 jo = array.getJSONObject(i);
				 p = new BaseProduct();
				 p.title = jo.getString("ptitle");
				 p.content = jo.getString("pcontent");
				 p.createTime = jo.getString("ptime");
				 p.expert = jo.getString("psendername");
				all.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return all;
	}

}
