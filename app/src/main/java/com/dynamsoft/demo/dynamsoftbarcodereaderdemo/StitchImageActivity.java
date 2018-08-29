package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bluelinelabs.logansquare.LoganSquare;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dynamsoft.barcode.Point;
import com.dynamsoft.barcode.afterprocess.jni.AfterProcess;
import com.dynamsoft.barcode.afterprocess.jni.BarcodeRecognitionResult;
import com.dynamsoft.barcode.afterprocess.jni.InputParasOfSwitchImagesFun;
import com.dynamsoft.barcode.afterprocess.jni.StitchImageResult;
import com.dynamsoft.barcode.BarcodeReader;
import com.dynamsoft.barcode.BarcodeReaderException;
import com.dynamsoft.barcode.EnumImagePixelFormat;
import com.dynamsoft.barcode.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectCoordinate;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;

import junit.framework.Assert;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/8/9.
 */
public class StitchImageActivity extends AppCompatActivity {
    @BindView(R.id.scale_imageview)
    SubsamplingScaleImageView scaleImageview;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    private String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
    private BarcodeReader reader;
    private List<DBRImage> dbrImageList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            scaleImageview.setImage(ImageSource.bitmap((Bitmap) msg.obj));
            pbProgress.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stitch_image);
        ButterKnife.bind(this);
        dbrImageList = LitePal.findAll(DBRImage.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = readImage();
                Message message = handler.obtainMessage();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();
    }

    private Bitmap readImage() {

        try {
            InputParasOfSwitchImagesFun[] input = new InputParasOfSwitchImagesFun[dbrImageList.size()];
            for (int i = 0; i < dbrImageList.size(); i++) {
                Bitmap bitmap = decodeFile(dbrImageList.get(i).getCodeImgPath());
                ArrayList<RectPoint[]> rectPoints = LoganSquare.parse(dbrImageList.get(i).getRectCoord(), RectCoordinate.class).getRectCoord();
                input[i] = new InputParasOfSwitchImagesFun();
                input[i].buffer = convertImage(bitmap);
                input[i].width = bitmap.getWidth();
                input[i].height = bitmap.getHeight();
                input[i].format = EnumImagePixelFormat.IPF_ARGB_8888;
                input[i].stride = bitmap.getWidth() * 4;
                input[i].domainOfImgX = bitmap.getWidth();
                input[i].domainOfImgY = bitmap.getHeight();
                BarcodeRecognitionResult[] b = new BarcodeRecognitionResult[dbrImageList.get(i).getCodeText().size()];
                for (int j = 0; j < dbrImageList.get(i).getCodeText().size(); j++) {
                    BarcodeRecognitionResult barcodeRecognitionResult = b[j] = new BarcodeRecognitionResult();
                    barcodeRecognitionResult.barcodeBytes = dbrImageList.get(i).getCodeText().get(j).getBytes();
                    barcodeRecognitionResult.format = Integer.valueOf(dbrImageList.get(i).getCodeFormat().get(j));
                    barcodeRecognitionResult.barcodeText = dbrImageList.get(i).getCodeText().get(j);
                    Point[] points = new Point[rectPoints.get(j).length];
                    for (int k = 0; k < rectPoints.get(j).length; k++) {
                        points[k] = new Point();
                        points[k].x = (int) rectPoints.get(j)[k].x;
                        points[k].y = (int) rectPoints.get(j)[k].y;
                    }
                    barcodeRecognitionResult.pts = points;
                }
                input[i].barcodeRecognitionResults = b;
            }
            StitchImageResult result = AfterProcess.stitchImages(input);
            return result.image;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

		/*Bitmap bitmap1;
		Bitmap bitmap2;
		Bitmap bitmap3 = null;
		TextResult[] textResults1;
		TextResult[] textResults2;
		BarcodeRecognitionResult[] localizationResults = null;
		for (int i = 0; i < fileNames.length; i++) {
			Log.d("result act", "result : " + i);
			if (i == 0) {
				bitmap1 = decodeFile(fileNames[0]);
				bitmap2 = decodeFile(fileNames[1]);
				textResults1 = decodeImage(bitmap1);
				textResults2 = decodeImage(bitmap2);

				InputParasOfSwitchImagesFun[] input1 = new InputParasOfSwitchImagesFun[2];
				input1[0] = new InputParasOfSwitchImagesFun();
				input1[0].buffer = convertImage(bitmap1);
				input1[0].width = bitmap1.getWidth();
				input1[0].height = bitmap1.getHeight();
				input1[0].stride = bitmap1.getWidth() * 4;
				input1[0].format = EnumImagePixelFormat.IPF_ARGB_8888;
				input1[0].domainOfImgX = bitmap1.getWidth();
				input1[0].domainOfImgY = bitmap1.getHeight();
				BarcodeRecognitionResult[] bar1 = new BarcodeRecognitionResult[textResults1.length];
				for (int a = 0; a < textResults1.length; a++) {
					TextResult textResult = textResults1[a];
					BarcodeRecognitionResult barcodeRecognitionResult = bar1[a] = new BarcodeRecognitionResult();
					barcodeRecognitionResult.barcodeBytes = textResult.barcodeBytes;
					barcodeRecognitionResult.barcodeText = textResult.barcodeText;
					barcodeRecognitionResult.pts = textResult.localizationResult.resultPoints;
					barcodeRecognitionResult.format = textResult.barcodeFormat;
				}
				input1[0].barcodeRecognitionResults = bar1;

				input1[1] = new InputParasOfSwitchImagesFun();
				input1[1].buffer = convertImage(bitmap2);
				input1[1].width = bitmap2.getWidth();
				input1[1].height = bitmap2.getHeight();
				input1[1].stride = bitmap2.getWidth() * 4;
				input1[1].format = EnumImagePixelFormat.IPF_ARGB_8888;
				input1[1].domainOfImgX = bitmap2.getWidth();
				input1[1].domainOfImgY = bitmap2.getHeight();
				BarcodeRecognitionResult[] bar2 = new BarcodeRecognitionResult[textResults2.length];
				for (int a = 0; a < textResults2.length; a++) {
					TextResult textResult = textResults2[a];
					BarcodeRecognitionResult barcodeRecognitionResult = bar2[a] = new BarcodeRecognitionResult();
					barcodeRecognitionResult.barcodeBytes = textResult.barcodeBytes;
					barcodeRecognitionResult.barcodeText = textResult.barcodeText;
					barcodeRecognitionResult.pts = textResult.localizationResult.resultPoints;
					barcodeRecognitionResult.format = textResult.barcodeFormat;
				}
				input1[1].barcodeRecognitionResults = bar2;
				StitchImageResult result = AfterProcess.stitchImages(input1);
				if(result != null){
					bitmap3 = result.image;
					localizationResults = result.resultArr;
				}
				/*switch (result.basedImg) {
					case 0:
						break;
					case 1:
						bitmap3 = bitmap1;
						localizationResults = new BarcodeRecognitionResult[textResults1.length];
						BarcodeRecognitionResult recognitionResult;
						for (int j = 0; j < textResults1.length; j++) {
							recognitionResult = new BarcodeRecognitionResult();
							recognitionResult.barcodeBytes = textResults1[j].barcodeBytes;
							recognitionResult.barcodeText = textResults1[j].barcodeText;
							recognitionResult.pts = textResults1[j].localizationResult.resultPoints;
							localizationResults[j] = recognitionResult;
						}
						break;
					case 2:
						bitmap3 = bitmap2;
						localizationResults = new BarcodeRecognitionResult[textResults2.length];
						BarcodeRecognitionResult recognitionResult1;
						for (int j = 0; j < textResults2.length; j++) {
							recognitionResult1 = new BarcodeRecognitionResult();
							recognitionResult1.barcodeBytes = textResults2[j].barcodeBytes;
							recognitionResult1.barcodeText = textResults2[j].barcodeText;
							recognitionResult1.pts = textResults2[j].localizationResult.resultPoints;
							localizationResults[j] = recognitionResult1;
						}
						break;
					case 3:
						bitmap3 = result.image;
						localizationResults = result.resultArr;
						break;
					default:
						break;
				}*/
			/*} else {
				if (fileNames.length <= 2) {
					break;
				}
				if (i + 1 < fileNames.length) {
					bitmap1 = decodeFile(fileNames[i + 1]);
					textResults1 = decodeImage(bitmap1);
					InputParasOfSwitchImagesFun[] input1 = new InputParasOfSwitchImagesFun[2];
					input1[0] = new InputParasOfSwitchImagesFun();
					input1[0].buffer = convertImage(bitmap1);
					input1[0].width = bitmap1.getWidth();
					input1[0].height = bitmap1.getHeight();
					input1[0].stride = bitmap1.getWidth() * 4;
					input1[0].format = EnumImagePixelFormat.IPF_ARGB_8888;
					input1[0].domainOfImgX = bitmap1.getWidth();
					input1[0].domainOfImgY = bitmap1.getHeight();
					BarcodeRecognitionResult[] bar1 = new BarcodeRecognitionResult[textResults1.length];
					for (int a = 0; a < textResults1.length; a++) {
						TextResult textResult = textResults1[a];
						BarcodeRecognitionResult barcodeRecognitionResult = bar1[a] = new BarcodeRecognitionResult();
						barcodeRecognitionResult.barcodeBytes = textResult.barcodeBytes;
						barcodeRecognitionResult.barcodeText = textResult.barcodeText;
						barcodeRecognitionResult.pts = textResult.localizationResult.resultPoints;
						barcodeRecognitionResult.format = textResult.barcodeFormat;
					}
					input1[0].barcodeRecognitionResults = bar1;

					input1[1] = new InputParasOfSwitchImagesFun();
					input1[1].buffer = convertImage(bitmap3);
					input1[1].width = bitmap3.getWidth();
					input1[1].height = bitmap3.getHeight();
					input1[1].stride = bitmap3.getWidth() * 4;
					input1[1].format = EnumImagePixelFormat.IPF_ARGB_8888;
					input1[1].domainOfImgX = bitmap3.getWidth();
					input1[1].domainOfImgY = bitmap3.getHeight();
					input1[1].barcodeRecognitionResults = localizationResults;
					StitchImageResult result = AfterProcess.stitchImages(input1);
					if (result != null){
						bitmap3 = result.image;
						localizationResults = result.resultArr;
					}
					/*switch (result.basedImg) {
						case 0:
							break;
						case 1:
							bitmap3 = bitmap1;
							localizationResults = new BarcodeRecognitionResult[textResults1.length];
							BarcodeRecognitionResult recognitionResult1;
							for (int j = 0; j < textResults1.length; j++) {
								recognitionResult1 = new BarcodeRecognitionResult();
								recognitionResult1.barcodeBytes = textResults1[j].barcodeBytes;
								recognitionResult1.barcodeText = textResults1[j].barcodeText;
								recognitionResult1.pts = textResults1[j].localizationResult.resultPoints;
								localizationResults[j] = recognitionResult1;
							}
							break;
						case 2:
							break;
						case 3:
							bitmap3 = result.image;
							localizationResults = result.resultArr;
							break;
						default:
							break;
					}
				}
			}
		}
		return bitmap3;*/
    }

    private Bitmap decodeFile(String fileName) {
        File file = new File(fileName);
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }


    private byte[] convertImage(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        return buf.array();
    }

    public void orderByName(List fliePath) {
        Collections.sort(fliePath, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }
}
