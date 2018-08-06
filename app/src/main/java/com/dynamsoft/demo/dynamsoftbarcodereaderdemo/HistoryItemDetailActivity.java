package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryDetailViewPagerAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight.HistoryPreviewViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/7/13.
 */
public class HistoryItemDetailActivity extends BaseActivity {
	@BindView(R.id.vp_history_detail)
	HistoryPreviewViewPager vpHistoryDetail;
	@BindView(R.id.lv_code_list)
	ListView lvCodeList;
	private DBRCache mCache;
	private String[] fileNames;
	private int intentPosition;
	private ArrayList<HistoryItemBean> listItem;
	private HistoryDetailViewPagerAdapter adapter;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, String>> recentCodeList = new ArrayList<>();


	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		mCache = DBRCache.get(this);
		setToolbarBackgroud("#ffffff");
		setToolbarTitle("Barcode Detail");
		setToolbarTitleColor("#000000");
		fileNames = getIntent().getStringArrayExtra("imgdetail_file");
		intentPosition = getIntent().getIntExtra("position", 0);
		fillHistoryList();
		vpHistoryDetail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				fillCodeList(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
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
		vpHistoryDetail.setCurrentItem(intentPosition);
		simpleAdapter = new SimpleAdapter(this, recentCodeList,
				R.layout.item_listview_detail_code_list, new String[]{"index", "format", "text"},
				new int[]{R.id.tv_index, R.id.tv_code_format_content, R.id.tv_code_text_content});
		lvCodeList.setAdapter(simpleAdapter);
		fillCodeList(intentPosition);
	}

	private void fillCodeList(int position) {
		recentCodeList.clear();
		for (int i = 0; i < listItem.get(position).getCodeFormat().size(); i++) {
			Map<String, String> item = new HashMap<>();
			item.put("index", i + "");
			item.put("format", listItem.get(position).getCodeFormat().get(i));
			item.put("text", listItem.get(position).getCodeText().get(i));
			recentCodeList.add(item);
		}
		simpleAdapter.notifyDataSetChanged();
		lvCodeList.startLayoutAnimation();
	}
}
