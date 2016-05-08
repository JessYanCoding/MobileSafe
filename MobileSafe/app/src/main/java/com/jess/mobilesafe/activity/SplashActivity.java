package com.jess.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.R.id;
import com.jess.mobilesafe.R.layout;
import com.jess.mobilesafe.R.menu;
import com.jess.mobilesafe.util.IOUtil;
import com.jess.mobilesafe.util.ServiceState;
import com.jess.touch.RocketService;
import com.jess.touch.TouchCleanActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	// 用来处理的常量
	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERRO = 1;
	protected static final int CODE_Protocol_ERRO = 2;
	protected static final int CODE_IO_ERRO = 3;
	protected static final int CODE_JASONPARSE_ERRO = 4;
	protected static final int CODE_NEW_VERSION = 5;
	protected static final int CODE_NOT_UPDATE = 6;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private Long startT;
	private long endT;
	private String versionName;
	private int versionCode;
	private String description;
	private String downloadUrl;
	private RelativeLayout rl;
	private SharedPreferences spf;
	// handler处理相应的行为
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 显示对话框
			case CODE_UPDATE_DIALOG:
				showDialog();
				break;
			// 错误提示
			case CODE_URL_ERRO:
				Toast.makeText(SplashActivity.this, "网络地址连接错误", 0).show();
				enterHome();
				break;
			case CODE_Protocol_ERRO:
				Toast.makeText(SplashActivity.this, "网络协议错误", 0).show();
				enterHome();
				break;
			case CODE_IO_ERRO:
				Toast.makeText(SplashActivity.this, "网络读取错误", 0).show();
				enterHome();
				break;
			case CODE_JASONPARSE_ERRO:
				Toast.makeText(SplashActivity.this, "json解析错误", 0).show();
				enterHome();
				break;
			// 最新版本不用更新，进入主页
			case CODE_NEW_VERSION:
				enterHome();
				break;
			case CODE_NOT_UPDATE:
				enterHome();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 拿到设备管理器
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(this,
				com.jess.mobilesafe.receiver.AdminReceiver.class);// 设备管理组件
		// 激活设备管理器
		activeDevice();
		// splash设置渐变效果
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1);
		rl = (RelativeLayout) findViewById(R.id.rl_splash);
		aa.setDuration(2000);
		rl.startAnimation(aa);
		//启动touch
		if (!ServiceState.serviceRunning(this, "com.jess.touch.RocketService")) {
			Intent intent = new Intent(this, RocketService.class);
			startService(intent);
		}
		// 拷贝火星地址转换数据库到files文件夹
		copy("axisoffset.dat");
		// 拷贝电话归属地查询数据库到files文件夹
		copy("address.db");
		// 拷贝常用号码数据库到files文件夹
		copy("commonnum.db");
		// 拷贝病毒数据库到files文件夹
		copy("antivirus.db");
		// 动态设置splah的版本号
		TextView tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本号:" + getVersionName());
		// 获得SharedPreferences然后进行相应处理
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 创建快捷图标
		shortcut();
		// 检测版本更新,读取SharedPreferences如果用户设置为更新就更新否则不更新，默认不更新
		if (checkIsUpdate()) {

			getVesionUpdate();
		} else {
			// 发送延时消息确保整splash页面停留至少2秒
			Message msg = handler.obtainMessage();
			msg.what = CODE_NOT_UPDATE;
			handler.sendMessageDelayed(msg, 2000);
		}

	}

	/**
	 * 创建快捷图标
	 */
	public void shortcut() {
		// 查看配置文件中是否已经设置快捷图标
		boolean install = spf.getBoolean("install", false);
		// 设置了就跳出
		if (install) {
			return;
		}
		// 设置安装快捷图标的意图
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 设置名字
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安卓卫士");
		// 设置图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.icon));
		// 设置意图
		Intent it = new Intent("com.jess.home");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, it);
		// 发送广播，创建快捷图标
		sendBroadcast(intent);
		// 创建后就讲配置文件设置印记
		spf.edit().putBoolean("install", true).commit();
	}

	/**
	 * 激活设备管理器
	 */
	public void activeDevice() {
		if (!mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
			// 如果未激活就激活弹出激活页面
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mDeviceAdminSample);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"开启手机防盗功能必须激活此设备管理器");
			startActivity(intent);
		}
	}

	/**
	 * 获取版本更新信息
	 */
	public void getVesionUpdate() {
		// 获取时间
		startT = System.currentTimeMillis();
		new Thread() {

			private Message msg = handler.obtainMessage();

			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					// 设置url地址
					URL url = new URL("http://192.168.0.101/update.json");
					conn = (HttpURLConnection) url.openConnection();
					// 设置连接超时
					conn.setConnectTimeout(5000);
					// 设置读取超时
					conn.setReadTimeout(5000);
					// 设置请求方式
					conn.setRequestMethod("GET");
					// 连接
					conn.connect();
					if (conn.getResponseCode() == 200) {
						// 获取网路读取流
						InputStream in = conn.getInputStream();
						String result = IOUtil.BytyToString(in);
						in.close();
						// 解析Json信息，并封装
						JSONObject jo = new JSONObject(result);
						versionName = (String) jo.get("versionName");
						versionCode = (Integer) jo.get("versionCode");
						description = jo.getString("description");
						downloadUrl = jo.getString("downloadUrl");
						// 有新版本，弹出更新对话框
						if (versionCode > getVersionCode()) {

							msg.what = CODE_UPDATE_DIALOG;

						} else {
							// 已经是最新版本，进入主页
							msg.what = CODE_NEW_VERSION;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = CODE_URL_ERRO;
					// 网络地址连接错误
					e.printStackTrace();
				} catch (ProtocolException e) {
					msg.what = CODE_Protocol_ERRO;
					// 网络协议错误
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = CODE_IO_ERRO;
					// 网络读取错误
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = CODE_JASONPARSE_ERRO;
					// json解析错误
					e.printStackTrace();
				} finally {
					// 如果停留在splash不足两秒，则延长两秒

					endT = System.currentTimeMillis();
					long time = endT - startT;
					if (time < 2000) {
						// handler.sendMessageDelayed(msg, 2000-time);
						try {
							Thread.sleep(2000 - time);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
					if (conn != null) {
						// 关闭网络连接
						conn.disconnect();
					}
				}
			}
		}.start();

	}

	/**
	 * 获取版本名
	 * 
	 * @return 版本名
	 */
	public String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 获取版本号
	 * 
	 * @return 版本号
	 */
	public int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 版本更新对话框
	 */
	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置标题
		builder.setTitle("最新版本:" + versionName);
		// 设置信息
		builder.setMessage(description);
		// 确认按钮
		builder.setPositiveButton("立即更新", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// 下载更新
				download();
			};
		});
		// 取消按钮
		builder.setNegativeButton("残忍拒绝", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// 取消后进入主页
				enterHome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				// 点返回进入主页
				enterHome();

			}
		});
		builder.show();

	}

	/**
	 * 下载新版程序
	 */
	public void download() {
		// 如果SD卡正确安装在执行下载
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			HttpUtils http = new HttpUtils();
			// 下载到SD卡的地址
			String downloadFile = Environment.getExternalStorageDirectory()
			// 设置下载信息
			// 参1，下载url，参2.下载到本地的地址。参3，如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
			// 参4如果从请求返回信息中获取到文件名，下载完成后自动重命名.参5处理回调方法
					+ "/mobilesafe.apk";
			http.download(downloadUrl, downloadFile, false, false,
					new RequestCallBack<File>() {

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							super.onLoading(total, current, isUploading);
							// 下载进度反馈到splash页面
							TextView tv_progress = (TextView) findViewById(R.id.tv_progress);
							// 设置textview可见
							tv_progress.setVisibility(View.VISIBLE);
							// 设置下载进度信息
							tv_progress.setText("下载进度:" + current * 100 / total
									+ "%");
						}

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
							// 下载成功后调用系统安装程序安装
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(arg0.result),
									"application/vnd.android.package-archive");
							startActivityForResult(intent, 1);
							// 开启系统的安装程序进度activity
							// Intent intents = new Intent();
							// intents.setClassName(
							// "com.android.packageinstaller",
							// "com.android.packageinstaller.InstallAppProgress");
							// // startActivity(intents);
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// 下载失败提示用户
							Toast.makeText(SplashActivity.this, "下载失败", 0)
									.show();
							// 失败后返回主页
							enterHome();
						}
					});
		} else {
			// 提示用户sd异常
			Toast.makeText(SplashActivity.this, "SD卡异常，请检查sd卡", 0).show();
			// 进入主页
			enterHome();
		}

	}

	/**
	 * 复制assets里的内容到内部存储的files文件夹
	 * 
	 * @param assetname
	 */
	public void copy(String assetname) {
		// 目标文件位置
		File locFile = new File(getFilesDir(), assetname);
		if (locFile.exists()) {
			return;
		}
		OutputStream out = null;
		InputStream in = null;
		try {
			// assets的文件变成读取流
			in = getAssets().open(assetname);
			// 输出流
			out = new FileOutputStream(locFile);
			byte[] buf = new byte[1024];
			int num = 0;
			// 开始拷贝
			while ((num = in.read(buf)) != -1) {
				out.write(buf, 0, num);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					// 如果读取流不为空则关闭
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// 关闭报错就设置为空，让虚拟机自己回收
					in = null;
				}
			}
			if (out != null) {
				try {
					// 如果输出流不为空则关闭
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// 关闭报错就设置为空，让虚拟机自己回收
					out = null;
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// 安装界面被关闭，强制进入主页
		enterHome();
	}

	/**
	 * 进入主界面
	 */
	public void enterHome() {
		Intent intent = new Intent(this, Home2Activity.class);
		startActivity(intent);
		// 关闭splash
		finish();
		// splash退出效果
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);

	}

	public boolean checkIsUpdate() {
		boolean isupdate = spf.getBoolean("isupdate", false);
		return isupdate;
	}

}
