package com.jess.mobilesafe.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	public static String BytyToString(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int num = 0;
		while ((num = in.read(buf)) != -1) {
			out.write(buf, 0, buf.length);
		}
		String result = out.toString();
		out.close();
		return result;
	}
}
