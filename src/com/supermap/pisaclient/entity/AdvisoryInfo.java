package com.supermap.pisaclient.entity;

import java.util.List;

public class AdvisoryInfo {
	
	public int	advInfoId;			//农情ID
	public int	userId;	
	public String userName;
	public String userHeaderFile;
	public String uploadTime;
	public String areacode;
	public String qestion;
	public String subjectName;
	public List<AdvImgs> mImgs;
	public List<AdvinfoComment> mComments;
	public List<AdvPraise> mPraises;
}
