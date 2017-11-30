package com.supermap.pisaclient.entity;

import java.io.Serializable;

public class Subject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 97573518983466086L;
	//学科分类
	public int 		subjectId; 			//学科id
	public String 	name;				//学科名字
	public int 		parentId;			//父学科id
}
