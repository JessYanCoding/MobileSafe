package com.jess.mobilesafe.receiver;

import com.jess.mobilesafe.activity.SplashActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class packageinstall extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Uri data = intent.getData();
		String action = intent.getAction();
		if (action.equals("android.intent.action.PACKAGE_ADDED")) {

			Toast.makeText(context, data + "被安装", 0).show();
		} else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
			Toast.makeText(context, data + "被卸载", 0).show();
		}
	}

}
