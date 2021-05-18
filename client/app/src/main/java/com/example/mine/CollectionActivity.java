package com.example.mine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class CollectionActivity extends AppCompatActivity {
    Fragment squareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        squareFragment = new SquareFragment(this.findViewById(R.id.heading));
        FrameLayout layout = (FrameLayout)this.findViewById(R.id.squares_container);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.squares_container, squareFragment)
                .commit();
    }
}