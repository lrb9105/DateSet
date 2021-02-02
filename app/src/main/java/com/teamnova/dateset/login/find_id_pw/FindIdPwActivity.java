package com.teamnova.dateset.login.find_id_pw;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.teamnova.dateset.ExThread;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.login.login.LoginChecker;
import com.teamnova.dateset.login.register.RegisterActivity3;

public class FindIdPwActivity extends AppCompatActivity {
    private Button btn_find_id;
    private Button btn_find_pw;
    private EditText editText_name;
    private EditText editText_phoneNum;
    private EditText editText_email;
    private EditText editText_phone_num2;
    private EditText editText_tempPw;
    private LinearLayout layout_find_id_pw;
    // 뒤로가기 버튼
    private ImageButton btn_back;

    private LoginChecker loginChecker;
    private UserDto userInfo;

    private ExThread exThread;

    // 인덳스
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_pw);


        this.initialize();

        // Intent 구현 시
        /*Intent intent = getIntent();
        userInfo = (UserDto) intent.getSerializableExtra("USER_INFO");
        if(userInfo != null) {
            Log.d("aaaa", userInfo.toString());
            Log.d("aaaa", userInfo.getId());
            Log.d("aaaa", userInfo.getPw());
            Log.d("aaaa", userInfo.getName());
            Log.d("aaaa", userInfo.getNickName());
            Log.d("aaaa", userInfo.getPhoneNum());
        }*/

    }

    private void initialize(){
        // 버튼 초기화
        btn_find_id = findViewById(R.id.btn_find_id);
        btn_find_pw = findViewById(R.id.btn_find_pw);

        // 에딧텍스트 초기화
        editText_name = findViewById(R.id.editText_name);
        editText_phoneNum = findViewById(R.id.editText_phoneNum);
        editText_email = findViewById(R.id.editText_email);
        editText_phone_num2 = findViewById(R.id.editText_phone_num2);
        editText_tempPw = findViewById(R.id.editText_tempPw);

        // 레이아웃
        layout_find_id_pw = findViewById(R.id.layout_find_id_pw);

        //객체초기화
        loginChecker = new LoginChecker(this);

        // id찾기
        btn_find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 유효성 체크
                String name = editText_name.getText().toString();
                String phoneNum = editText_phoneNum.getText().toString();

                if(name.equals("")) {
                    Toast.makeText(FindIdPwActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(phoneNum.equals("")) {
                    Toast.makeText(FindIdPwActivity.this, "전화번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // 아이디 찾기
                loginChecker.findId(name,phoneNum);
            }
        });

        // pw찾기
        btn_find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //유효성 체크
                String email = editText_email.getText().toString();
                String phoneNum2 = editText_phone_num2.getText().toString();

                if(email.equals("")) {
                    Toast.makeText(FindIdPwActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(phoneNum2.equals("")) {
                    Toast.makeText(FindIdPwActivity.this, "전화번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // 비밀번호 찾기(임시비밀번호 SMS 메시지로 전송).
                index = 0;
                // 해당 정보의 db값 가져 옴
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users");

                Query q = dRef.orderByKey();

                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("debug_aaa", "1111111");
                        UserDto userInfo = null;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            userInfo = dataSnapshot.getValue(UserDto.class);

                            // 입력된 정보에 해당하는 유저정보가 존재한다면 for문 나가기
                            if (userInfo.getId().equals(email) && userInfo.getPhoneNum().equals(phoneNum2)) {
                                break;
                            } else {
                                index++;
                            }
                        }

                        Log.d("debug_index", "" + index);
                        Log.d("debug_size", "" + snapshot.getChildrenCount());

                        // 해당 유저정보 존재
                        if (index != snapshot.getChildrenCount()) {
                            // 임시비밀번호 생성하여 전송
                            if (ContextCompat.checkSelfPermission(FindIdPwActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                // You can use the API that requires the permission.
                                 String tempPw = loginChecker.sendSmsTempPw(phoneNum2);

                                // 문자 발송 성공 시 다이얼로그
                                if(!tempPw.equals("")){
                                    // 임시비밀번호 저장
                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users/"+userInfo.getFbKey());
                                    userInfo.setPw(tempPw);
                                    dRef.setValue(userInfo);

                                    // 다이얼로그 메시지 생성
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FindIdPwActivity.this);

                                    builder.setTitle("임시비밀번호 발송 완료");
                                    builder.setMessage("임시비밀번호 발송이 완료되었습니다.");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {}
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
                                // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
                                if (ActivityCompat.shouldShowRequestPermissionRationale(FindIdPwActivity.this, Manifest.permission.SEND_SMS)) {

                                    // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                                    Snackbar.make(layout_find_id_pw, "이 앱을 실행하려면 문자전송 권한이 필요합니다.",
                                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                                            ActivityCompat.requestPermissions(FindIdPwActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1000);
                                        }
                                    }).show();
                                } else {
                                    // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                                    ActivityCompat.requestPermissions(FindIdPwActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1000);
                                }
                            }
                        } else { //해당 유저정보 없음
                            //로딩바 종료
                            Toast.makeText(FindIdPwActivity.this, "입력한 정보에 해당하는 유저 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                        q.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                q.addListenerForSingleValueEvent(listener);
            }
        });

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
                String tempPw = loginChecker.sendSmsTempPw(editText_phone_num2.getText().toString());

                // 문자 발송 성공 시 다이얼로그
                if(!tempPw.equals("")){
                    // 임시비밀번호 저장
                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users/"+userInfo.getFbKey());
                    userInfo.setPw(tempPw);
                    dRef.setValue(userInfo);

                    // 다이얼로그 메시지 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(FindIdPwActivity.this);

                    builder.setTitle("임시비밀번호 발송 완료");
                    builder.setMessage("임시비밀번호 발송이 완료되었습니다.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {}
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(layout_find_id_pw, "문자전송이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {
                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(layout_find_id_pw, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
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
}