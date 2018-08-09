package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dynamsoft.barcode.afterprocess.jni.AfterProcess;
import com.dynamsoft.barcode.afterprocess.jni.StitchImageResult;
import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.BarcodeReaderException;
import com.dynamsoft.barcode.jni.EnumImagePixelFormat;
import com.dynamsoft.barcode.jni.TextResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

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
			InputStream inputStream1 = manager.open("123.png");
			InputStream inputStream2 = manager.open("456.png");
			Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream1);
			Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream2);
			TextResult[] results1 = reader.decodeBufferedImage(bitmap1,"");
			TextResult[] results2 = reader.decodeBufferedImage(bitmap2,"");
			StitchImageResult result = AfterProcess.stitchImage(convertImage(bitmap1), convertImage(bitmap2),
					EnumImagePixelFormat.IPF_ARGB_8888, 688 * 4, 688 * 4, results1, results2,
					688, 449, 688, 591);
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
