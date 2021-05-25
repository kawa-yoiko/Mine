package com.example.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {
    PopupWindow popupWindowTag;
    PopupWindow popupWindowCollection;
    RecyclerView recyclerView;
    ArrayList<ImageItem> imageItems = new ArrayList<>(0);
    WidthEqualsHeightImageView addImage;
    private static int IMAGE_PICKER = 0;

    private String tags = "";
    private User.CollectionBrief collection = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        View createAreaView = null;
        View cv = getWindow().getDecorView();
        ViewGroup area = cv.findViewById(R.id.create_area);
        Intent intent = getIntent();
        String createType = intent.getStringExtra("create_type");

        if(createType.equals("text")) {
            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_text, null);
        }
        if(createType.equals("image")) {
            ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(9 - imageItems.size());
            ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_image, null);
            recyclerView = createAreaView.findViewById(R.id.recyclerview);
            addImage = new WidthEqualsHeightImageView(getApplicationContext());
            addImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addImage.setImageResource(R.drawable.luoxiaohei);
            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreateActivity.this, ImageGridActivity.class);
                    startActivityForResult(intent, IMAGE_PICKER);
                }
            });
            recyclerView.setAdapter(new ConcatAdapter(new ImagePickerAdapter(imageItems), new SingleViewAdapter(addImage)));
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
//            recyclerView.setAdapter(new ImagePickerAdapter(imageItems));
        }

        area.addView(createAreaView);

        View setTagButton = findViewById(R.id.add_tag);
        setTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.popup_tag, null);
                Button finishButton = popView.findViewById(R.id.finish);
                EditText tagInput = popView.findViewById(R.id.tag_input);
                tagInput.setText(tags);
                finishButton.setOnClickListener((View v1) -> popupWindowTag.dismiss());
                popupWindowTag = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowTag.setOutsideTouchable(true);
                popupWindowTag.setFocusable(true);
                popupWindowTag.showAtLocation(cv, Gravity.BOTTOM, 0, 0);
                popupWindowTag.setOnDismissListener(() -> {
                    tags = tagInput.getText().toString();
                });
            }
        });

        View popViewCollection = getLayoutInflater().inflate(R.layout.popup_collection, null);
        FrameLayout collectionContainer = popViewCollection.findViewById(R.id.fl_collection);
        collectionContainer.removeAllViews();
        collectionContainer.addView(CollectionListView.inflate(popViewCollection.getContext(), (User.CollectionBrief sel, Boolean init) -> {
            if (init && collection != null) return;
            android.util.Log.d("CreateActivity", "selected collection " + sel.title + " (" + sel.id + ")");
            collection = sel;
            if (!init) popupWindowCollection.dismiss();
        }));

        View setCollectionButton = findViewById(R.id.add_collection);
        setCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button finishButton = popViewCollection.findViewById(R.id.create);
                finishButton.setOnClickListener((View v1) -> {
                });
                popupWindowCollection = new PopupWindow(popViewCollection, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowCollection.setOutsideTouchable(true);
                popupWindowCollection.setFocusable(true);
                popupWindowCollection.showAtLocation(cv, Gravity.BOTTOM, 0, 0);
                popupWindowCollection.setOnDismissListener(() -> {
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.addAll(images);
                recyclerView.setAdapter(new ConcatAdapter(new ImagePickerAdapter(imageItems), new SingleViewAdapter(addImage)));
            }
        }
    }
}