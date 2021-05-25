package com.example.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class SquareFragment extends Fragment {
    public static class Item { }
    public static class DateItem extends Item {
        public String date;
        public DateItem(String date) { this.date = date; }
    }
    public static class PostItem extends Item {
        public int id;
    }
    public static class TextItem extends PostItem {
        public String caption;
        public String contents;
        public TextItem(int id, String caption, String contents) {
            this.id = id;
            this.caption = caption;
            this.contents = contents;
        }
    }
    public static class ImageItem extends PostItem {
        public String image;
        public ImageItem(int id, String image) {
            this.id = id;
            this.image = image;
        }
    }

    private RecyclerView recyclerView;
    private View headingView;
    private LinkedList<Item> dateAndImages;

    public SquareFragment() {
        this.dateAndImages = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            dateAndImages.add(new DateItem("2021年5月"));
            dateAndImages.add(new DateItem("2021年6月"));
            dateAndImages.add(new TextItem(1, "五条悟跳舞", "ll the beautiful things shining all around us,\n" +
                    "                all the beautiful things shining all around us; all the beautiful things shining all around us"));
        }
    }
    public SquareFragment(View headingView, List<Collection.PostBrief> posts) {
        this.headingView = headingView;
        this.dateAndImages = new LinkedList<>();
        for (Collection.PostBrief post : posts) {
            if (post.type == 0) {
                dateAndImages.add(new TextItem(post.id, post.caption, post.contents));
            } else if (post.type == 1) {
                dateAndImages.add(new ImageItem(post.id, post.contents));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        if (headingView != null) {
            SingleViewAdapter adapterHeading = new SingleViewAdapter(headingView);
            SquareAdapter adapter = new SquareAdapter(dateAndImages, 1);
            ConcatAdapter adapterConcat = new ConcatAdapter(adapterHeading, adapter);
            recyclerView.setAdapter(adapterConcat);
        } else {
            SquareAdapter adapter = new SquareAdapter(dateAndImages, 0);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.square_fragment, container, false);
    }
}
