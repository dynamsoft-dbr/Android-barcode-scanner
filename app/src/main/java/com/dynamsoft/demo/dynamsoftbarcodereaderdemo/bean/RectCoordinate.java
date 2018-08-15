package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * Created by Elemen on 2018/8/15.
 */
@JsonObject
public class RectCoordinate {
	@JsonField
	private ArrayList<RectPoint[]> rectCoord = new ArrayList<>();

	public ArrayList<RectPoint[]> getRectCoord() {
		return rectCoord;
	}

	public void setRectCoord(ArrayList<RectPoint[]> rectCoord) {
		this.rectCoord = rectCoord;
	}
}
