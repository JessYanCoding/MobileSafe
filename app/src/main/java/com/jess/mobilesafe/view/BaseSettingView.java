package com.jess.mobilesafe.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.mobilesafe.R;

public class BaseSettingView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
	private TextView tv_base_message;
	private CheckBox cb;
	private SharedPreferences spf;
	private String title;
	private String on;
	private String off;
	private TextView tv_base_title;

	// 有style时得用
	public BaseSettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initiative(context);
	}

	// 有属性时调用
	public BaseSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 标题的名字
		title = attrs.getAttributeValue(NAMESPACE, "titles");
		// 开启的描述
		on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		// 关闭的描述
		off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		initiative(context);
	}

	// 直接new时调用
	public BaseSettingView(Context context) {
		super(context);
		initiative(context);
	}

	// 初始化信息
	public void initiative(Context context) {
		// 将布局填充到view中作为子类，以后更好控制和调用
		View.inflate(getContext(), R.layout.view_basesetting, this);
		// 拿到checkbox和title和Message，应为已经是子类了所有可以直接find

		tv_base_title = (TextView) findViewById(R.id.tv_base_title);
		tv_base_message = (TextView) findViewById(R.id.tv_base_message);
		cb = (CheckBox) findViewById(R.id.cb_base_setting);
		tv_base_title.setText(title);

	}

	// 提供获得checkbox的状态
	public boolean getCBState() {
		return cb.isChecked();
	}

	// 设置checkbox的状态
	public void setCBState(boolean ischeck) {
		// 设置checkbox为勾选
		cb.setChecked(ischeck);
		// 自动设置描述
		descriptionChange(ischeck);
	}

	/**
	 * 自动设置描述
	 */
	public void descriptionChange(boolean ischeck) {
		if (ischeck) {
			tv_base_message.setText(on);
		} else {
			tv_base_message.setText(off);
		}
	}
}
