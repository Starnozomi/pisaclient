package com.supermap.agr.obj;

public class Product {
	private String data;
    private int imgId;
    
	public Product(String data, int imgId){
		this.data = data;
		this.imgId = imgId;
	}
	public Product(){}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	
	
}
