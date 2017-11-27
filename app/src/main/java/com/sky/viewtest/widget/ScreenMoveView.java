package com.sky.viewtest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by yuetu-develop on 2017/11/14.
 */

public class ScreenMoveView extends View {

    private int mLastX = 0;
    private int mLastY = 0;

    public ScreenMoveView(Context context) {
        super(context);
    }

    public ScreenMoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()){
            // 手指落下
            case MotionEvent.ACTION_DOWN:
                break;
            // 手指移动
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                // ViewHelper 是 nineoldandroids 提供的动画兼容库，可在github下载
                int translationX = (int) (ViewHelper.getTranslationX(this) + deltaX);
                int translationY = (int) (ViewHelper.getTranslationY(this) + deltaY);
                ViewHelper.setTranslationX(this,translationX);
                ViewHelper.setTranslationY(this,translationY);
                break;
            // 手指抬起
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        // true拦截父类传递
        return true;
    }
}
