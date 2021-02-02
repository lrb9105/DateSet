package com.teamnova.dateset.login.mypage;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.AddedFunctionActivity;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.post_list.PostListActivity;
import com.teamnova.dateset.post_writing.PostWritingActivity;
import com.teamnova.dateset.util.BitmapConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityManager activityManager;
    private ActivityManager.RunningTaskInfo runningTaskInfo;
    private String topActivityName;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    private ImageView imageView_profile;
    private TextView textView_email;
    private TextView textView_name;
    private TextView textView_nickName;
    private EditText editTextText_phoneNum;
    private EditText editText_certificationNum;
    private EditText editText_pw;
    private EditText editTextText_pwConfirm;
    private TextView textView_requirement2;
    private TextView textView_requirement3;


    private Button btn_certification;
    private Button btn_change_complete;

    private ImageButton btn_home;
    private ImageButton btn_chat;
    private ImageButton btn_post_writing;
    private ImageButton btn_my_page;
    private ImageButton btn_added_function;
    private MyPageManager myPageManager;

    private UserDto userInfo;
    private PostDto postInfo;
    private Bitmap img;

    // 변경 관련 플래그
    private boolean isCorrectPw = false;
    private boolean isCoincidedPw = false;
    private boolean isCorrectAuthNum = false;

    private int authNum;
    private InputStream in;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // 컴포넌트 초기화
        imageView_profile = findViewById(R.id.imageView_profile);
        textView_email = findViewById(R.id.textView_email);
        textView_name = findViewById(R.id.textView_name);
        textView_nickName = findViewById(R.id.textView_nickName);
        editTextText_phoneNum = findViewById(R.id.editTextText_phoneNum);
        editText_certificationNum = findViewById(R.id.editText_certificationNum);
        textView_requirement2 = findViewById(R.id.textView_requirement2);
        textView_requirement3 = findViewById(R.id.textView_requirement3);

        editText_pw = findViewById(R.id.editText_pw);
        editText_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if(inputText.length() > 0 && inputText.length() < 10){
                    textView_requirement2.setVisibility(View.VISIBLE);
                    textView_requirement2.setText("비밀번호는 10-16자사이 영문/숫자/특수문자를 모두 포함하셔야 합니다.");
                    textView_requirement2.setTextColor(Color.RED);
                } else if(inputText.length() > 16){
                    textView_requirement2.setVisibility(View.VISIBLE);
                    textView_requirement2.setText("비밀번호는 10-16자사이 영문/숫자/특수문자를 모두 포함하셔야 합니다.");
                    textView_requirement2.setTextColor(Color.RED);
                } else{
                    textView_requirement2.setVisibility(View.GONE);
                }
            }
        });

        editText_pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String pw = editText_pw.getText().toString();
                String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{10,16}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(pw);
                // 테스트용 - 나중에 지우기
                //isCorrectPw = true;

                // 정규식을 만족함 - 올바른 비밀번호
                if(!hasFocus){
                    if(matcher.find()) {
                        textView_requirement2.setVisibility(View.GONE);
                        isCorrectPw = true;
                    } else{
                        textView_requirement2.setVisibility(View.VISIBLE);
                        textView_requirement2.setText("비밀번호는 10-16자사이 영문(대,소문자)/숫자/특수문자를 모두 포함하셔야 합니다.");
                        textView_requirement2.setTextColor(Color.RED);
                        isCorrectPw = false;
                    }
                }
            }
        });
        editTextText_pwConfirm = findViewById(R.id.editTextText_pwConfirm);
        editTextText_pwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String pw = editText_pw.getText().toString();
                String pwConfirm = s.toString();

                // 비밀번호와 비밀번호 확인이 동일함
                if(pwConfirm.equals(pw)){
                    textView_requirement3.setText("비밀번호가 동일합니다.");
                    textView_requirement3.setTextColor(Color.GREEN);
                    isCoincidedPw = true;
                    userInfo.setPw(pw);
                } else{
                    textView_requirement3.setText("비밀번호가 일치하지 않습니다.");
                    textView_requirement3.setTextColor(Color.RED);
                    isCoincidedPw = false;
                }
            }
        });
        //버튼 초기화
        btn_certification = findViewById(R.id.btn_certification);
        btn_change_complete = findViewById(R.id.btn_change_complete);

        btn_certification.setOnClickListener(this);
        btn_change_complete.setOnClickListener(this);

        //하단 버튼 초기화
        btn_home = findViewById(R.id.btn_home);
        btn_chat = findViewById(R.id.btn_chat);
        btn_post_writing = findViewById(R.id.btn_post_writing);
        //btn_added_function = findViewById(R.id.btn_added_function);

        imageView_profile.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        btn_post_writing.setOnClickListener(this);
        btn_my_page.setOnClickListener(this);
        //btn_added_function.setOnClickListener(this);

        // 객체 초기화
        myPageManager = new MyPageManager();

        // 컴포넌트 초기화
        userInfo = this.bringUserInfo();

        Log.d("debug_onCreateMyPage","onCreate()호출");

        if(userInfo != null) {
            Log.d("debug_USER_INFO_MyPage", userInfo.toString());
            Log.d("debug_USER_INFO_MyPage", userInfo.getId());
            Log.d("debug_USER_INFO_MyPage", userInfo.getPw());
            Log.d("debug_USER_INFO_MyPage", userInfo.getName());
            Log.d("debug_USER_INFO_MyPage", userInfo.getNickName());
            Log.d("debug_USER_INFO_MyPage", userInfo.getPhoneNum());
        }
        if(userInfo != null) {
            this.setUserInfo(userInfo);
        }

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningTaskInfo = activityManager.getRunningTasks(1).get(0);
        topActivityName = runningTaskInfo.topActivity.getClassName();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Class cls = null;
        switch(v.getId()){
            case  R.id.btn_certification: // 인증번호 전송
                if(editTextText_phoneNum.getText() != null){
                    String phoneNum = editTextText_phoneNum.getText().toString();
                    if(!phoneNum.equals("")){
                        authNum =myPageManager.sendSmsAuthNum(phoneNum);
                        Toast.makeText(this, "인증번호를 전송했습니다: " + authNum, Toast.LENGTH_LONG).show();
                    }
                }
                return;
            case R.id.imageView_profile:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("프로필 교체");

                builder.setItems(R.array.photoOrImageOrRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        String[] items = getResources().getStringArray(R.array.photoOrImageOrRemove);
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent();
                        } else { //갤러리에서 가져오기
                            doTakeMultiAlbumAction();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return;
            case R.id.btn_change_complete: // 변경완료
                // 비밀번호
                Editable pwEditable = editText_pw.getText();
                String pw = null;
                if(pwEditable != null) {
                    pw = pwEditable.toString();
                }

                // 인증번호
                Editable authNumEditable = editText_certificationNum.getText();
                String authNum = null;
                if(authNumEditable != null) {
                    authNum = authNumEditable.toString();
                }

                // 비밀번호 변경 안함 => 비밀번호 관련 플래그 전부 true
                if(pw.equals("")){
                    isCorrectPw = true;
                    isCoincidedPw = true;
                }else{
                    if(!isCorrectPw || !isCoincidedPw){
                        Toast.makeText(this, "비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // 전화번호 변경 안함 => 전화번호 관련 플래그 전부 true
                if(authNum.equals("")){
                    isCorrectAuthNum = true;
                }else{
                    // 인증번호 동일
                    if(Integer.parseInt(authNum) == this.authNum){
                        String phoneNum = editTextText_phoneNum.getText().toString();
                        isCorrectAuthNum = true;
                        userInfo.setPhoneNum(phoneNum);
                    } else{
                        Toast.makeText(this, "인증번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // 변경관련 플래그 값이 전부 true인 경우에만 변경 완료
                if(isCorrectPw && isCoincidedPw && isCorrectAuthNum){
                    String imgStr = null;
                    if(img != null){
                        Log.d("debug_img",img.toString());
                        imgStr = BitmapConverter.BitmapToString(img);
                    }else{
                        Log.d("debug_img","null");
                    }

                    /*if(imgStr != null){
                        Log.d("debug_imgStr",imgStr);
                    }else{
                        Log.d("debug_imgStr","null");
                    }*/

                    userInfo.setProfileUrl(imgStr);

                    if(imgStr != null){
                        Log.d("debug_setProfileUrl",userInfo.getProfileUrl());
                        Log.d("debug_userDto",userInfo.toString());
                    }else{
                        Log.d("debug_imgStr","null");
                    }

                    // 다이얼로그 메시지 생성
                    builder = new AlertDialog.Builder(MyPageActivity.this);

                    builder.setTitle("정보 수정 완료").setMessage("정보수정이 완료되었습니다!");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                            myPageManager.saveUserInfo(userInfo);
                            Intent intent = getIntent();
                            postInfo = (PostDto)intent.getSerializableExtra("POST_INFO");
                            intent = new Intent(getApplicationContext(), PostListActivity.class);
                            intent.putExtra("USER_INFO",userInfo);

                            if(userInfo != null){
                                if(userInfo.getProfileUrl() != null){
                                    Log.d("debug_USER_INFO_my",userInfo.getProfileUrl());
                                } else{
                                    Log.d("debug_USER_INFO_my","null");
                                }
                                Log.d("debug_USER_INFO_my",userInfo.toString());
                                Log.d("debug_USER_INFO_my",userInfo.getId());
                                Log.d("debug_USER_INFO_my",userInfo.getPw());
                                Log.d("debug_USER_INFO_my",userInfo.getName());
                                Log.d("debug_USER_INFO_my",userInfo.getNickName());
                                Log.d("debug_USER_INFO_my",userInfo.getPhoneNum());
                            } else{

                            }

                            if(postInfo != null){
                                intent.putExtra("POST_INFO",postInfo);
                            }

                            startActivity(intent);
                            finish();
                        }
                    });

                    alertDialog = builder.create();
                    alertDialog.show();
                } else{
                    Toast.makeText(this, "변경할 수 없습니다. 비밀번호와 인증번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
                return;
            default:
                break;
        }
    }

    /**
     *  1. 메소드명: setUserInfo
     *  2. 메소드 역할: 컴포넌트들을 초기화한다.
     *  3. 입력파라미터
     *      1) userInfo: 유저정보 객체
     * */
    private void setUserInfo(UserDto userInfo){
        if(userInfo.getProfileUrl() != null){
            Bitmap img = BitmapConverter.StringToBitmap(userInfo.getProfileUrl());
            imageView_profile.setImageBitmap(img);
        }
        textView_email.setText("아이디: " + userInfo.getId());
        textView_name.setText("이름: " + userInfo.getName());
        textView_nickName.setText("닉네임: " + userInfo.getNickName());
        editTextText_phoneNum.setText(userInfo.getPhoneNum());
    }

    /**
     *  1. 메소드명: bringUserInfo
     *  2. 메소드 역할: 서버에서 유저정보를 가져온다.
     *  3. 출력파라미터
     *      1) userInfo: 서버에서 가져온 유저정보 객체
     * */
    private UserDto bringUserInfo(){
        UserDto userInfo = null;
        // db 구현
        //userInfo = myPageManager.bringUserInfo();

        // Intent 구현
        Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");

        return userInfo;
    }

    private void dispatchTakePictureIntent() {
        int permissionCheck = ContextCompat.checkSelfPermission(MyPageActivity.this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(MyPageActivity.this,new String[]{Manifest.permission.CAMERA},0);
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

    private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        // 프로필 사진 고를 땐 여러장 못고르도록.
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            imageView_profile.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    // 이미지 표시
                    imageView_profile.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                InputStream in = null;
                ArrayList<Bitmap> imgList = new ArrayList<>();

                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri =  clipData.getItemAt(i).getUri();
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    img = BitmapFactory.decodeStream(in);
                    imgList.add(img);
                    imageView_profile.setImageBitmap(img);
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(topActivityName + "_debug_onStart", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(topActivityName + "_debug_onResume", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause()호출 MyPageActivity", Toast.LENGTH_LONG).show();

        Log.d(topActivityName + "_debug_onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(topActivityName + "_debug_onStop", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Mypage-InputStream 해제", Toast.LENGTH_LONG).show();
        try {
            if(in != null){
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(topActivityName + "_debug_onDestroy", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(topActivityName + "_debug_onRestart", "onRestart");
    }
}