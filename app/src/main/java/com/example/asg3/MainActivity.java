package com.example.asg3;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.media.*;
import android.graphics.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    public static final String LOCAL_VIDEO_NAME = "soaring_cuhk.mp4";
    public static final List<String> VIDEO_URL_LIST = new ArrayList<>(4);
    public static final List<String> VIDEO_TITLE = Arrays.asList("Soaring\nCUHK", "Humble\nCottage", "Green\nBuilding\nAwards", "Space and\nEarth", "InfraRed\nSpots");
    public static boolean isTablet = false;
    static {
        VIDEO_URL_LIST.add("http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/humble_cottage_cuhk.mp4");
        VIDEO_URL_LIST.add("http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/green_bldg_cuhk.mp4");
        VIDEO_URL_LIST.add("http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/connecting_space_cuhk.mp4");
        VIDEO_URL_LIST.add("http://course.cse.cuhk.edu.hk/~csci3310/1819R2/asg3/infrared_cuhk.mp4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //android.util.Log.e("hello?", "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.util.Log.e("display size",metrics.widthPixels+"");isTablet = metrics.widthPixels >= 600 * metrics.density; //, 2) + Math.pow(metrics.heightPixels / metrics.ydpi, 2) > 6.5*6.5;
        */
        isTablet = (findViewById(R.id.video_fragment_container) != null);
        if (isTablet) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.recycler_view_fragment_container, VideoRecyclerViewFragment.createNewVideoRecyclerViewFragment(this)).commit();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.video_fragment_container, VideoViewFragment.createVideoViewFragment(0)).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.recycler_view_fragment_container, VideoRecyclerViewFragment.createNewVideoRecyclerViewFragment(this)).commit();
        }
    }
}


