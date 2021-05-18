package com.example.mine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
        Fragment discoverFragment = new DiscoverFragment();
        Fragment squareFragment = new SquareFragment();
        Fragment postsListFragment = new PostsListFragment();
        Fragment myFragment = new MyFragment();
        Fragment commentFragment = new CommentFragment();
        setCurrentFragment(myFragment);
//        View view = View.inflate(this.getBaseContext(), R.layout.search_box, null);
//        View cv = getWindow().getDecorView();
//        ViewGroup test = cv.findViewById(R.id.test);
//        test.addView(view);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PostActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }
}