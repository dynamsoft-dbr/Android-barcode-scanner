package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter.HistoryContentPagerAdapter;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends BaseActivity implements OnTabSelectListener {
	@BindView(R.id.vp_history_content)
	ViewPager vpHistoryContent;
	@BindView(R.id.tl_2)
	SlidingTabLayout generalScanTab;
	private Handler handler;
	private HistoryContentPagerAdapter historyContentPagerAdapter;
	private final int PAGE_TYPE = 0x0001;
	private final String[] mTitles = {"General Scan", "Best Coverage", "Overlap", "Customized"};
	private List<Fragment> mFragmentList = new ArrayList<>();
	private OverlapHistoryFragment overLapFg;
	private GeneralScanFragment generalSanFg;
	private BestCoverageFragment bestCoverageFg;
	private CustomFragment customFg;
	//private PanoramaFragment panoramaFg;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_history;
	}

	private String pageTitle;

	@Override
	protected void init(Bundle savedInstanceState) {
		ButterKnife.bind(this);
		setToolbarBackgroud("#ffffff");
		setToolbarTitle("History");
		setToolbarTitleColor("#000000");
		setToolbarNavIcon(R.drawable.ic_action_back);

		overLapFg = new OverlapHistoryFragment();
		generalSanFg = new GeneralScanFragment();
		bestCoverageFg = new BestCoverageFragment();
		customFg = new CustomFragment();
		//panoramaFg = new PanoramaFragment();
		mFragmentList.add(generalSanFg);
		mFragmentList.add(bestCoverageFg);
		mFragmentList.add(overLapFg);
		mFragmentList.add(customFg);
		//mFragmentList.add(panoramaFg);

		historyContentPagerAdapter = new HistoryContentPagerAdapter(getSupportFragmentManager(), mTitles, mFragmentList);
		vpHistoryContent.setAdapter(historyContentPagerAdapter);
		String type = getIntent().getStringExtra("templateType");
		if (type != null) {
			if ("GeneralSetting".equals(type)) {
				vpHistoryContent.setCurrentItem(0);
			} else if ("MultiBestSetting".equals(type)) {
				vpHistoryContent.setCurrentItem(1);
			} else if ("OverlapSetting".equals(type)) {
				vpHistoryContent.setCurrentItem(2);
			} else if ("CustomSetting".equals(type)) {
				vpHistoryContent.setCurrentItem(3);
			}
		}
		generalScanTab.setOnTabSelectListener(this);
		generalScanTab.setViewPager(vpHistoryContent);
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
		if (pageTitle == null){
			pageTitle = "General Scan";
		}
		builder.setMessage("Clear the history list of " + pageTitle + "?");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				clearHistoryList();
			}
		});
		builder.create();
		builder.show();
		return super.onOptionsItemSelected(item);
	}

	private void clearHistoryList() {
		switch (vpHistoryContent.getCurrentItem()) {
			case 0:
				generalSanFg.clearHistoryList();
				break;
			case 1:
				bestCoverageFg.clearHistoryList();
				break;
			case 2:
				overLapFg.clearHistoryList();
				break;
			case 3:
				customFg.clearHistoryList();
				break;
			default:
				break;
		}
	}

	@Override
	public void onTabSelect(int position) {
		pageTitle = String.valueOf(historyContentPagerAdapter.getPageTitle(position));
	}

	@Override
	public void onTabReselect(int position) {
		pageTitle = String.valueOf(historyContentPagerAdapter.getPageTitle(position));
	}
}
