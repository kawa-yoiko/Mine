package com.example.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import okhttp3.*;

public class MyFragment  extends Fragment {
    private PopupWindow popupWindow;
    public MyFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button post_button = view.findViewById(R.id.post_button);
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popView = getLayoutInflater().inflate(R.layout.post_type_selector, null);
                View text_button = popView.findViewById(R.id.text);
                View image_button = popView.findViewById(R.id.image);
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
                            default:
                                break;
                        }
                        intent.setClass(getActivity(), CreateActivity.class);
                        getActivity().startActivity(intent);
                    }
                };
                text_button.setOnClickListener(typeSelectorListener);
                image_button.setOnClickListener(typeSelectorListener);

                popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(post_button, 300, -100);
//                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            }
        });

        Button btn = (Button)view.findViewById(R.id.settings_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
            }
        });

        View gotoHomePageBtn = view.findViewById(R.id.goto_homepage);
        gotoHomePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomepageActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
}
