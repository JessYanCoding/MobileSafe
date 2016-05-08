package com.jess.touch;

import java.util.ArrayList;
import java.util.List;

import com.jess.mobilesafe.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;


public class ProcessInfoProvider {
	/**
	 * 得到所有的进程信息
	 * @param context 上下门获取包和activty管理器
	 * @return 返回装有所有进程信息的List集合
	 */
		public static List<ProcessInfo> getProcessInfo(Context context){
			//创建List储存所有进程信息
			List<ProcessInfo> infos = new ArrayList<ProcessInfo>();
			// 获取activity管理器
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			//得到所有运行的进程组成的List
			List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
			//遍历List得到需要的信息
			for (RunningAppProcessInfo runinfo: processes) {
				//创建processinfo储存每个进程信息
				ProcessInfo info = new ProcessInfo();
				//进程名，也就是包名
				String packageName = runinfo.processName;
				info.setPackageName(packageName);
				//进程的内存信息
				MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{runinfo.pid})[0];
				long memory = memoryInfo.getTotalPrivateDirty()*1024;
				info.setMemory(memory);
				
				try {
					//得到包管理器
					PackageManager pm = context.getPackageManager();
					//传入包名得到这个包的所有信息
					PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
					//得到应用名
					String name = packageInfo.applicationInfo.loadLabel(pm).toString();
					info.setName(name);
					//得到图标
					Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
					info.setIcon(icon);
					//是否为用户进程
					int flags = packageInfo.applicationInfo.flags;
					if ((flags&ApplicationInfo.FLAG_SYSTEM)==0) {
						info.setUser(true);
					}else{
						info.setUser(false);
					}
					
					
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					//如果报错，说明包名不正确是c系统文件，所以使用默认的包名设置为应用名，使用默认图标
					info.setName(packageName);
					info.setIcon(context.getResources().getDrawable(R.drawable.process_dafault));
				}
				
				//将每个进程的信息储存入集合
				infos.add(info);
			}
			return infos;
		}
}
