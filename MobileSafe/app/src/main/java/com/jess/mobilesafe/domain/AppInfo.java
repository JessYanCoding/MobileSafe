package com.jess.mobilesafe.domain;

import android.graphics.drawable.Drawable;



public class AppInfo {
	private String name;//应用名
	private String packageName;//包名
	private Drawable icon;//图标
	private boolean isRom;//文件所在位置，是否为内部存储
	private boolean isUser;//是否为用户文件
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
	public boolean isRom() {
		return isRom;
	}
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName
				+ ", isRom=" + isRom + ", isUser=" + isUser + "]";
	}
	
}
