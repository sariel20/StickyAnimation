package com.sariel.stickyanimation.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by LiangCheng on 2017/12/5.
 * 自定义下拉
 */

public class TouchPullView extends View {

    //圆 画笔
    private Paint mPaint;
    //圆 半径
    private int mCircleRadius = 150;
    private int mCirclePointX, mCirclePointY;

    //可拖动的高度
    private int mDragHeight = 800;
    //进度值
    private float mProgress;

    public TouchPullView(Context context) {
        super(context);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setStyle(Paint.Style.FILL);//填充
        p.setColor(0xFF000000);
        mPaint = p;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCirclePointX, mCirclePointY,
                mCircleRadius, mPaint);
    }

    /**
     * 当进行测量时触发
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 宽度意图  类型
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int iWidth = 2 * mCircleRadius + getPaddingLeft() + getPaddingRight();
        int iHeight = (int) ((mDragHeight * mProgress + 0.5f) +
                getPaddingTop() + getPaddingBottom());

        int measureWidth, measureHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            //确切的值
            measureWidth = width;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //最多的值
            measureWidth = Math.min(iWidth, width);
        } else {
            //未知
            measureWidth = iWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            //确切的值
            measureHeight = height;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //最多的值
            measureHeight = Math.min(iHeight, height);
        } else {
            //未知
            measureHeight = iHeight;
        }

        //设置测量的高度宽度
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 当大小改变时触发
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 右移相当于/2
        mCirclePointX = getWidth() >> 1;
        mCirclePointY = getHeight() >> 1;
    }


    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        Log.e("======>", "setProgress: " + progress);
        mProgress = progress;
        //请求重新进行测量
        requestLayout();
    }
}
