package com.example.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageShowActivity extends AppCompatActivity {
    private int imageNum;
    //    private int currentPage;
    private ImageView[] dots;
    private ArrayList<String> images;
    private int currentPage = 0;

//    public PhotoViewPagerFragment(List<String> images) {
//        this.images = images;
//        imageNum = images.size();
//        dots = new ImageView[imageNum];
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_show);

        Intent intent = getIntent();
        images = intent.getStringArrayListExtra("images");
        currentPage = intent.getIntExtra("current", 0);
        imageNum = images.size();
        dots = new ImageView[imageNum];

        ViewGroup dotsBox = findViewById(R.id.dots_box);
        for(int i = 0; i < imageNum; i++) {
            ImageView dot = new ImageView(this.getBaseContext());
            dot.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            dots[i] = dot;
            if(i == currentPage) {
                dot.setBackgroundResource(R.drawable.background_white);
            }
            else {
                dot.setBackgroundResource(R.drawable.background_grey2);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
            layoutParams.rightMargin = 12;
            layoutParams.leftMargin = 12;
            dotsBox.addView(dot, layoutParams);
        }
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PhotoViewPagerAdapter(getBaseContext(), images, "whole"));
        viewPager.setCurrentItem(currentPage);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dots[currentPage].setBackgroundResource(R.drawable.background_grey2);
                currentPage = position;
                dots[currentPage].setBackgroundResource(R.drawable.background_white);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
