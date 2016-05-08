package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.util.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends BaseTouch {
	private GridView gv;
	// 应用名
	String[] itemName = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",

	"手机杀毒", "缓存清理", "高级工具", "设置中心" };
	// 应用图片ID
	int[] itemR = new int[] { R.drawable.sjfd, R.drawable.t2xws,
			R.drawable.r3jgl, R.drawable.h7cql, R.drawable.l5ltj,
			R.drawable.s6jsd, R.drawable.j4cgl, R.drawable.g8jgj,
			R.drawable.s9zzx, };
	private SharedPreferences shp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置整个页面的布局
		setContentView(R.layout.activity_home);
		// 获取haredPreferences获得配置信息
		shp = getSharedPreferences("config", MODE_PRIVATE);
		// 拿到gridview并设置适配器
		gv = (GridView) findViewById(R.id.gv_home);
		MyAdapter adapter = new MyAdapter();
		gv.setAdapter(adapter);
		// 设置item的点击事件
		gv.setOnItemClickListener(new MyListener());

	}

	// item点击监听器
	class MyListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				// 弹出对话框输入密码
				showdialog();
				break;

			case 1:
				// 通讯卫士
				startActivity(new Intent(HomeActivity.this,
						CallSafeActivity.class));
				enterAndEndAnimation();
				break;
			case 2:
				// 软件管理
				startActivity(new Intent(HomeActivity.this,
						APPManageActivity.class));
				enterAndEndAnimation();
				break;

			case 3:
				// 进程管理
				startActivity(new Intent(HomeActivity.this,
						ProcessManageActivity.class));
				enterAndEndAnimation();
				break;
			case 4:

				break;

			case 5:
				break;
			case 6:

				break;

			case 7:
				// 高级工具
				startActivity(new Intent(HomeActivity.this, ToolActivity.class));
				enterAndEndAnimation();
				break;
			case 8:
				// 8,设置中心
				startActivity(new Intent(HomeActivity.this,
						SettingActivity.class));
				enterAndEndAnimation();
				break;

			}

		}

	}

	/**
	 * 自定义网格适配器
	 * 
	 * @author Administrator
	 * 
	 */

	class MyAdapter extends BaseAdapter {
		// 网格数量
		public int getCount() {
			return itemName.length;
		}

		public Object getItem(int position) {
			return itemName[position];
		}

		public long getItemId(int position) {
			return position;
		}

		// 设置展示的每个itemview
		public View getView(int position, View convertView, ViewGroup parent) {
			// 填充item布局xml
			View v = View.inflate(HomeActivity.this, R.layout.gridview_item,
					null);
			// 设置应用名，和图片
			ImageView iv = (ImageView) v.findViewById(R.id.iv_item);
			TextView tv = (TextView) v.findViewById(R.id.tv_item);
			iv.setImageResource(itemR[position]);
			tv.setText(itemName[position]);
			// 返回设置好的Item
			return v;

		}

	}

	/**
	 * 判断是否弹出 对话框设置密码
	 */

	public void showdialog() {
		// 从配置信息中获得密码
		String savepass = shp.getString("pass", null);
		// 如果密码不为空这直接输入密码
		if (!TextUtils.isEmpty(savepass)) {
			inputDialog(savepass);
		} else {
			// 如果密码为空这提示设置密码
			setDialog();
		}
	}

	/**
	 * 输入对话框
	 */
	public void inputDialog(final String savepass) {
		Builder builder = new Builder(this);
		// 得到Dialog
		final AlertDialog dialog = builder.create();
		// 将自定义dialog填充成view
		View v = View.inflate(this, R.layout.dialoginput, null);
		// 将自定义view设置给dialog,并且去边框保证2.x能达到效果
		dialog.setView(v, 0, 0, 0, 0);
		// 拿到editview
		final EditText et_dialogps = (EditText) v
				.findViewById(R.id.et_dialogps);
		// 获得确定和取消Button
		Button bt_ok = (Button) v.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
		// 给确定键设置监听
		bt_ok.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 获取密码信息
				String pass = et_dialogps.getText().toString();
				// 判断密码和确定密码是否正确，是否为空
				if (!TextUtils.isEmpty(pass)) {

					if (MD5Utils.encode(pass).equals(savepass)) {
						Toast.makeText(HomeActivity.this, "登陆成功", 0).show();
						// 成功登陆后跳转到手机防盗页面
						dialog.dismiss();
						Intent intent = new Intent(HomeActivity.this,
								LostFindActivity.class);
						startActivity(intent);
						// 跳转动画
						enterAndEndAnimation();
					} else {
						// 提示用户密码不正确
						Toast.makeText(HomeActivity.this, "密码不正确", 0).show();
					}
					// 密码为空提示用户
				} else {
					Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
				}
			}
		});

		// 给取消键设置监听

		bt_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 如果按取消键则隐藏dialog
				dialog.dismiss();
			}
		});
		// 显示
		dialog.show();

	}

	/**
	 * 设置对话框
	 */
	public void setDialog() {
		Builder builder = new Builder(this);
		// 得到Dialog
		final AlertDialog dialog = builder.create();
		// 将自定义dialog填充成view
		View v = View.inflate(this, R.layout.dialog, null);
		// 将自定义view设置给dialog,并且去边框保证2.x能达到效果
		dialog.setView(v, 0, 0, 0, 0);
		// 拿到editview
		final EditText et_dialogps = (EditText) v
				.findViewById(R.id.et_dialogps);
		// 拿到确认密码的editview
		final EditText et_dialogcps = (EditText) v
				.findViewById(R.id.et_dialogcps);
		// 获得确定和取消Button
		Button bt_ok = (Button) v.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
		// 给确定键设置监听
		bt_ok.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 获取密码信息
				String pass = et_dialogps.getText().toString();
				String cpass = et_dialogcps.getText().toString();
				// 判断密码和确定密码是否正确，是否为空
				if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cpass)) {

					if (pass.equals(cpass)) {
						// 达到要求后吧密码保存到配置信息中
						shp.edit().putString("pass", MD5Utils.encode(pass))
								.commit();
						Toast.makeText(HomeActivity.this, "登陆成功", 0).show();
						// 成功登陆后跳转到手机防盗页面
						dialog.dismiss();
						Intent intent = new Intent(HomeActivity.this,
								LostFindActivity.class);
						startActivity(intent);
						// 跳转动画
						enterAndEndAnimation();
					} else {
						// 提示用户密码和确认密码不匹配
						Toast.makeText(HomeActivity.this, "密码和确认密码不匹配", 0)
								.show();
					}
					// 密码和确认密码为空提示用户
				} else {
					Toast.makeText(HomeActivity.this, "密码或确认密码不能为空", 0).show();
				}
			}
		});

		// 给取消键设置监听

		bt_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 如果按取消键则隐藏dialog
				dialog.dismiss();
			}
		});
		// 显示
		dialog.show();
	}

	/**
	 * 进入和退出动画
	 */
	public void enterAndEndAnimation() {
		// 设置进入和退出的动画
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);
	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {

	}
}
