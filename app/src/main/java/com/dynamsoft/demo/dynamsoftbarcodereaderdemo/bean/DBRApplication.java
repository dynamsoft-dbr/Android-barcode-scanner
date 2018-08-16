package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.app.Application;

import org.litepal.LitePal;


public class DBRApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		LitePal.initialize(this);
	}
}
