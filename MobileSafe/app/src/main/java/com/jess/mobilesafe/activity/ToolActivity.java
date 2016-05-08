package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.util.SMSBackUpUtil;
import com.jess.mobilesafe.util.SMSBackUpUtil.ProgressInter;

import android.animation.AnimatorSet.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class ToolActivity extends BaseTouch {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tool);
	}

	/**
	 * 进入归属地查询
	 * 
	 * @param v
	 */
	public void enterAddressQuery(View v) {
		// 进入归属地activity
		Intent intent = new Intent(this, AddressQueryActivity.class);
		startActivity(intent);
		// 动画
		nextAnimation();
	}
	/**
	 * 备份短信
	 * @param v
	 */
	public void backup(View v) {
		// 判断sd卡是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			final ProgressDialog dialog = new ProgressDialog(this);
			// 设置进度条信息
			dialog.setMessage("正在备份中...\n备份文件地址在[SD卡根目录]\n文件名为[SMS_backup]");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// 显示进度条
			dialog.show();
			//耗时操作在子线程里
			new Thread() {
				public void run() {

					try {
						//使用工具栏备份
						SMSBackUpUtil.SMSBackUp(ToolActivity.this,
								new ProgressInter() {
									//继承接口重写方法设置进度条信息，保证核心代码不用重复更改
									public void setMAX(int max) {
										// 设置进度条的最大值
										dialog.setMax(max);
									}

									public void setProgress(int progress) {
										// 设置进度条进度
										dialog.setProgress(progress);
									}

								});
						// 备份完成隐藏进度条
						dialog.dismiss();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}.start();
		}
	}
	/**
	 * 进入常用号码查询页面
	 * @param v 
	 */
	public void commonNumber(View v){
		//跳转到常用号码查询页面
		startActivity(new Intent(this,CommonNumberQueryActivity.class));
		// 动画
		nextAnimation();
	}
	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {
		// 跳转到主页
		// Intent intent = new Intent(this, HomeActivity.class);
		// startActivity(intent);
		// 关闭页面
		finish();
		// step动画
		previousAnimation();
	}

}
