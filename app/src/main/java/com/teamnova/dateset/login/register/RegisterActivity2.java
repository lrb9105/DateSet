package com.teamnova.dateset.login.register;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.enum_cls.RegisterDenyReason;
import com.teamnova.dateset.login.login.LoginActivity;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.ProgressDialog;
import com.teamnova.dateset.util.SPManager;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity2 extends AppCompatActivity implements View.OnClickListener{
    // 상대방 id
    private EditText editText_opp_id;
    //회원가입 완료 버튼
    private Button btn_registerComplete;
    // 뒤로가기 버튼
    private ImageButton btn_back;
    // 거절이유
    private String denyReason = null;
    private int index;
    // 로딩바
    private ProgressDialog customProgressDialog;
    // 유저정보
    private UserDto myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        this.initialize();
    }

    private void initialize(){
        // 상대방 id
        editText_opp_id = findViewById(R.id.editText_opp_id);

        //다음 버튼
         btn_registerComplete = findViewById(R.id.btn_registerComplete);

         //리스너 등록
        this.setListener();

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

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void setListener(){
        btn_registerComplete.setOnClickListener(this);
    }

    // 상대방 아이디가 존재하지 않는지 확인
    public boolean validation() {
        denyReason = null;
        index = 0;

        boolean isPossibleRegister = true;
        String opponentId = editText_opp_id.getText().toString();

        if(opponentId.equals("")){
            denyReason = RegisterDenyReason.ID_NOT_ENTER.getDenyReason();
        }

        // 상대방 아이디 파이어베이스에서 가져오는 로직 추가
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users");

        Query q = dRef.orderByKey();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDto opponenetInfo = null;

                Log.d("debug_aaa","1111111");

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    opponenetInfo = dataSnapshot.getValue(UserDto.class);
                    // 입력된 아이디가 있다면 for문 나가기

                    Log.d("debug_entered_id",opponenetInfo.getId());
                    Log.d("debug_opponent_id",opponentId);

                    if(opponenetInfo.getId().equals(opponentId)){
                        break;
                    }else{
                        index++;
                    }
                }


                Log.d("debug_index","" + index);
                Log.d("debug_size","" + snapshot.getChildrenCount());

                // 즉, 해당 아이디 존재
                if(index != snapshot.getChildrenCount()){
                    myInfo = (UserDto)getIntent().getSerializableExtra("USER_INFO");

                    // 공유키가 존재하지 않는다면
                    // 공유키 생성 -> 내id, 상대방id 저장
                    if(opponenetInfo.getSharedKey() == null){
                        DatabaseReference mdbRefSharedKey = FirebaseDatabase.getInstance().getReference("sharedKey").push();
                        DatabaseReference mdbRef = null;

                        // 공유키 아이디 생성
                        String sharedKeyId = mdbRefSharedKey.getKey();

                        // 내정보 업데이트(공유키 저장)
                        String myFbKey = myInfo.getFbKey();
                        mdbRef = FirebaseDatabase.getInstance().getReference("users").child(myFbKey);
                        myInfo.setSharedKey(sharedKeyId);

                        mdbRef.setValue(myInfo);
                        
                        // 상대방정보 업데이트(공유키 저장)
                        String oppoFbkey = opponenetInfo.getFbKey();
                        mdbRef = FirebaseDatabase.getInstance().getReference("users").child(oppoFbkey);
                        opponenetInfo.setSharedKey(sharedKeyId);
                        mdbRef.setValue(opponenetInfo);

                        // 공유키 저장
                        SharedKeyDto sharedKey = new SharedKeyDto(sharedKeyId, myInfo.getId(), opponentId);

                        mdbRefSharedKey.setValue(sharedKey);
                    }

                    //로딩바 종료
                    customProgressDialog.dismiss();

                    // sp에 해당 아이디의 파일 생성
                    SPManager spManager = new SPManager(RegisterActivity2.this, "com.teamnova.dateset.user."+myInfo.getId(), MODE_PRIVATE);
                    spManager.setCompleteStep(2);

                    // 다이얼로그 메시지 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity2.this);

                    builder.setTitle("상대방정보 연결 완료").setMessage("다음으로 넘어갑니다.");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent intent = new Intent(getApplicationContext(), RegisterActivity3.class);
                            intent.putExtra("USER_INFO",myInfo);

                            startActivity(intent);
                            finish();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{ //해당 아이디 없음
                    //로딩바 종료
                    customProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity2.this, "입력하신 아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                q.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        q.addListenerForSingleValueEvent(listener);

        if(denyReason != null){
            Toast.makeText(this, denyReason, Toast.LENGTH_LONG).show();
            isPossibleRegister = false;
        }

        return isPossibleRegister;
    }

    /*public UserDto createUserInfo(){

    }*/

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.btn_registerComplete: // 회원가입 완료
                //로딩바 시작
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);
                if(!validation()){
                    return;
                }
                break;
            default:
                break;
        }
    }
}