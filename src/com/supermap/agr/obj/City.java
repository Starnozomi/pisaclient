package com.supermap.agr.obj;

import java.util.ArrayList;
import java.util.List;

public class City {
   String id;
   String text;
   String name;
   String code;
   List<City> children;
   
   public City(){
	   children = new ArrayList<City>();
   }
   
   public City(String id, String text, String name, String code) {
	  super();
	  this.id = id;
	  this.text = text;
	  this.name = name;
	  this.code = code;
   }

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getCode() {
	return code;
}

public void setCode(String code) {
	this.code = code;
}

public List<City> getChildren() {
	return children;
}
   
   
}
