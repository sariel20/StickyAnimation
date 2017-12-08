package com.sariel.stickyanimation.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LiangCheng on 2017/12/8.
 */

public class BezierView extends View {
    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Path mSrcBezier = new Path();
    private Path mBezier = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        Paint paint = mPaint;
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//抗抖动
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);

        //初始化源贝塞尔曲线
        mSrcBezier.cubicTo(200, 700, 500, 1200, 700, 200);

        new Thread() {
            @Override
            public void run() {
                initBezier();
            }
        }.start();

    }

    /**
     * 初始化4阶贝塞尔
     */
    private void initBezier() {
        //(0,0) (300,300)(200,700)(500,500) (700,1200)
        float[] xPaints = new float[]{0, 200, 500, 700, 800, 500, 600, 200};
        float[] yPaints = new float[]{0, 700, 1200, 200, 800, 1300, 600, 1000};


        Path path = mBezier;

        //精度
        int fps = 20000;
        for (int i = 0; i <= fps; i++) {
            //进度
            float pregress = i / (float) fps;
            float x = calculateBezier(pregress, xPaints);
            float y = calculateBezier(pregress, yPaints);
            //使用连接方式，当xy变动足够小的情况下就是平滑曲线
            path.lineTo(x, y);
            //刷新
            postInvalidate();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算某时刻的贝塞尔所处的值(x&y)
     *
     * @param t      时间 0~1
     * @param values 贝塞尔集合
     * @return t时刻贝塞尔所处点
     */
    private float calculateBezier(float t, float... values) {
        final int len = values.length;
        for (int i = len - 1; i > 0; i--) {
            //外层
            for (int j = 0; j < i; j++) {
                //内层 计算
                values[j] = values[j] + (values[j + 1] - values[j]) * t;
            }
        }
        //运算时结果保存在第一位
        return values[0];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mSrcBezier, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mBezier, mPaint);
    }
}
