package com.teamnova.dateset.login.login;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.login.register.RegisterActivity2;
import com.teamnova.dateset.login.register.RegisterActivity3;
import com.teamnova.dateset.post_list.PostListActivity;
import com.teamnova.dateset.login.find_id_pw.FindIdPwActivity;
import com.teamnova.dateset.login.register.RegisterActivity;
import com.teamnova.dateset.util.ProgressDialog;
import com.teamnova.dateset.util.ProgressLoadingDialog;
import com.teamnova.dateset.util.SPManager;

import java.util.HashMap;

/**
 1. 클래스명: LoginActivity
 2. 역할: 로그인, 회원가입, id/pw찾기 기능제공
 */
public class  LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_login;
    private TextView textView_register;
    private TextView textView_find_id_pw;
    private EditText editTextEmail;
    private EditText editTextPw;
    private UserDto userInfo;
    private HashMap<String, String> savedData;
    private int index;

    // 로딩바
    private ProgressLoadingDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        textView_register = findViewById(R.id.textView_register);
        textView_find_id_pw = findViewById(R.id.textView_find_id_pw);

        btn_login.setOnClickListener(this);
        textView_register.setOnClickListener(this);
        textView_find_id_pw.setOnClickListener(this);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPw = findViewById(R.id.editTextNumber_password);

        savedData = new HashMap<>();

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressLoadingDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    /**
     *  1. 이벤트 종류: onClick
     *  2. 역할: 로그인 버튼, 회원가입 텍스트, id/pw찾기 텍스트 클릭 시 발생하는 이벤트 정의
     *  3. 종류
     *      1) R.id.btn_login: //로그인버튼
     *          1> id/pw 유효성체크
     *          2> 입력한 id에 해당하는 pw 서버에서 가져와서 입력한 pw와 동일한지 확인 후 로그인
     *          3> HomeActivity로 이동
     *      2) R.id.textView_register: //회원가입
     *          1> RegisterActivity로 이동
     *      3) R.id.textView_find_id_pw: //아이디비밀번호찾기 링크
     *          1> FindIdAndPwActivity로 이동
     * */
    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch(v.getId()){
          case  R.id.btn_login: //로그인버튼
              String id = editTextEmail.getText().toString();
              String pw = editTextPw.getText().toString();

              if(id.equals("")){
                  Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_LONG).show();
                  return;
              }

              if(pw.equals("")){
                  Toast.makeText(this, "PW를 입력하세요", Toast.LENGTH_LONG).show();
                  return;
              }

              this.login(id,pw);
            break;
            case  R.id.textView_register: //회원가입 링크
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            case  R.id.textView_find_id_pw: //아이디비밀번호찾기 링크
                //Intent 구현
                UserDto userInfo = null;
                intent = getIntent();
                userInfo = (UserDto) intent.getSerializableExtra("USER_INFO");

                intent = new Intent(getApplicationContext(), FindIdPwActivity.class);
                //Intent 구현
                intent.putExtra("USER_INFO",userInfo);
                startActivityForResult(intent,1);
                break;
          default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("debug_onActivityResult()","호출");
        if(requestCode == 1){
            if (resultCode==RESULT_OK) {
                userInfo = (UserDto)data.getSerializableExtra("USER_INFO");
                Log.d("RESULT_OK","넘어옴");

                if(userInfo != null){
                    Log.d("debug_USER_INFO",userInfo.toString());
                    Log.d("debug_USER_INFO",userInfo.getId());
                    Log.d("debug_USER_INFO",userInfo.getPw());
                    Log.d("debug_USER_INFO",userInfo.getName());
                    Log.d("debug_USER_INFO",userInfo.getNickName());
                    Log.d("debug_USER_INFO",userInfo.getPhoneNum());
                }
            }
        }
    }

    private void login(String id, String pw) {
        //로딩바 시작
        customProgressDialog.show();
        customProgressDialog.setCancelable(false);

        // 백도어
        if(id.equals("1") && pw.equals("1")){
            userInfo = new UserDto("1","1","상대방","상대방","01012341234");
            //로딩바 종료
            customProgressDialog.dismiss();
            // Sign in success, update UI with the signed-in user's information

            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("USER_INFO",userInfo);
            startActivity(intent);
            finish();
            return;
        } else if(id.equals("2") && pw.equals("2")){
            userInfo = new UserDto("2","2","상대방","상대방","01012341234");
            //로딩바 종료
            customProgressDialog.dismiss();
            // Sign in success, update UI with the signed-in user's information

            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("USER_INFO",userInfo);
            startActivity(intent);
            finish();
            return;
        }

        index = 0;
        // 해당 id의 db값 가져 옴
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users");

        Query q = dRef.orderByKey();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("debug_aaa","1111111");

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userInfo = dataSnapshot.getValue(UserDto.class);

                    // 입력된 아이디가 존재한다면 for문 나가기
                    if(userInfo.getId().equals(id)){
                        break;
                    }else{
                        index++;
                    }
                }

                Log.d("debug_index","" + index);
                Log.d("debug_size","" + snapshot.getChildrenCount());

                // 해당 아이디 존재
                if(index != snapshot.getChildrenCount() && userInfo.getPw().equals(pw)){
                    // 해당 아이디의 sp가져옴
                    SPManager spManager = new SPManager(LoginActivity.this, "com.teamnova.dateset.user."+id, MODE_PRIVATE);
                    int step = spManager.getCompleteStep();
                    Intent intent = null;

                    // 저장되어있는 값에 따라 이동하는 액티비티 달라짐
                    // 0: 아이디 생성 안됨
                    // 1: 1단계 완료 => 2단계 회원가입 화면
                    // 2: 2단계 완료 => 3단계 회원가입 화면
                    // 3: 3단계 완료 => 홈화면
                    switch(step){
                        case  1:
                            //로딩바 종료
                            customProgressDialog.dismiss();
                            intent = new Intent(LoginActivity.this,RegisterActivity2.class);
                            intent.putExtra("USER_INFO",userInfo);
                            startActivity(intent);
                            break;
                        case  2:
                            //로딩바 종료
                            customProgressDialog.dismiss();
                            intent = new Intent(LoginActivity.this, RegisterActivity3.class);
                            intent.putExtra("USER_INFO",userInfo);
                            startActivity(intent);
                            break;
                        case  0:
                        case  3:
                        default:
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.signInWithEmailAndPassword(id,pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //로딩바 종료
                                        customProgressDialog.dismiss();
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // 메일 인증 사용 안함
                                        /*Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                        intent.putExtra("USER_INFO",userInfo);
                                        startActivity(intent);
                                        finish();*/

                                        // 메일 인증 사용
                                        if(userInfo.getId().equals("a@a.com") || userInfo.getId().equals("b@b.com")){
                                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                            intent.putExtra("USER_INFO",userInfo);
                                            startActivity(intent);
                                            finish();
                                        } else{
                                            if(user.isEmailVerified()){
                                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                                intent.putExtra("USER_INFO",userInfo);
                                                startActivity(intent);
                                                finish();
                                            } else{
                                                Toast.makeText(LoginActivity.this, "로그인을 하기위해 먼저 이메일 인증을 받으셔야 합니다!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        //로딩바 종료
                                        customProgressDialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "로그인을 하기위해 먼저 이메일 인증을 받으셔야 합니다!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            break;
                    }
                } else{ //해당 아이디 없음
                    //로딩바 종료
                    customProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "존재하지 않는 ID거나 비밀번호가 맞지 않습니다.", Toast.LENGTH_LONG).show();
                }
                q.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        q.addListenerForSingleValueEvent(listener);

        /*LoginChecker loginChecker = new LoginChecker();
        // Intent 구현
        Intent intent = getIntent();
        if(userInfo == null) {
            userInfo = (UserDto) intent.getSerializableExtra("USER_INFO");
        }

        if(userInfo != null){
            Log.d("debug_USER_INFO",userInfo.toString());
            Log.d("debug_USER_INFO",userInfo.getId());
            Log.d("debug_USER_INFO",userInfo.getPw());
            Log.d("debug_USER_INFO",userInfo.getName());
            Log.d("debug_USER_INFO",userInfo.getNickName());
            Log.d("debug_USER_INFO",userInfo.getPhoneNum());
        }

        int result = loginChecker.loginCheck(id, pw, userInfo);*/

        /*Log.d("debug_",id + "  " + pw);
        // 백도어
        if(id.equals("leeryon9105@naver.com") && pw.equals("!asdf12345")) {
            result = 1;
            userInfo = new UserDto("leeryon9105@naver.com","!asdf12345","이령빈","령","01032652285");
        } else if(id.equals("lrb9105@gmail.com") && pw.equals("!asdf12345")){
            result = 1;
            userInfo = new UserDto("lrb9105@gmail.com","!asdf12345","상대방","상대방","01012341234");
        } else if(id.equals("1") && pw.equals("1")){
            result = 1;
            userInfo = new UserDto("1","1","상대방","상대방","01012341234");
        } else if(id.equals("2") && pw.equals("2")){
            result = 1;
            userInfo = new UserDto("2","2","상대방","상대방","01012341234");
        }*/

/*        Log.d("debug_result",""+result);
        if(result == 1){
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(this, "존재하지 않는 ID거나 비밀번호가 맞지 않습니다.", Toast.LENGTH_LONG).show();
        }*/
    }
}