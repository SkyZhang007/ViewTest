![目录](http://upload-images.jianshu.io/upload_images/6762021-5f10c6e5fe110e7e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###一、View 滑动冲突背景
#### 1.1 View 滑动冲突常见场景
- 场景1 —— 外部和内部横竖滑动交错冲突
- 场景2 —— 外部和内部同向滑动冲突
- 场景3 —— 以上两种情况的嵌套

举个栗子：
- 场景1 —— 横向 ScrollView 嵌套 ListView。类似 ViewPager 和 Fragment，但是 ViewPager 内部处理了这种滑动冲突。
- 场景2 —— 竖向 ScrollView 嵌套 ListView。
- 场景3 —— TabLayout 嵌套多个层级的 ViewPager 以及 ListView。
#### 1.2 View 滑动冲突处理规则
针对以上滑动冲突，处理规则如下：
- 场景1 —— 根据用户手势坐标判断左右或者上下滑动，再分别交给响应的 View 处理；
- 场景2 —— 根据一定的业务逻辑，比如外部 View 没有滑动到底部，则禁止内部 View 滑动；
- 场景3 —— 根据具体的业务逻辑处理。
###二、View 滑动冲突解决方式
#### 2.1 外部拦截法
点击事件先经过父容器处理，如果不需要处理再交给子 View —— 符合点击事件分发机制
伪代码表示这种方式，重写父容器的 onInterceptTouchEvent：
```
 public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            intercepted = false;
            break;
        }
        case MotionEvent.ACTION_MOVE: {
            if(父容器拦截的规则){
                intercepted=true;
            }else{
                intercepted=false;
            }
            break;
        }
        case MotionEvent.ACTION_UP: {
            intercepted = false;
            break;
        }
        default:
            break;
        }
        mLastXIntercept=x;
        mLastYIntercept=y;
        return intercepted;
    }
```
**case MotionEvent.ACTION_DOWN**：必须返回 false，一旦返回 true 拦截事件，后续则不会再传递给子 View；
**case MotionEvent.ACTION_MOVE**：根据相应规则处理事件；
**case MotionEvent.ACTION_UP**：拦截后子 View 无法处理 onClick 事件。一旦父容器拦截事件，即使 onInterceptTouchEvent 返回 false，ACTION_UP 作为最后一个事件也会传递给父容器。
#### 2.2 内部拦截法
父容器不拦截任何事件，子元素需要则处理，不需要则交给父容器处理。重写子元素的 onInterceptTouchEvent 方法来影响父容器拦截事件。
同时父容器的 onInterceptTouchEvent 中拦截除 ACTION_DOWN 事件外的其他事件，这是为了让子元素通过 requestDisallowInterceptTouchEvent(true) 交给父容器处理的事件生效。
```
public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            // 告知父容器不要拦截此事件 
            parent.requestDisallowInterceptTouchEvent(true); 
            break;
        }
        case MotionEvent.ACTION_MOVE: {
            int deltaX=x-mLastXIntercept;
            int deltaY=y=mLastYIntercept;
            if(父容器需要拦截的事件){
            // 告知父容器需要拦截此事件
            parent.requestDisallowInterceptTouchEvent(false); 
            }
            break;
        }
        case MotionEvent.ACTION_UP: {
            intercepted = false;
            break;
        }
        default:
            break;
        }
        mLastXIntercept=x;
        mLastYIntercept=y;
        return super.dispathTouchEvent(event);
    }
```
###三、View 滑动冲突解决实例
#### 3.1 外部拦截法处理横竖交错冲突
（1）创建横向滑动 View 作为父容器
```
public class HorizontalScrollViewEx extends ViewGroup{
    ...
    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }
    ...
}
```

（2）布局文件添加该 View
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.sky.viewtest.widget.HorizontalScrollViewEx
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</LinearLayout>
```

（3）Activity 向父容器添加 ListView 模拟滑动冲突
```
private HorizontalScrollViewEx mListContainer;

@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conllision_view);
    mListContainer = (HorizontalScrollViewEx) findViewById(R.id.container);
    // 添加三个 listView 并添加到 HorizontalScrollViewEx
    for (int i = 0; i < 3; i++) {
        ListView listView = new ListView(this);
        List<String> list =  new ArrayList<>();
        for (int i1 = 0; i1 < 50; i1++) {
            list.add("page" + i + ", name: " + i1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        mListContainer.addView(listView);
    }
}
```

（4）重写父容器 onInterceptTouchEvent 方法，在相应地方拦截和分发事件
```
@Override
public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean intercepted = false;
    int x = (int) ev.getX();
    int y = (int) ev.getY();
    switch (ev.getAction()){
        case MotionEvent.ACTION_DOWN:
            mXDown = ev.getRawX();
            mXLastMove = mXDown;
            // 默认返回 false，因为一旦拦截，则无法传递给子 View
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

            int deltaX = x - mLastXIntercept;
            int deltaY = y - mLastYIntercept;
            // 如果左右滑动幅度大于上下，是左右滑动则拦截事件
            if(Math.abs(deltaX) > Math.abs(deltaY)){
                intercepted = true;
            }else{
                intercepted = false;
            }
            break;
        case MotionEvent.ACTION_UP:
            intercepted = false;
            break;
    }

    mLastXIntercept = x;
    mLastYIntercept = y;

    return intercepted;
}
```
**case MotionEvent.ACTION_DOWN:**默认返回 false，因为一旦返回 true 拦截事件，后面就没子 View 什么事了。
**case MotionEvent.ACTION_MOVE:**这里进行判定，如果左右滑动幅度大于上下，是左右滑动则拦截事件由父容器作处理

（5）重写父容器 onTouchEvent 方法，处理父容器拦截事件后的一系列操作
```
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
```
上文仅列出主要代码，全部代码可阅读参考资料

#### 3.2 内部拦截法处理横竖交错冲突
父容器拦截除 ACTION_DOWN 以外的所有事件，子元素需要拦截事件时，使用 getParent().requestDisallowInterceptTouchEvent(true) 方法来请求父容器不拦截事件。当某些事件需要父容器处理时，使用 getParent().requestDisallowInterceptTouchEvent(false) 

（1）自定义 ListView 作为子 View，重写 dispatchTouchEvent 方法
```
public class MyListView extends ListView {

    private int mLastX;
    private int mLastY;

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 要求父控件不拦截事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                // 如果是左右滑动
                if(Math.abs(deltaX) > Math.abs(deltaY)){
                    // 要求父控件拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }
}
```
**case MotionEvent.ACTION_DOWN:**中使用 requestDisallowInterceptTouchEvent 强行时父容器不拦截事件
**case MotionEvent.ACTION_MOVE:**横向滑动时，再让父容器处理相应事件

（2）复制 HorizontalScrollViewEx 重命名 HorizontalScrollViewEx2，并修改 onInterceptTouchEvent 方法拦截除 ACTION_DOWN 以外的所有事件。
```
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
```
（3）修改 Activity 使用新的 父容器和 ListView
```
mListContainer = (HorizontalScrollViewEx2) findViewById(R.id.container);
...
MyListView listView = new MyListView(this,null);
```

[实例代码地址](https://github.com/SkyZhang007/ViewTest)






参考资料：
>《Android开发艺术探索》
[Android 从0开始自定义控件之 View 的滑动冲突详解（四）](http://blog.csdn.net/airsaid/article/details/53244984)


