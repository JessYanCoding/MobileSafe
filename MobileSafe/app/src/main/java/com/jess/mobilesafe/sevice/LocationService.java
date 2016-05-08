package com.jess.mobilesafe.sevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {
	private MyListener listener;
	private LocationManager lm;
	private SharedPreferences shp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 拿到配置文件
		shp = getSharedPreferences("config", Context.MODE_PRIVATE);
		getLocation();
	}

	public void getLocation() {
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 设定标准
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 精确度
		criteria.setCostAllowed(true);// 是否允许网络

		// 拿到最好的定位方法
		String bsetpro = lm.getBestProvider(criteria, true);
		listener = new MyListener();
		// 请求位置更新
		lm.requestLocationUpdates(bsetpro, 0, 0, listener);
	}

	class MyListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// 位置发生改变时调用
			// 获取经纬度并且把把正确经纬度转换成火星坐标
			InputStream in = null;
			try {
				// 拿到转换需要的数据库
				in = new FileInputStream(new File(getFilesDir(),
						"axisoffset.dat"));
				// 开始转换
				PointDouble s2c = LocationChange.S2C(location.getLongitude(),
						location.getLatitude(), in);

				// 如果结果不为空在储存到配置文件中
				if (s2c != null) {
					double j = s2c.x;
					double w = s2c.y;
					// 提交火星坐标
					shp.edit()
							.putString("location", "jindu:" + j + "weidu:" + w)
							.commit();

					stopSelf();// 储存好位置马上停止服务节约电量
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						// 不为空在关闭
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						// 关闭报错则将读取刘设为空，虚拟机自动回收
						in = null;
					}
				}
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 状态改变时调用
		}

		public void onProviderEnabled(String provider) {
			// 位置提供者可用时调用
		}

		public void onProviderDisabled(String provider) {
			// 位置提供者不可用时调用
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 停止获取更新节约资源和电量
		lm.removeUpdates(listener);
	}

}
