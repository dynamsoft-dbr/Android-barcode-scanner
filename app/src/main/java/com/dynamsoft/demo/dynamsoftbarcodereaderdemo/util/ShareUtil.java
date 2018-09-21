package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ShareUtil {
	public static final String WEIXIN_PACKAGE_NAME = "";
	public static final String QQ_PACKAGE_NAME = "";
	private Context context;
	public ShareUtil(Context context) {
		this.context = context;
	}

	public static boolean stringCheck(String str) {
		if (null != str && !TextUtils.isEmpty(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * share text
	 *
	 * @param packageName
	 * @param content
	 * @param title
	 * @param subject
	 */
	public void shareText(String packageName, String className, String content, String title, String subject) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		//      if(null != className && null != packageName && !TextUtils.isEmpty(className) && !TextUtils.isEmpty(packageName)){
		//
		//      }else {
		//          if(null != packageName && !TextUtils.isEmpty(packageName)){
		//              intent.setPackage(packageName);
		//          }
		//      }
		if (stringCheck(className) && stringCheck(packageName)) {
			ComponentName componentName = new ComponentName(packageName, className);
			intent.setComponent(componentName);
		} else if (stringCheck(packageName)) {
			intent.setPackage(packageName);
		}

		intent.putExtra(Intent.EXTRA_TEXT, content);
		if (null != title && !TextUtils.isEmpty(title)) {
			intent.putExtra(Intent.EXTRA_TITLE, title);
		}
		if (null != subject && !TextUtils.isEmpty(subject)) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		intent.putExtra(Intent.EXTRA_TITLE, title);
		Intent chooserIntent = Intent.createChooser(intent, "share to：");
		context.startActivity(chooserIntent);
	}

	/**
	 * share web
	 */
	public void shareUrl(String packageName, String className, String content, String title, String subject) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
//      if(null != className && null != packageName && !TextUtils.isEmpty(className) && !TextUtils.isEmpty(packageName)){
//
//      }else {
//          if(null != packageName && !TextUtils.isEmpty(packageName)){
//              intent.setPackage(packageName);
//          }
//      }
		if (stringCheck(className) && stringCheck(packageName)) {
			ComponentName componentName = new ComponentName(packageName, className);
			intent.setComponent(componentName);
		} else if (stringCheck(packageName)) {
			intent.setPackage(packageName);
		}

		intent.putExtra(Intent.EXTRA_TEXT, content);
		if (null != title && !TextUtils.isEmpty(title)) {
			intent.putExtra(Intent.EXTRA_TITLE, title);
		}
		if (null != subject && !TextUtils.isEmpty(subject)) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		intent.putExtra(Intent.EXTRA_TITLE, title);
		Intent chooserIntent = Intent.createChooser(intent, "share to ：");
		context.startActivity(chooserIntent);
	}

	/**
	 * share pic
	 */
	public void shareImg(String packageName, String className, File file) {
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");
			if (stringCheck(packageName) && stringCheck(className)) {
				intent.setComponent(new ComponentName(packageName, className));
			} else if (stringCheck(packageName)) {
				intent.setPackage(packageName);
			}
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			Intent chooserIntent = Intent.createChooser(intent, "share to:");
			context.startActivity(chooserIntent);
		} else {
			Toast.makeText(context, "the file not exist", Toast.LENGTH_SHORT).show();
		}
	}

	public void shareMultiImages(ArrayList<Uri> imageUris, Context context){
		//ArrayList<Uri> imageUris = new ArrayList<>();
		//Uri uri1 = Uri.parse(getResourcesUri(R.drawable.dog));
		//Uri uri2 = Uri.parse(getResourcesUri(R.drawable.shu_1));
		//imageUris.add(uri1); imageUris.add(uri2);
		Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
		mulIntent.setType("image/jpeg");
		context.startActivity(Intent.createChooser(mulIntent,"share multi files"));
	}
	/**
	 * share music
	 */
	public void shareAudio(String packageName, String className, File file) {
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("audio/*");
			if (stringCheck(packageName) && stringCheck(className)) {
				intent.setComponent(new ComponentName(packageName, className));
			} else if (stringCheck(packageName)) {
				intent.setPackage(packageName);
			}
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			Intent chooserIntent = Intent.createChooser(intent, "share to :");
			context.startActivity(chooserIntent);
		} else {
			Toast.makeText(context, "file not exist", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 *
	 */
	public void shareVideo(String packageName, String className, File file) {
		setIntent("video/*", packageName, className, file);
	}

	public void setIntent(String type, String packageName, String className, File file) {
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType(type);
			if (stringCheck(packageName) && stringCheck(className)) {
				intent.setComponent(new ComponentName(packageName, className));
			} else if (stringCheck(packageName)) {
				intent.setPackage(packageName);
			}
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			Intent chooserIntent = Intent.createChooser(intent, "share to:");
			context.startActivity(chooserIntent);
		} else {
			Toast.makeText(context, "file not exist", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * share to wx
	 *
	 * @param title
	 * @param packageName
	 * @param className
	 * @param file
	 */
	public void shareImgToWXCircle(String title, String packageName, String className, File file) {
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent();
			ComponentName comp = new ComponentName(packageName, className);
			intent.setComponent(comp);
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			intent.putExtra("Kdescription", title);
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "file not exist", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 *
	 *
	 * @param packageName
	 */
	public boolean checkInstall(String packageName) {
		try {
			context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, "please install the app first", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public Bitmap getScreenShot(Activity context){
		View dView = context.getWindow().getDecorView();
		dView.setDrawingCacheEnabled(true);
		dView.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
		dView.destroyDrawingCache();
		return bitmap;
	}

	/**
	 *
	 */
	public void toInstallWebView(String url) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
}
