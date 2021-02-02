package com.teamnova.dateset;

import android.app.ActivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LifeCycleActivity extends AppCompatActivity {
    ActivityManager activityManager;
    ActivityManager.RunningTaskInfo runningTaskInfo;
    String topActivityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(topActivityName + "debug_onStart", "onStart");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(topActivityName + "debug_onRestoreInstanceState1","onRestoreInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(topActivityName + "debug_onResume", "onResume");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(topActivityName + "debug_onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(topActivityName + "debug_onStop", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(topActivityName + "debug_onDestroy", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(topActivityName + "debug_onRestart", "onRestart");
    }
}