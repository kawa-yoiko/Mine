package com.example.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class SquareFragment extends Fragment {
    private RecyclerView recyclerView;

    public SquareFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        LinkedList<Object> dateAndImages = new LinkedList<>();
        dateAndImages.add("2021年5月");
        dateAndImages.add(R.drawable.content1);
        dateAndImages.add(R.drawable.content2);
        dateAndImages.add("2021年6月");
        dateAndImages.add(R.drawable.content1);
        dateAndImages.add(R.drawable.content2);
        dateAndImages.add(R.drawable.content3);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        SquareAdapter adapter = new SquareAdapter(dateAndImages);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.square_fragment, container, false);
    }
}
