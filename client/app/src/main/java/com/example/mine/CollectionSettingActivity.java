package com.example.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;

public class CollectionSettingActivity extends AppCompatActivity {
    private static int IMAGE_PICKER = 0;
    private ImageView cover_img;
    private ImageItem imageItem;
    private EditText nameEdit;
    private EditText tagEdit;
    private EditText introductionEdit;
    private String name;
    private String tag;
    private String introduction;
    private boolean isCoverChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.collection_set);

        ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(1);
        ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

        View setCover = findViewById(R.id.set_cover);
        setCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionSettingActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                isCoverChanged = true;
            }
        });

        cover_img = findViewById(R.id.cover);

        tagEdit = findViewById(R.id.tag);
        nameEdit = findViewById(R.id.name);
        introductionEdit = findViewById(R.id.introduction);
        tag = tagEdit.getText().toString();
        name = nameEdit.getText().toString();
        introduction = introductionEdit.getText().toString();

        tagEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    tag = tagEdit.getText().toString();
                }
            }
        });
        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    name = nameEdit.getText().toString();
                }
            }
        });
        introductionEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    introduction = introductionEdit.getText().toString();
                }
            }
        });

        Button ensureBtn = findViewById(R.id.ensure);
        ensureBtn.setOnClickListener((View v) -> {
            //TODO: send name, tag introduction and avator to server
            // if need, apply constraint that: only send changed items to server
            if(isCoverChanged) {

            }
            else {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItem = images.get(0);
                String path = imageItem.path;
                Bitmap bm = BitmapFactory.decodeFile(path);
                cover_img.setImageBitmap(bm);
            }
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if(isCoverChanged) {
//
//        }
//        else {
//
//        }
//    }
}
