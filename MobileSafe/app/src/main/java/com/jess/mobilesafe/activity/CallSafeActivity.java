package com.jess.mobilesafe.activity;

import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.activity.BaseTouch2.MyAdapter2;
import com.jess.mobilesafe.activity.BaseTouch2.MyListener2;
import com.jess.mobilesafe.dao.BlackNumberDao;
import com.jess.mobilesafe.domain.BlackNumberInfo;

import android.animation.AnimatorSet.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class CallSafeActivity extends BaseTouch2 {
	private ListView lv_blackNumber;// 显示黑名单信息的Listview
	private List<BlackNumberInfo> infos;// 存放所有黑马单信息
	private RelativeLayout rl_black_loading;// 显示Loading信息的View
	private BlackNumberAdapter adapter;// 适配器
	private int index = 0;// 每次查询的起始位置
	private int total = 0;// //数据库中黑名单总数
	private boolean isLoading = false;// 加载状态
	private Button bt_blacknumber_add;// 添加黑名单列表的view
	private GridView gv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsafe);
		// 显示黑名单信息的Listview
		lv_blackNumber = (ListView) findViewById(R.id.lv_blacknumber);
		// 显示Loading信息的View
		rl_black_loading = (RelativeLayout) findViewById(R.id.rl_black_loading);
		// 拿到显示黑马单Listview
		lv_blackNumber = (ListView) findViewById(R.id.lv_blacknumber);
		// 拿到添加黑名单列表的view
		bt_blacknumber_add = (Button) findViewById(R.id.bt_blacknumber_add);
		// 添加点击监听
		bt_blacknumber_add.setOnClickListener(new AddClickListener());
		// 填充数据完成后显示
		fillData();
		// 给listview设置滑动监听
		lv_blackNumber.setOnScrollListener(new MyScrollListener());
		
		gv2 = (GridView) findViewById(R.id.gv_home2_low);
		setSD((SlidingDrawer) findViewById(R.id.sd));
		gv2.setAdapter(new MyAdapter2());
		gv2.setOnItemClickListener(new MyListener2());
	}

	/**
	 * 添加按钮的点击监听
	 * 
	 * @author Administrator
	 * 
	 */
	class AddClickListener implements OnClickListener {

		public void onClick(View v) {
			// 点击弹出对话框输入要拦截的号码
			showDialog();
		}

	}

	/**
	 * 输出要添加的号码的dialog
	 */
	AlertDialog dialog;

	public void showDialog() {
		// 设置自定义的view到dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CallSafeActivity.this);
		dialog = builder.create();
		// 填充自定义的View
		View view = View.inflate(CallSafeActivity.this,
				R.layout.dialog_blacknumber, null);
		// 找到自定义View中的组件冰给确定和取消键设置监听
		final EditText et_phone = (EditText) view
				.findViewById(R.id.et_black_dialog);
		final RadioGroup rg_mode = (RadioGroup) view.findViewById(R.id.rg_mode);
		// 确定键
		Button bt_ok = (Button) view.findViewById(R.id.bt_black_ok);
		// 取消键
		Button bt_cancel = (Button) view.findViewById(R.id.bt_black_cancel);
		// 给确定键设置监听
		bt_ok.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 得到电话号码
				String phone = et_phone.getText().toString();
				// 得到模式
				int id = rg_mode.getCheckedRadioButtonId();
				int mode = 2;
				switch (id) {
				case R.id.radio0:
					mode = 0;
					break;
				case R.id.radio1:
					mode = 1;
					break;
				case R.id.radio2:
					mode = 2;
					break;
				}
				// 用dao储存数据
				BlackNumberDao bd = new BlackNumberDao(CallSafeActivity.this);
				if (!TextUtils.isEmpty(phone)) {
					// 如果不为空则把数据储存到数据库
					bd.add(phone, mode + "");
					// 如果Listview已经存在此号码，那么删除后创建，保证号码唯一
					if (infos.size() > 0) {

						if (phone.equals(infos.get(0).getNumber())) {
							infos.remove(0);

						}
					}
					BlackNumberInfo info = new BlackNumberInfo();
					// 将输出储存到bean
					info.setNumber(phone);
					info.setMode(mode + "");
					// 将新的信息显示Listview的第一行
					infos.add(0, info);
				} else {
					Toast.makeText(CallSafeActivity.this, "电话号码不能为空", 0).show();
					return;
				}

				// 更新页面
				adapter.notifyDataSetChanged();
				dialog.dismiss();

			}
		});
		// 给取消键设置监听
		bt_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 取消则隐藏
				dialog.dismiss();
			}
		});
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * 滚动监听
	 * 
	 * @author Administrator
	 * 
	 */
	class MyScrollListener implements OnScrollListener {
		/**
		 * 滚动状态发生变化时调用
		 */
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 更具状态分类
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:// 禁止空闲状态
				if (lv_blackNumber.getLastVisiblePosition() == infos.size() - 1) {
					if (isLoading) {
						Toast.makeText(CallSafeActivity.this, "已经在加载数据了", 0)
								.show();
						return;
					}
					if (index >= total) {
						Toast.makeText(CallSafeActivity.this, "已经没有更多数据了", 0)
								.show();
						return;
					}
					isLoading = true;
					// 如果滚动到底部再次请求数据
					fillData();
				}
				break;
			case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸滚动状态

				break;

			default:
				break;
			}

		}

		/**
		 * 滚动时调用
		 */
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * handler用来刷新子线程需要的动作
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			rl_black_loading.setVisibility(View.INVISIBLE);

			// 设置显示到Listview
			if (adapter == null) {
				// 得到适配器
				adapter = new BlackNumberAdapter();
				lv_blackNumber.setAdapter(adapter);
			} else {
				// 如果不为空说明是第二次执行所以就只更新不创建
				adapter.notifyDataSetChanged();
			}
			isLoading = false;
		};
	};

	/**
	 * 用来填充数据
	 */
	public void fillData() {
		rl_black_loading.setVisibility(View.VISIBLE);
		// 耗时操作要放在子线程运行
		new Thread() {
			public void run() {
				BlackNumberDao bd = new BlackNumberDao(CallSafeActivity.this);
				// 获得部分的黑名单信息
				if (infos == null) {
					infos = bd.queryLimit(index);
				} else {
					// 如果不为空则
					// 将查询到的部分黑名单数据增加到总的list里
					infos.addAll(bd.queryLimit(index));
				}
				// 查询数据库中黑名单总数
				total = bd.querytotal();
				// 每次查询后Index自己增加20，配合滚动监听，只要拉到底部就查询新内容
				index += 20;
				// 查询到所有信息好，发送信息让handler显示
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class BlackNumberAdapter extends BaseAdapter {

		public int getCount() {
			// 要到显示到Listview的数量
			return infos.size();
		}

		/**
		 * 每个要显示的Item
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			// convertView为缓存，有缓存就用缓存，没缓存则填充新的View
			if (convertView != null) {
				view = convertView;
				// 将缓存中的Holder拿出来使用
				holder = (ViewHolder) view.getTag();
			} else {
				// 填充新的view
				view = View.inflate(CallSafeActivity.this,
						R.layout.item_blacknumber, null);
				// 创建新的Holder来存储组件，下次就用在find节约资源
				holder = new ViewHolder();
				holder.tv_black_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.tv_black_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.iv_black_delete = (ImageView) view
						.findViewById(R.id.iv_black_delete);
				// 将find完全部组件的Holder存到View中下次使用
				view.setTag(holder);
			}

			// 直接将Holder中的组件设置参数
			holder.tv_black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			// 根据数据库的值设置对应的模式
			if ("0".equals(mode)) {
				holder.tv_black_mode.setText("电话拦截");
			} else if ("1".equals(mode)) {
				holder.tv_black_mode.setText("短信拦截");
			} else if ("2".equals(mode)) {
				holder.tv_black_mode.setText("电话+短信拦截");
			}
			// 给删除图标设置监听
			holder.iv_black_delete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// 点击删除此项黑名单信息
					BlackNumberDao bd = new BlackNumberDao(
							CallSafeActivity.this);
					// 删除数据库信息
					bd.delete(infos.get(position).getNumber());
					// 删除页面信息
					infos.remove(position);
					// 更新页面
					adapter.notifyDataSetChanged();
				}
			});
			// 返回设置好的View
			return view;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

	}

	/**
	 * 用来存储View组件,用来优化
	 * 
	 * @author Administrator
	 * 
	 */
	static class ViewHolder {
		TextView tv_black_number;
		TextView tv_black_mode;
		ImageView iv_black_delete;
	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {
		// 向右滑动返回销魂当前页面
		finish();
		// 设置动画
		previousAnimation();
	}
}
