package com.example.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList = new ArrayList<>(2);
    private String[] tabText = {"作品", "合集"};

    public static class CollectionListFragment extends Fragment {
        private final String nickname;

        public CollectionListFragment(String nickname) {
            super(R.layout.empty_constraintlayout);
            this.nickname = nickname;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.container);
            layout.addView(CollectionListView.inflate(layout.getContext(), nickname, (User.CollectionBrief sel, Boolean init) -> {
                // android.util.Log.d("HomepageActivity", "Clicked collection " + sel.title);
                if (init) return;
                Intent intent = new Intent();
                intent.setClass(getContext(), LoadingActivity.class);
                intent.putExtra("type", LoadingActivity.DestType.collection);
                intent.putExtra("id", sel.id);
                getContext().startActivity(intent);
            }));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage);
        User user = (User) getIntent().getSerializableExtra("user");

        SquareFragment squareFragment = new SquareFragment(null, user.posts);
        CollectionListFragment discoverFragment = new CollectionListFragment(user.nickname);
        fragmentList.add(squareFragment);
        fragmentList.add(discoverFragment);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, tabText));

        ((TextView) findViewById(R.id.nickname)).setText(user.nickname);
        ((TextView) findViewById(R.id.signature)).setText(user.signature);
        ((TextView) findViewById(R.id.post_num)).setText(String.valueOf(user.posts.size()));
        ((TextView) findViewById(R.id.collection_num)).setText(String.valueOf(user.collections.size()));
        ServerReq.Utils.loadImage("/upload/" + user.avatar,
                ((ImageView) findViewById(R.id.avatar)));
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
