package com.example.mine;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.*;

public class ServerReq {
    static OkHttpClient client;

    static {
        client = new OkHttpClient();
    }

    private static class ReqCallback implements Callback {
        private final Consumer<String> callback;
        public ReqCallback(Consumer<String> callback) {
            this.callback = callback;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.d("network", "IOException " + e.toString());
            callback.accept(null);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            callback.accept(response.body().string());
        }
    }

    public static Call get(String url, Consumer<String> callbackFn) {
        Request request = new Request.Builder()
                .url("http://8.140.133.34:7678" + url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback(callbackFn));
        return call;
    }


    public static Call post(String url, Consumer<String> callbackFn) {
        RequestBody requestBody = new FormBody.Builder()
                .add("nickname", "kayuyuyuko")
                .add("email", "kayuyuyuko@kawa.moe")
                .add("password", "9876543210")
                .build();
        Request request = new Request.Builder()
                .url("http://8.140.133.34:7678" + url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback(callbackFn));
        return call;
    }
}
