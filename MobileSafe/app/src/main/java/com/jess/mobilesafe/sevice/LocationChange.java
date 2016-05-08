package com.jess.mobilesafe.sevice;

import java.io.InputStream;


public class LocationChange {
	public static PointDouble S2C(double longitude, double latitude,InputStream in) {
		// 104.14201748 longitude
		// 30.65283675 latitude
		try {
			ModifyOffset instance = ModifyOffset.getInstance(in);
			PointDouble s2c = instance
					.s2c(new PointDouble(longitude, latitude));
			return s2c;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
