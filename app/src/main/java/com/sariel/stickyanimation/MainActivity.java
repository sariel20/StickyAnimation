package com.sariel.stickyanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.sariel.stickyanimation.weight.TouchPullView;

public class MainActivity extends AppCompatActivity {

    //4 记录y坐标  mTouchMoveStartY
    private float mTouchMoveStartY = 0;
    //5 记录y最大拉动距离
    private static final float TOUCH_MOVE_MAX_Y = 600;

    private TouchPullView mTouchPull;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTouchPull = (TouchPullView) findViewById(R.id.touchPull);

        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //1得到意图  event.getActionMasked()
                int action = motionEvent.getActionMasked();
                //2判断意图 按下，移动
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY = motionEvent.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float y = motionEvent.getY();
                        if (y >= mTouchMoveStartY) {
                            float moveSize = y - mTouchMoveStartY;
                            float progress = moveSize >= TOUCH_MOVE_MAX_Y
                                    ? 1 : moveSize / TOUCH_MOVE_MAX_Y;
                            mTouchPull.setProgress(progress);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        mTouchPull.release();
                        return true;
                    default:
                        break;
                }

                return false;
            }
        });
    }
}
