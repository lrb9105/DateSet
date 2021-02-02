package com.teamnova.dateset;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class ExActivity extends LifeCycleActivity {
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex);

        final EditText editText1 = findViewById(R.id.editText1);

        if(savedInstanceState != null){
            CharSequence text = savedInstanceState.getCharSequence("text");
            editText1.setText(text);
        }

        button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Ex2Activity.class);

                //Intent intent = new Intent("com.teamnova.dateset.VIEW_ACTIVITY");
                startActivity(intent);

            }
        });

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningTaskInfo = activityManager.getRunningTasks(1).get(0);
        topActivityName = runningTaskInfo.topActivity.getClassName();

        Log.d(topActivityName + "debug_onCreate","onCreate");

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(topActivityName + "debug_onRestoreInstanceState2","onRestoreInstanceState2");

        final EditText editText1 = findViewById(R.id.editText1);
        CharSequence text = savedInstanceState.getCharSequence("text");
        editText1.setText(text);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        final EditText editText1 = findViewById(R.id.editText1);
        CharSequence text = editText1.getText();
        outState.putCharSequence("text",text);

        Log.d(topActivityName + "debug_onSaveInstanceState","onSaveInstanceState");
    }
}