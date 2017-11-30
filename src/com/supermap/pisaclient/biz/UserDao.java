/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @UserDao.java - 2014-5-30 上午9:30:22
 */

package com.supermap.pisaclient.biz;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.entity.SendMessageResult;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.http.HttpHelper;

public class UserDao {

	public static UserDao uDao;

	private UserDao() {

	}

	public static UserDao getInstance() {
		if (uDao == null)
			uDao = new UserDao();
		return uDao;
	}

	public void logout() {
		save(null, true);
	}

	public boolean save(User user, boolean isSave) {
		Constant.mLocalUser = user;
		if (isSave) {
			try {
				CommonUtil.Serialize(CommonUtil.SERIALIZE_PATH_USER, user);
			} catch (Exception e) {

			}
		}
		return true;
	}

	public User get() {
		try {
			if (Constant.mLocalUser != null) {
				return Constant.mLocalUser;
			}
			return (User) CommonUtil.DeSerialize(CommonUtil.SERIALIZE_PATH_USER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SendMessageResult findPassword(String loginName, String tel) {
		SendMessageResult result = new SendMessageResult();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"loginName\":\"" + loginName + "\", \"mobile\":\"" + tel + "\"}");
		try {
			JSONObject res = HttpHelper.loadOne(HttpHelper.FIND_PASSWORD, params);
			System.out.println(res.toString());
			result.isSuccess = res.getInt("code") == 1;
			result.msg = res.getString("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isExists(String loginName) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"loginName\":\"" + loginName + "\"}");
		try {
			String res = HttpHelper.loadString(HttpHelper.USER_IS_EXISTS, params);
			return "true".equals(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public User addUser(User user, File file) {
		if (file != null) {
			user.headImage = HttpHelper.loadPic(file, HttpHelper.ADD_PIC_URL);
		}
		HashMap<String, String> params = new HashMap<String, String>();
		if("null".equals(user.headImage) || user.headImage == null){
			user.headImage = "/pisa/images/indexSam/jcff1.jpg";
		}
		params.put("para", "{\"userName\":\"" + user.userName + "\",\"password\":\"" + user.password
				+ "\",\"address\":\"\",\"email\":\"\",\"loginName\":\"" + user.loginName + "\",\"mobile\":\"" + user.mobile
				+ "\",\"introduction\":\"\",\"showName\":\"" + user.userName + "\",\"headImage\":\"" + user.headImage + "\", \"areaCode\":\""
				+ user.areaCode + "\"}");
		try {
			String result = HttpHelper.loadString(HttpHelper.USER_ADD_URL, params);
			JSONObject obj = new JSONObject(result);
			String rs = obj.getString("success");
			if ("true".equals(rs)) {
				user.id = obj.getInt("userId");
				user.isExpert = false;
				return user;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bitmap getValidate(boolean isbig) {
		HashMap<String, String> params = new HashMap<String, String>();
		JSONObject jsonObject = new JSONObject();
		int imageWidth;
		int imageHeight;
		if (isbig) {
			imageHeight = 200;
			imageWidth =60;
		}
		else {
			imageHeight =100;
			imageWidth =30;
		}
		try {
			jsonObject.put("imageWidth", imageHeight);
			jsonObject.put("imageHeight", imageWidth);
			jsonObject.put("codeCount", 4);
			jsonObject.put("lineCount", 10);
			params.put("para", jsonObject.toString());
//			params.put("para", "{\"imageWidth\":100, \"imageHeight\":30, \"codeCount\":4, \"lineCount\":10}");
			byte[] img = HttpHelper.getByteData(HttpHelper.GETAUTHID, HttpHelper.METHOD_POST, params);
			return BitmapFactory.decodeByteArray(img, 0, img.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isServerValid(String code) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"code\":\"" + code + "\"}");
		try {
			String res = HttpHelper.loadString(HttpHelper.AUTH_VALID, params);
			return "true".equals(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean login(User user, boolean isSave) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"loginName\":\"" + user.loginName + "\", \"password\":\"" + user.password + "\"}");
		try {
			JSONObject obj = HttpHelper.loadOne(HttpHelper.USER_LOGIN, params, HttpHelper.METHOD_POST);
			if (obj != null) {
				if (obj != null) {
					if (obj.opt("id") != null) {
						user.id = obj.getInt("id");
					}
					if (obj.opt("username") != null) {
						user.userName = obj.getString("username");
					}
					if (obj.opt("loginName") != null) {
						user.loginName = obj.getString("loginName");
					}
					if (obj.opt("showName") != null) {
						user.showName = obj.getString("showName");
					}
					if (obj.opt("mobile") != null) {
						user.mobile = obj.getString("mobile");
					}
					if (obj.opt("address") != null) {
						user.address = obj.getString("address");
					}
					if (obj.opt("email") != null) {
						user.email = obj.getString("email");
					}
					if (obj.opt("headImage") != null) {
						user.headImage = obj.getString("headImage");
					}
					if (obj.opt("introduction") != null) {
						user.introduction = obj.getString("introduction");
					}
					if (obj.opt("areaCode") != null) {
						user.areaCode = obj.getString("areaCode");
					}
					/*if (obj.opt("isexport") != null) {
						user.isExport = obj.getBoolean("isexport");
					}*/
					if (obj.opt("role") != null) {
						user.isExpert = (obj.getString("role").equals("EXPERT"))?true:false;
					}
					if (obj.opt("roleid") != null) {
						user.roleid = obj.getInt("roleid");
						if (user.roleid != 0) {
							user.isVip = true;
						}
					}
				}
//				if (getExpertIdByUserId(user.id) == -1) {
//					user.isExport = false;
//				} else {
//					user.isExport = true;
//				}
				user.isSave = isSave;
				save(user, isSave);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public User searchById(int id) {
		User user = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param", "{\"Function\":\"user.detail.by.id\",\"CustomParams\":{\"id\":" + id + "},\"Type\":2}");
		try {
			JSONArray array = HttpHelper.load(HttpHelper.FUNCRION_QUERY, params, HttpHelper.METHOD_POST);
			if (array != null) {
				user = new User();
				JSONObject obj = array.getJSONObject(0);
				if (obj.opt("id") != null) {
					user.id = obj.getInt("id");
				}
				if (obj.opt("username") != null) {
					user.userName = obj.getString("username");
				}
				if (obj.opt("loginName") != null) {
					user.loginName = obj.getString("loginName");
				}
				if (obj.opt("showName") != null) {
					user.showName = obj.getString("showName");
				}
				if (obj.opt("mobile") != null) {
					user.mobile = obj.getString("mobile");
				}
				if (obj.opt("address") != null) {
					user.address = obj.getString("address");
				}
				if (obj.opt("Email") != null) {
					user.email = obj.getString("Email");
				}
				if (obj.opt("HeadImage") != null) {
					user.headImage = obj.getString("HeadImage");
				}
				if (obj.opt("Introduction") != null) {
					user.introduction = obj.getString("Introduction");
				}
				if (obj.opt("areaCode") != null) {
					user.areaCode = obj.getString("areaCode");
				}
				/*if (obj.opt("isexport") != null) {
					user.isExport = obj.getBoolean("isexport");
				}*/
				if (obj.opt("role") != null) {
					user.isExpert = obj.getString("role").equals("EXPERT")?true:false;
					//user.isExport = obj.getBoolean("role");
				}
				if (obj.opt("roleid") != null) {
					user.roleid = obj.getInt("roleid");
					if (user.roleid != 0) {
						user.isVip = true;
					}
				}
				user.isSave = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean updateUser(User user, File file) {
		if (file != null) {
			user.headImage = HttpHelper.loadPic(file, HttpHelper.ADD_PIC_URL);
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"userId\":" + user.id + ",\"userName\":\"" + user.userName
				+ "\",\"address\":\"重庆\",\"email\":\"\",\"loginname\":\"" + user.loginName + "\",\"mobile\":\"" + user.mobile
				+ "\",\"introduction\":\"rerewre\",\"showName\":\"" + user.userName + "\",\"departid\":\"2\", \"headImage\":\""
				+ user.headImage + "\", \"areaCode\":\"" + user.areaCode + "\"}");

		try {
			HttpHelper.loadString(HttpHelper.USER_UPDATE_URL, params, HttpHelper.METHOD_POST);
			save(user, user.isSave);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updatePassword(int id, String oldPwd, String newPwd) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("para", "{\"userId\":" + id + ",\"originalPassword\":\"" + oldPwd + "\",\"newPassword\":\"" + newPwd + "\"}");

		try {
			HttpHelper.loadString(HttpHelper.USER_UPDATE_PWD_URL, params, HttpHelper.METHOD_POST);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 根据用户ID获取用户名
	 * @param userId
	 * @return
	 */
	public String getUserName(int userId) {
		String name = "";
		HashMap<String, String> params = new HashMap<String, String>();
		String param = "{\"Function\":\"crop.username\",\"CustomParams\":{\"userId\":" + userId + "},\"Type\":2}";
		params.put("param", param);
		try {
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("showName") != null) {
					if (obj.getString("showName").equals("null")) {
						name = "";
					} else {
						name = obj.getString("showName");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}

	/**
	 * 根据专家ID获取用户ID
	 * @param commentId
	 * @return
	 */
	public int getUserIdByExpertId(int expertid) {
		int userId = -1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("expertid", expertid);
			jsonParam.put("Function", "adv.getUserIdByExpertId");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param", jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("userId") != null) {
					if (obj.getInt("userId") != -1) {
						userId = obj.getInt("userId");
					} else {
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
	 * 根据用户ID获取专家ID
	 * @param commentId
	 * @return
	 */
	public int getExpertIdByUserId(int userId) {
		int expertId = -1;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("userId", userId);
			jsonParam.put("Function", "adv.getUserIsExpert");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param", jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("expertId") != null) {
					if (obj.getInt("expertId") != -1) {
						expertId = obj.getInt("expertId");
					} else {
						expertId = -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return expertId;
	}

	/**
	 * 根据专家id查询是什么专家
	 * @param commentId
	 * @return
	 */
	public List<String> getExpertSubject(int expertId) {

		List<String> list;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			JSONObject jsonParam = new JSONObject();
			JSONObject customParams = new JSONObject();
			customParams.put("expertId", expertId);
			jsonParam.put("Function", "user.get.expert.subject");
			jsonParam.put("Type", 2);
			jsonParam.put("CustomParams", customParams.toString());
			params.put("param", jsonParam.toString());
			JSONArray array = HttpHelper.load(HttpHelper.DATA_QUERY_URL, params, HttpHelper.METHOD_POST);
			list = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj.opt("name") != null) {
					list.add(obj.getString("name"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	
	
}
