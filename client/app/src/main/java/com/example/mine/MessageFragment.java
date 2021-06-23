package com.example.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList = new ArrayList<>(2);
    private String[] tabText = {"通知", "消息"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO
//        Fragment messageRecordFragment = new MessageRecordFragment();
//        Fragment notificationFragment = new NotificationFragment();
//        fragmentList.add(notificationFragment);
//        fragmentList.add(messageRecordFragment);
//        viewPager = view.findViewById(R.id.view_pager);
//        tabLayout = view.findViewById(R.id.tab_layout);
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList, tabText));
    }

    private void initView(View view){
        Fragment messageRecordFragment = new MessageRecordFragment();
        Fragment notificationFragment = new NotificationFragment();
        fragmentList.add(notificationFragment);
        fragmentList.add(messageRecordFragment);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList, tabText));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_message, container, false);
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_message, container, false);
            initView(view);// 控件初始化
        }
        return view;
    }
}
