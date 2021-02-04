package com.teamnova.dateset.chatting.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamnova.dateset.MainActivity;
import com.teamnova.dateset.R;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.TokenDto;
import com.teamnova.dateset.home.HomeActivity;

import static com.teamnova.dateset.home.HomeActivity.sharedKeyDto;
import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private String key = userInfo.getSharedKey();
    private int index;
    String channelId = "333333";

    public MyFirebaseMessagingService() {
    }


    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("debug_msg","1111");

        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    // 토큰이 새로 생성되면 호출된다.
    @Override
    public void onNewToken(String token) {
        Log.d("debug_Token", "Refreshed token: " + token);

    }

    // FCM으로부터 메시지를 수신했을 떄 노티를 보내는 코드
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ChattingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                0);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.chatting)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.chatting))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        //.setFullScreenIntent(pendingIntent,true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "fcm_default_channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(index /* ID of notification */, notificationBuilder.build());
        index++;
    }
}