package com.jess.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.BaseTouch2.MyAdapter2;
import com.jess.mobilesafe.activity.BaseTouch2.MyListener2;
import com.jess.mobilesafe.domain.AppInfo;
import com.jess.mobilesafe.engine.APPInfoProvider;
import com.jess.mobilesafe.util.DipTranslateUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.JetPlayer.OnJetEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.CalendarContract.Instances;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 软件管理
 * 
 * @author Administrator
 * 
 */
public class APPManageActivity extends BaseTouch2{
	private TextView tv_rom;// 现实内存
	private TextView tv_sd;// 现实sd
	private ListView lv_app;// 显示应用信息的Listview
	private List<AppInfo> infos;// 所有的应用信息
	private RelativeLayout rl_app_loading;// 显示加载信息的view
	private List<AppInfo> userInfos;// 用户应用
	private List<AppInfo> systemInfos;// 系统应用
	private TextView tv_app_topinfo;// 显示到顶部的View
	private PopupWindow window;
	private LinearLayout ll_popu_unstall;
	private LinearLayout ll_popu_share;
	private LinearLayout ll_popu_open;
	private LinearLayout ll_popu_setting;
	private GridView gv2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanage);
		// 拿到现实内存和sd卡的view
		tv_rom = (TextView) findViewById(R.id.tv_app_rom);
		tv_sd = (TextView) findViewById(R.id.tv_app_sd);
		// 调用方法的到结果设置显示内存和sd信息
		// 显示内存大小
		tv_rom.setText("内存可用:"
				+ Formatter.formatFileSize(this, Environment.getDataDirectory()
						.getFreeSpace()));
		// 显示sd卡大小
		tv_sd.setText("SD卡可用:"
				+ Formatter.formatFileSize(this, Environment
						.getExternalStorageDirectory().getFreeSpace()));
		// 显示加载信息的View
		rl_app_loading = (RelativeLayout) findViewById(R.id.rl_app_loading);
		// 拿到显示应用信息的Listview
		lv_app = (ListView) findViewById(R.id.lv_app);
		// 拿到现实到顶部的view
		tv_app_topinfo = (TextView) findViewById(R.id.tv_app_topinfo);
		
		gv2 = (GridView) findViewById(R.id.gv_home2_low);
		setSD((SlidingDrawer) findViewById(R.id.sd));
		gv2.setAdapter(new MyAdapter2());
		gv2.setOnItemClickListener(new MyListener2());
		
		// 填充数据
		fillData();
		// 创建滚动监听
		lv_app.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismisspopup();
				// 为空跳出，因为填充数据时异步的所以调用时数据可能为空
				if (userInfos == null || systemInfos == null) {
					return;
				}
				if (firstVisibleItem < userInfos.size() + 1) {
					tv_app_topinfo.setText("用户程序(" + userInfos.size() + "个)");
				} else {
					tv_app_topinfo.setText("系统程序(" + systemInfos.size() + "个)");
				}

			}
		});
		// 设置Item监听
		lv_app.setOnItemClickListener(new OnItemClickListener() {

			AppInfo info = null;

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismisspopup();
				if (position == 0 || position == userInfos.size() + 1) {
					return;
				}
				// 得到所点击的Item信息
				Object object = lv_app.getItemAtPosition(position);
				if (object != null) {

					info = (AppInfo) object;
				}
				// 填充popup的自定义view
				View contentView = View.inflate(APPManageActivity.this,
						R.layout.item_popu, null);
				// 设置popup的大小
				window = new PopupWindow(contentView, -2, -2);
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// 设置背景后动画才生效
				window.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				// 设置popup的显示位置并且显示
				window.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, DipTranslateUtil.dip2px(APPManageActivity.this, 67),
						location[1]);
				AlphaAnimation aa = new AlphaAnimation(0, 1);
				aa.setDuration(500);
				// 开启动画
				contentView.startAnimation(aa);
				// 拿到popup的自定义View组件
				ll_popu_unstall = (LinearLayout) contentView
						.findViewById(R.id.ll_popu_unstall);
				ll_popu_share = (LinearLayout) contentView
						.findViewById(R.id.ll_popu_share);
				ll_popu_open = (LinearLayout) contentView
						.findViewById(R.id.ll_popu_open);
				ll_popu_setting = (LinearLayout) contentView.findViewById(R.id.ll_popu_setting);
				/**
				 * 给卸载设置监听
				 */
				ll_popu_unstall.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// 发送意图给包安装器
						Intent intent = new Intent(Intent.ACTION_DELETE);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setData(Uri.parse("package:"
								+ info.getPackageName()));
						startActivity(intent);
					}
				});

				/**
				 * 给分享设置监听
				 */
				ll_popu_share.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						//发送信息发送意图给短信app
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setType("text/plain");
						//信息内容
						intent.putExtra(Intent.EXTRA_TEXT, "好消息，好消息["
								+ info.getName()
								+ "]非常好用，你值得拥有，下载地址:http://www.appchina.com/app/"
								+ info.getPackageName());
						startActivity(intent);
					}
				});
				/**
				 * 给打开设置监听
				 */
				ll_popu_open.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// 使用包管理器传入包名得到要启动的应用的意图
						Intent intent = getPackageManager().getLaunchIntentForPackage(info.getPackageName());
						if (intent!=null) {
							//如果找到意图就启动
							startActivity(intent);
						}else {
							//没有则提示用户
							Toast.makeText(APPManageActivity.this, "该应用没有启动界面", 0).show();
						}
					}
				});
				/**
				 * 给设置设置监听
				 * 
				 */
				ll_popu_setting.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						//发送安装信息意图给设置app
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						//包名
						intent.setData(Uri.parse("package:"+info.getPackageName()));
						startActivity(intent);
					}
				});
			}
		});

	}

	/**
	 * 得到传入的地址的可用大小
	 * 
	 * @param path
	 * @return
	 */
	public String getPathSize(String path) {
		StatFs fs = new StatFs(path);
		// 拿到可用区块的数量
		int blocks = fs.getAvailableBlocks();
		// 拿到每个区块的大小
		int size = fs.getBlockSize();

		return Formatter.formatFileSize(this, blocks * size);

	}

	/**
	 * 处理子线程要刷新的ui操作
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 加载完成后隐藏提示
			rl_app_loading.setVisibility(View.INVISIBLE);
			// 设置适配器显示
			lv_app.setAdapter(new AppAdapter());
		};
	};

	/**
	 * 填充数据
	 */
	public void fillData() {
		// 加载数据时提示用户
		rl_app_loading.setVisibility(View.VISIBLE);
		// 填充数据时耗时操作所以用子线程
		new Thread() {

			public void run() {
				infos = APPInfoProvider.getAppInfo(APPManageActivity.this);
				// 用户应用
				userInfos = new ArrayList<AppInfo>();
				// 系统应用
				systemInfos = new ArrayList<AppInfo>();

				for (AppInfo info : infos) {
					// 遍历所有应用信息
					// 分类
					if (info.isUser()) {
						userInfos.add(info);
					} else {
						systemInfos.add(info);
					}

				}
				// 请求完数据发送消息显示数据
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * listview的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class AppAdapter extends BaseAdapter {

		public int getCount() {
			// 要显示的Item总数量
			return systemInfos.size() + userInfos.size() + 1 + 1;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo info = null;
			// 0坐标显示用户程序个数
			if (position == 0) {
				TextView textView = new TextView(APPManageActivity.this);
				textView.setText("用户程序(" + userInfos.size() + "个)");
				textView.setTextColor(0xBBFFFFFF);
				textView.setBackgroundColor(0x660044FF);
				return textView;
			} else if (position <= userInfos.size()) {
				// 显示用户信息
				info = userInfos.get(position - 1);
			} else if (position == userInfos.size() + 1) {
				// 显示系统程序的个数
				TextView textView = new TextView(APPManageActivity.this);
				textView.setText("系统程序(" + systemInfos.size() + "个)");
				textView.setTextColor(0xBBFFFFFF);
				textView.setBackgroundColor(0x660044FF);
				return textView;
			} else {
				// 显示系统程序
				info = systemInfos.get(position - userInfos.size() - 2);
			}
			// 每个Item的View
			View view = null;
			// 优化数据下次不用再查找组件
			ViewHolder holder = null;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 有缓存则使用缓存
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 如果没有缓存则从新填充View
				view = View.inflate(APPManageActivity.this, R.layout.item_app,
						null);
				holder = new ViewHolder();
				// 查找组件冰储存至Holder中，下次拿出来使用就不用查找了
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_app_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				// 储存进view中
				view.setTag(holder);
			}
			// 将数据填充至组件中
			// 设置图标
			holder.iv_icon.setImageDrawable(info.getIcon());
			// 设置应用名
			holder.tv_app_name.setText(info.getName());
			// 设置储存位置
			if (info.isRom()) {
				holder.tv_app_location.setText("内部储存");
			} else {
				holder.tv_app_location.setText("sd卡储存");
			}

			return view;
		}

		public Object getItem(int position) {
			AppInfo info = null;
			if (position == 0) {
				return null;
			} else if (position <= userInfos.size()) {
				info = userInfos.get(position - 1);
			} else if (position == userInfos.size() + 1) {
				return null;
			} else {
				info = systemInfos.get(position - userInfos.size() - 2);
			}
			// 每个Item的信息
			return info;
		}

		public long getItemId(int position) {
			// 每个item的Id
			return 0;
		}

	}

	/**
	 * 
	 */
	public void dismisspopup() {
		// 消除popup
		if (window != null && window.isShowing()) {
			window.dismiss();
			window = null;
		}
	}

	/**
	 * 用来储存找到的组件，下次就不用再查找直接使用
	 * 
	 * @author Administrator
	 * 
	 */

	static class ViewHolder {
		TextView tv_app_name;
		TextView tv_app_location;
		ImageView iv_icon;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// 返回时调用进出动画效果
		overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);

	}

	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPreviousPage() {
		// TODO Auto-generated method stub
		
	}

}
