package com.example.mine;

import android.content.Intent;
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

import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
    PopupWindow popupWindowTag;
    PopupWindow popupWindowCollection;
    RecyclerView recyclerView;
    ArrayList<ImageItem> imageItems = new ArrayList<>(0);
    WidthEqualsHeightImageView addImage;
    private static int IMAGE_PICKER = 0;

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

        if(createType.equals("text")) {
            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_text, null);
        } else if(createType.equals("image")) {
            ImagePickerGenerator imagePickerGenerator = new ImagePickerGenerator(9 - imageItems.size());
            ImagePicker imagePicker = imagePickerGenerator.getImagePicker();

            createAreaView = View.inflate(this.getBaseContext(), R.layout.create_area_image, null);
            recyclerView = createAreaView.findViewById(R.id.recyclerview);
            addImage = new WidthEqualsHeightImageView(getApplicationContext());
            addImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addImage.setImageResource(R.drawable.plus);
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
                });
            }
        });

        View popViewCollection = getLayoutInflater().inflate(R.layout.popup_collection, null);
        FrameLayout collectionContainer = popViewCollection.findViewById(R.id.fl_collection);
        collectionContainer.removeAllViews();
        collectionContainer.addView(CollectionListView.inflate(popViewCollection.getContext(), (User.CollectionBrief sel, Boolean init) -> {
            if (init && collection != null) return;
            Log.d("CreateActivity", "selected collection " + sel.title + " (" + sel.id + ")");
            collection = sel;
            if (!init) popupWindowCollection.dismiss();
        }));

        View setCollectionButton = findViewById(R.id.add_collection);
        setCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button finishButton = popViewCollection.findViewById(R.id.create);
                finishButton.setOnClickListener((View v1) -> {
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
        ((Button) findViewById(R.id.post_button)).setOnClickListener((View v) -> {
            if (createType.equals("text")) {
                ServerReq.postJson("/post/new", List.of(
                        new Pair<>("type", "0"),
                        new Pair<>("caption", ((EditText) createAreaView.findViewById(R.id.caption_input)).getText().toString()),
                        new Pair<>("contents", ((EditText) createAreaView.findViewById(R.id.contents_input)).getText().toString()),
                        new Pair<>("collection", String.valueOf(collection.id)),
                        new Pair<>("tags", tags)
                ), (JSONObject obj) -> {
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
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItems.addAll(images);
                recyclerView.setAdapter(new ConcatAdapter(new ImagePickerAdapter(imageItems), new SingleViewAdapter(addImage)));
            }
        }
    }
}