package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryListAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;

import org.litepal.LitePal;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

/**
 * Created by Elemen on 2018/8/29.
 */
public class OverlapHistoryFragment extends BaseFragment {
	private RecyclerView rlvHistory;
	private ProgressBar progressBar;

	private HistoryListAdapter historyListAdapter;
	private List<DBRImage> imageList;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (imageList != null && imageList.size() > 0) {
				historyListAdapter.clear();
			}
			progressBar.setVisibility(View.GONE);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history_item, null);
		rlvHistory = v.findViewById(R.id.rl_history);
		progressBar = v.findViewById(R.id.pb_progress);
		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		historyListAdapter = new HistoryListAdapter(rlvHistory);
		historyListAdapter.setOnItemChildClickListener(this);
		fillHistoryList();
	}

	private void fillHistoryList() {
		imageList = LitePal.findAll(DBRImage.class);
		Collections.reverse(imageList);
		if (imageList.size() > 16) {
			imageList = imageList.subList(0, 16);
		}
		historyListAdapter.setData(imageList);
		rlvHistory.addItemDecoration(BGADivider.newShapeDivider());
		rlvHistory.setLayoutManager(getLinearLayoutManager());
		rlvHistory.setAdapter(historyListAdapter);
	}

	public void clearHistoryList() {
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<DBRImage> imageList = LitePal.findAll(DBRImage.class);
				if (imageList != null && imageList.size() > 0) {
					String path = Environment.getExternalStorageDirectory() + "/dbr-preview-img";
					File file = new File(path);
					String[] fileNames = file.list();
					if (fileNames != null && fileNames.length > 0) {
						for (int i = 0; i < fileNames.length; i++) {
							File temp = new File(path, fileNames[i]);
							if (temp.isFile()) {
								temp.delete();
							}
						}
					}
					LitePal.deleteAll(DBRImage.class);
				}
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	private RecyclerView.LayoutManager getLinearLayoutManager() {
		return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
	}

	@Override
	public void onItemChildClick(ViewGroup parent, View childView, int position) {
		switch (childView.getId()) {
			case R.id.cl_item_history:
				Intent intent = new Intent(getActivity(), HistoryItemDetailActivity.class);
				intent.putExtra("page_type", 1);
				intent.putExtra("position", position);
				intent.putExtra("templateType", "OverlapSetting");
				startActivity(intent);
				break;
			default:
				break;
		}
	}
}
