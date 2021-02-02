package com.teamnova.dateset;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Ex2Activity extends LifeCycleActivity {
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex2);

        button2 = findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ExActivity.class);
                startActivity(intent);
            }
        });

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningTaskInfo = activityManager.getRunningTasks(1).get(0);
        topActivityName = runningTaskInfo.topActivity.getClassName();
    }
}