package com.example.mine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewPagerAdapter extends PagerAdapter {
    private List<String> images;
    private Context context;

    public PhotoViewPagerAdapter(Context context, List<String> images){
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        return;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        WidthEqualsHeightPhotoView imageView = new WidthEqualsHeightPhotoView(context);
        //imageView.setImageResource(images.get(position));
        ServerReq.Utils.loadImage("/upload/" + images.get(position), imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float x, float y) {
//
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//
//            }
//        });
        return imageView;
    }
}
