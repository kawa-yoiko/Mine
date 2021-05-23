package com.example.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class PhotoViewPagerFragment extends Fragment {
    private int imageNum;
//    private int currentPage;
    private ImageView[] dots;
    private ArrayList<Integer> images;
    private int currentPage = 0;

    public PhotoViewPagerFragment() {
        images = new ArrayList<Integer>(Arrays.asList(R.drawable.luoxiaohei, R.drawable.luoxiaohei2,R.drawable.luoxiaohei, R.drawable.luoxiaohei2, R.drawable.luoxiaohei));
        imageNum = 5;
        dots = new ImageView[imageNum];
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup dotsBox = view.findViewById(R.id.dots_box);
        for(int i = 0; i < imageNum; i++) {
            ImageView dot = new ImageView(this.getContext());
            dot.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            dots[i] = dot;
            if(i == 0) {
                dot.setBackgroundResource(R.drawable.background_whiteblue);
            }
            else {
                dot.setBackgroundResource(R.drawable.background_grey2);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            layoutParams.rightMargin = 8;
            layoutParams.leftMargin = 8;
            dotsBox.addView(dot, layoutParams);
        }
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new PhotoViewPagerAdapter(this.getContext(), images));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dots[currentPage].setBackgroundResource(R.drawable.background_grey2);
                currentPage = position;
                dots[currentPage].setBackgroundResource(R.drawable.background_whiteblue);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pager, container, false);
    }

}
