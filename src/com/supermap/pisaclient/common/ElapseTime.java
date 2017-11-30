package com.supermap.pisaclient.common;

public class ElapseTime {
	
	private long start;
	private long end;
	private String tag;
	
	public ElapseTime(String tag){
		this.tag = tag;
	}
	
	public void start(){
		start =System.currentTimeMillis();
	}
	
	public void end(){
		end =System.currentTimeMillis();
		System.out.println(tag+"加载耗时 "+(end-start)/(float)1000+"秒");
	}

}
