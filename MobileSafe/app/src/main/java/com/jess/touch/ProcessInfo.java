package com.jess.touch;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	private String name;//应用名
	private String packageName;//包名
	private Drawable icon;//图标
	private long memory;//进程所占内存
	private boolean isUser;//是否为用户进程
	private boolean isCheck;//checkbox是否勾选
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	@Override
	public String toString() {
		return "ProcessInfo [name=" + name + ", packageName=" + packageName
				+ ", memory=" + memory + ", isUser=" + isUser + "]";
	}
	
}
