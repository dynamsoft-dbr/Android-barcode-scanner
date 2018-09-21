package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.graphics.YuvImage;
import com.dynamsoft.barcode.TextResult;


public class YuvInfo implements Cloneable{
	public String cacheName = "";
	public YuvImage yuvImage;
	public TextResult[] textResult;

	@Override
	public YuvInfo clone() {
		YuvInfo yuvInfo = null;
		try{
			yuvInfo = (YuvInfo)super.clone();
		} catch (CloneNotSupportedException e){
			e.printStackTrace();
		}
		return yuvInfo;
	}
}
