package com.learn.customeview;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by dong on 2017/3/4 0004.
 */

public class SearchView extends View {
    private static final String TAG = "SearchView";
    private Path path_search;
    private Path path_circle;
    private Matrix mMatrix;
    private PathMeasure mPathMeasure;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private ValueAnimator mStartAnimator;
    private ValueAnimator mSearchAnimator;
    private ValueAnimator mEndAnimator;
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private float mAnimatorValue = 0;
    private State mCurrentState;
    private Handler mAnimatorHandler;

    public static enum State {
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }


    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mWidth = 200;
        mHeight = 200;
        initPaint();
        initPath();
        initListener();
        initAnimator();
        initHandler();
        mCurrentState = State.STARTING;
        mStartAnimator.start();
    }


    private void initPath() {
        mPathMeasure = new PathMeasure();
        path_search = new Path();
        float serchCircleR = (mWidth > mHeight ? mHeight : mWidth) * 0.2f;
        float outCircleR = serchCircleR + serchCircleR * 2f;
        //path_search.addCircle(0 ,0, 50, Path.Direction.CW);
        //path_search.addArc(new RectF(-50,-50,50,50), 45, 359.9f);
        path_search.addArc(new RectF(-serchCircleR,-serchCircleR,serchCircleR,serchCircleR), 45, 359.9f);
        //path_search.lineTo(100, 100);
        path_circle = new Path();
        //path_circle.addCircle(0, 0, 100, Path.Direction.CW);
        //path_circle.addArc(new RectF(-100,-100,100,100), 45, -359.9f);
        path_circle.addArc(new RectF(-outCircleR,-outCircleR,outCircleR,outCircleR), 45, -359.9f);
        float[] pos = new float[2];
        float[] tan = new float[2];
        mPathMeasure.setPath(path_circle, false);
        mPathMeasure.getPosTan(0, pos, tan);
        path_search.lineTo(pos[0], pos[1]);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initListener() {
        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorHandler.sendEmptyMessage(0);
                /*mCurrentState = State.SEARCHING;
                mSearchAnimator.start();*/

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: mAnimatorValue = " + mAnimatorValue);
                invalidate();
            }
        };
    }

    private void initAnimator() {
        mStartAnimator = ValueAnimator.ofFloat(0 ,1).setDuration(2000);
        mSearchAnimator = ValueAnimator.ofFloat(0 ,1).setDuration(2000);
        mEndAnimator = ValueAnimator.ofFloat(0 ,1).setDuration(2000);

        mSearchAnimator.setRepeatCount(3);

        mStartAnimator.addListener(mAnimatorListener);
        mSearchAnimator.addListener(mAnimatorListener);
        mEndAnimator.addListener(mAnimatorListener);

        mStartAnimator.addUpdateListener(mAnimatorUpdateListener);
        mSearchAnimator.addUpdateListener(mAnimatorUpdateListener);
        mEndAnimator.addUpdateListener(mAnimatorUpdateListener);
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case NONE:
                        break;
                    case STARTING:
                        //mStartAnimator.removeAllListeners();
                        mCurrentState = State.SEARCHING;
                        mSearchAnimator.start();
                        break;
                    case SEARCHING:
                        //mSearchAnimator.removeAllListeners();
                        mCurrentState = State.ENDING;
                        mEndAnimator.start();
                        break;
                    case ENDING:
                        //mEndAnimator.removeAllListeners();
                        mCurrentState = State.STARTING;
                        mStartAnimator.start();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = 200;
        mHeight = 200;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearchView(canvas);
        /*canvas.drawPath(path_search, mPaint);
        canvas.drawPath(path_circle, mPaint);*/

    }

    private void drawSearchView(Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);
        switch (mCurrentState) {
            case NONE:
                canvas.drawPath(path_search, mPaint);
                break;
            case STARTING:
                Path dst0 = new Path();
                mPathMeasure.setPath(path_search, false);
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst0, true);
                canvas.drawPath(dst0, mPaint);
                break;
            case SEARCHING:
                Path dst1 = new Path();
                mPathMeasure.setPath(path_circle, false);
                float stopPos = mPathMeasure.getLength() * mAnimatorValue;
                float startPos = (float) (stopPos - (0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f);
                mPathMeasure.getSegment(startPos, stopPos, dst1, true);
                canvas.drawPath(dst1, mPaint);
                break;
            case ENDING:
                Path dst2 = new Path();
                mPathMeasure.setPath(path_search, false);
                mPathMeasure.getSegment(mPathMeasure.getLength() * (1 - mAnimatorValue), mPathMeasure.getLength(), dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            default:
                break;
        }
    }
}
