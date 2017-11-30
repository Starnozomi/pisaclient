package com.supermap.pisaclient.common;

import android.content.Context;
import android.widget.Toast;

/**
 *  ��ֹ�����ظ���Toast�����ʾ  
 */
public class ToastUtils {

	/* ֮ǰ��ʾ������ */  
    private static String oldMsg ;  
    /* Toast���� */  
    private static Toast toast = null ;  
    /* ��һ��ʱ�� */  
    private static long oneTime = 0 ;  
    /* �ڶ���ʱ�� */  
    private static long twoTime = 0 ;  
      
    /** 
     * ��ʾToast  
     */  
    public static void ToastUtils(){
    }
    
    public static void showToast(Context context,String message){  
        if(toast == null){  
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);  
            toast.show() ;  
            oneTime = System.currentTimeMillis() ;  
        }else{  
            twoTime = System.currentTimeMillis() ;  
            if(message.equals(oldMsg)){  
                if(twoTime - oneTime > Toast.LENGTH_SHORT){  
                    toast.show() ;  
                }  
            }else{  
                oldMsg = message ;  
                toast.setText(message) ;  
                toast.show() ;  
            }  
        }  
        oneTime = twoTime ;  
    }  
}
