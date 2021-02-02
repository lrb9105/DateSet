package com.teamnova.dateset.addedfunc.calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.chatting.service.ChattingAlarmService;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.home.HomeActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private int index;
    private NotificationCompat.Builder notificationBuilder;
    private String channelId = "2222";
    private String title;
    private String contents;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        this.context = context;

        // 일정 혹은 기념일 데이터
        ScheduleDto dtoSch = (ScheduleDto)intent.getExtras().getSerializable("SCHEDULE_DTO");
        AnniversaryDto dtoAnniversary = (AnniversaryDto)intent.getExtras().getSerializable(("ANNIVERSARY_DTO"));

        Log.d("debug_dto", "" + intent.getExtras());
        Log.d("debug_dto", "" + intent.hasExtra("TITLE"));

        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            Log.d("debug_dto",intent.getExtras().getString("TITLE"));
            Log.d("debug_dto",intent.getExtras().getString("CONTENTS"));
        }


        // Notification 제목 및 내용 지정
        title = intent.getExtras().getString("TITLE");
        contents = intent.getExtras().getString("CONTENTS");

        intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        notificationManager = NotificationManagerCompat.from(context);

        // 알림 채널 등록
        this.createNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.calendar)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.calendar))
                .setContentTitle(title)
                .setContentText(contents)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contents))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND);

        notificationManager.notify(index, notificationBuilder.build());
        index++;
    }

    // notification채널 생성
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "캘린더 알람";
            String description = "캘린더 알람";;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}