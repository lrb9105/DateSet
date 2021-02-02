package com.teamnova.dateset.login.register;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.enum_cls.RegisterDenyReason;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.login.login.LoginActivity;
import com.teamnova.dateset.util.BitmapConverter;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.ProgressDialog;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity3 extends AppCompatActivity implements View.OnClickListener{
    // 프로필사진 저장 위해 필요한 값들
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    private final int REQUEST_WIDTH = 512;
    private final int REQUEST_HEIGHT = 512;

    private String mCurrentPhotoPath;
    
    // user프로필사진
    private CircleImageView user_profile;

    //이름, 닉네임
    private EditText editText_name;
    private EditText editText_nickName;
    //전화번호 인증
    private EditText editText_phoneNum;
    private Button btn_certification;
    // 인증번호 확인
    private EditText editText_confirm;
    private Button btn_confirm;
    //회원가입 완료 버튼
    private Button btn_registerComplete;
    // 뒤로가기 버튼
    private ImageButton btn_back;
    // 타이머
    private LinearLayout layout_timer;
    private TextView textView_timer_remain;
    private TextView textView_timer;
    // 레이아웃
    private LinearLayout layout_register;
    // 처음 사귄날
    private TextView textview_first_day;

    //타이머
    private Handler handler;
    private int minute;
    private int sec;
    private TimerThread t;

    // 시작날짜
    private int firstDayYear;
    private int firstDayMonth;
    private int firstDayDay;

    // 개인정보 처리방침
    private TextView textView_private_info;
    private CheckBox checkbox_private_info;

    // 객체
    private RegisterChecker registerChecker;
    private String phoneNum;

    // db참조 객체
    private DatabaseReference mDbRefOfSharedKey;

    // Storage 객체
    private StorageReference mStorageRef;

    // 프로필
    private Bitmap bitmap;
    // uploadTask
    private UploadTask uploadTask;

    // 스토리지
    private StorageReference ref;

    //유저정보
    private UserDto userInfo;

    // 로딩바
    private ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        try {
            this.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws Exception {
        // 유저정보
        userInfo = (UserDto)getIntent().getSerializableExtra("USER_INFO");
        // db객체
        mDbRefOfSharedKey = FirebaseDatabase.getInstance().getReference("sharedKey");

        // 스토리지 객체
        mStorageRef = FirebaseStorage.getInstance().getReference("profile");

        // 프로필사진
        user_profile = findViewById(R.id.user_profile);
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);


                builder.setItems(R.array.photoOrImageWithoutRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent();
                        } else if(pos == 1) { //갤러리에서 가져오기
                            doTakeMultiAlbumAction();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        
        //이름, 닉네임
         editText_name = findViewById(R.id.editText_name);
         editText_nickName = findViewById(R.id.editText_nickName);

        //전화번호 인증
         editText_phoneNum = findViewById(R.id.editText_phoneNum);
         btn_certification = findViewById(R.id.btn_certification);

        // 인증번호 확인
         editText_confirm = findViewById(R.id.editText_confirm);
         btn_confirm = findViewById(R.id.btn_confirm);

        //회원가입 완료 버튼
         btn_registerComplete = findViewById(R.id.btn_registerComplete);
         btn_registerComplete.setEnabled(false);

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

        //타이머
        layout_timer = findViewById(R.id.layout_timer);
        textView_timer_remain = findViewById(R.id.textView_timer_remain);;
        textView_timer = findViewById(R.id.textView_timer);

        //타이머 핸들러
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 0){
                    if(minute == 0 && sec == 0){
                        handler.removeMessages(0);
                        t.setRunning(false);
                        return;
                    }

                    sec--;
                    if(sec == 0){
                        minute--;
                        sec = 60;
                    }
                    textView_timer.setText("0" + minute + ":" + (sec < 10?"0" + sec : sec));
                }
            }
        };

        // 레이아웃
        layout_register = findViewById(R.id.layout_register);

        // 처음 사귄날
        textview_first_day = findViewById(R.id.textview_first_day);
        textview_first_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity3.this);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + dayOfMonth +"일";
                        String day = "";
                        month = month + 1;

                        try {
                            day = Util.getDateDay(date,"yyyy년 mm월 dd일");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        firstDayYear = year;
                        firstDayMonth = month;
                        firstDayDay = dayOfMonth;

                        String dateAndDay = date+" ("+day+")";
                        textview_first_day.setText(dateAndDay);
                    }
                });
                dialog.show();
            }
        });

        textview_first_day.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.LTGRAY);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });

        // 공동 키값에 처음사귄날이 저장되어있다면 가져오기
        // 아니면 현재날짜 세팅
        textview_first_day.setText(getCurrentDate());
        firstDayYear = Calendar.getInstance().get(Calendar.YEAR);
        firstDayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        firstDayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // 개인정보 처리방침
        textView_private_info = findViewById(R.id.textView_private_info);
        textView_private_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity3.this,PrivateInfoActivity.class);
                startActivity(intent);
            }
        });
        checkbox_private_info = findViewById(R.id.checkbox_private_info);
        checkbox_private_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox_private_info.isChecked()){
                    btn_registerComplete.setEnabled(true);
                    btn_registerComplete.setBackgroundColor(ContextCompat.getColor(RegisterActivity3.this, R.color.orange));
                } else{
                    btn_registerComplete.setBackgroundColor(ContextCompat.getColor(RegisterActivity3.this, R.color.grey));
                    btn_registerComplete.setEnabled(false);
                }
            }
        });

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //객체
        registerChecker = new RegisterChecker();
    }

    private void setListener(){
        btn_certification.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_registerComplete.setOnClickListener(this);
    }

    public boolean validation(){
        String denyReason = null;
        boolean isPossibleRegister = true;

        if(editText_name.getText().toString().equals("")){
            denyReason = RegisterDenyReason.NAME_NOT_ENTER.getDenyReason();
        } else if(editText_nickName.getText().toString().equals("")){
            denyReason = RegisterDenyReason.NICKNAME_NOT_ENTER.getDenyReason();
        } else if(editText_phoneNum.getText().toString().equals("")){
            denyReason = RegisterDenyReason.PHONE_NUM_NOT_ENTER.getDenyReason();
        } else if(editText_confirm.getText().toString().equals("")){
            denyReason = RegisterDenyReason.CERTIFICATION_NUM_NOT_ENTER.getDenyReason();
        }

        if(denyReason != null){
            Toast.makeText(this, denyReason, Toast.LENGTH_LONG).show();
            isPossibleRegister = false;
        }

        return isPossibleRegister;
    }

    // id와 pw를 받아서 userDto를 저장
    // 로그인화면 혹은 2단계회원가입 화면에서 넘어 올 때 intent에 id와 pw를 받아서 가져옴
    public UserDto createUserInfo(){
        String name = editText_name.getText().toString();
        String nickName = editText_nickName.getText().toString();
        String phoneNum = editText_phoneNum.getText().toString();

        Intent intent = getIntent();
        UserDto userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");
        String id = userInfo.getId();
        String pw = userInfo.getPw();

        return new UserDto(id,pw,name,nickName,phoneNum);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.btn_certification: // 인증번호 전송
                phoneNum = editText_phoneNum.getText().toString();

                if(!phoneNum.equals("")){
                    if(editText_phoneNum.getText() != null){
                        if (ContextCompat.checkSelfPermission(RegisterActivity3.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                            // You can use the API that requires the permission.
                            int authNum = 0;
                            authNum = registerChecker.sendSmsAuthNum(phoneNum);

                            // 문자 발송 성공 시 다이얼로그
                            if(authNum > 0){
                                // 다이얼로그 메시지 생성
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);

                                builder.setTitle("인증번호 발송 완료");
                                builder.setMessage("인증번호 발송이 완료되었습니다.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        textView_timer.setText("05:00");

                                        minute = 4;
                                        sec = 60;
                                        // 기존에 실행되고 있는 스레드가 있다면 종료
                                        if(t != null && (t.getState() == Thread.State.RUNNABLE || t.getState() == Thread.State.TIMED_WAITING)){
                                            t.setRunning(false);
                                        }

                                        layout_timer.setVisibility(View.VISIBLE);

                                        t = new TimerThread(handler);
                                        t.start();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
                            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
                            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity3.this, Manifest.permission.SEND_SMS)) {

                                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                                Snackbar.make(layout_register, "이 앱을 실행하려면 문자전송 권한이 필요합니다.",
                                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                                        ActivityCompat.requestPermissions( RegisterActivity3.this, new String[]{Manifest.permission.SEND_SMS}, 1000);
                                    }
                                }).show();
                            } else {
                                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                                ActivityCompat.requestPermissions( RegisterActivity3.this, new String[]{Manifest.permission.SEND_SMS}, 1000);
                            }
                        }
                    }
                }
                break;
            case  R.id.btn_confirm: // 인증확인
                if(editText_confirm.getText() != null){
                    String inputAuthNum = editText_confirm.getText().toString();

                    if(!inputAuthNum.equals("")){
                        if(minute == 0 && sec == 0){
                            Toast.makeText(this, "인증시간이 초과되었습니다. 다시 인증하세요.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int input = Integer.parseInt(inputAuthNum);

                        // 인증번호가 동일하다면
                        if(registerChecker.isCorrectAuthNum(input)){
                            // 메세지큐에 있는 모든 메시지 삭제, 쓰레드 종료
                            handler.removeMessages(0);
                            t.setRunning(false);

                            // 다이얼로그 메시지 생성
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);

                            builder.setMessage("인증번호가 일치합니다.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    btn_certification.setVisibility(View.GONE);
                                    btn_confirm.setVisibility(View.GONE);
                                    layout_timer.setVisibility(View.GONE);
                                    editText_phoneNum.setEnabled(false);
                                    editText_confirm.setEnabled(false);
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else{
                            Toast.makeText(this, "인증번호를 확인하세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
            case  R.id.btn_registerComplete: // 회원가입 완료
                if(!validation()){
                    return;
                }

                //로딩바 시작
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);

                // 이미지를 선택했다면
                if(bitmap != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String profileUrl = "profile/"+userInfo.getId()+"_profile.JPEG";
                    ref =  FirebaseStorage.getInstance().getReference(profileUrl);
                    uploadTask =ref.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 내정보 저장
                            userInfo.setProfileUrl(profileUrl);
                            userInfo.setName(editText_name.getText().toString());
                            userInfo.setNickName(editText_nickName.getText().toString());
                            userInfo.setPhoneNum(editText_phoneNum.getText().toString());

                            DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("users/"+userInfo.getFbKey());
                            mDbRef.setValue(userInfo);

                            // sharedKey 처음사귄날 저장
                            // sharedKey 데이터 가져옴
                            mDbRef = FirebaseDatabase.getInstance().getReference("sharedKey/"+userInfo.getSharedKey());
                            DatabaseReference finalMDbRef = mDbRef;
                            mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    SharedKeyDto sharedKeyDto = snapshot.getValue(SharedKeyDto.class);
                                    // user1에 저장되어 있다면
                                    if(sharedKeyDto.getUser1Id().equals(userInfo.getId())){
                                        sharedKeyDto.setUser1ProfileUrl(profileUrl);
                                        sharedKeyDto.setUser1NickName(userInfo.getNickName());
                                    } else{ // user2에 저장되어 있다면
                                        sharedKeyDto.setUser2ProfileUrl(profileUrl);
                                        sharedKeyDto.setUser2NickName(userInfo.getNickName());
                                    }

                                    String firstDay = "" + firstDayYear + "" + (firstDayMonth < 10 ? "0"+firstDayMonth : firstDayMonth) + ""  + (firstDayDay < 10 ? "0"+ firstDayDay : firstDayDay);
                                    sharedKeyDto.setDateOfFirstDay(firstDay);
                                    sharedKeyDto.setSharedKeyId(sharedKeyDto.getSharedKeyId());
                                    finalMDbRef.setValue(sharedKeyDto);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Log.d("debug_upload",ref.getDownloadUrl().toString());
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        }
                    });
                } else{ // 이미지를 선택하지 않았다면 선택하라고 토스트
                    Toast.makeText(this, "프로필을 선택해주세요!", Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                    return;
                    // 내정보 저장
                    /*userInfo.setName(editText_name.getText().toString());
                    userInfo.setNickName(editText_nickName.getText().toString());
                    userInfo.setPhoneNum(editText_phoneNum.getText().toString());

                    DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("users/"+userInfo.getFbKey());
                    mDbRef.setValue(userInfo);

                    // sharedKey 처음사귄날 저장
                    // sharedKey 데이터 가져옴
                    mDbRef = FirebaseDatabase.getInstance().getReference("sharedKey/"+userInfo.getSharedKey());
                    DatabaseReference finalMDbRef = mDbRef;
                    mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SharedKeyDto sharedKeyDto = snapshot.getValue(SharedKeyDto.class);
                            // user1에 저장되어 있다면
                            if(sharedKeyDto.getUser1Id().equals(userInfo.getId())){
                                sharedKeyDto.setUser1NickName(userInfo.getNickName());
                            } else{ // user2에 저장되어 있다면
                                sharedKeyDto.setUser2NickName(userInfo.getNickName());
                            }

                            String firstDay = "" + firstDayYear + "" + (firstDayMonth < 10 ? "0"+firstDayMonth : firstDayMonth) + ""  + (firstDayDay < 10 ? "0"+ firstDayDay : firstDayDay);
                            sharedKeyDto.setDateOfFirstDay(firstDay);
                            sharedKeyDto.setSharedKeyId(sharedKeyDto.getSharedKeyId());
                            finalMDbRef.setValue(sharedKeyDto);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/
                }


                //로딩바 종료
                customProgressDialog.dismiss();

                // sp에 해당 아이디의 파일 생성
                SPManager spManager = new SPManager(RegisterActivity3.this, "com.teamnova.dateset.user."+userInfo.getId(), MODE_PRIVATE);
                spManager.setCompleteStep(3);
                
                // 다이얼로그 메시지 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);

                builder.setTitle("회원가입 완료").setMessage("회원가입이 완료되었습니다!");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //intent.putExtra("USER_INFO",createUserInfo());
                        startActivity(intent);
                        finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    // 요청한 권한에 대한 결과를 처리하는 콜백 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ( requestCode == 1000) {

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                check_result = false;
            }

            if (check_result) {
                // 모든 퍼미션을 허용했다면 카메라 프리뷰를 시작합니다.
                int authNum = 0;
                authNum = registerChecker.sendSmsAuthNum(phoneNum);

                // 문자 발송 성공 시 다이얼로그
                if(authNum > 0){
                    // 다이얼로그 메시지 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);

                    builder.setTitle("인증번호 발송 완료");
                    builder.setMessage("인증번호 발송이 완료되었습니다.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            textView_timer.setText("05:00");

                            minute = 4;
                            sec = 60;
                            // 기존에 실행되고 있는 스레드가 있다면 종료
                            if(t != null && (t.getState() == Thread.State.RUNNABLE || t.getState() == Thread.State.TIMED_WAITING)){
                                t.setRunning(false);
                            }

                            layout_timer.setVisibility(View.VISIBLE);

                            t = new TimerThread(handler);
                            t.start();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(layout_register, "문자전송이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {
                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(layout_register, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    class TimerThread extends Thread {
        private Handler handler;
        private boolean isRunning = true;

        public TimerThread(Handler handler){
            this.handler = handler;
        }

        @Override
        public void run() {
            while(isRunning){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }
    }

    private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }

    private void dispatchTakePictureIntent() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0);
        }else{ //카메라 권한 있음
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) { }
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.teamnova.dateset.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 직접촬영
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            user_profile.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 갤러리에서 가져오기
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    // 이미지 저장
                    /*Bitmap bitmap = BitmapFactory.decodeStream(in);*/
                    bitmap = resizeBitmap(in);
                    user_profile.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 촬영한 사진을 저장할 파일을 생성한다.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    private Bitmap resizeBitmap(InputStream in){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap= BitmapFactory.decodeStream(in,null,options);

        int width = 300; // 축소시킬 너비
        int height = 300; // 축소시킬 높이
        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        if (bmpWidth > width) {
            // 원하는 너비보다 클 경우의 설정
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        } else if (bmpHeight > height) {
            // 원하는 높이보다 클 경우의 설정
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        }

        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
        return resizedBmp;
    }

    // 현재 일자 리턴
    private String getCurrentDate() throws Exception {
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy년 MM월 dd일");
        Date today = new Date();
        String date = format1.format(today);
        String day = Util.getDateDay(date,"yyyy년 MM월 dd일");
        return date+" ("+day+")";
    }
}