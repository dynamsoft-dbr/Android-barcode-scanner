package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Elemen on 2018/7/16.
 */
public class HistoryPreviewViewPager extends ViewPager {
	private boolean isSlide = false;

	public HistoryPreviewViewPager(@NonNull Context context) {
		super(context);
	}

	public HistoryPreviewViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSlide(boolean slide) {
		isSlide = slide;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return isSlide;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}
}
