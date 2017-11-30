package com.supermap.pisaclient.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.supermap.pisaclient.ui.UserLoginActivity;

public class CommonDialog {

	public static void setLoginDialog(final Context context){
		new AlertDialog.Builder(context) 
	    .setTitle("还没登录或者注册").setMessage("是否登录?")  
	    .setPositiveButton("登录",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent();
				intent.setClass(context,UserLoginActivity.class);
				context.startActivity(intent);
			}
	    })  
	    .setNegativeButton("取消",new DialogInterface.OnClickListener() {  
	                @Override  
	                public void onClick(DialogInterface dialog,int which) { 
	                }  
	            }).show();// 
	}
}
