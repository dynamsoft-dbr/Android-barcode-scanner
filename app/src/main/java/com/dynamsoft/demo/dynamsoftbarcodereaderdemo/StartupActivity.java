package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StartupActivity extends AppCompatActivity {
	private static final int PRC_PHOTO_PICKER = 1;
	private static final int RC_CHOOSE_PHOTO = 1;
	private static final String TAG = "StartupActivity";
	DBRCache mCache;
	TextView tvHistory;
	@BindView(R.id.btn_history)
	Button btnHistory;
	@BindView(R.id.tv_startup_title)
	TextView tvStartupTitle;
	@BindView(R.id.btn_general)
	Button btnGeneral;
	@BindView(R.id.btn_multi_best)
	Button btnMultiBest;
	@BindView(R.id.btn_multi_bal)
	Button btnMultiBal;
	@BindView(R.id.btn_panorama)
	Button btnPanorma;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.btn_history, R.id.btn_general, R.id.btn_multi_best, R.id.btn_multi_bal, R.id.btn_panorama, R.id.tv_history})
	public void onViewClicked(View view) {
		mCache = DBRCache.get(this, "SettingCache");
		switch (view.getId()) {
			case R.id.btn_history:
				startActivity(new Intent(StartupActivity.this, HistoryActivity.class));
				break;
			case R.id.tv_history:
				startActivity(new Intent(StartupActivity.this, HistoryActivity.class));
				break;
			case R.id.btn_general:
				mCache.put("templateType", "GeneralSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_best:
				mCache.put("templateType", "MultiBestSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_bal:
				mCache.put("templateType", "MultiBalSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_panorama:
				mCache.put("templateType","PanormaSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
			default:
				break;
		}
	}
}


