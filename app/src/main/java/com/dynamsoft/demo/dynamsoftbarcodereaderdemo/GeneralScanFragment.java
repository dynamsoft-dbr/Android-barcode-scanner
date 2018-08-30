package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Elemen on 2018/8/29.
 */
public class GeneralScanFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history_item, null);
		return v;
	}
}
