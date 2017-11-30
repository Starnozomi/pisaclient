package com.supermap.agr.common;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Toast;

public class Common {
	
	/**
	 *  ��ȡapp�汾�� 
	 */
	public static String getVersion(Context context){
		String version ="";
		PackageManager manager = context.getPackageManager();
		try { 
		       PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		       version = "�汾��:" + info.versionName; //�汾��
	     } catch (NameNotFoundException e) {
		   // TODO Auto-generated catch block
		    e.printStackTrace();
		 }
		 return version;
	}
	
	/**
	 *  ������Toast 
	 */
	public static void serToast(Context context, String input){
		Toast.makeText(context, input, Toast.LENGTH_SHORT).show();
	}

	/**
	 *  ������ҳ���� 
	 */
	public static void setWeb(WebView web){
		
		WebSettings webSettings = web.getSettings();
		
		webSettings.setJavaScriptEnabled(true);//����WebView֧��JavaScript  
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);  
		webSettings.setUseWideViewPort(true);//�ؼ��  
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
		webSettings.setDisplayZoomControls(false);  
		webSettings.setJavaScriptEnabled(true); // ����֧��javascript�ű�  
		webSettings.setAllowFileAccess(true); // ��������ļ�  
		webSettings.setBuiltInZoomControls(true); // ������ʾ���Ű�ť  
		webSettings.setSupportZoom(true); // ֧������ 
	}

}
