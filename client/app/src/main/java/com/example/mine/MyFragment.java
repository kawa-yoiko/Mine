package com.example.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import okhttp3.*;

public class MyFragment  extends Fragment {
    private PopupWindow popupWindow;
    private static final int REQUEST_ACCOUNT_SETTINGS = 1;
    public MyFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView settingIcon = view.findViewById(R.id.setting_icon);
        ImageView aboutIcon = view.findViewById(R.id.about_icon);
        ImageView starIcon = view.findViewById(R.id.star_icon);
        ImageView logoutIcon = view.findViewById(R.id.logout_icon);
        settingIcon.setColorFilter(Color.parseColor("#AAAAAA"));
        aboutIcon.setColorFilter(Color.parseColor("#8196BE"));
        starIcon.setColorFilter(Color.parseColor("#F4DB35"));
        logoutIcon.setColorFilter(Color.parseColor("#888888"));
        
        Button post_button = view.findViewById(R.id.post_button);
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.post_type_selector, null);
                
                ImageView textIcon = popView.findViewById(R.id.text_icon);
                ImageView imageIcon = popView.findViewById(R.id.image_icon);
                ImageView musicIcon = popView.findViewById(R.id.music_icon);
                ImageView videoIcon = popView.findViewById(R.id.video_icon);
                textIcon.setColorFilter(Color.parseColor("#78DDB9"));
                imageIcon.setColorFilter(Color.parseColor("#5FB4E0"));
                musicIcon.setColorFilter(Color.parseColor("#FFC636"));
                videoIcon.setColorFilter(Color.parseColor("#B887C9"));
                
                View text_button = popView.findViewById(R.id.text);
                View image_button = popView.findViewById(R.id.image);
                View music_button = popView.findViewById(R.id.music);
                View video_button = popView.findViewById(R.id.video);
                View.OnClickListener typeSelectorListener = new View.OnClickListener() {
                    @Override
                     public void onClick(View view) {
                        Intent intent = new Intent();
                        switch (view.getId()) {
                            case R.id.text:
                                intent.putExtra("create_type", "text");
                                break;
                            case R.id.image:
                                intent.putExtra("create_type", "image");
                                break;
                            case R.id.music:
                                intent.putExtra("create_type", "audio");
                                break;
                            case R.id.video:
                                intent.putExtra("create_type", "video");
                                break;
                            default:
                                break;
                        }
                        intent.setClass(getActivity(), CreateActivity.class);
                        getActivity().startActivity(intent);
                    }
                };
                text_button.setOnClickListener(typeSelectorListener);
                image_button.setOnClickListener(typeSelectorListener);
                music_button.setOnClickListener(typeSelectorListener);
                video_button.setOnClickListener(typeSelectorListener);

                popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(post_button, 300, -100);
//                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            }
        });

        ( view.findViewById(R.id.settings_button)).setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            this.startActivityForResult(intent, REQUEST_ACCOUNT_SETTINGS);
        });

        ( view.findViewById(R.id.stars_button)).setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), StarActivity.class);
            getActivity().startActivity(intent);
        });

        (view.findViewById(R.id.logout_button)).setOnClickListener((View v) -> {
            Activity activity = getActivity();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
            pref.edit().remove("token").apply();
            Intent intentNew = new Intent();
            intentNew.setClass(activity, LoginActivity.class);
            intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            activity.startActivity(intentNew);
            activity.finish();
        });

        ImageView gotoHomePageBtn = view.findViewById(R.id.goto_homepage);
        gotoHomePageBtn.setColorFilter(Color.parseColor("#555555"));
        gotoHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoadingActivity.class);
                intent.putExtra("type", LoadingActivity.DestType.homepage);
                intent.putExtra("nickname", ServerReq.getMyNickname());
                getActivity().startActivity(intent);
            }
        });

        updateUserInfoDisplay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_SETTINGS) {
            this.updateUserInfoDisplay();
        }
    }

    private void updateUserInfoDisplay() {
        ImageView avatar = getView().findViewById(R.id.avatar);
        if (ServerReq.getMyAvatar().isEmpty()) {
            avatar.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.themeyellow, null));
            avatar.setImageResource(R.drawable.image);
        } else {
            avatar.setColorFilter(null);
            ServerReq.Utils.loadImage("/upload/" + ServerReq.getMyAvatar(), avatar);
        }
        ((TextView) getView().findViewById(R.id.nickname)).setText(ServerReq.getMyNickname());
        ((TextView) getView().findViewById(R.id.signature)).setText(ServerReq.getMyBio());
    }
}
