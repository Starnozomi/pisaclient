package com.supermap.pisaclient.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.FileHelper;
import com.supermap.pisaclient.common.FileManager;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;

public class AdvCache {
	
	public String getFilePaht(int type,int userId,String serchrContent){
		String filePath = null;
		if(type==Constant.ADV_TYPE_MY){
			filePath = FileManager.getSaveAdvsCacheDir() +type+""+userId + ".advs" ;
		}
		else if(type==Constant.ADV_TYPE_SERCH){
			filePath = FileManager.getSaveAdvsCacheDir() +type+serchrContent+".advs" ;
		}
		else {
			filePath = FileManager.getSaveAdvsCacheDir() +type+".advs" ;
		}
		return filePath;
	}
	
	public boolean saveAdvs(List<AdvisoryInfo> advInfos ,int type,int userId,String serchrContent){
		saveAdvs(advInfos, getFilePaht(type, userId, serchrContent));
		return true;
	}
	
	public boolean saveAdvs(List<AdvisoryInfo> advInfos ,String filePath){
		
		JSONArray advArray = new JSONArray();
		JSONObject obj = null;
		for (AdvisoryInfo advisoryInfo : advInfos) {
			obj = new JSONObject();
			try {
				obj.put("advInfoId", advisoryInfo.advInfoId);
				obj.put("userId", advisoryInfo.userId);
				obj.put("userName", advisoryInfo.userName);
				obj.put("userHeaderFile", advisoryInfo.userHeaderFile);
				obj.put("uploadTime", advisoryInfo.uploadTime);
				obj.put("areacode", advisoryInfo.areacode);
				obj.put("qestion", advisoryInfo.qestion);
				obj.put("subjectName", advisoryInfo.subjectName);
				if ((advisoryInfo.mImgs!=null)&&(advisoryInfo.mImgs.size()>0)) {
					JSONArray mImgArray = new JSONArray();
					for (AdvImgs advImg : advisoryInfo.mImgs) {
						JSONObject img = new JSONObject();
						img.put("id", advImg.id);
						img.put("advInfoId", advImg.advInfoId);
						img.put("URLcontent", advImg.URLcontent);
						img.put("descript", advImg.descript);
						mImgArray.put(img);
					}
					obj.put("mImgs", mImgArray.toString());
				}
				
				if ((advisoryInfo.mComments!=null)&&(advisoryInfo.mComments.size()>0)) {
					JSONArray mCommentArray = new JSONArray();
					for (AdvinfoComment advComment : advisoryInfo.mComments) {
						JSONObject comment = new JSONObject();
						comment.put("commentId", advComment.commentId);
						comment.put("advInfoId", advComment.advInfoId);
						comment.put("comment", advComment.comment);
						comment.put("parentId", advComment.parentId);
						comment.put("userId", advComment.userId);
						comment.put("commentTime", advComment.commentTime);
						comment.put("username", advComment.username);
						comment.put("parentusername", advComment.parentusername);
						comment.put("isExpert", advComment.isExpert);
						comment.put("expertName", advComment.expertName);
						comment.put("score", advComment.score);
						comment.put("isShowScoreButton", advComment.isShowScoreButton);
						mCommentArray.put(comment);
					}
					obj.put("mComments", mCommentArray.toString());
				}
				
				if ((advisoryInfo.mPraises!=null)&&(advisoryInfo.mPraises.size()>0)) {
					JSONArray praiseArray = new JSONArray();
					for (AdvPraise advPraise : advisoryInfo.mPraises) {
						JSONObject praise = new JSONObject();
						praise.put("praiseId", advPraise.praiseId);
						praise.put("advInfoId", advPraise.advInfoId);
						praise.put("isPraise", advPraise.isPraise);
						praise.put("userId", advPraise.userId);
						praise.put("praiseTime", advPraise.praiseTime);
						praiseArray.put(praise);
					}
					obj.put("mPraises", praiseArray.toString());
				}
				
				advArray.put(obj);
			} catch (JSONException e) {
				continue;
			}
		}
		FileHelper.writeFile(filePath, advArray.toString());
		System.out.println("咨询数据缓存成功");
		return true;
	}
	private List<AdvImgs> getAdvImgs(JSONArray  array){
		
		List<AdvImgs> mImags = new ArrayList<AdvImgs>();
		AdvImgs advImg  = null;
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				advImg = new AdvImgs();
				if (obj.opt("id")!=null) {
					advImg.id = obj.optInt("id");
				}
				if (obj.opt("advInfoId")!=null) {
					advImg.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("URLcontent")!=null) {
					advImg.URLcontent = obj.getString("URLcontent");
				}
				
				if (obj.opt("descript")!=null) {
					if (obj.getString("descript").equals("null")) {
						advImg.descript="";
					}
					else {
						advImg.descript = obj.getString("descript");
					}
				}
				mImags.add(advImg);
			}
		} catch (Exception e) {
		}
		return	mImags;
		
	}
	
	public List<AdvisoryInfo> getAdvisoryInfos(int type , int userId, String serchrContent){
		List<AdvisoryInfo> advs = new ArrayList<AdvisoryInfo>();
		String advspath =  getFilePaht(type, userId, serchrContent);
		String strAdvs = null;
		try {
			strAdvs = FileHelper.readTextFile(advspath);
			JSONArray array = new JSONArray(strAdvs);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				AdvisoryInfo advInfo = new AdvisoryInfo();
				if (obj.opt("advInfoId")!=null) {
					advInfo.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("userId")!=null) {
					advInfo.userId = obj.optInt("userId");
				}
				if (obj.opt("userName")!=null) {
					advInfo.userName = obj.getString("userName");
				}
				if (obj.opt("userHeaderFile")!=null) {
					advInfo.userHeaderFile = obj.getString("userHeaderFile");
				}
				if (obj.opt("uploadTime")!=null) {
					advInfo.uploadTime = obj.getString("uploadTime");
				}
				if (obj.opt("areacode")!=null) {
					advInfo.areacode = obj.getString("areacode");
				}
				if (obj.opt("qestion")!=null) {
					advInfo.qestion = obj.getString("qestion");
				}
				if (obj.opt("subjectName")!=null) {
					advInfo.subjectName = obj.getString("subjectName");
				}
				
				if (obj.opt("mImgs")!=null) {
					JSONArray mImgs = new JSONArray(obj.getString("mImgs"));
					advInfo.mImgs = getAdvImgs(mImgs);
				}
				
				if (obj.opt("mPraises")!=null) {
					JSONArray mPraises = new JSONArray(obj.getString("mPraises"));
					advInfo.mPraises = getAdvPraises(mPraises);
				}
				
				if (obj.opt("mComments")!=null) {
					JSONArray mComments = new JSONArray(obj.getString("mComments"));
					advInfo.mComments = getAdvinfoComments(mComments);
				}
				advs.add(advInfo);
			}
		} catch (IOException e) {
		} catch (JSONException e) {
		}
		return  advs;
	}
	
	
	private List<AdvPraise> getAdvPraises(JSONArray array){
		List<AdvPraise> praises = new ArrayList<AdvPraise>();
		AdvPraise advPraise = null;
		try {
			for(int i= 0;i<array.length();i++){
				JSONObject obj = array.getJSONObject(i);
				advPraise = new AdvPraise();
				if (obj.opt("praiseId")!=null) {
					advPraise.praiseId = obj.optInt("praiseId");
				}
				if (obj.opt("advInfoId")!=null) {
					advPraise.advInfoId = obj.optInt("advInfoId");
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
				praises.add(advPraise);
			}
		} catch (Exception e) {
		}
		return praises;

	}
	
	
	private List<AdvinfoComment> getAdvinfoComments(JSONArray array){
		List<AdvinfoComment> comments = new ArrayList<AdvinfoComment>();
		AdvinfoComment comment = null;
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				comment = new AdvinfoComment();
				if (obj.opt("commentId")!=null) {
					comment.commentId = obj.optInt("commentId");
				}
				if (obj.opt("advInfoId")!=null) {
					comment.advInfoId = obj.optInt("advInfoId");
				}
				if (obj.opt("comment")!=null) {
					comment.comment = obj.getString("comment");
				}
				if (obj.opt("parentId")!=null) {
					comment.parentId = obj.optInt("parentId");
				}
				if (obj.opt("userId")!=null) {
					comment.userId = obj.optInt("userId");
				}
				if (obj.opt("parentId")!=null) {
					comment.parentId = obj.optInt("parentId");
				}
				if (obj.opt("commentTime")!=null) {
					comment.commentTime = obj.getString("commentTime");
				}
				if (obj.opt("username")!=null) {
					comment.username = obj.getString("username");
				}
				if (obj.opt("parentusername")!=null) {
					comment.parentusername = obj.getString("parentusername");
				}
				if (obj.opt("isExpert")!=null) {
					comment.isExpert = obj.getBoolean("isExpert");
				}
				if (obj.opt("expertName")!=null) {
					comment.expertName = obj.getString("expertName");
				}
				
				if (obj.opt("score")!=null) {
					comment.score = obj.getLong("score");
				}
				if (obj.opt("isShowScoreButton")!=null) {
					comment.isShowScoreButton = obj.getBoolean("isShowScoreButton");
				}
				comments.add(comment);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return comments;
	}
}
