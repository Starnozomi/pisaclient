package com.supermap.pisaclient.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSException;

import android.R.integer;

import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ElapseTime;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.http.HttpHelper;

public class AdvQueryDao {
	
	/**
	 * 获取最新的咨询信息
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> getAdvInfoByNew(int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.all.new");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("最新咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list =getAdvisoryInfos(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	public List<AdvisoryInfo> getAdvInfoByNew(String areaCode,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("pagesize", pagesize);
			customParams.put("areaCode", areaCode);
			jsonParam.put("Function", "advisoryinfo.getbyareacode.all.new");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("最新咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list =getAdvisoryInfos(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	
	
	
	/**
	 * 获取我的的咨询信息
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> getAdvInfoofMy(int userId,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("userId", userId);
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.all.my");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("我的咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
		    list = getAdvisoryInfos(array) ;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 获取我的的咨询信息
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> serchAdvInfo(String content,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("content", "%"+content+"%");
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.by.content");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("搜索咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list = getAdvisoryInfos(array);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	
	
	/**
	 * 获取我的的咨询信息
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> getAdvInfoofMyExpert(int userId,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("userId", userId);
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.all.my.expert");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("我的咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list = getAdvisoryInfos(array);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	/**
	 * 获取学科分类咨询信息
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> getAdvInfoBySubject(int subjectid,int userId,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("subjectid", subjectid);
			customParams.put("userId", userId);
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.all.subject");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = getAdvisoryInfos(array);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 获取热门分类咨询信息
	 * 评论数目最多
	 * @param parentId
	 * @return
	 */
	public List<AdvisoryInfo> getAdvInfoByComments(int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("pagesize", pagesize);
			jsonParam.put("Function", "advisoryinfo.get.all.more");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("热门咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list = getAdvisoryInfos(array) ;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	public List<AdvisoryInfo> getAdvInfoByComments(String areaCode,int num,int pagesize){
		List<AdvisoryInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"adv.subject.get\",\"CustomParams\":{\"parentId\":"+parentId+"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("num", num);
			customParams.put("pagesize", pagesize);
			customParams.put("areaCode", areaCode);
			jsonParam.put("Function", "advisoryinfo.getbyareacode.all.more");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			ElapseTime elapseTime = new ElapseTime("热门咨询");
			elapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			elapseTime.end();
			list = getAdvisoryInfos(array) ;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	public AdvisoryInfo getAdvisoryInfo(int advInfoId){
		AdvisoryInfo advinfo = null;
//		String param = "{\"Function\":\"adv.query\",\"CustomParams\":{\"advInfoId\":2},\"Type\":2}";
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("advInfoId", advInfoId);
			jsonParam.put("Function", "adv.query");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advinfo = new AdvisoryInfo();
				if (obj.opt("advInfoId")!=null) {
					advinfo.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("userId")!=null) {
					advinfo.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					advinfo.userName = obj.optString("username");
				}
				if (obj.opt("HeadImage")!=null) {
					advinfo.userHeaderFile = obj.optString("HeadImage");
				}
				if (obj.opt("uploadTime")!=null) {
					advinfo.uploadTime = obj.optString("uploadTime");
				}
				if (obj.opt("areacode")!=null) {
					advinfo.areacode = obj.optString("areacode");
				}
				if (obj.opt("qestion")!=null) {
					advinfo.qestion = obj.optString("qestion");
				}
				
				if (obj.opt("subjectName")!=null) {
					advinfo.subjectName = obj.optString("subjectName");
				}
				
				advinfo.mImgs = new ArrayList<AdvImgs>();
				advinfo.mComments = new ArrayList<AdvinfoComment>();
				advinfo.mPraises = new  ArrayList<AdvPraise>();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		List<AdvisoryInfo> list = new ArrayList<AdvisoryInfo>();
		list.add(advinfo);
		getAdvImgs(list);
		getAdvPraise(list);
		getAdvComments(list);
		return list.get(0);
	}



	
	public List<AdvImgs> getAdvImgs(int advInfoId){
		
		List<AdvImgs> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			 String param = "{\"Function\":\"adv.getAdvImg\",\"CustomParams\":{\"advInfoId\":\"1\"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("advInfoId", advInfoId);
			jsonParam.put("Function", "adv.getAdvImg");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvImgs>();
			AdvImgs advImgs  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advImgs = new AdvImgs();
				if (obj.opt("advInfoId")!=null) {
					advImgs.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("imageURLContent")!=null) {
					advImgs.URLcontent = obj.getString("imageURLContent");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						advImgs.descript="";
					}
					else {
						advImgs.descript = obj.getString("descript");
					}
				}
				list.add(advImgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	
	public List<AdvinfoComment> getAdvComments(int advInfoId){
		
		List<AdvinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			 String param = "{\"Function\":\"adv.getAdvComment\",\"CustomParams\":{\"advInfoId\":\"2\"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("advInfoId", advInfoId);
			jsonParam.put("Function", "adv.getAdvComment");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvinfoComment>();
			AdvinfoComment advinfoComment  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advinfoComment = new AdvinfoComment();
				if (obj.opt("commentId")!=null) {
					advinfoComment.commentId = obj.optInt("commentId");
				}
				if (obj.opt("advInfoId")!=null) {
					advinfoComment.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("comment")!=null) {
					advinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("commentTime")!=null) {
					advinfoComment.commentTime = obj.getString("commentTime");
				}
				
				if (obj.opt("expertid")!=null) {
					if (obj.getString("expertid").equals("null")) {
						advinfoComment.isExpert = false;
					}
					else {
						advinfoComment.expertid = obj.optInt("expertid");
						advinfoComment.isExpert = true;
					}
				}
				//获取专家名字
				if (advinfoComment.isExpert) {
					if (obj.opt("expertName")!=null) {
						advinfoComment.expertName = obj.getString("expertName");
					}
					if (obj.opt("score")!=null) {
						if (!obj.getString("score").equals("null")) {
							advinfoComment.score = (float)obj.getDouble("score");
						}
						else {
							advinfoComment.score = -1;
						}
					}
				
				}
				else {//获取用户名字
					if (obj.opt("userId")!=null) {
						advinfoComment.userId = obj.optInt("userId");
						advinfoComment.username = obj.getString("username");
					}
				}
				//设置回复的是谁的名字
				if (obj.opt("parentId")!=null) {
					advinfoComment.parentId = obj.getInt("parentId");
					if (advinfoComment.parentId != -1) {
						if (obj.opt("parentExpertId")!=null) {
							if (!obj.getString("parentExpertId").equals("null")) {
								advinfoComment.parentusername = obj.getString("parentExpertName");
							}else {
								advinfoComment.parentusername = obj.getString("parentUsername");
							}
						}
					}
				}
				list.add(advinfoComment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 根据commentid 获取改评论
	 * @param commentid
	 * @return
	 */
	public List<AdvinfoComment> getAdvComment(int commentid){
		
		List<AdvinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			 String param = "{\"Function\":\"adv.getAdvComment\",\"CustomParams\":{\"advInfoId\":\"2\"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("id", commentid);
			jsonParam.put("Function", "adv.get.comment");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvinfoComment>();
			AdvinfoComment advinfoComment  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advinfoComment = new AdvinfoComment();
				if (obj.opt("commentId")!=null) {
					advinfoComment.commentId = obj.optInt("commentId");
				}
				if (obj.opt("advInfoId")!=null) {
					advinfoComment.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("comment")!=null) {
					advinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("commentTime")!=null) {
					advinfoComment.commentTime = obj.getString("commentTime");
				}
				
				if (obj.opt("expertid")!=null) {
					if (obj.getString("expertid").equals("null")) {
						advinfoComment.isExpert = false;
					}
					else {
						advinfoComment.expertid = obj.optInt("expertid");
						advinfoComment.isExpert = true;
					}
				}
				//获取专家名字
				if (advinfoComment.isExpert) {
					if (obj.opt("expertName")!=null) {
						advinfoComment.expertName = obj.getString("expertName");
					}
					if (obj.opt("score")!=null) {
						if (!obj.getString("score").equals("null")) {
							advinfoComment.score = (float)obj.getDouble("score");
						}
						else {
							advinfoComment.score = -1;
						}
					}
				
				}
				else {//获取用户名字
					if (obj.opt("userId")!=null) {
						advinfoComment.userId = obj.optInt("userId");
						advinfoComment.username = obj.getString("username");
					}
				}
				//设置回复的是谁的名字
				if (obj.opt("parentId")!=null) {
					advinfoComment.parentId = obj.getInt("parentId");
					if (advinfoComment.parentId != -1) {
						if (obj.opt("parentExpertId")!=null) {
							if (!obj.getString("parentExpertId").equals("null")) {
								advinfoComment.parentusername = obj.getString("parentExpertName");
							}else {
								advinfoComment.parentusername = obj.getString("parentUsername");
							}
						}
					}
				}
				list.add(advinfoComment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 根据评论ID获取评论的用户ID
	 * @param commentId
	 * @return
	 */
	public int getUserId(int commentId){
		int  userId = -1;
		HashMap<String, String> params = new HashMap<String, String>();
		String param = "{\"Function\":\"adv.getuserId\",\"CustomParams\":{\"commentId\":"+commentId+"},\"Type\":2}";
		params.put("param",  param);
		try {
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("userId")!=null) {
					if (obj.getInt("userId")!=-1) {
						userId = obj.getInt("userId");
					}
					else {
						userId = -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return userId;
		
	}
	
	/**
	 * 根据咨询id和专家id获取专家打分
	 * @param commentId
	 * @return
	 */
	public float getExpertScore(int advInfoId,int expertId){
		float  score = -1;
		
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("advInfoId", advInfoId);
			customParams.put("expertId", expertId);
			jsonParam.put("Function", "adv.getExpertScore");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if ((obj.opt("score")!=null)&&(!obj.opt("score").equals("null"))) {
					score = (float)obj.getDouble("score");
				}
				else {
					score = -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return score;
		
	}
	
	public List<AdvPraise> getAdvPraises(int advInfoId){
		
		List<AdvPraise> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			 String param = "{\"Function\":\"adv.getAdvImg\",\"CustomParams\":{\"advInfoId\":\"1\"},\"Type\":2}";
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("advInfoId", advInfoId);
			jsonParam.put("Function", "adv.getAdvPraise");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvPraise>();
			AdvPraise advPraise  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advPraise = new AdvPraise();
				if (obj.opt("advInfoId")!=null) {
					advPraise.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("id")!=null) {
					advPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("isPraise")!=null) {
					advPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					advPraise.userId = obj.optInt("userId");
				}
				if (obj.opt("praiseTime")!=null) {
					advPraise.praiseTime = obj.optString("praiseTime");
				}
				list.add(advPraise);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 
	 * 根据id获取作物种类名字
	 * @return
	 */
	public String getSubjectName(int id) {
		String name = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"adv.get.subjectmsg\",\"CustomParams\":{\"id\":\"" + id + "\" },\"Type\":2}";
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.GET_CROP_BY_ID_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				name = obj.getString("name");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}
	
	private String getParams(List<AdvisoryInfo> advInfos,String function){
		JSONObject param = new JSONObject();
		try {
			param.put("Function", function);
			param.put("Type", 3);
			param.put("CustomParams", getCustomParams(advInfos));
		} catch (JSONException e) {
		}
		return param.toString();
	}
	
	private String getCustomParams(List<AdvisoryInfo> advInfos){
		JSONArray customParams = new JSONArray();
		try {
			for (AdvisoryInfo advInfo :advInfos) {
				JSONObject obj = new JSONObject();
				obj.put("advInfoId", advInfo.advInfoId);
				customParams.put(obj);
			}
		} catch (Exception e) {
		}
		return customParams.toString();
	}
	
	public AdvisoryInfo getAdvisoryInfoByScoreID(int scoreId){
		List<AdvisoryInfo> list = new ArrayList<AdvisoryInfo>();
		AdvisoryInfo advinfo = null;
//		String param = "{\"Function\":\"adv.query\",\"CustomParams\":{\"advInfoId\":2},\"Type\":2}";
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("scoreId", scoreId);
			jsonParam.put("Function", "adv.score.advinfo");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param",  jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = getAdvisoryInfos(array);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if ((list!=null)&&(list.size()>0)) {
			return list.get(0);
		}
		else {
			return null;
		}
		
	}
	
	private List<AdvisoryInfo> getAdvisoryInfos(JSONArray array){
		List<AdvisoryInfo> list = new ArrayList<AdvisoryInfo>();
		try {
			AdvisoryInfo advinfo = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advinfo = new AdvisoryInfo();
				if (obj.opt("advInfoId")!=null) {
					advinfo.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("userId")!=null) {
					advinfo.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					advinfo.userName = obj.optString("username");
				}
				if (obj.opt("HeadImage")!=null) {
					advinfo.userHeaderFile = obj.optString("HeadImage");
				}
				if (obj.opt("uploadTime")!=null) {
					advinfo.uploadTime = obj.optString("uploadTime");
				}
				if (obj.opt("areacode")!=null) {
					advinfo.areacode = obj.optString("areacode");
				}
				if (obj.opt("qestion")!=null) {
					advinfo.qestion = obj.optString("qestion");
				}
				
				if (obj.opt("subjectName")!=null) {
					advinfo.subjectName = obj.optString("subjectName");
				}
				
				advinfo.mImgs = new ArrayList<AdvImgs>();
				advinfo.mComments = new ArrayList<AdvinfoComment>();
				advinfo.mPraises = new  ArrayList<AdvPraise>();
				
				list.add(advinfo);
			}
		} catch (Exception e) {
		}
		if ((list!=null)&&(list.size()>0)) {
			ElapseTime elapseTime  = null;
			elapseTime = new ElapseTime("咨询图片");
			elapseTime.start();
			getAdvImgs(list);
			elapseTime.end();
			
			elapseTime = new ElapseTime("咨询评论");
			elapseTime.start();
			getAdvComments(list);
			elapseTime.end();
			
			elapseTime = new ElapseTime("咨询赞");
			elapseTime.start();
			getAdvPraise(list);
			elapseTime.end();
		}
		return list;
	}
	
	private boolean getAdvImgs(List<AdvisoryInfo> advInfos){
		List<AdvImgs> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"crop.getCropImg\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			String param  =  getParams(advInfos, "adv.getAdvImg");
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvImgs>();
			AdvImgs advImg  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advImg = new AdvImgs();
				
				if (obj.opt("advInfoId")!=null) {
					advImg.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("imageURLContent")!=null) {
					advImg.URLcontent = obj.getString("imageURLContent");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						advImg.descript="";
					}
					else {
						advImg.descript = obj.getString("descript");
					}
				}
				
				for(AdvisoryInfo advInfo:advInfos){
					if (advImg.advInfoId==advInfo.advInfoId) {
						advInfo.mImgs.add(advImg);
						break;
					}
				}
				
				list.add(advImg);//可以用于返回
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean getAdvComments(List<AdvisoryInfo> advInfos){
		List<AdvinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"crop.getCropImg\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			String param  =  getParams(advInfos, "adv.getAdvComment");
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvinfoComment>();
			AdvinfoComment advinfoComment  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advinfoComment = new AdvinfoComment();
				if (obj.opt("commentId")!=null) {
					advinfoComment.commentId = obj.optInt("commentId");
				}
				if (obj.opt("advInfoId")!=null) {
					advinfoComment.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("comment")!=null) {
					advinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("commentTime")!=null) {
					advinfoComment.commentTime = obj.getString("commentTime");
				}
				
				if (obj.opt("expertid")!=null) {
					if (obj.getString("expertid").equals("null")) {
						advinfoComment.isExpert = false;
					}
					else {
						advinfoComment.expertid = obj.optInt("expertid");
						advinfoComment.isExpert = true;
					}
				}
				//获取专家名字
				if (advinfoComment.isExpert) {
					if (obj.opt("expertName")!=null) {
						advinfoComment.expertName = obj.getString("expertName");
					}else{
						advinfoComment.expertName = obj.getString("username");
					}
					if (obj.opt("score")!=null) {
						if (!obj.getString("score").equals("null")) {
							advinfoComment.score = (float)obj.getDouble("score");
						}
						else {
							advinfoComment.score = -1;
						}
					}
				
				}
				else {//获取用户名字
					if (obj.opt("userId")!=null) {
						advinfoComment.userId = obj.optInt("userId");
						advinfoComment.username = obj.getString("username");
					}
				}
				//设置回复的是谁的名字
				if (obj.opt("parentId")!=null) {
					advinfoComment.parentId = obj.getInt("parentId");
					if (advinfoComment.parentId != -1) {
						if (obj.opt("parentExpertId")!=null) {
							if (!obj.getString("parentExpertId").equals("null")) {
								advinfoComment.parentusername = obj.getString("parentExpertName");
							}else {
								advinfoComment.parentusername = obj.getString("parentUsername");
							}
						}
					}
				}
				//查询默认是 升序
				for(AdvisoryInfo advInfo:advInfos){
					if (advinfoComment.advInfoId==advInfo.advInfoId) {
						advInfo.mComments.add(advinfoComment);
						break;
					}
				}
				list.add(advinfoComment);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean getAdvPraise(List<AdvisoryInfo> advInfos){
		List<AdvPraise> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = getParams(advInfos,"adv.getAdvPraise");
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AdvPraise>();
			AdvPraise advPraise = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advPraise = new AdvPraise();
				if (obj.opt("advInfoId")!=null) {
					advPraise.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("id")!=null) {
					advPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("isPraise")!=null) {
					advPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					advPraise.userId = obj.optInt("userId");
				}
				if (obj.opt("praiseTime")!=null) {
					advPraise.praiseTime = obj.optString("praiseTime");
				}
				list.add(advPraise);
				
				///查询默认是 升序
				for(AdvisoryInfo advInfo:advInfos){
					if (advPraise.advInfoId==advInfo.advInfoId) {
						advInfo.mPraises.add(advPraise);
						break;
					}
				}
				
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public List<AdvisoryInfo> getAdvInfoByPage(int type,int userID,String content ,boolean isExpert,int num ,int pagesize){
		List<AdvisoryInfo> list = new ArrayList<AdvisoryInfo>();
		switch (type) {
		case Constant.ADV_TYPE_HOT:// 热门咨询信息
			list = getAdvInfoByComments(num, pagesize);
			break;
		case -1:
		case Constant.ADV_TYPE_NEW:// 最新咨询信息
			list = getAdvInfoByNew(num, pagesize);
			break;
		case Constant.ADV_TYPE_MY:// 我的咨询信息
			if (isExpert) {
				list = getAdvInfoofMyExpert(userID, num,pagesize);
			} else {
				list = getAdvInfoofMy(userID, num,pagesize);
			}
			break;
		case 4:// 搜索咨询信息
			list = serchAdvInfo(content, num,pagesize);
			break;
		default:
			break;
		}
		return list;
	}
	
	public List<AdvisoryInfo> getAdvInfoByPage(String areaCode, int type,int userID,String content ,boolean isExpert,int num ,int pagesize){
		List<AdvisoryInfo> list = new ArrayList<AdvisoryInfo>();
		switch (type) {
		case Constant.ADV_TYPE_HOT:// 热门咨询信息
			list = getAdvInfoByComments(areaCode, num, pagesize);
			break;
		case -1:
		case Constant.ADV_TYPE_NEW:// 最新咨询信息
			list = getAdvInfoByNew(areaCode, num, pagesize);
			break;
		case Constant.ADV_TYPE_MY:// 我的咨询信息
			if (isExpert) {
				list = getAdvInfoofMyExpert(userID, num, pagesize);
			} else {
				list = getAdvInfoofMy(userID, num, pagesize);
			}
			break;
		case 4:// 搜索咨询信息
			list = serchAdvInfo(content, num, pagesize);
			break;
		default:
			break;
		}
		return list;
	}
	
}
