package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryContentPagerAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryListAdapter;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

public class HistoryActivity extends BaseActivity implements OnTabSelectListener {
	@BindView(R.id.vp_history_content)
	ViewPager vpHistoryContent;
	@BindView(R.id.tl_2)
	SlidingTabLayout overlapTab;


	private HistoryContentPagerAdapter historyContentPagerAdapter;

	private final String[] mTitles = {"General Scan", "Best Coverage", "Overlap", "Panorama"};
	@Override
	protected int getLayoutId() {
		return R.layout.activity_history;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		setToolbarBackgroud("#ffffff");
		setToolbarTitle("History");
		setToolbarTitleColor("#000000");
		setToolbarNavIcon(R.drawable.ic_action_back);

		historyContentPagerAdapter = new HistoryContentPagerAdapter(getSupportFragmentManager(), mTitles);
		vpHistoryContent.setAdapter(historyContentPagerAdapter);
		overlapTab.setOnTabSelectListener(this);
		overlapTab.setViewPager(vpHistoryContent);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_share).setVisible(false);
		menu.findItem(R.id.menu_capture).setVisible(false);
		menu.findItem(R.id.menu_file).setVisible(false);
		menu.findItem(R.id.menu_scanning).setVisible(false);
		menu.findItem(R.id.menu_Setting).setVisible(false);
		menu.findItem(R.id.menu_delete).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme));
		builder.setMessage("Clear the history list?");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//progressBar.setVisibility(View.VISIBLE);
				clearHistoryList();
			}
		});
		builder.create();
		builder.show();
		return super.onOptionsItemSelected(item);
	}

	private void clearHistoryList() {
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
				//handler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	public void onTabSelect(int position) {
	}

	@Override
	public void onTabReselect(int position) {

	}
}
