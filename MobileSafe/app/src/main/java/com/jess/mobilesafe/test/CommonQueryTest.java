package com.jess.mobilesafe.test;

import com.jess.mobilesafe.dao.CommonDao;

import android.test.AndroidTestCase;

public class CommonQueryTest extends AndroidTestCase {
		public void test(){
			CommonDao cd  = new CommonDao();
			System.out.println(cd.getChildContent(2, 4));
			
		}
}
