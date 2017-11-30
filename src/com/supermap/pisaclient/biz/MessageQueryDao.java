package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.http.HttpHelper;

public class MessageQueryDao {
	
	
	public List<Integer> getUncheckMsg(int userid){
		
		List<Integer> msgidList = null;
		int msgid = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("userid", userid);
			jsonParam.put("Function", "msg.qury.unchecked");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
//			  String param = "{\"Function\":\"msg.qury.unchecked\",\"CustomParams\":{\"userid\":\"3\"},\"Type\":2}";
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			msgidList = new ArrayList<Integer>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("msgid")!=null) {
					msgid = obj.getInt("msgid");
					msgidList.add(msgid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return msgidList;
	}
	
	public ClientMsg getMsg(int msgid){
		
		ClientMsg clientMsg = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("msgid", msgid);
			jsonParam.put("Function", "msg.qury");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
//			  String param = "{\"Function\":\"msg.qury.unchecked\",\"CustomParams\":{\"userid\":\"3\"},\"Type\":2}";
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				clientMsg = new ClientMsg();
				if ((obj.opt("msgid")!=null)&&(!"null".equals(obj.getString("msgid")))) {
					clientMsg.msgid = obj.getInt("msgid");
				}
				if ((obj.opt("fromid")!=null)&&(!"null".equals(obj.getString("fromid")))) {
					clientMsg.fromid = obj.getInt("fromid");
				}
				if ((obj.opt("toid")!=null)&&(!"null".equals(obj.getString("toid")))) {
					clientMsg.toid = obj.getInt("toid");
				}
				if ((obj.opt("msgtypeid")!=null)&&(!"null".equals(obj.getString("msgtypeid")))) {
					clientMsg.msgtypeid = obj.getInt("msgtypeid");
				}
				if ((obj.opt("mainid")!=null)&&(!"null".equals(obj.getString("mainid")))) {
					clientMsg.mainid = obj.getInt("mainid");
				}
				if ((obj.opt("msgsendtypeid")!=null)&&(!"null".equals(obj.getString("msgsendtypeid")))) {
					clientMsg.msgsendtypeid = obj.getInt("msgsendtypeid");
				}
				if ((obj.opt("sendmainid")!=null)&&(!"null".equals(obj.getString("sendmainid")))) {
					clientMsg.sendmainid = obj.getInt("sendmainid");
				}
				if (obj.opt("content")!=null) {
					clientMsg.content = obj.getString("content");
				}
				if (obj.opt("msgTime")!=null) {
					clientMsg.msgTime = obj.getString("msgTime");
				}
				
				if ((obj.opt("areacode")!=null)&&(!"null".equals(obj.getString("areacode")))) {
					clientMsg.areacode = obj.getString("areacode");
				}
				if ((obj.opt("croptype")!=null)&&(!"null".equals(obj.getString("croptype")))) {
					clientMsg.croptype = obj.getInt("croptype");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return clientMsg;
	}
}
