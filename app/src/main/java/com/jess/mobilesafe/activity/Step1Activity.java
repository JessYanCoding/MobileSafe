package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Step1Activity extends BaseStepActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step1);

	}

	@Override
	public void showNextPage() {
		// 跳转到下第2页
		Intent intent = new Intent(this, Step2Activity.class);
		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		nextAnimation();
	}

	@Override
	public void showPreviousPage() {

	}

}
