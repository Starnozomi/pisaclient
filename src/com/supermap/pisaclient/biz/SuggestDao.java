/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @SuggestDao.java - 2014-7-10 下午5:06:51
 */

package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.FactorIndexRangeMsg;
import com.supermap.pisaclient.entity.Suggest;
import com.supermap.pisaclient.http.HttpHelper;

public class SuggestDao {

	public boolean addSuggest(Suggest suggest) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			if (suggest.pubertyId==0) { //大户
				params.put("param", "{\"name\":\"t_suggest.add\", \"values\":\"[{\\\"typeid\\\":" + suggest.typeid + ",\\\"areacode\\\":\\\""
						+ suggest.areacode + "\\\",\\\"title\\\":\\\"" + suggest.title + "\\\",\\\"info\\\":\\\"" + suggest.info
						+ "\\\",\\\"userid\\\":" + suggest.userid + ",\\\"pubertyId\\\":" + null + "}]\"}");
			}
			else { //专家
				params.put("param", "{\"name\":\"t_suggest.add\", \"values\":\"[{\\\"typeid\\\":" + suggest.typeid + ",\\\"areacode\\\":\\\""
						+ suggest.areacode + "\\\",\\\"title\\\":\\\"" + suggest.title + "\\\",\\\"info\\\":\\\"" + suggest.info
						+ "\\\",\\\"userid\\\":" + suggest.userid + ",\\\"pubertyId\\\":" + suggest.pubertyId + "}]\"}");
			}
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

	public Suggest getById(int id) {
		Suggest s = new Suggest();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("param", "{\"Function\":\"cq.get.suggestbyid\",\"CustomParams\":{\"id\":" + id + "},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				s.id = obj.getInt("id");
				s.typeid = obj.getInt("typeid");
				s.typename = obj.getString("typename");
				s.areacode = obj.getString("areacode");
				s.areaname = obj.getString("areaname");
				s.title = obj.getString("title");
				s.info = obj.getString("info");
				s.userid = obj.getInt("userid");
				s.username = obj.getString("username");
				s.stime = obj.getString("stime");
			}
		} catch (Exception e) {

		}
		return s;
	}

	public List<Suggest> getAll(int userId, int pageSize, int pageIndex, int typeid) {
		List<Suggest> all = new ArrayList<Suggest>();
		try {
			int num = pageSize * (pageIndex - 1);
			HashMap<String, String> params = new HashMap<String, String>();
			if (typeid == 0)
				params.put("param", "{\"Function\":\"cq.get.suggestall\",\"CustomParams\":{\"pagesize\":" + pageSize + ",\"num\":" + num
						+ ",\"userid\":" + userId + "},\"Type\":2}");
			else
				params.put("param", "{\"Function\":\"cq.get.suggest\",\"CustomParams\":{\"typeid\":" + typeid + ",\"pagesize\":" + pageSize
						+ ", \"num\":" + num + ",\"userid\":" + userId + "},\"Type\":2}");
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Suggest s = new Suggest();
				s.id = obj.getInt("id");
				s.typeid = obj.getInt("typeid");
				s.typename = obj.getString("typename");
				s.areacode = obj.getString("areacode");
				s.areaname = obj.getString("areaname");
				s.title = obj.getString("title");
				s.info = obj.getString("info");
				s.userid = obj.getInt("userid");
				s.username = obj.getString("username");
				s.stime = obj.getString("stime");
				all.add(s);
			}
		} catch (Exception e) {

		}
		return all;
	}
	
	
	/**
	 * 根据农作物类型和生产期查询影响因子
	 * @param parentId
	 * @return
	 */
	public List<Integer> getfactoryId(int growthperiod){
		List<Integer> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("growthperiod", growthperiod);
			jsonParam.put("Function", "suggest.query.factory");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<Integer>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				int temp = obj.getInt("factorId");
				list.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	
	/**
	 * 根据农作物影响因子查询因子范围信息
	 * @param parentId
	 * @return
	 */
	public List<FactorIndexRangeMsg> getfactoryList(int factorId){
		List<FactorIndexRangeMsg> list = null;
		FactorIndexRangeMsg msg = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("factorId", factorId);
			jsonParam.put("Function", "suggest.query.factory.list");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<FactorIndexRangeMsg>();
			for (int i = 0; i < array.length(); i++) {
				
				JSONObject obj = array.getJSONObject(i);
				msg = new FactorIndexRangeMsg();
				msg.descript = obj.getString("descript");
				msg.levelName = obj.getString("levelName");
				msg.suggest = obj.getString("suggest");
				msg.minValue = obj.getLong("minValue");
				msg.maxValue = obj.getLong("maxValue");
				list.add(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 根据农作物影响因子查询因子范围信息
	 * @param parentId
	 * @return
	 */
	public List<String> getExpertKnowleage(int expertid,int pubertyId){
		List<String> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("expertid", expertid);
			customParams.put("pubertyId", pubertyId);
			jsonParam.put("Function", "suggest.query.knowleage");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String content = obj.getString("content");
				if ((content!=null)&&(!content.equals(""))&&(!content.equals("null"))) {
					list.add(content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	
}
