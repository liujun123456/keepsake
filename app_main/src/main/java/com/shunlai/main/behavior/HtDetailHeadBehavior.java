package com.shunlai.main.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shunlai.im.utils.ScreenUtil;
import com.shunlai.ui.DisableScrollScrollView;
import com.shunlai.ui.srecyclerview.ScreenUtils;

/**
 * @author Liu
 * @Date 2021/6/18
 * @mobile 18711832023
 */
public class HtDetailHeadBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean isBeingDragged;

    private int lastMotionY;

    private DisableScrollScrollView HeaderView;

    private View headContentView;

    private View dependencyView;

    private RelativeLayout titleView;

    private Boolean isInit=false;

    private Boolean isUseEvent=false;

    private VelocityTracker velocityTracker;   //速度追踪类

    private boolean isInTop=false;

    private Runnable flingRunnable;

    OverScroller scroller;


    public HtDetailHeadBehavior() {

    }

    public HtDetailHeadBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void doInit(){
        isInit=true;
        isInTop=true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN){
            if (flingRunnable != null) {
                parent.removeCallbacks(flingRunnable);
                flingRunnable = null;
            }
            isUseEvent = false;
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            isBeingDragged=parent.isPointInChildBounds(child, x, y)&&isInTop&&isInit;
            if (isBeingDragged){
                lastMotionY = y;
                if (velocityTracker==null){
                    velocityTracker=VelocityTracker.obtain();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        boolean consumeUp = false;
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_MOVE:{
                final int y = (int) ev.getY();
                int dy = lastMotionY - y;
                lastMotionY = y;
                dealDy2(dy);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                break;
            }
            case MotionEvent.ACTION_UP:{
                if (velocityTracker != null) {
                    consumeUp = true;
                    velocityTracker.addMovement(ev);
                    velocityTracker.computeCurrentVelocity(1000);
                    float yvel = velocityTracker.getYVelocity();
                    fling(Math.round(yvel),parent);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                isBeingDragged = false;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            }
        }
        if (velocityTracker != null) {
            velocityTracker.addMovement(ev);
        }
        return isBeingDragged || consumeUp;
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (child instanceof DisableScrollScrollView){
            HeaderView=(DisableScrollScrollView) child;
            headContentView=HeaderView.getChildAt(0);
        }
        if (dependency instanceof ViewPager){
            dependencyView=dependency;
        }
        if (dependency instanceof RelativeLayout){
            titleView=(RelativeLayout) dependency;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (isInit){
            return super.onLayoutChild(parent, child, layoutDirection);
        }
        CoordinatorLayout.LayoutParams layoutParams=(CoordinatorLayout.LayoutParams)dependencyView.getLayoutParams();
        layoutParams.height=parent.getHeight()-ScreenUtils.dip2px(dependencyView.getContext(),44f);
        dependencyView.setLayoutParams(layoutParams);
        dependencyView.setTranslationY(headContentView.getHeight()+titleView.getHeight());

        HeaderView.setTranslationY(titleView.getHeight());
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (target instanceof RecyclerView){
            RecyclerView list = (RecyclerView) target;
            int pos = ((LinearLayoutManager) list.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (pos==0){
                isInTop=true;
                dealDy2(dy);
            }else {
                isInTop=false;
            }
            if (isUseEvent){
                consumed[1]=dy;
            }else {
                if (HeaderView.getTranslationY()>0){
                    mListener.onTop(false);
                }else {
                    mListener.onTop(true);
                }
            }
        }

    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if (dyConsumed==0&&target instanceof RecyclerView){
            ((RecyclerView) target).stopScroll();
        }
    }


    private void fling(int velocityY,CoordinatorLayout parent){

        if (flingRunnable != null) {
            parent.removeCallbacks(flingRunnable);
            flingRunnable = null;
        }

        if (scroller == null) {
            scroller = new OverScroller(parent.getContext());
        }

        scroller.fling(
            0,
            (int) dependencyView.getTranslationY(), // curr
            0,
            velocityY, // velocity.
            0,
            0, // x
            ScreenUtil.dip2px(dependencyView.getContext(),44f),
            headContentView.getHeight()+titleView.getHeight()); // y

        if (scroller.computeScrollOffset()){
            lastY=(int) dependencyView.getTranslationY();
            flingRunnable = new FlingRunnable(parent);
            ViewCompat.postOnAnimation(parent, flingRunnable);
        }
    }

    volatile int lastY=0;
    class FlingRunnable implements Runnable{
        CoordinatorLayout mLayout;

        FlingRunnable(CoordinatorLayout layout ){
            mLayout=layout;
        }
        @Override
        public void run() {
            if (scroller.computeScrollOffset()){
                dealDy2(lastY-scroller.getCurrY());
                lastY=scroller.getCurrY();
                ViewCompat.postOnAnimation(mLayout, this);
            }
        }
    }


    private void dealDy2(int dy){
        if (HeaderView.getTranslationY()>10){
            mListener.onTop(false);
        }else {
            mListener.onTop(true);
        }
        if (dy>=0){  //上推
            Float headerY=HeaderView.getTranslationY();
            Float realHeaderY=headerY-dy;
            if (realHeaderY<0){   //headerView已触顶
                if (headerY>0){
                    HeaderView.setTranslationY(0);
                }
                Float dependY=dependencyView.getTranslationY();
                if (dependY-dy<=ScreenUtils.dip2px(HeaderView.getContext(),44f)){
                    dependencyView.setTranslationY(ScreenUtils.dip2px(HeaderView.getContext(),44f));
                    headContentView.setTranslationY(-headContentView.getHeight()+ScreenUtils.dip2px(HeaderView.getContext(),44f));
                    isUseEvent=false;
                }else {
                    headContentView.setTranslationY(headContentView.getTranslationY()-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                    isUseEvent=true;
                }
            }else {
                HeaderView.setTranslationY(headerY-dy);
                dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                isUseEvent=true;
            }

        }else {  //下滑
            if (headContentView.getTranslationY()<0){  //如果内容可滑动
                Float dependY=headContentView.getTranslationY();
                if (dependY-dy>=0){
                    headContentView.setTranslationY(0);
                    dependencyView.setTranslationY(headContentView.getHeight());
                }else {
                    headContentView.setTranslationY(dependY-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                }
            }else {
                Float dependY=HeaderView.getTranslationY();
                if (dependY-dy>=titleView.getHeight()){
                    HeaderView.setTranslationY(titleView.getHeight());
                    dependencyView.setTranslationY(headContentView.getHeight()+titleView.getHeight());
                }else {
                    HeaderView.setTranslationY(HeaderView.getTranslationY()-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                }
            }
            isUseEvent=true;
        }
    }

    public TopListener mListener;
    public void setTopListener(TopListener listener){
        mListener=listener;
    }

    public interface TopListener{
        void onTop(Boolean bol);
    }

}
