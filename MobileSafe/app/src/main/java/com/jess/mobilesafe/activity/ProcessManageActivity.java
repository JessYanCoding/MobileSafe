package com.jess.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.domain.AppInfo;
import com.jess.mobilesafe.domain.ProcessInfo;
import com.jess.mobilesafe.engine.ProcessInfoProvider;
import com.jess.mobilesafe.sevice.KillProcessService;
import com.jess.mobilesafe.util.ProcessInfoUtil;
import com.jess.mobilesafe.util.ServiceState;

/**
 * 进程管理
 * 
 * @author Administrator
 * 
 */
public class ProcessManageActivity extends Activity {
	private TextView tv_process_count;// 显示进程个数的View
	private TextView tv_process_memory;// 显示总内存和剩余内存的view
	private RelativeLayout rl_process;// 显示loading的view
	private List<ProcessInfo> userInfos;// 储存用户进程的list
	private List<ProcessInfo> systemInfos;// 储存系统进程的list
	private ListView lv_process;// 现实进程信息的ListView
	private ProcessAdapter adapter;// 适配器
	private long avaimemory;// 可用内存
	int proCount;// 运行中的进程个数
	private String totalm;// 总内存
	private SharedPreferences spf;// 用于储存配置信息

	/**
	 * 构造方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processmanage);
		// 拿到显示进程个数的View
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		// 拿到显示总内存和剩余内存的view
		tv_process_memory = (TextView) findViewById(R.id.tv_process_memory);
		// 设置显示进程个数
		proCount = ProcessInfoUtil.getProcessCount(this);
		tv_process_count.setText("运行中的进程:" + proCount + "个");
		avaimemory = ProcessInfoUtil.getAvaiMemory(this);
		String avaim = Formatter.formatFileSize(this,
				ProcessInfoUtil.getAvaiMemory(this));
		totalm = Formatter.formatFileSize(this,
				ProcessInfoUtil.getTotalMemory(this));
		tv_process_memory.setText("剩余/总内存:" + avaim + "/" + totalm);
		// 拿到显示loading的view
		rl_process = (RelativeLayout) findViewById(R.id.rl_process_loading);
		// 拿到现实进程信息的ListView
		lv_process = (ListView) findViewById(R.id.lv_process);
		// 拿到SharedPreferences进行相应的配置
		spf = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 填充数据
		fillData();
		// 设置Item监听
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 如果是显示显示进程个数的Item则跳出不能点击
				if (position == 0 || position == userInfos.size() + 1) {
					return;
				}

				// 获得当前位置Item的进程信息
				Object object = lv_process.getItemAtPosition(position);
				ProcessInfo info = null;
				if (object != null) {
					// 强转为进程信息
					info = (ProcessInfo) object;
				}
				if (getPackageName().equals(info.getPackageName())) {
					return;
				}
				// 拿到当前位置的View
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_process);
				// 根据当前进程信息的记录来设置是否勾选
				if (info != null) {

					if (info.isCheck()) {
						info.setCheck(false);
						cb.setChecked(false);
					} else {
						info.setCheck(true);
						cb.setChecked(true);
					}
				}
			}
		});
	}

	/**
	 * 处理子线程刷新Ui
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 数据填充完毕隐藏加载中提示
			rl_process.setVisibility(View.INVISIBLE);
			// 给List设置adapter显示内容
			adapter = new ProcessAdapter();
			lv_process.setAdapter(adapter);
		};
	};

	/**
	 * 填充数据
	 */
	public void fillData() {
		// 填充数据提示用户正在加载中
		rl_process.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				// 拿到储存所有进程信息的List
				List<ProcessInfo> infos = ProcessInfoProvider
						.getProcessInfo(ProcessManageActivity.this);
				// 储存用户进程的List
				userInfos = new ArrayList<ProcessInfo>();
				// 储存系统进程的List
				systemInfos = new ArrayList<ProcessInfo>();
				// 对进程信息分类
				for (ProcessInfo Info : infos) {
					if (Info.isUser()) {
						userInfos.add(Info);
					} else {
						systemInfos.add(Info);
					}
				}

				handler.sendEmptyMessage(0);

			};
		}.start();
	}

	/**
	 * listview的适配器，用于显示每个Item
	 * 
	 * @author Administrator
	 * 
	 */
	class ProcessAdapter extends BaseAdapter {
		// 当前的进程信息
		ProcessInfo info = null;
		// 储存组件的Holder
		ViewHolder holder = null;

		public int getCount() {
			// 显示的item长度
			boolean systemprocess = spf.getBoolean("systemprocess", true);
			if (systemprocess) {
				return userInfos.size() + systemInfos.size() + 1 + 1;
			} else {
				return userInfos.size() + 1;
			}
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// 0坐标显示用户进程个数
			if (position == 0) {
				TextView textView = new TextView(ProcessManageActivity.this);
				textView.setText("用户进程(" + userInfos.size() + "个)");
				textView.setTextColor(0xBBFFFFFF);
				textView.setBackgroundColor(0x660044FF);
				return textView;
			} else if (position <= userInfos.size()) {
				// 显示用户进程信息
				info = userInfos.get(position - 1);
			} else if (position == userInfos.size() + 1) {
				// 显示系统进程的个数
				TextView textView = new TextView(ProcessManageActivity.this);
				textView.setText("系统程序(" + systemInfos.size() + "个)");
				textView.setTextColor(0xBBFFFFFF);
				textView.setBackgroundColor(0x660044FF);
				return textView;
			} else {
				// 显示系统进程
				info = systemInfos.get(position - userInfos.size() - 2);
			}
			// 显示每个item
			View view = null;
			// 如果有缓存则使用缓存,缓存必须为制定的类型
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 如果无缓存则填充新的View,
				view = View.inflate(ProcessManageActivity.this,
						R.layout.item_process, null);
				// 创建新的holder，将组件寻找好储存进去
				holder = new ViewHolder();
				holder.tv_process_name = (TextView) view
						.findViewById(R.id.tv_process_name);
				holder.iv_process_icon = (ImageView) view
						.findViewById(R.id.iv_process_icon);
				holder.tv_process_memory = (TextView) view
						.findViewById(R.id.tv_process_memory);
				holder.cb_process = (CheckBox) view
						.findViewById(R.id.cb_process);
				// 将holder储存进View,以便下次使用
				view.setTag(holder);
			}
			// 设置每个item要显示的信息
			holder.iv_process_icon.setImageDrawable(info.getIcon());
			holder.tv_process_name.setText(info.getName());
			holder.tv_process_memory.setText("内存占用:"
					+ Formatter.formatFileSize(ProcessManageActivity.this,
							info.getMemory()));
			// 设置是否勾选
			if (info.isCheck()) {
				holder.cb_process.setChecked(true);
			} else {
				holder.cb_process.setChecked(false);
			}

			if (getPackageName().equals(info.getPackageName())) {
				holder.cb_process.setVisibility(View.GONE);
			} else {
				holder.cb_process.setVisibility(View.VISIBLE);
			}
			// 返回view
			return view;
		}

		/**
		 * 获取每个Item的信息
		 */
		public Object getItem(int position) {
			ProcessInfo info = null;
			if (position == 0) {
				// 0位置不返回值
				return null;
			} else if (position <= userInfos.size()) {
				// 用户进程的信息
				info = userInfos.get(position - 1);
			} else if (position == userInfos.size() + 1) {
				// 不返回值
				return null;
			} else {
				// 系统进程的信息
				info = systemInfos.get(position - userInfos.size() - 2);
			}
			// 没找到返回空
			return info;
		}

		public long getItemId(int position) {
			return 0;
		}

	}

	/**
	 * 全选
	 * 
	 * @param v
	 */
	public void allSelect(View v) {
		// 遍历用户进程，全部设置为勾选
		for (ProcessInfo info : userInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setCheck(true);
		}
		// 遍历系统进程全部设置为勾选
		for (ProcessInfo info : systemInfos) {
			info.setCheck(true);
		}

		// 更新页面
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选
	 * 
	 * @param v
	 */
	public void reverse(View v) {
		// 遍历用户进程，全部设置为当前相反的勾选状态
		for (ProcessInfo info : userInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setCheck(!info.isCheck());
		}
		// 遍历系统进程全部设置为当前相反的勾选状态
		for (ProcessInfo info : systemInfos) {
			info.setCheck(!info.isCheck());
		}

		// 更新页面
		adapter.notifyDataSetChanged();
	}

	/**
	 * 一键清理
	 * 
	 * @param v
	 */
	public void clean(View v) {
		// 拿到activity管理器
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 存储所有要杀死的进程
		List<ProcessInfo> kills = new ArrayList<ProcessInfo>();
		int killCount = 0;
		long killmem = 0;
		for (ProcessInfo info : userInfos) {
			// 遍历所有用户进程如果为勾选状态则杀死该进程
			if (info.isCheck()) {
				am.killBackgroundProcesses(info.getPackageName());
				kills.add(info);
				killCount++;
				killmem += info.getMemory();
			}
		}
		// 遍历系统进程如果为勾选则杀死该进程
		for (ProcessInfo info : systemInfos) {
			if (info.isCheck()) {
				am.killBackgroundProcesses(info.getPackageName());
				kills.add(info);
				killCount++;
				killmem += info.getMemory();
			}
		}
		// 避免并发，上面两个集合遍历完后，在吧杀死的进程移除List
		for (ProcessInfo info : kills) {
			if (info.isUser()) {
				userInfos.remove(info);
			} else {
				systemInfos.remove(info);
			}
		}
		// 每次清理完，调整进程数，和剩余内存
		proCount -= killCount;
		avaimemory += killmem;
		// 重新设置进程数和剩余空间
		tv_process_count.setText("运行中的进程:" + proCount + "个");
		tv_process_memory.setText("剩余/总内存:"
				+ Formatter.formatFileSize(this, avaimemory) + "/" + totalm);
		// 提示用户清理进程的情况
		Toast.makeText(
				this,
				"杀死了" + killCount + "个进程," + "释放了"
						+ Formatter.formatFileSize(this, killmem), 0).show();
		// 更新页面
		adapter.notifyDataSetChanged();
	}

	/**
	 * 设置
	 * 
	 * @param v
	 */
	public void setting(View v) {
		// 多选框的显示和是否勾选
		final String[] items = new String[] { "显示系统进程", "锁屏后杀死后台进程" };
		final boolean[] check = new boolean[] { false, false };
		// 从配置文件中的得到是否开机启动的信息
		boolean systemprocess = spf.getBoolean("systemprocess", true);
		boolean running = ServiceState.serviceRunning(this,
				"com.jess.mobilesafe.sevice.KillProcessService");
		// 将配置文件中的信息复制给多选框
		check[0] = systemprocess;
		check[1] = running;
		// 创建builder
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ProcessManageActivity.this);
		// 设置多选框
		builder.setMultiChoiceItems(items, check,
				new OnMultiChoiceClickListener() {

					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						// 点击后将值付给check
						check[which] = isChecked;
					}

				});
		// 设置确定按钮的并且监听事件
		builder.setPositiveButton("立即生效",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 查看是否勾选储存到配置文件
						if (check[0]) {
							spf.edit().putBoolean("systemprocess", true)
									.commit();
						} else {
							spf.edit().putBoolean("systemprocess", false)
									.commit();
						}

						if (check[1]) {
							startService(new Intent(ProcessManageActivity.this,
									KillProcessService.class));
						} else {
							stopService(new Intent(ProcessManageActivity.this,
									KillProcessService.class));
						}
						// 更新界面
						adapter.notifyDataSetChanged();
					}

				});
		// 显示多选框
		builder.show();
	}

	/**
	 * holderview储存每个组件，优化性能
	 * 
	 * @author Administrator
	 * 
	 */
	static class ViewHolder {
		ImageView iv_process_icon;
		TextView tv_process_name;
		TextView tv_process_memory;
		CheckBox cb_process;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// 返回时调用进出动画效果
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);

	}

}
