package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Step4Activity extends BaseStepActivity {
	private SharedPreferences shp;
	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step4);
		// 拿到配置文件
		shp = getSharedPreferences("config", MODE_PRIVATE);
		// 拿到checkbox
		cb = (CheckBox) findViewById(R.id.cb_step4);
		// 设置监听
		cb.setOnCheckedChangeListener(new MyChangeListener());
		// 初始化时检查配置信息中是否开启
		Boolean protect = shp.getBoolean("protect", false);
		if (protect) {
			//如果配置文件为true就开启
			cb.setChecked(true);
		}else{
			//如果配置文件为false就不开启
			cb.setChecked(false);
		}

	}
	//checkchange监听器
	class MyChangeListener implements OnCheckedChangeListener{

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			//当checkbox状态改变调用此方法
			// 如果checkbox为勾选就将他设置为不勾选
			if (isChecked) {
				cb.setText("防盗保护已开启");
				shp.edit().putBoolean("protect", true).commit();
			} else {
				// 如果checkbox为不勾选就将他设置为勾选
				cb.setText("防盗保护没有开启");
				shp.edit().putBoolean("protect", false).commit();
			}
		}
		
	}
	

	@Override
	public void showNextPage() {
		// 将设置成功信息储存到配置文件中下次启动不在设置想到页面
		shp.edit().putBoolean("configed", true).commit();
		// 跳转到手机防盗主页
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		// 关闭页面
		finish();

		// step动画
		nextAnimation();

	}

	@Override
	public void showPreviousPage() {
		// 跳转到下第3页
		Intent intent = new Intent(this, Step3Activity.class);
		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();

	}

}
