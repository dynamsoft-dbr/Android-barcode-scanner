package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BarcodeTypeActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_type);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.barcodetypetoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCache = DBRCache.get(this, "SettingCache");
        if ("true".equals(mCache.getAsString("CODE_39"))) {
            mCode39.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("CODE_128"))) {
            mCode128.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("CODE_93"))) {

            mCode93.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("CODABAR"))) {
            mCodabar.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("ITF"))){
            mITF.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("EAN_13"))){
            mEAN13.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("EAN_8"))){
            mEAN8.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("UPC_A"))){
            mUPCA.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("UPC_E"))){
            mUPCE.setChecked(true);
        }
        if ("true".equals(mCache.getAsString("INDUSTRIAL_25"))){
            mIndustrial25.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCode39.isChecked()) {
            mCache.put("CODE_39", "true");
        } else {
            mCache.put("CODE_39", "false");
        }
        if (mCode128.isChecked()) {
            mCache.put("CODE_128", "true");
        } else {
            mCache.put("CODE_128", "false");
        }
        if (mCode93.isChecked()) {
            mCache.put("CODE_93", "true");
        } else {
            mCache.put("CODE_93", "false");
        }
        if (mCodabar.isChecked()) {
            mCache.put("CODABAR", "true");
        } else {
            mCache.put("CODABAR", "false");
        }
        if (mITF.isChecked()) {
            mCache.put("ITF", "true");
        } else {
            mCache.put("ITF", "false");
        }
        if (mEAN13.isChecked()) {
            mCache.put("EAN_13", "true");
        } else {
            mCache.put("EAN_13", "false");
        }
        if (mEAN8.isChecked()) {
            mCache.put("EAN_8", "true");
        } else {
            mCache.put("EAN_8", "false");
        }
        if (mUPCA.isChecked()) {
            mCache.put("UPC_A", "true");
        } else {
            mCache.put("UPC_A", "false");
        }
        if (mUPCE.isChecked()) {
            mCache.put("UPC_E", "true");
        } else {
            mCache.put("UPC_E", "false");
        }
        if (mIndustrial25.isChecked()){
            mCache.put("INDUSTRIAL_25", "true");
        } else {
            mCache.put("INDUSTRIAL_25", "false");
        }
        setResult(0);
        super.onBackPressed();
    }

}
