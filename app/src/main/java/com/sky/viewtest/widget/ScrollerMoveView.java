package com.sky.viewtest.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by yuetu-develop on 2017/11/15.
 */

public class ScrollerMoveView extends LinearLayout {

    private Scroller mScroller;

    public ScrollerMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public void smoothScrollTo(int destX, int destY){
        // getScrollX获取View在屏幕上从初始点偏移的值
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        // step2：开始滑动 1000ms 平滑滑向destX
        mScroller.startScroll(scrollX,0,deltaX,0,1000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        // step3：
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }
}
