package com.jess.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {
	SQLiteDatabase db;
	// 初始化
	public AntivirusDao() {
		// 数据库的位置
		String path = "data/data/com.jess.mobilesafe/files/antivirus.db";
		// 打开数据库的到数据库对象
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
	}
	public String query(String md5){
		String result=null;
		Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
			
		}
		return result;
	}
	/**
	 * 关闭数据库释放资源
	 */
	public void close(){
		if (db!=null) {
			db.close();
		}
	}
}
