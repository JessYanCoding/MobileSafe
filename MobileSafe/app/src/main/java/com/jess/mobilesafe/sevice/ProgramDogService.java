package com.jess.mobilesafe.sevice;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jess.mobilesafe.activity.ProgramDogActivity;
import com.jess.mobilesafe.dao.LockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class ProgramDogService extends Service {
	private LockDao dao;
	private List<String> infos;
	private Timer timer;
	private TimerTask task;
	private ActivityManager am;
	private String beforetask="";
	private Myobserver observer;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dao = new LockDao(this);
		
		infos = dao.queryAll();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		observer = new Myobserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://com.jess.change"), true, observer);
		timer= new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				
				RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
				String packageName = taskInfo.topActivity.getPackageName();
				System.out.println(packageName);
				if (!packageName.equals(beforetask)) {
					if (!packageName.equals(getPackageName())) {
						beforetask = packageName;
						if (infos.contains(packageName)) {
							Intent intent = new Intent(ProgramDogService.this,ProgramDogActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("package", packageName);
							startActivity(intent);
						}
					}
					
				}
				
			}
		};
		
		timer.schedule(task, 0, 100);
	}
	
	class Myobserver extends ContentObserver{

		public Myobserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			infos = dao.queryAll();
		}
		
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (dao!=null) {
			dao.close();
			dao=null;
		}
		
		if (timer!=null&&task!=null) {
			timer.cancel();
			task.cancel();
			timer=null;
			task=null;
			
		}
		if (observer!=null) {
			getContentResolver().unregisterContentObserver(observer);
			observer = null;
		}
		
	}
}
