package com.example.mine;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;

public class DiscoverActivity extends AppCompatActivity {
    private TabLayout tab;
    private RecyclerView recyclerView;
    LinkedList<Post> discovers = new LinkedList<>();
    private Button dayBtn;
    private Button monthBtn;
    private Button semesterBtn;
    private Button totalBtn;
    private Drawable grey;
    private Drawable darkGrey;
    private SingleViewAdapter singleViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_discover);

        grey = this.getResources().getDrawable(R.drawable.bt_background_grey);
        darkGrey = this.getResources().getDrawable(R.drawable.bt_background_grey1);

        View headView = getLayoutInflater().inflate(R.layout.activity_discover_head, null, false);
        singleViewAdapter = new SingleViewAdapter(headView);

        dayBtn = headView.findViewById(R.id.day_hot);
        monthBtn = headView.findViewById(R.id.month_hot);
        semesterBtn = headView.findViewById(R.id.semester_hot);
        totalBtn = headView.findViewById(R.id.total_hot);

        recyclerView = findViewById(R.id.recyclerview);
        //TODO： get corresponding discover data -- 最新
        discovers.add(new Post("粥粥","测试","","kuriko", "2020.5.31", "测试", "测试最新", 0, 0, 0, 0));

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));

        tab = headView.findViewById(R.id.tab);
        View hotSelector = headView.findViewById(R.id.hot_selector);
        hotSelector.setVisibility(View.GONE);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("最新")) {
                    hotSelector.setVisibility(View.GONE);
                    //TODO： get corresponding discover data -- 最新
                    discovers.clear();
                    discovers.add(new Post("粥粥","测试","","kuriko", "2020.5.31", "测试最新", "测试", 0, 0, 0, 0));
                    recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));
                }
                else if (tab.getText().equals("最热")) {
                    hotSelector.setVisibility(View.VISIBLE);
                    setHotSelector("day");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        View.OnClickListener hotSelectorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.day_hot:
                        setHotSelector("day");
                        break;
                    case R.id.month_hot:
                        setHotSelector("month");
                        break;
                    case R.id.semester_hot:
                        setHotSelector("semester");
                        break;
                    case R.id.total_hot:
                        setHotSelector("total");
                        break;
                }
            }
        };
        dayBtn.setOnClickListener(hotSelectorListener);
        monthBtn.setOnClickListener(hotSelectorListener);
        semesterBtn.setOnClickListener(hotSelectorListener);
        totalBtn.setOnClickListener(hotSelectorListener);

        //TODO: get info of this tag
        TextView tag = headView.findViewById(R.id.tag);
        tag.setText("粥粥");
        TextView tag_num = headView.findViewById(R.id.tag_num);
        tag_num.setText("21k");
    }

    private void setHotSelector(String type) {
        //TODO of kuriko: optimize： estimate if the new btn is same as the current;
        discovers.clear();
        if(type.equals("day")) {
            dayBtn.setBackground(darkGrey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(grey);
            //TODO: get corresponding discover data -- daily hot
            discovers.add(new Post("粥粥","测试日榜","","kuriko", "2020.5.31", "测试", "测试", 0, 0, 0, 0));
            recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));
        }
        else if(type.equals("month")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(darkGrey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(grey);
            //TODO: get corresponding discover data -- monthly hot
            discovers.add(new Post("粥粥","测试月榜","","kuriko", "2020.5.31", "测试", "测试", 0, 0, 0, 0));
            recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));
        }
        else if(type.equals("semester")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(darkGrey);
            totalBtn.setBackground(grey);
            //TODO: get corresponding discover data -- semester's hot
            discovers.add(new Post("粥粥","测试季榜","","kuriko", "2020.5.31", "测试", "测试", 0, 0, 0, 0));
            recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));
        }
        else if(type.equals("total")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(darkGrey);
            //TODO: get corresponding discover data -- totally hot
            discovers.add(new Post("粥粥","测试总榜","","kuriko", "2020.5.31", "测试", "测试", 0, 0, 0, 0));
            recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, new DiscoverAdapter(discovers)));
        }
    }
}
