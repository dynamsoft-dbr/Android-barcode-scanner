package com.dynamsoft.demo.dynamsoftbarcodereaderdemo.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.R;
import com.dynamsoft.demo.dynamsoftbarcodereaderdemo.bean.RectPoint;

import java.util.ArrayList;

/**
 * Created by Martin on 7/16/2018.
 */

public class CanvasImageView extends ImageView{
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
    private boolean depOnWidt;
    public CanvasImageView(Context context, int imageWidth, int imageHeight){
        super(context);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(9f);
        paint.setColor(getResources().getColor(R.color.aboutOK));
        paint.setAntiAlias(true);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        srcWidth = imageWidth;
        srcHeight = imageHeight;
    }
    public CanvasImageView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(9f);
        paint.setColor(getResources().getColor(R.color.aboutOK));
        paint.setAntiAlias(true);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
    }

    public CanvasImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float scale = getScale(viewWidth, viewHeight, srcWidth, srcHeight);
        if (rectCoord != null && rectCoord.size() > 0) {
            if (depOnWidt) {
                float move = (viewHeight - scale * srcHeight) / 2;
                for (int i = 0; i < rectCoord.size(); i++) {
                    path.reset();
                    path.moveTo(rectCoord.get(i)[0].x * scale + paddingLeft, rectCoord.get(i)[0].y * scale + move + paddingTop);
                    path.lineTo(rectCoord.get(i)[1].x * scale + paddingLeft, rectCoord.get(i)[1].y * scale + move + paddingTop);
                    path.lineTo(rectCoord.get(i)[2].x * scale + paddingLeft, rectCoord.get(i)[2].y * scale + move + paddingTop);
                    path.lineTo(rectCoord.get(i)[3].x * scale + paddingLeft, rectCoord.get(i)[3].y * scale + move + paddingTop);
                    path.close();
                    canvas.drawPath(path, paint);
                }
            }
            else{
                float move = (viewWidth - scale * srcWidth) / 2;
                for (int i = 0; i < rectCoord.size(); i++) {
                    path.reset();
                    path.moveTo(rectCoord.get(i)[0].x * scale + move + paddingLeft, rectCoord.get(i)[0].y * scale + paddingTop);
                    path.lineTo(rectCoord.get(i)[1].x * scale + move + paddingLeft, rectCoord.get(i)[1].y * scale + paddingTop);
                    path.lineTo(rectCoord.get(i)[2].x * scale + move + paddingLeft, rectCoord.get(i)[2].y * scale + paddingTop);
                    path.lineTo(rectCoord.get(i)[3].x * scale + move + paddingLeft, rectCoord.get(i)[3].y * scale + paddingTop);
                    path.close();
                    canvas.drawPath(path, paint);
                }
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
    }

    public void setCanvasDegree(int degree) {
        this.degree = degree;
    }

    private float getScale(int viewWidth, int viewHeight, int imageWidth, int imageHeght){
        float scale;
        if (((float) viewWidth / (float) imageWidth) > ((float) viewHeight / (float) imageHeght)) {
            scale = (float) (viewHeight) / (float) imageHeght;
            depOnWidt = false;
        } else {
            scale = (float) viewWidth / (float) imageWidth;
            depOnWidt = true;
        }
        return scale;
    }
}
