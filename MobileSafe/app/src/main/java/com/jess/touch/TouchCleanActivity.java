package com.jess.touch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jess.mobilesafe.R;

import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TouchCleanActivity extends Activity {
	private RelativeLayout rl_touchclean;
	private GridView gv_clean;
	private MyAdapter adapter;
	private RelativeLayout rl_app_loading;
	private List<ProcessInfo> infos;
	private ActivityManager am;
	private TextView tv_touch_prompt1;
	private TextView tv_touch_prompt2;
	private List<LinearLayout> animationlist;
	private Random random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touchclean);
		rl_touchclean = (RelativeLayout) findViewById(R.id.rl_touchclean);
		gv_clean = (GridView) findViewById(R.id.gv_clean);
		rl_app_loading = (RelativeLayout) findViewById(R.id.rl_app_loading);
		tv_touch_prompt1 = (TextView) findViewById(R.id.tv_touch_prompt1);
		tv_touch_prompt2 = (TextView) findViewById(R.id.tv_touch_prompt2);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		animationlist = new ArrayList<LinearLayout>();
		random = new Random();
		rl_touchclean.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					finish();
				}
				return true;
			}
		});
		filldata();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			rl_app_loading.setVisibility(View.INVISIBLE);
			adapter = new MyAdapter();
			gv_clean.setAdapter(adapter);

			gv_clean.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object object = gv_clean.getItemAtPosition(position);

					if (object != null) {
						final ProcessInfo info = (ProcessInfo) object;
						final String packageName = info.getPackageName();

						TranslateAnimation ta = new TranslateAnimation(
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_PARENT, 1,
								TranslateAnimation.RELATIVE_TO_PARENT, 0,
								TranslateAnimation.RELATIVE_TO_SELF, -2.5f);
						ta.setDuration(500);
						view.startAnimation(ta);

						new Handler().postDelayed(new Runnable() {

							public void run() {

								infos.remove(info);

								adapter.notifyDataSetChanged();

								am.killBackgroundProcesses(packageName);

								Toast.makeText(
										TouchCleanActivity.this,
										"清理["
												+ info.getName()
												+ "]成功,释放:"
												+ Formatter
														.formatFileSize(
																TouchCleanActivity.this,
																info.getMemory())
												+ "内存", 0).show();
							}
						}, 500);
					}

				}
			});
		};
	};

	public void filldata() {
		rl_app_loading.setVisibility(View.VISIBLE);
		new Thread() {

			public void run() {
				infos = ProcessInfoProvider
						.getProcessInfo(TouchCleanActivity.this);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	class MyAdapter extends BaseAdapter {

		public int getCount() {
			return infos.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// 显示每个item
			ViewHolder holder = null;
			View view = null;
			// 如果有缓存则使用缓存,缓存必须为制定的类型
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 如果无缓存则填充新的View,
				view = View.inflate(TouchCleanActivity.this,
						R.layout.item_touchclean, null);
				// 创建新的holder，将组件寻找好储存进去
				holder = new ViewHolder();
				holder.iv_touchclean_icon = (ImageView) view
						.findViewById(R.id.iv_touchclean_icon);
				holder.tv_touchclean_name = (TextView) view
						.findViewById(R.id.tv_touchclean_name);

				holder.ll_item_touchclean = (LinearLayout) view
						.findViewById(R.id.ll_item_touchclean);
				// 将holder储存进View,以便下次使用
				view.setTag(holder);
			}

			holder.iv_touchclean_icon.setBackgroundDrawable(infos.get(position)
					.getIcon());
			holder.tv_touchclean_name.setText(infos.get(position).getName());

			Animation animation = AnimationUtils.loadAnimation(
					TouchCleanActivity.this, R.anim.t);

			animation.setInterpolator(new CycleInterpolator(random.nextInt(2)+2));
			holder.ll_item_touchclean.startAnimation(animation);
			animationlist.add(holder.ll_item_touchclean);

			return view;
		}

		public Object getItem(int position) {
			ProcessInfo info = null;
			if (infos != null && infos.size() > 0) {
				info = infos.get(position);
			}
			return info;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	static class ViewHolder {
		private ImageView iv_touchclean_icon;
		private TextView tv_touchclean_name;
		private LinearLayout ll_item_touchclean;

	}

	public void clean(View v) {
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		long total = 0;
		for (ProcessInfo info : infos) {
			total += info.getMemory();
		}
		for (LinearLayout ll : animationlist) {
			ll.clearAnimation();
		}
		infos.clear();
		adapter.notifyDataSetChanged();
		tv_touch_prompt1.setVisibility(View.VISIBLE);
		tv_touch_prompt2.setVisibility(View.VISIBLE);
		tv_touch_prompt2.setText("共清理:" + Formatter.formatFileSize(this, total)
				+ "内存");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(TouchCleanActivity.this, RocketService.class);
		startService(intent);
	}

}
