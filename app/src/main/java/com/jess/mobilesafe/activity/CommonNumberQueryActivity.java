package com.jess.mobilesafe.activity;

import com.jess.mobilesafe.R;
import com.jess.mobilesafe.dao.CommonDao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends BaseTouch {
	private ExpandableListView elv_common;// 扩展的listview
	private CommonDao cd;// 用来查询常用号码的dao对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commonquery);
		// 创建用来查询常用号码的dao对象
		cd = new CommonDao();
		// 拿到扩展的listview
		elv_common = (ExpandableListView) findViewById(R.id.elv_common);
		elv_common.setAdapter(new MyAdapter());
		elv_common.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 将对应位置view转换成textview
				TextView view = (TextView) v;
				// 拿到内容，取里面的电话号码
				String phone = view.getText().toString().split("\n")[1].trim();
				// 发送拨号器意图
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + phone));
				// 开始意图
				startActivity(intent);
				return true;
			}
		});
	}

	class MyAdapter extends BaseExpandableListAdapter {

		public int getGroupCount() {
			// 组的长度
			return cd.getGroupCount();
		}

		public int getChildrenCount(int groupPosition) {
			// 每组孩子的长度
			return cd.getChildrenCount(groupPosition);
		}

		public Object getGroup(int groupPosition) {
			return null;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		public long getGroupId(int groupPosition) {
			return 0;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		public boolean hasStableIds() {
			// 是否允许id重复
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 设置每组的View
			TextView view = null;
			// 有缓存则使用缓存
			if (convertView != null) {
				view = (TextView) convertView;
			} else {
				// 没缓存则创建新的View
				view = new TextView(CommonNumberQueryActivity.this);
			}
			// 设置相应的内容
			view.setText("     " + cd.getGroupName(groupPosition) + "");
			view.setTextColor(0xaaffffff);
			view.setTextSize(22);
			return view;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// 设置每组每个孩子的View
			TextView view = null;
			// 有缓存则使用缓存
			if (convertView != null) {
				view = (TextView) convertView;
			} else {
				// 没缓存则创建新的View
				view = new TextView(CommonNumberQueryActivity.this);
			}
			// 设置相应的内容
			view.setText(cd.getChildContent(groupPosition, childPosition) + "");
			view.setTextColor(0x88ffffff);
			view.setTextSize(18);
			return view;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// 是否可选
			return true;
		}

	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {
		// 设置返回上一页
		finish();
		previousAnimation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭数据库释放资源
		if (cd != null) {
			cd.close();
		}
	}

}
