package com.jess.mobilesafe.sevice;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.ToolActivity;
import com.jess.mobilesafe.receiver.AppWidgetClean;
import com.jess.mobilesafe.sevice.KillProcessService.LockKillReceiver;
import com.jess.mobilesafe.util.ProcessInfoUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetService extends Service {
	private AppWidgetManager am;// widget管理器
	private Timer timer;// 计时器
	private TimerTask task;// 定时任务
	private CleanReceiver receiver;
	private AutoOpenReceiver opreceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 创建
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 创建管理器
		am = AppWidgetManager.getInstance(this);
		// 创建定时器和定时任务
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				updateWidget();
			}
		};
		// 设置立即开始定时任务，4秒间隔一次
		timer.schedule(task, 0, 4000);
		// 注册清理内存的广播接受者

		receiver = new CleanReceiver();
		IntentFilter filter = new IntentFilter("com.jess.clean");
		registerReceiver(receiver, filter);
		// 创建关屏和开屏接收器对象
		opreceiver = new AutoOpenReceiver();
		IntentFilter filters = new IntentFilter();
		// 添加关屏和开屏的action
		filters.addAction(Intent.ACTION_SCREEN_OFF);
		filters.addAction(Intent.ACTION_SCREEN_ON);
		// 注册
		registerReceiver(opreceiver, filters);
	}

	/**
	 * 更新widget
	 */
	public void updateWidget() {
		// 创建远程view，传入要布局文件个包名
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.widget_clean);
		// 通过工具类得到总的内存，和剩余内存
		long total = ProcessInfoUtil.getTotalMemory(WidgetService.this);
		long avaiMemory = ProcessInfoUtil.getAvaiMemory(WidgetService.this);
		// 得到所占用的内存
		long possess = total - avaiMemory;
		// 占用百分比
		long result = possess * 100 / total;
		// 设置占用百分比
		views.setTextViewText(R.id.used_num, result + "");
		// 设置进度
		views.setProgressBar(R.id.rom_progressBar1, (int) total, (int) possess,
				false);
		// 给清理键设置清理广播，点击就发送广播
		Intent intent = new Intent("com.jess.clean");
		// 传入意图给延迟意图
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				WidgetService.this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 将延迟意图设置给清理键
		views.setOnClickPendingIntent(R.id.kill_all, pendingIntent);


		// 得到widget组件
		ComponentName provider = new ComponentName(WidgetService.this,
				AppWidgetClean.class);
		// 键远程view和widget组件相关联，并设置到屏幕上
		am.updateAppWidget(provider, views);
	}

	/**
	 * 接收widget的清理广播
	 * 
	 * @author Administrator
	 * 
	 */
	class CleanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到清理的广播
			// 创建activiti管理器
			ActivityManager ams = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningAppProcesses = ams
					.getRunningAppProcesses();
			Toast.makeText(WidgetService.this, "清理完毕", 0).show();
			// 拿到所有在运行的进程信息，遍历进程
			for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				// 传入包名杀死所有在运行的后台进程
				ams.killBackgroundProcesses(runningAppProcessInfo.processName);
				// 立即更新widget给用户更好的体验
				updateWidget();

			}
		}

	}

	/**
	 * 接收开屏和关屏广播，关闭或开启更新
	 * 
	 * @author Administrator
	 * 
	 */
	class AutoOpenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				// 屏幕关闭则停止更新widget,并释放资源
				if (timer != null && task != null) {
					timer.cancel();
					task.cancel();
					timer = null;
					task = null;
				}

			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// 屏幕开启在启动更新widget的状态
				if (timer == null && task == null) {
					// 创建定时器和定时任务
					timer = new Timer();
					task = new TimerTask() {

						@Override
						public void run() {
							updateWidget();
						}
					};
					// 设置立即开始定时任务，4秒间隔一次
					timer.schedule(task, 0, 4000);
				}
			}
		}

	}

	/**
	 * 销毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 如果不为空就释放资源
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
		// 服务结束注销广播接受者
		if (receiver != null) {

			unregisterReceiver(receiver);
			receiver = null;
		}
		// 释放资源
		if (opreceiver != null) {
			unregisterReceiver(opreceiver);
			opreceiver = null;
		}
	}
}
