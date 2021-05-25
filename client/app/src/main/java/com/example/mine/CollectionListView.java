package com.example.mine;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CollectionListView {
    public static View inflate(Context context, BiConsumer<User.CollectionBrief, Boolean> callback) {
        FrameLayout container = new FrameLayout(context);
        View.inflate(context, R.layout.loading_indicator, container);

        RecyclerView recyclerView = new RecyclerView(context);
        List<User.CollectionBrief> collections = new ArrayList<>();
        recyclerView.setAdapter(new Adapter(collections, callback));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ServerReq.getJson("/whoami", (JSONObject obj) -> {
            User user = new User(obj);
            collections.addAll(user.collections);
            container.removeAllViews();
            container.addView(recyclerView);
            callback.accept(user.collections.get(0), true);
        });

        return container;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<User.CollectionBrief> collections;
        private final BiConsumer<User.CollectionBrief, Boolean> callback;

        public Adapter(List<User.CollectionBrief> collections, BiConsumer<User.CollectionBrief, Boolean> callback) {
            this.collections = collections;
            this.callback = callback;
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
            User.CollectionBrief collection = collections.get(position);
            ((TextView) v.findViewById(R.id.title)).setText(collection.title);
            ((TextView) v.findViewById(R.id.count)).setText(
                    String.format(Locale.CHINESE, "%d 篇作品", collection.postCount));
            v.setOnClickListener((View v1) -> this.callback.accept(collection, false));
        }

        @Override
        public int getItemCount() {
            return collections.size();
        }
    }
}
