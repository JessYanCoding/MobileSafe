package com.jess.mobilesafe.sevice;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class KillProcessService extends Service {

	private BroadcastReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 创建接收器对象
		receiver = new LockKillReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);

	}

	/**
	 * 接收关屏的接受者，作用是关屏后杀死后台在运行的进程
	 * 
	 * @author Administrator
	 * 
	 */
	class LockKillReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 创建activiti管理器
			ActivityManager am = (ActivityManager) context
					.getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
			// 拿到所有在运行的进程信息，遍历进程
			for (RunningAppProcessInfo info : processes) {
				//传入包名杀死所有在运行的后台进程
				am.killBackgroundProcesses(info.processName);
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
}
