package com.example.mine;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CreateActivity extends AppCompatActivity {
    PopupWindow popupWindowTag;
    PopupWindow popupWindowCollection;
    RecyclerView recyclerView;
    ArrayList<ImageItem> imageItems = new ArrayList<>(0);
    WidthEqualsHeightImageView addImage;
    ImageButton addMediaButton;
    Uri singleMediaUri;
    private static int IMAGE_PICKER = 0;
    private static int VIDEO_PICKER = 2;

    private String tags = "";
    private User.CollectionBrief collection = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create);
        final View createAreaView;
        View cv = getWindow().getDecorView();
        ViewGroup area = cv.findViewById(R.id.create_area);
        Intent intent = getIntent();
        String createType = intent.getStringExtra("create_type");

        ImageView collectionIcon = findViewById(R.id.collection_icon);
        ImageView tagIcon = findViewById(R.id.tag_icon);
        collectionIcon.setColorFilter(Color.parseColor("#BBBBBB"));
        tagIcon.setColorFilter(Color.parseColor("#BBBBBB"));

        if(createType.equals("text")) {
            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_text, null);
        } else if(createType.equals("image")) {
            ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(9 - imageItems.size());
            ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_image, null);
            recyclerView = createAreaView.findViewById(R.id.recyclerview);
            addImage = new WidthEqualsHeightImageView(getApplicationContext());
            addImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addImage.setImageResource(R.mipmap.ic_add_white_48dp);
            addImage.setColorFilter(Color.parseColor("#CCCCCC"));
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
        } else if (createType.equals("video") || createType.equals("audio")) {
            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_video, null);
            addMediaButton = (ImageButton) createAreaView.findViewById(R.id.btn_select);
            addMediaButton.setOnClickListener((View v) -> {
                Intent videoIntent = new Intent();
                videoIntent.setType(createType + "/*");
                videoIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(videoIntent, "Select Media"), VIDEO_PICKER);
            });
        } else {
            createAreaView = null;
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
                    CreateActivity.this.runOnUiThread(() -> ((TextView) cv.findViewById(R.id.tag)).setText(tags));
                });
            }
        });
        ((TextView) cv.findViewById(R.id.tag)).setText("");

        View popViewCollection = getLayoutInflater().inflate(R.layout.popup_collection, null);
        FrameLayout collectionContainer = popViewCollection.findViewById(R.id.fl_collection);
        collectionContainer.removeAllViews();
        collectionContainer.addView(CollectionListView.inflate(popViewCollection.getContext(), (User.CollectionBrief sel, Boolean init) -> {
            if (init && collection != null) return;
            Log.d("CreateActivity", "selected collection " + sel.title + " (" + sel.id + ")");
            collection = sel;
            CreateActivity.this.runOnUiThread(() -> ((TextView) cv.findViewById(R.id.collection)).setText(sel.title));
            if (!init) popupWindowCollection.dismiss();
        }));

        View setCollectionButton = findViewById(R.id.add_collection);
        setCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button createButton = popViewCollection.findViewById(R.id.create);
                createButton.setOnClickListener((View v1) -> {
                    Intent intentToCreate = new Intent();
                    intentToCreate.setClass(v1.getContext(), CollectionSettingActivity.class);
                    v1.getContext().startActivity(intentToCreate);
                });
                popupWindowCollection = new PopupWindow(popViewCollection, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindowCollection.setOutsideTouchable(true);
                popupWindowCollection.setFocusable(true);
                popupWindowCollection.showAtLocation(cv, Gravity.BOTTOM, 0, 0);
                popupWindowCollection.setOnDismissListener(() -> {
                });
            }
        });

        // Post button
        Consumer<JSONObject> goToCreatedPost = (JSONObject obj) -> {
            Log.d("CreateActivity", obj.toString());
            int id = -1;
            try {
                id = obj.getInt("id");
            } catch (JSONException e) {
                Log.e("CreateActivity", e.toString());
                return;
            }
            Intent intentOut = new Intent(this, LoadingActivity.class);
            intentOut.putExtra("type", LoadingActivity.DestType.post);
            intentOut.putExtra("id", id);
            intentOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentOut);
            this.finish();
        };
        ((Button) findViewById(R.id.post_button)).setOnClickListener((View v) -> {
            v.setEnabled(false);
            if (createType.equals("text")) {
                ServerReq.postJson("/post/new", List.of(
                        new Pair<>("type", "0"),
                        new Pair<>("caption", ((EditText) createAreaView.findViewById(R.id.caption_input)).getText().toString()),
                        new Pair<>("contents", ((EditText) createAreaView.findViewById(R.id.contents_input)).getText().toString()),
                        new Pair<>("collection", String.valueOf(collection.id)),
                        new Pair<>("tags", tags)
                ), goToCreatedPost);
            } else if (createType.equals("image")) {
                String[] ids = new String[imageItems.size()];
                double[] progress = new double[imageItems.size()];
                int i = 0;
                for (ImageItem item : imageItems) {
                    int j = i++;
                    ServerReq.uploadFile("/upload", new File(item.path), (Long len, Long sent) -> {
                        progress[j] = (double)sent / len;
                        double totalProgress = 0;
                        for (double p : progress) totalProgress += p;
                        totalProgress /= progress.length;
                        Log.d("CreateActivity", "image upload progress = " + totalProgress);
                        ((ProgressBar) cv.findViewById(R.id.progress)).setMax(1000);
                        ((ProgressBar) cv.findViewById(R.id.progress)).setProgress((int) Math.round(1000 * totalProgress), true);
                    }, (JSONObject obj) -> {
                        try {
                            ids[j] = obj.getJSONArray("ids").getString(0);
                        } catch (JSONException e) {
                            Log.e("CreateActivity", e.toString());
                        }
                        Log.d("CreateActivity", "upload " + j + " = " + ids[j]);
                        // Check: is ids full?
                        boolean full = true;
                        for (String s : ids)
                            if (s == null) {
                                full = false;
                                break;
                            }
                        if (full) {
                            StringBuilder idsJoined = new StringBuilder();
                            for (String s : ids) {
                                if (idsJoined.length() != 0) idsJoined.append(" ");
                                idsJoined.append(s);
                            }
                            ServerReq.postJson("/post/new", List.of(
                                    new Pair<>("type", "1"),
                                    new Pair<>("caption", ((EditText) createAreaView.findViewById(R.id.caption_input)).getText().toString()),
                                    new Pair<>("contents", idsJoined.toString()),
                                    new Pair<>("collection", String.valueOf(collection.id)),
                                    new Pair<>("tags", tags)
                            ), goToCreatedPost);
                        }
                    });
                }
            } else if (createType.equals("video") || createType.equals("audio")) {
                Log.d("CreateActivity", "media path: " + singleMediaUri);
                InputStream istr;
                try {
                    istr = getContentResolver().openInputStream(singleMediaUri);
                } catch (Exception e) {
                    Log.e("CreateActivity", e.toString());
                    return;
                }
                // ServerReq.uploadFile("/upload", new File(ContentProviderUtils.getPath(getBaseContext(), singleMediaUri)), (Long len, Long sent) -> {
                ServerReq.uploadFileStream("/upload", istr, (Long len, Long sent) -> {
                    double progress = (double)sent / len;
                    // Log.d("CreateActivity", "video upload progress = " + progress);
                    ((ProgressBar) cv.findViewById(R.id.progress)).setMax(1000);
                    ((ProgressBar) cv.findViewById(R.id.progress)).setProgress((int) Math.round(1000 * progress), true);
                }, (JSONObject obj) -> {
                    String id = "";
                    try {
                        id = obj.getJSONArray("ids").getString(0);
                    } catch (JSONException e) {
                        Log.e("CreateActivity", e.toString());
                    }
                    Log.d("CreateActivity", "upload id = " + id);
                    ServerReq.postJson("/post/new", List.of(
                            new Pair<>("type", createType.equals("video") ? "3" : "2"),
                            new Pair<>("caption", ((EditText) createAreaView.findViewById(R.id.caption_input)).getText().toString()),
                            new Pair<>("contents", id),
                            new Pair<>("collection", String.valueOf(collection.id)),
                            new Pair<>("tags", tags)
                    ), goToCreatedPost);
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER) {
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS && data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.addAll(images);
                recyclerView.setAdapter(new ConcatAdapter(new ImagePickerAdapter(imageItems), new SingleViewAdapter(addImage)));
            }
        } else if (requestCode == VIDEO_PICKER) {
            if (resultCode == RESULT_OK) {
                singleMediaUri = data.getData();
                Log.d("CreateAcvitity", singleMediaUri.toString());
                addMediaButton.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.video_view,
                        new VideoPlayerFragment(singleMediaUri)).commit();
            }
        }
    }

    private static class ContentProviderUtils {
        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @author paulburke
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }
}