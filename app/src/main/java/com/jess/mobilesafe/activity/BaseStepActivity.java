package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public abstract class BaseStepActivity extends Activity {
	private GestureDetector gd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置手势监听
		gd = new GestureDetector(this, new MyDestureListener());
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
			if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
				Toast.makeText(BaseStepActivity.this, "滑动的角度不能贴斜哦！", 0).show();
				return true;
			}

			// 判断横向滑动速度是否太慢了
			if (Math.abs(velocityX) < 100) {
				Toast.makeText(BaseStepActivity.this, "滑动的太慢了哦！", 0).show();
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
	

	public void next(View v) {
		showNextPage();
	}

	/**
	 * 实现下一步按钮点击方法
	 * 
	 * @param v
	 */
	public void previous(View v) {
		showPreviousPage();
	}

	/**
	 * 跳转到下一页
	 */
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
