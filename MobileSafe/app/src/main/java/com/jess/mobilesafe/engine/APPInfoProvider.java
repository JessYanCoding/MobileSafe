package com.jess.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.jess.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class APPInfoProvider {
	public static List<AppInfo> getAppInfo(Context context) {
		// 创建List储存所有包的信息
		List<AppInfo> infos = new ArrayList<AppInfo>();
		// 得到包管理器
		PackageManager manager = context.getPackageManager();
		// 得到所有包的信息
		List<PackageInfo> list = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		// 遍历所有包的信息
		for (PackageInfo packageInfo : list) {
			// 创建javabean储存每个包的信息
			AppInfo info = new AppInfo();
			// 包名
			String packageName = packageInfo.packageName;
			info.setPackageName(packageName);
			// 应用名
			String name = packageInfo.applicationInfo.loadLabel(manager)
					.toString();
			info.setName(name);
			// 得到应用图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
			info.setIcon(icon);

			int flags = packageInfo.applicationInfo.flags;

			if ((flags & packageInfo.applicationInfo.FLAG_SYSTEM) == 0) {
				// 如果域和结果为0说明是用户应用

				info.setUser(true);
			} else {
				info.setUser(false);
			}

			if ((flags & packageInfo.applicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 如果域和结果为0说明是在内部储存
				info.setRom(true);
			} else {

				info.setRom(false);
			}
			// 把每个包的信息添加到list集合
			infos.add(info);

		}
		// 返回集合
		return infos;

	}
}
