package com.learn.customeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by dongjiangpeng on 2017/3/3 0003.
 */

public class PathView extends View{
    private static final String TAG = "PathView";
    private Paint mPaint;
    private float mCurrentValue = 0;
    private int mWidth;
    private int mHeight;
    private float[] mPos;
    private float[] mTan;
    private Matrix mMatrix;
    private Bitmap mBitmap;

    public PathView(Context context) {
        super(context);
        init();
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPos = new float[2];
        mTan = new float[2];
        mMatrix = new Matrix();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow, options);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCurrentValue += 0.005;
        if (mCurrentValue >= 1) {
            mCurrentValue = 0;
        }
        canvas.translate(mWidth / 2, mHeight /2);
        Path path = new Path();
        path.addCircle(0, 0, 200, Path.Direction.CW);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength() * mCurrentValue, mPos, mTan);
        float degree = (float) (Math.atan2(mTan[1], mTan[0]) * 180 / Math.PI);
        mMatrix.reset();
        //mMatrix.preTranslate(-mBitmap.getWidth(), -mBitmap.getHeight()/2);
        /*mMatrix.preRotate(degree, mBitmap.getWidth()/2 ,mBitmap.getHeight()/2);
        mMatrix.preTranslate(mPos[0] - mBitmap.getWidth()/2, mPos[1] - mBitmap.getHeight());*/
        mMatrix.postRotate(degree, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);   // 旋转图片
        mMatrix.postTranslate(mPos[0] - mBitmap.getWidth() / 2, mPos[1] - mBitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合

        canvas.drawLines(new float[]{-mWidth/2,0,mWidth/2,0,0,-mHeight/2,0,mHeight/2}, mPaint);
        canvas.drawPath(path, mPaint);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        Log.d(TAG, "onDraw: degree = " + degree + ",");
        invalidate();
        /*canvas.translate(mWidth / 2, mHeight /2);
        canvas.drawLines(new float[]{-mWidth/2,0,mWidth/2,0,0,-mHeight/2,0,mHeight/2}, mPaint);
        canvas.drawCircle(0,0,200,mPaint);
        mMatrix.preTranslate(200 - mBitmap.getWidth(), 0 - mBitmap.getHeight()/2);
        canvas.drawBitmap(mBitmap,mMatrix,mPaint);*/
    }
}
