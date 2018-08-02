package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {
	private DBRCache mCache;
	@BindView(R.id.setoned)
	ImageView ivSetOned;
	@BindView(R.id.ckboned)
	CheckBox mOned;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.settingtoolbar);
		setSupportActionBar(toolbar);
		mCache = DBRCache.get(this, "SettingCache");
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.setoned:
				oneDCache(mOned.isChecked());
				startActivity(new Intent(SettingActivity.this, BarcodeTypeActivity.class));
				break;
			case R.id.setalgorithm:
				startActivity(new Intent(SettingActivity.this, AlgorithmSettingActivity.class));
				break;
			default:
				break;
		}
	}
	private void oneDCache(boolean oneDisChecked){
		if(oneDisChecked){
			mCache.put("CODE_39", "true");
			mCache.put("CODE_128", "true");
			mCache.put("CODE_93", "true");
			mCache.put("CODABAR", "true");
			mCache.put("ITF", "true");
			mCache.put("EAN_13", "true");
			mCache.put("EAN_8", "true");
			mCache.put("UPC_A", "true");
			mCache.put("UPC_E", "true");
			mCache.put("INDUSTRIAL_25", "true");
		}
	}
	private void onedCheck(){
		if (("true".equals(mCache.getAsString("CODE_39"))) && ("true".equals(mCache.getAsString("CODE_128"))) &&
		  	("true".equals(mCache.getAsString("CODE_93"))) && ("true".equals(mCache.getAsString("CODABAR"))) &&
			("true".equals(mCache.getAsString("ITF"))) && ("true".equals(mCache.getAsString("EAN_13"))) &&
			("true".equals(mCache.getAsString("EAN_8"))) &&  ("true".equals(mCache.getAsString("UPC_A"))) &&
			("true".equals(mCache.getAsString("UPC_E"))) && ("true".equals(mCache.getAsString("INDUSTRIAL_25")))){
				mOned.setChecked(true);
		}
		else{
			mOned.setChecked(false);
		}
	}
	@Override
	public void onResume(){
		onedCheck();
		super.onResume();
	}
}
