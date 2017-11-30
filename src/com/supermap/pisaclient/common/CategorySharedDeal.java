package com.supermap.pisaclient.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CategorySharedDeal {

	public SharedPreferences shared;

	public CategorySharedDeal(Context context) {
		shared = context.getSharedPreferences("pisa_category", Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putInt("category", 0);
		editor.commit();
	}

	public SharedPreferences getShared() {
		return shared;
	}

	public void setShared(SharedPreferences shared) {
		this.shared = shared;
	}

	public void setDefaultCategory(int category) {
		Editor editor = shared.edit();
		editor.putInt("default_category", category);
		editor.commit();
	}

	public int getDefaultCategory() {
		return shared.getInt("default_category", 0);
	}

}