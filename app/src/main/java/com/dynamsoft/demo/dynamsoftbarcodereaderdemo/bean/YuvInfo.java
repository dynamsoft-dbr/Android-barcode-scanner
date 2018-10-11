package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.graphics.YuvImage;
import com.dynamsoft.barcode.TextResult;


public class YuvInfo{
	public String cacheName = "";
	public YuvImage yuvImage;
	public TextResult[] textResult;
	public YuvInfo deepClone() {
		YuvInfo yuvInfo = new YuvInfo();
		yuvInfo.textResult = this.textResult;
		byte[] bytes = new byte[this.yuvImage.getYuvData().length];
		System.arraycopy(this.yuvImage.getYuvData(), 0, bytes, 0, this.yuvImage.getYuvData().length);
		yuvInfo.yuvImage = new YuvImage(bytes, yuvImage.getYuvFormat(), yuvImage.getWidth(), yuvImage.getHeight(), null);
		yuvInfo.textResult = new TextResult[this.textResult.length];
		System.arraycopy(this.textResult, 0, yuvInfo.textResult, 0, this.textResult.length);
		return yuvInfo;
	}
}
