package com.jess.mobilesafe.application;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;

public class Myapplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}
	class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{

		public void uncaughtException(Thread thread, Throwable ex) {
			
			android.os.Process.killProcess(android.os.Process.myPid());
			
		}
		
	}
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
			
}
