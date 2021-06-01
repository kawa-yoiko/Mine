package com.example.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class MessageRecordFragment extends Fragment {
    private RecyclerView recyclerView;

    public MessageRecordFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinkedList<MessageRecord> records = new LinkedList<>();

        records.add(new MessageRecord("kuriko", "粥粥", "1", "1h前", R.drawable.luoxiaohei));
        records.add(new MessageRecord("kuriko", "ldzldzldzldzldz", "0", "2h前", R.drawable.luoxiaohei));
        records.add(new MessageRecord("栗子", "中午去哪吃", "1", "3h前", R.drawable.luoxiaohei));
        records.add(new MessageRecord("kuriko", "不想起床", "2", "3h前", R.drawable.luoxiaohei));

        recyclerView = view.findViewById(R.id.recyclerview);

//        Button bt = view.findViewById(R.id.test);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                records.remove(0);
//                MessageRecordAdapter adapterNew = new MessageRecordAdapter(records);
//                recyclerView.setAdapter(adapterNew);
//            }
//        });
        MessageRecordAdapter adapter = new MessageRecordAdapter(records);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messagerecord, container, false);
    }
}
