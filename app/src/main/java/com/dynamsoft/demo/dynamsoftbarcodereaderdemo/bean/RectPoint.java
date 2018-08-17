package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class RectPoint implements Parcelable {
	@JsonField
	public float x;
	@JsonField
	public float y;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(this.x);
		dest.writeFloat(this.y);
	}

	public RectPoint() {
	}

	protected RectPoint(Parcel in) {
		this.x = in.readFloat();
		this.y = in.readFloat();
	}

	public static final Parcelable.Creator<RectPoint> CREATOR = new Parcelable.Creator<RectPoint>() {
		@Override
		public RectPoint createFromParcel(Parcel source) {
			return new RectPoint(source);
		}

		@Override
		public RectPoint[] newArray(int size) {
			return new RectPoint[size];
		}
	};
}
