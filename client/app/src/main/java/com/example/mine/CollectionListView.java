package com.example.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CollectionListView {
    public static View inflate(Context context) {
        RecyclerView recyclerView = new RecyclerView(context);

        List<Collection> collections = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            collections.add(new Collection() {{
                title = "title";
                posts = new ArrayList<>();
                posts.add(null);
            }});

        recyclerView.setAdapter(new Adapter(collections));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        List<Collection> collections;

        public Adapter(List<Collection> collections) {
            this.collections = collections;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            View v = holder.itemView;
            Collection collection = collections.get(position);
            ((TextView) v.findViewById(R.id.title)).setText(collection.title);
            ((TextView) v.findViewById(R.id.count)).setText(
                    String.format(Locale.CHINESE, "%d 篇作品", collection.posts.size()));
        }

        @Override
        public int getItemCount() {
            return collections.size();
        }
    }
}
