package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends BaseTouch {
	private SharedPreferences shp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 拿到配置信息查看是否设置过向导
		shp = getSharedPreferences("config", MODE_PRIVATE);
		checkConfiged();
		
	}

	/**
	 * 判断是否进入向导页
	 */
	public void checkConfiged() {
		boolean configed = shp.getBoolean("configed", false);
		// 设置过就显示页面否则跳转到向导页
		if (configed) {
			// 显示页面
			setContentView(R.layout.activity_lostfind);
			//拿到显示电话的view
			TextView tv = (TextView) findViewById(R.id.tv_lostfind_phone);
			//拿到开启状态的view
			ImageView iv = (ImageView) findViewById(R.id.iv_isok);
			//初始化时检查配置信息
			String phone = shp.getString("phone", null);
			//将配置信息中的安全号码设置到view
			if (!TextUtils.isEmpty(phone)) {
				tv.setText(phone);
			}else{
				//如果没有设置则提示
				tv.setText("");
			}
			//读取配置文件中是否开启保护
			boolean protect = shp.getBoolean("protect", false);
			if (protect) {
				iv.setBackgroundResource(R.drawable.ok);
			}else{
				iv.setBackgroundResource(R.drawable.no);
			}
		} else {
			// 跳转到向导页
			Intent intent = new Intent(this, Step1Activity.class);
			startActivity(intent);
			// 摧毁当前页
			finish();
			// 设置动画
			nextAnimation();
		}
	}

	/**
	 * 重新跳转到向导页
	 * 
	 * @param v
	 */
	public void reEnter(View v) {
		// 跳转到向导页
		Intent intent = new Intent(this, Step1Activity.class);
		startActivity(intent);
		// 摧毁当前页
		finish();
		// 设置动画
		previousAnimation();
	}

	public void nextAnimation() {
		// 设置进入和退出的动画
		overridePendingTransition(R.anim.step_next_in, R.anim.step_next_out);
	}

	public void previousAnimation() {
		// 设置进入和退出的动画
		overridePendingTransition(R.anim.step_previous_in,
				R.anim.step_previous_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// 返回时调用进出动画效果
		previousAnimation();

	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {
		// 跳转到主页
//		Intent intent = new Intent(this, HomeActivity.class);
//		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();

	}
}
