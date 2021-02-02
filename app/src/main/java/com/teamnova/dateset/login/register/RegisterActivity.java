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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.enum_cls.RegisterDenyReason;
import com.teamnova.dateset.login.find_id_pw.FindIdPwActivity;
import com.teamnova.dateset.login.login.LoginActivity;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.ProgressDialog;
import com.teamnova.dateset.util.SPManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //메일주소
    private EditText editTextEmail;
    private TextView textView_requirement1;
    // 비밀번호
    private EditText editText_pw;
    private TextView textView_requirement2;
    // 비밀번호 확인
    private EditText editText_pw_confirm;
    private TextView textView_requirement3;

    //회원가입 완료 버튼
    private Button btn_registerComplete;
    // 뒤로가기 버튼
    private ImageButton btn_back;


    //회원가입 확인 플래그
    private boolean isCorrectPw = false;
    private boolean isCoincidedPw = false;

    //객체
    private RegisterChecker registerChecker;
    private DatabaseReference mDbRef;
    private FirebaseAuth mAuth;

    // 거절이유
    private String denyReason = null;

    // 로딩바
    private ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.initialize();
    }

    private void initialize() {
        //메일주소
        editTextEmail = findViewById(R.id.editTextText_emailAddress);
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (inputText.length() > 0) {
                    textView_requirement1.setVisibility(View.GONE);
                } else {
                    textView_requirement1.setVisibility(View.VISIBLE);
                }
            }
        });
        textView_requirement1 = findViewById(R.id.textView_requirement1);

        // 비밀번호
        editText_pw = findViewById(R.id.editText_pw);
        textView_requirement2 = findViewById(R.id.textView_requirement2);
        editText_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (inputText.length() > 0 && inputText.length() < 10) {
                    textView_requirement2.setVisibility(View.VISIBLE);
                    textView_requirement2.setText("비밀번호는 10-16자사이 영문/숫자/특수문자를 모두 포함하셔야 합니다.");
                    textView_requirement2.setTextColor(Color.RED);
                } else if (inputText.length() > 16) {
                    textView_requirement2.setVisibility(View.VISIBLE);
                    textView_requirement2.setText("비밀번호는 10-16자사이 영문/숫자/특수문자를 모두 포함하셔야 합니다.");
                    textView_requirement2.setTextColor(Color.RED);
                } else {
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
                if (!hasFocus) {
                    if (matcher.find()) {
                        textView_requirement2.setVisibility(View.GONE);
                        isCorrectPw = true;
                    } else {
                        textView_requirement2.setVisibility(View.VISIBLE);
                        textView_requirement2.setText("비밀번호는 10-16자사이 영문(대,소문자)/숫자/특수문자를 모두 포함하셔야 합니다.");
                        textView_requirement2.setTextColor(Color.RED);
                        isCorrectPw = false;
                    }
                }
            }
        });


        // 비밀번호 확인
        editText_pw_confirm = findViewById(R.id.editText_pw_confirm);
        textView_requirement3 = findViewById(R.id.textView_requirement3);
        editText_pw_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String pw = editText_pw.getText().toString();
                String pwConfirm = s.toString();

                // 비밀번호와 비밀번호 확인이 동일함
                if (pwConfirm.equals(pw)) {
                    textView_requirement3.setText("비밀번호가 동일합니다.");
                    textView_requirement3.setTextColor(Color.GREEN);
                    isCoincidedPw = true;
                } else {
                    textView_requirement3.setText("비밀번호가 일치하지 않습니다.");
                    textView_requirement3.setTextColor(Color.RED);
                    isCoincidedPw = false;
                }
            }
        });

        //회원가입 완료 버튼
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

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundColor(Color.LTGRAY);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        //객체
        registerChecker = new RegisterChecker();
        mDbRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


    }

    private void setListener() {
        btn_registerComplete.setOnClickListener(this);
    }

    public boolean validation() {
        denyReason = null;

        boolean isPossibleRegister = true;

        // 만약 로그인한 사용자가 있다면 로그아웃
        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }

        String id = editTextEmail.getText().toString();
        String pw = editText_pw.getText().toString();

        if (id.equals("")) {
            denyReason = RegisterDenyReason.ID_NOT_ENTER.getDenyReason();
        } else if (pw.equals("")) {
            denyReason = RegisterDenyReason.PW_NOT_ENTER.getDenyReason();
        } else if (!isCorrectPw) {
            denyReason = RegisterDenyReason.PW_NOT_CORRECT.getDenyReason();
        } else if (!isCoincidedPw) {
            denyReason = RegisterDenyReason.PW_NOT_EQUAL.getDenyReason();
        }

        if(denyReason != null){
            Toast.makeText(this, denyReason, Toast.LENGTH_LONG).show();
            isPossibleRegister = false;
        }

        return isPossibleRegister;
    }

    /*    public UserDto createUserInfo(){
        String id = editTextEmail.getText().toString();
        String pw = editText_pw.getText().toString();;
        return new UserDto(id,pw,name,nickName,phoneNum);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_registerComplete: // 회원가입 완료
                if (!validation()) {
                    return;
                }

                //로딩바 시작
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);

                // ID, PW
                String userId = editTextEmail.getText().toString();
                String pw = editText_pw.getText().toString();

                mAuth.createUserWithEmailAndPassword(userId,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 회원가입 성공
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user == null){
                                mAuth.signInWithEmailAndPassword(userId,pw);
                                Log.d("debug_login1", "로그인 안되있어서 로그인", task.getException());
                            } else{
                                // 회원가입 성공 후 메일 보냄
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("debug_email", "Email sent.");
                                                }
                                            }
                                        });

                                // 파이어베이스에 데이터 저장
                                DatabaseReference dbRef = mDbRef.push();
                                String uId = mAuth.getUid();
                                String key = dbRef.getKey();

                                UserDto userInfo = new UserDto(uId, key,userId, pw);
                                dbRef.setValue(userInfo);

                                mAuth.signOut();
                                //로딩바 종료
                                customProgressDialog.dismiss();

                                // sp에 해당 아이디의 파일 생성
                                SPManager spManager = new SPManager(RegisterActivity.this, "com.teamnova.dateset.user."+userId, MODE_PRIVATE);
                                spManager.setCompleteStep(1);

                                // 다이얼로그 메시지 생성
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                                builder.setTitle("로그인정보 생성 완료").setMessage("로그인 하기 위해선 메일 인증을 완료해야 합니다. 입력하신 이메일에서 메일인증을 진행하시기 바랍니다.");

                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 인텐트
                                        Intent intent = new Intent(getApplicationContext(), RegisterActivity2.class);
                                        intent.putExtra("USER_INFO",userInfo);

                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                Log.d("debug_login2", "회원가입 후 로그인 되어있음", task.getException());
                            }
                            Log.d("debug_login", "signInWithEmail:success", task.getException());
                        } else { // 회원가입 실패
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, RegisterDenyReason.EXIST_ID.getDenyReason(), Toast.LENGTH_SHORT).show();
                            Log.d("debug_login", "signInWithEmail:failure", task.getException());

                            //로딩바 종료
                            customProgressDialog.dismiss();
                            return;
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}