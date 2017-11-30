package com.supermap.pisaclient.entity;

public class AdvinfoComment {

		public int commentId;
		public int advInfoId;
		public String comment;
		public int parentId;
		public int userId;
		public String commentTime;
		public String username;
		public String parentusername;
		public boolean isExpert;
		public int expertid;
		public String expertName;
		public float score;				//如果是专家对某一条咨询的评论，记录这条记录的用户给专家的打分
		public boolean isShowScoreButton  =false;
}
