package com.shunlai.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author Liu
 * @Date 2021/5/24
 * @mobile 18711832023
 */
public class MarqueeTextView  extends AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);

    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
