package com.supermap.pisaclient.ui;

import java.util.ArrayList;
import java.util.List;

import org.restlet.security.User;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.MessageAdapter;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.ClientMsg;

public class MessageCenterActivity extends BaseActivity {

	private CityDao mCityDao;
	private View mContent;
	private ListView mListView;

	private List<ClientMsg> mClientMsgs = new ArrayList<ClientMsg>();
	private int mIndexPage = 0;
	private int mMaxPageSize = 10;
	private MessageAdapter mAdapter;

	private View mMoreView;
	private ProgressBar mpg;
	private Button mbtn_add_more;
	private Handler handler;
	private int lastVisibleIndex = 0;
	private int MaxDateNum = 0;
	private boolean isRemoveAddMoreView = false;
	private com.supermap.pisaclient.entity.User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setTvTitle(Utils.getString(this, R.string.msg_center));
		setIsBack(true);
		setBackOnClickListener(this);
		setIsNavigator(false);
		mCityDao = new CityDao(this);
//		int num = mCityDao.getUnCheckedMsgNum();
//		CommonUtil.showToask(this, "有" + num + "条未读消息");

		mContent = inflater(R.layout.message_center_main);
		mListView = (ListView) mContent.findViewById(R.id.lv_msg_center);

		mIndexPage++;
		user = UserDao.getInstance().get();
		if (user!=null) {
			mClientMsgs = mCityDao.getMsgs(user.id,mMaxPageSize * (mIndexPage - 1), mMaxPageSize);
		}
		
		MaxDateNum = mCityDao.getMsgNums();

		mMoreView = getLayoutInflater().inflate(R.layout.add_more_data, null);
		mbtn_add_more = (Button) mMoreView.findViewById(R.id.bt_load);
		mpg = (ProgressBar) mMoreView.findViewById(R.id.pg_add_more);
		handler = new Handler();
		mListView.addFooterView(mMoreView);

		mAdapter = new MessageAdapter(this, mClientMsgs);
		mListView.setAdapter(mAdapter);

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				drawRemindNum(R.id.rad_situation, Constant.NO_REMIND_FLAG);
				// mBinder.setmCrops_Remind_num(0);

				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mAdapter.getCount()) {
					// 当滑到底部时自动加载
					// mpg.setVisibility(View.VISIBLE);
					// mbtn_add_more.setVisibility(View.GONE);
					// handler.postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// loadMoreDate();
					// mbtn_add_more.setVisibility(View.VISIBLE);
					// mpg.setVisibility(View.GONE);
					// mAdapter.notifyDataSetChanged();
					// }
					// }, 2000);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 计算最后可见条目的索引
				lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;

				// 所有的条目已经和最大条数相等，则移除底部的View
				if (totalItemCount == MaxDateNum + 1) {
					if (!isRemoveAddMoreView) {
						mListView.removeFooterView(mMoreView);
					}
					if ((MaxDateNum != 0) && (!isRemoveAddMoreView) && (totalItemCount > MaxDateNum)) {
//						CommonUtil.showToask(MessageCenterActivity.this, "数据全部加载完成，没有更多数据！");
					}
					isRemoveAddMoreView = true;
				}
			}
		});

		mbtn_add_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIndexPage++;
				mpg.setVisibility(View.VISIBLE);// 将进度条可见
				mbtn_add_more.setVisibility(View.GONE);// 按钮不可见
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMoreDate();// 加载更多数据
						mbtn_add_more.setVisibility(View.VISIBLE);
						mpg.setVisibility(View.GONE);
					}
				}, 2000);
			}
		});
	}

	private void loadMoreDate() {
		user = UserDao.getInstance().get();
		List<ClientMsg> list  = null;
		if (user!=null) {
			list = mCityDao.getMsgs(user.id,mMaxPageSize * (mIndexPage - 1), mMaxPageSize);
			mClientMsgs.addAll(list);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_back:
			finish();
			break;
		default:
			break;
		}
		super.onClick(v);
	}

}
