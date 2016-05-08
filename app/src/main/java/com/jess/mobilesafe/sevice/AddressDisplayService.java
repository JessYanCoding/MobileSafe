package com.jess.mobilesafe.sevice;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.dao.AddressDao;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddressDisplayService extends Service {

	private TelephonyManager tm;
	private MyPhoneListener listener;
	private OutCallReceiver receiver;
	private WindowManager wm;
	private View view;
	private SharedPreferences spf;
	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private int wDwidth;
	private int wDhight;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化时调用监听
		displayAddress();
		// 拿到配置文件
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 动态注册去电归属地显示接收器
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		receiver = new OutCallReceiver();
		registerReceiver(receiver, filter);

	}

	/**
	 * 监听电话来电并显示归属地
	 */
	public void displayAddress() {
		// 拿到电话管理器
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 设置电话状态监听
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * 电话状态监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class MyPhoneListener extends PhoneStateListener {
		@Override
		// 电话状态发生改变时调用
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			// 来电铃声时
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				// 查询数据库拿到归属地
				String address = AddressDao.addressQuery(incomingNumber);
				// 显示到屏幕
				show(address);
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				if (wm != null && view != null) {
					// 空闲时移除view
					wm.removeView(view);
				}
			}
		}
	}

	/**
	 * 去电广播接收器
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到电话号码
			String resultData = getResultData();
			// 查询数据库拿到归属地
			String address = AddressDao.addressQuery(resultData);
			// 显示到屏幕
			// Toast.makeText(AddressDisplayService.this, address, 1).show();
			show(address);
		}

	}

	/**
	 * 自定义浮窗
	 */
	public void show(String text) {
		// 拿到配置中心存储的显示坐标
		int x = spf.getInt("lastX", 0);
		int y = spf.getInt("lastY", 0);
		// 获得窗口管理器
		// 闹到屏幕的宽高
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wDwidth = wm.getDefaultDisplay().getWidth();
		wDhight = wm.getDefaultDisplay().getHeight();
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		// 将默认的重心位置从中心，设置到左上方，与设置位置的页面相同。变异量才能吻合
		params.gravity = Gravity.LEFT + Gravity.TOP;
		// 将配置文件中存储的坐标设置上去
		params.x = x;
		params.y = y;
		params.setTitle("Toast");
		// 自定义View
		view = View.inflate(this, R.layout.item_address_display, null);
		// 拿到自定义view中的文本框
		TextView tv_call_address = (TextView) view
				.findViewById(R.id.tv_call_address);
		// 拿到自定义view设置背景
		LinearLayout ll = (LinearLayout) view
				.findViewById(R.id.ll_address_display);
		// 得到配置文件中的风格背景资源id
		int style = spf.getInt("style", 0);
		int[] id = new int[] { R.drawable.call_locate_blue,
				R.drawable.style_transparent, R.drawable.style_orange,
				R.drawable.style_green, R.drawable.style_gray };
		// 设置背景
		ll.setBackgroundResource(id[style]);
		// 设置地址信息
		tv_call_address.setText(text);
		// 设置监听
		view.setOnTouchListener(new MyOnTouchListener());
		// 设置到window上
		wm.addView(view, params);
	}

	class MyOnTouchListener implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			// 点击事件
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 起始的坐标
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				// 移动的位置
				int endX = (int) event.getRawX();
				int endY = (int) event.getRawY();

				// 移动的偏移量
				int x = endX - startX;
				int y = endY - startY;
				// 设置编译后的位置
				params.x += x;
				params.y += y;
				//放在越界
				if (params.x<0) {
					params.x=0;
				}
				if(params.y<0){
					params.y=0;
				}
				if (params.x> wDwidth- view.getWidth()) {
					params.x=wDwidth- view.getWidth();
				}
				if (params.y > wDhight - view.getHeight()) {
					params.y=wDhight - view.getHeight();
				}
				// 更新位置
				wm.updateViewLayout(view, params);
				
				// 将这次的坐标记录
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();

				break;
			case MotionEvent.ACTION_UP:
				// 储存移动后的坐标到配置文件
				int lastX = params.x;
				int lastY = params.y;
				if (lastX >= 0 && lastX <= wDwidth- view.getWidth()
						&& lastY >= 0
						&& lastY <= wDhight - view.getHeight()) {

					spf.edit().putInt("lastX", lastX).commit();
					spf.edit().putInt("lastY", lastY).commit();
				}
				break;

			default:
				break;
			}
			return true;
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 撤销注册接收器
		unregisterReceiver(receiver);
		// 服务销毁时撤销监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}
}
