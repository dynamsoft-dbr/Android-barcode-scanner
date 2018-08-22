package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.barcode.BarcodeReader;
import com.dynamsoft.barcode.BarcodeReaderException;
import com.dynamsoft.barcode.TextResult;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryDetailViewPagerAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectCoordinate;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.FrameUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.ShareUtil;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HistoryPreviewViewPager;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;


public class HistoryItemDetailActivity extends BaseActivity {
	private final int DECODE_FINISHI = 0x0001;
	private final int DECODE_FAILED = 0X0045;
	@BindView(R.id.vp_history_detail)
	HistoryPreviewViewPager vpHistoryDetail;
	@BindView(R.id.lv_code_list)
	ListView lvCodeList;
	@BindView(R.id.pb_progress)
	ProgressBar pbProgress;
	@BindView(R.id.pv_photo_detail)
	PhotoView pvPhotoDetail;
	@BindView(R.id.tv_decode_time)
	TextView tvDecodeTime;
	@BindView(R.id.tv_barcode_count)
	TextView tvBarcodeCount;
	private DBRCache mCache;
	private String[] fileNames;
	private int intentPosition;
	private ArrayList<HistoryItemBean> listItem;
	private List<DBRImage> imageList;
	private HistoryDetailViewPagerAdapter adapter;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, String>> recentCodeList = new ArrayList<>();
	private BarcodeReader reader;
	private int pageType;
	private ShareUtil shareUtil;
	private Long decodeTime;
	private int angle;
	private int scaleValue = -1;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DECODE_FINISHI:
					pvPhotoDetail.setImageBitmap((Bitmap) msg.obj);
					if (pageType == 0) {
						pvPhotoDetail.setRotation(90);
					}
					if (pageType == 2) {
						pvPhotoDetail.setRotation(angle);
					}
					pbProgress.setVisibility(View.GONE);
					simpleAdapter.notifyDataSetChanged();
					lvCodeList.startLayoutAnimation();
					tvDecodeTime.setText("Total time spent: " + String.valueOf(decodeTime) + "ms");
					tvBarcodeCount.setText("QTY: " + String.valueOf(recentCodeList.size()));
					break;
				case DECODE_FAILED:
					Toast.makeText(HistoryItemDetailActivity.this, "Decode failed.", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		mCache = DBRCache.get(this);
		setToolbarBackgroud("#ffffff");
		setToolbarTitle("Barcode Detail");
		setToolbarTitleColor("#000000");
		initBarcodeReader();
		shareUtil = new ShareUtil(this);
		simpleAdapter = new SimpleAdapter(this, recentCodeList,
				R.layout.item_listview_detail_code_list, new String[]{"index", "format", "text"},
				new int[]{R.id.tv_index, R.id.tv_code_format_content, R.id.tv_code_text_content});
		lvCodeList.setAdapter(simpleAdapter);
		pageType = getIntent().getIntExtra("page_type", 0);
		switch (pageType) {
			case 0:
				fromCamera(0);
				break;
			case 1:
				fromHistoryList();
				break;
			case 2:
				fromCamera(2);
				break;
			default:
		}
	}

	private void fromCamera(int imgLocation) {
		pbProgress.setVisibility(View.VISIBLE);
		drawRectOnImg(imgLocation);
	}

