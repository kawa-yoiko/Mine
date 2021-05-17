package com.example.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.LinkedList;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;

    public CommentFragment() {}

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
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

}
