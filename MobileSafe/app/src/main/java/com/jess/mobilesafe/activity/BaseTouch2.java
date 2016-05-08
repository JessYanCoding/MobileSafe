package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.BaseStepActivity.MyDestureListener;
import com.jess.mobilesafe.activity.Home2Activity.MyAdapter2;
import com.jess.mobilesafe.activity.Home2Activity.MyListener2;
import com.jess.mobilesafe.util.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BaseTouch2 extends Activity {
	private GestureDetector gd;
	
	String[] itemName2 = new String[] { "手机防盗", "进程管理", "高级工具", "设置中心" };
	int[] itemR2 = new int[] { R.drawable.sjfd, R.drawable.h7cql,
			R.drawable.g8jgj, R.drawable.s9zzx};
	private SharedPreferences shp;
	private SlidingDrawer sd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置手势监听
		gd = new GestureDetector(this, new MyDestureListener());
		// 获取haredPreferences获得配置信息
		shp = getSharedPreferences("config", MODE_PRIVATE);
		
	}
	public void setSD(SlidingDrawer sd){
		this.sd=sd;
	}

	/**
	 * 监听
	 * 
	 * @author Administrator
	 * 
	 */
	class MyListener2 implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				// 手机防盗
				// 弹出对话框输入密码
				showdialog();
				break;

			case 1:
				// 进程管理
				startActivity(new Intent(BaseTouch2.this,
						ProcessManageActivity.class));
				enterAndEndAnimation();
				break;

			case 2:
				// 高级工具
				startActivity(new Intent(BaseTouch2.this, ToolActivity.class));
				enterAndEndAnimation();
				break;
			case 3:
				// 设置中心
				startActivity(new Intent(BaseTouch2.this,
						SettingActivity.class));
				enterAndEndAnimation();
				break;

			}

		}

	}
	/**
	 * 适配器
	 * @author Administrator
	 *
	 */
	class MyAdapter2 extends BaseAdapter {
		// 网格数量
		public int getCount() {
			return itemName2.length;
		}
		
		public Object getItem(int position) {
			return itemName2[position];
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		// 设置展示的每个itemview
		public View getView(int position, View convertView, ViewGroup parent) {
			// 填充item布局xml
			View v = View.inflate(BaseTouch2.this, R.layout.gridview_item2,
					null);
			// 设置应用名，和图片
			ImageView iv = (ImageView) v.findViewById(R.id.iv_item);
			TextView tv = (TextView) v.findViewById(R.id.tv_item);
			iv.setImageResource(itemR2[position]);
			tv.setText(itemName2[position]);
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
						Toast.makeText(BaseTouch2.this, "登陆成功", 0).show();
						// 成功登陆后跳转到手机防盗页面
						dialog.dismiss();
						Intent intent = new Intent(BaseTouch2.this,
								LostFindActivity.class);
						startActivity(intent);
						// 跳转动画
						enterAndEndAnimation();
					} else {
						// 提示用户密码不正确
						Toast.makeText(BaseTouch2.this, "密码不正确", 0).show();
					}
					// 密码为空提示用户
				} else {
					Toast.makeText(BaseTouch2.this, "密码不能为空", 0).show();
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
						Toast.makeText(BaseTouch2.this, "登陆成功", 0).show();
						// 成功登陆后跳转到手机防盗页面
						dialog.dismiss();
						Intent intent = new Intent(BaseTouch2.this,
								LostFindActivity.class);
						startActivity(intent);
						// 跳转动画
						enterAndEndAnimation();
					} else {
						// 提示用户密码和确认密码不匹配
						Toast.makeText(BaseTouch2.this, "密码和确认密码不匹配", 0)
								.show();
					}
					// 密码和确认密码为空提示用户
				} else {
					Toast.makeText(BaseTouch2.this, "密码或确认密码不能为空", 0).show();
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
	 * 检测menu键打开抽屉
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==KeyEvent.KEYCODE_MENU) {
			
			if (sd.isOpened()) {
				sd.close();
			}else if (!sd.isOpened()) {
				
				sd.open();
			}
			
			
				
		}
		return super.onKeyDown(keyCode, event);
		
	}
	/**
	 * 进入和退出动画
	 */
	public void enterAndEndAnimation() {
		// 设置进入和退出的动画
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);
	}


	/**
	 * 实现下一步按钮点击方法
	 * 
	 * @param v
	 */
	class MyDestureListener extends SimpleOnGestureListener {
		// 监听滑动手势，e1滑动的起点，e2滑动的终点,velocityX水平速度，垂直速度
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// 判断纵向坐标是否超过100，提示手势不正确
			// if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
			// Toast.makeText(BaseTouch.this, "滑动的角度不能贴斜哦！", 0).show();
			// return true;
			// }

			// 向上滑动退出当前页
			if (e1.getRawY() - e2.getRawY() > 250) {

				exit();
			}
			// 判断横向滑动速度是否太慢了
			if (Math.abs(velocityX) < 50) {
				Toast.makeText(BaseTouch2.this, "滑动的太慢了哦！", 0).show();
				return true;
			}
			// 向右滑动，展示上一页
			if (e2.getRawX() - e1.getRawX() > 200) {
				showPreviousPage();
				return true;
			}
			// 向左滑动，展示下一页
			if (e1.getRawX() - e2.getRawX() > 200) {
				showNextPage();
				return true;
			}

			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}

	/**
	 * 跳转到下一页
	 */
	public void exit() {
		// 设置退出的动画
		finish();
		overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
	}

	public abstract void showNextPage();

	/**
	 * 跳转到上一页
	 */
	public abstract void showPreviousPage();

	/**
	 * 进入和退出动画
	 */
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
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将组件触摸事件传递给gesture
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
