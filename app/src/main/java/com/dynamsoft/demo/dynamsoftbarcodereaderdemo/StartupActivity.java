package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Elemen on 2018/7/2.
 */
public class StartupActivity extends AppCompatActivity {
	private static final int PRC_PHOTO_PICKER = 1;
	private static final int RC_CHOOSE_PHOTO = 1;
	private static final String TAG = "StartupActivity";
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

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.btn_history, R.id.btn_general, R.id.btn_multi_best, R.id.btn_multi_bal})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_history:
				startActivity(new Intent(StartupActivity.this, HistoryActivity.class));
				break;
			case R.id.btn_general:
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_best:
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_bal:
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			default:
				break;
		}
	}
}


