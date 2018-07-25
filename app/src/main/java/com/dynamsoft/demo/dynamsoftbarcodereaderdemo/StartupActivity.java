package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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
	@BindView(R.id.imageButton)
	ImageButton imageButton;
	@BindView(R.id.ib_balance_multi)
	ImageButton imageButton2;
	@BindView(R.id.ib_single)
	ImageButton imageButton3;
	@BindView(R.id.tv_history)
	TextView tvHistory;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.imageButton, R.id.ib_balance_multi, R.id.ib_single, R.id.tv_history,R.id.ib_fast_multi})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.imageButton:

			case R.id.ib_balance_multi:

			case R.id.ib_single:
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.tv_history:
				startActivity(new Intent(StartupActivity.this,HistoryActivity.class));
				break;
			default:
				break;
		}
	}
}


