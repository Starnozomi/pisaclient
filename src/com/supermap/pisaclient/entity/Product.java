/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @Product.java - 2014-3-25 上午10:58:20
 */

package com.supermap.pisaclient.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

	public int ProductID;
	public int ProductTemplateID;
	public String ProductTitle;
	public String ProductSummary;
	public String CreateTime;
	public String SmallUrl;
	public String BigUrl;
	public String Source;
	public String CategoryName;
	public String info;
	public int Collects;
	public int Comments;

	public static final Parcelable.Creator<Product> CREATOR = new Creator<Product>() {

		public Product createFromParcel(Parcel source) {
			Product mProduct = new Product();
			mProduct.ProductID = source.readInt();
			mProduct.ProductTemplateID = source.readInt();
			mProduct.ProductTitle = source.readString();
			mProduct.ProductSummary = source.readString();
			mProduct.CreateTime = source.readString();
			mProduct.SmallUrl = source.readString();
			mProduct.BigUrl = source.readString();
			mProduct.Source = source.readString();
			mProduct.CategoryName = source.readString();
			mProduct.info = source.readString();
			mProduct.Collects = source.readInt();
			mProduct.Comments = source.readInt();
			return mProduct;
		}

		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ProductID);
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
		dest.writeInt(Comments);
	}

}
