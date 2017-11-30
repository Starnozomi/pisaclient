/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Product.java - 2014-3-25 上午10:58:20
 */

package com.supermap.pisaclient.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseProduct implements Parcelable {


	public String productid;
	public String expert;
	public String crop;
	public String title;
	public String content;
	public String createTime;
	public String sender;
	public String folder;
	
	
	


	public static final Parcelable.Creator<BaseProduct> CREATOR = new Creator<BaseProduct>() {

		public BaseProduct createFromParcel(Parcel source) {
			BaseProduct mProduct = new BaseProduct();
			
			return mProduct;
		}

		@Override
		public BaseProduct[] newArray(int size) {
			return new BaseProduct[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		/*dest.writeInt(ProductID);
		dest.writeInt(ProductTemplateID);
		dest.writeString(ProductTitle);
		dest.writeString(ProductSummary);
		dest.writeString(CreateTime);
		dest.writeString(SmallUrl);
		dest.writeString(BigUrl);
		dest.writeString(Source);
		dest.writeString(CategoryName);
		dest.writeString(info);
		dest.writeInt(Collects);
		dest.writeInt(Comments);*/
	}

}
