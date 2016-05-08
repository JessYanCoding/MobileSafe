package com.jess.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jess.mobilesafe.db.MyOppenHelper;
import com.jess.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {

	private MyOppenHelper helper;
	String table = "blacknumber";// 表名创建为成员变量，以免写错

	public BlackNumberDao(Context context) {
		// 构造方法中创建helper
		helper = new MyOppenHelper(context);

	}

	/**
	 * 增加黑名单到数据库
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式
	 */
	public void add(String number, String mode) {
		if (queryNumber(number)) {
			update(number, mode);
		}else{
			// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
			SQLiteDatabase db = helper.getWritableDatabase();
			// 传入的参数封装到contentValues
			ContentValues values = new ContentValues();
			values.put("number", number);
			values.put("mode", mode);
			// 插入数据库
			db.insert(table, null, values);
			// 关闭数据库
			db.close();
		}
	}

	/**
	 * 删除黑名单
	 * 
	 * @param number
	 *            黑名单号码
	 */
	public void delete(String number) {
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 删除黑名单
		db.delete(table, "number=?", new String[] { number });
		// 关闭数据库
		db.close();
	}

	/**
	 * 修改黑马单
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式
	 */
	public void update(String number, String mode) {
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 传入的要修改的参数封装到contentValues
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		// 修改数据库中的参数
		db.update(table, values, "number=?", new String[] { number });
		// 关闭数据库
		db.close();
	}

	/**
	 * 查询该黑名单号码是否存在
	 * 
	 * @param number
	 *            黑马单号码
	 * @return true为存在，false为不存在
	 */
	public boolean queryNumber(String number) {
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 查询数据库
		Cursor query = db.query(table, new String[] { "number" }, "number=?",
				new String[] { number }, null, null, null);
		// 如果查询到就是存在
		boolean isExist = query.moveToNext();
		// 关闭数据库
		db.close();
		// 返回结果
		return isExist;
	}

	/**
	 * 查询拦截模式
	 * 
	 * @param number
	 *            黑马单号码
	 * @return 返回对应的拦截模式，Null为没有
	 */
	public String queryMode(String number) {
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 查询数据库
		Cursor query = db.query(table, new String[] { "mode" }, "number=?",
				new String[] { number }, null, null, null);
		String mode = null;
		if (query.moveToNext()) {
			// 得到拦截模式
			mode = query.getString(0);
		}
		// 关闭数据库
		db.close();
		// 返回结果
		return mode;
	}

	/**
	 * 查询全部的黑名单信息
	 * 
	 * @return 返回保存全部黑名单信息的List
	 */
	public List<BlackNumberInfo> queryAll() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建list保存所有黑名单信息
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 查询数据库
		Cursor query = db.query(table, new String[] { "number", "mode" }, null,
				null, null, null, null);
		while (query.moveToNext()) {
			// 创建blacknumberinfo对象保存每个黑名单的信息
			BlackNumberInfo info = new BlackNumberInfo();
			// 保存号码
			info.setNumber(query.getString(0));
			// 保存拦截模式
			info.setMode(query.getString(1));
			// 将单个黑名单信息保存进List集合
			list.add(info);
		}
		db.close();
		return list;
	}

	/**
	 * 分批查询数据，每次查询20个
	 * 
	 * @param index
	 *            查询的起始坐标
	 * @return 返回保存全部黑名单信息的List
	 */
	public List<BlackNumberInfo> queryLimit(int index) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建list保存所有黑名单信息
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 查询数据库
		Cursor query = db.rawQuery(
				"select number,mode from blacknumber order by _id desc limit 20 offset ?",
				new String[] { index + "" });
		while (query.moveToNext()) {
			// 创建blacknumberinfo对象保存每个黑名单的信息
			BlackNumberInfo info = new BlackNumberInfo();
			// 保存号码
			info.setNumber(query.getString(0));
			// 保存拦截模式
			info.setMode(query.getString(1));
			// 将单个黑名单信息保存进List集合
			list.add(info);
		}
		db.close();
		return list;
	}
	/**
	 * 查询黑名单总数
	 * @return 黑名单的总数
	 */
	public int querytotal() {
		// 创建数据，每个方法单独创建数据库，以便每次用完数据库后关闭
		SQLiteDatabase db = helper.getWritableDatabase();
		// 查询数据库
		Cursor query = db.rawQuery("select count(*) from blacknumber", null);
		int count = 0;
		if (query.moveToNext()) {
			//得到黑名单总数
			count = query.getInt(0);
		}
		//关闭数据库
		db.close();
		return count;
	}

}