	private void fromHistoryList() {
		intentPosition = getIntent().getIntExtra("position", 0);
		fillHistoryList();
		vpHistoryDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				fillCodeList(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_share).setVisible(true);
		menu.findItem(R.id.menu_capture).setVisible(false);
		menu.findItem(R.id.menu_file).setVisible(false);
		menu.findItem(R.id.menu_scanning).setVisible(false);
		menu.findItem(R.id.menu_Setting).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bitmap shotBitmap = shareUtil.getScreenShot(this);
		if (shotBitmap != null) {
			ArrayList<Uri> imageUris = new ArrayList<>();
			Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), shotBitmap, null, null));
			imageUris.add(uri);
			shareUtil.shareMultiImages(imageUris, this);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_history_item_detail;
	}

	private void fillHistoryList() {
/*		HistoryItemBean historyItemBean;
		listItem = new ArrayList<>();
		for (int i = 0; i < fileNames.length; i++) {
			try {
				historyItemBean = LoganSquare.parse(mCache.getAsStringWithFileName(fileNames[i]),
						HistoryItemBean.class);
				if (historyItemBean != null) {
					listItem.add(historyItemBean);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		imageList = LitePal.findAll(DBRImage.class);
		Collections.reverse(imageList);
		if (imageList.size() > 16) {
			imageList = imageList.subList(0, 16);
		}
		adapter = new HistoryDetailViewPagerAdapter(this, imageList);
		vpHistoryDetail.setAdapter(adapter);
		vpHistoryDetail.setCurrentItem(intentPosition);
		fillCodeList(intentPosition);
	}

	private void fillCodeList(int position) {
		if (imageList != null && imageList.size() > 0) {
			recentCodeList.clear();
			for (int i = 0; i < imageList.get(position).getCodeFormat().size(); i++) {
				Map<String, String> item = new HashMap<>();
				item.put("index", i + 1 + "");
				item.put("format", DBRUtil.getCodeFormat(imageList.get(position).getCodeFormat().get(i)));
				item.put("text", imageList.get(position).getCodeText().get(i));
				recentCodeList.add(item);
			}
			tvDecodeTime.setText("Total time spent: " + String.valueOf(imageList.get(0).getDecodeTime()) + "ms");
			tvBarcodeCount.setText("QTY: " + String.valueOf(recentCodeList.size()));
			simpleAdapter.notifyDataSetChanged();
			lvCodeList.startLayoutAnimation();
		}
	}

	private void drawRectOnImg(final int imgLocation) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(9f);
				paint.setColor(getResources().getColor(R.color.aboutOK));
				paint.setAntiAlias(true);
				String imgPath;
				String fileName;
				Path path = new Path();
				Bitmap oriBitmap;
				BitmapFactory.Options opts = new BitmapFactory.Options();
				if (imgLocation == 0) {
					opts.inSampleSize = 4;
					scaleValue = 4;
					imgPath = new File(getExternalFilesDir("photos"),
							getIntent().getStringExtra("photoname") + ".jpg").getAbsolutePath();
					fileName = new File(getExternalFilesDir("photos"),
							getIntent().getStringExtra("photoname") + ".jpg").getName();
					oriBitmap = BitmapFactory.decodeFile(imgPath, opts);
				} else {
					opts.inSampleSize = 2;
					scaleValue = 2;
					imgPath = getIntent().getStringExtra("FilePath");
					fileName = new File(getIntent().getStringExtra("FilePath")).getName();
					oriBitmap = BitmapFactory.decodeFile(imgPath, opts);
					angle = DBRUtil.readPictureDegree(imgPath);
				}
				if (oriBitmap == null) {
					Message message = mHandler.obtainMessage();
					message.what = DECODE_FAILED;
					mHandler.sendMessage(message);
					return;
				}
				Bitmap rectBitmap = oriBitmap.copy(Bitmap.Config.RGB_565, true);
				TextResult[] textResults;
				try {
					long startTime = System.currentTimeMillis();
					textResults = reader.decodeBufferedImage(rectBitmap, "Custom");
					long endTime = System.currentTimeMillis();
					decodeTime = endTime - startTime;
					ArrayList<TextResult> resultArrayList = new ArrayList<>();
					for (int i = 0; i < textResults.length; i++) {
						if (textResults[i] != null && textResults[i].localizationResult.extendedResultArray[0].confidence > 30) {
							resultArrayList.add(textResults[i]);
						}
					}
					textResults = resultArrayList.toArray(new TextResult[resultArrayList.size()]);
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
							Map<String, String> item = new HashMap<>();
							item.put("index", i + 1 + "");
							item.put("format", DBRUtil.getCodeFormat(textResults[i].barcodeFormat + ""));
							item.put("text", textResults[i].barcodeText);
							recentCodeList.add(item);
						}
					}

					Message message = mHandler.obtainMessage();
					message.what = DECODE_FINISHI;
					message.obj = rectBitmap;
					mHandler.sendMessage(message);

					if (textResults != null && textResults.length > 0) {
						DBRImage dbrImage = new DBRImage();
						dbrImage.setDecodeTime(decodeTime);
						dbrImage.setCodeImgPath(imgPath);
						ArrayList<String> codeFormatList = new ArrayList<>();
						ArrayList<String> codeTextList = new ArrayList<>();
						for (TextResult result1 : textResults) {
							codeFormatList.add(result1.barcodeFormat + "");
							codeTextList.add(result1.barcodeText);
						}
						ArrayList<RectPoint[]> pointList = FrameUtil.translatePoints(textResults);
						RectCoordinate rectCoordinate = new RectCoordinate();
						rectCoordinate.setRectCoord(pointList);
						String rectCoord = LoganSquare.serialize(rectCoordinate);
						dbrImage.setCodeText(codeTextList);
						dbrImage.setCodeFormat(codeFormatList);
						dbrImage.setFileName(fileName);
						dbrImage.setRectCoord(rectCoord);
						dbrImage.setScaleValue(scaleValue);
						dbrImage.save();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BarcodeReaderException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void initBarcodeReader() {
		try {
			reader = new BarcodeReader(getString(R.string.dbr_license));
			DBRCache mSettingCache = DBRCache.get(this, "SettingCache");
			String templateType = mSettingCache.getAsString("templateType");
			reader.initRuntimeSettingsWithString(mSettingCache.getAsString(templateType), 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(this);
	}
}
