package com.example.mine;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private TextView titleView;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ImageView searchButton;
    //TODO maybe need modify
    private int isLogin = 0;
    private int LOGIN = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        if (intent.getStringExtra("isLogin") != null) {
            String str = intent.getStringExtra("isLogin");
            if(str.equals("true")) {
                // already login
                isLogin = 1;
                this.setUpAllViews();
            }
        }
        if(isLogin == 0) {
            //goto LoginActivity
            Intent intentNew = new Intent();
            intentNew.setClass(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(intentNew);
        }



//
//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    private void setUpAllViews() {
//        ButterKnife.bind(this);
        titleView = findViewById(R.id.title);
        toolbar = findViewById(R.id.toolbar);
        searchButton = findViewById(R.id.bt_search);
        searchButton.setColorFilter(Color.parseColor("#777777"));

        Fragment discoverFragment = new DiscoverFragment();
        Fragment squareFragment = new SquareFragment();
        Fragment postsListFragment = new PostsListFragment();
        Fragment myFragment = new MyFragment();
        Fragment photoViewPagerFragment = new PhotoViewPagerFragment(new ArrayList<>(List.of()));
        Fragment messageFragment = new MessageFragment();
//        Fragment commentFragment = new CommentFragment();
        setCurrentFragment(postsListFragment);
        titleView.setText("首页");
//        View view = View.inflate(this.getBaseContext(), R.layout.search_box, null);
//        View cv = getWindow().getDecorView();
//        ViewGroup test = cv.findViewById(R.id.test);
//        test.addView(view);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.main:
                            setCurrentFragment(postsListFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            titleView.setText("首页");
                            searchButton.setVisibility(View.GONE);
                            return true;
                        case R.id.discover:
                            setCurrentFragment(discoverFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            titleView.setText("发现");
                            searchButton.setVisibility(View.VISIBLE);
                            return true;
                        case R.id.message:
                            setCurrentFragment(messageFragment);
                            toolbar.setVisibility(View.GONE);
                            return true;
                        case R.id.my:
                            setCurrentFragment(myFragment);
                            toolbar.setVisibility(View.VISIBLE);
                            searchButton.setVisibility(View.GONE);
                            titleView.setText("我的");
                            return true;
                    }
                    return false;
                }
        );

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew = new Intent();
                intentNew.setClass(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(intentNew);
            }
        });

    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == LOGIN){
//            if(data.getStringExtra("Login") == "true") {
//                isLogin = 1;
//            }
//        }
//    }
}