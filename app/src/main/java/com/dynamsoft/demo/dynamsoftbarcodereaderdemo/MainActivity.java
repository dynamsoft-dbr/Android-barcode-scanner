package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.barcode.coordsmap.jni.CoordsMapResult;
import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.BarcodeReaderException;
import com.dynamsoft.barcode.jni.EnumImagePixelFormat;
import com.dynamsoft.barcode.jni.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.FrameUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HUDCanvasView;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.pierfrancescosoffritti.slidingdrawer.SlidingDrawer;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.camera.CameraParameters;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pub.devrel.easypermissions.EasyPermissions;

import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
	private final int DETECT_BARCODE = 0x0001;
	private final int OBTAIN_PREVIEW_SIZE = 0x0002;
	private final int BARCODE_RECT_COORD = 0x0003;

	@BindView(R.id.cameraView)
	CameraView cameraView;
	@BindView(R.id.tv_flash)
	TextView mFlash;
	@BindView(R.id.scanCountText)
	TextView mScanCount;
	@BindView(R.id.hud_view)
	HUDCanvasView hudView;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.drag_view)
	TextView dragView;
	@BindView(R.id.sliding_drawer)
	SlidingDrawer slidingDrawer;
	@BindView(R.id.rl_barcode_list)
	ListView lvBarcodeList;
	@BindView(R.id.btn_capture)
	Button btnCapture;
	@BindView(R.id.btn_start)
	Button btnStart;
	@BindView(R.id.btn_finish)
	Button btnFinish;
	private BarcodeReader reader;
	private TextResult[] result;
	private boolean isDetected = true;
	private boolean isDrawerExpand = false;
	private boolean isFlashOn = false;
	private boolean hasCameraPermission;
	private boolean detectStart = false;
	private DBRCache mCache;
	private String name = "";
	private ArrayList<String> allResultText = new ArrayList<>();
	private float previewScale;
	private Resolution previewSize = null;
	private FrameUtil frameUtil;
	private List<Map<String, String>> recentCodeList = new ArrayList<>();
	private SimpleAdapter simpleAdapter;
	private long startDetectTime = 0;
	private long endDetectTime = 0;
	private String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
	private ExecutorService threadManager = Executors.newSingleThreadExecutor();
	private Fotoapparat fotoapparat;
	private int frameTime = 0;
	private ArrayList<YuvImage> yuvList = new ArrayList<>();
	private ArrayList<TextResult[]> textResultList = new ArrayList<>();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case DETECT_BARCODE:
					TextResult[] result = (TextResult[]) msg.obj;
					fulFillRecentList(result);
					for (TextResult aResult : result) {
						if (!allResultText.contains(aResult.barcodeText)) {
							allResultText.add(aResult.barcodeText);
						}
					}
					int count = allResultText.size();
					mScanCount.setText(count + " Scanned");
					break;
				case BARCODE_RECT_COORD:
					drawDocumentBox((ArrayList<RectPoint[]>) msg.obj);
					break;
				case OBTAIN_PREVIEW_SIZE:
					obtainPreviewScale();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		askForPermissions();
		Logger.addLogAdapter(new AndroidLogAdapter());
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
/*			TextResult[] results=reader.decodeFileInMemory(input2byte(),"");
			CoordsMapResult coordsMapResult=CoordsMapResult.coordsMap(results,results,1080,1440);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		initUI();
		frameUtil = new FrameUtil();
		mCache = DBRCache.get(this, 1000 * 1000 * 50, 16);
		setupFotoapparat();
	}

	public byte[] input2byte() throws IOException {
		InputStream ims = getAssets().open("123.jpg");
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = ims.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		return swapStream.toByteArray();
	}

	private void setupSlidingDrawer() {
		slidingDrawer.addSlideListener(new SlidingDrawer.OnSlideListener() {
			@Override
			public void onSlide(SlidingDrawer slidingDrawer, float currentSlide) {
				if (slidingDrawer.getState() == SlidingDrawer.COLLAPSED) {
					Logger.d("sliding drawer 0");
					isDrawerExpand = false;
					recentCodeList.clear();
					simpleAdapter.notifyDataSetChanged();
				} else if (slidingDrawer.getState() == SlidingDrawer.EXPANDED) {
					Logger.d("sliding drawer 1");
					isDrawerExpand = true;
					lvBarcodeList.startLayoutAnimation();
					simpleAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void askForPermissions() {
		String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (!EasyPermissions.hasPermissions(this, perms)) {
			hasCameraPermission = false;
			EasyPermissions.requestPermissions(this, "We need camera permission to provide service.", 0, perms);
		} else {
			hasCameraPermission = true;
			cameraView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (hasCameraPermission) {
			fotoapparat.start();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (hasCameraPermission) {
			fotoapparat.stop();
		}
	}

	@OnClick(R.id.tv_flash)
	public void onFlashClick() {
		if (isFlashOn) {
			isFlashOn = false;
			fotoapparat.updateConfiguration(
					UpdateConfiguration.builder()
							.flash(off())
							.build()
			);
		} else {
			isFlashOn = true;
			fotoapparat.updateConfiguration(
					UpdateConfiguration.builder()
							.flash(torch())
							.build()
			);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
		hasCameraPermission = true;
		fotoapparat.start();
		cameraView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

	}

	private void obtainPreviewScale() {
		if (hudView.getWidth() == 0 || hudView.getHeight() == 0) {
			return;
		}
		fotoapparat.getCurrentParameters().whenAvailable(new Function1<CameraParameters, Unit>() {
			@Override
			public Unit invoke(CameraParameters cameraParameters) {
				previewSize = cameraParameters.getPreviewResolution();
				previewScale = frameUtil.calculatePreviewScale(previewSize, hudView.getWidth(), hudView.getHeight());
				return Unit.INSTANCE;
			}
		});

	}

	private void drawDocumentBox(ArrayList<RectPoint[]> rectCoord) {
		hudView.clear();
		hudView.setBoundaryPoints(rectCoord);
		hudView.invalidate();
		isDetected = true;
	}

	private void fulFillRecentList(TextResult[] result) {
		recentCodeList.clear();
		dragView.setText(result[0].barcodeText);

		for (TextResult aResult1 : result) {
			Map<String, String> recentCodeItem = new HashMap<>();
			recentCodeItem.put("format", aResult1.barcodeFormat + "");
			recentCodeItem.put("text", aResult1.barcodeText);
			recentCodeList.add(recentCodeItem);
		}
	}

	private void setupFotoapparat() {
		fotoapparat = Fotoapparat
				.with(this)
				.into(cameraView)
				.previewScaleType(ScaleType.CenterCrop)
				.lensPosition(back())
				.frameProcessor(new CodeFrameProcesser())
				.build();
	}

	private void initUI() {
		slidingDrawer.setDragView(dragView);
		setSupportActionBar(toolbar);
		simpleAdapter = new SimpleAdapter(MainActivity.this, recentCodeList,
				R.layout.item_listview_recent_code, new String[]{"format", "text"}, new int[]{R.id.tv_code_format, R.id.tv_code_text});
		lvBarcodeList.setAdapter(simpleAdapter);
		setupSlidingDrawer();
	}

	@OnClick({R.id.btn_start, R.id.btn_finish})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_start:
				detectStart = true;
				break;
			case R.id.btn_finish:
				detectStart = false;
				break;
			default:
				break;
		}
	}

	class CodeFrameProcesser implements FrameProcessor {
		@Override
		public void process(@NonNull Frame frame) {
			try {
				if (isDetected && !isDrawerExpand && detectStart) {
					isDetected = false;
					if (previewSize == null) {
						Message obtainPreviewMsg = handler.obtainMessage();
						obtainPreviewMsg.what = OBTAIN_PREVIEW_SIZE;
						handler.sendMessage(obtainPreviewMsg);
					}
					YuvImage yuvImage = new YuvImage(frame.getImage(), ImageFormat.NV21,
							frame.getSize().width, frame.getSize().height, null);
					int wid = frame.getSize().width;
					int hgt = frame.getSize().height;
					long startTime = System.currentTimeMillis();
					result = reader.decodeBuffer(yuvImage.getYuvData(), wid, hgt,
							yuvImage.getStrides()[0], EnumImagePixelFormat.IPF_NV21, "Custom_100947_777");
					long endTime = System.currentTimeMillis();
					long duringTime = endTime - startTime;
					Logger.d("detect code time : " + duringTime);
					if (result != null && result.length > 0) {
						ArrayList<RectPoint[]> rectCoord = frameUtil.handlePoints(result, previewScale, hgt, wid);
						Message message = handler.obtainMessage();
						message.obj = result;
						message.what = DETECT_BARCODE;
						handler.sendMessage(message);
						Message coordMessage = handler.obtainMessage();
						coordMessage.obj = rectCoord;
						coordMessage.what = BARCODE_RECT_COORD;
						handler.sendMessage(coordMessage);
						if (frameTime==1){
							yuvList.add(frameTime,yuvImage);
							textResultList.add(frameTime,result);
							CoordsMapResult coordsMapResult = CoordsMapResult.coordsMap(textResultList.get(0), textResultList.get(1), wid, hgt);
							if (coordsMapResult != null) {
								switch (coordsMapResult.basedImg) {
									case 0:
										checkTimeAndSaveImg(yuvList.get(0), textResultList.get(0));
										checkTimeAndSaveImg(yuvList.get(1), textResultList.get(1));
										break;
									case 1:
										TextResult[] newResultBase1 = new TextResult[coordsMapResult.resultArr.length];
										for (int i = 0; i < coordsMapResult.resultArr.length; i++) {
											newResultBase1[i].localizationResult.resultPoints = coordsMapResult.resultArr[i].pts;
											newResultBase1[i].barcodeText = coordsMapResult.resultArr[i].barcodeText;
										}
										checkTimeAndSaveImg(yuvList.get(0), newResultBase1);
										break;
									case 2:
										TextResult[] newResultBase2 = new TextResult[coordsMapResult.resultArr.length];
										for (int i = 0; i < coordsMapResult.resultArr.length; i++) {
											newResultBase2[i].localizationResult.resultPoints = coordsMapResult.resultArr[i].pts;
											newResultBase2[i].barcodeText = coordsMapResult.resultArr[i].barcodeText;
										}
										checkTimeAndSaveImg(yuvList.get(1), newResultBase2);
										break;
									case -1:
										checkTimeAndSaveImg(yuvList.get(1), textResultList.get(1));
										break;
									default:
										break;
								}
							}
							frameTime=0;
						}else {
							yuvList.add(frameTime,yuvImage);
							textResultList.add(frameTime,result);
							frameTime++;
						}
					} else {
						isDetected = true;
					}
				}
			} catch (BarcodeReaderException e) {
				e.printStackTrace();
			}
		}

		private void checkTimeAndSaveImg(final YuvImage yuvImage, final TextResult[] results) {
			endDetectTime = System.currentTimeMillis();
			threadManager.execute(new Runnable() {
				@Override
				public void run() {
					try {
						long startSaveFile = System.currentTimeMillis();
						YuvImage newYuv = new YuvImage(FrameUtil.rotateYUVDegree90(yuvImage.getYuvData(),
								yuvImage.getWidth(), yuvImage.getHeight()), ImageFormat.NV21, yuvImage.getHeight(), yuvImage.getWidth(), null);
						String name = System.currentTimeMillis() + "";
						File previewFile = new File(path + "/" + name + ".jpg");
						if (!previewFile.exists()) {
							previewFile.getParentFile().mkdirs();
							previewFile.createNewFile();
						}
						FileOutputStream fileOutputStream = new FileOutputStream(previewFile);
						newYuv.compressToJpeg(new Rect(0, 0, newYuv.getWidth(), newYuv.getHeight()), 100, fileOutputStream);
						fileOutputStream.flush();
						fileOutputStream.close();
						HistoryItemBean itemBean = new HistoryItemBean();
						ArrayList<String> codeFormatList = new ArrayList<>();
						ArrayList<String> codeTextList = new ArrayList<>();
						ArrayList<RectPoint[]> pointList = frameUtil.rotatePoints(results, yuvImage.getHeight(), yuvImage.getWidth());
						for (TextResult result1 : results) {
							codeFormatList.add(result1.barcodeFormat + "");
							codeTextList.add(result1.barcodeText);
						}
						itemBean.setCodeFormat(codeFormatList);
						itemBean.setCodeText(codeTextList);
						itemBean.setCodeImgPath(path + "/" + name + ".jpg");
						itemBean.setRectCoord(pointList);
						String jsonResult = LoganSquare.serialize(itemBean);
						mCache.put(name, jsonResult);
						long endSaveFile = System.currentTimeMillis();
						Logger.d("save file time : " + (endSaveFile - startSaveFile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}

