package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.sevice.AddressDisplayService;
import com.jess.mobilesafe.sevice.BlackAbortService;
import com.jess.mobilesafe.sevice.LockService;
import com.jess.mobilesafe.sevice.ProgramDogService;
import com.jess.mobilesafe.util.ServiceState;
import com.jess.mobilesafe.view.BaseSettingView;
import com.jess.mobilesafe.view.BaseSettingView2;
import com.jess.touch.RocketService;

import android.animation.AnimatorSet.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingActivity extends BaseTouch {
	private com.jess.mobilesafe.view.BaseSettingView bv_update;
	private SharedPreferences spf;
	private com.jess.mobilesafe.view.BaseSettingView bsv_address;
	private com.jess.mobilesafe.view.BaseSettingView bv_address_boot;
	private com.jess.mobilesafe.view.BaseSettingView2 bv_address_Style;
	private com.jess.mobilesafe.view.BaseSettingView2 bv_address_position;
	private com.jess.mobilesafe.view.BaseSettingView2 bv_boot;
	private com.jess.mobilesafe.view.BaseSettingView bv_lock;
	private com.jess.mobilesafe.view.BaseSettingView bv_lock_boot;
	private com.jess.mobilesafe.view.BaseSettingView bv_black;
	private com.jess.mobilesafe.view.BaseSettingView bv_programdog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 初始化
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		// 拿到更新设置的view
		bv_update = (BaseSettingView) findViewById(R.id.bv_update);
		// 拿到归属地显示的View
		bsv_address = (BaseSettingView) findViewById(R.id.bv_address);
		// 拿到归属地显示开机启动的View
		// bv_address_boot = (BaseSettingView)
		// findViewById(R.id.bv_address_boot);
		// 拿到归属地显示开机启动的View
		bv_address_Style = (BaseSettingView2) findViewById(R.id.bv_address_Style);
		// 拿到归属地提示框位置的View
		bv_address_position = (BaseSettingView2) findViewById(R.id.bv_address_position);
		// 拿到锁屏助手的view
		bv_lock = (BaseSettingView) findViewById(R.id.bv_lock);
		// 拿到锁屏助手开机启动的view
		// bv_lock_boot = (BaseSettingView) findViewById(R.id.bv_lock_boot);
		// 拿到黑名单拦截的View
		bv_black = (BaseSettingView) findViewById(R.id.bv_black);
		// 拿到所有开机启动设置的view
		bv_boot = (BaseSettingView2) findViewById(R.id.bv_boot);
		//拿到程序锁的View
		bv_programdog = (BaseSettingView) findViewById(R.id.bv_programdog);
		// 拿到SharedPreferences进行相应的配置
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 调用更新设置的逻辑
		updateSetting();
		// 初始化归属地显示的方法
		addressDisplay();
		// 归属地开机启动
		// addressDisplayBoot();
		// 归属地提示框风格
		addressStyle();
		// 提示框位置
		addressPosition();
		// 锁屏助手
		lock();
		// 锁屏助手开机启动;
		// lockBoot();
		// 黑名单拦截
		blackAbort();
		// 开机启动设置
		boot();
		// 程序锁
		programLock();
	}

	public void updateSetting() {
		// 设置更新设置的点击监听
		// 拿到checkbox和tv，应为已经是子类了所有可以直接find
		TextView tv_base_message = (TextView) bv_update
				.findViewById(R.id.tv_base_message);
		CheckBox cb_base_setting = (CheckBox) findViewById(R.id.cb_base_setting);
		// 每次初始化时从SharedPreferences读取配置信息进行设置
		// spf.edit().putBoolean("isupdate", false).commit();
		boolean isupdate = spf.getBoolean("isupdate", false);
		// 如果为true就把checkbox设置成true反之为false
		if (isupdate) {
			cb_base_setting.setChecked(true);
		} else {

			cb_base_setting.setChecked(false);
		}
		// 根据配置信息设置描述信息
		bv_update.descriptionChange(isupdate);
		SettingListener listener = new SettingListener();
		// 给自动更新设置监听
		bv_update.setOnClickListener(listener);
	}

	/**
	 * 自动更新的监听
	 * 
	 * @author Administrator
	 * 
	 */

	class SettingListener implements OnClickListener {

		public void onClick(View v) {
			// 设置checkbox勾选状态，将勾选状态储存到SharedPreferences;
			if (bv_update.getCBState()) {
				bv_update.setCBState(false);
				spf.edit().putBoolean("isupdate", false).commit();
			} else {
				bv_update.setCBState(true);
				// 勾选框不勾选，存储false到配置文件
				spf.edit().putBoolean("isupdate", true).commit();
			}
		}

	}

	/**
	 * 归属地显示的初始化信息
	 */
	public void addressDisplay() {
		// 查看归属地显示服务是否开启
		boolean serviceRunning = ServiceState.serviceRunning(this,
				"com.jess.mobilesafe.sevice.AddressDisplayService");
		if (serviceRunning) {
			// 服务如果存在则显示勾选
			bsv_address.setCBState(true);
		} else {
			// 服务不存在则不勾选
			bsv_address.setCBState(false);
		}
		bsv_address.setOnClickListener(new OnClickListener() {

			Intent intent = new Intent(SettingActivity.this,
					AddressDisplayService.class);

			public void onClick(View v) {
				// 如果是勾选就设置为不勾选
				if (bsv_address.getCBState()) {
					bsv_address.setCBState(false);
					// 不勾选状态就停止服务
					stopService(intent);
				} else {
					// 如果是不勾选就设置为勾选
					bsv_address.setCBState(true);
					// 勾选状态就启动服务
					startService(intent);
				}
			}
		});
	}

	/**
	 * 归属地显示开机启动
	 */
	/*
	 * public void addressDisplayBoot() { boolean addressboot =
	 * spf.getBoolean("addressboot", false); // 如果为true就把checkbox设置成true反之为false
	 * if (addressboot) { bv_address_boot.setCBState(true); } else {
	 * 
	 * bv_address_boot.setCBState(false); }
	 * 
	 * bv_address_boot.setOnClickListener(new OnClickListener() { public void
	 * onClick(View v) { // 如果是勾选就设置为不勾选 if (bv_address_boot.getCBState()) {
	 * bv_address_boot.setCBState(false); // 不勾选状态就储存配置文件为不启动
	 * spf.edit().putBoolean("addressboot", false).commit(); } else { //
	 * 如果是不勾选就设置为勾选 bv_address_boot.setCBState(true); // 勾选状态就储存配置文件为启动
	 * spf.edit().putBoolean("addressboot", true).commit(); } } }); }
	 */

	// 所有的风格名
	String[] items = new String[] { "卫士蓝", "半透明", "活力黄", "苹果绿", "金属灰", };

	/**
	 * 归属地显示框
	 */
	public void addressStyle() {
		// 设置标题
		bv_address_Style.setTitle("归属地提示框风格设置");
		// 拿到配置文件储存的风格
		int style = spf.getInt("style", 0);
		// 动态设置描述
		bv_address_Style.setDesc(items[style]);
		// 设置点击监听
		bv_address_Style.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 点击弹出选择风格的单选框
				showDialog();
			}
		});
	}

	/**
	 * 提示框风格单选框
	 * 
	 */
	public void showDialog() {
		// 单刀Builder
		AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		// 设置单选框
		int style = spf.getInt("style", 0);
		builder.setSingleChoiceItems(items, style, new MyStyleLitener());
		// 显示单选框
		builder.show();
	}

	// 为单选框设置监听
	class MyStyleLitener implements
			android.content.DialogInterface.OnClickListener {
		// 单击调用
		public void onClick(DialogInterface dialog, int which) {
			// 将选择的风格存储到配置信息
			spf.edit().putInt("style", which).commit();
			// 动态设置描述
			bv_address_Style.setDesc(items[which]);
			// 隐藏对话框
			dialog.dismiss();
		}

	}

	/**
	 * 提示框位置显示
	 */
	public void addressPosition() {
		// 设置标题
		bv_address_position.setTitle("归属地提示框位置设置");
		bv_address_position.setDesc("拖拉图标设置提示框显示位置");
		// 设置监听
		bv_address_position.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 点击就发送到位置设置页面
				startActivity(new Intent(SettingActivity.this,
						AddressPositionActivity.class));
				// 动画
				nextAnimation();
			}
		});
	}

	/**
	 * 锁屏助手
	 */
	public void lock() {
		boolean serviceRunning = ServiceState.serviceRunning(this,
				"com.jess.touch.RocketService");
		if (serviceRunning) {
			// 服务如果存在则显示勾选
			bv_lock.setCBState(true);
		} else {
			// 服务不存在则不勾选
			bv_lock.setCBState(false);
		}
		bv_lock.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				// 如果是勾选就设置为不勾选
				if (bv_lock.getCBState()) {
					bv_lock.setCBState(false);
					// 不勾选状态就停止服务
					stopService(new Intent(SettingActivity.this,
							RocketService.class));
				} else {
					// 如果是不勾选就设置为勾选
					bv_lock.setCBState(true);
					// 勾选状态就启动服务
					startService(new Intent(SettingActivity.this,
							RocketService.class));
				}
			}
		});
	}

	/**
	 * 锁屏助手开机启动
	 */
	/*
	 * public void lockBoot() { boolean lockboot = spf.getBoolean("lockboot",
	 * false); // 如果为true就把checkbox设置成true反之为false if (lockboot) {
	 * bv_lock_boot.setCBState(true); } else {
	 * 
	 * bv_lock_boot.setCBState(false); }
	 * 
	 * bv_lock_boot.setOnClickListener(new OnClickListener() { public void
	 * onClick(View v) { // 如果是勾选就设置为不勾选 if (bv_lock_boot.getCBState()) {
	 * bv_lock_boot.setCBState(false); // 不勾选状态就储存配置文件为不启动
	 * spf.edit().putBoolean("lockboot", false).commit(); } else { //
	 * 如果是不勾选就设置为勾选 bv_lock_boot.setCBState(true); // 勾选状态就储存配置文件为启动
	 * spf.edit().putBoolean("lockboot", true).commit(); } } }); }
	 */

	/**
	 * 黑名单拦截
	 */
	public void blackAbort() {
		boolean serviceRunning = ServiceState.serviceRunning(this,
				"com.jess.mobilesafe.sevice.BlackAbortService");
		if (serviceRunning) {
			// 服务如果存在则显示勾选
			bv_black.setCBState(true);
		} else {
			// 服务不存在则不勾选
			bv_black.setCBState(false);
		}
		bv_black.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 如果是勾选就设置为不勾选
				if (bv_black.getCBState()) {
					bv_black.setCBState(false);
					// 不勾选状态就停止服务
					stopService(new Intent(SettingActivity.this,
							BlackAbortService.class));
				} else {
					// 如果是不勾选就设置为勾选
					bv_black.setCBState(true);
					// 勾选状态就启动服务
					startService(new Intent(SettingActivity.this,
							BlackAbortService.class));
				}

			}
		});
	}

	/**
	 * 开机启动的设置
	 */
	public void boot() {
		// 多选框的显示和是否勾选
		final String[] items = new String[] { "归属地显示开机启动", "锁屏清理开机启动","黑名单拦截开机启动","程序锁开机启动"};
		final boolean[] check = new boolean[] { false, false,false,false };
		// 设置View标题和描述
		bv_boot.setTitle("开机启动设置");
		bv_boot.setDesc("可以设置所有组件的开机启动");
		// 从配置文件中的得到是否开机启动的信息
		boolean addressboot = spf.getBoolean("addressboot", false);
		boolean lockboot = spf.getBoolean("lockboot", true);
		boolean blackboot = spf.getBoolean("blackboot", false);
		boolean programboot = spf.getBoolean("programboot", false);
		// 将配置文件中的信息复制给多选框
		check[0] = addressboot;
		check[1] = lockboot;
		check[2] = blackboot;
		check[3] = programboot;

		bv_boot.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 创建builder
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingActivity.this);
				// 设置多选框
				builder.setMultiChoiceItems(items, check,
						new OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								// 点击后将值付给check
								check[which] = isChecked;
							}

						});
				// 设置确定按钮的并且监听事件
				builder.setPositiveButton("立即生效",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
									// 查看是否勾选储存到配置文件
									if (check[0]) {
										spf.edit()
												.putBoolean("addressboot", true)
												.commit();
									} else {
										spf.edit()
												.putBoolean("addressboot",
														false).commit();
									}

									
									if (check[1]) {
										spf.edit().putBoolean("lockboot", true)
												.commit();
									} else {
										spf.edit()
												.putBoolean("lockboot", false)
												.commit();
									}
									
									
									
									if (check[2]) {
										spf.edit().putBoolean("blackboot", true)
										.commit();
									} else {
										spf.edit()
										.putBoolean("blackboot", false)
										.commit();
									}
									
									
									
									if (check[3]) {
										spf.edit().putBoolean("programboot", true)
										.commit();
									} else {
										spf.edit()
										.putBoolean("programboot", false)
										.commit();
									}
							}

						});
				// 显示多选框
				builder.show();
			}
		});

	}
	
	/**
	 * 程序锁
	 */
	public void programLock() {
		boolean serviceRunning = ServiceState.serviceRunning(this,
				"com.jess.mobilesafe.sevice.ProgramDogService");
		if (serviceRunning) {
			// 服务如果存在则显示勾选
			bv_programdog.setCBState(true);
		} else {
			// 服务不存在则不勾选
			bv_programdog.setCBState(false);
		}
		bv_programdog.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 如果是勾选就设置为不勾选
				if (bv_programdog.getCBState()) {
					bv_programdog.setCBState(false);
					// 不勾选状态就停止服务
					stopService(new Intent(SettingActivity.this,
							ProgramDogService.class));
				} else {
					// 如果是不勾选就设置为勾选
					bv_programdog.setCBState(true);
					// 勾选状态就启动服务
					startService(new Intent(SettingActivity.this,
							ProgramDogService.class));
				}

			}
		});
	}


	/**
	 * 进入和退出动画
	 */
	public void enterAndEndAnimation() {
		// 设置进入和退出的动画
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// 返回时调用进出动画效果
		enterAndEndAnimation();

	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {
		// 跳转到主页
		// Intent intent = new Intent(this, HomeActivity.class);
		// startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();

	}
}
