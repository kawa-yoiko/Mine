package com.example.mine;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import uk.co.senab.photoview.PhotoView;

public class WidthEqualsHeightPhotoView extends PhotoView {
    public WidthEqualsHeightPhotoView(Context context) {
        super(context);
    }

    public WidthEqualsHeightPhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthEqualsHeightPhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
