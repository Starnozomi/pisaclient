package com.supermap.pisaclient.ui;

import android.os.Bundle;
import android.view.View;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.Utils;

public class CropDetailActivity extends BaseActivity {
	
	private View mContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTvTitle(Utils.getString(this, R.string.crop_detail));
		setIsBack(true);
		setIsNavigator(false);
		
		mContent = inflater(R.layout.warning_detail_main);
		setBackOnClickListener(this);
	}

}
