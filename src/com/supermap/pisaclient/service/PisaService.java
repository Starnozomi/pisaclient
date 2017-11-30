package com.supermap.pisaclient.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.ViewDebug.FlagToString;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.biz.CropDao;
import com.supermap.pisaclient.biz.MessageQueryDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.biz.WeatherDao;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.DateUtiles;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.LocalHelper;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.Address;
import com.supermap.pisaclient.entity.AgrImgs;
import com.supermap.pisaclient.entity.AgrInfo;
import com.supermap.pisaclient.entity.AgrPraise;
import com.supermap.pisaclient.entity.AgrinfoComment;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.entity.WarningInfo;
import com.supermap.pisaclient.ui.AdvDetailActivity;
import com.supermap.pisaclient.ui.CropUploadActivity;
import com.supermap.pisaclient.ui.MessageCenterActivity;
import com.supermap.pisaclient.ui.SuggestDetailActivity;

public class PisaService extends Service {

	private PisaBinder mBinder = new PisaBinder();

	private Handler mHandler;

	private final int CHECK_UPDATE_TIME = 1000 * 20;

	private int mWarning_Remind_num = 0;

	private int mCrops_Remind_num = 0;

	private ServiceThread mServiceThread;

	private CityDao mCityDao;

	private CropDao mCropDao;

	private List<City> mCities;

	private WeatherDao mWeatherDao;

	private boolean isNetOk = true;

	private int netCheckTimes = 0;

	private MessageQueryDao messageQueryDao;
	
	private PreferencesService mPreferencesService;
	
	private LocalHelper instance ;
	
	private String mAreacode;
	
	private NotificationManager manager ;
	
	private int mUserID ;

