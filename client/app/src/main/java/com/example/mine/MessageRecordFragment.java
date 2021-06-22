package com.example.mine;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageRecordFragment extends Fragment {
    private RecyclerView recyclerView;
    LinkedList<MessageRecord> records;
    MessageRecordAdapter adapter;
    Timer timer;

    public MessageRecordFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        records = new LinkedList<>();

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
        adapter = new MessageRecordAdapter(records, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateRecentList();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRecentList();
            }
        }, 2000, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateRecentList() {
        Handler handler = new Handler(Looper.getMainLooper());
        ServerReq.getJsonArray("/message/latest", (JSONArray arr) -> handler.post(() -> {
            try {
                ArrayList<MessageRecord> newRecords = new ArrayList<>();
                ArrayList<Integer> originalIndices = new ArrayList<>();
                ArrayList<Boolean> changed = new ArrayList<>();
                newRecords.ensureCapacity(arr.length());
                originalIndices.ensureCapacity(arr.length());
                changed.ensureCapacity(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    MessageRecord newRec = new MessageRecord(arr.getJSONObject(i));
                    newRecords.add(newRec);
                    // Brute force. No need to optimize
                    int originalIndex = 0;
                    boolean itemChanged = false;
                    for (MessageRecord rec : records) {
                        if (rec.getUsername().equals(newRec.getUsername())) {
                            itemChanged = (rec.getMessageId() != newRec.getMessageId() ||
                                    !rec.getMessageNum().equals(newRec.getMessageNum()));
                            break;
                        }
                        originalIndex++;
                    }
                    originalIndices.add(originalIndex < records.size() ? originalIndex : -1);
                    changed.add(itemChanged);
                }
                records.clear();
                records.addAll(newRecords);
                // Correctly reorder
                // adapter.notifyDataSetChanged();
                boolean anythingMoved = false;
                ArrayList<Integer> movedOriginals = new ArrayList<>();
                movedOriginals.ensureCapacity(records.size());
                for (int i = 0; i < originalIndices.size(); i++) {
                    int originalIndex = originalIndices.get(i);
                    if (originalIndex != -1) {
                        int currentOriginalIndex = originalIndex + movedOriginals.size();
                        for (int j : movedOriginals)
                            if (j < originalIndex) currentOriginalIndex--;
                        if (currentOriginalIndex != movedOriginals.size()) {
                            anythingMoved = true;
                            adapter.notifyItemMoved(currentOriginalIndex, movedOriginals.size());
                        }
                        movedOriginals.add(originalIndex);
                    }
                }
                for (int i = 0; i < originalIndices.size(); i++) {
                    int originalIndex = originalIndices.get(i);
                    if (originalIndex == -1)
                        adapter.notifyItemInserted(i);
                    else if (changed.get(i))
                        adapter.notifyItemChanged(i);
                }
                if (anythingMoved)
                    recyclerView.smoothScrollToPosition(0);
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

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateRecentList();
    }
}
