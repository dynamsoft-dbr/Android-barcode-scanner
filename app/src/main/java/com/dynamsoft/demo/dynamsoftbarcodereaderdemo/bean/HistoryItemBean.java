package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * Created by Elemen on 2018/7/3.
 */
@JsonObject
public class HistoryItemBean {
	@JsonField
	private String codeImgPath = "";
	@JsonField
	private ArrayList<String> codeFormat = new ArrayList<>();
	@JsonField
	private ArrayList<String> codeText = new ArrayList<>();
	@JsonField
	private ArrayList<RectPoint[]> rectCoord = new ArrayList<>();

	public ArrayList<RectPoint[]> getRectCoord() {
		return rectCoord;
	}

	public void setRectCoord(ArrayList<RectPoint[]> rectCoord) {
		this.rectCoord = rectCoord;
	}

	public String getCodeImgPath() {
		return codeImgPath;
	}

	public void setCodeImgPath(String codeImgPath) {
		this.codeImgPath = codeImgPath;
	}

	public ArrayList<String> getCodeFormat() {
		return codeFormat;
	}

	public void setCodeFormat(ArrayList<String> codeFormat) {
		this.codeFormat = codeFormat;
	}

	public ArrayList<String> getCodeText() {
		return codeText;
	}

	public void setCodeText(ArrayList<String> codeText) {
		this.codeText = codeText;
	}
}
