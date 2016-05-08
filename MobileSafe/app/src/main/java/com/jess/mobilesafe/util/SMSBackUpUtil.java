package com.jess.mobilesafe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xmlpull.v1.XmlSerializer;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SMSBackUpUtil {
	public static void SMSBackUp(Context context,ProgressInter pi) throws Exception {
		//拿到内容解决者
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		//查询短信的4个内容
		Cursor query = resolver.query(uri, new String[] { "address", "date",
				"body", "type" }, null, null, null);
		int count = query.getCount();
		//将总进度设置给接口
		pi.setMAX(count);
		//拿到xml序列化器
		XmlSerializer xs = Xml.newSerializer();
		//设置备份文件地址
		OutputStream os = new FileOutputStream(new File(
				Environment.getExternalStorageDirectory(), "SMS_backup.xml"));
		xs.setOutput(os, "UTF-8");
		//开始节点
		xs.startDocument("UTF-8", true);
		xs.startTag(null, "SMS");
		int x = 0;
		//遍历短信信息
		while (query.moveToNext()) {
			String address = query.getString(0);
			String date = query.getString(1);
			String body = query.getString(2);
			String type = query.getString(3);
			xs.startTag("", "msg");
			//将得到的短信信息设置给对应节点
			xs.startTag(null, "address");
			xs.text(address);
			xs.endTag(null, "address");

			xs.startTag(null, "date");
			xs.text(date);
			xs.endTag(null, "date");

			xs.startTag(null, "body");
			xs.text(body);
			xs.endTag(null, "body");

			xs.startTag(null, "type");
			xs.text(type);
			xs.endTag(null, "type");

			xs.endTag("", "msg");
			x++;
			pi.setProgress(x);
		}
			//结束节点
			xs.endTag(null, "SMS");
			//告诉系统已经备份完成
			xs.endDocument();
			//关闭输出流
			os.close();
		
	}
	/**
	 * 定义专门用于设置进度的接口
	 * @author Administrator
	 *
	 */
	public interface ProgressInter{
		/**
		 * 设置最大值
		 * @param max 
		 */
		public void setMAX(int max);
		/**
		 * 设置进度
		 * @param progress
		 */
		public void setProgress(int progress);
	}
}
