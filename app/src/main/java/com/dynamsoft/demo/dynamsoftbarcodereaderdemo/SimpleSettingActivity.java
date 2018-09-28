package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class SimpleSettingActivity extends BaseActivity {
    private DBRSetting mSetting;
    private DBRCache mSettingCache;
    private DBRSetting.ImageParameter mImageParameter;
    private boolean beepSoundEnable;
    private final int REQUEST_ONED_SETTING = 0x0001;
    private final int RESPONSE_ONED_SETTING = 0x0001;
    private List<String> barcodeInvertMode = new ArrayList<>();
    private ArrayAdapter<String> barcodeInvertModeSpinnerAdapter;
    private ArrayList<String> tempFormats;
    @BindView(R.id.simple_setoned)
    ImageView ivSetOned;
    @BindView(R.id.simple_sp_barcode_invert_mode)
    Spinner spBarcodeInvertMode;
    @BindView(R.id.simple_ckbpdf417)
    CheckBox mPDF417;
    @BindView(R.id.simple_ckbqrcode)
    CheckBox mQRCode;
    @BindView(R.id.simple_ckbdatamatrix)
    CheckBox mDataMatrix;
    @BindView(R.id.simple_ckbaztec)
    CheckBox mAZTEC;
    @BindView(R.id.simple_sc_beep_sound)
    SwitchCompat scBeepSound;
    @BindView(R.id.simple_tv_scale_down_threshold)
    TextView tvScaleDownThreshold;
    @BindView(R.id.simple_et_scale_down_threshold)
    EditText etScaleDownThreshold;
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
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbarBackgroud("#000000");
        setToolbarNavIcon(R.drawable.ic_action_back_dark);
        setToolbarTitle("Settings");
        setToolbarTitleColor("#ffffff");
        initUI();
        initSetting();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_setting;
    }
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.simple_setoned:
                Intent intent = new Intent(SimpleSettingActivity.this, BarcodeTypeActivity.class);
                intent.putExtra("DBRSetting", mSetting);
                startActivityForResult(intent, REQUEST_ONED_SETTING);
                break;
            case R.id.simple_tv_scale_down_threshold:
                etScaleDownThreshold.setText(tvScaleDownThreshold.getText());
                tvScaleDownThreshold.setVisibility(View.GONE);
                etScaleDownThreshold.setVisibility(View.VISIBLE);
                break;
            case R.id.simple_iv_scale_down_threshold:
                AlertDialog builder1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme)).setTitle(R.string.scale_down_Threshold).setMessage(R.string.scale_down_threshold_tip).create();
                builder1.show();
                break;
            case R.id.simple_iv_barcode_invert_mode:
                AlertDialog builder2 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme)).setTitle(R.string.barcode_invert_mode).setMessage(R.string.barcode_invert_mode_tip).create();
                builder2.show();
                break;
            case R.id.simple_iv_beepsound:
                AlertDialog builder3 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme)).setTitle(R.string.beep_sound).setMessage(R.string.beep_sound_tip).create();
                builder3.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mImageParameter.getBarcodeFormatIds().size() > 0){
            mSettingCache.put("beepSound", String.valueOf(beepSoundEnable));
            mSetting.setImageParameter(mImageParameter);
            try {
                mSettingCache.put("Setting", LoganSquare.serialize(mSetting));
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            super.onBackPressed();
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

    private void initUI(){
        mSettingCache = DBRCache.get(this, "SettingCache");
        try {
            mSetting = LoganSquare.parse(mSettingCache.getAsString("Setting"), DBRSetting.class);
        }
        catch (Exception ex){

            ex.printStackTrace();

        }
        mImageParameter = mSetting.getImageParameter();
        barcodeInvertMode.add("DarkOnLight");
        barcodeInvertMode.add("LightOnDark");
        barcodeInvertModeSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, barcodeInvertMode);
        barcodeInvertModeSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spBarcodeInvertMode.setAdapter(barcodeInvertModeSpinnerAdapter);
        mDataMatrix.setOnCheckedChangeListener(onCKBCheckedChange);
        mQRCode.setOnCheckedChangeListener(onCKBCheckedChange);
        mPDF417.setOnCheckedChangeListener(onCKBCheckedChange);
        mAZTEC.setOnCheckedChangeListener(onCKBCheckedChange);
        spBarcodeInvertMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    mImageParameter.setBarcodeInvertMode(barcodeInvertMode.get(0));
                }
                else{
                    mImageParameter.setBarcodeInvertMode(barcodeInvertMode.get(1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etScaleDownThreshold.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    int tempValue;
                    try {
                        imm.hideSoftInputFromWindow(etScaleDownThreshold.getWindowToken(), 0);
                        tempValue = Integer.parseInt(etScaleDownThreshold.getText().toString());
                        if (tempValue >= 512) {
                            mImageParameter.setScaleDownThreshold(tempValue);
                        } else {
                            Toast.makeText(SimpleSettingActivity.this, "Input Invalid! Legal value: [512, 0x7fffffff]", Toast.LENGTH_LONG).show();
                            mImageParameter.setScaleDownThreshold(1000);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(SimpleSettingActivity.this, "Input Invalid! Legal value: [512, 0x7fffffff]", Toast.LENGTH_LONG).show();
                        mImageParameter.setScaleDownThreshold(1000);
                    }
                    tvScaleDownThreshold.setText(String.valueOf(mImageParameter.getScaleDownThreshold()));
                    etScaleDownThreshold.setVisibility(View.GONE);
                    tvScaleDownThreshold.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
        scBeepSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beepSoundEnable = scBeepSound.isChecked();
            }
        });
    }
    private void initSetting(){
        scBeepSound.setChecked(Boolean.parseBoolean(mSettingCache.getAsString("beepSound")));
        if (mImageParameter.getBarcodeInvertMode().equals(barcodeInvertMode.get(0))){
            spBarcodeInvertMode.setSelection(0);
        }else {
            spBarcodeInvertMode.setSelection(1);
        }
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
        tvScaleDownThreshold.setText(String.valueOf(mImageParameter.getScaleDownThreshold()));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ONED_SETTING && resultCode == RESPONSE_ONED_SETTING)) {
            mSetting = (DBRSetting) data.getSerializableExtra("OneDSetting");
            mImageParameter = mSetting.getImageParameter();
        }
    }
    CheckBox.OnCheckedChangeListener onCKBCheckedChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.simple_ckbpdf417:
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
                case R.id.simple_ckbqrcode:
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
                case R.id.simple_ckbdatamatrix:
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
                case R.id.simple_ckbaztec:
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
}
