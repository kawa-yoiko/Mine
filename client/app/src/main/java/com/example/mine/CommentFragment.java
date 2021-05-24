package com.example.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.LinkedList;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private View headingView;

    public CommentFragment(View headingView) { this.headingView = headingView; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        LinkedList<Comment> comments = new LinkedList<>();
        comments.add(new Comment(R.drawable.flower, "kuriko1023", "太太，饿饿，饭饭", "05-10", "", "255"));
        comments.add(new Comment(R.drawable.luoxiaohei, "yyg", "口水从奇怪的地方流了出来", "05-10", "", "25"));
        comments.add(new Comment(R.drawable.luoxiaohei2, "栗子", "太太下凡辛苦了", "05-9", "", "2"));
        CommentAdapter commentAdapter = new CommentAdapter(comments);
        SingleViewAdapter headingAdapter = new SingleViewAdapter(headingView);
        SingleViewAdapter loadingAdapter = new SingleViewAdapter(View.inflate(getContext(), R.layout.loading_indicator, null));
        ConcatAdapter concatAdapter = new ConcatAdapter(headingAdapter, commentAdapter, loadingAdapter);
        recyclerView.setAdapter(concatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        Handler handler = new Handler(Looper.getMainLooper());
        recyclerView.addOnScrollListener(new InfScrollListener(recyclerView.getLayoutManager(), 3) {
            @Override
            public void load(int start) {
                Log.d("Comment", "Load " + start);
                InfScrollListener listener = this;
                (new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                        }
                        Log.d("comment", "current size " + comments.size());
                        if (comments.size() < 12) {
                            comments.add(new Comment(R.drawable.flower, "kuriko1023", "太太，饿饿，饭饭", "05-10", "", "255"));
                            comments.add(new Comment(R.drawable.luoxiaohei, "yyg", "口水从奇怪的地方流了出来", "05-10", "", "25"));
                            comments.add(new Comment(R.drawable.luoxiaohei2, "栗子", "太太下凡辛苦了", "05-9", "", "2"));
                            handler.post(() -> commentAdapter.notifyItemRangeInserted(comments.size() - 3, 3));
                            listener.finishLoad(false);
                        } else {
                            handler.post(() -> loadingAdapter.clear());
                            listener.finishLoad(true);
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

}
