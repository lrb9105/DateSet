package com.teamnova.dateset.chatting.chatting;
import android.app.ActivityManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.ChattingAdapter;
import com.teamnova.dateset.chatting.service.ChattingAlarmService;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.TokenDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.teamnova.dateset.home.HomeActivity.sharedKeyDto;
import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class ChattingActivity extends AppCompatActivity {
    // 데이터를 저장할 때 사용할 키값 - 나와 연결된 상대는 동일한 키값을 갖는다.
    private final String key = userInfo.getSharedKey();

    // db참조 객체
    private DatabaseReference mDatabaseRef; // Firebase db에서 읽고 쓰기를 하기위한 db참조 객체

    // 컴포넌트
    private EditText editText_chatting_writing;
    private Button btn_write;
    private ImageButton btn_back;

    //리사이클러뷰
    private RecyclerView recyclerView_chat_area;
    private ChattingAdapter recyclerAdapter;
    private ArrayList<ChattingDto> chattingList;
    private RecyclerView.LayoutManager layoutManager;

    //이벤트 리스너
    private ChildEventListener childEventListener;

    // 전화걸기
    private ImageButton btn_call;
    private boolean isCallClicked = false;

    private boolean isOpenedByAlarm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        Intent intent = getIntent();
        if(intent.getStringExtra("IS_OPENED_BY_ALARM") != null){
            Log.d("debug_intent","111");
            if(intent.getStringExtra("IS_OPENED_BY_ALARM").equals("TRUE")) {
                Log.d("debug_intent",intent.getStringExtra("IS_OPENED_BY_ALARM"));
                isOpenedByAlarm = true;
            }
        }

        Log.d("debug_chatting_onCreate","1111");

        /*Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");*/

        editText_chatting_writing = findViewById(R.id.editText_chatting_writing);

/*        // focus를 받았을 때 리사이클러뷰의 가장 마지막인덱스로 이동한다.
        editText_chatting_writing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    int lastPosition = recyclerAdapter.getItemCount()-1;
                    if(lastPosition >= 0){
                        recyclerView_chat_area.scrollToPosition(lastPosition);
                    }
                }
            }
        });

        // 엔터키가 눌리면 마지막 포커스로 이동
        editText_chatting_writing.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        int lastPosition = recyclerAdapter.getItemCount()-1;
                        if(lastPosition >= 0){
                            recyclerView_chat_area.scrollToPosition(lastPosition);
                        }
                }
                return true;
            }
        });*/

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundColor(Color.LTGRAY);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        btn_write = findViewById(R.id.btn_write);

        // db 참조객체 가져오기
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("chattings/"+key);

        editText_chatting_writing = findViewById(R.id.editText_chatting_writing);
        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = "";

                if(editText_chatting_writing.getText() != null){
                    contents = editText_chatting_writing.getText().toString();
                }
                if(!contents.equals("")){
                    String fromNickName = userInfo.getNickName();
                    writeChattingMsg(fromNickName, contents);
                }
            }
        });

        btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 usre1에 들어있으므로 user2의 전화번호를 가져와야 한다.
                if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
                    String phoneNum = "";

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                UserDto userInfo = dataSnapshot.getValue(UserDto.class);
                                if(userInfo.getId().equals(sharedKeyDto.getUser2Id())){
                                    isCallClicked = true;
                                    Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.getPhoneNum()));
                                    startActivity(tt);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else { // 사용자가 usre2에 들어있으므로 user1의 전화번호를 가져와야 한다.
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                UserDto userInfo = dataSnapshot.getValue(UserDto.class);
                                if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                                    isCallClicked = true;
                                    Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.getPhoneNum()));
                                    startActivity(tt);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        // 채팅정보를 저장할 리스트, 채팅정보를 뿌려줄 리사이클러뷰
        chattingList = new ArrayList<>();
        recyclerView_chat_area = findViewById(R.id.recyclerView_chat_area);

        // 레이아웃 매니저
        layoutManager = new LinearLayoutManager(this);

        // 리사이클러뷰 세팅
        recyclerView_chat_area.setLayoutManager(layoutManager);
        recyclerAdapter = new ChattingAdapter(chattingList,userInfo.getId(), this);
        recyclerView_chat_area.setAdapter(recyclerAdapter);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 전화 버튼 클릭 시 isCallClicked값이 true로 바뀜, 따라서 눌리지 않은경우(false)만 리사이클러뷰에 추가되도록.
                ChattingDto chatInfo = snapshot.getValue(ChattingDto.class);

                recyclerAdapter.addChatInfo(chatInfo);
                int lastPosition = recyclerAdapter.getItemCount()-1;
                recyclerView_chat_area.scrollToPosition(lastPosition);
                //recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("debug_",error.getMessage());
            }
        };

        mDatabaseRef.addChildEventListener(childEventListener);
    }

    private void writeChattingMsg(String fromNickName, String contents) {
        //날짜
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy년 MM월 dd일");
        SimpleDateFormat format2 = new SimpleDateFormat( "a hh:mm");
        Date today = new Date();
        String date = format1.format(today);
        String time = format2.format(today);

        String myId = userInfo.getId();
        String oppoId = null;

        // 내가 user1이라면
        if(sharedKeyDto.getUser1Id().equals(myId)){
            oppoId = sharedKeyDto.getUser2Id();
        } else{
            oppoId = sharedKeyDto.getUser1Id();
        }

        ChattingDto chattingDto = new ChattingDto(myId,oppoId,fromNickName,contents,date,time);
        //mDatabaseRef.child(UUID.randomUUID().toString()).setValue(chattingDto);
        mDatabaseRef.push().setValue(chattingDto);
        editText_chatting_writing.setText("");

        // push 보내기
        DatabaseReference mDbRefToken = FirebaseDatabase.getInstance().getReference("token/"+key);
        mDbRefToken.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TokenDto tokenDto = snapshot.getValue(TokenDto.class);
                String mPushToken = null;
                // 내가 user1이라면
                if(sharedKeyDto.getUser1Id().equals(myId)){
                    mPushToken = tokenDto.getUser2Token();
                } else{
                    mPushToken = tokenDto.getUser1Token();
                }
                Log.d("debug_push_token",mPushToken);

                // This registration token comes from the client FCM SDKs.
                String registrationToken = "YOUR_REGISTRATION_TOKEN";

// See documentation on defining a message payload.
                /*Message message = Message.builder()
                        .putData("score", "850")
                        .putData("time", "2:45")
                        .setToken(registrationToken)
                        .build();

// Send a message to the device corresponding to the provided
// registration token.
                String response = FirebaseMessaging.getInstance().send(message);*/
// Response is a message ID string.
                //System.out.println("Successfully sent message: " + response);

                SendNotification.sendNotification(mPushToken, fromNickName, contents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Intent intentForService  = new Intent(this, ChattingAlarmService.class);
        stopService(intentForService);*/
        Log.d("debug_service","서비스 중지");
        //mDatabaseRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*Intent intentForService  = new Intent(this, ChattingAlarmService.class);
        intentForService.putExtra("ID",userInfo.getId());
        stopService(intentForService);*/
        Log.d("debug_service","서비스 시작");
        //mDatabaseRef.removeEventListener(childEventListener);
    }

/*    @Override
    public void onBackPressed() {
        if(isOpenedByAlarm){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            Log.d("debug_Alarm","11111");
        } else{
            Log.d("debug_Normal","22222");
            //onBackPressed();
        }
    }*/
}