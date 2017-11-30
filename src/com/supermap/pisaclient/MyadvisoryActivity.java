
package com.supermap.pisaclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.ui.AdvisoryQuestionActivity;
import com.supermap.pisaclient.ui.BaseActivity;

public class MyadvisoryActivity extends BaseActivity {

	private View mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.advisory_title));
		setRgNavigator(R.id.rad_advisory);
		setIsBack(true);
		mContent = inflater(R.layout.activity_myadvisory);
		
		startAdvisoryQuestionList();
	}

    /**
     *   指定参数  跳转到  “我的咨询”
     */
	private void startAdvisoryQuestionList() {
		Intent intent = new Intent();
		intent.putExtra("type", 3);
		intent.putExtra("jugg", "0");
		intent.setClass(this, AdvisoryQuestionActivity.class);
		startActivity(intent);
		this.finish();
	}

}
