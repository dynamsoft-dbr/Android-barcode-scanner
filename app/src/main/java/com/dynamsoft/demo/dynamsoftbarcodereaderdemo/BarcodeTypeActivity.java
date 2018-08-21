package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRSetting;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BarcodeTypeActivity extends BaseActivity {
    @BindView(R.id.ckbcode39)
    CheckBox mCode39;
    @BindView(R.id.ckbcode128)
    CheckBox mCode128;
    @BindView(R.id.ckbcode93)
    CheckBox mCode93;
    @BindView(R.id.ckbcodabar)
    CheckBox mCodabar;
    @BindView(R.id.ckbitf)
    CheckBox mITF;
    @BindView(R.id.ckbean13)
    CheckBox mEAN13;
    @BindView(R.id.ckbean8)
    CheckBox mEAN8;
    @BindView(R.id.ckbupca)
    CheckBox mUPCA;
    @BindView(R.id.ckbupce)
    CheckBox mUPCE;
    @BindView(R.id.ckbindustrial25)
    CheckBox mIndustrial25;
    private int mBarcodeFormat;
    private DBRCache mCache;
    private ArrayList<String> tempFormats;
    private DBRSetting mSetting;
    private DBRSetting.ImageParameter mImageParameter;
    private final int REQUEST_ONED_SETTING = 0x0001;
    private final int RESPONSE_ONED_SETTING = 0x0001;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_barcode_type;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbarBackgroud("#000000");
        setToolbarNavIcon(R.drawable.ic_action_back_dark);
        setToolbarTitle("OneD Barcode Format");
        setToolbarTitleColor("#ffffff");
        initUI();
        mCode39.setOnCheckedChangeListener(checkedChangeListener);
        mCode93.setOnCheckedChangeListener(checkedChangeListener);
        mCode128.setOnCheckedChangeListener(checkedChangeListener);
        mCodabar.setOnCheckedChangeListener(checkedChangeListener);
        mITF.setOnCheckedChangeListener(checkedChangeListener);
        mUPCE.setOnCheckedChangeListener(checkedChangeListener);
        mUPCA.setOnCheckedChangeListener(checkedChangeListener);
        mEAN8.setOnCheckedChangeListener(checkedChangeListener);
        mEAN13.setOnCheckedChangeListener(checkedChangeListener);
        mIndustrial25.setOnCheckedChangeListener(checkedChangeListener);
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
    CheckBox.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.ckbcode39:
                    if (mCode39.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("CODE_39");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("CODE_39");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbcode93:
                    if (mCode93.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("CODE_93");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("CODE_93");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbcode128:
                    if (mCode128.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("CODE_128");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("CODE_128");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbcodabar:
                    if (mCodabar.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("CODABAR");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("CODABAR");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbitf:
                    if (mITF.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("ITF");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("ITF");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbean8:
                    if (mEAN8.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("EAN_8");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("EAN_8");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbean13:
                    if (mEAN13.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("EAN_13");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("EAN_13");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbupca:
                    if (mUPCA.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("UPC_A");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("UPC_A");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbupce:
                    if (mUPCE.isChecked()) {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("UPC_E");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("UPC_E");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                case R.id.ckbindustrial25:
                    if (mIndustrial25.isChecked()){
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.add("INDUSTRIAL_25");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    } else {
                        tempFormats = mImageParameter.getBarcodeFormatIds();
                        tempFormats.remove("INDUSTRIAL_25");
                        mImageParameter.setBarcodeFormatIds(tempFormats);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        mSetting.setImageParameter(mImageParameter);
        Intent intent = new Intent();
        intent.putExtra("OneDSetting", mSetting);
        setResult(RESPONSE_ONED_SETTING, intent);
        super.onBackPressed();
    }
    private void initUI(){
        mSetting = (DBRSetting) getIntent().getSerializableExtra("DBRSetting");
        mImageParameter = mSetting.getImageParameter();
        ArrayList<String> formats = mImageParameter.getBarcodeFormatIds();
        if (formats.contains("CODE_39")){
            mCode39.setChecked(true);
        } else {
            mCode39.setChecked(false);
        }
        if (formats.contains("CODE_128")){
            mCode128.setChecked(true);
        } else {
            mCode128.setChecked(false);
        }
        if (formats.contains("CODE_93")){
            mCode93.setChecked(true);
        } else {
            mCode93.setChecked(false);
        }
        if (formats.contains("CODABAR")){
            mCodabar.setChecked(true);
        } else {
            mCodabar.setChecked(false);
        }
        if (formats.contains("ITF")){
            mITF.setChecked(true);
        } else {
            mITF.setChecked(false);
        }
        if (formats.contains("EAN_13")){
            mEAN13.setChecked(true);
        } else {
            mEAN13.setChecked(false);
        }
        if (formats.contains("EAN_8")){
            mEAN8.setChecked(true);
        } else {
            mEAN8.setChecked(false);
        }
        if (formats.contains("UPC_A")){
            mUPCA.setChecked(true);
        } else {
            mUPCA.setChecked(false);
        }
        if (formats.contains("UPC_E")) {
            mUPCE.setChecked(true);
        } else {
            mUPCE.setChecked(false);
        }
        if (formats.contains("INDUSTRIAL_25")) {
            mIndustrial25.setChecked(true);
        } else {
            mIndustrial25.setChecked(false);
        }
    }

}
