package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.ServiceConfigurationError;

public class DiscoverFragment extends Fragment  {
    private RecyclerView recyclerView;

    public DiscoverFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        LinkedList<Post> posts = new LinkedList<>();
        DiscoverAdapter discoverAdapter = new DiscoverAdapter(posts);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setAdapter(discoverAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        Handler handler = new Handler(Looper.getMainLooper());
        recyclerView.addOnScrollListener(new InfScrollListener(recyclerView.getLayoutManager(), 3) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void load(int start) {
                Log.d("Subscription timeline", "Load " + start);
                InfScrollListener listener = this;
                ServerReq.getJsonArray("/discover_timeline?start=" + start + "&count=10", (JSONArray arr) -> {
                    try {
                        int n = arr.length();
                        for (int i = 0; i < n; i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Log.d("Discover timeline", obj.toString());
                            posts.add(new Post(obj));
                        }
                        boolean complete = (n < 10);
                        handler.post(() -> {
                            if (posts.size() == n)
                                discoverAdapter.notifyDataSetChanged();
                            discoverAdapter.notifyItemRangeInserted(posts.size() - n, n);
                            // if (complete) loadingAdapter.clear();
                        });
                        listener.finishLoad(complete);
                    } catch (Exception e) {
                        Log.e("CommentFragment", "During parsing: " + e);
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

}
