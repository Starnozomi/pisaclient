package com.supermap.pisaclient.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supermap.android.commons.EventStatus;
import com.supermap.android.maps.measure.MeasureMode;
import com.supermap.android.maps.measure.MeasureParameters;
import com.supermap.android.maps.measure.MeasureResult;
import com.supermap.android.maps.measure.MeasureService;
import com.supermap.android.maps.measure.MeasureService.MeasureEventListener;
import com.supermap.pisaclient.R;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.entity.AdvImgs;
import com.supermap.pisaclient.entity.AdvPraise;
import com.supermap.pisaclient.entity.AdvinfoComment;
import com.supermap.pisaclient.entity.AdvisoryInfo;
import com.supermap.pisaclient.entity.Category;
import com.supermap.pisaclient.entity.Product;
import com.supermap.pisaclient.entity.UpdataInfo;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.services.components.commontypes.Point2D;
import com.supermap.services.components.commontypes.Unit;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class CommonUtil {

	public static  String SERIALIZE_PATH_USER = FileManager.getSaveUserCacheDir()+"user_info.data";

	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId() + tm.getSimSerialNumber() + tm.getSubscriberId();
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static String getStime(Date dt) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return df.format(dt);
	}

	public static double getResolution(double scale) {
		return 0.0254 / ((1 / scale) * 96 * ((Math.PI * 2 * 6378137) / 360));
	}

	public static boolean isMobileNO(String mobiles) {
		String telRegex = "[1][3578]\\d{9}";
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	public static List<Product> CURRENT_PROF = new ArrayList<Product>();
	public static List<Category> CURRENT_CATEGORY = new ArrayList<Category>();
	
	//咨询 我的
		public static List<AdvisoryInfo> mMyAdvInfos = new ArrayList<AdvisoryInfo>();
		public static  List<List<AdvImgs>> mMyImgList = new ArrayList<List<AdvImgs>>();
		public static  List<List<AdvinfoComment>> mMyAdvComments = new ArrayList<List<AdvinfoComment>>();
		public static List<List<AdvPraise>> mMyAdvPraises = new ArrayList<List<AdvPraise>>();
		//咨询 最新
		public static List<AdvisoryInfo> mNewAdvInfos = new ArrayList<AdvisoryInfo>();
		public static List<List<AdvImgs>> mNewImgList = new ArrayList<List<AdvImgs>>();
		public static List<List<AdvinfoComment>> mNewAdvComments = new ArrayList<List<AdvinfoComment>>();
		public static List<List<AdvPraise>> mNewAdvPraises = new ArrayList<List<AdvPraise>>();
		//咨询 热门
		public static List<AdvisoryInfo> mHotAdvInfos = new ArrayList<AdvisoryInfo>();
		public static List<List<AdvImgs>> mHotImgList = new ArrayList<List<AdvImgs>>();
		public static List<List<AdvinfoComment>> mHotAdvComments = new ArrayList<List<AdvinfoComment>>();
		public static List<List<AdvPraise>> mHotAdvPraises = new ArrayList<List<AdvPraise>>();
		//咨询 收索
		public static String mSerch;
		public static List<AdvisoryInfo> mSerchAdvInfos = new ArrayList<AdvisoryInfo>();
		public static List<List<AdvImgs>> mSerchImgList = new ArrayList<List<AdvImgs>>();
		public static List<List<AdvinfoComment>> mSerchAdvComments = new ArrayList<List<AdvinfoComment>>();
		public static List<List<AdvPraise>> mSerchAdvPraises = new ArrayList<List<AdvPraise>>();

	public static boolean isValidString(String str) {
		if (str != null && str.length() > 1) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == '\n') {
					return false;
				}
			}
		}
		return true;
	}

	public static String getCutString(String s, int length) {
		int index = s.indexOf(".");
		if (index != -1 && s.length() > index + length + 1) {
			s = s.substring(0, index + 1 + length);
		}
		return s;
	}

	public static void checkUpdate(Context context) {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setDeltaUpdate(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (updateStatus == 0 && updateInfo != null) {
					
				}
			}
		});
		UmengUpdateAgent.update(context);
	}
	
	public static void checkUpdateForClick(final Context context) {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setDeltaUpdate(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (updateStatus == 0 && updateInfo != null) {
					
				}
				else {
					CommonUtil.showToask(context, "已经是最新版本");
				}
			}
		});
		UmengUpdateAgent.update(context);
	}

	public static com.supermap.services.components.commontypes.Point2D getCenterPoint(
			com.supermap.services.components.commontypes.Point2D[] points) {
		if (points != null) {
			int length = points.length;
			com.supermap.services.components.commontypes.Point2D center = new com.supermap.services.components.commontypes.Point2D();
			double x = 0;
			double y = 0;
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					x += points[i].x;
					y += points[i].y;
				}
				center.x = x / length;
				center.y = y / length;
			}
			return center;
		}
		return null;
	}

	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static boolean setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return false;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		return true;
	}

	public static boolean sendMessage(String content, String phone) {
		SmsManager smsManager = SmsManager.getDefault();
		if (content.length() > 70) {
			List<String> contents = smsManager.divideMessage(content);
			for (String sms : contents) {
				smsManager.sendTextMessage(phone, null, sms, null, null);
			}
		} else {
			smsManager.sendTextMessage(phone, null, content, null, null);
		}
		return true;
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static void Serialize(String path, Object obj) throws Exception {
		try {
			File file = new File(FileManager.getSaveUserCacheDir());
			if (!file.exists())
				file.mkdirs();
			File fileData = new File(path);
			if (!fileData.exists())
				fileData.createNewFile();
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			if (obj != null)
				out.writeObject(obj);
			out.close();
			fos.close();
		} catch (Exception e) {

		}
	}

	public static Object DeSerialize(String path) throws Exception {
		try {
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream oin = new ObjectInputStream(fis);
			Object obj = oin.readObject();
			oin.close();
			fis.close();
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/";
		}
	}

	public static boolean checkNetState(Context context) {
		boolean netstate = false;
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						netstate = true;
						break;
					}
				}
			}
		}
		return netstate;
	}

	public static int getZoom(long[] scales) {
		if (Constant.MAP_TYPE == Constant.SATTELITE) {
			return Constant.SCALE_INDEX;
		} else {
			if (Constant.SCALE_INDEX >= scales.length) {
				Constant.LAST_SATTELITE = Constant.SCALE_INDEX;
				return scales.length - 1;
			} else {
				return Constant.SCALE_INDEX;
			}
		}
	}

	public static double getMetreSquare(double scale, double area) {
		return scale * scale * area * Constant.MAP_RESOLUTION;
	}

	public static CustomProgressDialog createProgressDialog(Context context) {
		CustomProgressDialog progress = CustomProgressDialog.createDialog(context);
		progress.setCancelable(true);
		return progress;
	}

	public static void showToask(Context context, String tip) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom, null);
		TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
		text.setText(tip);
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
	 /**
	  *  根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    public static int dipToPixels(Context context,int dip) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}
	
	
	public static int getDeviceScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();  
		dm = context.getApplicationContext().getResources().getDisplayMetrics();  
		return dm.heightPixels;
//		DisplayMetrics dm = new DisplayMetrics();
//		//获取屏幕信息
//		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		manager.getDefaultDisplay().getMetrics(dm);
//		int screenHeigh = dm.heightPixels;
//		return screenHeigh;
	}
	
	public static boolean getIsbigScreenOrHighDensty(Context context){
		DisplayMetrics dm = new DisplayMetrics();  
		dm = context.getResources().getDisplayMetrics();  
		int  densityDpi = dm.densityDpi;
		if (densityDpi>240) {
			return true;
		}
		else {
			 return false;
		}
	}
	
	
	
	public static int getDeviceScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		//获取屏幕信息
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}
	

	public static MeasureResult areaMeasure(String measureUrl, Point2D[] pts) {
		// 构造查询参数
		MeasureParameters parameters = new MeasureParameters();
		parameters.point2Ds = pts;
		parameters.unit = Unit.METER;// 使用平方米为单位，默认是米
		MeasureService service = new MeasureService(measureUrl);
		MyMeasureEventListener listener = new MyMeasureEventListener();
		service.process(parameters, listener, MeasureMode.AREA);
		try {
			listener.waitUntilProcessed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listener.getResult();

	}

	static class MyMeasureEventListener extends MeasureEventListener {
		private MeasureResult result;

		@Override
		public void onMeasureStatusChanged(Object sourceObject, EventStatus status) {
			// 分析结果
			if (sourceObject instanceof MeasureResult) {
				result = (MeasureResult) sourceObject;
			}
		}

		public MeasureResult getResult() {
			return result;
		}
	}

	public static String getAddress(Location location) {
		String url = "http://maps.google.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude()
				+ "&language=zh_CN&sensor=false";
		try {
			String content = HttpHelper.loadString(url);
			JSONObject jo1 = new JSONObject(content);
			String str1 = jo1.getString("results");
			JSONArray arr1 = new JSONArray(str1); // results 数组
			String str2 = arr1.get(0).toString();
			JSONObject jo2 = new JSONObject(str2);
			String area = jo2.getString("address_components");
			JSONArray arr2 = new JSONArray(area);
			String long_name = "";
			for (int i = 0; i < arr2.length(); i++) {
				JSONObject obj = arr2.getJSONObject(i);
				long_name = obj.getString("long_name");
				if ("[\"sublocality\",\"political\"]".equals(obj.getString("types"))) {
					return long_name;
				}
			}
		} catch (Exception e) {

		}
		return "定位失败";
	}

	/**
	 * 获取当前版本号
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context) throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packInfo.versionName;
	}
	
	/**
	 * 解析XML获取更新信息
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception
	{
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type=parser.getEventType();
		UpdataInfo info=new UpdataInfo();
		while(type!=XmlPullParser.END_DOCUMENT)
		{
			switch(type)
			{
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName()))
				{
					info.setVersion(parser.nextText());//获取版本号
				}
				else if("url".equals(parser.getName()))
				{
					info.setUrl(parser.nextText());//获取要升级的APK下载地址
				}
				else if("description".equals(parser.getName()))
				{
					info.setDescription(parser.nextText());//获取该文件的信息
				}
				break;
			}
			type=parser.next();
		}
		return info;		
	}
	
	/**
	 * 从服务器获取文件
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static File getFileFromServer(String path,ProgressDialog pd) throws Exception
	{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			URL url=new URL(path);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			pd.setMax(conn.getContentLength());
			InputStream is=conn.getInputStream();
			File file=new File(Environment.getExternalStorageDirectory(),"Gdmth.apk");
			FileOutputStream fos=new FileOutputStream(file);
			BufferedInputStream bis=new BufferedInputStream(is);
			byte[] buffer=new byte[1024];
			int len;
			int total=0;
			while((len=bis.read(buffer))!=-1)
			{
				fos.write(buffer,0,len);
				total+=len;
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		}
		else
		{
			return null;	
		}
	}
}
