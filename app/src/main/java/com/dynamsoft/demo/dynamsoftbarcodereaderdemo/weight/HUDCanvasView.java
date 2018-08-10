package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dynamsoft.barcode.Point;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;

import java.util.ArrayList;

/**
 * Draw an array of shapes on a canvas
 */
public class HUDCanvasView extends View {
	int paddingLeft;
	int paddingTop;
	int paddingRight;
	int paddingBottom;
	private ArrayList<RectPoint[]> rectCoord = null;
	private Path path = new Path();
	private Paint paint;
	private int degree;
	private float previewScale;
	private int srcWidth, srcHeight;

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
		if (rectCoord != null && rectCoord.size() > 0) {
			for (int i = 0; i < rectCoord.size(); i++) {
				path.reset();
				path.moveTo(rectCoord.get(i)[0].x + paddingLeft, rectCoord.get(i)[0].y + paddingTop);
				path.lineTo(rectCoord.get(i)[1].x + paddingLeft, rectCoord.get(i)[1].y + paddingTop);
				path.lineTo(rectCoord.get(i)[2].x + paddingLeft, rectCoord.get(i)[2].y + paddingTop);
				path.lineTo(rectCoord.get(i)[3].x + paddingLeft, rectCoord.get(i)[3].y + paddingTop);
				path.close();
				canvas.drawPath(path, paint);
			}
		}
	}

	public void setBoundaryPoints(ArrayList<RectPoint[]> rectCoord) {
		this.rectCoord = rectCoord;
	}

	public void setBoundaryColor(String color) {
		paint.setColor(Color.parseColor(color));
	}

	public void setBoundaryThickness(int thickness) {
		paint.setStrokeWidth(thickness);
	}

	public void clear() {
		rectCoord = null;
		invalidate();
	}

	public void setCanvasDegree(int degree) {
		this.degree = degree;
	}
}
