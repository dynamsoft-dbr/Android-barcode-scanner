package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.BarcodeReaderException;
import com.dynamsoft.barcode.jni.TextResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/7/19.
 */
public class PhotoPreviewActivity extends AppCompatActivity {
	private final int DECODE_FINISHI = 0x0001;
	@BindView(R.id.iv_photo_preview)
	ImageView ivPhotoPreview;
	@BindView(R.id.pb_progress)
	ProgressBar pbProgress;
	private BarcodeReader reader;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DECODE_FINISHI:

					Glide.with(PhotoPreviewActivity.this)
							.load((byte[]) msg.obj)
							.into(ivPhotoPreview);
					ivPhotoPreview.setRotation(90);
					pbProgress.setVisibility(View.GONE);
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photopreview);
		ButterKnife.bind(this);
		pbProgress.setVisibility(View.VISIBLE);
		initBarcodeReader();
		drawRectOnImg(ivPhotoPreview);
	}

	private void initBarcodeReader() {
		try {
			reader = new BarcodeReader(getString(R.string.dbr_license));
			JSONObject jsonObject = new JSONObject("{\n" +
					"  \"ImageParameters\": {\n" +
					"    \"Name\": \"Custom_100947_777\",\n" +
					"    \"BarcodeFormatIds\": [\n" +
					"      \"QR_CODE\"\n" +
					"    ],\n" +
					"    \"LocalizationAlgorithmPriority\": [\"ConnectedBlock\", \"Lines\", \"Statistics\", \"FullImageAsBarcodeZone\"],\n" +
					"    \"AntiDamageLevel\": 5,\n" +
					"    \"DeblurLevel\":5,\n" +
					"    \"ScaleDownThreshold\": 1000\n" +
					"  }\n" +
					"}");
			reader.appendParameterTemplate(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawRectOnImg(ImageView imageView) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(9f);
				paint.setColor(getResources().getColor(R.color.aboutOK));
				paint.setAntiAlias(true);
				Path path = new Path();
				Bitmap oriBitmap = BitmapFactory.decodeFile(new File(getExternalFilesDir("photos"),
						getIntent().getStringExtra("photoname") + ".jpg").getAbsolutePath());
				Bitmap rectBitmap = oriBitmap.copy(Bitmap.Config.ARGB_8888, true);
				try {
					TextResult[] textResults = reader.decodeBufferedImage(rectBitmap, "Custom_100947_777");
					if (textResults != null && textResults.length > 0) {
						Canvas canvas = new Canvas(rectBitmap);
						for (int i = 0; i < textResults.length; i++) {
							path.reset();
							path.moveTo(textResults[i].localizationResult.resultPoints[0].x, textResults[i].localizationResult.resultPoints[0].y);
							path.lineTo(textResults[i].localizationResult.resultPoints[1].x, textResults[i].localizationResult.resultPoints[1].y);
							path.lineTo(textResults[i].localizationResult.resultPoints[2].x, textResults[i].localizationResult.resultPoints[2].y);
							path.lineTo(textResults[i].localizationResult.resultPoints[3].x, textResults[i].localizationResult.resultPoints[3].y);
							path.close();
							canvas.drawPath(path, paint);
						}
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						rectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						byte[] bytes = baos.toByteArray();
						Message message = mHandler.obtainMessage();
						message.what = DECODE_FINISHI;
						message.obj = bytes;
						mHandler.sendMessage(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BarcodeReaderException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
