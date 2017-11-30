/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Product.java - 2014-3-25 上午10:58:20
 */

package com.supermap.pisaclient.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ExpertProduct implements Parcelable {

	public String expert;
	public String crop;
	public String title;
	public String content;
	public String createTime;
	public String farmlandName;


	public static final Parcelable.Creator<ExpertProduct> CREATOR = new Creator<ExpertProduct>() {

		public ExpertProduct createFromParcel(Parcel source) {
			ExpertProduct mProduct = new ExpertProduct();
			return mProduct;
		}

		@Override
		public ExpertProduct[] newArray(int size) {
			return new ExpertProduct[size];
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
