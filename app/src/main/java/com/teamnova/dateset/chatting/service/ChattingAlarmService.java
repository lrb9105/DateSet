package com.teamnova.dateset.chatting.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.SPManager;


public class ChattingAlarmService extends Service {
    private int index;
    private String key = HomeActivity.userInfo.getSharedKey();
    private Handler handler;
    private NotificationCompat.Builder notificationBuilder;
    private String channelId = "1111";
    private String title;
    private String contents;
    private Intent intent;
    private PendingIntent pendingIntent;
    private Bitmap largeIcon;
    private NotificationManagerCompat notificationManager;
    private String userId;
    //private ChattingListeningThread thread;
    private DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("chattings/"+key);


    public ChattingAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        intent = new Intent(this, ChattingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IS_OPENED_BY_ALARM","TRUE");
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        super.onCreate();
        Log.d("debug_destroy","서비스 생성");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug_destroy","서비스 생성2");

        userId = intent.getStringExtra("ID");
        notificationManager = NotificationManagerCompat.from(ChattingAlarmService.this);

        // 알림 채널 등록
        this.createNotificationChannel();

        Log.d("debug_serviceStart","서비스 시작");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChattingDto chattingInfo = snapshot.getValue(ChattingDto.class);

                if(!chattingInfo.getFromId().equals(userId)){
                    Log.d("debug_chatting1",chattingInfo.getFromId());
                    Log.d("debug_chatting2",userId);

                    title = chattingInfo.getFromNickName();
                    contents = chattingInfo.getContents();

                    notificationBuilder = new NotificationCompat.Builder(ChattingAlarmService.this,channelId)
                            .setSmallIcon(R.drawable.common_x_icon_24)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(title)
                            .setContentText(contents)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(contents))
                            //.setFullScreenIntent(pendingIntent,true)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_SOUND);

                    notificationManager.notify(index, notificationBuilder.build());
                    index++;
                }

                /*Message msg = handler.obtainMessage();
                msg.obj = chattingInfo;
                msg.what = 0;

                handler.sendMessage(msg);*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDbRef.addChildEventListener(childEventListener);


        // 알림발생 핸들러
        /*handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ChattingDto chattingInfo = (ChattingDto)msg.obj;

                // 메세지를 보낸 사람이 현재 ID와 다른 경우에만 알람 보내기
                if(!chattingInfo.getFromId().equals(userId)){
                    Log.d("debug_chatting1",chattingInfo.getFromId());
                    Log.d("debug_chatting2",userId);

                    title = chattingInfo.getFromNickName();
                    contents = chattingInfo.getContents();

                    notificationBuilder = new NotificationCompat.Builder(ChattingAlarmService.this,channelId)
                            .setSmallIcon(R.drawable.common_x_icon_24)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(title)
                            .setContentText(contents)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(contents))
                            //.setFullScreenIntent(pendingIntent,true)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setDefaults(Notification.DEFAULT_SOUND);

                    notificationManager.notify(index, notificationBuilder.build());
                    index++;
                }
            }
        };*/

/*        thread = new ChattingListeningThread(handler);

        thread.start();*/

        return START_NOT_STICKY;
    }

    // notification채널 생성
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_chatting);
            String description = getString(R.string.channel_description_chatting);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //thread.setRunning(false);
        Log.d("debug_destroy","서비스 파괴");
    }

 /*   class ChattingListeningThread extends Thread{
        private Handler handler;
        private DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("chattings/"+key);
        private ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChattingDto chattingInfo = snapshot.getValue(ChattingDto.class);

                Message msg = handler.obtainMessage();
                msg.obj = chattingInfo;
                msg.what = 0;

                handler.sendMessage(msg);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        public ChattingListeningThread(Handler handler){
            this.handler  = handler;
        }

        private boolean isRunning = true;

        @Override
        public void run() {
            mDbRef.addChildEventListener(childEventListener);
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }
    }*/
}