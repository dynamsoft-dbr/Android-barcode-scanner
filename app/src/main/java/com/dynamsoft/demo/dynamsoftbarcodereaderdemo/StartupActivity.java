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

	private ArrayList<String> filePath;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					TextResult[] results = (TextResult[]) msg.obj;
					final AlertDialog.Builder builder = new AlertDialog.Builder(StartupActivity.this)
							.setTitle("Barcode Result");
					String resultMessage = "Total barcode(s) found: " + String.valueOf(results.length) + "\n";
					String barcodeFormat = "";
					for (int i = 0; i < results.length; i++) {
						if (results[i].localizationResult != null &&
								results[i].localizationResult.resultPoints != null &&
								results[i].localizationResult.resultPoints.length > 0) {

							int x0 = results[i].localizationResult.resultPoints[0].x;
							int y0 = results[i].localizationResult.resultPoints[0].y;
							int x1 = results[i].localizationResult.resultPoints[1].x;
							int y1 = results[i].localizationResult.resultPoints[1].y;
							int x2 = results[i].localizationResult.resultPoints[2].x;
							int y2 = results[i].localizationResult.resultPoints[2].y;
							int x3 = results[i].localizationResult.resultPoints[3].x;
							int y3 = results[i].localizationResult.resultPoints[3].y;
							int[] xAarray = new int[]{x0, x1, x2, x3};
							int[] yAarray = new int[]{y0, y1, y2, y3};
							Arrays.sort(xAarray);
							Arrays.sort(yAarray);
							switch (results[i].barcodeFormat) {
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
								default:
									break;
							}
							resultMessage += "Barcode : " + String.valueOf(i + 1) + "\nType : " + barcodeFormat + "\nResult : " + results[i].barcodeText + "\nRegion : {Left : " + xAarray[0]
									+ " Top : " + yAarray[0] + " Right : " + xAarray[3] + " Bottom : " + yAarray[3]
									+ "}\n\n";
						}
					}
					builder.setMessage(resultMessage);
					builder.show();
					break;
				default:
					break;
			}
		}
	};

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
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.imageButton3:
				choicePhotoWrapper();
				break;
			case R.id.tv_history:
				startActivity(new Intent(StartupActivity.this,HistoryActivity.class));
				break;
			default:
				break;
		}
	}

	@AfterPermissionGranted(PRC_PHOTO_PICKER)
	private void choicePhotoWrapper() {
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
		if (EasyPermissions.hasPermissions(this, perms)) {

			//File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

			Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
					.selectedPhotos(null)
					.pauseOnScroll(false)
					.maxChooseCount(1)
					.build();
			startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
			//ArrayList<String> Temp = BGAPhotoPickerActivity.getSelectedPhotos(photoPickerIntent);
			//for (String t:Temp) {
			//	Log.e("Test", t);
			//}
		} else {
			EasyPermissions.requestPermissions(this, "Need permissions!", PRC_PHOTO_PICKER, perms);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_CHOOSE_PHOTO && resultCode == RESULT_OK) {
			filePath = BGAPhotoPickerActivity.getSelectedPhotos(data);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BarcodeReader reader = new BarcodeReader("f0068MgAAAB1lHa1TS73f6hvSsyG9UkU+EITa8w0074QekQD7/go" +
								"xYCguWUiLgYMKRg4ta39gsM08V5J5F3H0l6puHcJ0Yso=");
						InputStream inputStream = new FileInputStream(filePath.get(0));
						byte[] bytes = new byte[inputStream.available()];
						inputStream.read(bytes);
						inputStream.close();
						TextResult[] results = reader.decodeFileInMemory(bytes, "");
						Message message = handler.obtainMessage();
						message.obj = results;
						message.what = 0;
						handler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}


