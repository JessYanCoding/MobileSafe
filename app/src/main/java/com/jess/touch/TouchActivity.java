package com.jess.touch;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.receiver.AdminReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TouchActivity extends Activity {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	boolean isstart = true;
	private SharedPreferences spf;
	private RelativeLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch);
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
		mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);// 设备管理组件
		ll = (RelativeLayout) findViewById(R.id.ll);
		ImageView iv_home = (ImageView) findViewById(R.id.iv_home);
		ImageView iv_lock = (ImageView) findViewById(R.id.iv_lock);
		ImageView iv_clean = (ImageView) findViewById(R.id.iv_clean);
		ImageView iv_menu = (ImageView) findViewById(R.id.iv_menu);

		iv_home.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addCategory(Intent.CATEGORY_HOME);
				startActivity(i);
				finish();
			}
		});

		iv_lock.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				activeDevice();
				if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
					mDPM.lockNow();// 立即锁屏
					finish();
				}
			}
		});

		iv_clean.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				isstart = false;
				startActivity(new Intent(TouchActivity.this,TouchCleanActivity.class));
				
			}
		});

		ll.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					finish();
				}
				return true;
			}
		});

		iv_menu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Builder builder = new Builder(TouchActivity.this);
				AlertDialog dialog = builder.create();
				View view = View.inflate(TouchActivity.this,
						R.layout.view_dialog, null);
				TextView tv_close = (TextView) view.findViewById(R.id.tv_close);
				TextView tv_unstall = (TextView) view
						.findViewById(R.id.tv_unstall);
				final TextView tv_boot = (TextView) view
						.findViewById(R.id.tv_boot);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
				boolean boot = spf.getBoolean("lockboot", true);
				if (boot) {
					cb.setChecked(true);
					tv_boot.setText("开机启动已开启");
				} else {
					cb.setChecked(false);
					tv_boot.setText("开机启动已关闭");
				}
				tv_close.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						finish();
						isstart = false;
					}
				});

				tv_unstall.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
							mDPM.removeActiveAdmin(mDeviceAdminSample);// 取消激活
						}

						// 卸载程序
						Intent intent = new Intent(Intent.ACTION_DELETE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivity(intent);

					}
				});

				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							tv_boot.setText("开机启动已开启");
							spf.edit().putBoolean("lockboot", true).commit();
						} else {
							tv_boot.setText("开机启动已关闭");
							spf.edit().putBoolean("lockboot", false).commit();
						}
					}
				});

				dialog.setOnCancelListener(new OnCancelListener() {

					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();

			}
		});

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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (isstart) {
			Intent intent = new Intent(TouchActivity.this, RocketService.class);
			startService(intent);

		}
	}

}
