package com.supermap.pisaclient.ui;


//import com.supermap.pisaclient.ui.SettingActivity;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.ui.CustomDialog;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.ExitApplication;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.entity.UpdataInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutappActivity extends BaseActivity{

//	private ImageButton Ibtn_back;
	private TextView App_version;
	private ImageView App_qr;
    private String result;
    private View mContent;
    private RelativeLayout App_update;
    Intent intent;
    private String bol;
    private Dialog dia;
//    private TextView mtv_my;

    
    protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		setTvTitle(Utils.getString(this, R.string.about));
		setIsNavigator(false);
		setIsBack(true);
		setBackOnClickListener(this);
		setIsNavigator(false);
		//setIsMy(true);
		
		mContent = inflater(R.layout.activity_aboutapp);
		//mtv_my.setText("联系我们");
		//mtv_my.setOnClickListener(this);
		
        App_version = (TextView) mContent.findViewById(R.id.app_version);
        
        App_update = (RelativeLayout) mContent.findViewById(R.id.app_update);
        App_update.setOnClickListener(this);
        
//        App_qr = (ImageView) mContent.findViewById(R.id.app_qr);
//        App_qr.setOnClickListener(this);
//        
//        Context context = AboutappActivity.this;
//        dia = new Dialog(context, R.style.edit_AlertDialog_style);
//        dia.setContentView(R.layout.app_qr);
//        //App_qr.setBackgroundResource(R.mipmap.iv_android);
//        //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
//        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
//        Window w = dia.getWindow();
//        WindowManager.LayoutParams lp = w.getAttributes();
//        lp.x = 0;
//        lp.y = 40;
//        dia.onWindowAttributesChanged(lp); 
        
        intent = getIntent();
//        bol = intent.getStringExtra("Visibility");
        //CommonUtil.showToask(AboutappActivity.this, bol);
//        if(bol.equals("1"))  {        	
//        App_update.setVisibility(View.VISIBLE);
//        } else if(bol.equals("0")) {
        App_update.setVisibility(View.INVISIBLE);	
//        }
     
        result = "版本号: "+versionGet();
        App_version.setText(result);
	}
	
	//获取版本号
	public String versionGet(){
		String version ="";
		PackageManager manager = this.getPackageManager();
        try { 
        	  PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
              version = info.versionName; //版本名
         } catch (NameNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
        return version;
	}
	

	
	public boolean checkNet() {
		if (!CommonUtil.checkNetState(this)) {
			CommonUtil.showToask(this, getResources().getString(R.string.net_errer));
			return false;
		}
		return true;
	}
	
	public void onClick(View v) {
//		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibtn_back:
			finish();
			break;
//		case R.id.app_update: //点击之后直接升级，无需验证版本
//			if (checkNet()) {
//				//CommonUtil.checkUpdateForClick(this);
//				new Thread(new InstallTask()).start();
//				//showUpdataDialog();
//			}
//			break;
//		case R.id.app_qr:
//			dia.show();
//			break;
		default:
			break;
//		case R.id.tv_my:
//			  CustomDialog.Builder builder = new CustomDialog.Builder(AboutappActivity.this);
//			  //builder.setMessage("用户交流QQ群:588370350\n\n进群验证:cqnq");		
//		     builder.create().show();
//			break;
		}
		super.onClick(v);
	}
	
//	UpdataInfo info;
//	public class InstallTask implements Runnable {
//		@Override
//		public void run() {
//			try
//			{
//				String path=getResources().getString(R.string.apk_xml_url);
//				URL url=new URL(path);
//				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//				conn.setConnectTimeout(5000);
//				InputStream is =conn.getInputStream();
//				info=CommonUtil.getUpdataInfo(is);
//				
//					System.out.println("版本号不同，提示升级");
//					Message msg=new Message();
//					msg.what=Constant.UPDATA_CLIENT;
//					handler.sendMessage(msg);
//				
//			}
//			catch (Exception e)
//			{
//				Message msg=new Message();
//				msg.what=Constant.UPDATA_ERROR;
//				handler.sendMessage(msg);
//				e.printStackTrace();
//			}
//		}	
//	}
//	
//	Handler handler=new Handler()
//	{
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch(msg.what)
//			{
//			case Constant.UPDATA_CLIENT:
//				showUpdataDialog();
//				break;
//			case Constant.UPDATA_ERROR:
//				Toast.makeText(AboutappActivity.this, "服务器超时，获取更新信息失败",Toast.LENGTH_SHORT).show();
//				break;
//			case Constant.DOWN_ERROR:
//				Toast.makeText(AboutappActivity.this, "下载新版本失败",Toast.LENGTH_SHORT).show();
//				break;
//
//			}
//		}
//	};
//
//	private void showUpdataDialog()
//	{
//		AlertDialog.Builder builder=new Builder(this);
//		builder.setTitle("版本升级");
//		builder.setMessage(info.getDescription());
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//						downLoadApk();
//			}		
//		});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//			}			
//		});
//		AlertDialog dialog=builder.create();
//		dialog.show();
//	}
//	
//	private void downLoadApk()
//	{
//		final ProgressDialog pd;
//		pd=new ProgressDialog(this);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("正在下载更新");
//		pd.show();
//		new Thread(){
//			@Override
//			public void run() {
//				try{
//					File file=CommonUtil.getFileFromServer(info.getUrl(), pd);
//					sleep(3000);
//					installApk(file);
//					pd.dismiss();
//				}
//				catch(Exception e)
//				{
//					Message msg=new Message();
//					msg.what=Constant.DOWN_ERROR;
//					handler.sendMessage(msg);
//					e.printStackTrace();
//				}
//			}		
//		}.start();
//	}
//	private void installApk(File file)
//	{
//		Intent intent=new Intent();
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
//	}
}
