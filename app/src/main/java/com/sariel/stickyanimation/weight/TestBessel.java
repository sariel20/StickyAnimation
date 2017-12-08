package com.sariel.stickyanimation.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LiangCheng on 2017/12/7.
 */

public class TestBessel extends View {
    public TestBessel(Context context) {
        super(context);
        init();
    }

    public TestBessel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestBessel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TestBessel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //画笔
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();

    private void init() {
        //在方法中局部变量响应速度大于成员变量

        Paint paint = mPaint;
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//抗抖动
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);

        //一阶贝塞尔曲线
        Path path = mPath;
        path.moveTo(100, 100);//点位置
        path.lineTo(400, 400);//线

        //二阶贝塞尔曲线
        path.quadTo(600, 100, 800, 400);
        //相对与上次结束的点
//        path.rQuadTo(200, -300, 400, 0);

        path.moveTo(400, 800);
        //三阶贝塞尔曲线
//        path.cubicTo(500, 600, 700, 1200, 800, 800);
        path.rCubicTo(100, -200, 300, 400, 400, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPoint(600, 100, mPaint);
        canvas.drawPoint(500, 600, mPaint);
        canvas.drawPoint(700, 1200, mPaint);
    }
}
