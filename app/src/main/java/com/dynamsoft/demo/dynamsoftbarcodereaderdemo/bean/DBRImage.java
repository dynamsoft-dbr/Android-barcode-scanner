package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import com.dynamsoft.barcode.Point;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;

public class DBRImage extends LitePalSupport {
	@Column(defaultValue = "")
	private String codeImgPath;

	@Column(defaultValue = "")
	private String fileName;

	private ArrayList<String> codeFormat = new ArrayList<>();

	private ArrayList<String> codeText = new ArrayList<>();

	private ArrayList<Point[]> codePoint = new ArrayList<>();

	private ArrayList<byte[]> codeBytes = new ArrayList<>();


	private long decodeTime = 0;

	private int scaleValue = -1;

	public int getScaleValue() {
		return scaleValue;
	}

	public void setScaleValue(int scaleValue) {
		this.scaleValue = scaleValue;
	}

	public long getDecodeTime() {
		return decodeTime;
	}

	public void setDecodeTime(long decodeTime) {
		this.decodeTime = decodeTime;
	}

	public String getRectCoord() {
		return rectCoord;
	}

	public void setRectCoord(String rectCoord) {
		this.rectCoord = rectCoord;
	}

	@Column(defaultValue = "")
	private String rectCoord;

	public String getCodeImgPath() {
		return codeImgPath;
	}

	public void setCodeImgPath(String codeImgPath) {
		this.codeImgPath = codeImgPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public ArrayList<Point[]> getCodePoint() {
		return codePoint;
	}

	public void setCodePoint(ArrayList<Point[]> codePoint) {
		this.codePoint = codePoint;
	}

	public ArrayList<byte[]> getCodeBytes() {
		return codeBytes;
	}

	public void setCodeBytes(ArrayList<byte[]> codeBytes) {
		this.codeBytes = codeBytes;
	}
}
