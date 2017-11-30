package com.supermap.pisaclient.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.pisaclient.common.FileHelper;
import com.supermap.pisaclient.common.FileManager;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;

public class CropsCache {
	
	public boolean saveCrops(List<AgrInfo> agrInfos,int userID){
		JSONArray cropList = new JSONArray();
		AgrInfo agrInfo = null;
		JSONObject object = null;
		for (int i = 0; i < agrInfos.size(); i++) {
			agrInfo = agrInfos.get(i);
			object = new JSONObject();
			try {
				object.put("agrInfoId", agrInfo.agrInfoId);
				object.put("userId", agrInfo.userId);
				object.put("userName", agrInfo.userName);
				object.put("userHeaderFile", agrInfo.userHeaderFile);
				object.put("uploadTime", agrInfo.uploadTime);
				object.put("areacode", agrInfo.areacode);
				object.put("descript", agrInfo.descript);
				object.put("croptype", agrInfo.croptype);
				object.put("breed", agrInfo.breed);
				object.put("growperiod", agrInfo.growperiod);
				//添加图片
				if ((agrInfo.mImgs!=null)&&(agrInfo.mImgs.size()>0)) {
					JSONArray jsonImgs = new JSONArray();
					for(AgrImgs agrImg :agrInfo.mImgs){
						JSONObject img = new JSONObject();
						img.put("id", agrImg.id);
						img.put("agrInfoId", agrImg.agrInfoId);
						img.put("URLcontent", agrImg.URLcontent);
						img.put("descript", agrImg.descript);
						jsonImgs.put(img);
					}
					object.put("mImgs", jsonImgs);
				}
				
				//添加评论
				if((agrInfo.mComments!=null)&&(agrInfo.mComments.size()>0)){
					JSONArray jsonComments = new JSONArray();
					for(AgrinfoComment agrcomment :agrInfo.mComments){
						JSONObject comment = new JSONObject();
						comment.put("commentId", agrcomment.commentId);
						comment.put("agrInfoId", agrcomment.agrInfoId);
						comment.put("comment", agrcomment.comment);
						comment.put("parentId", agrcomment.parentId);
						comment.put("userId", agrcomment.userId);
						comment.put("username", agrcomment.username);
						comment.put("parentusername", agrcomment.parentusername);
						comment.put("commentTime", agrcomment.commentTime);
						jsonComments.put(comment);
					}
					object.put("mComments", jsonComments);
				}
				
				//添加赞
				if ((agrInfo.mAgrPraises!=null)&&(agrInfo.mAgrPraises.size()>0)) {
					JSONArray jsonPraise = new JSONArray();
					for(AgrPraise agrPraise : agrInfo.mAgrPraises){
						JSONObject praise = new JSONObject();
						praise.put("praiseId", agrPraise.praiseId);
						praise.put("agrInfoId", agrPraise.agrInfoId);
						praise.put("userId", agrPraise.userId);
						praise.put("userName", agrPraise.userName);
						praise.put("praiseTime", agrPraise.praiseTime);
						praise.put("isPraise", agrPraise.isPraise);
						jsonPraise.put(praise);
					}
					object.put("mAgrPraises", jsonPraise);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cropList.put(object);
		}
		String file = FileManager.getSaveCropsCacheDir() + userID + ".crops" ;
		FileHelper.writeFile(file, cropList.toString());
		System.out.println("农情动态数据缓存成功");
		
		return true;
	}
	
	
	public List<AgrInfo> getCopsList(int userID){
		List<AgrInfo> crops = new ArrayList<AgrInfo>();
		String cropspath = FileManager.getSaveCropsCacheDir() + userID + ".crops" ;
		String strFarms = null;
		try {
			strFarms = FileHelper.readTextFile(cropspath);
			JSONArray array = new JSONArray(strFarms);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				AgrInfo agrInfo = new AgrInfo();
				if (obj.opt("agrInfoId")!=null) {
					agrInfo.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("userId")!=null) {
					agrInfo.userId = obj.optInt("userId");
				}
				if (obj.opt("userName")!=null) {
					agrInfo.userName = obj.getString("userName");
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
					agrInfo.descript = obj.getString("descript");
				}
				
				if (obj.opt("croptype")!=null) {
					agrInfo.croptype = obj.getString("croptype");
				}
				//作物品种
				if (obj.opt("breed")!=null) {
					agrInfo.breed = obj.getString("breed");
				}
				//作物发育期
				if (obj.opt("growperiod")!=null) {//有没有这个字段
					agrInfo.growperiod = obj.getString("growperiod");
				}
				
				if (obj.opt("mImgs")!=null) {
					JSONArray mImgs = new JSONArray(obj.getString("mImgs"));
					agrInfo.mImgs = getAgrImgs(mImgs);
				}
				
				if (obj.opt("mAgrPraises")!=null) {
					JSONArray mPraises = new JSONArray(obj.getString("mAgrPraises"));
					agrInfo.mAgrPraises = getAgrPraises(mPraises);
				}
				
				if (obj.opt("mComments")!=null) {
					JSONArray mComments = new JSONArray(obj.getString("mComments"));
					agrInfo.mComments = getAgrinfoComments(mComments);
				}
				crops.add(agrInfo);
			}
			
			
		} catch (IOException e) {
		} catch (JSONException e) {
		}
		
		return crops;
	}
	
	public List<AgrImgs> getAgrImgs(JSONArray array){
		List<AgrImgs> agrImgs = new ArrayList<AgrImgs>();
		AgrImgs agrImg  = null;
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				agrImg = new AgrImgs();
				agrImg.id = obj.optInt("id");
				if (obj.opt("agrInfoId")!=null) {
					agrImg.agrInfoId = obj.optInt("agrInfoId");
				}
				if (obj.opt("URLcontent")!=null) {
					agrImg.URLcontent = obj.getString("URLcontent");
				}
				
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						agrImg.descript="";
					}
					else {
						agrImg.descript = obj.getString("descript");
					}
				}
				agrImgs.add(agrImg);
			}
		} catch (Exception e) {
		}
		return agrImgs;
	}
	
	public List<AgrinfoComment> getAgrinfoComments(JSONArray array){
		List<AgrinfoComment> comments = new ArrayList<AgrinfoComment>();
		AgrinfoComment agrinfoComment  = null;
		try {
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
				
				if(obj.opt("userId")!=null){
					agrinfoComment.userId = obj.optInt("userId");
				}
				
				if (obj.opt("commentTime")!=null) {
					agrinfoComment.commentTime = obj.getString("commentTime");
				}
				
				if (obj.opt("username")!=null) {
					agrinfoComment.username = obj.getString("username");
				}
				
				if (obj.opt("parentusername")!=null) {
					agrinfoComment.parentusername = obj.getString("parentusername");
				}
				comments.add(agrinfoComment);
			}
		} catch (Exception e) {
		}

		return comments;
	}
	
	public List<AgrPraise> getAgrPraises(JSONArray array){
		List<AgrPraise> praises = new ArrayList<AgrPraise>();
		AgrPraise agrPraise = null;
		try {
			for(int i= 0;i<array.length();i++){
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
				if (obj.opt("userName")!=null) {
					agrPraise.userName = obj.optString("userName");
				}
				if (obj.opt("praiseTime")!=null) {
					agrPraise.praiseTime = obj.optString("praiseTime");
				}
				praises.add(agrPraise);
			}
		} catch (Exception e) {
		}
		
		return praises;
	}
	

}
