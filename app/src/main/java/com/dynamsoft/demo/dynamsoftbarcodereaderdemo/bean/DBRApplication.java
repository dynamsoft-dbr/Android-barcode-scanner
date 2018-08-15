package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.app.Application;

import org.litepal.LitePal;


/**
 * Created by Elemen on 2018/7/11.
 */
public class DBRApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		LitePal.initialize(this);
	}
}
