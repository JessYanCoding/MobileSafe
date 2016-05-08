package com.jess.mobilesafe.receiver;

import com.jess.mobilesafe.sevice.WidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class AppWidgetClean extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//每30分钟调用一次，所以启动服务，确保widget在，更新服务也在
		context.startService(new Intent(context,WidgetService.class));
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		//创建第一个widget调用，所以开启更新widget的服务
		context.startService(new Intent(context,WidgetService.class));
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		//销毁最后一个widget调用，所有销毁服务
		context.stopService(new Intent(context,WidgetService.class));
	}
		
}
