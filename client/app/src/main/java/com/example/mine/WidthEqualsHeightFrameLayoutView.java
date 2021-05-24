package com.example.mine;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class WidthEqualsHeightFrameLayoutView extends FrameLayout {

    public WidthEqualsHeightFrameLayoutView(Context context) {
        super(context);
    }

    public WidthEqualsHeightFrameLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthEqualsHeightFrameLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WidthEqualsHeightFrameLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
