package com.example.mine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        posts.add(new Post("粥粥的烹饪", "#美食 #狗粮 #每周粥粥", "", "kuriko", "15分钟前", "今天是甜粥粥。",
                "", 0, 221, 221, 221));
        posts.add(new Post("沙雕", "#自然科学 #物理 #什么奇怪的东西混进来了", "", "pisces", "30分钟前", "栗子发射",
                "什么是栗子发射器呢？很多人都想知道这个问题，快来跟小编一起看看吧。顾名思义，栗子发射器就是发射栗子的发射器，这就是什么是栗子发射器的解释，希望粥粥精心整理的这篇内容能够解决你的困惑。", 1, 1023, 1023, 1023));
        PostsListAdapter postsListAdapter = new PostsListAdapter(posts);
        recyclerView.setAdapter(postsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}