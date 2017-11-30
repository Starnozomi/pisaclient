package com.supermap.pisaclient.entity;

import java.util.List;

public class AgrInfo {
	
	public int agrInfoId;			//农情ID
	public int userId;	
	public String userName;
	public String userHeaderFile;
	public String uploadTime;
	public String areacode;
	public String descript;
	public String croptype;
	public String breed;
	public String growperiod;
	public int croptypeId;
	public int breedId;
	public int growperiodId;
	public List<AgrImgs> mImgs;
	public List<AgrinfoComment> mComments;
	public List<AgrPraise> mAgrPraises;
}
