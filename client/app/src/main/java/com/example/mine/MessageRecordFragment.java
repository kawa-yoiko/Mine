package com.example.mine;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.LinkedList;

public class MessageRecordFragment extends Fragment {
    private RecyclerView recyclerView;

    public MessageRecordFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinkedList<MessageRecord> records = new LinkedList<>();

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

        Handler handler = new Handler(Looper.getMainLooper());
        ServerReq.getJsonArray("/message/latest", (JSONArray arr) -> handler.post(() -> {
            try {
                for (int i = 0; i < arr.length(); i++) {
                    records.add(new MessageRecord(arr.getJSONObject(i)));
                }
                adapter.notifyItemRangeInserted(0, arr.length());
            } catch (Exception e) {
                Log.e("MessageRecordFragment", e.toString());
            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messagerecord, container, false);
    }
}
