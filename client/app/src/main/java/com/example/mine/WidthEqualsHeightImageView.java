package com.example.mine;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class WidthEqualsHeightImageView extends AppCompatImageView {

    public WidthEqualsHeightImageView(Context context) {
        super(context);
    }

    public WidthEqualsHeightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthEqualsHeightImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}