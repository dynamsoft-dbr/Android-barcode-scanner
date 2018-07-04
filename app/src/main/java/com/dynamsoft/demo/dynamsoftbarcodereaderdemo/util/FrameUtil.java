package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util;

import android.util.Log;

import com.otaliastudios.cameraview.Size;

/**
 * Created by Elemen on 2018/7/4.
 */
public class FrameUtil {


	public static float calculatePreviewScale(Size size, int viewWidth, int viewHeight) {
		if (size == null) {
			return 0;
		}
		float previewScale;
		boolean dependOnWid;
		if (size.getHeight() > size.getWidth()) {
			if (((float) viewWidth / (float) size.getWidth()) > ((float) viewHeight / (float) size.getHeight())) {
				previewScale = (float) viewWidth / (float) size.getWidth();
				dependOnWid = true;
				Log.d("scaletype", "0");
			} else {
				previewScale = (float) (viewHeight) / (float) size.getHeight();
				dependOnWid = false;
				Log.d("scaletype", "1");

			}
		} else {
			if (((float) viewWidth / (float) size.getHeight()) > ((float) viewHeight / (float) size.getWidth())) {
				previewScale = (float) viewWidth / (float) size.getHeight();
				dependOnWid = true;
				Log.d("scaletype", "2");

			} else {
				previewScale = (float) (viewHeight) / (float) size.getWidth();
				dependOnWid = false;
				Log.d("scaletype", "3");

			}
		}
		Log.d("PreviewFrameHelper", " previewSize: " + size.getWidth() + " * "
				+ size.getHeight() + "previewscale: " + previewScale);
		return  previewScale;
	}
}
