package com.supermap.pisaclient.biz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;


import android.R.integer;

import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.http.HttpHelper;

public class MessageUploadDao {

	/**
	 * 添加消息
	 * 
	 * @param fromid 			发出人
	 * @param toid				接收人
	 * @param msgtypeid			消息类型：对应 t_msg_type表 
	 * @param mainid			如果：msgtypeid = 2 ,mainid 是咨询回复表主键id msgtypeid = 4 ,mainid 是咨询打分表主键id
	 * @param msgsendtypeid		消息发送类型：对应t_send_msg_type表 默认值：0
	 * @param sendmainid		消息发送类型对应的表的 主键  默认值 0：
	 * @param content
	 * @return  成功返回msgid， 失败返回0
	 */
	public int addMsg(ClientMsg msg){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonObj = new JSONObject();
			if (msg.fromid==0) {
				jsonObj.put("fromid","null");
			}else {
				jsonObj.put("fromid", msg.fromid);
			}
			
			if (msg.toid==0) {
				jsonObj.put("toid","null");
			}else {
				jsonObj.put("toid", msg.toid);
			}
			
			if (msg.msgtypeid==0) {
				jsonObj.put("msgtypeid","null");
			}else {
				jsonObj.put("msgtypeid", msg.msgtypeid);
			}
			
			if (msg.mainid==0) {
				jsonObj.put("mainid","null");
			}else {
				jsonObj.put("mainid", msg.mainid);
			}
			
			if (msg.msgsendtypeid==0) {
				jsonObj.put("msgsendtypeid","null");
			}else {
				jsonObj.put("msgsendtypeid", msg.msgsendtypeid);
			}
			
			if (msg.sendmainid==0) {
				jsonObj.put("sendmainid","null");
			}else {
				jsonObj.put("sendmainid", msg.sendmainid);
			}

			jsonObj.put("content", msg.content);
			if (msg.areacode==null) {
				jsonObj.put("areacode", "null");
			}
			else {
				jsonObj.put("areacode", msg.areacode);
			}
			
			if (msg.croptype==0) {
				jsonObj.put("croptype","null");
			}else {
				jsonObj.put("croptype", msg.croptype);
			}
			JSONArray jsonValue = new JSONArray();
			jsonValue.put(jsonObj);
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("name","msg.add");
			jsonParam.put("values", jsonValue);
//			  String param = "{\"name\":\"msg.add\",\"values\":[{\"fromid\":3,\"toid\":2,\"msgtypeid\":1,\"mainid\":2,\"msgsendtypeid\":0,\"sendmainid\":2,\"content\":\"好好\"}]}";
			params.put("param", jsonParam.toString());
			String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
			String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			return Integer.parseInt(num);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 添加未查看消息
	 * @param userid
	 * @param msgid
	 * @return 成功返回未查看消息id 失败返回0
	 */
	  public int addunCheckMsg(int userid,int msgid){
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("userid", userid);
				jsonObj.put("msgid", msgid);
				JSONArray jsonValue = new JSONArray();
				jsonValue.put(jsonObj);
				JSONObject jsonParam = new JSONObject();
				jsonParam.put("name","msg.add.uncheck");
				jsonParam.put("values", jsonValue);
//				String param = "{\"name\":\"msg.add.uncheck\",\"values\":[{\"userid\":3,\"msgid\":4}]}";
				params.put("param", jsonParam.toString());
				String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
				String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
				return Integer.parseInt(num);
			} catch (Exception e) {
				return 0;
			}
		}
	  
	  /**
	   * 更新消息已查看
	   * @param userid
	   * @param msgid
	   * @return 成功返回1，失败返回0
	   */
	  public int updateCheckMsg(int userid,int msgid){
		  System.out.println("userid :"+userid+"  msgid:"+msgid);
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("userid", userid);
				jsonObj.put("msgid", msgid);
				JSONArray jsonValue = new JSONArray();
				jsonValue.put(jsonObj);
				JSONObject jsonParam = new JSONObject();
				jsonParam.put("name","msg.update.checked");
				jsonParam.put("values", jsonValue);
//				  String param = "{\"name\":\"msg.update.checked\",\"values\":[{\"userid\":3,\"msgid\":5}]}";
				params.put("param", jsonParam.toString());
				String res = HttpHelper.loadString(HttpHelper.FUNCRION_UPDATE, params, HttpHelper.METHOD_POST);
				System.out.println(res+"-----");
				//				String num = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
				return Integer.parseInt(res);
			} catch (Exception e) {
				return 0;
			}
		}
	}
