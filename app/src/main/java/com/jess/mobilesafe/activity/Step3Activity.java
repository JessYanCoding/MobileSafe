package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Step3Activity extends BaseStepActivity {
	private EditText et_step3;
	private SharedPreferences shp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 初始化
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step3);
		// 拿到联系人输入框
		et_step3 = (EditText) findViewById(R.id.et_step3);
		// 获取haredPreferences获得配置信息
		shp = getSharedPreferences("config", MODE_PRIVATE);
		// 每次初始化时检查是否储存phone
		String phone = shp.getString("phone", null);
		if (!TextUtils.isEmpty(phone)) {
			// 如果电话不为空就显示
			et_step3.setText(phone);
		}
	}

	public void enterContact(View v) {
		// 跳转到contact页面显示联系人
		Intent intent = new Intent(this, ContactActivity.class);
		startActivityForResult(intent, 1);
		// 进入动画
		nextAnimation();
	}

	@Override
	public void showNextPage() {
		// 将号码空格去除
		String phone = et_step3.getText().toString().trim();
		// 必须输入安全号码才能到下一页
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "安全号码不能为空", 0).show();
			return;
		}
		// 将输入的号码储存到配置文件中
		shp.edit().putString("phone", phone).commit();
		// 跳转到下第4页
		Intent intent = new Intent(this, Step4Activity.class);
		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		nextAnimation();

	}

	@Override
	public void showPreviousPage() {
		// 跳转到下第2页
		Intent intent = new Intent(this, Step2Activity.class);
		startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();

	}

	/**
	 * 拿到contact的联系人电话
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 如果结果码正确则开始设置
		if (resultCode == 2) {
			// 从intent中读取封装电话号码
			String phone = data.getStringExtra("phone");
			// 处理电话的空格和横岗
			//细节处理，非空验证
			if (!TextUtils.isEmpty(phone)) {
				phone = phone.replaceAll("-", "").replaceAll(" ", "");
				et_step3.setText(phone);
				
			}
		}
	}

}
