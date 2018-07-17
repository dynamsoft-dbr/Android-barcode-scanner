package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryDetailViewPagerAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HistoryPreviewViewPager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/7/13.
 */
public class HistoryItemDetailActivity extends AppCompatActivity {
	@BindView(R.id.vp_history_detail)
	HistoryPreviewViewPager vpHistoryDetail;
	private DBRCache mCache;
	private String[] fileNames;
	private int position;
	private ArrayList<HistoryItemBean> listItem;
	private HistoryDetailViewPagerAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_item_detail);
		ButterKnife.bind(this);
		mCache = DBRCache.get(this);
		fileNames = getIntent().getStringArrayExtra("imgdetail_file");
		position=getIntent().getIntExtra("position",0);
		fillHistoryList();
	}



	private void fillHistoryList() {
		HistoryItemBean historyItemBean;
		listItem = new ArrayList<>();
		for (int i = 0; i < fileNames.length; i++) {
			try {
				historyItemBean = LoganSquare.parse(mCache.getAsStringWithFileName(fileNames[i]),
						HistoryItemBean.class);
				if (historyItemBean != null) {
					listItem.add(historyItemBean);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		adapter = new HistoryDetailViewPagerAdapter(this, listItem, false);
		vpHistoryDetail.setAdapter(adapter);
		vpHistoryDetail.setCurrentItem(position);
	}
}
