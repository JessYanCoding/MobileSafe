package com.jess.mobilesafe.test;

import java.util.List;

import com.jess.mobilesafe.domain.AppInfo;
import com.jess.mobilesafe.engine.APPInfoProvider;

import android.test.AndroidTestCase;

public class APPinfoTest extends AndroidTestCase {
		public void appinfo(){
			List<AppInfo> info = APPInfoProvider.getAppInfo(getContext());
			for (AppInfo appInfo : info) {
				System.out.println(appInfo.toString());
				
			}
		}
}
