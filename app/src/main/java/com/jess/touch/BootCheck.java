package com.jess.touch;

import com.jess.mobilesafe.util.ServiceState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

public class BootCheck extends BroadcastReceiver {
	SharedPreferences shp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 开启启动后立即检测sim卡是否变更
		shp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		// 查看是否配置开机启动锁屏助手
		boolean lockboot = shp.getBoolean("lockboot", true);
		// 依照配置信息开机启动或不开机启动锁屏助手
		if (lockboot) {
			// 如果配置了就开机启动
			if (!ServiceState.serviceRunning(context,
					"com.jess.touch.RocketService")) {

				context.startService(new Intent(context, RocketService.class));
			}
		}
	}

}
