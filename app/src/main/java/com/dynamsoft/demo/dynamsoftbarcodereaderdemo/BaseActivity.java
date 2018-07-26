package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by Elemen on 2018/7/25.
 */
public abstract class BaseActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private FrameLayout viewContent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_top_bar);
		Logger.addLogAdapter(new AndroidLogAdapter());
		toolbar = findViewById(R.id.toolbar);
		viewContent = findViewById(R.id.fl_view_content);
		setSupportActionBar(toolbar);
		LayoutInflater.from(this).inflate(getLayoutId(), viewContent);
		init(savedInstanceState);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	protected abstract int getLayoutId();

	protected abstract void init(Bundle savedInstanceState);
}
