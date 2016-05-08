package com.jess.mobilesafe.sevice;


import com.jess.mobilesafe.R;
import com.jess.mobilesafe.receiver.AdminReceiver;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LockService extends Service {
	private WindowManager wm;
	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private int wDwidth;
	private int wDhight;
	private View view;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	long[] mHits = new long[1];// 数组长度表示要点击的次数
	long[] mHits2 = new long[1];// 数组长度表示要点击的次数

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(this,AdminReceiver.class);// 设备管理组件
		show();
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wm.removeView(view);
	}
	/**
	 * 自定义浮窗
	 */
	public void show() {
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
		params.setTitle("Toast");
		// 自定义View
		view = View.inflate(this, R.layout.view_lock, null);
//		ImageView iv =  (ImageView) view.findViewById(R.id.iv_rocket);
//		iv.setBackgroundResource(R.drawable.rocket);
//		AnimationDrawable ad= (AnimationDrawable) iv.getBackground();
//		ad.start();
		ImageView iv_left =  (ImageView) view.findViewById(R.id.iv_left);
		ImageView iv_right =  (ImageView) view.findViewById(R.id.iv_right);
		// 设置监听
		view.setOnTouchListener(new MyOnTouchListener());
		iv_left.setOnClickListener(new MyLeftListener());
		iv_right.setOnClickListener(new MyRightListener());
		// 设置到window上
		wm.addView(view, params);
	}
	/**
	 * 点击监听
	 */
	class MyLeftListener implements OnClickListener{

		public void onClick(View v) {
			// 双击居中
			System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
			mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
			if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					Intent i= new Intent(Intent.ACTION_MAIN);
				    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    i.addCategory(Intent.CATEGORY_HOME);
				    startActivity(i);
			}
			
			
		}
		
	}
	class MyRightListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.arraycopy(mHits2, 1, mHits2, 0, mHits2.length - 1);
			mHits2[mHits2.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
			if (mHits2[0] >= (SystemClock.uptimeMillis() - 500)) {
				if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
					mDPM.lockNow();// 立即锁屏
				}
			}
			
		}
		
	}
	/**
	 * 触碰监听
	 * @author Administrator
	 *
	 */
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
				break;

			default:
				break;
			}
			return true;
		}
	}

}
