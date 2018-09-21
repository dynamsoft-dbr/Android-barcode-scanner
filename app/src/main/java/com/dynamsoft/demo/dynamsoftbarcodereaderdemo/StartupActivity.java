package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StartupActivity extends AppCompatActivity {
	private static final Slide SLIDE_RIGHT = new Slide(Gravity.RIGHT);
	private static final Slide SLIDE_LEFT = new Slide(Gravity.LEFT);
	DBRCache mCache;
	@BindView(R.id.btn_history)
	Button btnHistory;
	@BindView(R.id.tv_startup_title)
	TextView tvStartupTitle;
	@BindView(R.id.btn_general_show_detail)
	ImageButton btnGeneralShow;
	@BindView(R.id.btn_best_coverage_show_detail)
	ImageButton btnBestCoverageShow;
	@BindView(R.id.btn_overlap_show_detail)
	ImageButton btnOverlapShow;
	@BindView(R.id.btn_custom_show_detail)
	ImageButton btnCustomShow;
	@BindView(R.id.btn_general)
	View btnGeneral;
	@BindView(R.id.btn_multi_best)
	View btnMultiBest;
	@BindView(R.id.btn_multi_bal)
	View btnMultiBal;
	@BindView(R.id.btn_custom)
	View btnCustom;
	@BindView(R.id.tv_history)
	TextView tvHistory;
	@BindView(R.id.tv_general)
	LinearLayout tvGeneral;
	@BindView(R.id.tv_best_coverage)
	LinearLayout tvBestCoverage;
	@BindView(R.id.tv_overlap)
	LinearLayout tvOverlap;
	@BindView(R.id.tv_custom)
	LinearLayout tvCustom;
	@BindView(R.id.view_general)
	LinearLayout viewGeneral;
	@BindView(R.id.view_best_coverage)
	LinearLayout viewBestCoverage;
	@BindView(R.id.view_oervlap)
	LinearLayout viewOverlap;
	@BindView(R.id.view_custom)
	LinearLayout viewCustom;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.btn_history, R.id.btn_general, R.id.btn_multi_best, R.id.btn_multi_bal, R.id.tv_history, R.id.tv_url, R.id.btn_custom, R.id.btn_general_show_detail, R.id.btn_best_coverage_show_detail, R.id.btn_overlap_show_detail, R.id.btn_custom_show_detail, R.id.tv_custom, R.id.tv_overlap, R.id.tv_best_coverage, R.id.tv_general})
	public void onViewClicked(View view) {
		mCache = DBRCache.get(this, "SettingCache");
		switch (view.getId()) {
			case R.id.btn_history:
				startActivity(new Intent(StartupActivity.this, HistoryActivity.class));
				break;
			case R.id.tv_history:
				startActivity(new Intent(StartupActivity.this, HistoryActivity.class));
				break;
			case R.id.btn_general:
				mCache.put("templateType", "GeneralSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_best:
				mCache.put("templateType", "MultiBestSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_multi_bal:
				mCache.put("templateType", "OverlapSetting");
				startActivity(new Intent(StartupActivity.this, MainActivity.class));
				break;
			case R.id.btn_custom:
				mCache.put("templateType", "CustomSetting");
				startActivity(new Intent(StartupActivity.this, SettingActivity.class));
				break;
			case R.id.tv_url:
				openUrl();
				break;
			case R.id.btn_general_show_detail:
				viewGeneralGone();
				if (tvBestCoverage.getVisibility() == View.VISIBLE) {
					tvBestCoverageGone();
				}
				if (tvOverlap.getVisibility() == View.VISIBLE) {
					tvOverlapGone();
				}
				if (tvCustom.getVisibility() == View.VISIBLE) {
					tvCustomGone();
				}
				break;
			case R.id.btn_best_coverage_show_detail:
				viewBestCoverageGone();
				if (tvGeneral.getVisibility() == View.VISIBLE) {
					tvGeneralGone();
				}
				if (tvOverlap.getVisibility() == View.VISIBLE) {
					tvOverlapGone();
				}
				if (tvCustom.getVisibility() == View.VISIBLE) {
					tvCustomGone();
				}
				break;
			case R.id.btn_overlap_show_detail:
				viewOverlapGone();
				if (tvGeneral.getVisibility() == View.VISIBLE) {
					tvGeneralGone();
				}
				if (tvBestCoverage.getVisibility() == View.VISIBLE) {
					tvBestCoverageGone();
				}
				if (tvCustom.getVisibility() == View.VISIBLE) {
					tvCustomGone();
				}
				break;
			case R.id.btn_custom_show_detail:
				viewCustomGone();
				if (tvGeneral.getVisibility() == View.VISIBLE) {
					tvGeneralGone();
				}
				if (tvBestCoverage.getVisibility() == View.VISIBLE) {
					tvBestCoverageGone();
				}
				if (tvOverlap.getVisibility() == View.VISIBLE) {
					tvOverlapGone();
				}
				break;
			case R.id.tv_general:
				tvGeneralGone();
				break;
			case R.id.tv_best_coverage:
				tvBestCoverageGone();
				break;
			case R.id.tv_overlap:
				tvOverlapGone();
				break;
			case R.id.tv_custom:
				tvCustomGone();
				break;
			default:
				break;
		}
	}
	private void openUrl(){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("https://www.dynamsoft.com/Products/barcode-scanner-sdk-android.aspx ");
		intent.setData(content_url);
		startActivity(intent);
	}
	private void viewGeneralGone() {
		TransitionManager.beginDelayedTransition(viewGeneral, SLIDE_LEFT);
		TransitionManager.beginDelayedTransition(tvGeneral, SLIDE_RIGHT);
		viewGeneral.setVisibility(View.GONE);
		tvGeneral.setVisibility(View.VISIBLE);
	}
	private void viewBestCoverageGone() {
		TransitionManager.beginDelayedTransition(viewBestCoverage, SLIDE_LEFT);
		TransitionManager.beginDelayedTransition(tvBestCoverage, SLIDE_RIGHT);
		tvBestCoverage.setVisibility(View.VISIBLE);
		viewBestCoverage.setVisibility(View.GONE);
	}
	private void viewOverlapGone() {
		TransitionManager.beginDelayedTransition(viewOverlap, SLIDE_LEFT);
		TransitionManager.beginDelayedTransition(tvOverlap, SLIDE_RIGHT);
		tvOverlap.setVisibility(View.VISIBLE);
		viewOverlap.setVisibility(View.GONE);
	}
	private void viewCustomGone() {
		TransitionManager.beginDelayedTransition(viewCustom, SLIDE_LEFT);
		TransitionManager.beginDelayedTransition(tvCustom, SLIDE_RIGHT);
		viewCustom.setVisibility(View.GONE);
		tvCustom.setVisibility(View.VISIBLE);
	}
	private void tvGeneralGone() {
		TransitionManager.beginDelayedTransition(tvGeneral, SLIDE_RIGHT);
		TransitionManager.beginDelayedTransition(viewGeneral, SLIDE_LEFT);
		tvGeneral.setVisibility(View.GONE);
		viewGeneral.setVisibility(View.VISIBLE);
	}
	private void tvBestCoverageGone() {
		TransitionManager.beginDelayedTransition(tvBestCoverage, SLIDE_RIGHT);
		TransitionManager.beginDelayedTransition(viewBestCoverage, SLIDE_LEFT);
		tvBestCoverage.setVisibility(View.GONE);
		viewBestCoverage.setVisibility(View.VISIBLE);
	}
	private void tvOverlapGone() {
		TransitionManager.beginDelayedTransition(tvOverlap, SLIDE_RIGHT);
		TransitionManager.beginDelayedTransition(viewOverlap, SLIDE_LEFT);
		tvOverlap.setVisibility(View.GONE);
		viewOverlap.setVisibility(View.VISIBLE);
	}
	private void tvCustomGone() {
		TransitionManager.beginDelayedTransition(tvCustom, SLIDE_RIGHT);
		TransitionManager.beginDelayedTransition(viewCustom, SLIDE_LEFT);
		tvCustom.setVisibility(View.GONE);
		viewCustom.setVisibility(View.VISIBLE);
	}
}


