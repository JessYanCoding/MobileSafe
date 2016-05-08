package com.jess.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.jess.mobilesafe.db.LockOppenHelper;
import com.jess.mobilesafe.db.MyOppenHelper;
import com.jess.mobilesafe.domain.BlackNumberInfo;

public class LockDao {

	private LockOppenHelper helper;
	SQLiteDatabase db;
	String table = "lock";// 表名创建为成员变量，以免写错
	Context context;

	public LockDao(Context context) {
		// 构造方法中创建helper
		this.context = context;
		helper = new LockOppenHelper(context);
		db = helper.getWritableDatabase();
	}

	public void add(String packageName) {
		if (queryLock(packageName)) {
			return;
		}else{
			// 传入的参数封装到contentValues
			ContentValues values = new ContentValues();
			values.put("packageName", packageName);
			// 插入数据库
			db.insert(table, null, values);
			context.getContentResolver().notifyChange(Uri.parse("content://com.jess.change"), null);
			
		}
	}

	public void delete(String packageName) {
		// 删除黑名单
		db.delete(table, "packageName=?", new String[] { packageName });
		context.getContentResolver().notifyChange(Uri.parse("content://com.jess.change"), null);
	}


	public boolean queryLock(String packageName) {
		// 查询数据库
		Cursor query = db.query(table, new String[] { "packageName" }, "packageName=?",
				new String[] { packageName }, null, null, null);
		// 如果查询到就是存在
		boolean isExist = query.moveToNext();
		// 返回结果
		return isExist;
	}
	
	public List<String> queryAll() {
		List<String> result = new ArrayList<String>();
		// 查询数据库
		Cursor query = db.query(table, new String[] { "packageName" }, null,
				null, null, null, null);
		while (query.moveToNext()) {
			result.add(query.getString(0));
		}
		if (result.size()>0) {
			
			return result;
		}else{
			return null;
		}
	}
	
	public void close(){
		db.close();
	}

}
