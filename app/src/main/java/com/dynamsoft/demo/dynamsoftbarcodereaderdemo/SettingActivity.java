package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRSetting;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
	private DBRSetting mSetting;
	private DBRCache mSettingCache;
	private DBRSetting.ImageParameter mImageParameter;
	private String templateType;
	private boolean beepSoundEnable;
	private boolean overlapEnable;
	private List<String> one2Ten = new ArrayList<>();
	private List<String> colourImageConvertMode = new ArrayList<>();
	private List<String> barcodeInvertMode = new ArrayList<>();
	private List<String> mode = new ArrayList<>();
	private ArrayList<String> tempFormats;
	private ArrayAdapter<String> one2tenSpinnerAdapter;
	private ArrayAdapter<String> barcodeInvertModeSpinnerAdapter;
	private ArrayAdapter<String> colourImageConvertModeSpinnerAdapter;
	private ArrayAdapter<String> modeAdapter;
	private final int REQUEST_ONED_SETTING = 0x0001;
	private final int REQUEST_ALGORITHM_SETTING = 0x0002;
	private final int RESPONSE_ONED_SETTING = 0x0001;
	private final int RESPONSE_ALGORITHM_SETTING = 0x0002;
	private final int RESPONSE_ALLSETTING = 0x0001;
	@BindView(R.id.setoned)
	ImageView ivSetOned;
	@BindView(R.id.ckbpdf417)
	CheckBox mPDF417;
	@BindView(R.id.ckbqrcode)
	CheckBox mQRCode;
	@BindView(R.id.ckbdatamatrix)
	CheckBox mDataMatrix;
	@BindView(R.id.ckbaztec)
	CheckBox mAZTEC;
	@BindView(R.id.tv_expected_barcode_count)
	TextView tvExpectedBarcodeCount;
	@BindView(R.id.et_expected_barcode_count)
	EditText etExpectedBarcodeCount;
	@BindView(R.id.tv_timeout)
	TextView tvTimeout;
	@BindView(R.id.et_timeout)
	EditText etTimeout;
	@BindView(R.id.tv_scale_down_threshold)
	TextView tvScaleDownThreshold;
	@BindView(R.id.et_scale_down_threshold)
	EditText etScaleDownThreshold;
	@BindView(R.id.tv_binarization_block_size)
	TextView tvBinarizationBlockSize;
	@BindView(R.id.et_binarization_block_size)
	EditText etBinarizationBlockSize;
	@BindView(R.id.tv_max_barcode_count)
	TextView tvMaxBarcodeCount;
	@BindView(R.id.et_max_barcode_count)
	EditText etMaxBarcodeCount;
	@BindView(R.id.sc_enable_fill_binary_vacancy)
	SwitchCompat scEnableFillBinaryVacancy;
	@BindView(R.id.sc_region_predetection_mode)
	SwitchCompat scRegionPredetectionMode;
	@BindView(R.id.sc_text_filter_mode)
	SwitchCompat scTextFilterMode;
	@BindView(R.id.sc_beep_sound)
	SwitchCompat scBeepSound;
	@BindView(R.id.sp_deblur_level)
	Spinner spDeblurLevel;
	@BindView(R.id.sp_anti_damage_level)
	Spinner spAntiDamageLevel;
	@BindView(R.id.sp_gray_equalization_sensitivity)
	Spinner spGrayEqualizationSensitivity;
	@BindView(R.id.sp_texture_detection_sensitivity)
	Spinner spTextureDetectionSensitivity;
	@BindView(R.id.sp_barcode_invert_mode)
	Spinner spBarcodeInvertMode;
	@BindView(R.id.sp_colour_image_convert_mode)
	Spinner spColourImageConvertMode;
	@BindView(R.id.sp_mode)
	Spinner spMode;
	@Override
	protected int getLayoutId() {
		return R.layout.activity_setting;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		setToolbarBackgroud("#000000");
		setToolbarNavIcon(R.drawable.ic_action_back_dark);
		setToolbarTitle("Advanced");
		setToolbarTitleColor("#ffffff");
		etExpectedBarcodeCount.setOnEditorActionListener(onEditFinish);
		etTimeout.setOnEditorActionListener(onEditFinish);
		etExpectedBarcodeCount.setOnEditorActionListener(onEditFinish);
		etBinarizationBlockSize.setOnEditorActionListener(onEditFinish);
		//etMaxDimofFullImageAsBarcodeZone.setOnEditorActionListener(onEditFinish);
		etMaxBarcodeCount.setOnEditorActionListener(onEditFinish);
		etScaleDownThreshold.setOnEditorActionListener(onEditFinish);
		scEnableFillBinaryVacancy.setOnCheckedChangeListener(onSCCheckedChange);
		scRegionPredetectionMode.setOnCheckedChangeListener(onSCCheckedChange);
		scTextFilterMode.setOnCheckedChangeListener(onSCCheckedChange);
		scBeepSound.setOnCheckedChangeListener(onSCCheckedChange);
		mDataMatrix.setOnCheckedChangeListener(onCKBCheckedChange);
		mQRCode.setOnCheckedChangeListener(onCKBCheckedChange);
		mPDF417.setOnCheckedChangeListener(onCKBCheckedChange);
		mAZTEC.setOnCheckedChangeListener(onCKBCheckedChange);
		//initSpinner();
		//initSetting();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_share).setVisible(false);
		menu.findItem(R.id.menu_capture).setVisible(false);
		menu.findItem(R.id.menu_file).setVisible(false);
		menu.findItem(R.id.menu_scanning).setVisible(false);
		menu.findItem(R.id.menu_Setting).setVisible(false);
		menu.findItem(R.id.menu_Done).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mImageParameter.getBarcodeFormatIds().size() > 0){
			mSetting.setImageParameter(mImageParameter);
			try {
				mSettingCache.put("beepSound", String.valueOf(beepSoundEnable));
				mSettingCache.put("Overlap", String.valueOf(overlapEnable));
				mSettingCache.put("Setting", LoganSquare.serialize(mSetting));
				if ("CustomSetting".equals(templateType)) {
					Intent intent = new Intent(SettingActivity.this, MainActivity.class);
					intent.putExtra("templateType", templateType);
					startActivity(intent);
				} else {
					super.onBackPressed();
				}
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		} else {
			final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme));
			builder.setMessage("You must choose at least one barcode format.");
			builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
			builder.create();
			builder.show();
		}
		return super.onOptionsItemSelected(item);
	}
	public void onTipsClicked(View view) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme));
		builder.create();
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		switch (view.getId()) {
			case R.id.iv_expected_barcode_count:
				builder.setMessage(getText(R.string.expected_barcode_count_tip));
				builder.setTitle(R.string.expected_barcode_count);
				builder.show();
				break;
			case R.id.iv_timeout:
				builder.setMessage(getText(R.string.timeout_tip));
				builder.setTitle(R.string.timeout);
				builder.show();
				break;
			case R.id.iv_deblur_level:
				builder.setMessage(getText(R.string.deblur_level_tip));
				builder.setTitle(R.string.deblurlevel);
				builder.show();
				break;
			case R.id.iv_anti_damage_level:
				builder.setMessage(getText(R.string.anti_damage_level_tip));
				builder.setTitle(R.string.anti_damage_level);
				builder.show();
				break;
			case R.id.iv_text_filter_mode:
				builder.setMessage(getText(R.string.text_filter_mode_tip));
				builder.setTitle(R.string.text_filter_mode);
				builder.show();
				break;
			case R.id.iv_region_predetection_mode:
				builder.setMessage(getText(R.string.region_predetection_mode_tip));
				builder.setTitle(R.string.region_predetection_mode);
				builder.show();
				break;
			case R.id.iv_scale_down_threshold:
				builder.setMessage(getText(R.string.scale_down_threshold_tip));
				builder.setTitle(R.string.scale_down_Threshold);
				builder.show();
				break;
			case R.id.iv_colour_image_convert_mode:
				builder.setMessage(getText(R.string.colour_image_convert_mode_tip));
				builder.setTitle(R.string.colour_image_convert_mode);
				builder.show();
				break;
			case R.id.iv_barcode_invert_mode:
				builder.setMessage(getText(R.string.barcode_invert_mode_tip));
				builder.setTitle(R.string.barcode_invert_mode);
				builder.show();
				break;
			case R.id.iv_gray_equalization_sensitivity:
				builder.setMessage(getText(R.string.gray_equalization_sensitivity_tip));
				builder.setTitle(R.string.gray_equalization_sensitivity);
				builder.show();
				break;
			case R.id.iv_texture_detection_sensitivity:
				builder.setMessage(getText(R.string.texture_detection_sensitivity_tip));
				builder.setTitle(R.string.texture_detection_sensitivity);
				builder.show();
				break;
			case R.id.iv_binarization_block_size:
				builder.setMessage(getText(R.string.binarization_block_size_tip));
				builder.setTitle(R.string.binarization_block_size);
				builder.show();
				break;
			case R.id.iv_localization_algorithm_priority:
				builder.setMessage(getText(R.string.localization_algorithm_priority_tip));
				builder.setTitle(R.string.localization_algorithm_priority);
				builder.show();
				break;
			case R.id.iv_max_barcode_count:
				builder.setMessage(getText(R.string.max_barcode_count_tip));
				builder.setTitle(R.string.max_barcode_count);
				builder.show();
				break;
			case R.id.iv_enable_fill_binary_vacancy:
				builder.setMessage(getText(R.string.enable_fill_binary_vacancy_tip));
				builder.setTitle(R.string.enable_fill_binary_vacancy);
				builder.show();
				break;
			case R.id.iv_beepsound:
				builder.setTitle(R.string.beep_sound);
				builder.setMessage(getText(R.string.beep_sound_tip));
				builder.show();
				break;
			case R.id.iv_if_multi_frame:
				builder.setTitle(R.string.mode);
				builder.setMessage(getText(R.string.if_multi_frame_tip));
				builder.show();
				break;
			default:
				break;
		}
	}
	public void onClicked(View view) {
		switch (view.getId()) {
			case R.id.setoned:
				Intent intent = new Intent(SettingActivity.this, BarcodeTypeActivity.class);
				intent.putExtra("DBRSetting", mSetting);
				startActivityForResult(intent, REQUEST_ONED_SETTING);
				break;
			case R.id.setalgorithm:
				Intent intent1 = new Intent(SettingActivity.this, AlgorithmSettingActivity.class);
				intent1.putExtra("DBRSetting", mSetting);
				startActivityForResult(intent1, REQUEST_ALGORITHM_SETTING);
				break;
			case R.id.tv_expected_barcode_count:
				etExpectedBarcodeCount.setText(tvExpectedBarcodeCount.getText());
				tvExpectedBarcodeCount.setVisibility(View.GONE);
				etExpectedBarcodeCount.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_timeout:
				etTimeout.setText(tvTimeout.getText());
				tvTimeout.setVisibility(View.GONE);
				etTimeout.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_scale_down_threshold:
				etScaleDownThreshold.setText(tvScaleDownThreshold.getText());
				tvScaleDownThreshold.setVisibility(View.GONE);
				etScaleDownThreshold.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_binarization_block_size:
				etBinarizationBlockSize.setText(tvBinarizationBlockSize.getText());
				tvBinarizationBlockSize.setVisibility(View.GONE);
				etBinarizationBlockSize.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_max_barcode_count:
				etMaxBarcodeCount.setText(tvMaxBarcodeCount.getText());
				tvMaxBarcodeCount.setVisibility(View.GONE);
				etMaxBarcodeCount.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}
	private void initSpinner() {
		barcodeInvertMode.add("DarkOnLight");
		barcodeInvertMode.add("LightOnDark");
		barcodeInvertModeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, barcodeInvertMode);
		barcodeInvertModeSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		colourImageConvertMode.add("Auto");
		colourImageConvertMode.add("Grayscale");
		colourImageConvertModeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, colourImageConvertMode);
		colourImageConvertModeSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		mode.add("Single-Frame Scan");
		mode.add("Multi-Frame Scan");
		modeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, mode);
		modeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		for (int i = 0; i < 10; i++) {
			one2Ten.add(String.valueOf(i));
		}
		one2tenSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, one2Ten);
		one2tenSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		spDeblurLevel.setAdapter(one2tenSpinnerAdapter);
		spAntiDamageLevel.setAdapter(one2tenSpinnerAdapter);
		spGrayEqualizationSensitivity.setAdapter(one2tenSpinnerAdapter);
		spTextureDetectionSensitivity.setAdapter(one2tenSpinnerAdapter);
		spBarcodeInvertMode.setAdapter(barcodeInvertModeSpinnerAdapter);
		spColourImageConvertMode.setAdapter(colourImageConvertModeSpinnerAdapter);
		spMode.setAdapter(modeAdapter);
		mSettingCache = DBRCache.get(this, "SettingCache");
		templateType = mSettingCache.getAsString("templateType");
		try {
			mSetting = LoganSquare.parse(mSettingCache.getAsString("Setting"), DBRSetting.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (mSetting == null) {
			mSetting = new DBRSetting();
		}
		mImageParameter = mSetting.getImageParameter();
		initSpinnerDelegate();
	}
	private void initSpinnerDelegate() {
		spDeblurLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mImageParameter.setDeblurLevel(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spAntiDamageLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mImageParameter.setAntiDamageLevel(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spGrayEqualizationSensitivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mImageParameter.setGrayEqualizationSensitivity(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spTextureDetectionSensitivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mImageParameter.setTextureDetectionSensitivity(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spBarcodeInvertMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					mImageParameter.setBarcodeInvertMode(barcodeInvertMode.get(0));
				} else {
					mImageParameter.setBarcodeInvertMode(barcodeInvertMode.get(1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spColourImageConvertMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					mImageParameter.setColourImageConvertMode(colourImageConvertMode.get(0));
				} else {
					mImageParameter.setColourImageConvertMode(colourImageConvertMode.get(1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					overlapEnable = false;
				} else {
					overlapEnable = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}
	private void initSetting(){
		//mSettingCache = DBRCache.get(this, "SettingCache");
		String beepSound = mSettingCache.getAsString("beepSound");
		if (beepSound == null) {
			mSettingCache.put("beepSound", "true");
		}
		scBeepSound.setChecked(Boolean.parseBoolean(mSettingCache.getAsString("beepSound")));
		try {
			//mSetting = LoganSquare.parse(mSettingCache.getAsString("Setting"), DBRSetting.class);
			//mImageParameter = mSetting.getImageParameter();
			tvExpectedBarcodeCount.setText(String.valueOf(mImageParameter.getExpectedBarcodesCount()));
			tvTimeout.setText(String.valueOf(mImageParameter.getTimeout()));
			spDeblurLevel.setSelection(mImageParameter.getDeblurLevel());
			spAntiDamageLevel.setSelection(mImageParameter.getAntiDamageLevel());
			if (Boolean.parseBoolean(mSettingCache.getAsString("Overlap"))) {
				spMode.setSelection(1);
			} else {
				spMode.setSelection(0);
			}
			if ("Enable".equals(mImageParameter.getTextFilterMode())){
				scTextFilterMode.setChecked(true);
			} else {
				scTextFilterMode.setChecked(false);
			}
			if ("Enable".equals(mImageParameter.getRegionPredetectionMode())){
				scRegionPredetectionMode.setChecked(true);
			} else {
				scRegionPredetectionMode.setChecked(false);
			}
			tvScaleDownThreshold.setText(String.valueOf(mImageParameter.getScaleDownThreshold()));
			if (mImageParameter.getColourImageConvertMode().equals(colourImageConvertMode.get(0))){
				spColourImageConvertMode.setSelection(0);
			}else {
				spColourImageConvertMode.setSelection(1);
			}
			if (mImageParameter.getBarcodeInvertMode().equals(barcodeInvertMode.get(0))){
				spBarcodeInvertMode.setSelection(0);
			}else {
				spBarcodeInvertMode.setSelection(1);
			}
			spGrayEqualizationSensitivity.setSelection(mImageParameter.getGrayEqualizationSensitivity());
			spTextureDetectionSensitivity.setSelection(mImageParameter.getTextureDetectionSensitivity());
			tvBinarizationBlockSize.setText(String.valueOf(mImageParameter.getBinarizationBlockSize()));

			//tvMaxDimofFullImageAsBarcodeZone.setText(String.valueOf(mImageParameter.getMaxDimOfFullImageAsBarcodeZone()));
			tvMaxBarcodeCount.setText(String.valueOf(mImageParameter.getMaxBarcodesCount()));
			scEnableFillBinaryVacancy.setChecked(mImageParameter.isEnableFillBinaryVacancy());
			ArrayList<String> formats = mImageParameter.getBarcodeFormatIds();
			if (formats.contains("PDF417")) {
				mPDF417.setChecked(true);
			} else {
				mPDF417.setChecked(false);
			}
			if (formats.contains("QR_CODE")) {
				mQRCode.setChecked(true);
			} else {
				mQRCode.setChecked(false);
			}
			if (formats.contains("DATAMATRIX")) {
				mDataMatrix.setChecked(true);
			} else {
				mDataMatrix.setChecked(false);
			}
			if (formats.contains("AZTEC")) {
				mAZTEC.setChecked(true);
			} else {
				mAZTEC.setChecked(false);
			}
			/*if (formats.contains("CODE_39") && formats.contains("CODE_128") &&
			    	formats.contains("CODE_93") && formats.contains("CODABAR") &&
					formats.contains("ITF") && formats.contains("EAN_13") &&
					formats.contains("EAN_8") && formats.contains("UPC_E") &&
					formats.contains("INDUSTRIAL_25") && formats.contains("UPC_A")){
				mOned.setChecked(true);
			} else {
				mOned.setChecked(false);
			}*/
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	SwitchCompat.OnCheckedChangeListener onSCCheckedChange = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()){
				case R.id.sc_enable_fill_binary_vacancy:
					mImageParameter.setEnableFillBinaryVacancy(scEnableFillBinaryVacancy.isChecked());
					break;
				case R.id.sc_region_predetection_mode:
					if(scRegionPredetectionMode.isChecked()) {
						mImageParameter.setRegionPredetectionMode("Enable");
					} else {
						mImageParameter.setRegionPredetectionMode("Disable");
					}
					break;
				case R.id.sc_text_filter_mode:
					if(scTextFilterMode.isChecked()) {
						mImageParameter.setTextFilterMode("Enable");
					} else {
						mImageParameter.setTextFilterMode("Disable");
					}
					break;
				case R.id.sc_beep_sound:
					beepSoundEnable = scBeepSound.isChecked();
					break;
				default:
					break;
			}

		}
	};
	CheckBox.OnCheckedChangeListener onCKBCheckedChange = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()){
				/*case R.id.ckboned:
					if (mOned.isChecked()) {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.addAll(oneDFormats);
						mSetting.setBarcodeFormatIds(tempFormats);
					}else {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.removeAll(oneDFormats);
						mSetting.setBarcodeFormatIds(tempFormats);
					}
					break;*/
				case R.id.ckbpdf417:
					if (mPDF417.isChecked()){
						tempFormats = mImageParameter.getBarcodeFormatIds();
						if (!tempFormats.contains("PDF417")) {
							tempFormats.add("PDF417");
							mImageParameter.setBarcodeFormatIds(tempFormats);
						}
					}else {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						tempFormats.remove("PDF417");
						mImageParameter.setBarcodeFormatIds(tempFormats);
					}
					break;
				case R.id.ckbqrcode:
					if (mQRCode.isChecked()) {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						if (!tempFormats.contains("QR_CODE")) {
							tempFormats.add("QR_CODE");
							mImageParameter.setBarcodeFormatIds(tempFormats);
						}
					}else {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						tempFormats.remove("QR_CODE");
						mImageParameter.setBarcodeFormatIds(tempFormats);
					}
					break;
				case R.id.ckbdatamatrix:
					if (mDataMatrix.isChecked()) {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						if (!tempFormats.contains("DATAMATRIX")) {
							tempFormats.add("DATAMATRIX");
							mImageParameter.setBarcodeFormatIds(tempFormats);
						}
					}else {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						tempFormats.remove("DATAMATRIX");
						mImageParameter.setBarcodeFormatIds(tempFormats);
					}
					break;
				case R.id.ckbaztec:
					if (mAZTEC.isChecked()) {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						if (!tempFormats.contains("AZTEC")) {
							tempFormats.add("AZTEC");
							mImageParameter.setBarcodeFormatIds(tempFormats);
						}
					} else {
						tempFormats = mImageParameter.getBarcodeFormatIds();
						tempFormats.remove("AZTEC");
						mImageParameter.setBarcodeFormatIds(tempFormats);
					}
				default:
					break;
			}
		}
	};
	EditText.OnEditorActionListener onEditFinish = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.ACTION_DOWN) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				int tempValue;
				switch (v.getId()) {
					case R.id.et_expected_barcode_count:
						try {
							imm.hideSoftInputFromWindow(etExpectedBarcodeCount.getWindowToken(), 0);
							tempValue = Integer.parseInt(etExpectedBarcodeCount.getText().toString());
							if (tempValue >= 0 && tempValue <= 512) {
								mImageParameter.setExpectedBarcodesCount(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 512]", Toast.LENGTH_LONG).show();
								mImageParameter.setExpectedBarcodesCount(0);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 512]", Toast.LENGTH_LONG).show();
							mImageParameter.setExpectedBarcodesCount(0);
						}
						tvExpectedBarcodeCount.setText(String.valueOf(mImageParameter.getExpectedBarcodesCount()));
						etExpectedBarcodeCount.setVisibility(View.GONE);
						tvExpectedBarcodeCount.setVisibility(View.VISIBLE);
						break;
					case R.id.et_timeout:
						try {
							imm.hideSoftInputFromWindow(etTimeout.getWindowToken(), 0);
							tempValue = Integer.parseInt(etTimeout.getText().toString());
							if (tempValue >= 0) {
								mImageParameter.setTimeout(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 0x7fffffff]", Toast.LENGTH_LONG).show();
								mImageParameter.setTimeout(2000);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 0x7fffffff]", Toast.LENGTH_LONG).show();
							mImageParameter.setTimeout(2000);
						}
						tvTimeout.setText(String.valueOf(mImageParameter.getTimeout()));
						etTimeout.setVisibility(View.GONE);
						tvTimeout.setVisibility(View.VISIBLE);
						break;
					case R.id.et_scale_down_threshold:
						try {
							imm.hideSoftInputFromWindow(etScaleDownThreshold.getWindowToken(), 0);
							tempValue = Integer.parseInt(etScaleDownThreshold.getText().toString());
							if (tempValue >= 512) {
								mImageParameter.setScaleDownThreshold(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [512, 0x7fffffff]", Toast.LENGTH_LONG).show();
								mImageParameter.setScaleDownThreshold(1000);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [512, 0x7fffffff]", Toast.LENGTH_LONG).show();
							mImageParameter.setScaleDownThreshold(1000);
						}
						tvScaleDownThreshold.setText(String.valueOf(mImageParameter.getScaleDownThreshold()));
						etScaleDownThreshold.setVisibility(View.GONE);
						tvScaleDownThreshold.setVisibility(View.VISIBLE);
						break;
					case R.id.et_binarization_block_size:
						try {
							imm.hideSoftInputFromWindow(etBinarizationBlockSize.getWindowToken(), 0);
							tempValue = Integer.parseInt(etBinarizationBlockSize.getText().toString());
							if (tempValue >= 0 && tempValue <= 1000) {
								mImageParameter.setBinarizationBlockSize(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 1000]", Toast.LENGTH_LONG).show();
								mImageParameter.setBinarizationBlockSize(0);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [0, 1000]", Toast.LENGTH_LONG).show();
							mImageParameter.setBinarizationBlockSize(0);
						}
						tvBinarizationBlockSize.setText(String.valueOf(mImageParameter.getBinarizationBlockSize()));
						etBinarizationBlockSize.setVisibility(View.GONE);
						tvBinarizationBlockSize.setVisibility(View.VISIBLE);
						break;
					/*case R.id.et_max_dimof_full_image_as_barcode_zone:
						try {
							imm.hideSoftInputFromWindow(etMaxDimofFullImageAsBarcodeZone.getWindowToken(), 0);
							tempValue = Integer.parseInt(etMaxDimofFullImageAsBarcodeZone.getText().toString());
							if (tempValue >= 262144){
								mImageParameter.setMaxDimOfFullImageAsBarcodeZone(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [262144, 0x7fffffff]", Toast.LENGTH_LONG).show();
							}
						}
						catch (Exception ex){
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [262144, 0x7fffffff]", Toast.LENGTH_LONG).show();
						}
						tvMaxDimofFullImageAsBarcodeZone.setText(String.valueOf(mImageParameter.getMaxDimOfFullImageAsBarcodeZone()));
						etMaxDimofFullImageAsBarcodeZone.setVisibility(View.GONE);
						tvMaxDimofFullImageAsBarcodeZone.setVisibility(View.VISIBLE);
						break;*/
					case R.id.et_max_barcode_count:
						try {
							imm.hideSoftInputFromWindow(etMaxBarcodeCount.getWindowToken(), 0);
							tempValue = Integer.parseInt(etMaxBarcodeCount.getText().toString());
							if (tempValue >= 1 && tempValue <= 512) {
								mImageParameter.setMaxBarcodesCount(tempValue);
							} else {
								Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [1, 512]", Toast.LENGTH_LONG).show();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							Toast.makeText(SettingActivity.this, "Input Invalid! Legal value: [1, 512]", Toast.LENGTH_LONG).show();
						}
						tvMaxBarcodeCount.setText(String.valueOf(mImageParameter.getMaxBarcodesCount()));
						etMaxBarcodeCount.setVisibility(View.GONE);
						tvMaxBarcodeCount.setVisibility(View.VISIBLE);
					default:
						break;
				}
				return true;
			}
			return false;
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == REQUEST_ONED_SETTING && resultCode == RESPONSE_ONED_SETTING)) {
			mSetting = (DBRSetting) data.getSerializableExtra("OneDSetting");
			mImageParameter = mSetting.getImageParameter();
			/*ArrayList<String> formats = mSetting.getBarcodeFormatIds();
			if (formats.contains("CODE_39") && formats.contains("CODE_128") &&
					formats.contains("CODE_93") && formats.contains("CODABAR") &&
					formats.contains("ITF") && formats.contains("EAN_13") &&
					formats.contains("EAN_8") && formats.contains("UPC_E") &&
					formats.contains("INDUSTRIAL_25") && formats.contains("UPC_A")){
				mOned.setChecked(true);
			} else {
				mOned.setChecked(false);
			}*/
		}
		if ((requestCode == REQUEST_ALGORITHM_SETTING && resultCode == RESPONSE_ALGORITHM_SETTING)){
			mSetting = (DBRSetting) data.getSerializableExtra("AlgorithmSetting");
			mImageParameter = mSetting.getImageParameter();
		}
	}

	@Override
	protected void onResume() {
		initSetting();
		super.onResume();
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSpinner();
	}

}
