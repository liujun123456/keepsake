package com.shunlai.mine.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.shunlai.ui.srecyclerview.ScreenUtils;

/**
 * @author Liu
 * @Date 2021/6/18
 * @mobile 18711832023
 */
public class MineDetailBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean isBeingDragged;

    private int lastMotionY;

    private LinearLayout HeaderView;

    private View headContentView;

    private View dependencyView;

    private View titleView;

    private Boolean isInit=false;

    private Boolean isUseEvent=false;

    private VelocityTracker velocityTracker;   //速度追踪类

    private boolean isInTop=false;

    private Runnable flingRunnable;

    private int MountingHeight=0;

    private int pullOffSet=0;

    OverScroller scroller;


    public MineDetailBehavior() {

    }

    public MineDetailBehavior(Context context, AttributeSet attrs) {
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
            isBeingDragged=(parent.isPointInChildBounds(child, x, y)||parent.isPointInChildBounds(titleView,x,y))&&isInTop&&isInit;
            if (isBeingDragged){
                lastMotionY = y;
                if (velocityTracker==null){
                    velocityTracker=VelocityTracker.obtain();
                }
            }
        }else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE){
            int y = (int) ev.getY();
            int yDiff = Math.abs(y - lastMotionY);
            if (isBeingDragged&&yDiff>8){
                lastMotionY=y;
                return true;
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
                dealDy2(dy,1);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:{
                break;
            }
            case MotionEvent.ACTION_UP:{
                if (pullOffSet>0){
                    mListener.onPullRelease(pullOffSet);
                    pullOffSet=0;
                    titleView.setTranslationY(0);
                    HeaderView.setTranslationY(titleView.getHeight());
                    dependencyView.setTranslationY(HeaderView.getHeight()+titleView.getHeight());
                    break;
                }
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
        if (child instanceof LinearLayout){
            HeaderView=(LinearLayout) child;
            headContentView=HeaderView.getChildAt(0);
            if (headContentView instanceof LinearLayout){
                MountingHeight=((LinearLayout) headContentView).getChildAt(((LinearLayout) headContentView).getChildCount()-1).getHeight();
            }
        }
        if (dependency instanceof ViewPager){
            dependencyView=dependency;
        }
        if (dependency instanceof LinearLayout){
            titleView=dependency;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (isInit){
            return super.onLayoutChild(parent, child, layoutDirection);
        }
        CoordinatorLayout.LayoutParams layoutParams=(CoordinatorLayout.LayoutParams)dependencyView.getLayoutParams();
        layoutParams.height=parent.getHeight()-MountingHeight;
        dependencyView.setLayoutParams(layoutParams);
        dependencyView.setTranslationY(HeaderView.getHeight()+titleView.getHeight());

        HeaderView.setTranslationY(titleView.getHeight());

        if (dependencyView!=null&&dependencyView.getHeight()>0&&
            HeaderView!=null&&HeaderView.getHeight()>0&&
            titleView!=null&&titleView.getHeight()>0){
            doInit();
        }
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
                dealDy2(dy,3);
            }else {
                if (dy>0){   //viewpager+recyclerview的情况下，可能会出现tab切换，但是头部只出来了一半，这个时候也需要消费事件
                    dealDy2(dy,3);
                }
                isInTop=false;
            }
            if (isUseEvent){
                consumed[1]=dy;
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

    private void fling(int velocityY, CoordinatorLayout parent){

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
            MountingHeight,
            HeaderView.getHeight()+titleView.getHeight()); // y

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
                dealDy2(lastY-scroller.getCurrY(),2);
                lastY=scroller.getCurrY();
                ViewCompat.postOnAnimation(mLayout, this);
            }
        }
    }


    private void dealDy2(int dy,int type){
        if (dy>=0){  //上推
            Float headerY=HeaderView.getTranslationY();
            Float realHeaderY=headerY-dy;
            if (realHeaderY<0){   //headerView已触顶
                if (headerY>0){
                    titleView.setTranslationY(-titleView.getHeight());
                    HeaderView.setTranslationY(0);
                }
                Float dependY=dependencyView.getTranslationY();
                if (dependY-dy<=MountingHeight){
                    dependencyView.setTranslationY(MountingHeight);
                    headContentView.setTranslationY(-headContentView.getHeight()+MountingHeight);
                    isUseEvent=false;
                }else {
                    headContentView.setTranslationY(headContentView.getTranslationY()-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                    isUseEvent=true;
                }
            }else {
                if (type==1&&pullOffSet>0){
                    pullOffSet-=dy;
                    mListener.onPull(pullOffSet);
                }
                HeaderView.setTranslationY(headerY-dy);
                dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                titleView.setTranslationY(titleView.getTranslationY()-dy);
                isUseEvent=true;
            }

        }else {  //下滑
            if (headContentView.getTranslationY()<0){  //如果内容可滑动
                Float dependY=headContentView.getTranslationY();
                if (dependY-dy>=0){
                    headContentView.setTranslationY(0);
                    dependencyView.setTranslationY(HeaderView.getHeight());
                }else {
                    headContentView.setTranslationY(dependY-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                }
            }else {
                Float dependY=HeaderView.getTranslationY();
                if (dependY-dy>=titleView.getHeight()){
                    if (type==1){
                        pullOffSet-=dy;
                        mListener.onPull(pullOffSet);
                        titleView.setTranslationY(titleView.getTranslationY()-dy);
                        HeaderView.setTranslationY(HeaderView.getTranslationY()-dy);
                        dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                    }else {
                        titleView.setTranslationY(0);
                        HeaderView.setTranslationY(titleView.getHeight());
                        dependencyView.setTranslationY(HeaderView.getHeight()+titleView.getHeight());
                    }
                }else {
                    titleView.setTranslationY(titleView.getTranslationY()-dy);
                    HeaderView.setTranslationY(HeaderView.getTranslationY()-dy);
                    dependencyView.setTranslationY(dependencyView.getTranslationY()-dy);
                }
            }
            isUseEvent=true;
        }

        if (HeaderView.getTranslationY()>0){
            mListener.onTop(false);
        }else {
            mListener.onTop(true);
        }
    }

    public TopListener mListener;

    public void setTopListener(TopListener listener){
        mListener=listener;
    }

    public interface TopListener{
        void onTop(Boolean bol);
        void onPull(int distance);
        void onPullRelease(int distance);
    }

}
