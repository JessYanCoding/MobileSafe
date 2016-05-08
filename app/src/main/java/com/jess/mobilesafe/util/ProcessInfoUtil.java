package com.jess.mobilesafe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessInfoUtil {
	/**
	 * 获取运行的进程总个数
	 * 
	 * @return 进程个数
	 */
	public static int getProcessCount(Context context) {
		// 获取activity管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	/**
	 * 获取可用的内存
	 * @param context 上下文对象用来获取activity管理器
	 * @return 返回可用的内存数
	 */
	public static long getAvaiMemory(Context context) {
		// 获取activity管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获取所有内存空间
	 * @param context 上下文对象用来获取activity管理器
	 * @return 总的内存数
	 * @throws FileNotFoundException 
	 */
	//4.1系统版本以下智能通过读取系统文件获取总内存
	public static long getTotalMemory(Context context){
		File file = new File("/proc/meminfo");
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String result = br.readLine();
			StringBuffer buf = new StringBuffer();
			for (char c : result.toCharArray()) {
				if (c>='0'&&c<='9') {
					buf.append(c);
				}
			}
			return Integer.valueOf(buf.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo outInfo = new MemoryInfo();
			am.getMemoryInfo(outInfo);
			return outInfo.totalMem;
		}
		
		
		
	}
}
