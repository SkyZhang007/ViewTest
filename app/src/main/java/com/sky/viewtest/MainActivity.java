package com.sky.viewtest;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sky.viewtest.widget.ScrollerMoveView;

public class MainActivity extends AppCompatActivity {

    private ScrollerMoveView scrollerMoveView;
    private Button mButton;

    private static final int MESSAGE_SCROLL_TO = 1;
    private static final int FRAME_COUNT = 30;
    private static final int DELAYED_TIME = 33;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollerMoveView = (ScrollerMoveView) findViewById(R.id.smv);
        mButton = (Button) findViewById(R.id.btn_test);

    }

    public void scrollerMove(View view) {
        scrollerMoveView.smoothScrollTo(-500, 200);
//        scrollerMoveView.beginScroll();
    }

    public void animMove(View view) {
        final int startX = 0;
        final int deltaX = 100;
        ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mButton.scrollTo(startX + (int) (deltaX * fraction), 0);
            }
        });
        animator.start();
    }

    public void postMove(View view) {
        mHandler.sendEmptyMessage(MESSAGE_SCROLL_TO);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SCROLL_TO:
                    mCount++;
                    if (mCount <= FRAME_COUNT) {
                        float fraction = mCount / (float) FRAME_COUNT;
                        int scrollx = (int) (fraction * 100);
                        mButton.scrollTo(scrollx, 0);
                        mHandler.sendEmptyMessageDelayed(MESSAGE_SCROLL_TO, DELAYED_TIME);
                    }
                    break;
            }
        }
    };

}
