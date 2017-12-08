package com.sariel.stickyanimation.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.sariel.stickyanimation.R;

/**
 * Created by LiangCheng on 2017/12/5.
 * 自定义下拉
 */

public class TouchPullView extends View {

    //圆 画笔
    private Paint mPaint;
    //圆 半径
    private float mCircleRadius = 50;
    private float mCirclePointX, mCirclePointY;

    //可拖动的高度
    private int mDragHeight = 300;
    //进度值
    private float mProgress;

    //目标宽度
    private int mTargetWidth = 400;
    //贝塞尔路径及画笔
    private Path mPath = new Path();
    private Paint mPathPaint;
    //重心点最终高度 决定控制点y
    private int mTargetGravityHeight = 10;
    //角度变换0~135度
    private int mTangentAngle = 105;
    private DecelerateInterpolator mProgressInterpolator = new DecelerateInterpolator();
    private android.view.animation.Interpolator mTanentAngleInterpolator;

    private Drawable mContent = null;
    private int mContentMargin = 0;

    public TouchPullView(Context context) {
        super(context);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {

        //得到参数
        final Context context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.TouchPullView, 0, 0);
        int color = array.getColor(R.styleable.TouchPullView_pColor, 0x20000000);
        mCircleRadius = array.getDimension(R.styleable.TouchPullView_pRadius, mCircleRadius);
        mDragHeight = array.getDimensionPixelOffset(R.styleable.TouchPullView_pDraHeight, mDragHeight);
        mTangentAngle = array.getInteger(R.styleable.TouchPullView_pTangentAngle, 100);
        mTargetWidth = array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetWidth, mTargetWidth);
        mTargetGravityHeight = array.getDimensionPixelOffset(R.styleable.TouchPullView_TargetGravityHeight, mTargetGravityHeight);

        mContent = array.getDrawable(R.styleable.TouchPullView_pContentDrawable);
        mContentMargin = array.getDimensionPixelOffset(R.styleable.TouchPullView_pContentDrawableMargin, 0);
        //销毁
        array.recycle();

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setStyle(Paint.Style.FILL);//填充
        p.setColor(0xFF303F9F);
        mPaint = p;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setAntiAlias(true);//抗锯齿
        p.setDither(true);//防抖动
        p.setStyle(Paint.Style.FILL);//填充
        p.setColor(0xFF3F51B5);
        mPathPaint = p;

        //切角路径插值器
        mTanentAngleInterpolator = PathInterpolatorCompat.create(
                (mCircleRadius * 2.0f) / mDragHeight,
                90.0f / mTangentAngle
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //进行基础坐标参数系改变
        int count = canvas.save();
        float tranX = (getWidth() -
                getValueByLine(getWidth(), mTargetWidth, mProgress)) / 2;
        canvas.translate(tranX, 0);

        //画贝塞尔曲线
        canvas.drawPath(mPath, mPathPaint);
        //画圆
        canvas.drawCircle(mCirclePointX, mCirclePointY,
                mCircleRadius, mPaint);

        Drawable drawable = mContent;
        if (drawable != null) {
            canvas.save();
            //剪切矩形区域
            canvas.clipRect(drawable.getBounds());
            //绘制Drawable
            drawable.draw(canvas);
            canvas.restore();
        }

        canvas.restoreToCount(count);
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

        int iWidth = (int) (2 * mCircleRadius + getPaddingLeft() + getPaddingRight());
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
        //当高度变化时进行路径更新
        updatePathLayout();
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

    /**
     * 更新路径等相关操作
     */
    private void updatePathLayout() {
        //获取进度
        final float progress = mProgressInterpolator.getInterpolation(mProgress);

        //获取可绘制区域宽高
        final float w = getValueByLine(getWidth(), mTargetWidth, mProgress);
        final float h = getValueByLine(0, mDragHeight, mProgress);

        //X对称轴参数 圆的中心点x坐标
        final float cPointx = w / 2.0f;
        //圆的半径
        final float cRadius = mCircleRadius;
        //圆的中心点Y坐标
        final float cPointy = h - cRadius;
        //控制点结束y的值
        final float endControlY = mTargetGravityHeight;

        //更新圆坐标
        mCirclePointX = cPointx;
        mCirclePointY = cPointy;

        final Path path = mPath;
        //复位
        path.reset();
        path.moveTo(0, 0);

        //左边部分的结束点和控制点
        float lEndPointX, lEndPointY;
        float lControlPointX, lControlPointY;

        //获取当前切线弧度
        float angle = mTangentAngle * mTanentAngleInterpolator.getInterpolation(progress);
        double radian = Math.toRadians(angle);
        float x = (float) (Math.sin(radian) * cRadius);
        float y = (float) (Math.cos(radian) * cRadius);

        lEndPointX = cPointx - x;
        lEndPointY = cPointy + y;
        //控制点的y坐标变化
        lControlPointY = getValueByLine(0, endControlY, progress);
        //控制点与结束点之间的高度
        float tHeight = lEndPointY - lControlPointY;
        //控制点与X坐标距离
        float tWidth = (float) (tHeight / Math.tan(radian));
        lControlPointX = lEndPointX - tWidth;

        //贝塞尔
        path.quadTo(lControlPointX, lControlPointY, lEndPointX, lEndPointY);
        //连接到右边
        path.lineTo(cPointx + (cPointx - lEndPointX), lEndPointY);
        //右侧贝塞尔曲线
        path.quadTo(cPointx + cPointx - lControlPointX, lControlPointY, w, 0);

        //更新内容部分Drawable
        updateContentLayout(cPointx, cPointy, cRadius);
    }

    /**
     * 对内容部分进行测量并进行设置
     *
     * @param cx
     * @param cy
     * @param radius
     */
    private void updateContentLayout(float cx, float cy, float radius) {
        Drawable drawable = mContent;
        if (drawable != null) {
            int margin = mContentMargin;
            int l = (int) (cx - radius + margin);
            int r = (int) (cx + radius - margin);
            int t = (int) (cy - radius + margin);
            int b = (int) (cy + radius - margin);

            drawable.setBounds(l, t, r, b);

        }
    }

    /**
     * 获取当前值
     *
     * @param start    起始
     * @param end      结束
     * @param progress 进度
     * @return 当前进度值
     */
    private float getValueByLine(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    //释放动画
    private ValueAnimator valueAnimator;

    /**
     * 添加释放操作
     */
    public void release() {
        if (valueAnimator == null) {
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress, 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object val = animation.getAnimatedValue();
                    if (val instanceof Float) {
                        setProgress((Float) val);
                    }
                }
            });
            valueAnimator = animator;
        } else {
            valueAnimator.cancel();
            valueAnimator.setFloatValues(mProgress, 0f);
        }
        valueAnimator.start();
    }
}
