package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.graphics.YuvImage;

import com.dynamsoft.barcode.jni.TextResult;

/**
 * Created by Elemen on 2018/7/27.
 */
public class YuvInfo {
	public String cacheName = "";
	public YuvImage yuvImage;
	public TextResult[] textResult;
}
