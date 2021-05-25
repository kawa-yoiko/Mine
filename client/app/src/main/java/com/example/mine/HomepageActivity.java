package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList = new ArrayList<>(2);
    private String[] tabText = {"作品", "合集"};

    public static class CollectionListFragment extends Fragment {
        public CollectionListFragment() {
            super(R.layout.empty_constraintlayout);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.container);
            layout.addView(CollectionListView.inflate(layout.getContext(), (User.CollectionBrief sel, Boolean init) -> {
                android.util.Log.d("HomepageActivity", "Clicked collection " + sel.title);
            }));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage);
        Fragment squareFragment = new SquareFragment();
        Fragment discoverFragment = new CollectionListFragment();
        fragmentList.add(squareFragment);
        fragmentList.add(discoverFragment);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, tabText));
    }


//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//    }
}
