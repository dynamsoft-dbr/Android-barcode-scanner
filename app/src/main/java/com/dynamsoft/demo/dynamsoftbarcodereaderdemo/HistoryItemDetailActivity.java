package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryDetailViewPagerAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HistoryPreviewViewPager;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/7/13.
 */
public class HistoryItemDetailActivity extends BaseActivity {
	@BindView(R.id.vp_history_detail)
	HistoryPreviewViewPager vpHistoryDetail;
	private DBRCache mCache;
	private String[] fileNames;
	private int position;
	private ArrayList<HistoryItemBean> listItem;
	private HistoryDetailViewPagerAdapter adapter;
	private SimpleAdapter simpleAdapter;


	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		mCache = DBRCache.get(this);
		setToolbarBackgroud("#ffffff");
		setToolbarTitle("Barcode Detail");
		setToolbarTitleColor("#000000");
		fileNames = getIntent().getStringArrayExtra("imgdetail_file");
		position = getIntent().getIntExtra("position", 0);
		fillHistoryList();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_history_item_detail;
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
		adapter = new HistoryDetailViewPagerAdapter(this, listItem);
		vpHistoryDetail.setAdapter(adapter);
		vpHistoryDetail.setCurrentItem(position);
		/*simpleAdapter = new SimpleAdapter(this, recentCodeList,
				R.layout.item_listview_recent_code, new String[]{"format", "text"}, new int[]{R.id.tv_code_format, R.id.tv_code_text});*/
	}
}
