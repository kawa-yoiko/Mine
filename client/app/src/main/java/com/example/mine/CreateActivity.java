package com.example.mine;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class CreateActivity extends AppCompatActivity {
    PopupWindow popupWindowTag;
    PopupWindow popupWindowCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        View createAreaView = null;
        View cv = getWindow().getDecorView();
        ViewGroup area = cv.findViewById(R.id.create_area);
        Intent intent = getIntent();
//        String createType = intent.getStringExtra("create_type");
//        if(createType.equals("text")) {
//            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_text, null);
//        }
//        if(createType.equals("image")) {
//            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_image, null);
//        }
//        area.addView(createAreaView);
        View setTagButton = findViewById(R.id.add_tag);
        setTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.popup_tag, null);
                Button finishButton = popView.findViewById(R.id.finish);
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                popupWindowTag = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowTag.setOutsideTouchable(true);
                popupWindowTag.setFocusable(true);
                popupWindowTag.showAtLocation(cv, Gravity.BOTTOM, 0, 0);
            }
        });
        View setCollectionButton = findViewById(R.id.add_collection);
        setCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.popup_collection, null);
                Fragment discoverFragment = new DiscoverFragment();
                Button finishButton = popView.findViewById(R.id.create);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_collection, discoverFragment).commit();
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                popupWindowCollection = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowCollection.setOutsideTouchable(true);
                popupWindowCollection.setFocusable(true);
                popupWindowCollection.showAtLocation(cv, Gravity.BOTTOM, 0, 0);
            }
        });
    }
}