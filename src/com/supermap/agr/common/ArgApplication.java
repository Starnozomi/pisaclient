package com.supermap.agr.common;

import android.app.Application;
import android.content.Context;


public class ArgApplication extends Application {  
    private static Context context;  
    @Override  
    public void onCreate() {  
        context=getApplicationContext();  
    }  
    public static Context getContext(){  
        return context;  
    }  
  
}  