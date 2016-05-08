package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AddressPositionActivity extends Activity {
	private int startX;
	private int startY;
	private ImageView drag;
	private SharedPreferences spf;
	int WDwidth;
	int WDheight;
	private TextView tv_position_top;
	private TextView tv_position_bottom;
	long[] mHits = new long[2];// 数组长度表示要点击的次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.activity_address_position);
		// 拿到屏幕的的宽度和高度
		WDwidth = getWindowManager().getDefaultDisplay().getWidth();
		WDheight = getWindowManager().getDefaultDisplay().getHeight();
		// 拿到SharedPreferences进行相应的配置
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 拿到drag
		drag = (ImageView) findViewById(R.id.iv_drag);
		// 拿到上下两个标题
		tv_position_top = (TextView) findViewById(R.id.tv_position_top);
		tv_position_bottom = (TextView) findViewById(R.id.tv_position_bottom);
		// 初始化drag的位置
		ininDrag();
		// 给drag设置触碰监听
		drag.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// 双击居中
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 把图片居中
					drag.layout(WDwidth / 2 - drag.getWidth() / 2,
							drag.getTop(), WDwidth / 2 + drag.getWidth()
									/ 2, drag.getBottom());
					int lastX = drag.getLeft();
					int lastY = drag.getTop();
					// 将居中后的坐标提交
					spf.edit().putInt("lastX", lastX).commit();
					spf.edit().putInt("lastY", lastY).commit();
				}
				
			}
		});
		drag.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				// 触碰事件
				int action = event.getAction();
				switch (action) {
				// 手指按下
				case MotionEvent.ACTION_DOWN:
					// 起始的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动的位置
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 移动的偏移量
					int x = endX - startX;
					int y = endY - startY;

					// 原来的左右上下位置加上偏移量
					int l = drag.getLeft() + x;
					int r = drag.getRight() + x;
					int t = drag.getTop() + y;
					int b = drag.getBottom() + y;

					// 防止坐标越界
					if (l < 0 || r > WDwidth) {
						break;
					}
					if(t > WDheight - drag.getHeight()-35){
						break;
					}
					if(t<0){
						break;
					}
					// 根据drag的位置显示或隐藏上或者下提示框
					if (t > WDheight / 2) {
						tv_position_top.setVisibility(View.VISIBLE);
						tv_position_bottom.setVisibility(View.INVISIBLE);
					} else {
						tv_position_top.setVisibility(View.INVISIBLE);
						tv_position_bottom.setVisibility(View.VISIBLE);
					}
					// 更新位置
					drag.layout(l, t, r, b);
					// 将这次的坐标记录
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 储存移动后的坐标到配置文件
					int lastX = drag.getLeft();
					int lastY = drag.getTop();
					if (lastX >= 0 && lastX <= WDwidth - drag.getWidth()
							&& lastY >= 0
							&& lastY <= WDheight - drag.getHeight()-35) {

						spf.edit().putInt("lastX", lastX).commit();
						spf.edit().putInt("lastY", lastY).commit();
					}

					break;

				}
				return false;
			}
		});
	}

	/**
	 * 初始化drag的位置
	 */
	public void ininDrag() {
		// 拿到配置文件中记录的坐标
		int x = spf.getInt("lastX", 0);
		int y = spf.getInt("lastY", 0);
		// 根据drag的位置显示或隐藏上或者下提示框
		if (y > WDheight / 2) {
			tv_position_top.setVisibility(View.VISIBLE);
			tv_position_bottom.setVisibility(View.INVISIBLE);
		} else {
			tv_position_top.setVisibility(View.INVISIBLE);
			tv_position_bottom.setVisibility(View.VISIBLE);
		}
		// 拿到布局参数信息
		RelativeLayout.LayoutParams rlparm = (RelativeLayout.LayoutParams) drag
				.getLayoutParams();
		// 更新布局位置
		// 设置左边距
		rlparm.leftMargin = x;
		// 设置上边距
		rlparm.topMargin = y;
		// 重新设置位置
		drag.setLayoutParams(rlparm);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// 返回时调用进出动画效果
		overridePendingTransition(R.anim.step_previous_in,
				R.anim.step_previous_out);

	}
}
