package com.example.asg3;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.*;
import android.widget.*;
import android.net.*;
import android.content.*;
import android.media.*;
import android.graphics.*;

import java.util.*;

public class VideoViewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.video_view_activity);
        Intent intent = getIntent();
        int itemPosition = 0;
        if (intent != null) {
            itemPosition = intent.getIntExtra("com.example.asg3.VIDEO_ITEM_POSITION", 0);
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(itemPosition));
        if (fragment == null) {
            fragment = VideoViewFragment.createVideoViewFragment(itemPosition);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.video_view_activity_fragment_container, fragment, String.valueOf(itemPosition)).commit();
        //}
    }
}
