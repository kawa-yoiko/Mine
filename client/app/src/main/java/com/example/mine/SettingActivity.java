package com.example.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
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

public class SettingActivity extends AppCompatActivity {
    private static int IMAGE_PICKER = 0;
    private ImageView avatar_img;
    private ImageItem imageItem;
    private EditText nicknameEdit;
    private EditText passwordEdit;
    private EditText introductionEdit;
    private String nickname;
    private String password;
    private String introduction;
    private boolean isAvatarChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_set);

        ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(1);
        ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

        View setAvatar = findViewById(R.id.set_avatar);
        setAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                isAvatarChanged = true;
            }
        });

        avatar_img = findViewById(R.id.avatar);

        passwordEdit = findViewById(R.id.password);
        nicknameEdit = findViewById(R.id.nickname);
        introductionEdit = findViewById(R.id.introduction);
        password = passwordEdit.getText().toString();
        nickname = nicknameEdit.getText().toString();
        introduction = introductionEdit.getText().toString();

        passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password = passwordEdit.getText().toString();
                }
            }
        });
        nicknameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    nickname = nicknameEdit.getText().toString();
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
                avatar_img.setImageBitmap(bm);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: send nickname, password introduction and avator to server
        // if need, apply constraint that: only send changed items to server
        if(isAvatarChanged) {

        }
        else {

        }
    }
}
