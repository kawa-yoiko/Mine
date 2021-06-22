package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private final int postId;
    private final View headingView;
    private final Consumer<Comment> replyCallback;

    private LinkedList<Comment> comments;

    public CommentFragment(int postId, View headingView, Consumer<Comment> replyCallback) {
        this.postId = postId;
        this.headingView = headingView;
        this.replyCallback = replyCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        comments = new LinkedList<>();
        CommentAdapter commentAdapter = new CommentAdapter(this.postId, comments, replyCallback);
        SingleViewAdapter headingAdapter = new SingleViewAdapter(headingView);
        SingleViewAdapter loadingAdapter = new SingleViewAdapter(
                getLayoutInflater().inflate(R.layout.loading_indicator, (ViewGroup) view, false));
        ConcatAdapter concatAdapter = new ConcatAdapter(headingAdapter, commentAdapter, loadingAdapter);
        recyclerView.setAdapter(concatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        Handler handler = new Handler(Looper.getMainLooper());
        recyclerView.addOnScrollListener(new InfScrollListener(recyclerView.getLayoutManager(), 3) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void load(int start) {
                if (start < 2) return;
                Log.d("Comment", "Load " + start);
                InfScrollListener listener = this;
                ServerReq.getJsonArray("/post/" + CommentFragment.this.postId +
                        "/comments?start=" + (start - 2) + "&count=10", (JSONArray arr) -> {
                    try {
                        int n = arr.length();
                        for (int i = 0; i < n; i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            comments.add(new Comment(obj));
                        }
                        boolean complete = (n < 10);
                        handler.post(() -> {
                            commentAdapter.notifyItemRangeInserted(comments.size() - n, n);
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

    public void refresh() {
        this.onViewCreated(getView(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

}
