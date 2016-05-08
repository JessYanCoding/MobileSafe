package com.jess.mobilesafe.activity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.BaseTouch2.MyAdapter2;
import com.jess.mobilesafe.activity.BaseTouch2.MyListener2;
import com.jess.mobilesafe.domain.AppInfo;
import com.jess.mobilesafe.engine.APPInfoProvider;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class CacheClearActivity extends BaseTouch2 {
	protected static final int SCAN = 0;
	protected static final int FINISH = 1;
	private static final int SHOW = 3;
	private ImageView iv_cache_scan;
	private GridView gv2;
	private TextView tv_cache_scan;
	private PackageManager pm;
	private long cacheTotal = 0;
	private TextView tv_cache_num;
	private TextView tv_cache_size;
	private TextView tv_cache_prompt;
	private LinearLayout ll_cache_top;
	private ExpandableListView tlv_cache;
	private List<CacheInfo> innerInfos;
	private long innerCache;
	private RelativeLayout rl_cache_inner;
	private RelativeLayout rl_cache_external;
	private TextView tv_cache_innertotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cacheclear);
		iv_cache_scan = (ImageView) findViewById(R.id.iv_cache_scan);
		tv_cache_scan = (TextView) findViewById(R.id.tv_cache_scan);
		tv_cache_num = (TextView) findViewById(R.id.tv_cache_num);
		tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
		tv_cache_prompt = (TextView) findViewById(R.id.tv_cache_prompt);
		ll_cache_top = (LinearLayout) findViewById(R.id.ll_cache_top);
		tlv_cache = (ExpandableListView) findViewById(R.id.tlv_cache);
		rl_cache_inner = (RelativeLayout) findViewById(R.id.rl_cache_inner);
		rl_cache_external = (RelativeLayout) findViewById(R.id.rl_cache_external);
		tv_cache_innertotal = (TextView) findViewById(R.id.tv_cache_innertotal);

		gv2 = (GridView) findViewById(R.id.gv_home2_low);
		setSD((SlidingDrawer) findViewById(R.id.sd));
		gv2.setAdapter(new MyAdapter2());
		gv2.setOnItemClickListener(new MyListener2());

		TranslateAnimation ta = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, -0.92f);

		ta.setDuration(1000);
		ta.setRepeatMode(TranslateAnimation.REVERSE);
		ta.setRepeatCount(-1);

		iv_cache_scan.startAnimation(ta);
		check();
		checkSD();
	}

	private void checkSD() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdfile = Environment.getExternalStorageDirectory();
			File[] files = sdfile.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println(file.getAbsolutePath());
				}
			}

		}
	}

	class MyAdapter extends BaseExpandableListAdapter {

		public int getGroupCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			if (groupPosition == 0) {

				if (innerInfos != null) {

					return innerInfos.size();
				}
			}
			return 0;
		}

		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = null;
			if (groupPosition == 0) {

				view = View.inflate(CacheClearActivity.this,
						R.layout.item_cache_group, null);
				TextView tv = (TextView) view
						.findViewById(R.id.tv_cache_grouptotal);
				tv.setText(Formatter.formatFileSize(CacheClearActivity.this,
						innerCache));
			} else {
				view = View.inflate(CacheClearActivity.this,
						R.layout.item_cache_group, null);
				TextView tv = (TextView) view.findViewById(R.id.tv_storage);
				tv.setText("外部储存");
			}
			return view;
		}

		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View v = null;
			if (groupPosition == 0) {
				ViewHolder holder = null;
				if (convertView != null) {
					v = convertView;
					holder = (ViewHolder) v.getTag();
				} else {
					v = View.inflate(CacheClearActivity.this,
							R.layout.item_cache_child, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
					holder.iv_cache_delete = (ImageView) v
							.findViewById(R.id.iv_cache_delete);
					holder.tv_cache_childname = (TextView) v
							.findViewById(R.id.tv_cache_childname);
					holder.tv_cache_childtotal = (TextView) v
							.findViewById(R.id.tv_cache_childtotal);
					v.setTag(holder);
				}
				holder.iv_icon.setBackgroundDrawable(innerInfos.get(
						childPosition).getIcon());
				holder.tv_cache_childname.setText(innerInfos.get(childPosition)
						.getName());
				holder.tv_cache_childtotal.setText(Formatter.formatFileSize(
						CacheClearActivity.this, innerInfos.get(childPosition)
								.getCache()));
				holder.iv_cache_delete
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub
								try {
									Method method = PackageManager.class
											.getMethod(
													"deleteApplicationCacheFiles",
													String.class,
													IPackageDataObserver.class);
									method.invoke(pm,
											innerInfos.get(childPosition)
													.getPackageName(),
											new IPackageDataObserver.Stub() {

												public void onRemoveCompleted(
														String packageName,
														boolean succeeded)
														throws RemoteException {

												}
											});

								} catch (Exception e) {
									Intent intent = new Intent(
											"android.settings.APPLICATION_DETAILS_SETTINGS");
									intent.addCategory(Intent.CATEGORY_DEFAULT);
									// 包名
									intent.setData(Uri.parse("package:"
											+ innerInfos.get(childPosition)
													.getPackageName()));
									startActivity(intent);
								}
							}
						});
			}
			return v;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	static class ViewHolder {
		private ImageView iv_icon;
		private ImageView iv_cache_delete;
		private TextView tv_cache_childname;
		private TextView tv_cache_childtotal;

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCAN:
				String name = (String) msg.obj;
				tv_cache_scan.setText("正在扫描:" + name);
				break;

			case FINISH:
				if (cacheTotal > 0) {
					ll_cache_top.setBackgroundColor(getResources().getColor(
							R.color.brown));
					tv_cache_prompt.setVisibility(View.VISIBLE);
					tlv_cache.setAdapter(new MyAdapter());
					rl_cache_inner.setVisibility(View.INVISIBLE);
					rl_cache_external.setVisibility(View.INVISIBLE);
				} else {
					tv_cache_prompt.setVisibility(View.VISIBLE);
					tv_cache_prompt.setText("您的手机一尘不染!");
				}
				
				tv_cache_scan.setText("扫描完成");
				iv_cache_scan.clearAnimation();
				iv_cache_scan.setVisibility(View.INVISIBLE);

				break;

			case SHOW:
				if (cacheTotal > 0) {
					char[] sizes = Formatter.formatFileSize(
							CacheClearActivity.this, cacheTotal).toCharArray();
					StringBuffer buf = new StringBuffer();
					StringBuffer buf2 = new StringBuffer();
					for (char c : sizes) {
						if (c >= '0' && c <= '9' || c == '.') {
							buf.append(c);
						} else {
							buf2.append(c);
						}

					}
					String size = buf.toString();
					tv_cache_num.setText(size);
					tv_cache_size.setText(buf2.toString());
					tv_cache_innertotal.setText(Formatter.formatFileSize(
							CacheClearActivity.this, innerCache));
				}

				break;
			}

		};
	};

	public void check() {
		new Thread() {

			public void run() {
				List<AppInfo> infos = APPInfoProvider
						.getAppInfo(CacheClearActivity.this);
				pm = getPackageManager();
				for (AppInfo info : infos) {
					String name = info.getName();
					String packageName = info.getPackageName();
					Message msg = handler.obtainMessage();
					msg.obj = name;
					msg.what = SCAN;
					handler.sendMessage(msg);

					try {
						Method method = PackageManager.class.getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(pm, packageName, new MyPackageObserver());
						sleep(70);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Message msg = handler.obtainMessage();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};

		}.start();
	}

	class MyPackageObserver extends IPackageStatsObserver.Stub {

		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			if (innerInfos == null) {
				innerInfos = new ArrayList<CacheClearActivity.CacheInfo>();
			}
			if (pStats.cacheSize > 0) {
				CacheInfo info = new CacheInfo();
				cacheTotal += pStats.cacheSize;
				innerCache += pStats.cacheSize;
				Message msg = handler.obtainMessage();
				msg.what = SHOW;
				handler.sendMessage(msg);
				try {
					long cache = pStats.cacheSize;
					String packageName = pStats.packageName;
					Drawable icon = pm.getPackageInfo(packageName, 0).applicationInfo
							.loadIcon(pm);
					String name = pm.getPackageInfo(packageName, 0).applicationInfo
							.loadLabel(pm).toString();

					info.setCache(cache);
					info.setIcon(icon);
					info.setName(name);
					info.setPackageName(packageName);

					innerInfos.add(info);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void clearAll(View v) {
		try {
			Method method = PackageManager.class.getMethod(
					"freeStorageAndNotify", long.class,
					IPackageDataObserver.class);
			try {
				method.invoke(pm, Integer.MAX_VALUE,
						new IPackageDataObserver.Stub() {

							public void onRemoveCompleted(String packageName,
									boolean succeeded) throws RemoteException {
								// if (succeeded) {
								// System.out.println("清理失败");
								// }else if(!succeeded){
								// System.out.println("清理完成");
								// }else{
								// System.out.println("故障");
								// }

							}
						});
				finish();
				Intent intent = new Intent(this, CacheFinishActivity.class);
				intent.putExtra("cacheTotal", cacheTotal);
				startActivity(intent);
				overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: handle exception
	}

	class CacheInfo {
		private String name;
		private String packageName;
		private Drawable icon;
		private long cache;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public long getCache() {
			return cache;
		}

		public void setCache(long cache) {
			this.cache = cache;
		}

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
