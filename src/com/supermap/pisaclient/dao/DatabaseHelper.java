/* 
 * Copyright 2013 Share.Ltd  All rights reserved.
 * 
 * @DatabaseHelper.java - 2014-3-31 上午10:22:33
 */

package com.supermap.pisaclient.dao;

import com.supermap.pisaclient.common.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constant.DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Constant.TAB_CITY);
		db.execSQL(Constant.TAB_ALLCITY);
		db.execSQL(Constant.TAB_WEATHER);
		db.execSQL(Constant.TAB_TEMPERATURE);
		db.execSQL(Constant.TAB_WARNING);
		db.execSQL(Constant.TAB_AGRINFO);
		db.execSQL(Constant.TAB_AGRINFOCOMMENTS);
		db.execSQL(Constant.TAB_AGRPRAISE);
		db.execSQL(Constant.TAB_AGRIMGS);
		db.execSQL(Constant.TAB_MSG);
		db.execSQL(Constant.TAB_PRODUCT_CATEGORY);
		db.execSQL(Constant.TAB_PRODUCT);
		db.execSQL(Constant.TAB_PRODUCT_COMMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {// 数据库升级，保存用户原有的数据
			db.execSQL("drop table Cities");
			db.execSQL("drop table Weathers");
			db.execSQL("drop table Warnings");
		}
	}

}
