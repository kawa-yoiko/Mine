package com.example.mine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private TextView titleView;
    private androidx.appcompat.widget.Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//
//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ServerReq.login("uu2", "aaaaaa2", (Boolean success) -> {
            android.util.Log.d("MainActivity", success ? ServerReq.token : "T-T");
        });

//        ButterKnife.bind(this);
        titleView = findViewById(R.id.title);
        toolbar = findViewById(R.id.toolbar);

        Fragment discoverFragment = new DiscoverFragment();
        Fragment squareFragment = new SquareFragment();
        Fragment postsListFragment = new PostsListFragment();
        Fragment myFragment = new MyFragment();
        Fragment photoViewPagerFragment = new PhotoViewPagerFragment(List.of());
//        Fragment commentFragment = new CommentFragment();
        setCurrentFragment(photoViewPagerFragment);
//        View view = View.inflate(this.getBaseContext(), R.layout.search_box, null);
//        View cv = getWindow().getDecorView();
//        ViewGroup test = cv.findViewById(R.id.test);
//        test.addView(view);
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, TestActivity.class);
//        MainActivity.this.startActivity(intent);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.main:
                            setCurrentFragment(postsListFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            titleView.setText("首页");
                            return true;
                        case R.id.discover:
                            setCurrentFragment(discoverFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            titleView.setText("发现");
                            return true;
                        case R.id.message:
                            setCurrentFragment(squareFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            titleView.setText("消息");
                            return true;
                        case R.id.my:
                            setCurrentFragment(myFragment);
                            toolbar.setVisibility(View.INVISIBLE);
                            return true;
                    }
                    return false;
                }
        );

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }
}