package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.TextResult;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Elemen on 2018/7/2.
 */
public class StartupActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
	private static final int PRC_PHOTO_PICKER = 1;
	private static final int RC_CHOOSE_PHOTO = 1;
	private static final String TAG = "StartupActivity";
	@BindView(R.id.imageButton)
	ImageButton imageButton;
	@BindView(R.id.imageButton2)
	ImageButton imageButton2;
	@BindView(R.id.imageButton3)
	ImageButton imageButton3;
	@BindView(R.id.tv_history)
	TextView tvHistory;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		ButterKnife.bind(this);
	}


	@OnClick({R.id.imageButton, R.id.imageButton2, R.id.imageButton3, R.id.tv_history})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.imageButton:

			case R.id.imageButton2:

			case R.id.imageButton3:
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.tv_history:
				startActivity(new Intent(StartupActivity.this,HistoryActivity.class));
				break;
			default:
				break;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

	}

	@Override
	public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

	}

}


