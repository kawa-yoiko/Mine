package com.example.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
        LinkedList<Discover> discovers = new LinkedList<>();
        discovers.add(new Discover("粥粥可爱子", "#狗粮", "221", R.drawable.content1));
        discovers.add(new Discover("栗子可爱子", "#狗粮", "221", R.drawable.content2));
        discovers.add(new Discover("简介简介简介简介", "#手绘", "1k", R.drawable.content1));
        discovers.add(new Discover("简介简介简介", "#音乐", "1k", R.drawable.content3));
        discovers.add(new Discover("五跳舞跳舞跳舞", "#五条悟", "1023", R.drawable.content1));
        DiscoverAdapter discoverAdapter = new DiscoverAdapter(discovers);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setAdapter(discoverAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

}
