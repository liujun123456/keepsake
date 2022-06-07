package com.shunlai.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 解决ScrollView嵌套RecyclerView显示不全
 * 2015-12-15
 */
public class EnlargedRecyclerView extends RecyclerView {
    public EnlargedRecyclerView(Context context) {
        super(context);
    }

    public EnlargedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnlargedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
