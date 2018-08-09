package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.barcode.jni.BarcodeReader;
import com.dynamsoft.barcode.jni.BarcodeReaderException;
import com.dynamsoft.barcode.jni.EnumImagePixelFormat;
import com.dynamsoft.barcode.jni.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRSetting;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.FrameUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HUDCanvasView;
import com.orhanobut.logger.Logger;
import com.pierfrancescosoffritti.slidingdrawer.SlidingDrawer;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.UpdateConfiguration;
import io.fotoapparat.parameter.Resolution;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.camera.CameraParameters;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pub.devrel.easypermissions.EasyPermissions;

import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FlashSelectorsKt.torch;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
	private final int DETECT_BARCODE = 0x0001;
	private final int OBTAIN_PREVIEW_SIZE = 0x0002;
	private final int BARCODE_RECT_COORD = 0x0003;
	private final int REQUEST_CHOOSE_PHOTO = 0x0001;
	private final int REQUEST_SETTING = 0x0002;

	@BindView(R.id.cameraView)
	CameraView cameraView;
	@BindView(R.id.tv_flash)
	Button mFlash;
	@BindView(R.id.scanCountText)
	TextView mScanCount;
	@BindView(R.id.hud_view)
	HUDCanvasView hudView;
	@BindView(R.id.drag_view)
	TextView dragView;
	@BindView(R.id.sliding_drawer)
	SlidingDrawer slidingDrawer;
	@BindView(R.id.rl_barcode_list)
	ListView lvBarcodeList;
	@BindView(R.id.btn_capture)
	Button btnCapture;
	private BarcodeReader reader;
	private TextResult[] result;
	private boolean isDetected = true;
	private boolean isCameraStarted = false;
	private boolean isDrawerExpand = false;
	private boolean isSingleMode = false;
	private DBRCache mCache;
	private DBRCache mSettingCache;
	private boolean isFlashOn = false;
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
	private boolean hasCameraPermission;
	private Fotoapparat fotoapparat;

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
						if (!allResultText.contains(aResult.barcodeText) && aResult.localizationResult.extendedResultArray[0].confidence > 50) {
							allResultText.add(aResult.barcodeText);
						}
					}
					int count = allResultText.size();
					if (count > 1) {
						mScanCount.setText(count + " Barcodes Scanned");
					} else {
						mScanCount.setText(count + " Barcode Scanned");
					}
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
	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		askForPermissions();
		setToolbarBackgroud("#000000");
		setToolbarNavIcon(R.drawable.ic_action_back_dark);
		setToolbarTitle("Scan Barcode");
		setToolbarTitleColor("#ffffff");
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
					"  },\n" +
					"\"version\": \"1.0\"" +
					"}");
			reader.appendParameterTemplate(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initUI();
		frameUtil = new FrameUtil();
		mCache = DBRCache.get(this,1000 * 1000 * 50,16);
		setupFotoapparat();
	}

	private void setupSlidingDrawer() {
		slidingDrawer.addSlideListener(new SlidingDrawer.OnSlideListener() {
			@Override
			public void onSlide(SlidingDrawer slidingDrawer, float currentSlide) {
				if (slidingDrawer.getState() == SlidingDrawer.COLLAPSED) {
					Logger.d("sliding drawer 0");
					isDrawerExpand = false;
					recentCodeList.clear();
				} else if (slidingDrawer.getState() == SlidingDrawer.EXPANDED) {
					Logger.d("sliding drawer 1");
					isDrawerExpand = true;
					hudView.clear();
					dragView.setText("Drag me");
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_scanning:
				switchToMulti();
				break;
			case R.id.menu_capture:
				switchToSingle();
				break;
			case R.id.menu_file:
				choicePhotoWrapper();
				break;
			case R.id.menu_Setting:
				goToSetting();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*try {
			reader = new BarcodeReader("t0068MgAAAJmtGjsv3J5mDE0ECeH0+ZFEr7BJl7gcdJZFYzqa2sZK" +
					"hpQcsNcQlPZooMc5wDrCWMKnQ72T/+01qsEpM3nwIjc=");
			JSONObject object = new JSONObject("{\n" +
					"  \"ImageParameters\": {\n" +
					"    \"Name\": \"linear\",\n" +
					"    \"BarcodeFormatIds\": [],\n" +
					"    \"DeblurLevel\": 9,\n" +
					"    \"AntiDamageLevel\": 9,\n" +
					"    \"TextFilterMode\": \"Enable\"\n" +
					"  }\n" +
					"}");
			JSONArray jsonArray = object.getJSONObject("ImageParameters").getJSONArray("BarcodeFormatIds");
			if (mCache.getAsString("linear").equals("1")) {
				jsonArray.put("OneD");
			}
			if (mCache.getAsString("qrcode").equals("1")) {
				jsonArray.put("QR_CODE");
			}
			if (mCache.getAsString("pdf417").equals("1")) {
				jsonArray.put("PDF417");
			}
			if (mCache.getAsString("matrix").equals("1")) {
				jsonArray.put("DATAMATRIX");
			}
			Log.d("code type", "type : " + object.toString());
			//reader.appendParameterTemplate(object.toString());
			name = "linear";
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
			String filePath = BGAPhotoHelper.getFilePathFromUri(data.getData());
			Intent intent = new Intent(MainActivity.this, HistoryItemDetailActivity.class);
			intent.putExtra("page_type", 2);
			intent.putExtra("FilePath", filePath);
			startActivity(intent);
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
		if (!isSingleMode) {
			hudView.setBoundaryPoints(rectCoord);
		}
		hudView.invalidate();
		isDetected = true;
	}

	private void fulFillRecentList(TextResult[] result) {
		recentCodeList.clear();
		dragView.setText(result[0].barcodeText);
		for (TextResult aResult1 : result) {
			Map<String, String> recentCodeItem = new HashMap<>();
			recentCodeItem.put("format", DBRUtil.getCodeFormat(aResult1.barcodeFormat + ""));
			recentCodeItem.put("text", aResult1.barcodeText);
			recentCodeList.add(recentCodeItem);
		}
		simpleAdapter.notifyDataSetChanged();
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

	public void shootSound() {
		MediaActionSound sound = new MediaActionSound();
		sound.play(MediaActionSound.SHUTTER_CLICK);
	}

	private void initUI() {
		slidingDrawer.setDragView(dragView);
		simpleAdapter = new SimpleAdapter(MainActivity.this, recentCodeList,
				R.layout.item_listview_recent_code, new String[]{"format", "text"}, new int[]{R.id.tv_code_format, R.id.tv_code_text});
		lvBarcodeList.setAdapter(simpleAdapter);
		btnCapture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shootSound();
				PhotoResult photoResult = fotoapparat.takePicture();
				final String photoName = System.currentTimeMillis() + "";
				photoResult.saveToFile(new File(getExternalFilesDir("photos"), photoName + ".jpg"
				)).whenDone(new WhenDoneListener<Unit>() {
					@Override
					public void whenDone(@Nullable Unit it) {
						Logger.d("save img done~!");
						Intent intent = new Intent(MainActivity.this, HistoryItemDetailActivity.class);
						intent.putExtra("page_type", 0);
						intent.putExtra("photoname", photoName);
						startActivity(intent);
					}
				});
			}
		});
		setupSlidingDrawer();
	}

	private void switchToMulti() {
		isSingleMode = false;
		slidingDrawer.setVisibility(View.VISIBLE);
		mScanCount.setVisibility(View.VISIBLE);
		btnCapture.setVisibility(View.GONE);
	}

	private void switchToSingle() {
		isSingleMode = true;
		slidingDrawer.setVisibility(View.GONE);
		mScanCount.setVisibility(View.GONE);
		btnCapture.setVisibility(View.VISIBLE);
	}

	private void choicePhotoWrapper() {
		BGAPhotoHelper photoHelper = new BGAPhotoHelper(new File(Environment.getExternalStorageDirectory(), "DBRDemo"));
		startActivityForResult(photoHelper.getChooseSystemGalleryIntent(), REQUEST_CHOOSE_PHOTO);
	}
	private void goToSetting(){
		String strSetting = "";
		mSettingCache = DBRCache.get(this, "SettingCache");
		if (mSettingCache.getAsString("GeneralSetting") == null){
			DBRSetting generalSetting = new DBRSetting();
			try {
				strSetting = LoganSquare.serialize(generalSetting);
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
			mSettingCache.put("GeneralSetting", strSetting);
		}
		startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), REQUEST_SETTING);
	}

	class CodeFrameProcesser implements FrameProcessor {
		@Override
		public void process(@NonNull Frame frame) {
			try {
				if (isDetected && !isDrawerExpand && !isSingleMode) {
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
					Message coordMessage = handler.obtainMessage();
					Message message = handler.obtainMessage();
					if (result != null && result.length > 0) {
						ArrayList<RectPoint[]> rectCoord = frameUtil.handlePoints(result, previewScale, hgt, wid);
						message.obj = result;
						message.what = DETECT_BARCODE;
						handler.sendMessage(message);
						coordMessage.obj = rectCoord;
						checkTimeAndSaveImg(yuvImage, result);
					} else {
						isDetected = true;
					}
					coordMessage.what = BARCODE_RECT_COORD;
					handler.sendMessage(coordMessage);
				}
			} catch (BarcodeReaderException e) {
				e.printStackTrace();
			}
		}

		private void checkTimeAndSaveImg(final YuvImage yuvImage, final TextResult[] results) {
			if (startDetectTime != 0) {
				endDetectTime = System.currentTimeMillis();
				if (endDetectTime - startDetectTime > 500) {
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
								for (int i = 0; i < results.length; i++) {
									codeFormatList.add(results[i].barcodeFormat + "");
									codeTextList.add(results[i].barcodeText);
								}
								itemBean.setFileName(name);
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
			} else {
				startDetectTime = System.currentTimeMillis();
			}
		}
	}
}

