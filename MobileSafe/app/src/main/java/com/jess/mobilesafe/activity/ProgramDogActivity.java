package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.util.MD5Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramDogActivity extends Activity {
	private TextView et_dog_input;
	private SharedPreferences spf;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_programdog);
			
			// 拿到SharedPreferences进行相应的配置
			spf = getSharedPreferences("config", Context.MODE_PRIVATE);
			
			et_dog_input = (TextView) findViewById(R.id.et_dog_input);
			
			TextView tv_dog_name = (TextView) findViewById(R.id.tv_dog_name);
			ImageView iv_dog_icon = (ImageView) findViewById(R.id.iv_dog_icon);
			
			String packageName = getIntent().getStringExtra("package");
			
			try {
				String name = getPackageManager().getPackageInfo(packageName, 0).applicationInfo.loadLabel(getPackageManager()).toString();
				Drawable icon = getPackageManager().getPackageInfo(packageName, 0).applicationInfo.loadIcon(getPackageManager());
				tv_dog_name.setText(name);
				iv_dog_icon.setBackgroundDrawable(icon);
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void ok(View v) {
			String password = et_dog_input.getText().toString().trim();
			if (!TextUtils.isEmpty(password)
					&& MD5Utils.encode(password).equals(
							spf.getString("lockpassword", ""))) {
				finish();
				overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
				
			} else {
				Toast.makeText(this, "密码错误", 0).show();
			}
		}
		
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			finish();
		}
		@Override
		public void onBackPressed() {
			Toast.makeText(this, "请输入密码,或者按home键退回到桌面", 0).show();
		}
}
