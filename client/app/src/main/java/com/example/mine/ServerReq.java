package com.example.mine;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import okhttp3.*;

public class ServerReq {
    static OkHttpClient client;

    static {
        client = new OkHttpClient();
    }

    private static String getFullUrl(String url) {
        if (url.startsWith("/")) {
            return "http://8.140.133.34:7678" + url;
        } else {
            return url;
        }
    }

    private static String streamToString(InputStream stream) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = stream.read(buffer)) != -1; )
                out.write(buffer, 0, length);
            return out.toString("UTF-8");
        } catch (Exception e) {
            Log.e("network", "Exception during string reading " + e.toString());
            return null;
        }
    }

    private static JSONObject parseJson(String s) {
        if (s != null) {
            try {
                return new JSONObject(s);
            } catch (JSONException e) {
                Log.e("network", "JSON parse " + s + " " + e.toString());
                return null;
            }
        }
        return null;
    }

    private static class ReqCallback implements Callback {
        private final Consumer<InputStream> callback;
        public ReqCallback(Consumer<InputStream> callback) {
            this.callback = callback;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.e("network", "IOException " + e.toString());
            callback.accept(null);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            callback.accept(response.body().byteStream());
        }
    }

    public static void getStream(String url, Consumer<InputStream> callbackFn) {
        Request request = new Request.Builder()
                .url(getFullUrl(url))
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback(callbackFn));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void get(String url, Consumer<String> callbackFn) {
        getStream(url, (InputStream stream) -> {
            callbackFn.accept(streamToString(stream));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getJson(String url, Consumer<JSONObject> callbackFn) {
        get(url, (String s) -> {
            callbackFn.accept(parseJson(s));
        });
    }

    public static void postStream(String url, Consumer<InputStream> callbackFn) {
        RequestBody requestBody = new FormBody.Builder()
                .add("nickname", "kayuyuyuko")
                .add("email", "kayuyuyuko@kawa.moe")
                .add("password", "9876543210")
                .build();
        Request request = new Request.Builder()
                .url(getFullUrl(url))
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback(callbackFn));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void post(String url, Consumer<String> callbackFn) {
        postStream(url, (InputStream stream) -> {
            callbackFn.accept(streamToString(stream));
        });
    }

    public static class Utils {
        private static Activity getActivity(View view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
            return null;
        }

        public static void loadImage(String url, ImageView imageView) {
            Activity activity = getActivity(imageView);
            if (activity == null) {
                Log.e("network", "Hosting activity is null");
                return;
            }
            ServerReq.getStream(url, (InputStream stream) -> {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                activity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            });
        }
    }
}
