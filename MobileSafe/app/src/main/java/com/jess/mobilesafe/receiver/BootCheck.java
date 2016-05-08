package com.jess.mobilesafe.receiver;

import com.jess.mobilesafe.activity.SettingActivity;
import com.jess.mobilesafe.sevice.AddressDisplayService;
import com.jess.mobilesafe.sevice.BlackAbortService;
import com.jess.mobilesafe.sevice.LockService;
import com.jess.mobilesafe.sevice.ProgramDogService;
import com.jess.mobilesafe.util.ServiceState;
import com.jess.touch.RocketService;

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
		// 查看是否配置开机启动归属地查询
		boolean addressboot = shp.getBoolean("addressboot", false);
		// 查看是否配置开机启动锁屏助手
		boolean lockboot = shp.getBoolean("lockboot", true);
		boolean blackboot = shp.getBoolean("blackboot", false);
		boolean programboot = shp.getBoolean("programboot", false);
		// 依照配置信息开机启动或不开机启动归属地查询显示
		if (addressboot) {
			//如果配置了就开机启动
			Intent it_add = new Intent(context,
					AddressDisplayService.class);
			context.startService(it_add);
		}
		// 依照配置信息开机启动或不开机启动锁屏助手
		if (lockboot) {
			//如果配置了就开机启动
			if (!ServiceState.serviceRunning(context, "com.jess.touch.RocketService")) {
				
				context.startService(new Intent(context,
						RocketService.class));
			}
		}
		
		if (blackboot) {
			//如果配置了就开机启动
			context.startService(new Intent(context,
					BlackAbortService.class));
		}
		
		if (programboot) {
			//如果配置了就开机启动
			context.startService(new Intent(context,
					ProgramDogService.class));
		}
		// 查看配置文件是否已经设置防盗保护
		boolean protect = shp.getBoolean("protect", false);
		// 如果用户设置了防盗保护在开始下面的功能
		if (protect) {
			// 拿到sharapreference里的sim序列号
			String SIMNumber = shp.getString("SIM", null);
			// 获得当前的sim序列号进行比对
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String cNumber = tm.getSimSerialNumber();
			// 如果两个序列号都不为空
			if (!TextUtils.isEmpty(SIMNumber) && !TextUtils.isEmpty(cNumber)) {
				// 在序列号不匹配的情况下
				if (!SIMNumber.equals(cNumber)) {
					//SIM已变更发送警报短信
					//获得配置信息中的安全号码
					String phone = shp.getString("phone", "");
					SmsManager sm = SmsManager.getDefault();
					//发送警报短信
					sm.sendTextMessage(phone, null, "SMS is change!", null, null);
				} 

			}
		}
	}

}
