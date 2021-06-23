package com.example.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private static int IMAGE_PICKER = 0;
    private ImageView avatar_img;
    private ImageItem imageItem;
    private EditText nicknameEdit;
    private EditText passwordEdit;
    private EditText passwordConfirmEdit;
    private EditText introductionEdit;
    private boolean isAvatarChanged = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_set);

        ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(1);
        ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

        View setAvatar = findViewById(R.id.set_avatar);
        View.OnClickListener setAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                isAvatarChanged = true;
            }
        };
        setAvatar.setOnClickListener(setAvatarListener);

        avatar_img = findViewById(R.id.avatar);

        passwordEdit = findViewById(R.id.password);
        passwordConfirmEdit = findViewById(R.id.password_confirm);
        nicknameEdit = findViewById(R.id.nickname);
        introductionEdit = findViewById(R.id.introduction);

        ServerReq.Utils.loadImage("/upload/" + ServerReq.getMyAvatar(), avatar_img);
        nicknameEdit.setText(ServerReq.getMyNickname());
        introductionEdit.setText(ServerReq.getMyBio());

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener((View v) -> {
            setAvatar.setOnClickListener(null);
            nicknameEdit.setEnabled(false);
            passwordEdit.setEnabled(false);
            introductionEdit.setEnabled(false);
            saveButton.setEnabled(false);
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable setTextualProfile = () ->
                ServerReq.postJson("/whoami/edit", List.of(
                        new Pair<>("nickname", nicknameEdit.getText().toString()),
                        new Pair<>("signature", introductionEdit.getText().toString())
                ), (JSONObject objWhoAmI) -> handler.post(() -> {
                    try {
                        int errorCode = objWhoAmI.getInt("error");
                        if (errorCode != 0) {
                            setAvatar.setOnClickListener(setAvatarListener);
                            nicknameEdit.setEnabled(true);
                            passwordEdit.setEnabled(true);
                            passwordConfirmEdit.setEnabled(true);
                            introductionEdit.setEnabled(true);
                            saveButton.setEnabled(true);
                            Toast.makeText(this,
                                    (errorCode == 1 ? "用户名过长或过短" :
                                            errorCode == 2 ? "用户名已被占用" : "未知错误"),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        ServerReq.updateMyInfo(objWhoAmI.getJSONObject("user"));
                    } catch (Exception e) {
                        Log.e("SettingActivity", e.toString());
                    }
                    finish();
                }));
            if (imageItem != null)
                ServerReq.uploadFile("/upload/avatar", new File(imageItem.path),
                        (Long len, Long sent) -> {},
                        (JSONObject obj) -> setTextualProfile.run());
            else setTextualProfile.run();
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
