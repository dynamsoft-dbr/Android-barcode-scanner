package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

/**
 * Created by Elemen on 2018/7/3.
 */
public class HistoryItemBean {
	public String codeImgPath = "";
	public String codeFormat = "";
	public String codeText = "";

	public HistoryItemBean(String codeImgPath, String codeFormat, String codeText) {
		this.codeImgPath = codeImgPath;
		this.codeFormat = codeFormat;
		this.codeText = codeText;
	}
}
