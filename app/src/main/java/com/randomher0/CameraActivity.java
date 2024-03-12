package com.randomher0;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.randomher0.ui.main.SectionsPagerAdapter;
//import com.randomher0.databinding.ActivityMain2Binding;

public class CameraActivity extends AppCompatActivity {

//    private ActivityMain2Binding binding;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMain2Binding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = binding.viewPager;
//        viewPager.setAdapter(sectionsPagerAdapter);
//        TabLayout tabs = binding.tabs;
//        tabs.setupWithViewPager(viewPager);
//        FloatingActionButton fab = binding.fab;
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }

//    private void readParams() {
//        SharedPreferences sharedPref = getSharedPreferences(
////                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
////        SharedPreferences.Editor.getString("ssid");
////        SharedPreferences.Editor.getString("password");
////        SharedPreferences.Editor.getInt()
////        SharedPreferences.Editor.getBoolean()
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        VideoView videoView = findViewById(R.id.idVideoView);

        MediaController mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.globe);

        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);

        videoView.requestFocus();
        // on below line we are calling start  method to start our video view.
        videoView.start();
    }

//    private void createCameraPreview() {
//
//    }
}