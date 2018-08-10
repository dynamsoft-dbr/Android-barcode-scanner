package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.dynamsoft.barcode.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;

import java.util.ArrayList;

import io.fotoapparat.parameter.Resolution;

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

	public static byte[] rotateYUV420sp270(byte[] src, int width, int height) {
		int count = 0;
		int uvHeight = height >> 1;
		int imgSize = width * height;
		byte[] des = new byte[imgSize * 3 >> 1];
		//copy y
		for (int j = width - 1; j >= 0; j--) {
			for (int i = 0; i < height; i++) {
				des[count++] = src[width * i + j];
			}
		}
		//u,v
		for (int j = width - 1; j > 0; j -= 2) {
			for (int i = 0; i < uvHeight; i++) {
				des[count++] = src[imgSize + width * i + j - 1];
				des[count++] = src[imgSize + width * i + j];
			}
		}
		return des;
	}

	public static byte[] rotateYUVDegree90(byte[] data, int imageWidth, int imageHeight) {
		byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
		int i = 0;
		for (int x = 0; x < imageWidth; x++) {
			for (int y = imageHeight - 1; y >= 0; y--) {
				yuv[i] = data[y * imageWidth + x];
				i++;
			}
		}
		i = imageWidth * imageHeight * 3 / 2 - 1;
		for (int x = imageWidth - 1; x > 0; x = x - 2) {
			for (int y = 0; y < imageHeight / 2; y++) {
				yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
				i--;
				yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
				i--;
			}
		}
		return yuv;
	}

	public float calculatePreviewScale(Resolution size, int viewWidth, int viewHeight) {
		if (size == null) {
			return 0;
		}
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		float previewScale;
		if (size.height > size.width) {
			if (((float) viewWidth / (float) size.width) > ((float) viewHeight / (float) size.height)) {
				previewScale = (float) viewWidth / (float) size.width;
				dependOnWid = true;
				Log.d("scaletype", "0");
			} else {
				previewScale = (float) (viewHeight) / (float) size.height;
				dependOnWid = false;
				Log.d("scaletype", "1");

			}
		} else {
			if (((float) viewWidth / (float) size.height) > ((float) viewHeight / (float) size.width)) {
				previewScale = (float) viewWidth / (float) size.height;
				dependOnWid = true;
				Log.d("scaletype", "2");

			} else {
				previewScale = (float) (viewHeight) / (float) size.width;
				dependOnWid = false;
				Log.d("scaletype", "3");

			}
		}
		Log.d("PreviewFrameHelper", " previewSize: " + size.width + " * "
				+ size.height + "previewscale: " + previewScale + "hudwidth" + viewWidth + " hudheight" + viewHeight);
		return previewScale;
	}

	public ArrayList<RectPoint[]> handlePoints(TextResult[] textResults, float previewScale, int srcBitmapHeight, int srcBitmapWidth) {
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

	public ArrayList<RectPoint[]> rotatePoints(TextResult[] textResults, int srcBitmapHeight, int srcBitmapWidth) {
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
			point0.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[0].y);
			point0.y = textResults[i].localizationResult.resultPoints[0].x;
			point1.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[1].y);
			point1.y = textResults[i].localizationResult.resultPoints[1].x;
			point2.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[2].y);
			point2.y = textResults[i].localizationResult.resultPoints[2].x;
			point3.x = (srcBitmapHeight - textResults[i].localizationResult.resultPoints[3].y);
			point3.y = textResults[i].localizationResult.resultPoints[3].x;
			points[0] = point0;
			points[1] = point1;
			points[2] = point2;
			points[3] = point3;
			rectCoord.add(points);
		}
		return rectCoord;
	}
}
