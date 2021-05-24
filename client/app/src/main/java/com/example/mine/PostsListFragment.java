package com.example.mine;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

public class PostsListFragment extends Fragment {
    private RecyclerView recyclerView;


    public PostsListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        LinkedList<Post> posts = new LinkedList<>();
        PostsListAdapter postsListAdapter = new PostsListAdapter(posts);
        SingleViewAdapter loadingAdapter = new SingleViewAdapter(
                getLayoutInflater().inflate(R.layout.loading_indicator, (ViewGroup) view, false));
        recyclerView.setAdapter(new ConcatAdapter(postsListAdapter, loadingAdapter));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        Handler handler = new Handler(Looper.getMainLooper());
        recyclerView.addOnScrollListener(new InfScrollListener(recyclerView.getLayoutManager(), 3) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void load(int start) {
                Log.d("Subscription timeline", "Load " + start);
                InfScrollListener listener = this;
                ServerReq.getJsonArray("/subscription_timeline?start=" + (start - 1) + "&count=10", (JSONArray arr) -> {
                    try {
                        int n = arr.length();
                        for (int i = 0; i < n; i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Log.d("Subscription timeline", obj.toString());
                            posts.add(new Post(obj));
                        }
                        boolean complete = (n < 10);
                        handler.post(() -> {
                            if (posts.size() == n)
                                postsListAdapter.notifyDataSetChanged();
                            postsListAdapter.notifyItemRangeInserted(posts.size() - n, n);
                            if (complete) loadingAdapter.clear();
                        });
                        listener.finishLoad(complete);
                    } catch (Exception e) {
                        Log.e("CommentFragment", "During parsing: " + e);
                    }
                });
            }
        });
    }
}