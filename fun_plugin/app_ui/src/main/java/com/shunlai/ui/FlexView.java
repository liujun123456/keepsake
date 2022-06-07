package com.shunlai.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author Liu
 * @Date 2020/9/18
 * @mobile 18711832023
 */
public class FlexView extends ViewGroup {

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private int childCount = 0;
    private int childMargin;
    private boolean centerHorizontal;
    private int childMaxHeight = 0;
    private int maxLine;

    public FlexView(Context context) {
        this(context, null);
    }

    public FlexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.FlexView);
        childMargin = (int) typedArray.getDimension(R.styleable.FlexView_margin_width, 10f);
        centerHorizontal = typedArray.getBoolean(R.styleable.FlexView_centerHorizontal, false);
        maxLine = typedArray.getInt(R.styleable.FlexView_maxLine, 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();
        resetParams();
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = measureHeight();
        setMeasuredDimension(mWidth, mHeight);

    }

    int useWidth = 0;
    int currentLine = 1;
    int userHeight = 0;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        resetParams();
        for (int pos = 0; pos < childCount; pos++) {
            if (!layoutChildView(getChildAt(pos), pos)) {
                return;
            }
        }
    }

    private int measureHeight() {
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (useWidth + view.getMeasuredWidth() > mWidth) {
                if (currentLine == maxLine) {
                    return userHeight + childMaxHeight;
                } else {
                    currentLine += 1;
                    useWidth = 0;
                    userHeight += childMaxHeight + childMargin;
                    childMaxHeight = 0;
                }
            }
            if (childMaxHeight < view.getMeasuredHeight()) {
                childMaxHeight = view.getMeasuredHeight();
            }
            useWidth = useWidth + view.getMeasuredWidth() + childMargin;
        }
        return userHeight + childMaxHeight;
    }

    private void resetParams() {
        useWidth = 0;
        currentLine = 1;
        userHeight = 0;
        childMaxHeight = 0;
    }

    private boolean layoutChildView(View view, int position) {
        if (position == 0) {
            if (centerHorizontal) {
                int top = mHeight / 2 - view.getMeasuredHeight() / 2;
                int bottom = mHeight / 2 + view.getMeasuredHeight() / 2;
                view.layout(0, top, useWidth + view.getMeasuredWidth(), bottom);
            } else {
                view.layout(0, 0, useWidth + view.getMeasuredWidth(), view.getMeasuredHeight());
            }
            if (childMaxHeight < view.getMeasuredHeight()) {
                childMaxHeight = view.getMeasuredHeight();
            }
            useWidth = useWidth + view.getMeasuredWidth() + childMargin;
        } else {
            if ((useWidth + view.getMeasuredWidth()) > mWidth) {
                if (currentLine == maxLine) {
                    return false;
                } else {
                    currentLine += 1;
                    useWidth = 0;
                    userHeight += childMaxHeight + childMargin;
                    childMaxHeight = 0;
                }

            }
            if (centerHorizontal) {
                int top = mHeight / 2 - view.getMeasuredHeight() / 2;
                int bottom = mHeight / 2 + view.getMeasuredHeight() / 2;
                view.layout(useWidth, top, useWidth + view.getMeasuredWidth(), bottom);
            } else {
                view.layout(useWidth, userHeight, useWidth + view.getMeasuredWidth(), userHeight + view.getMeasuredHeight());
            }
            if (childMaxHeight < view.getMeasuredHeight()) {
                childMaxHeight = view.getMeasuredHeight();
            }
            useWidth = useWidth + view.getMeasuredWidth() + childMargin;
        }
        return true;
    }
}
