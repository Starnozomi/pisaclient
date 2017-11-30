package com.supermap.pisaclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.WeatherDataUtil;
import com.supermap.pisaclient.entity.WarningStandard;

public class WarningDetailActivity extends BaseActivity {
	
	private View mContent;
	private ImageView  miv_type;
	private TextView   mtv_time;
	private TextView   mtv_content;
	private TextView   mtv_tips;
	private TextView   mtv_standard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.weather_warning_detail));
		setIsBack(true);
		setIsNavigator(false);
		Bundle  bundle = getIntent().getExtras();
		String type = bundle.getString("type");
		String level = bundle.getString("level");
		String time = bundle.getString("time");
		String content = bundle.getString("content");
		String title = bundle.getString("title");
		mContent = inflater(R.layout.warning_detail_main);
		setBackOnClickListener(this);
		
		miv_type = (ImageView)mContent.findViewById(R.id.iv_warning_detail_tpye);
		mtv_time =(TextView)mContent.findViewById(R.id.tv_warning_detail_create_time);
		mtv_content =(TextView)mContent.findViewById(R.id.tv_warning_detail_content);
		mtv_tips =(TextView)mContent.findViewById(R.id.tv_wt_tips);
		mtv_standard = (TextView)mContent.findViewById(R.id.tv_wt_standard);
		
		miv_type.setImageResource(WeatherDataUtil.mhm_wt_warning.get(type+level));
		mtv_time .setText(time);
		mtv_content.setText(content);
		
		WarningStandard warningStandard = new WarningStandard();
		warningStandard = ExitApplication.getInstance().mCityDao.getWarningStandard(type, level);
		mtv_standard.setText(warningStandard.standard);
		mtv_tips.setText(warningStandard.tips);
//		Toast.makeText(this, "standard"+warningStandard.standard+"tips"+ warningStandard.tips, Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			finish();
			break;
		}
		super.onClick(v);
	}

}
