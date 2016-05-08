package com.jess.mobilesafe.sevice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.jess.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class BlackAbortService extends Service {

	private SMSAbort receiver;// 短信拦截
	private BlackNumberDao bd;// 数据库dao
	private TelephonyManager tm;// 电话服务
	private MyPhoneStateListener listener;// 电话状态监听

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 创建数据库dao
		bd = new BlackNumberDao(this);
		// 服务创建时注册广播接收者
		// 创建广播接收器
		receiver = new SMSAbort();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver, filter);
		// 得到电话服务
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 设置电话状态监听
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * 电话状态监听
	 * 
	 * @author Administrator
	 * 
	 */
	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声状态
				if (bd.queryNumber(incomingNumber)) {
					// 查询拦截模式
					String mode = bd.queryMode(incomingNumber);
					if ("0".equals(mode) || "2".equals(mode)) {
						// 如果模式为电话或者全部拦截，则拦截此电话
						abortCall();
						//因为生成未接日志是异步的所以立即删除日志可能不成功
						// 注册内容观察者删除对应的未接电话记录
						Uri uri = Uri.parse("content://call_log/calls");
						getContentResolver().registerContentObserver(uri, true,new MyContentObserver(new Handler(),incomingNumber) );
					}
				}
				break;

			}
		}
	}
	/**
	 * 内容观察者，观察未接电话
	 */
	class MyContentObserver extends ContentObserver{
		String incomingNumber;
		public MyContentObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			//调用删除未接电话的方法
			deleteRecord(incomingNumber);
			//删除后自动解除注册
			getContentResolver().unregisterContentObserver(this);
		}
		
		
	}
	/**
	 * 短信拦截
	 * 
	 * @author Administrator
	 * 
	 */
	class SMSAbort extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 从Intent解析出短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) object);
				// 得到发送短信的号码
				String phone = msg.getOriginatingAddress();
				// 将号码查询数据库

				if (bd.queryNumber(phone)) {
					// 查询拦截模式
					String mode = bd.queryMode(phone);
					if ("1".equals(mode) || "2".equals(mode)) {
						// 如果模式为短信或者全部拦截，则拦截此短信
						abortBroadcast();
					}
				}
			}

		}

	}

	/**
	 * 电话拦截
	 */
	public void abortCall() {
		//挂断电话
		try {
			//得到字节码
			Class clazz = BlackAbortService.class.getClassLoader().loadClass("android.os.ServiceManager");
			//从字节码里拿到方法
			Method method = clazz.getMethod("getService", String.class);
			//调用方法,拿到iBinder
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			//拷贝aidl，拿到Telephone代理对象
			ITelephony itel = ITelephony.Stub.asInterface(ibinder);
			//调用endcall方法
			itel.endCall();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 使用能容提供者删除未接电话
	 * @param incomingNumber 要删除的未接电话
	 */
	public void deleteRecord(String incomingNumber) {
		// 删除未接电话记录
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 服务销毁时注销广播接收者
		unregisterReceiver(receiver);
		receiver = null;
		// 服务销毁时注销监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		tm = null;
		listener = null;
	}
}
