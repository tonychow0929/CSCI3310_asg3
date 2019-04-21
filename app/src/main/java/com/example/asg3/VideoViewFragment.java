package com.example.asg3;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.widget.MediaController;
import android.view.*;
import android.widget.*;
import android.hardware.*;

import java.util.Collections;

public class VideoViewFragment extends android.support.v4.app.Fragment implements SensorEventListener {
    //private Activity activity;
    private VideoView videoView;
    private MediaController mediaController;
    private ImageView videoImageView;
    private int videoPosition = 1;
    private int itemPosition = 0;
    private boolean isVideoPaused = false;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] accelerometerData, magnetometerData;
    private Display display;
    public VideoViewFragment() {
        super();
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        switch (ev.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                this.accelerometerData = ev.values.clone();
                break;
            }

            case Sensor.TYPE_MAGNETIC_FIELD: {
                this.magnetometerData = ev.values.clone();
                break;
            }

            default:
                break;
        }
        if (this.accelerometerData != null && this.magnetometerData != null) {
            float[] rotationMatrix = new float[9];
            boolean rotationSucceed = SensorManager.getRotationMatrix(rotationMatrix,null, this.accelerometerData, this.magnetometerData);
            if (rotationSucceed) {
                float[] rotationMatrixAdjusted = rotationMatrix.clone();//new float[9];
                switch (display.getRotation()) {
                    case Surface.ROTATION_0:
                        rotationMatrixAdjusted = rotationMatrix.clone();
                        break;

                    case Surface.ROTATION_90:
                        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted);
                        break;

                    case Surface.ROTATION_180:
                        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted);
                        break;

                    case Surface.ROTATION_270:
                        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted);
                        break;
                }
                float[] orientation = new float[3];

                SensorManager.getOrientation(rotationMatrixAdjusted, orientation);
                float azimuth = orientation[0];
                float pitch = orientation[1];
                float roll = orientation[2];
                android.util.Log.e("orientation", azimuth + " " + pitch + " " + roll);
                if (Math.abs(pitch) < Math.PI/3 && Math.abs(roll) < Math.PI/18) {
                    //videoView.start();
                    if (mediaController == null) {
                        mediaController = new MediaController(getContext());
                        mediaController.setMediaPlayer(videoView);// .setEnabled(true);
                        videoView.setMediaController(mediaController);
                        videoView.start();
                    }
                    videoImageView.setImageDrawable(null);
                } else if (Math.abs(pitch) >= Math.PI/3) {
                    videoView.pause();
                    videoView.setMediaController(null);
                    mediaController = null;//.setEnabled(false);
                    videoImageView.setImageDrawable(getResources().getDrawable(R.drawable.pause_40,null));
                } else if (roll < -Math.PI/18) {
                    int newPos = videoView.getCurrentPosition()-500;
                    if (newPos >= 1) {
                        videoView.seekTo(newPos); //videoView.getCurrentPosition()-500);
                    }
                    videoView.setMediaController(null);
                    mediaController = null;//.setEnabled(false);
                    videoImageView.setImageDrawable(getResources().getDrawable(R.drawable.fast_rewind_40, null));
                } else if (roll >= Math.PI/18) {
                    int newPos = videoView.getCurrentPosition() + 500;
                    if (newPos <= videoView.getDuration()) {
                        videoView.seekTo(newPos); //videoView.getCurrentPosition()+500);
                    }
                    videoView.setMediaController(null);
                    mediaController = null;//.setEnabled(false);
                    videoImageView.setImageDrawable(getResources().getDrawable(R.drawable.fast_forward_40, null));
                } else {
                    mediaController.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*public void setActivity(Activity activity) {
        this.activity = activity;
    }*/

    public static VideoViewFragment createVideoViewFragment(int itemPosition) {
        VideoViewFragment frag = new VideoViewFragment();
        //frag.setActivity(activity);
        frag.itemPosition = itemPosition;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedState) {
        View view = inflater.inflate(R.layout.video_view_fragment, container, false);
        //android.util.Log.e("position", itemPosition + "");
        if (savedState != null) {
            this.itemPosition = savedState.getInt("com.example.asg3.ITEM_POSITION", 0);
            this.videoPosition = savedState.getInt("com.example.asg3.VIDEO_POSITION_FOR_ITEM_" + this.itemPosition, 1);
            this.isVideoPaused = savedState.getBoolean("com.example.asg3.VIDEO_IS_PAUSED", false);
        }
        this.videoView = view.findViewById(R.id.video_view);
        this.videoImageView = view.findViewById(R.id.video_image_view);
        if (itemPosition > 0) {
            videoView.setVideoURI(Uri.parse(MainActivity.VIDEO_URL_LIST.get(itemPosition - 1)), Collections.<String, String>emptyMap());
        } else {
            videoView.setVideoURI(Uri.parse("android.resource://com.example.asg3/" + R.raw.soaring_cuhk));
        }
        this.mediaController = new MediaController(getContext());
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController); android.util.Log.e("position",itemPosition + " " + videoPosition+"");
        videoView.seekTo(this.videoPosition);
        videoView.setZOrderMediaOverlay(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //}
        if (!this.isVideoPaused) {
            videoView.start();
        }
        //return view;
        this.sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.display = getActivity().getWindowManager().getDefaultDisplay();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.videoPosition = this.videoView.getCurrentPosition();
        this.isVideoPaused = !(this.videoView.isPlaying());
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if (savedState != null) {
            android.util.Log.e("hello","saving " + this.itemPosition + " " + this.videoView.getCurrentPosition());
            savedState.putInt("com.example.asg3.ITEM_POSITION", this.itemPosition);
            savedState.putInt("com.example.asg3.VIDEO_POSITION_FOR_ITEM_" + this.itemPosition, this.videoPosition);
            savedState.putBoolean("com.example.asg3.VIDEO_IS_PAUSED", this.isVideoPaused);
        }
    }

}