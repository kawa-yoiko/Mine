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

import java.util.LinkedList;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;

    public NotificationFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinkedList<Notification> records = new LinkedList<>();

        for (int i = 0; i < 20; i++) {
            records.add(new Notification(1, "uu1 点赞了你的文章", "1 小时前", R.drawable.luoxiaohei));
            records.add(new Notification(1, "uu2 点赞了你的文章", "1 小时前", R.drawable.luoxiaohei2));
            records.add(new Notification(2, "合集「uu3 的位分类合集」更新了", "1 小时前", R.drawable.luoxiaohei2));
            records.add(new Notification(0, "uu4 回复了你的文章", "1 小时前", R.drawable.luoxiaohei));
            records.add(new Notification(0, "uu5 回复了你的评论", "1 小时前", R.drawable.luoxiaohei2));
        }


        NotificationAdapter adapter = new NotificationAdapter(records);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
}
