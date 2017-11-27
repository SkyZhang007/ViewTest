package com.sky.viewtest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by yuetu-develop on 2017/11/24.
 */

public class HorizontalScrollViewEx2 extends ViewGroup{

    private final Scroller mScroller;
    // 代表左右边界
    private int mLeftBorder;
    private int mRightBorder;
    private float mXDown;
    private float mXMove;
    private float mXLastMove;
    // 记录手指坐标信息
    private int mLastXIntercept;
    private int mLastYIntercept;

    public HorizontalScrollViewEx2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            // 为每一个子View测量大小
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                // 为每一个子View重新布局
                childAt.layout(i * childAt.getMeasuredWidth(), 0
                        , (i + 1) * childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
            // 初始化左右边界值
            mLeftBorder = getChildAt(0).getLeft();
            mRightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mXLastMove = mXDown;

                intercepted = false;
                // 如果滑动没有完成，就继续由父控件处理
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                mXLastMove = mXMove;

                intercepted = true;
                break;
            case MotionEvent.ACTION_UP:
                intercepted = true;
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }

    // 如果父 View 拦截事件则自己进行处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
                if(getScrollX() + scrolledX < mLeftBorder){
                    scrollTo(mLeftBorder, 0);
                    return true;
                }else if(getScrollX() + getWidth() + scrolledX > mRightBorder){
                    scrollTo(mRightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy(scrolledX, 0);
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
                int dx = targetIndex * getWidth() - getScrollX();
                mScroller.startScroll(getScrollX(), 0 , dx, 0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