	public Handler mShowHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.UPDATE_WARNING_FLAG:
				break;
			case Constant.UPDATE_CROPS_FLAG:
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		init();

	}

	private void init() {
		mCities = ExitApplication.getInstance().mCities;
		mCityDao = new CityDao(getApplicationContext());
		mWeatherDao = new WeatherDao();
		mCropDao = new CropDao();
		messageQueryDao = new MessageQueryDao();
		mPreferencesService = new PreferencesService(getApplicationContext());
		mCityDao.initWaringDB();
		mServiceThread = new ServiceThread();
		mServiceThread.start();
	//	initLocalArea();
		manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		System.out.println("pisa service exit");
		super.onDestroy();
	}

	private void updateMsg() {
		User user = UserDao.getInstance().get();
		if (user != null) {
			List<Integer> list = messageQueryDao.getUncheckMsg(user.id);
			if ((list != null) && (list.size() > 0)) {
				for (Integer integer : list) {
					System.out.println("msgid+:"+integer);
					ClientMsg msg = messageQueryDao.getMsg(integer);
					if (msg==null) {
						return;
					}
					System.out.println("new msgid+:"+msg.msgid);
					if (!mCityDao.isHavingTheMsg(msg)) {
						showNotification(msg);
//						showNotificationNew(msg);
						mCityDao.addMsg(msg);
						update(Constant.UPDATE_MSG_FLAG, 1);
					}
				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	public void showNotification(ClientMsg msg) {
		System.out.println("msgid in no:"+msg.msgid);
		// ① 获取NotificationManager的引用
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		// ② 初始化Notification
		int icon = R.drawable.app_ico;
		CharSequence tickerText = "重庆农气";
		long when = System.currentTimeMillis();
//		Notification mNotification = new Notification(icon, tickerText, when);
		Notification mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = tickerText;
		mNotification.when = when;
		mNotification.defaults = Notification.DEFAULT_ALL;
		// mNotification.flags |= Notification.FLAG_NO_CLEAR;
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		// ③ 定义notification的消息 和 PendingIntent
		Context context = this;
		CharSequence contentTitle = "重庆农气";
		CharSequence contentText = null;
		Intent notificationIntent = null;
		switch (msg.msgtypeid) {
		case 1:// 普通消息
			notificationIntent = new Intent(this, MessageCenterActivity.class);
			break;
		case 2:// 专家回复消息
			contentText = "回复消息：";
			notificationIntent = new Intent(this, AdvDetailActivity.class);
			break;
		case 3:// 用户回复专家消息
			contentText = "回复消息：";
			notificationIntent = new Intent(this, AdvDetailActivity.class);
			break;
		case 4:// 用户给专家打分
			contentText = "打分消息：";
			notificationIntent = new Intent(this, AdvDetailActivity.class);
			break;
		case 5:// 提醒 专家回复用户 （用户咨询以后，系统分配专家解答）
			contentText = "咨询解答：";
			notificationIntent = new Intent(this, AdvDetailActivity.class);
			break;
		case 6:// 提醒用户 农情上报
			contentText = "农情上报：";
			notificationIntent = new Intent(this, CropUploadActivity.class);
			break;
		case 7:// 产品提议
			contentText = "产品提议：";
			notificationIntent = new Intent(this, SuggestDetailActivity.class);
			break;
		case 8:// 专家建议
			contentText = "专家建议：";
			notificationIntent = new Intent(this, SuggestDetailActivity.class);
			break;
		default:
			notificationIntent = new Intent(this, MessageCenterActivity.class);
			break;
		}

		contentText = contentText + msg.content;

		notificationIntent.putExtra("msgid", msg.msgid);
		System.out.println("get msgid:"+notificationIntent.getIntExtra("msgid", 0));
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT );
		mNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(msg.msgid, mNotification);
	}
	


     @SuppressLint("NewApi")
	private void showNotificationNew(ClientMsg msg) {
    	 
    	 System.out.println("msgid in no:"+msg.msgid);
    	 int icon = R.drawable.app_ico;
    	 Context context = this;
    	 CharSequence tickerText = "重庆农气";
 		 CharSequence contentTitle = "重庆农气";
 		 CharSequence contentText = null;
 		 Intent resultIntent = null;

         TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
         switch (msg.msgtypeid) {
 		case 1:// 普通消息
 			  // Adds the back stack
 	         stackBuilder.addParentStack(MessageCenterActivity.class);
 	         // Adds the Intent to the top of the stack
 	         resultIntent= new Intent(this, MessageCenterActivity.class);
 	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 2:// 专家回复消息
 			contentText = "回复消息：";
 			 stackBuilder.addParentStack(AdvDetailActivity.class);
 	         resultIntent = new Intent(this, AdvDetailActivity.class);
 	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 3:// 用户回复专家消息
 			contentText = "回复消息：";
 			stackBuilder.addParentStack(AdvDetailActivity.class);
	         resultIntent = new Intent(this, AdvDetailActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 4:// 用户给专家打分
 			contentText = "打分消息：";
 			stackBuilder.addParentStack(AdvDetailActivity.class);
	         resultIntent = new Intent(this, AdvDetailActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 5:// 提醒 专家回复用户 （用户咨询以后，系统分配专家解答）
 			contentText = "咨询解答：";
 			stackBuilder.addParentStack(AdvDetailActivity.class);
	         resultIntent = new Intent(this, AdvDetailActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 6:// 提醒用户 农情上报
 			contentText = "农情上报：";
 			stackBuilder.addParentStack(CropUploadActivity.class);
	         resultIntent = new Intent(this, CropUploadActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 7:// 产品提议
 			contentText = "产品提议：";
 			stackBuilder.addParentStack(SuggestDetailActivity.class);
	         resultIntent = new Intent(this, SuggestDetailActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		case 8:// 专家建议
 			contentText = "专家建议：";
 			stackBuilder.addParentStack(SuggestDetailActivity.class);
	         resultIntent = new Intent(this, SuggestDetailActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		default:
 			stackBuilder.addParentStack(MessageCenterActivity.class);
	         resultIntent = new Intent(this, MessageCenterActivity.class);
	         stackBuilder.addNextIntent(resultIntent);
 			break;
 		}
         
         resultIntent.putExtra("msgid", msg.msgid);
         // Gets a PendingIntent containing the entire back stack
         PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                 PendingIntent.FLAG_UPDATE_CURRENT);

         Notification notification = new NotificationCompat.Builder(PisaService.this)
                 .setSmallIcon(icon)
                 .setTicker(tickerText)
                 .setContentTitle(contentTitle).setContentText(contentText)
                 .setContentIntent(resultPendingIntent).setAutoCancel(true)
                 .setDefaults(Notification.DEFAULT_ALL).build();
         manager.notify(msg.msgid, notification);
     }

	private void updateWarnings() {
		if (mCities != null) {
			for (City city : mCities) {
				if (city.name.equals("自动定位")) {
					String locateArea = LocalHelper.getInstance(getApplicationContext()).getCity();
					updateWarning(locateArea);
				} else {
					updateWarning(city.name);
				}
			}
		}
	}

	private void updateWarning(String city) {
		String areacode = null;
		areacode = mCityDao.queryAreacode(city);
		List<WarningInfo> list = mWeatherDao.getWarningInfos(areacode, DateUtiles.getLastDayTime());
		if (list != null) {
			mCityDao.addWarning(list);
		}
		mWarning_Remind_num = mCityDao.getUnCheckedWarningNum();
		update(Constant.UPDATE_WARNING_FLAG, mWarning_Remind_num);
	}

	private void update(int flag, int num) {
		Message msg = Message.obtain();
		msg.what = flag;
		msg.arg1 = num;
		if (mHandler != null) {
			mHandler.sendMessage(msg);
		}
	}

	private void updateCrops() {

		List<AgrInfo> result = new ArrayList<AgrInfo>();
		if (UserDao.getInstance().get() == null) {// 没有登陆，就只能查看当前县的农情动态
			String city = LocalHelper.getInstance(getApplicationContext()).getCity();
			String areacode = mCityDao.queryAreacode(city);
			// areacode = "500240";
			result = mCropDao.getAgrInfosByAreacode(areacode, mCityDao.getLastFreshTime(areacode,-1));
		} else {
			User mUser = UserDao.getInstance().get();
			result = mCropDao.getAgrInfos(mUser.id, mCityDao.getLastFreshTime(mUser.areaCode,mUser.id));
		}
		if (result != null) {
			mCityDao.addAgrInfoList(result);
			addCommentToDB(result);
			addPraiseToDB(result);
			addAgrImgToDB(result);
		}
		// 更新以前的农情评论，赞有没有新的变化
		// 不用遍历，只需用数据库中最大的评论时间，和暂时间去服务器抓取有没有新的评论和赞
		updateCommentToDB();
		updatePraiseToDB();
		if (result != null) {
			mCrops_Remind_num += result.size();
			update(Constant.UPDATE_CROPS_FLAG, mCrops_Remind_num);
		}
	}
	
	
	
	public void updateCropsNew(){
		User mUser = UserDao.getInstance().get();
		int num = 0;
		if(mUser==null){
			if (mAreacode==null) {
				return;
			}
			num = mCropDao.getRefreshAgrInfosByAreacodeNum(mAreacode, mPreferencesService.getCropLastRefreshTime(-1));
			mUserID = -1;
		}
		else {
			num = mCropDao.getRefreshAgrInfosNum(mUser.id, mPreferencesService.getCropLastRefreshTime(mUser.id));
			mUserID = mUser.id;
		}
		
		if (num>0) {
			mPreferencesService.saveCropLastRefreshTime(mUserID, DateUtiles.getNow());
			mCrops_Remind_num+=num;
		}
		update(Constant.UPDATE_CROPS_FLAG, mCrops_Remind_num);
		
		
	}
	
	private void initLocalArea(){
		instance = LocalHelper.getInstance(getApplicationContext());
		instance.init();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Address address = instance.getAddress();
				if (address!=null) {
					mAreacode = address.code;
				}
				instance.close();
			}
		}, 10*10000);
	}

	public void addAgrImgToDB(List<AgrInfo> list) {
		List<AgrImgs> imageslist = new ArrayList<AgrImgs>();
		for (AgrInfo agrInfo : list) {
			imageslist = mCropDao.getAgrImgs(agrInfo.agrInfoId);
			mCityDao.addAgrImgList(imageslist);
		}
	}

	public void addCommentToDB(List<AgrInfo> list) {
		List<AgrinfoComment> commentslist = new ArrayList<AgrinfoComment>();
		for (AgrInfo agrInfo : list) {
			commentslist = mCropDao.getAgrImgComments(agrInfo.agrInfoId);
			mCityDao.addAgrinfoCommentList(commentslist);
		}
	}

	public void addPraiseToDB(List<AgrInfo> list) {
		List<AgrPraise> praiseslList = new ArrayList<AgrPraise>();
		for (AgrInfo agrInfo : list) {
			praiseslList = mCropDao.getAgrPraise(agrInfo.agrInfoId);
			mCityDao.addAgrpaiseList(praiseslList);
		}
	}

	public void updateCommentToDB() {
		List<AgrinfoComment> commentslist = new ArrayList<AgrinfoComment>();
		commentslist = mCropDao.getAgrComments(mCityDao.getLocalCommentMaxTime());
		if ((commentslist != null) && (commentslist.size() > 0)) {
			mCityDao.addAgrinfoCommentList(commentslist);
		}
	}

	public void updatePraiseToDB() {
		List<AgrPraise> praiseslList = new ArrayList<AgrPraise>();
		praiseslList = mCropDao.getAgrPraise(mCityDao.getLocalPrasieMaxTime());
		if ((praiseslList != null) && (praiseslList.size() > 0)) {
			mCityDao.addAgrpaiseList(praiseslList);
		}
	}

	class ServiceThread extends Thread {

		@Override
		public void run() {

			while (ExitApplication.getInstance().mAppStarted) {

				if (!CommonUtil.checkNetState(getApplicationContext())) {
					netCheckTimes++;
					if (netCheckTimes <= 3) {
						// CommonUtil.showToask(getApplicationContext(), "请检查网络");
					}
					isNetOk = false;
					continue;
				}
				isNetOk = true;
				if (isNetOk) {
					updateWarnings();
//					updateCrops();
					updateCropsNew();
					updateMsg();
				}

				try {
					Thread.sleep(CHECK_UPDATE_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	// 删除通知
	private void clearNotification() {
		// 启动后删除之前我们定义的通知
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);

	}

	public class PisaBinder extends Binder {

		public void setHandler(Handler handler) {
			mHandler = handler;
		}

		public void startDownload() {
		}

		public void setCitys(List<City> list) {
			mCities = list;
		}

		public void setNoWarningRemind() {
			mCityDao.setChecked(DateUtiles.getNow());
			mWarning_Remind_num = mCityDao.getUnCheckedWarningNum();
			update(Constant.UPDATE_WARNING_FLAG, mWarning_Remind_num);
		}

		public int getmWarning_Remind_num() {
			return mWarning_Remind_num;
		}

		public void setmWarning_Remind_num(int warning_Remind_num) {
			mWarning_Remind_num = warning_Remind_num;
		}

		public int getmCrops_Remind_num() {
			return mCrops_Remind_num;
		}
		
		public int getmMsg_Remind_num() {
			
			User user = UserDao.getInstance().get();
			if (user != null) {
				int unRedMsg = mCityDao.getUnCheckedMsgNum(user.id);
				return unRedMsg;
			}
			else {
				return 0;
			}
		}

		public void setmCrops_Remind_num(int crops_Remind_num) {
			mCrops_Remind_num = crops_Remind_num;
			update(Constant.UPDATE_CROPS_FLAG, mCrops_Remind_num);
		}
		
		public void setLocalArea(String areacode){
			PisaService.this.mAreacode = areacode;
		}

		public void updateCropsMethod() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					updateCropsNew();
				}
			}).start();
		}

	}
}
