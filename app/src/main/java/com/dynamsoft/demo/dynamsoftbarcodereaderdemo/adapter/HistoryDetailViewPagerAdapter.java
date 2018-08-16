package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bumptech.glide.Glide;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.DBRImage;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.HistoryItemBean;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectCoordinate;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.util.DBRUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.util.BGABrowserPhotoViewAttacher;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;


public class HistoryDetailViewPagerAdapter extends PagerAdapter {
	private List<DBRImage> picPathList;
	private Context context;

	public HistoryDetailViewPagerAdapter(Context context, List<DBRImage> picPathList) {
		this.picPathList = picPathList;
		this.context = context;
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, int position) {
		BGAImageView imageView = new BGAImageView(context);
		drawRectOnImg(imageView, position);
		container.addView(imageView);
		final BGABrowserPhotoViewAttacher photoViewAttacher = new BGABrowserPhotoViewAttacher(imageView);
		//imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		imageView.setDelegate(new BGAImageView.Delegate() {
			@Override
			public void onDrawableChanged(Drawable drawable) {
				if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth()
						&& drawable.getIntrinsicHeight() > BGAPhotoPickerUtil.getScreenHeight()) {
					photoViewAttacher.setIsSetTopCrop(true);
					photoViewAttacher.setUpdateBaseMatrix();
				} else {
					photoViewAttacher.update();
				}
			}
		});
		return imageView;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return picPathList == null ? 0 : picPathList.size();
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == object;
	}

	private void drawRectOnImg(ImageView imageView, int position) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(9f);
		paint.setColor(context.getResources().getColor(R.color.aboutOK));
		paint.setAntiAlias(true);
		Path path = new Path();
/*		HistoryItemBean historyItemBean;
		historyItemBean = picPathList.get(position);*/
		DBRImage dbrImage = picPathList.get(position);
		if (dbrImage != null) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			if (dbrImage.getScaleValue() != -1){
				opts.inSampleSize = dbrImage.getScaleValue();
			}
			Bitmap oriBitmap = BitmapFactory.decodeFile(dbrImage.getCodeImgPath(),opts);
			if (oriBitmap == null) {
				Toast.makeText(context, "The image dosen't exist.", Toast.LENGTH_SHORT).show();
				return;
			}
			RectCoordinate rectCoordinate = null;
			try {
				rectCoordinate = LoganSquare.parse(dbrImage.getRectCoord(), RectCoordinate.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bitmap rectBitmap = oriBitmap.copy(Bitmap.Config.ARGB_8888, true);
			if (rectCoordinate != null) {
				List<RectPoint[]> pointList = rectCoordinate.getRectCoord();
				Canvas canvas = new Canvas(rectBitmap);
				for (int i = 0; i < pointList.size(); i++) {
					path.reset();
					path.moveTo(pointList.get(i)[0].x, pointList.get(i)[0].y);
					path.lineTo(pointList.get(i)[1].x, pointList.get(i)[1].y);
					path.lineTo(pointList.get(i)[2].x, pointList.get(i)[2].y);
					path.lineTo(pointList.get(i)[3].x, pointList.get(i)[3].y);
					path.close();
					canvas.drawPath(path, paint);
				}
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			rectBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
			byte[] bytes = baos.toByteArray();
			if (dbrImage.getScaleValue()==4){
				imageView.setRotation(90);
			}else if (dbrImage.getScaleValue()==2){
				imageView.setRotation(DBRUtil.readPictureDegree(dbrImage.getCodeImgPath()));
			}
			Glide.with(context)
					.load(bytes)
					.into(imageView);
		}
	}
}
