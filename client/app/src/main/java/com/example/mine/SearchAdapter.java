package com.example.mine;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private LinkedList<String> data;

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public SearchAdapter(LinkedList<String> data)
    {
        this.data = data;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        String search = data.get(position);
        View item = holder.itemView;
        TextView contentText = item.findViewById(R.id.content);
        contentText.setText(search);
        item.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            //GO TO activity corresponding to the serach
            intent.setClass(v.getContext(), DiscoverActivity.class);
            intent.putExtra("search", search);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
