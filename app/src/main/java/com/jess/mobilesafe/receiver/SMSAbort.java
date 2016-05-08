package com.jess.mobilesafe.receiver;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.sevice.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

public class SMSAbort extends BroadcastReceiver {

	private SharedPreferences shp;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 拿到配置文件
		shp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(context,
				com.jess.mobilesafe.receiver.AdminReceiver.class);// 设备管理组件
		// 检查防盗保护是否开启
		boolean protect = shp.getBoolean("protect", false);
		// 如果用户设置了防盗保护在开始下面的功能
		if (protect) {
			// 拿到短信主体
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				// 收到的短信内容
				String msg = sms.getMessageBody();
				// 来源电话号码
				String originatingAddress = sms.getOriginatingAddress();
				if ("#*location*#".equals(msg)) {
					// 拦截短信
					abortBroadcast();
					// GPS追踪
					// 打开位置服务寻找位置信息，获得信息好储存到配置文件中，然后立即关闭节约资源
					Intent it = new Intent(context, LocationService.class);
					context.startService(it);
					// 获取配置文件中上次读取的位置
					String location = shp.getString("location",
							"get location......");
					System.out.println(location);
					SmsManager sm = SmsManager.getDefault();
					// 发送位置短信
					sm.sendTextMessage(originatingAddress, null, location,
							null, null);

				} else if ("#*alarm*#".equals(msg)) {
					// 拦截短信
					abortBroadcast();
					// 播放报警音乐
					MediaPlayer player = MediaPlayer
							.create(context, R.raw.ylzs);
					// 设置音量
					player.setVolume(1f, 1f);
					// 设置单曲循环
					player.setLooping(true);
					// 开始播放
					player.start();

				} else if (msg.contains("#*wipedata*#,")) {
					// 拦截短信
					abortBroadcast();
					// 远程删除数据
					if (msg.split(",")[1].equals(shp.getString("phone", ""))) {
						//如果安全号码正确才能消除数据
						if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
							mDPM.wipeData(0);// 清除数据,恢复出厂设置
						}
					}
				} else if (msg.contains("#*lockscreen*#,")) {
					// 拦截短信
					abortBroadcast();
					//如果安全号码正确
					if (msg.split(",")[1].equals(shp.getString("phone", ""))) {
						// 远程锁屏
						if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
							mDPM.lockNow();// 立即锁屏
							//如果有短信里有密码就设置开机密码
							if (msg.split(",").length>=3) {
								if (!TextUtils.isEmpty(msg.split(",")[2])) {
									mDPM.resetPassword(msg.split(",")[2], 0);
								}
							}
						}
					}
				}
			}
		}
	}

}
