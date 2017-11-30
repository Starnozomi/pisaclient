package com.supermap.agr.common;

import android.app.Activity;
import android.view.Display;

public class Constant {

	public static String SESSIONID = null;
	public static final int UPDATA_CLIENT=1;
	public static final int UPDATA_ERROR=0;
	public static final int DOWN_ERROR=-1;
	public static final int UPDATA_NO=2;
	
	public static int[] getDisplay(Activity activity) {
		int[] data = new int[2];
		Display mDisplay = activity.getWindowManager().getDefaultDisplay();
		data[0] = mDisplay.getWidth();
		data[1] = mDisplay.getHeight();
		return data;
	}
}
