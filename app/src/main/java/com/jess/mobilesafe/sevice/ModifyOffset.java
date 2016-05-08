package com.jess.mobilesafe.sevice;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * 火星地球坐标转化.地图坐标修偏
 * 
 */
public class ModifyOffset {
	private static ModifyOffset modifyOffset;
	static double[] X = new double[660 * 450];
	static double[] Y = new double[660 * 450];


	private ModifyOffset(InputStream inputStream) throws Exception {
		init(inputStream);
	}

	public synchronized static ModifyOffset getInstance(InputStream is) throws Exception {
		if (modifyOffset == null) {
			modifyOffset = new ModifyOffset(is);
		}
		return modifyOffset;
	}

	public void init(InputStream inputStream) throws Exception {
		ObjectInputStream in = new ObjectInputStream(inputStream);
		try {
			int i = 0;
			while (in.available() > 0) {
				if ((i & 1) == 1) {
					Y[(i - 1) >> 1] = in.readInt() / 100000.0d;
					;
				} else {
					X[i >> 1] = in.readInt() / 100000.0d;
					;
				}
				i++;
			}
		} finally {
			if (in != null)
				in.close();
		}
	}

	// standard -> china
	public PointDouble s2c(PointDouble pt) {
		int cnt = 10;
		double x = pt.x, y = pt.y;
		while (cnt-- > 0) {
			if (x < 71.9989d || x > 137.8998d || y < 9.9997d || y > 54.8996d)
				return pt;
			int ix = (int) (10.0d * (x - 72.0d));
			int iy = (int) (10.0d * (y - 10.0d));
			double dx = (x - 72.0d - 0.1d * ix) * 10.0d;
			double dy = (y - 10.0d - 0.1d * iy) * 10.0d;
			x = (x + pt.x + (1.0d - dx) * (1.0d - dy) * X[ix + 660 * iy] + dx
					* (1.0d - dy) * X[ix + 660 * iy + 1] + dx * dy
					* X[ix + 660 * iy + 661] + (1.0d - dx) * dy
					* X[ix + 660 * iy + 660] - x) / 2.0d;
			y = (y + pt.y + (1.0d - dx) * (1.0d - dy) * Y[ix + 660 * iy] + dx
					* (1.0d - dy) * Y[ix + 660 * iy + 1] + dx * dy
					* Y[ix + 660 * iy + 661] + (1.0d - dx) * dy
					* Y[ix + 660 * iy + 660] - y) / 2.0d;
		}
		return new PointDouble(x, y);
	}

	// china -> standard
	public PointDouble c2s(PointDouble pt) {
		int cnt = 10;
		double x = pt.x, y = pt.y;
		while (cnt-- > 0) {
			if (x < 71.9989d || x > 137.8998d || y < 9.9997d || y > 54.8996d)
				return pt;
			int ix = (int) (10.0d * (x - 72.0d));
			int iy = (int) (10.0d * (y - 10.0d));
			double dx = (x - 72.0d - 0.1d * ix) * 10.0d;
			double dy = (y - 10.0d - 0.1d * iy) * 10.0d;
			x = (x + pt.x - (1.0d - dx) * (1.0d - dy) * X[ix + 660 * iy] - dx
					* (1.0d - dy) * X[ix + 660 * iy + 1] - dx * dy
					* X[ix + 660 * iy + 661] - (1.0d - dx) * dy
					* X[ix + 660 * iy + 660] + x) / 2.0d;
			y = (y + pt.y - (1.0d - dx) * (1.0d - dy) * Y[ix + 660 * iy] - dx
					* (1.0d - dy) * Y[ix + 660 * iy + 1] - dx * dy
					* Y[ix + 660 * iy + 661] - (1.0d - dx) * dy
					* Y[ix + 660 * iy + 660] + y) / 2.0d;
		}
		return new PointDouble(x, y);
	}

}

class PointDouble {
	double x, y;

	PointDouble(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "x=" + x + ", y=" + y;
	}
}
