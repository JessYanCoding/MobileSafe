package com.jess.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jess.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends BaseTouch {
	private ContentResolver resolver;
	ArrayList<Map<String, String>> list;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		// 检查联系人信息
		readContact();
		// 拿到listview将联系人展示
		lv = (ListView) findViewById(R.id.lv_contact);
		// 设置适配器进行展示
		lv.setAdapter(new Myadapter());
		// 设置监听器进行相应的逻辑处理
		lv.setOnItemClickListener(new MyListener());
	}

	class MyListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 拿到用户点击的联系人电话
			
			String phone = list.get(position).get("phone");
			// 包装到Intent设置到结果里
			//如果Phone为空就不发送结果
			if (!TextUtils.isEmpty(phone)) {
				
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(2, intent);
			}
			// 关闭页面并返回数据
			finish();
			// 返回动画
			previousAnimation();
		}

	}

	class Myadapter extends BaseAdapter {

		public int getCount() {
			// 总共的Item数量
			return list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			if (convertView == null) {
				// 如果没有缓存则重新填充view
				v = View.inflate(ContactActivity.this, R.layout.item_contact,
						null);
				// 优化将找到的组件封装到Pocket类里
				TextView tv_name = (TextView) v
						.findViewById(R.id.tv_item_contactName);
				TextView tv_phone = (TextView) v
						.findViewById(R.id.tv_item_contactPhone);
				Pocket pocket = new Pocket(tv_name, tv_phone);
				// 设置进去下次就不用find直接可以拿出来用
				v.setTag(pocket);
			} else {
				// 如果有缓存则直接使用缓存
				v = convertView;
			}
			// 优化，将找好的组件拿出来，直接设置效果，
			Pocket pocket = (Pocket) v.getTag();
			pocket.tv_name.setText(list.get(position).get("name"));
			pocket.tv_phone.setText(list.get(position).get("phone"));
			// 返回View 显示到Listview
			return v;
		}

		// 专门来装item组件，下次就不用再find节约资源
		class Pocket {
			TextView tv_name;
			TextView tv_phone;

			Pocket(TextView tv_name, TextView tv_phone) {
				this.tv_name = tv_name;
				this.tv_phone = tv_phone;
			}
		}

	}

	/**
	 * 读取手机的联系人信息
	 */
	public void readContact() {
		// 两个表的uri
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		// 设置list储存联系人信息
		list = new ArrayList<Map<String, String>>();
		// 内容解决者，来访问内容提供者
		resolver = getContentResolver();
		// 查询所有联系人id
		Cursor cr_id = resolver.query(uri, new String[] { "_id" }, null, null,
				null);
		while (cr_id.moveToNext()) {
			int id = cr_id.getInt(0);
			// 拿到所有联系人id，在逐个查找每个联系人的资料
			Cursor cr_data = resolver.query(uriData, new String[] { "data1",
					"mimetype" }, "raw_contact_id = ?",
					new String[] { id + "" }, null);
			// map储存联系人的所有资料
			Map map = new HashMap<String, String>();
			while (cr_data.moveToNext()) {
				String data = cr_data.getString(0);
				String mimetype = cr_data.getString(1);
				// 如果Mimetype满足相应的类型就储存到Map
				if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
					if (!TextUtils.isEmpty(data)) {
						map.put("phone", data);
					}
				}
				if (mimetype.equals("vnd.android.cursor.item/name")) {
					if (!TextUtils.isEmpty(data)) {
						map.put("name", data);
					}
				}

			}
			// 将储存每个联系人资料的map存储到list里，此List含有所有联系人信息
			//细节处理不添加为空的联系人
			if (map.size() > 0) {
				list.add(map);
			}
		}
	}

	@Override
	public void showNextPage() {

	}

	@Override
	public void showPreviousPage() {

	}

	// 返回时动画
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 返回时调用进出动画效果
		previousAnimation();

	}
}
