package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class HistoryContentPagerAdapter extends FragmentPagerAdapter {
	private String[] mTitles;
	private List<Fragment> mFragmentList;

	public HistoryContentPagerAdapter(FragmentManager fm, String[] mTitles, List<Fragment> mFragmentList) {
		super(fm);
		this.mTitles = mTitles;
		this.mFragmentList = mFragmentList;
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
