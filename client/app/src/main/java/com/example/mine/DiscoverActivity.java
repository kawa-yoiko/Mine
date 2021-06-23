package com.example.mine;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;

import java.util.LinkedList;

public class DiscoverActivity extends AppCompatActivity {
    private String searchTag;

    private TabLayout tab;
    private RecyclerView recyclerView;
    LinkedList<Post> discovers = new LinkedList<>();
    private DiscoverAdapter discoverAdapter;
    private InfScrollListener infScrollListener = null;

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

        this.searchTag = getIntent().getStringExtra("search");

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
        this.discoverAdapter = new DiscoverAdapter(discovers);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        // Initialize view with newest posts
        populateTimeline("new");

        tab = headView.findViewById(R.id.tab);
        View hotSelector = headView.findViewById(R.id.hot_selector);
        hotSelector.setVisibility(View.GONE);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("最新")) {
                    hotSelector.setVisibility(View.GONE);
                    populateTimeline("new");
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
        tag.setText(searchTag);
        TextView tag_num = headView.findViewById(R.id.tag_num);
        tag_num.setText("21k");
    }

    private void populateTimeline(String type) {
        discovers.clear();
        discoverAdapter.notifyDataSetChanged();

        SingleViewAdapter loadingAdapter = new SingleViewAdapter(
                getLayoutInflater().inflate(R.layout.loading_indicator, (ViewGroup) recyclerView, false));
        recyclerView.setAdapter(new ConcatAdapter(singleViewAdapter, discoverAdapter, loadingAdapter));

        Handler handler = new Handler(Looper.getMainLooper());
        recyclerView.removeOnScrollListener(infScrollListener);
        infScrollListener = new InfScrollListener(recyclerView.getLayoutManager(), 3) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void load(int start) {
                InfScrollListener listener = this;
                ServerReq.getJsonArray("/search_posts?type=" + type + "&tag=" + searchTag + "&start=" + start + "&count=10", (JSONArray arr) -> {
                    try {
                        int n = arr.length();
                        for (int i = 0; i < n; i++) {
                            discovers.add(new Post(arr.getJSONObject(i)));
                        }
                        boolean complete = (n < 10);
                        Log.d("DiscoverActivity", "start = " + start + ", n = " + n + ", total = " + discovers.size());
                        handler.post(() -> {
                            if (discovers.size() == n)
                                discoverAdapter.notifyDataSetChanged();
                            else
                                discoverAdapter.notifyItemRangeInserted(discovers.size() - n, n);
                            if (complete) loadingAdapter.clear();
                        });
                        listener.finishLoad(complete);
                    } catch (Exception e) {
                        Log.e("DiscoverActivity", e.toString());
                    }
                });
            }
        };
        recyclerView.addOnScrollListener(infScrollListener);
    }

    private void setHotSelector(String type) {
        //TODO of kuriko: optimize： estimate if the new btn is same as the current;
        if(type.equals("day")) {
            dayBtn.setBackground(darkGrey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(grey);
            populateTimeline("day");
        }
        else if(type.equals("month")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(darkGrey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(grey);
            populateTimeline("month");
        }
        else if(type.equals("semester")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(darkGrey);
            totalBtn.setBackground(grey);
            populateTimeline("season");
        }
        else if(type.equals("total")) {
            dayBtn.setBackground(grey);
            monthBtn.setBackground(grey);
            semesterBtn.setBackground(grey);
            totalBtn.setBackground(darkGrey);
            populateTimeline("all");
        }
    }
}
