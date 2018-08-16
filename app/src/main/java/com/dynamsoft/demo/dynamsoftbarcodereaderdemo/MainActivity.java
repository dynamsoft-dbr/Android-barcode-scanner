package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.dynamsoft.barcode.afterprocess.jni.AfterProcess;
import com.dynamsoft.barcode.afterprocess.jni.CoordsMapResult;
import com.dynamsoft.barcode.BarcodeReader;
import com.dynamsoft.barcode.BarcodeReaderException;
import com.dynamsoft.barcode.EnumImagePixelFormat;
import com.dynamsoft.barcode.LocalizationResult;
import com.dynamsoft.barcode.PublicParameterSettings;
import com.dynamsoft.barcode.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRSetting;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectCoordinate;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.YuvInfo;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.FrameUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HUDCanvasView;
import com.orhanobut.logger.Logger;
import com.pierfrancescosoffritti.slidingdrawer.SlidingDrawer;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

import java.io.BufferedOutputStream;
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
	private final int RESPONSE_GENERAL_SETTING = 0x0001;
	private final int RESPONSE_MULTIBEST_SETTING = 0X0002;
	private final int RESPONSE_MULTIBAL_SETTING = 0X0003;

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
	String templateType;
	private boolean isDetected = true;
	private boolean isCameraStarted = false;
	private boolean isDrawerExpand = false;
	private boolean isSingleMode = false;
	private DBRCache mCache;
	private DBRCache mSettingCache;
	private DBRSetting mSetting;
	private boolean isFlashOn = false;
	private ArrayList<String> allResultText = new ArrayList<>();
	private float previewScale;
	private Resolution previewSize = null;
	private FrameUtil frameUtil;
	private List<Map<String, String>> recentCodeList = new ArrayList<>();
	private SimpleAdapter simpleAdapter;
	private long startDetectTime = 0;
	private long endDetectTime = 0;
	private int frameTime = 0;
	private String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
	private ExecutorService threadManager = Executors.newSingleThreadExecutor();
	private boolean hasCameraPermission;
	private Fotoapparat fotoapparat;
	private YuvInfo yuvInfo;
	private ArrayList<YuvInfo> yuvInfoList = new ArrayList<>();
	private long duringTime;

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
		initTemplate();
		initUI();
		frameUtil = new FrameUtil();
		mCache = DBRCache.get(this, 1000 * 1000 * 50, 16);
		setupFotoapparat();
	}

	private void initTemplate() {
		try {
			reader = new BarcodeReader(getString(R.string.dbr_license));
			mSettingCache = DBRCache.get(this, "SettingCache");
			templateType = mSettingCache.getAsString("templateType");
			if ("GeneralSetting".equals(templateType)) {
				String setting = mSettingCache.getAsString("GeneralSetting");
				if (setting != null) {
					reader.initRuntimeSettingsWithString(setting, 2);
				} else {
					mSetting = new DBRSetting();
					mSettingCache.put("GeneralSetting", LoganSquare.serialize(mSetting));
					reader.initRuntimeSettingsWithString(LoganSquare.serialize(mSetting), 2);
				}
			}
			if ("MultiBestSetting".equals(templateType)) {
				DBRSetting multiBest = new DBRSetting();
				DBRSetting.ImageParameter multiBestImgP = new DBRSetting.ImageParameter();
				multiBestImgP.setAntiDamageLevel(7);
				multiBestImgP.setDeblurLevel(9);
				multiBestImgP.setScaleDownThreshold(1000);
				multiBest.setImageParameter(multiBestImgP);
				mSettingCache.put("MultiBestSetting", LoganSquare.serialize(multiBest));
				reader.initRuntimeSettingsWithString(LoganSquare.serialize(multiBest), 2);
			}
			if ("MultiBalSetting".equals(templateType)) {
				DBRSetting multiBal = new DBRSetting();
				DBRSetting.ImageParameter multiBalImgP = new DBRSetting.ImageParameter();
				multiBalImgP.setAntiDamageLevel(5);
				multiBalImgP.setDeblurLevel(5);
				multiBalImgP.setScaleDownThreshold(1000);
				multiBalImgP.setLocalizationAlgorithmPriority(new ArrayList<String>() {{
					add("ConnectedBlock");
					add("Lines");
					add("Statistics");
					add("FullImageAsBarcodeZone");
				}});
				multiBal.setImageParameter(multiBalImgP);
				mSettingCache.put("MultiBalSetting", LoganSquare.serialize(multiBal));
				reader.initRuntimeSettingsWithString(LoganSquare.serialize(multiBal), 2);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK) {
			String filePath = BGAPhotoHelper.getFilePathFromUri(data.getData());
			Intent intent = new Intent(MainActivity.this, HistoryItemDetailActivity.class);
			intent.putExtra("page_type", 2);
			intent.putExtra("FilePath", filePath);
			startActivity(intent);
		}
		if (requestCode == REQUEST_SETTING) {
			String setting = "";
			mSettingCache = DBRCache.get(this, "SettingCache");
			if (resultCode == RESPONSE_GENERAL_SETTING) {
				setting = mSettingCache.getAsString("GeneralSetting");
			}
			if (resultCode == RESPONSE_MULTIBEST_SETTING) {
				setting = mSettingCache.getAsString("MultiBestSetting");
			}
			if (resultCode == RESPONSE_MULTIBAL_SETTING) {
				setting = mSettingCache.getAsString("MultiBalSetting");
			}
			try {
				reader = new BarcodeReader(getString(R.string.dbr_license));
				reader.initRuntimeSettingsWithString(setting, 2);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
		hudView.clear();
		hudView.invalidate();
		slidingDrawer.setVisibility(View.GONE);
		mScanCount.setVisibility(View.GONE);
		btnCapture.setVisibility(View.VISIBLE);
	}

	private void choicePhotoWrapper() {
		BGAPhotoHelper photoHelper = new BGAPhotoHelper(new File(Environment.getExternalStorageDirectory(), "DBRDemo"));
		startActivityForResult(photoHelper.getChooseSystemGalleryIntent(), REQUEST_CHOOSE_PHOTO);
	}

	private void goToSetting() {
		Intent intent = new Intent(MainActivity.this, SettingActivity.class);
		intent.putExtra("templateType", templateType);
		startActivityForResult(intent, REQUEST_SETTING);
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
							yuvImage.getStrides()[0], EnumImagePixelFormat.IPF_NV21, "Custom");
					long endTime = System.currentTimeMillis();
					duringTime = endTime - startTime;
					/*Logger.d("detect code time : " + duringTime + "  endTime :" + endTime);
					File file = new File(Environment.getExternalStorageDirectory(), endTime + "");
					try{
						file.createNewFile();
						FileOutputStream outputStream = new FileOutputStream(file);
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
						bufferedOutputStream.write(yuvImage.getYuvData());
						bufferedOutputStream.flush();
						if (outputStream != null){
							outputStream.close();
						}
						if (bufferedOutputStream != null){
							bufferedOutputStream.close();
						}

					}catch (Exception ex){
						ex.printStackTrace();
					}*/
					Message coordMessage = handler.obtainMessage();
					Message message = handler.obtainMessage();
					if (result != null && result.length > 0) {
						ArrayList<RectPoint[]> rectCoord = frameUtil.handlePoints(result, previewScale, hgt, wid);
						message.obj = result;
						message.what = DETECT_BARCODE;
						handler.sendMessage(message);
						coordMessage.obj = rectCoord;
						if (frameTime == 0) {
							yuvInfo = new YuvInfo();
							yuvInfo.cacheName = System.currentTimeMillis() + "";
							yuvInfo.yuvImage = yuvImage;
							yuvInfo.textResult = result;
							yuvInfoList.add(yuvInfo);
							handleImage(yuvInfo, null);
							frameTime++;
						} else if (frameTime == 1) {
							yuvInfo = new YuvInfo();
							yuvInfo.textResult = result;
							yuvInfo.yuvImage = yuvImage;
							yuvInfo.cacheName = System.currentTimeMillis() + "";
							if (yuvInfoList.size() == 1) {
								yuvInfoList.add(yuvInfo);
							} else {
								yuvInfoList.set(1, yuvInfo);
							}
							CoordsMapResult coordsMapResult = AfterProcess.coordsMap
									(yuvInfoList.get(0).textResult, yuvInfoList.get(1).textResult, wid, hgt);
							if (coordsMapResult != null) {
								LocalizationResult localizationResult;
								TextResult textResult;
								//Logger.d("maptype : " + coordsMapResult.basedImg);
								switch (coordsMapResult.basedImg) {
									case 0:
										handleImage(yuvInfoList.get(1), null);
										yuvInfoList.set(0, yuvInfoList.get(1));
										break;
									case 1:
										TextResult[] newResultBase1 = new TextResult[result.length + coordsMapResult.resultArr.length];
										for (int i = 0; i < result.length + coordsMapResult.resultArr.length; i++) {
											if (i < result.length) {
												newResultBase1[i] = result[i];
											} else {
												localizationResult = new LocalizationResult();
												localizationResult.resultPoints = coordsMapResult.resultArr[i - result.length].pts;
												textResult = new TextResult();
												textResult.localizationResult = localizationResult;
												textResult.barcodeText = coordsMapResult.resultArr[i - result.length].barcodeText;
												textResult.barcodeBytes = coordsMapResult.resultArr[i - result.length].barcodeBytes;
												textResult.barcodeFormat = coordsMapResult.resultArr[i - result.length].format;
												newResultBase1[i] = textResult;
											}
										}
										yuvInfo.textResult = newResultBase1;
										yuvInfoList.set(0, yuvInfo);
										handleImage(yuvInfoList.get(0), yuvInfoList.get(1).cacheName);
										break;
									case 2:
										TextResult[] newResultBase2 = new TextResult[result.length + coordsMapResult.resultArr.length];
										for (int i = 0; i < result.length + coordsMapResult.resultArr.length; i++) {
											if (i < result.length) {
												newResultBase2[i] = result[i];
											} else {
												localizationResult = new LocalizationResult();
												localizationResult.resultPoints = coordsMapResult.resultArr[i - result.length].pts;
												textResult = new TextResult();
												textResult.localizationResult = localizationResult;
												textResult.barcodeText = coordsMapResult.resultArr[i - result.length].barcodeText;
												textResult.barcodeBytes = coordsMapResult.resultArr[i - result.length].barcodeBytes;
												textResult.barcodeFormat = coordsMapResult.resultArr[i - result.length].format;
												newResultBase2[i] = textResult;
											}
										}
										yuvInfo.textResult = newResultBase2;
										yuvInfoList.set(0, yuvInfo);
										handleImage(yuvInfoList.get(1), yuvInfoList.get(0).cacheName);
										break;
									case -1:
										break;
									default:
										break;
								}
							}
						}

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

		private void deleteErroCache(String name) {
			if (name == null) {
				return;
			}
			mCache.remove(name);
			File previewFile = new File(path + "/" + name + ".jpg");
			if (previewFile.exists()) {
				previewFile.delete();
			}
		}

		private void handleImage(final YuvInfo yuvInfo, final String deleCacheName) {
			endDetectTime = System.currentTimeMillis();
			threadManager.execute(new Runnable() {
				@Override
				public void run() {
					try {
						deleteErroCache(deleCacheName);
						long startSaveFile = System.currentTimeMillis();
						YuvImage newYuv = new YuvImage(FrameUtil.rotateYUVDegree90(yuvInfo.yuvImage.getYuvData(),
								yuvInfo.yuvImage.getWidth(), yuvInfo.yuvImage.getHeight()), ImageFormat.NV21, yuvInfo.yuvImage.getHeight(), yuvInfo.yuvImage.getWidth(), null);
						File previewFile = new File(path + "/" + yuvInfo.cacheName + ".jpg");
						if (!previewFile.exists()) {
							previewFile.getParentFile().mkdirs();
							previewFile.createNewFile();
						}
						FileOutputStream fileOutputStream = new FileOutputStream(previewFile);
						newYuv.compressToJpeg(new Rect(0, 0, newYuv.getWidth(), newYuv.getHeight()), 100, fileOutputStream);
						fileOutputStream.flush();
						fileOutputStream.close();
						ArrayList<String> codeFormatList = new ArrayList<>();
						ArrayList<String> codeTextList = new ArrayList<>();
						ArrayList<RectPoint[]> pointList = frameUtil.rotatePoints(yuvInfo.textResult,
								yuvInfo.yuvImage.getHeight(), yuvInfo.yuvImage.getWidth());
						for (TextResult result1 : yuvInfo.textResult) {
							codeFormatList.add(result1.barcodeFormat + "");
							codeTextList.add(result1.barcodeText);
						}

						DBRImage dbrImage = new DBRImage();
						dbrImage.setFileName(yuvInfo.cacheName);
						dbrImage.setCodeFormat(codeFormatList);
						dbrImage.setCodeText(codeTextList);
						dbrImage.setCodeImgPath(path + "/" + yuvInfo.cacheName + ".jpg");
						RectCoordinate rectCoordinate=new RectCoordinate();
						rectCoordinate.setRectCoord(pointList);
						String rectCoord=LoganSquare.serialize(rectCoordinate);
						dbrImage.setRectCoord(rectCoord);
						dbrImage.setDecodeTime(duringTime);
						dbrImage.save();
			/*		    HistoryItemBean itemBean = new HistoryItemBean();
						itemBean.setFileName(yuvInfo.cacheName);
						itemBean.setCodeFormat(codeFormatList);
						itemBean.setCodeText(codeTextList);
						itemBean.setCodeImgPath(path + "/" + yuvInfo.cacheName + ".jpg");
						itemBean.setRectCoord(pointList);
						itemBean.setDecodeTime(duringTime);
						String jsonResult = LoganSquare.serialize(itemBean);
						mCache.put(yuvInfo.cacheName, jsonResult);*/
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

