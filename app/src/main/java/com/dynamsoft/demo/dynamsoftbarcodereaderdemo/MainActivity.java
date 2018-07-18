package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
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
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;
import com.otaliastudios.cameraview.Size;
import com.pierfrancescosoffritti.slidingdrawer.SlidingDrawer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
	private static final int PRC_PHOTO_PICKER = 1;
	private static final int RC_CHOOSE_PHOTO = 1;
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
	private BarcodeReader reader;
	private TextResult[] result;
	private boolean isDetected = true;
	private boolean isCameraStarted = false;
	private boolean isDrawerExpand = false;
	private DBRCache mCache;
	private String name = "";
	private boolean isFlashOn = false;
	private ArrayList<String> allResultText = new ArrayList<String>();
	private float previewScale;
	private Size previewSize = null;
	private FrameUtil frameUtil;
	private List<Map<String, String>> recentCodeList = new ArrayList<>();
	private SimpleAdapter simpleAdapter;
	private long startDetectTime = 0;
	private long endDetectTime = 0;
	private String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
	private ExecutorService threadManager = Executors.newCachedThreadPool();

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
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (!EasyPermissions.hasPermissions(this, perms)) {
			EasyPermissions.requestPermissions(this, "", 0, perms);
		}
		slidingDrawer.setDragView(dragView);
		Logger.addLogAdapter(new AndroidLogAdapter());
		try {
			reader = new BarcodeReader("t0068MgAAAA70elzyXYmS7moRx7im7XPCr58/2f7IyvaQfe2y0go" +
					"R2REXg7tfQ8Mv48LhyuiCPwaCnuPb7CKFYrg9B/Yc30k=");
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
/*			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10; i++) {
						long startFile=System.currentTimeMillis();
						try {
							reader.decodeFileInMemory(input2byte(), "Custom_100947_777");
						} catch (BarcodeReaderException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						long endFile=System.currentTimeMillis();
						Logger.d("decode file time : "+(endFile-startFile));
					}
				}
			}).start();*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		setSupportActionBar(toolbar);
		frameUtil = new FrameUtil();
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage(R.string.about);
				builder.setPositiveButton("Overview", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse("https://www.dynamsoft.com/Products/barcode-scanner-sdk-android.aspx");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
				});
				builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
		mCache = DBRCache.get(this, 1000 * 1000 * 50, 16);

		cameraView.addCameraListener(new CameraListener() {
			@Override
			public void onCameraOpened(CameraOptions options) {
				super.onCameraOpened(options);
				isCameraStarted = true;
			}
		});
		cameraView.addFrameProcessor(new CodeFrameProcesser());
		simpleAdapter = new SimpleAdapter(MainActivity.this, recentCodeList,
				R.layout.item_listview_recent_code, new String[]{"format", "text"}, new int[]{R.id.tv_code_format, R.id.tv_code_text});
		lvBarcodeList.setAdapter(simpleAdapter);
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

	public final byte[] input2byte()
			throws IOException {
		InputStream ims = getAssets().open("1531816782728.jpg");
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = ims.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		return swapStream.toByteArray();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
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
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraView.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraView.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraView.destroy();
	}

	@OnClick(R.id.tv_flash)
	public void onFlashClick() {
		if (isFlashOn) {
			isFlashOn = false;
			cameraView.setFlash(Flash.OFF);
		} else {
			isFlashOn = true;
			cameraView.setFlash(Flash.TORCH);
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

	private void obtainPreviewScale() {
		if (hudView.getWidth() == 0 || hudView.getHeight() == 0) {
			return;
		}
		previewSize = cameraView.getPreviewSize();
		previewScale = frameUtil.calculatePreviewScale(previewSize, hudView.getWidth(), hudView.getHeight());
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

	class CodeFrameProcesser implements FrameProcessor {
		@Override
		public void process(@NonNull Frame frame) {
			try {
				if (isDetected && isCameraStarted && !isDrawerExpand) {
					isDetected = false;
					if (previewSize == null) {
						Message obtainPreviewMsg = handler.obtainMessage();
						obtainPreviewMsg.what = OBTAIN_PREVIEW_SIZE;
						handler.sendMessage(obtainPreviewMsg);
					}
					YuvImage yuvImage = new YuvImage(frame.getData(), frame.getFormat(),
							frame.getSize().getWidth(), frame.getSize().getHeight(), null);
					int wid = frame.getSize().getWidth();
					int hgt = frame.getSize().getHeight();
					long startTime = System.currentTimeMillis();
					result = reader.decodeBuffer(yuvImage.getYuvData(), wid, hgt,
							yuvImage.getStrides()[0], EnumImagePixelFormat.IPF_NV21, "Custom_100947_777");
					long endTime = System.currentTimeMillis();
					long duringTime = endTime - startTime;
					Logger.d("detect code time : " + duringTime);
					if (duringTime > 1000) {
						File file = new File(Environment.getExternalStorageDirectory() + "/dbr-preview/");
						if (!file.exists()) {
							file.getParentFile().mkdirs();
							file.createNewFile();
						}
						FileOutputStream outputStream;
						try {
							outputStream = new FileOutputStream(file + "/" + System.currentTimeMillis() + ".jpg");
							yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, outputStream);
							outputStream.flush();
							outputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					//Logger.d("barcode result" + Arrays.toString(result) + " src width : " + wid + "src height : " + hgt);
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
						checkTimeAndSaveImg(yuvImage, result);
					} else {
						isDetected = true;
					}
				}
			} catch (BarcodeReaderException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void checkTimeAndSaveImg(final YuvImage yuvImage, final TextResult[] results) {
			if (startDetectTime != 0) {
				endDetectTime = System.currentTimeMillis();
				if (endDetectTime - startDetectTime > 1000) {
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

