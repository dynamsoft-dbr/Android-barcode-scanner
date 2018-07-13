package com.dynamsoft.demo.dynamsoftbarcodereaderdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Elemen on 2018/7/13.
 */
public class HistoryItemDetailActivity extends AppCompatActivity {
	@BindView(R.id.iv_history_item_detail)
	ImageView ivHistoryItemDetail;
	private DBRCache mCache;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_item_detail);
		ButterKnife.bind(this);
		mCache = DBRCache.get(this);
		drawRectOnImg();
	}

	private void drawRectOnImg() {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(9f);
		paint.setColor(getResources().getColor(R.color.aboutOK));
		paint.setAntiAlias(true);
		Path path = new Path();
		HistoryItemBean historyItemBean;
		try {
			historyItemBean = LoganSquare.parse(mCache.getAsStringWithFileName(getIntent().getStringExtra("imgdetail_file")),
					HistoryItemBean.class);
			if (historyItemBean != null) {
				Bitmap oriBitmap = BitmapFactory.decodeFile(historyItemBean.getCodeImgPath());
				Bitmap rectBitmap = oriBitmap.copy(Bitmap.Config.ARGB_8888, true);
				Canvas canvas = new Canvas(rectBitmap);
				for (int i = 0; i < historyItemBean.getRectCoord().size(); i++) {
					path.reset();
					path.moveTo(historyItemBean.getRectCoord().get(i)[0].x, historyItemBean.getRectCoord().get(i)[0].y);
					path.lineTo(historyItemBean.getRectCoord().get(i)[1].x, historyItemBean.getRectCoord().get(i)[1].y);
					path.lineTo(historyItemBean.getRectCoord().get(i)[2].x, historyItemBean.getRectCoord().get(i)[2].y);
					path.lineTo(historyItemBean.getRectCoord().get(i)[3].x, historyItemBean.getRectCoord().get(i)[3].y);
					path.close();
					canvas.drawPath(path, paint);
				}

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				rectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] bytes = baos.toByteArray();
				Glide.with(this)
						.load(bytes)
						.into(ivHistoryItemDetail);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
