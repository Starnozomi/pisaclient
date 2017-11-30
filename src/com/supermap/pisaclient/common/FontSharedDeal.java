package com.supermap.pisaclient.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FontSharedDeal {

	public SharedPreferences shared;

	public FontSharedDeal(Context context) {
		shared = context.getSharedPreferences("pisa_font", Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putInt("font", 0);
		editor.commit();
	}

	public SharedPreferences getShared() {
		return shared;
	}

	public void setShared(SharedPreferences shared) {
		this.shared = shared;
	}

	public void setDefaultCity(int font) {
		Editor editor = shared.edit();
		editor.putInt("default_font", font);
		editor.commit();
	}

	public int getDefaultFont() {
		return shared.getInt("default_font", (Constant.MAX_FONT + Constant.MIN_FONT) / 4);
	}

}