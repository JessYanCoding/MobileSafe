package com.jess.mobilesafe.test;

import java.util.List;

import com.jess.mobilesafe.domain.ProcessInfo;
import com.jess.mobilesafe.engine.ProcessInfoProvider;
import com.jess.mobilesafe.util.ProcessInfoUtil;

import android.test.AndroidTestCase;
import android.text.format.Formatter;

public class ProcessTest extends AndroidTestCase{
		public void processtotal(){
			try {
				long totalMemory = ProcessInfoUtil.getTotalMemory(getContext());
				System.out.println(Formatter.formatFileSize(getContext(), totalMemory));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void avai(){
			long memory = ProcessInfoUtil.getAvaiMemory(getContext());
			System.out.println(Formatter.formatFileSize(getContext(), memory));
		}
		
		public void getAll(){
			List<ProcessInfo> infos = ProcessInfoProvider.getProcessInfo(getContext());
			for (ProcessInfo info : infos) {
				System.out.println(info.toString());				
			}
		}
}
