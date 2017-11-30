package com.supermap.pisaclient.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TimeSharedDeal {

	public SharedPreferences shared;

	public TimeSharedDeal(Context context) {
		shared = context.getSharedPreferences("pisa_time", Context.MODE_PRIVATE);
	}

	public SharedPreferences getShared() {
		return shared;
	}

	public void setShared(SharedPreferences shared) {
		this.shared = shared;
	}

	public void setUpTime(String time) {
		Editor editor = shared.edit();
		editor.putString("up_time", time);
		editor.commit();
	}

	public String getUpTime() {
		return shared.getString("up_time", null);
	}

}