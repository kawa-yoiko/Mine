package com.example.mine;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.*;

public class ServerReq {
    static OkHttpClient client;

    static String token;

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

    private static JSONArray parseJsonArray(String s) {
        if (s != null) {
            try {
                return new JSONArray(s);
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
            if (response.code() >= 400 && response.code() <= 599)
                Log.e("network", "Response code " + response);
            callback.accept(response.body().byteStream());
        }
    }

    public static void getStream(String url, Consumer<InputStream> callbackFn) {
        Request request = new Request.Builder()
                .url(getFullUrl(url))
                .header("Authorization", token != null ? "Bearer " + token : "")
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getJsonArray(String url, Consumer<JSONArray> callbackFn) {
        get(url, (String s) -> {
            callbackFn.accept(parseJsonArray(s));
        });
    }

    public static void postStream(String url, List<Pair<String, String>> params, Consumer<InputStream> callbackFn) {
        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        for (Pair<String, String> p : params)
            requestBodyBuilder.add(p.first, p.second);
        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder()
                .url(getFullUrl(url))
                .header("Authorization", token != null ? "Bearer " + token : "")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback(callbackFn));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void post(String url, List<Pair<String, String>> params, Consumer<String> callbackFn) {
        postStream(url, params, (InputStream stream) -> {
            callbackFn.accept(streamToString(stream));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void postJson(String url, List<Pair<String, String>> params, Consumer<JSONObject> callbackFn) {
        post(url, params, (String s) -> {
            callbackFn.accept(parseJson(s));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void login(String nickname, String password, Consumer<Boolean> callbackFn) {
        postJson("/login", Arrays.asList(
                new Pair<>("nickname", nickname),
                new Pair<>("password", password)
        ), (JSONObject obj) -> {
            if (obj == null) {
                callbackFn.accept(false);
            } else {
                try {
                    token = obj.getString("token");
                    callbackFn.accept(true);
                } catch (JSONException e) {
                    callbackFn.accept(false);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void uploadFile(String url, File file, Consumer<JSONObject> callbackFn) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileqwqwqwq", "filequququq",
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();
        Request request = new Request.Builder()
                .url(getFullUrl(url))
                .header("Authorization", token != null ? "Bearer " + token : "")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new ReqCallback((InputStream stream) -> {
            callbackFn.accept(parseJson(streamToString(stream)));
        }));
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
            imageView.setImageDrawable(null);
            ServerReq.getStream(url, (InputStream stream) -> {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                activity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            });
        }
    }
}
