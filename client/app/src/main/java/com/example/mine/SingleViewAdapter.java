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

    private View view;
    public SingleViewAdapter(View view) {
        ViewGroup parent = (ViewGroup)view.getParent();
        if(parent != null) {
            parent.removeView(view);
        }
        this.view = view;
    }

    @Override
    public int getItemCount() {
        return (view != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) { return 0; }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder holder, int position) {
    }

    public void clear() {
        view = null;
        this.notifyItemRemoved(0);
    }
}
