package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util;

import android.media.ExifInterface;

/**
 * Created by Elemen on 2018/8/8.
 */
public class DBRUtil {
	public static String getCodeFormat(String formatNum){
		String barcodeFormat="0";
		switch (Integer.parseInt(formatNum)) {
			case 234882047:
				barcodeFormat = "all";
				break;
			case 1023:
				barcodeFormat = "OneD";
				break;
			case 1:
				barcodeFormat = "CODE_39";
				break;
			case 2:
				barcodeFormat = "CODE_128";
				break;
			case 4:
				barcodeFormat = "CODE_93";
				break;
			case 8:
				barcodeFormat = "CODABAR";
				break;
			case 16:
				barcodeFormat = "ITF";
				break;
			case 32:
				barcodeFormat = "EAN_13";
				break;
			case 64:
				barcodeFormat = "EAN_8";
				break;
			case 128:
				barcodeFormat = "UPC_A";
				break;
			case 256:
				barcodeFormat = "UPC_E";
				break;
			case 512:
				barcodeFormat = "INDUSTRIAL_25";
				break;
			case 33554432:
				barcodeFormat = "PDF417";
				break;
			case 67108864:
				barcodeFormat = "QR_CODE";
				break;
			case 134217728:
				barcodeFormat = "DATAMATAIX";
				break;
			case 268435456:
				barcodeFormat = "AZTEC";
				break;
			default:
				break;
		}
		return barcodeFormat;
	}

	public static void clearHistory(){

	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				default:
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return degree;
	}
}
