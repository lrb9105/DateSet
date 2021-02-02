package com.teamnova.dateset;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ExThread extends Thread{
    Context context;

    public ExThread(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        while(true){
            new Handler().post(new Runnable() {
                int count = 1;
                @Override
                public void run() {
                    Toast.makeText(context, "" + count, Toast.LENGTH_LONG).show();
                    count++;
                }
            });
            Looper.loop();
        }
        //Toast.makeText(this.context, "기본 토스트 메시지", Toast.LENGTH_SHORT).show();
    }
}
