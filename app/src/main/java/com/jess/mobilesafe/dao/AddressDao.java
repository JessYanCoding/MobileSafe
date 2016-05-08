package com.jess.mobilesafe.dao;

import java.io.ObjectInputStream.GetField;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	public static String addressQuery(String phone) {
		String address = "未知号码";
		// 数据库的位置
		String dataPath = "data/data/com.jess.mobilesafe/files/address.db";
		// 打开数据库的到数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dataPath, null,
				SQLiteDatabase.OPEN_READONLY);
		//过滤手机号码
		if (phone.matches("^1[3-8]\\d{9}$")) {
			Cursor cursor = db.rawQuery(
					"select location from data2 where id = (select outkey from data1 where id=?)",
					new String[] { phone.substring(0, 7) });
			while (cursor.moveToNext()) {
				//查询出结果返回给address
				address = cursor.getString(0);
			}
			
			cursor.close();
		}else if(phone.matches("^\\d+$")){
			//根据手机位数匹配
			switch (phone.length()) {
				case 3:
					address = "公共电话";
					break;
				case 4:
					address = "模拟器";
					break;
				case 5:
					address = "客服电话";
					break;
				case 7:
				case 8:
					address = "本地电话";
					break;
				default:
					// 01088881234
					// 048388888888
					if (phone.startsWith("0") && phone.length() > 10) {// 有可能是长途电话
						// 有些区号是4位,有些区号是3位(包括0)

						// 先查询4位区号
						Cursor cursor = db.rawQuery(
								"select location from data2 where area =?",
								new String[] { phone.substring(1, 4) });

						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						} else {
							cursor.close();

							// 查询3位区号
							cursor = db.rawQuery(
									"select location from data2 where area =?",
									new String[] { phone.substring(1, 3) });

							if (cursor.moveToNext()) {
								address = cursor.getString(0);
							}

							cursor.close();
						}
					}
					break;
				}
			}
		//数据库关闭
		db.close();
		return address;
	}
}
