package com.supermap.pisaclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;



public class ProductExpertDetailActivity extends BaseActivity {

	private View mContent;
	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvCrop;
	private TextView tvTime;
	private TextView tvExpert;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = this.getIntent();
		ExitApplication.getInstance().addActivity(this);
		setTvTitle(Utils.getString(this, R.string.product_expert_detail));
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		String time = intent.getStringExtra("time");
		String crop = intent.getStringExtra("crop");
		String expert = intent.getStringExtra("expert");
		mContent = inflater(R.layout.product_expert_detail);
		this.tvTitle = (TextView)mContent.findViewById(R.id.tv_title);
		this.tvContent = (TextView)mContent.findViewById(R.id.tv_content);
		this.tvCrop = (TextView)mContent.findViewById(R.id.tv_crop);
		this.tvTime = (TextView)mContent.findViewById(R.id.tv_create_time);
		this.tvExpert = (TextView)mContent.findViewById(R.id.tv_expert);
		
		this.tvTitle.setText(title);
		this.tvContent.setText(content);
		this.tvCrop.setText(crop);
		this.tvTime.setText(time);
		this.tvExpert.setText(expert);
	}

	
	
	
}
