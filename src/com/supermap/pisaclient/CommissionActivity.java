package com.supermap.pisaclient;

import java.util.List;

import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.ui.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CommissionActivity extends BaseActivity {

	private TextView area, crop, product;
	private View mContent;
	Bundle bundle;
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_commission);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.use_commission));
		setRgNavigator(R.id.rad_more);
		setIsBack(true);
		mContent = inflater(R.layout.activity_commission);
		
		initView();
	}

	private void initView() {
		area = (TextView) findViewById(R.id.area);
		crop = (TextView) findViewById(R.id.crop);
		product = (TextView) findViewById(R.id.product);
		
		area.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(CommissionActivity.this, DetailedareaActivity.class);
				startActivity(intent);
			}
		});
		
		crop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(CommissionActivity.this, DetailedcropActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
}
