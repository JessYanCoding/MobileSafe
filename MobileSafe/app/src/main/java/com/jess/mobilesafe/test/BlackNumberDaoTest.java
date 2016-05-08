package com.jess.mobilesafe.test;

import java.util.List;
import java.util.Random;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.jess.mobilesafe.dao.BlackNumberDao;
import com.jess.mobilesafe.db.LockOppenHelper;
import com.jess.mobilesafe.db.MyOppenHelper;
import com.jess.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDaoTest extends AndroidTestCase {
	private BlackNumberDao bd;

	public void test() {
		LockOppenHelper helper = new LockOppenHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
	}

	@Override
	protected void setUp() throws Exception {
		// 在测试执行之前调用
		super.setUp();
		bd = new BlackNumberDao(getContext());
	}

	
	public void add(){
		for (int i = 10; i < 100; i++) {
			bd.add("136787878"+i, new Random().nextInt(3)+"");
		}
		
	}
	public void delete(){
		bd.delete("888");
	}
	public void update(){
		bd.update("110", "2");
	}
	public void query(){
		boolean result = bd.queryNumber("110");
		System.out.println(result);
	}
	public void queryMode(){
		String mode = bd.queryMode("110");
		System.out.println(mode);
	}
	public void queryAll(){
		List<BlackNumberInfo> queryAll = bd.queryAll();
		for (BlackNumberInfo blackNumberInfo : queryAll) {
			System.out.println(blackNumberInfo.toString());
		}
	}
}
