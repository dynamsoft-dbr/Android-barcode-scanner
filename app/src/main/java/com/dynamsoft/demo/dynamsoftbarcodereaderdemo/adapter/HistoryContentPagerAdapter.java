package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.BaseFragment;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.BestCoverageFragment;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.GeneralScanFragment;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.OverlapHistoryFragment;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.PanoramaFragment;

import java.util.Arrays;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;

/**
 * Created by Elemen on 2018/8/29.
 */
public class HistoryContentPagerAdapter extends FragmentPagerAdapter {
	private String[] mTitles;
	private List<BaseFragment> mFragmentList = Arrays.asList(
			new GeneralScanFragment(),
			new BestCoverageFragment(),
			new OverlapHistoryFragment(),
			new PanoramaFragment()
	);

	public HistoryContentPagerAdapter(FragmentManager fm, String[] mTitles) {
		super(fm);
		this.mTitles = mTitles;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}
}
