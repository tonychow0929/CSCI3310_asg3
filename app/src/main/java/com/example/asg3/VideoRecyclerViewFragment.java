package com.example.asg3;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.*;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.*;

import java.util.*;
import java.io.*;
import java.net.*;

import com.example.asg3.R;
import com.example.asg3.VideoRecyclerViewAdapter;

import java.util.Collections;

public class VideoRecyclerViewFragment extends android.support.v4.app.Fragment {
    private Activity activity;
    public VideoRecyclerViewFragment() {
        super();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static com.example.asg3.VideoRecyclerViewFragment createNewVideoRecyclerViewFragment(Activity activity) {
        com.example.asg3.VideoRecyclerViewFragment frag = new com.example.asg3.VideoRecyclerViewFragment();
        frag.setActivity(activity);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        RecyclerView videoRecyclerView = view.findViewById(R.id.video_recycler_view);
        this.activity = getActivity();
        //android.util.Log.e("activity",this.activity.toString());
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        videoRecyclerView.setAdapter(new VideoRecyclerViewAdapter(activity));
        return view;
    }

    //void applyFragmentTransaction(int itemPosition) {}
}

class VideoRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected Activity activity;
    protected ImageView itemImageView;
    protected TextView itemTextView;
    protected int position;
    public VideoRecyclerViewHolder(Activity activity, View itemView, VideoRecyclerViewAdapter adapter) {
        super(itemView);
        this.activity = activity;
        this.itemImageView = itemView.findViewById(R.id.item_image_view);
        this.itemTextView = itemView.findViewById(R.id.item_text_view);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        /*DisplayMetrics metrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (Math.pow(metrics.widthPixels / metrics.xdpi, 2) + Math.pow(metrics.heightPixels / metrics.ydpi, 2) <= 6.5*6.5) {*/
        if (MainActivity.isTablet) {
            android.support.v4.app.FragmentManager fragmentManager = ((android.support.v7.app.AppCompatActivity)(this.activity)).getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment oldFragment = fragmentManager.findFragmentByTag("tablet_current_video");
            transaction.remove(oldFragment).replace(R.id.video_fragment_container, VideoViewFragment.createVideoViewFragment(this.position), "tablet_current_video").addToBackStack(null).commit();
        } else {
            Intent intent = new Intent(this.activity, VideoViewActivity.class);
            intent.putExtra("com.example.asg3.VIDEO_ITEM_POSITION", this.position);
            activity.startActivity(intent);
        }
    }
}

class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewHolder> {
    private Activity activity;
    private LayoutInflater layoutInflater;
    //private int position;
    public VideoRecyclerViewAdapter(Activity activity) {
        super();
        this.activity = activity;// = context;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override @NonNull
    public VideoRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View itemView = layoutInflater.inflate(R.layout.video_item, parent, false);
        return new VideoRecyclerViewHolder(activity, itemView, VideoRecyclerViewAdapter.this);
    }


    @Override
    public void onBindViewHolder(VideoRecyclerViewHolder holder, int position) {
        //android.util.Log.e("Position: " + position, "Hello?");
        holder.position = position;
        if (position > 0) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                String url = android.net.Uri.parse(MainActivity.VIDEO_URL_LIST.get(position-1)).toString();
                android.util.Log.e("url",url);
                retriever.setDataSource(url, new HashMap<String, String>());
                Bitmap frameAt3000 = retriever.getScaledFrameAtTime(3_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 300, 225);
                holder.itemImageView.setImageBitmap(frameAt3000);
                retriever.release();
            } catch (Exception ex) {
                android.util.Log.wtf("Cannot load video", ex);
            }
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            AssetFileDescriptor fd = holder.itemImageView.getResources().openRawResourceFd(R.raw.soaring_cuhk);
            retriever.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            Bitmap frameAt3000 = retriever.getScaledFrameAtTime(3_000_000, MediaMetadataRetriever.OPTION_CLOSEST,300,225);
            holder.itemImageView.setImageBitmap(frameAt3000);
            retriever.release();
        } //android.util.Log.e("Hi " + position,"er ");
        holder.itemTextView.setText(MainActivity.VIDEO_TITLE.get(position));
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

