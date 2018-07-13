package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.dynamsoft.barcode.jni.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.otaliastudios.cameraview.Size;

import java.util.ArrayList;

/**
 * Created by Elemen on 2018/7/4.
 */
public class FrameUtil {
	private int viewWidth;
	private int viewHeight;
	private boolean dependOnWid;

	public static Bitmap rotateBitmap(Bitmap origin) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(90);
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		origin.recycle();
		return newBM;
	}

	public float calculatePreviewScale(Size size, int viewWidth, int viewHeight) {
		if (size == null) {
			return 0;
		}
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		float previewScale;
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
				+ size.getHeight() + "previewscale: " + previewScale + "hudwidth" + viewWidth+" hudheight"+viewHeight);
		return previewScale;
	}

	public ArrayList<RectPoint[]> handlePoints(TextResult[] textResults, float previewScale, int srcBitmapHeight,int srcBitmapWidth) {
		ArrayList<RectPoint[]> rectCoord = new ArrayList<>();
		RectPoint point0;
		RectPoint point1;
		RectPoint point2;
		RectPoint point3;
		RectPoint[] points;
		for (int i = 0; i < textResults.length; i++) {
			points = new RectPoint[4];
			point0 = new RectPoint();
			point1 = new RectPoint();
			point2 = new RectPoint();
			point3 = new RectPoint();
			if(dependOnWid){
				point0.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[0].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point0.y = textResults[i].localizationResult.resultPoints[0].x * previewScale - (srcBitmapWidth * previewScale - viewHeight) / 2;
				point1.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[1].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point1.y = textResults[i].localizationResult.resultPoints[1].x * previewScale - (srcBitmapWidth * previewScale - viewHeight) / 2 ;
				point2.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[2].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point2.y = textResults[i].localizationResult.resultPoints[2].x * previewScale - (srcBitmapWidth * previewScale - viewHeight) / 2 ;
				point3.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[3].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point3.y = textResults[i].localizationResult.resultPoints[3].x * previewScale - (srcBitmapWidth * previewScale - viewHeight) / 2 ;
				points[0] = point0;
				points[1] = point1;
				points[2] = point2;
				points[3] = point3;
				rectCoord.add(points);
			}
			else {
				point0.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[0].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point0.y = textResults[i].localizationResult.resultPoints[0].x * previewScale;
				point1.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[1].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point1.y = textResults[i].localizationResult.resultPoints[1].x * previewScale;
				point2.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[2].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point2.y = textResults[i].localizationResult.resultPoints[2].x * previewScale;
				point3.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[3].y) * previewScale - (srcBitmapHeight * previewScale - viewWidth) / 2;
				point3.y = textResults[i].localizationResult.resultPoints[3].x * previewScale;
				points[0] = point0;
				points[1] = point1;
				points[2] = point2;
				points[3] = point3;
				rectCoord.add(points);
			}
		}
		return rectCoord;
	}
}
