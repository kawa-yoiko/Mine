package com.example.mine;

import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private String[] tabText;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> viewList, String[] tabText){
        super(fm);
        this.fragmentList = viewList;
        this.tabText = tabText;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }
}
