package com.supermap.agr.obj;

import java.util.List;

public class Focus {
    String cityname;
    //List<Product> listpro;
    
    public Focus(String cityname){
    	this.cityname = cityname;
    	
    }
    public Focus(){}

	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

    
}
