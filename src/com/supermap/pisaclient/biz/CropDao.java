package com.supermap.pisaclient.biz;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.array;

import com.supermap.pisaclient.common.ElapseTime;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.CropInfo;
import com.supermap.pisaclient.entity.CropPeriod;
import com.supermap.pisaclient.entity.CropType;
import com.supermap.pisaclient.entity.FeedBack;
import com.supermap.pisaclient.http.HttpHelper;

public class CropDao  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4304390222622599031L;
	
	
	@Deprecated
	public List<AgrInfo> getAgrInfos(int userId, String lastFreshTime){
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = getAgrInfos(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	public int getAgrInfosNum(int userId,int typeId) {
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllbyUserIdAndTypeId.num\",\"CustomParams\":{\"userId\":"+userId+",\"typeId\":"+typeId+" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
		}
		return totalCount;
	}
	
	public int getAgrInfosNum(int userId){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAll.num\",\"CustomParams\":{\"userId\":"+userId+" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
		}
		return totalCount;
	}
	
	public List<AgrInfo> getAgrInfosByid(int userId) {
		List<AgrInfo> list = null;
		try {
				HashMap<String, String> params = new HashMap<String, String>();
				JSONObject paramsjObject = new JSONObject();
				JSONObject customJObject = new JSONObject();
//				customJObject.put("num", (pageIndex-1)*pageSize);
				customJObject.put("userId", userId);
//				customJObject.put("typeId", typeId);
//				customJObject.put("pagesize", pageSize);
				
				paramsjObject.put("Function", "cropInfo.getAllbyUserId.index");
				paramsjObject.put("CustomParams", customJObject.toString());
				paramsjObject.put("Type", 2);
				
//				String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
				String param = paramsjObject.toString();
				params.put("param",  param);
				ElapseTime cropsElapseTime = new ElapseTime("农情信息");
				cropsElapseTime.start();
				JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
				cropsElapseTime.end();
				//list = getAgrInfos(array);
				list = getAgrInfosNew(array);
			} catch (Exception e) {
				return null;
			}
			return list;
	}
	
	public int getRefreshAgrInfosNum(int userId,String lastFreshTime){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllByTime.num\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
		}
		return totalCount;
	}
	
	/**
	 * 根据用户id和种养类型id获取农情
	 * @param userId
	 * @param typeId
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<AgrInfo> getAgrInfos(int userId,int typeId,int pageSize,int pageIndex){
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject paramsjObject = new JSONObject();
			JSONObject customJObject = new JSONObject();
			customJObject.put("num", (pageIndex-1)*pageSize);
			customJObject.put("userId", userId);
			customJObject.put("typeId", typeId);
			customJObject.put("pagesize", pageSize);
			
			paramsjObject.put("Function", "cropInfo.getAllbyUserIdAndTypeId.index");
			paramsjObject.put("CustomParams", customJObject.toString());
			paramsjObject.put("Type", 2);
			
//			String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			String param = paramsjObject.toString();
			params.put("param",  param);
			ElapseTime cropsElapseTime = new ElapseTime("农情信息");
			cropsElapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			cropsElapseTime.end();
			//			list = getAgrInfos(array);
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	/**
	 *  查询所有
	 * @param userId
	 * @param pageSize  >0
	 * @param pageIndex 从1开始
	 * @return
	 */
	public List<AgrInfo> getAgrInfos(int userId,int pageSize,int pageIndex) {
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject paramsjObject = new JSONObject();
			JSONObject customJObject = new JSONObject();
			customJObject.put("num", (pageIndex-1)*pageSize);
			customJObject.put("userId", userId);
			customJObject.put("pagesize", pageSize);
			
			paramsjObject.put("Function", "cropInfo.getAll.index");
			paramsjObject.put("CustomParams", customJObject.toString());
			paramsjObject.put("Type", 2);
			
//			String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			String param = paramsjObject.toString();
			params.put("param",  param);
			ElapseTime cropsElapseTime = new ElapseTime("农情信息");
			cropsElapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			cropsElapseTime.end();
			//			list = getAgrInfos(array);
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	@Deprecated
	public List<AgrInfo> getAgrInfosByAreacode(String areacode, String lastFreshTime){
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllbyAreacode\",\"CustomParams\":{\"areacode\":\""+areacode+"\",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
//			list = getAgrInfos(array);
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	public int getAgrInfosByAreacodeNum(String areacode,int typeId){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllbyAreacodeAndTypeId.num\",\"CustomParams\":{\"areacode\":\""+areacode+"\",\"typeId\":"+typeId+" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
			totalCount = -1;
		}
		return totalCount;
	}
	
	public int getAgrInfosByAreacodeNum(String areacode){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllbyAreacode.num\",\"CustomParams\":{\"areacode\":\""+areacode+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
			totalCount = -1;
		}
		return totalCount;
	}
	
	public int getRefreshAgrInfosByAreacodeNum(String areacode,String lastFreshTime){
		int totalCount = 0;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"cropInfo.getAllByTime.num\",\"CustomParams\":{\"areacode\":\""+areacode+"\",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			JSONObject obj = array.getJSONObject(0);
			if (obj.opt("num")!=null) {
				totalCount = obj.getInt("num");
			}
		} catch (Exception e) {
			totalCount = -1;
		}
		return totalCount;
	}
	
	/**
	 * 根据区域代码和种养类型id获取农情
	 * @param areacode
	 * @param typeId
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<AgrInfo> getAgrInfosByAreacode(String areacode,int typeId,int pageSize,int pageIndex){
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject paramsjObject = new JSONObject();
			JSONObject customJObject = new JSONObject();
			customJObject.put("num", (pageIndex-1)*pageSize);
			customJObject.put("typeId", typeId);
			customJObject.put("areacode", areacode);
			customJObject.put("pagesize", pageSize);
			
			paramsjObject.put("Function", "cropInfo.getAllbyAreacodeAndTypeId.index");
			paramsjObject.put("CustomParams", customJObject.toString());
			paramsjObject.put("Type", 2);
			
//			String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			String param = paramsjObject.toString();
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
//			list = getAgrInfos(array);
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	/**
	 * 
	 * @param pageSize  >0
	 * @param pageIndex 从1开始
	 * @return
	 */
	public List<AgrInfo> getAgrInfosByAreacode(String areacode, int pageSize, int pageIndex){
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject paramsjObject = new JSONObject();
			JSONObject customJObject = new JSONObject();
			customJObject.put("num", (pageIndex-1)*pageSize);
			customJObject.put("areacode", areacode);
			customJObject.put("pagesize", pageSize);
			
			paramsjObject.put("Function", "cropInfo.getAllbyAreacode.index");
			paramsjObject.put("CustomParams", customJObject.toString());
			paramsjObject.put("Type", 2);
			
//			String param = "{\"Function\":\"cropInfo.getAll\",\"CustomParams\":{\"userId\":"+userId+",\"lastFreshTime\":\""+lastFreshTime+"\" },\"Type\":2}";
			String param = paramsjObject.toString();
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
//			list = getAgrInfos(array);
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	@Deprecated
	public List<AgrImgs> getAgrImgs(int agrInfoId){
		List<AgrImgs> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCropImg\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrImgs>();
			AgrImgs agrImgs  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrImgs = new AgrImgs();
				agrImgs.id = obj.optInt("id");
				if (obj.opt("agrInfoId")!=null) {
					agrImgs.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("imageURLContent")!=null) {
					System.out.println(obj.getString("imageURLContent"));
					agrImgs.URLcontent = obj.getString("imageURLContent");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						agrImgs.descript="";
					}
					else {
						agrImgs.descript = obj.getString("descript");
					}
				}
				
				list.add(agrImgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 获取评论
	 * 包括评论用户名，被评论的用户名
	 * @param agrInfoId
	 * @return
	 */
	@Deprecated
	public List<AgrinfoComment> getAgrImgComments(int agrInfoId){
		List<AgrinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCropImgComment\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrinfoComment>();
			AgrinfoComment agrinfoComment = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrinfoComment = new AgrinfoComment();
				if (obj.opt("id")!=null) {
					agrinfoComment.commentId = obj.optInt("id");
				}
				if(obj.opt("agrInfoId")!=null){
					agrinfoComment.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("comment")!=null) {
					agrinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("parentId")!=null) {
					agrinfoComment.parentId = obj.optInt("parentId");
					if (agrinfoComment.parentId!=-1) {
						//先获取用户ID
						int userId = getUserId(agrinfoComment.parentId);
						//在获取用户名
						agrinfoComment.parentusername = UserDao.getInstance().getUserName(userId);
					}
				}
				if(obj.opt("userId")!=null){
					agrinfoComment.userId = obj.optInt("userId");
					agrinfoComment.username = UserDao.getInstance().getUserName(agrinfoComment.userId);
				}
				if (obj.opt("commentTime")!=null) {
					agrinfoComment.commentTime = obj.getString("commentTime");
				}
				
				list.add(agrinfoComment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 根据评论id获取评论信息
	 * @param agrInfoId
	 * @return
	 */
	public AgrinfoComment getAgrComment(int commentId){
		AgrinfoComment agrinfoComment = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getcomment\",\"CustomParams\":{\"commentId\":"+commentId+"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrinfoComment = new AgrinfoComment();
				if (obj.opt("id")!=null) {
					agrinfoComment.commentId = obj.optInt("id");
				}
				if(obj.opt("agrInfoId")!=null){
					agrinfoComment.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("comment")!=null) {
					agrinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("parentId")!=null) {
					agrinfoComment.parentId = obj.optInt("parentId");
				}
				
				if (obj.opt("parentusername")!=null) {
					agrinfoComment.parentusername = obj.getString("parentusername");
				}
				
				if(obj.opt("userId")!=null){
					agrinfoComment.userId = obj.optInt("userId");
				}
				
				if (obj.opt("username")!=null) {
					agrinfoComment.username = obj.getString("username");
				}
				
				if (obj.opt("commentTime")!=null) {
					agrinfoComment.commentTime = obj.getString("commentTime");
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return agrinfoComment;
	}
	
	
	/**
	 * 根据评论ID获取评论的用户ID
	 * @param commentId
	 * @return
	 */
	public int getUserId(int commentId){
		int  userId = -1;
		HashMap<String, String> params = new HashMap<String, String>();
		String param = "{\"Function\":\"crop.getuserId\",\"CustomParams\":{\"commentId\":"+commentId+"},\"Type\":2}";
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
	 * 根据 用户ID 获取 作物定制列表
	 * @param commentId
	 * @return
	 */
	public List<CropType> getCropByUserId(int userId){
		List<CropType> list = null;
		HashMap<String, String> params = new HashMap<String, String>();
		String param = "{\"Function\":\"crop.getuserId\",\"CustomParams\":{\"userId\":"+userId+"},\"Type\":2}";
		params.put("param",  param);
		try {
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			CropType cropType = null;
			list = new ArrayList<CropType>();
			for (int i = 0; i < array.length(); i++) {
				cropType = new CropType();
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("id")!=null) {
					cropType.id = obj.optInt("id");
				}
				if (obj.opt("name")!=null) {
					cropType.name = obj.getString("name");
				}
				if (obj.opt("parentId")!=null) {
					cropType.parentId = obj.optInt("parentId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}	
	
	@Deprecated
	public List<AgrinfoComment> getAgrComments(String commentTime){
		List<AgrinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCropImgCommentByTime\",\"CustomParams\":{\"commentTime\":\""+commentTime+"\"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrinfoComment>();
			AgrinfoComment agrinfoComment = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrinfoComment = new AgrinfoComment();
				if (obj.opt("id")!=null) {
					agrinfoComment.commentId = obj.optInt("id");
				}
				if(obj.opt("agrInfoId")!=null){
					agrinfoComment.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("comment")!=null) {
					agrinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("parentId")!=null) {
					agrinfoComment.parentId = obj.optInt("parentId");
					if (agrinfoComment.parentId!=-1) {
						//先获取用户ID
						int userId = getUserId(agrinfoComment.parentId);
						//在获取用户名
						agrinfoComment.parentusername = UserDao.getInstance().getUserName(userId);
					}
				}
				if(obj.opt("userId")!=null){
					agrinfoComment.userId = obj.optInt("userId");
					agrinfoComment.username =UserDao.getInstance().getUserName(agrinfoComment.userId);
				}
				if (obj.opt("commentTime")!=null) {
					agrinfoComment.commentTime = obj.getString("commentTime");
				}
				
				list.add(agrinfoComment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	@Deprecated
	public List<AgrPraise> getAgrPraise(int agrInfoId){
		List<AgrPraise> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCropPraise\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrPraise>();
			AgrPraise agrPraise = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrPraise = new AgrPraise();
				if (obj.opt("id")!=null) {
					agrPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("agrInfoId")!=null) {
					agrPraise.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("isPraise")!=null) {
					agrPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					agrPraise.userId = obj.optInt("userId");
					agrPraise.userName = UserDao.getInstance().getUserName(agrPraise.userId);
				}
				if (obj.opt("praiseTime")!=null) {
					agrPraise.praiseTime = obj.optString("praiseTime");
				}
				list.add(agrPraise);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 根据赞id获取赞信息
	 * @param id
	 * @return
	 */
	public AgrPraise getAgrPraisebyid(int id){
		AgrPraise agrPraise = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getpraise\",\"CustomParams\":{\"id\":"+id+"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrPraise = new AgrPraise();
				if (obj.opt("id")!=null) {
					agrPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("agrInfoId")!=null) {
					agrPraise.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("isPraise")!=null) {
					agrPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					agrPraise.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					agrPraise.userName = obj.getString("username");
				}
				if (obj.opt("praiseTime")!=null) {
					agrPraise.praiseTime = obj.optString("praiseTime");
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return agrPraise;
	}
	@Deprecated
	public List<AgrPraise> getAgrPraise(String praiseTime){
		List<AgrPraise> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			String param = "{\"Function\":\"crop.getCropPraiseByTime\",\"CustomParams\":{\"praiseTime\":\""+praiseTime+"\"},\"Type\":2}";
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrPraise>();
			AgrPraise agrPraise = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrPraise = new AgrPraise();
				if (obj.opt("id")!=null) {
					agrPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("agrInfoId")!=null) {
					agrPraise.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("isPraise")!=null) {
					agrPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					agrPraise.userId = obj.optInt("userId");
					agrPraise.userName = UserDao.getInstance().getUserName(agrPraise.userId);
				}
				if (obj.opt("praiseTime")!=null) {
					agrPraise.praiseTime = obj.optString("praiseTime");
				}
				list.add(agrPraise);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	@Deprecated
	public List<AgrInfo> getAgrInfos(JSONArray array){
		
		List<AgrInfo> list = null;
		list = new ArrayList<AgrInfo>();
		AgrInfo agrInfo = null;
		try {
			
			for (int i = 0; i < array.length(); i++) {
				agrInfo= new AgrInfo();
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("agrInfoId")!=null) {
					agrInfo.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("userId")!=null) {
					agrInfo.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					agrInfo.userName = obj.getString("username");
				}
				if (obj.opt("HeadImage")!=null) {
					agrInfo.userHeaderFile = obj.getString("HeadImage");
				}
				if (obj.opt("uploadTime")!=null) {
					agrInfo.uploadTime = obj.getString("uploadTime");
				}
				if (obj.opt("areacode")!=null) {
					agrInfo.areacode = obj.getString("areacode");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						agrInfo.descript="";
					}
					else {
						agrInfo.descript = obj.getString("descript");
					}
				}
				//作物类型
				if (obj.opt("type")!=null) {//有没有这个字段
					if (obj.getString("type").equals("null")) {//字段是否为null
						agrInfo.croptype="";
					}
					else {
						int type = obj.getInt("type");
						if (type==0) {
							agrInfo.croptype="";
						}
						else {
							agrInfo.croptype = new CropTypeDao().getCropTypeName(type);
						}
					}
				}
				//作物品种
				if (obj.opt("breed")!=null) {//有没有这个字段
					if (obj.getString("breed").equals("null")) {//字段是否为null
						agrInfo.breed="";
					}
					else {
						int breed = obj.getInt("breed");
						if (breed==0) {
							agrInfo.breed="";
						}
						else {
							agrInfo.breed = new CropTypeDao().getCropTypeName(breed);
						}
					}
				}
				
				//作物发育期
				if (obj.opt("growthperiod")!=null) {//有没有这个字段
					if (obj.getString("growthperiod").equals("null")) {//字段是否为null
						agrInfo.growperiod="";
					}
					else {
						int growthperiod = obj.getInt("growthperiod");
						if (growthperiod==0) {
							agrInfo.growperiod="";
						}
						else {
							agrInfo.growperiod = new CropPeriodDao().getCropGrowPeriod(growthperiod);
						}
					}
				}
				//获取农情动态的时候，把图片，评论和赞一起获取
				agrInfo.mImgs = getAgrImgs(agrInfo.agrInfoId);
				agrInfo.mComments = getAgrImgComments(agrInfo.agrInfoId);
				agrInfo.mAgrPraises = getAgrPraise(agrInfo.agrInfoId);
				
				list.add(agrInfo);
			}
			
		} catch (Exception e) {
		}
		
		return list;
	}
	
	public List<AgrInfo> getAgrInfosNew(JSONArray array){
		
		List<AgrInfo> list = null;
		list = new ArrayList<AgrInfo>();
		AgrInfo agrInfo = null;
		try {
			
			for (int i = 0; i < array.length(); i++) {
				agrInfo= new AgrInfo();
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("agrInfoId")!=null) {
					agrInfo.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("userId")!=null) {
					agrInfo.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					agrInfo.userName = obj.getString("username");
				}
				if (obj.opt("HeadImage")!=null) {
					agrInfo.userHeaderFile = obj.getString("HeadImage");
				}
				if (obj.opt("uploadTime")!=null) {
					agrInfo.uploadTime = obj.getString("uploadTime");
				}
				if (obj.opt("areacode")!=null) {
					agrInfo.areacode = obj.getString("areacode");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						agrInfo.descript="";
					}
					else {
						agrInfo.descript = obj.getString("descript");
					}
				}
				//作物类型
				if (obj.opt("type")!=null) {//有没有这个字段
					if (obj.getString("type").equals("null")) {//字段是否为null
						agrInfo.croptypeId=0;
					}
					else {
						agrInfo.croptypeId=obj.getInt("type");
					}
				}
				
				//作物品种
				if (obj.opt("breed")!=null) {//有没有这个字段
					if (obj.getString("breed").equals("null")) {//字段是否为null
						agrInfo.breedId=0;
					}
					else {
						agrInfo.breedId = obj.getInt("breed");
					}
				}
				
				//作物发育期
				if (obj.opt("growthperiod")!=null) {//有没有这个字段
					if (obj.getString("growthperiod").equals("null")) {//字段是否为null
						agrInfo.growperiodId=0;
					}
					else {
						agrInfo.growperiodId = obj.getInt("growthperiod");
					}
				}
				
				
				if (obj.opt("typename")!=null) {//有没有这个字段
					if (obj.getString("typename").equals("null")) {//字段是否为null
						agrInfo.croptype=obj.getString("breedname");
						agrInfo.breed="";
					}
					else {
						agrInfo.croptype=obj.getString("typename");
						agrInfo.breed = obj.getString("breedname");
					}
					
				}
				if (obj.opt("growthperiodname")!=null) {//有没有这个字段
					agrInfo.growperiod = obj.getString("growthperiodname");
				}
				
				
				
				agrInfo.mImgs = new ArrayList<AgrImgs>();
				agrInfo.mComments = new ArrayList<AgrinfoComment>();
				agrInfo.mAgrPraises = new  ArrayList<AgrPraise>();
				list.add(agrInfo);
			}
			
		} catch (Exception e) {
		}
		
		if ((list!=null)&&(list.size()>0)) {
			//获取农情动态的时候，把图片，评论和赞一起获取
			long old  =0;
			long now  =0; 
			
			old=System.currentTimeMillis();
			getAgrImgsNew(list);
			now =System.currentTimeMillis();
			System.out.println("图片加载耗时 "+(now-old)/(float)1000+"秒");
			
			old=System.currentTimeMillis();
			getAgrPraiseNew(list);
			now =System.currentTimeMillis();
			System.out.println("赞加载耗时 "+(now-old)/(float)1000+"秒");
			
			old=System.currentTimeMillis();
			getAgrImgCommentsNew(list);
			now =System.currentTimeMillis();
			System.out.println("评论载耗时 "+(now-old)/(float)1000+"秒");
		}
		return list;
	}

	private String getParams(List<AgrInfo> agrInfos,String function){
		JSONObject param = new JSONObject();
		try {
			param.put("Function", function);
			param.put("Type", 3);
			param.put("CustomParams", getCustomParams(agrInfos));
		} catch (JSONException e) {
		}
		return param.toString();
	}
	
	private String getCustomParams(List<AgrInfo> agrInfos){
		JSONArray customParams = new JSONArray();
		try {
			for (AgrInfo agrInfo :agrInfos) {
				JSONObject obj = new JSONObject();
				obj.put("agrInfoId", agrInfo.agrInfoId);
				customParams.put(obj);
			}
		} catch (Exception e) {
		}
		return customParams.toString();
	}
	
	public Boolean getAgrImgsNew(List<AgrInfo> agrInfos){
		List<AgrImgs> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"crop.getCropImg\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			String param  =  getParams(agrInfos, "crop.getCropImg");
			params.put("param", param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrImgs>();
			AgrImgs agrImgs  = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrImgs = new AgrImgs();
				agrImgs.id = obj.optInt("id");
				if (obj.opt("agrInfoId")!=null) {
					agrImgs.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("imageURLContent")!=null) {
					agrImgs.URLcontent = obj.getString("imageURLContent");
				}
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						agrImgs.descript="";
					}
					else {
						agrImgs.descript = obj.getString("descript");
					}
				}
				
				for(AgrInfo agrInfo:agrInfos){
					if (agrImgs.agrInfoId==agrInfo.agrInfoId) {
						agrInfo.mImgs.add(agrImgs);
						break;
					}
				}
				
				list.add(agrImgs);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean getAgrPraiseNew(List<AgrInfo> agrInfos){
		List<AgrPraise> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"crop.getCropPraiseByTime\",\"CustomParams\":{\"praiseTime\":\""+praiseTime+"\"},\"Type\":2}";
			
			String param = getParams(agrInfos,"crop.getCropPraise");
			params.put("param",  param);
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrPraise>();
			AgrPraise agrPraise = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrPraise = new AgrPraise();
				if (obj.opt("id")!=null) {
					agrPraise.praiseId = obj.optInt("id");
				}
				if (obj.opt("agrInfoId")!=null) {
					agrPraise.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("isPraise")!=null) {
					agrPraise.isPraise = obj.optInt("isPraise");
				}
				if (obj.opt("userId")!=null) {
					agrPraise.userId = obj.optInt("userId");
				}
				if (obj.opt("username")!=null) {
					agrPraise.userName = obj.optString("username");
				}
				if (obj.opt("praiseTime")!=null) {
					agrPraise.praiseTime = obj.optString("praiseTime");
				}
				list.add(agrPraise);
				
				for(AgrInfo agrInfo:agrInfos){
					if (agrPraise.agrInfoId==agrInfo.agrInfoId) {
						agrInfo.mAgrPraises.add(agrPraise);
						break;
					}
				}
				
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean getAgrImgCommentsNew(List<AgrInfo> agrInfos){
		List<AgrinfoComment> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
//			String param = "{\"Function\":\"crop.getCropImgComment\",\"CustomParams\":{\"agrInfoId\":"+agrInfoId+"},\"Type\":2}";
			params.put("param", getParams(agrInfos, "crop.getCropImgComment"));
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<AgrinfoComment>();
			AgrinfoComment agrinfoComment = null;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrinfoComment = new AgrinfoComment();
				if (obj.opt("id")!=null) {
					agrinfoComment.commentId = obj.optInt("id");
				}
				if(obj.opt("agrInfoId")!=null){
					agrinfoComment.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("comment")!=null) {
					agrinfoComment.comment = obj.getString("comment");
				}
				if (obj.opt("parentId")!=null) {
					agrinfoComment.parentId = obj.optInt("parentId");
				}
				
				if (obj.opt("parentusername")!=null) {
					agrinfoComment.parentusername = obj.getString("parentusername");
				}
				
				if(obj.opt("userId")!=null){
					agrinfoComment.userId = obj.optInt("userId");
				}
				
				if (obj.opt("username")!=null) {
					agrinfoComment.username = obj.getString("username");
				}
				
				if (obj.opt("commentTime")!=null) {
					agrinfoComment.commentTime = obj.getString("commentTime");
				}
				
				list.add(agrinfoComment);
				for(AgrInfo agrInfo:agrInfos){
					if (agrinfoComment.agrInfoId==agrInfo.agrInfoId) {
						agrInfo.mComments.add(agrinfoComment);
						break;
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public void getCropTypeInfoInCurrentTime(List<CropType> cropTypes ,List<CropPeriod> cropPeriods){
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				String param = "{\"Function\":\"crop.getCopesInTime\",\"CustomParams\":{},\"Type\":2}";
				params.put("param", param);
				JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
				CropType cropType = null;
				CropPeriod period = null;
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					cropType = new CropType();
					cropType.id = obj.getInt("type");
					cropType.name = obj.getString("typename");
					cropType.parentId = 0;
					if (!isExistCropType(cropTypes, cropType)) {
						cropTypes.add(cropType);	
					}
					period = new CropPeriod();
					period.id = obj.getInt("growthperiod");
					period.growthperiod = obj.getString("growthperiodname");
					period.cropsType = obj.getInt("type");
					cropPeriods.add(period);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private boolean isExistCropType(List<CropType> list,CropType cropType){
		for(CropType mCropType: list){
			if (cropType.id==mCropType.id) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 *  按用户id查询用户个人农情 
	 */
	public List<AgrInfo> getAgrInfosByuserId(int userid) {
		List<AgrInfo> list = null;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject paramsjObject = new JSONObject();
			JSONObject customJObject = new JSONObject();
//			customJObject.put("num", (pageIndex-1)*pageSize);
			customJObject.put("userId", userid);
//			customJObject.put("typeId", typeId);
//			customJObject.put("pagesize", pageSize);
			
			paramsjObject.put("Function", "agrinfo.getAllByUserId.index");
			paramsjObject.put("CustomParams", customJObject.toString());
			paramsjObject.put("Type", 2);
			
			String param = paramsjObject.toString();
			params.put("param",  param);
			ElapseTime cropsElapseTime = new ElapseTime("农情信息");
			cropsElapseTime.start();
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			cropsElapseTime.end();
			list = getAgrInfosNew(array);
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	
}
