package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.view.BaseSettingView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Step2Activity extends BaseStepActivity {

	private com.jess.mobilesafe.view.BaseSettingView bsv;
	private SharedPreferences shp;
	String SIMNuber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 初始化
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step2);
		// 拿到配置文件
		shp = getSharedPreferences("config", MODE_PRIVATE);
		// 读取sim序列号
		SIMNuber = shp.getString("SIM", null);
		// 拿到自定义组件并且给饿他设置监听处理逻辑
		bsv = (BaseSettingView) findViewById(R.id.bsv_step2);
		bsv.setOnClickListener(new MyListener());

		// 初始化时如果配置文件中序列号为空则将checkbox也设置为不勾选
		if (TextUtils.isEmpty(SIMNuber)) {
			// 如果checkbox为勾选则设置为不勾选，如果为不勾选则不设置
			if (bsv.getCBState()) {

				bsv.setCBState(false);
			}
		} else {
			// 初始化时如果配置文件中序列号不为空则将checkbox设置为勾选
			bsv.setCBState(true);
		}
	}

	/**
	 * 给自定义组件设置点击事件，做相应处理
	 */
	class MyListener implements OnClickListener {

		public void onClick(View v) {
			// 如果checkbox为勾选就将他设置为不勾选，并且清空SIM信息
			if (bsv.getCBState()) {
				bsv.setCBState(false);
				setSimInfo(false);
			} else {
				// 如果checkbox为不勾选就将他设置为勾选，并且记录SIM信息
				bsv.setCBState(true);
				setSimInfo(true);
			}
		}

	}

	/**
	 * 设置配置文件中的sim信息
	 */
	public void setSimInfo(boolean isSet) {
		// 通过ture和false控制是否把sim序列号储存到配置信息中
		if (isSet) {
			// 如果是true拿到电话管家
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			// 获得sim卡序列号
			String number = tm.getSimSerialNumber();
			// 存入配置文件中
			shp.edit().putString("SIM", number).commit();
		} else {
			// 如果为false就是不存储序列号
			// 先判断配置文件中的序列号是否为空
			// 为空则不做操作
			if (!TextUtils.isEmpty(SIMNuber)) {
				// 不为空这将SIM卡信息清空
				shp.edit().putString("SIM", "").commit();
			}
		}
	}

	/**
	 * 跳转到下一页
	 */
	public void showNextPage() {
		// 判断配置信息中是否有sim序列号，没有则不能进入下一页
		String number = shp.getString("SIM", null);
		// 如果配置信息中序列号不为空就可以跳转
		if (!TextUtils.isEmpty(number)) {
			// 跳转到下第3页
			Intent intent = new Intent(this, Step3Activity.class);
			startActivity(intent);
			// 关闭页面
			finish();
			// step动画
			nextAnimation();
		} else {
			// 否则提示
			Toast.makeText(Step2Activity.this, "进入下一页必须绑定SIM卡", 0).show();
		}
	}

	/**
	 * 跳转到上一页
	 */
	public void showPreviousPage() {
		// 跳转到下第1页
		Intent intent = new Intent(this, Step1Activity.class);
		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();
	}

}
