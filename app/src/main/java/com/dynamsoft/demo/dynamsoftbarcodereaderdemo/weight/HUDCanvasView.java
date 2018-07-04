package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dynamsoft.barcode.jni.Point;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;

/**
 * Draw an array of shapes on a canvas
 */
public class HUDCanvasView extends View {
	int paddingLeft;
	int paddingTop;
	int paddingRight;
	int paddingBottom;
	private Point[] points = null;
	private Path path = new Path();
	private Paint paint;
	private int degree;
	private boolean canDrawBox = true;

	public HUDCanvasView(Context context) {
		super(context);
	}

	public HUDCanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(9f);
		paint.setColor(getResources().getColor(R.color.aboutOK));
		paint.setAntiAlias(true);

		paddingLeft = getPaddingLeft();
		paddingTop = getPaddingTop();
		paddingRight = getPaddingRight();
		paddingBottom = getPaddingBottom();

		Log.d("hud", "padding info : " + paddingLeft + " * " + paddingTop);
	}

	public HUDCanvasView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (points != null && points.length > 0 && canDrawBox) {
			//canvas.save();
			//canvas.rotate(degree,getWidth()/2,getHeight()/2);
			path.reset();
			path.moveTo(points[0].x + paddingLeft, points[0].y + paddingTop);
			path.lineTo(points[1].x + paddingLeft, points[1].y + paddingTop);
			path.lineTo(points[2].x + paddingLeft, points[2].y+ paddingTop);
			path.lineTo(points[3].x + paddingLeft, points[3].y + paddingTop);
			path.close();
			canvas.drawPath(path, paint);
			//canvas.restore();
		}
	}

	public void setBoundaryPoints(Point[] points) {
		this.points = points;
	}

	public void setBoundaryColor(String color) {
		paint.setColor(Color.parseColor(color));
	}

	public void setBoundaryThickness(int thickness) {
		if (thickness == 0) {
			canDrawBox = false;
		} else {
			canDrawBox = true;
			paint.setStrokeWidth(thickness);
		}
	}

	public void clear() {
		points = null;
	}

	public void setCanvasDegree(int degree) {
		this.degree = degree;
	}
}
