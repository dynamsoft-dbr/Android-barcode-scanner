package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dynamsoft.barcode.afterprocess.jni.AfterProcess;
import com.dynamsoft.barcode.afterprocess.jni.BarcodeRecognitionResult;
import com.dynamsoft.barcode.afterprocess.jni.InputParasOfSwitchImagesFun;
import com.dynamsoft.barcode.afterprocess.jni.StitchImageResult;
import com.dynamsoft.barcode.BarcodeReader;
import com.dynamsoft.barcode.BarcodeReaderException;
import com.dynamsoft.barcode.EnumImagePixelFormat;
import com.dynamsoft.barcode.TextResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Elemen on 2018/7/31.
 */
public class TestActivity extends AppCompatActivity {
	@BindView(R.id.btn_switch)
	Button btnSwitch;
	@BindView(R.id.scale_imageview)
	SubsamplingScaleImageView scaleImageview;

	private BarcodeReader reader;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		ButterKnife.bind(this);
		try {
			reader = new BarcodeReader(getString(R.string.dbr_license));
			JSONObject jsonObject = new JSONObject("{\n" +
					"  \"ImageParameters\": {\n" +
					"    \"Name\": \"Custom_100947_777\",\n" +
					"    \"BarcodeFormatIds\": [\n" +
					"      \"CODE_39\",\n" +
					"      \"CODE_128\",\n" +
					"      \"CODE_93\",\n" +
					"      \"CODABAR\",\n" +
					"      \"ITF\",\n" +
					"      \"EAN_13\",\n" +
					"      \"EAN_8\",\n" +
					"      \"UPC_A\",\n" +
					"      \"UPC_E\"" +
					"    ],\n" +
					"    \"LocalizationAlgorithmPriority\": [\"ConnectedBlock\", \"Lines\", \"Statistics\", \"FullImageAsBarcodeZone\"],\n" +
					"    \"AntiDamageLevel\": 5,\n" +
					"    \"DeblurLevel\":5,\n" +
					"    \"ScaleDownThreshold\": 1000\n" +
					"  },\n" +
					"\"version\":\"1.0\"" +
					"}");
			reader.appendParameterTemplate(jsonObject.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] input2byte(int i) throws IOException {
		InputStream ims = getAssets().open(i + ".png");
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = ims.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		return swapStream.toByteArray();
	}

	@OnClick(R.id.btn_switch)
	public void onViewClicked() {
		try {
			AssetManager manager = getAssets();
			InputStream inputStream1 = manager.open("1.jpg");
			InputStream inputStream2 = manager.open("2.jpg");
			Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream1);
			Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream2);
			TextResult[] results1 = reader.decodeBufferedImage(bitmap1, "");
			TextResult[] results2 = reader.decodeBufferedImage(bitmap2, "");
			ArrayList<TextResult> red1 = new ArrayList<>();
			ArrayList<TextResult> red2 = new ArrayList<>();
			for (int i = 0;i < results1.length; i++){
				for(int j = 0; j <results2.length; j++){
					if(results1[i].barcodeFormat == results2[j].barcodeFormat && results1[i].barcodeText.equals(results2[j].barcodeText)){
						red1.add(results1[i]);
						red2.add(results2[j]);
					}
				}
			}
			for(int i = 0; i < red1.size() - 1; i++){
				int a1 = Math.abs(red1.get(i).localizationResult.resultPoints[0].x - red1.get(i).localizationResult.resultPoints[1].x);
				int a2 = Math.abs(red1.get(i).localizationResult.resultPoints[2].x - red1.get(i).localizationResult.resultPoints[3].x);
				int b1 = Math.abs(red1.get(i).localizationResult.resultPoints[0].y - red1.get(i).localizationResult.resultPoints[3].y);
				int b2 = Math.abs(red1.get(i).localizationResult.resultPoints[1].y - red1.get(i).localizationResult.resultPoints[2].y);

				int a3 = Math.abs(red2.get(i).localizationResult.resultPoints[0].x - red2.get(i).localizationResult.resultPoints[1].x);
				int a4 = Math.abs(red2.get(i).localizationResult.resultPoints[2].x - red2.get(i).localizationResult.resultPoints[3].x);
				int b3 = Math.abs(red2.get(i).localizationResult.resultPoints[0].y - red2.get(i).localizationResult.resultPoints[3].y);
				int b4 = Math.abs(red2.get(i).localizationResult.resultPoints[1].y - red2.get(i).localizationResult.resultPoints[2].y);
				if (Math.abs(((a1 + a1) / 2) - (a3 + a4) / 2) > 3 || Math.abs(((b1 + b2) / 2) - (b3 + b4) / 2) > 3){
					Log.e("", "");
				}
			}
			InputParasOfSwitchImagesFun[] input1 = new InputParasOfSwitchImagesFun[2];
			input1[0] = new InputParasOfSwitchImagesFun();
			input1[0].buffer = convertImage(bitmap1);
			input1[0].width = 1200;
			input1[0].height = 1600;
			input1[0].stride = 1200 * 4;
			input1[0].format = EnumImagePixelFormat.IPF_ARGB_8888;
			input1[0].domainOfImgX = 1200;
			input1[0].domainOfImgY = 1600;
			BarcodeRecognitionResult[] bar1 = new BarcodeRecognitionResult[results1.length];
			for (int i = 0; i < results1.length; ++i) {
				TextResult textResult = results1[i];
				BarcodeRecognitionResult barcodeRecognitionResult = bar1[i] = new BarcodeRecognitionResult();
				barcodeRecognitionResult.barcodeBytes = textResult.barcodeBytes;
				barcodeRecognitionResult.barcodeText = textResult.barcodeText;
				barcodeRecognitionResult.pts = textResult.localizationResult.resultPoints;
				barcodeRecognitionResult.format = textResult.barcodeFormat;
			}
			input1[0].barcodeRecognitionResults = bar1;

			input1[1] = new InputParasOfSwitchImagesFun();
			input1[1].buffer = convertImage(bitmap2);
			input1[1].width = 1200;
			input1[1].height = 1600;
			input1[1].stride = 1200 * 4;
			input1[1].format = EnumImagePixelFormat.IPF_ARGB_8888;
			input1[1].domainOfImgX = 1200;
			input1[1].domainOfImgY = 1600;
			BarcodeRecognitionResult[] bar2 = new BarcodeRecognitionResult[results2.length];
			for (int i = 0; i < results2.length; ++i) {
				TextResult textResult = results2[i];
				BarcodeRecognitionResult barcodeRecognitionResult = bar2[i] = new BarcodeRecognitionResult();
				barcodeRecognitionResult.barcodeBytes = textResult.barcodeBytes;
				barcodeRecognitionResult.barcodeText = textResult.barcodeText;
				barcodeRecognitionResult.pts = textResult.localizationResult.resultPoints;
				barcodeRecognitionResult.format = textResult.barcodeFormat;
			}
			input1[1].barcodeRecognitionResults = bar2;
			StitchImageResult result = AfterProcess.stitchImages(input1);
			Bitmap bitmap = result.image;


/*			Bitmap bm = Bitmap.createBitmap(result.basedImgWidth, result.basedImgHeight, Bitmap.Config.RGB_565);
			ByteBuffer byteBuffer=ByteBuffer.wrap(result.imageBytes);
			byteBuffer.rewind();
			bm.copyPixelsFromBuffer(byteBuffer);
			byteBuffer.position(0);*/
			scaleImageview.setImage(ImageSource.bitmap(bitmap));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BarcodeReaderException e) {
			e.printStackTrace();
		}
	}

	private byte[] convertImage(Bitmap bitmap) {
		int bytes = bitmap.getByteCount();
		ByteBuffer buf = ByteBuffer.allocate(bytes);
		bitmap.copyPixelsToBuffer(buf);
		byte[] byteArray = buf.array();
		return byteArray;
	}
}
