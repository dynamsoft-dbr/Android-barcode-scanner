package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRSetting;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
	private DBRSetting mSetting;
	private DBRCache mSettingCache;
	private List<String> one2Ten = new ArrayList<>();
	private List<String> colourImageConvertMode = new ArrayList<>();
	private List<String> barcodeInvertMode = new ArrayList<>();
	private ArrayList<String> tempFormats;
	private final ArrayList<String> oneDFormats = new ArrayList<String>(){{
		add("CODE_39");
		add("CODE_93");
		add("CODE_128");
		add("CODABAR");
		add("ITF");
		add("EAN_13");
		add("EAN_8");
		add("UPC_A");
		add("UPC_E");
		add("INDUSTRIAL_25");}};
	private ArrayAdapter<String> one2tenSpinnerAdapter;
	private ArrayAdapter<String> isEnableSpinnerAdapter;
	private ArrayAdapter<String> barcodeInvertModeSpinnerAdapter;
	private ArrayAdapter<String> colourImageConvertModeSpinnerAdapter;
	private final int REQUEST_ONED_SETTING = 0x0001;
	private final int RESPONSE_ONED_SETTING = 0x0001;
	private final int REQUEST_ALGORITHM_SETTING = 0x0002;
	private final int RESPONSE_ALGORITHM_SETTING = 0x0002;
	@BindView(R.id.setoned)
	ImageView ivSetOned;
	@BindView(R.id.ckbpdf417)
	CheckBox mPDF417;
	@BindView(R.id.ckbqrcode)
	CheckBox mQRCode;
	@BindView(R.id.ckbdatamatrix)
	CheckBox mDataMatrix;
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
	@BindView(R.id.tv_max_dimof_full_image_as_barcode_zone)
	TextView tvMaxDimofFullImageAsBarcodeZone;
	@BindView(R.id.et_max_dimof_full_image_as_barcode_zone)
	EditText etMaxDimofFullImageAsBarcodeZone;
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
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_setting;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		setToolbarBackgroud("#000000");
		setToolbarNavIcon(R.drawable.ic_action_back_dark);
		setToolbarTitle("Setting");
		setToolbarTitleColor("#ffffff");
		initSpinner();
		initSetting();
		etExpectedBarcodeCount.setOnEditorActionListener(onEditFinish);
		etTimeout.setOnEditorActionListener(onEditFinish);
		etExpectedBarcodeCount.setOnEditorActionListener(onEditFinish);
		etBinarizationBlockSize.setOnEditorActionListener(onEditFinish);
		etMaxDimofFullImageAsBarcodeZone.setOnEditorActionListener(onEditFinish);
		etMaxBarcodeCount.setOnEditorActionListener(onEditFinish);
		etScaleDownThreshold.setOnEditorActionListener(onEditFinish);
		scEnableFillBinaryVacancy.setOnCheckedChangeListener(onSCCheckedChange);
		scRegionPredetectionMode.setOnCheckedChangeListener(onSCCheckedChange);
		scTextFilterMode.setOnCheckedChangeListener(onSCCheckedChange);
		mDataMatrix.setOnCheckedChangeListener(onCKBCheckedChange);
		mQRCode.setOnCheckedChangeListener(onCKBCheckedChange);
		mPDF417.setOnCheckedChangeListener(onCKBCheckedChange);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_share).setVisible(false);
		menu.findItem(R.id.menu_capture).setVisible(false);
		menu.findItem(R.id.menu_file).setVisible(false);
		menu.findItem(R.id.menu_scanning).setVisible(false);
		menu.findItem(R.id.menu_Setting).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
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
			case R.id.tv_max_dimof_full_image_as_barcode_zone:
				etMaxDimofFullImageAsBarcodeZone.setText(tvMaxDimofFullImageAsBarcodeZone.getText());
				tvMaxDimofFullImageAsBarcodeZone.setVisibility(View.GONE);
				etMaxDimofFullImageAsBarcodeZone.setVisibility(View.VISIBLE);
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
	private void initSpinner(){
		barcodeInvertMode.add("DarkOnLight");
		barcodeInvertMode.add("LightOnDark");
		barcodeInvertModeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, barcodeInvertMode);
		barcodeInvertModeSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		colourImageConvertMode.add("Auto");
		colourImageConvertMode.add("Grayscale");
		colourImageConvertModeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, colourImageConvertMode);
		colourImageConvertModeSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		for(int i = 0; i < 10; i++){
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

		spDeblurLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSetting.setDeblurLevel(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spAntiDamageLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSetting.setAntiDamageLevel(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spGrayEqualizationSensitivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSetting.setGrayEqualizationSensitivity(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spTextureDetectionSensitivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSetting.setTextureDetectionSensitivity(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spBarcodeInvertMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					mSetting.setBarcodeInvertMode(barcodeInvertMode.get(0));
				}
				else{
					mSetting.setBarcodeInvertMode(barcodeInvertMode.get(1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spColourImageConvertMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position ==0){
					mSetting.setColourImageConvertMode(colourImageConvertMode.get(0));
				}
				else {
					mSetting.setColourImageConvertMode(colourImageConvertMode.get(1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}
	private void initSetting(){
		mSettingCache = DBRCache.get(this, "SettingCache");
		try {
			mSetting = LoganSquare.parse(mSettingCache.getAsString("GeneralSetting"), DBRSetting.class);
			tvExpectedBarcodeCount.setText(String.valueOf(mSetting.getExpectedBarcodesCount()));
			tvTimeout.setText(String.valueOf(mSetting.getTimeout()));
			spDeblurLevel.setSelection(mSetting.getDeblurLevel());
			spAntiDamageLevel.setSelection(mSetting.getAntiDamageLevel());
			scTextFilterMode.setChecked(mSetting.isTextFilterMode());
			scRegionPredetectionMode.setChecked(mSetting.isRegionPredetectionMode());
			tvScaleDownThreshold.setText(String.valueOf(mSetting.getScaleDownThreshold()));
			if (mSetting.getColourImageConvertMode().equals(colourImageConvertMode.get(0))){
				spColourImageConvertMode.setSelection(0);
			}else {
				spColourImageConvertMode.setSelection(1);
			}
			if (mSetting.getBarcodeInvertMode().equals(barcodeInvertMode.get(0))){
				spBarcodeInvertMode.setSelection(0);
			}else {
				spBarcodeInvertMode.setSelection(1);
			}
			spGrayEqualizationSensitivity.setSelection(mSetting.getGrayEqualizationSensitivity());
			spTextureDetectionSensitivity.setSelection(mSetting.getTextureDetectionSensitivity());
			tvBinarizationBlockSize.setText(String.valueOf(mSetting.getBinarizationBlockSize()));

			tvMaxDimofFullImageAsBarcodeZone.setText(String.valueOf(mSetting.getMaxDimOfFullImageAsBarcodeZone()));
			tvMaxBarcodeCount.setText(String.valueOf(mSetting.getMaxBarcodesCount()));
			scEnableFillBinaryVacancy.setChecked(mSetting.isEnableFillBinaryVacancy());
			ArrayList<String> formats = mSetting.getBarcodeFormatIds();
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
	@Override
	public void onBackPressed(){
		try {
			mSettingCache.put("GeneralSetting", LoganSquare.serialize(mSetting));
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		super.onBackPressed();
	}
	SwitchCompat.OnCheckedChangeListener onSCCheckedChange = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()){
				case R.id.sc_enable_fill_binary_vacancy:
					mSetting.setEnableFillBinaryVacancy(scEnableFillBinaryVacancy.isChecked());
					break;
				case R.id.sc_region_predetection_mode:
					mSetting.setRegionPredetectionMode(scRegionPredetectionMode.isChecked());
					break;
				case R.id.sc_text_filter_mode:
					mSetting.setTextFilterMode(scTextFilterMode.isChecked());
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
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.add("PDF417");
						mSetting.setBarcodeFormatIds(tempFormats);
					}else {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.remove("PDF417");
						mSetting.setBarcodeFormatIds(tempFormats);
					}
					break;
				case R.id.ckbqrcode:
					if (mQRCode.isChecked()) {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.add("QR_CODE");
						mSetting.setBarcodeFormatIds(tempFormats);
					}else {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.remove("QR_CODE");
						mSetting.setBarcodeFormatIds(tempFormats);
					}
					break;
				case R.id.ckbdatamatrix:
					if (mDataMatrix.isChecked()) {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.add("DATAMATRIX");
						mSetting.setBarcodeFormatIds(tempFormats);
					}else {
						tempFormats = mSetting.getBarcodeFormatIds();
						tempFormats.remove("DATAMATRIX ");
						mSetting.setBarcodeFormatIds(tempFormats);
					}
					break;
				default:
					break;
			}
		}
	};
	EditText.OnEditorActionListener onEditFinish = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.ACTION_DOWN) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				switch (v.getId()) {
					case R.id.et_expected_barcode_count:
						tvExpectedBarcodeCount.setText(etExpectedBarcodeCount.getText());
						imm.hideSoftInputFromWindow(etExpectedBarcodeCount.getWindowToken(), 0);
						etExpectedBarcodeCount.setVisibility(View.GONE);
						tvExpectedBarcodeCount.setVisibility(View.VISIBLE);
						mSetting.setExpectedBarcodesCount(Integer.parseInt(tvExpectedBarcodeCount.getText().toString()));
						break;
					case R.id.et_timeout:
						tvTimeout.setText(etTimeout.getText());
						imm.hideSoftInputFromWindow(etTimeout.getWindowToken(), 0);
						etTimeout.setVisibility(View.GONE);
						tvTimeout.setVisibility(View.VISIBLE);
						mSetting.setTimeout(Integer.parseInt(tvTimeout.getText().toString()));
						break;
					case R.id.et_scale_down_threshold:
						tvScaleDownThreshold.setText(etScaleDownThreshold.getText());
						imm.hideSoftInputFromWindow(etScaleDownThreshold.getWindowToken(), 0);
						etScaleDownThreshold.setVisibility(View.GONE);
						tvScaleDownThreshold.setVisibility(View.VISIBLE);
						mSetting.setScaleDownThreshold(Integer.parseInt(tvScaleDownThreshold.getText().toString()));
						break;
					case R.id.et_binarization_block_size:
						tvBinarizationBlockSize.setText(etBinarizationBlockSize.getText());
						imm.hideSoftInputFromWindow(etBinarizationBlockSize.getWindowToken(), 0);
						etBinarizationBlockSize.setVisibility(View.GONE);
						tvBinarizationBlockSize.setVisibility(View.VISIBLE);
						mSetting.setBinarizationBlockSize(Integer.parseInt(tvBinarizationBlockSize.getText().toString()));
						break;
					case R.id.et_max_dimof_full_image_as_barcode_zone:
						tvMaxDimofFullImageAsBarcodeZone.setText(etMaxDimofFullImageAsBarcodeZone.getText());
						imm.hideSoftInputFromWindow(etMaxDimofFullImageAsBarcodeZone.getWindowToken(), 0);
						etMaxDimofFullImageAsBarcodeZone.setVisibility(View.GONE);
						tvMaxDimofFullImageAsBarcodeZone.setVisibility(View.VISIBLE);
						mSetting.setMaxDimOfFullImageAsBarcodeZone(Integer.parseInt(tvMaxDimofFullImageAsBarcodeZone.getText().toString()));
						break;
					case R.id.et_max_barcode_count:
						tvMaxBarcodeCount.setText(etMaxBarcodeCount.getText());
						imm.hideSoftInputFromWindow(etMaxBarcodeCount.getWindowToken(), 0);
						etMaxBarcodeCount.setVisibility(View.GONE);
						tvMaxBarcodeCount.setVisibility(View.VISIBLE);
						mSetting.setMaxBarcodesCount(Long.parseLong(tvMaxBarcodeCount.getText().toString()));
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
		}
	}
}
