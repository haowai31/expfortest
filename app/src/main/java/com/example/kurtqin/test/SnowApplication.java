package com.example.kurtqin.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class SnowApplication extends Application {
	
	private static Context context;
	private String TAG = "SnowApplication";
	

    @Override
	public void onCreate(){
		super.onCreate();

		Log.d(TAG, "SnowApplication onCreate");
		SnowApplication.context = getApplicationContext();

		int a  = this.getApplicationInfo().flags;
	}
	
	public static Context getAppContext() {
		return SnowApplication.context;
	}

	public static boolean isUserDebug() {return true; }
	
}
