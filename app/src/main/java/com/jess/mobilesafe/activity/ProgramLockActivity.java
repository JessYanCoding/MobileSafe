package com.jess.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.BaseTouch2.MyAdapter2;
import com.jess.mobilesafe.activity.BaseTouch2.MyListener2;
import com.jess.mobilesafe.dao.LockDao;
import com.jess.mobilesafe.domain.AppInfo;
import com.jess.mobilesafe.engine.APPInfoProvider;
import com.jess.mobilesafe.util.MD5Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramLockActivity extends BaseTouch2 {
	private GridView gv2;
	private ImageView iv_unlock;
	private ImageView iv_lock;
	private GridView gv_unlock;
	private GridView gv_lock;
	private RelativeLayout rl_app_loading;
	private List<AppInfo> infos;
	private List<AppInfo> unlockinfos;
	private List<AppInfo> lockinfos;
	private MyGridadapter unlockada;
	private MyGridadapter lockada;
	private LockDao lockdao;
	private TextView tv_lockcount;
	private TextView tv_unlockcount;
	private LinearLayout ll_lock_setpass;
	private LinearLayout ll_lock_inputpass;
	private SharedPreferences spf;
	private TextView et_lock_set1;
	private TextView et_lock_set2;
	private TextView et_lock_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_programlock);

		gv2 = (GridView) findViewById(R.id.gv_home2_low);
		setSD((SlidingDrawer) findViewById(R.id.sd));
		gv2.setAdapter(new MyAdapter2());
		gv2.setOnItemClickListener(new MyListener2());

		// 拿到SharedPreferences进行相应的配置
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);

		iv_unlock = (ImageView) findViewById(R.id.iv_unlock);
		iv_lock = (ImageView) findViewById(R.id.iv_lock);
		gv_unlock = (GridView) findViewById(R.id.gv_unlock);
		gv_lock = (GridView) findViewById(R.id.gv_lock);
		rl_app_loading = (RelativeLayout) findViewById(R.id.rl_app_loading);
		tv_lockcount = (TextView) findViewById(R.id.tv_lockcount);
		tv_unlockcount = (TextView) findViewById(R.id.tv_unlockcount);
		ll_lock_setpass = (LinearLayout) findViewById(R.id.ll_lock_setpass);
		ll_lock_inputpass = (LinearLayout) findViewById(R.id.ll_lock_inputpass);
		et_lock_set1 = (TextView) findViewById(R.id.et_lock_set1);
		et_lock_set2 = (TextView) findViewById(R.id.et_lock_set2);
		et_lock_input = (TextView) findViewById(R.id.et_lock_input);

		iv_unlock.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				iv_unlock
						.setBackgroundColor(0x33000000);
				iv_lock.setBackgroundColor(0x33ffffff);

				gv_unlock.setVisibility(View.VISIBLE);
				gv_lock.setVisibility(View.INVISIBLE);

			}
		});

		iv_lock.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				iv_unlock
						.setBackgroundColor(0x33ffffff);
				iv_lock.setBackgroundColor(0x33000000);

				gv_unlock.setVisibility(View.INVISIBLE);
				gv_lock.setVisibility(View.VISIBLE);

			}
		});
		if (spf.getString("lockpassword", null) == null) {
			ll_lock_setpass.setVisibility(View.VISIBLE);

		} else {
			ll_lock_inputpass.setVisibility(View.VISIBLE);

		}

	}

	public void setting(View v) {
		String password = et_lock_set1.getText().toString().trim();
		String confirm = et_lock_set2.getText().toString().trim();
		if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm)
				&& password.equals(confirm)) {
			spf.edit().putString("lockpassword", MD5Utils.encode(password))
					.commit();
			ll_lock_setpass.setVisibility(View.INVISIBLE);
			fillData();
			Toast.makeText(this, "欢迎第一次使用程序锁,如需启动该功能需要在设置中心里开启,此功能在5.0以后,由于安全原因已被Google砍掉!", 1).show();
		} else {
			Toast.makeText(this, "密码为空,或两次密码不一致", 0).show();
		}
	}

	public void ok(View v) {
		String password = et_lock_input.getText().toString().trim();
		if (!TextUtils.isEmpty(password)
				&& MD5Utils.encode(password).equals(
						spf.getString("lockpassword", ""))) {
			ll_lock_inputpass.setVisibility(View.INVISIBLE);
			fillData();
		} else {
			Toast.makeText(this, "密码错误", 0).show();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			rl_app_loading.setVisibility(View.INVISIBLE);

			tv_lockcount.setText("点击图标解锁" + "(共:" + lockinfos.size() + "个)");
			tv_unlockcount
					.setText("点击图标加锁" + "(共:" + unlockinfos.size() + "个)");
			unlockada = new MyGridadapter(true);
			lockada = new MyGridadapter(false);

			gv_unlock.setAdapter(unlockada);
			gv_lock.setAdapter(lockada);
			gv_unlock.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object object = gv_unlock.getItemAtPosition(position);

					if (object != null) {
						final AppInfo info = (AppInfo) object;
						final String packageName = info.getPackageName();

						ViewHolder holder = (ViewHolder) view.getTag();

						TranslateAnimation ta = new TranslateAnimation(
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_PARENT, 1,
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_SELF, -2.5f);
						ta.setDuration(500);
						holder.ll_item_lock.startAnimation(ta);

						new Handler().postDelayed(new Runnable() {

							public void run() {

								lockdao.add(packageName);
								unlockinfos.remove(info);
								if (!lockinfos.contains(info)) {
									
									lockinfos.add(0, info);
								}
									
								lockada.notifyDataSetChanged();
								unlockada.notifyDataSetChanged();
								tv_lockcount.setText("点击图标解锁" + "(共:"
										+ lockinfos.size() + "个)");
								tv_unlockcount.setText("点击图标加锁" + "(共:"
										+ unlockinfos.size() + "个)");

							}
						}, 500);

					}
				}
			});
			gv_lock.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object object = gv_lock.getItemAtPosition(position);

					if (object != null) {
						final AppInfo info = (AppInfo) object;
						final String packageName = info.getPackageName();
						ViewHolder holder = (ViewHolder) view.getTag();

						TranslateAnimation ta = new TranslateAnimation(
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_PARENT, -1,
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_SELF, -2.5f);
						ta.setDuration(500);
						holder.ll_item_lock.startAnimation(ta);

						new Handler().postDelayed(new Runnable() {

							public void run() {
								lockdao.delete(packageName);
								lockinfos.remove(info);
								if (!unlockinfos.contains(info)) {
									
									unlockinfos.add(0, info);
								}

								lockada.notifyDataSetChanged();
								unlockada.notifyDataSetChanged();
								tv_lockcount.setText("点击图标解锁" + "(共:"
										+ lockinfos.size() + "个)");
								tv_unlockcount.setText("点击图标加锁" + "(共:"
										+ unlockinfos.size() + "个)");
							}
						}, 500);

					}
				}
			});
		};
	};

	public void fillData() {
		rl_app_loading.setVisibility(View.VISIBLE);
		new Thread() {

			public void run() {
				infos = APPInfoProvider.getAppInfo(ProgramLockActivity.this);

				lockdao = new LockDao(ProgramLockActivity.this);
				unlockinfos = new ArrayList<AppInfo>();
				lockinfos = new ArrayList<AppInfo>();
				for (AppInfo info : infos) {
					if (lockdao.queryLock(info.getPackageName())) {
						lockinfos.add(info);
					} else {
						unlockinfos.add(info);
					}

				}
				handler.sendEmptyMessage(1);

			};
		}.start();
	}

	class MyGridadapter extends BaseAdapter {
		boolean isunlock;

		MyGridadapter(boolean isunlock) {
			this.isunlock = isunlock;
		}

		public int getCount() {
			if (!isunlock) {
				return lockinfos.size();
			}
			return unlockinfos.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo info = null;
			if (!isunlock) {
				info = lockinfos.get(position);
			} else {
				info = unlockinfos.get(position);
			}
			View view = null;
			ViewHolder holder = null;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(ProgramLockActivity.this,
						R.layout.item_lockprogram, null);
				holder = new ViewHolder();
				holder.iv_lock_icon = (ImageView) view
						.findViewById(R.id.iv_lock_icon);
				holder.tv_lock_name = (TextView) view
						.findViewById(R.id.tv_lock_name);
				holder.ll_item_lock = (LinearLayout) view
						.findViewById(R.id.ll_item_lock);
				view.setTag(holder);
			}

			holder.iv_lock_icon.setBackgroundDrawable(info.getIcon());
			holder.tv_lock_name.setText(info.getName());
			return view;
		}

		public Object getItem(int position) {
			AppInfo info = null;
			if (!isunlock) {
				info = lockinfos.get(position);
			} else {
				info = unlockinfos.get(position);
			}
			return info;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	static class ViewHolder {
		private ImageView iv_lock_icon;
		private TextView tv_lock_name;
		private LinearLayout ll_item_lock;
	}

	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPreviousPage() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (lockada != null) {

			lockdao.close();
			lockada = null;
		}
	}

}
