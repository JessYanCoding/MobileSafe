package com.jess.mobilesafe.util;

import java.util.List;

import android.R.bool;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceState {
	public static boolean serviceRunning(Context context, String serviceName) {
		// 拿到activity管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 拿到所有运行的服务的信息
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// 判断运行中的服务的名称是否和参数中的服务想通
			String className = runningServiceInfo.service.getClassName();
			if (className.equals(serviceName)) {
				// 想通则返回True
				return true;
			}
		}
		return false;
	}
}
