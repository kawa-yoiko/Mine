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

        records.add(new Notification(1, "粥粥点赞了你的文章",  "1h前", R.drawable.luoxiaohei));
        records.add(new Notification(1, "ldz点赞了你的文章",  "2h前", R.drawable.luoxiaohei));
        records.add(new Notification(2, "食堂合集更新了", "3天前", R.drawable.luoxiaohei));
        records.add(new Notification(0, "粥粥回复了你的文章", "5.20",  R.drawable.luoxiaohei));


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
