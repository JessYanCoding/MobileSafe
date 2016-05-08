package com.jess.mobilesafe.view;

import com.jess.mobilesafe.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseSettingView2 extends RelativeLayout {
	private TextView tv_base_message2;
	private TextView tv_base_title2;

	// 有style时得用
	public BaseSettingView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initiative(context);
	}

	// 有属性时调用
	public BaseSettingView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initiative(context);
	}

	// 直接new时调用
	public BaseSettingView2(Context context) {
		super(context);
		initiative(context);
	}

	// 初始化信息
	public void initiative(Context context) {
		// 将布局填充到view中作为子类，以后更好控制和调用
		View.inflate(getContext(), R.layout.view_basesetting2, this);
		tv_base_message2 = (TextView) findViewById(R.id.tv_base_message2);
		tv_base_title2 = (TextView) findViewById(R.id.tv_base_title2);

	}


	public void setTitle(String title) {
		// 设置标题信息
		tv_base_title2.setText(title);
	}
	public void setDesc(String description) {
		// 设置描述信息
		tv_base_message2.setText(description);
	}

}
