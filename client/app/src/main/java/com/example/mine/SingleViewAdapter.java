package com.example.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SingleViewAdapter extends RecyclerView.Adapter<SingleViewAdapter.SingleViewHolder> {
    public static class SingleViewHolder extends RecyclerView.ViewHolder {
        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /*
    private final int viewId;
    public SingleViewAdapter(int viewId) {
        this.viewId = viewId;
    }
     */

    private View view;
    public SingleViewAdapter(View view) {
        ((ViewGroup)view.getParent()).removeView(view);
        this.view = view;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) { return 0; }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // View view = LayoutInflater.from(parent.getContext()).inflate(this.viewId, parent, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder holder, int position) {
    }
}
