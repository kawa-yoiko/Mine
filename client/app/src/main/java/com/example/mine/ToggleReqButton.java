package com.example.mine;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ToggleReqButton {
    private Handler handler;

    private int state;
    private final View button;
    private final ImageView icon;
    private final TextView text;

    private final int iconNormal;
    private final int iconActive;
    private final int tint;
    private final int grey = Color.parseColor("#888888");

    public ToggleReqButton(View button, ImageView icon, TextView text,
                           int iconNormal, int iconActive, int tint,
                           String url, String key) {
        handler = new Handler(Looper.getMainLooper());
        this.state = 0;
        this.button = button;
        this.icon = icon;
        this.text = text;

        this.iconNormal = iconNormal;
        this.iconActive = iconActive;
        this.tint = tint;

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int isAdd = 1 - state;
                setState(-1, -1);
                ServerReq.postJson(url, List.of(
                        new Pair<>("is_" + key, String.valueOf(isAdd))
                ), (JSONObject obj) -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    try {
                        int count = obj.getInt(key + "_count");
                        setState(isAdd, count);
                    } catch (JSONException e) {
                        Log.e("ToggleReqButton", e.toString());
                    }
                });
            }
        });
    }

    public void setState(int state, int number) {
        this.state = state;
        handler.post(() -> {
            button.setEnabled(state != -1);
            icon.setImageResource(
                    state == -1 ? iconNormal :
                    state == 0 ? iconNormal : iconActive);
            icon.setColorFilter(
                    state == -1 ? tint :
                    state == 0 ? grey : tint);
            if (number != -1) text.setText(String.valueOf(number));
        });
    }

    public int getState() { return state; }
}
