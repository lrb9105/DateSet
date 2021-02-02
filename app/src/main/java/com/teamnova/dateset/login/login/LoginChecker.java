package com.teamnova.dateset.login.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.teamnova.dateset.MainActivity;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.login.find_id_pw.FindIdPwActivity;
import com.teamnova.dateset.login.register.RegisterActivity2;
import com.teamnova.dateset.login.register.RegisterActivity3;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.SmsSender;

/**
 1. 클래스명: LoginChecker
 2. 역할
    1) 로그인 시 정확한 id,pw를 입력했는지 확인 후 앱에 로그인 시켜주는역할.
    2) id/pw찾기
*/
public class LoginChecker {
    private int index;
    private Context context;
    private String idInstance;
    private String phoneNumInstance;
    private String nameInstance;
    /**
     *  1. 메소드명: loginCheck
     *  2. 메소드 역할: 사용자가 입력한 id와 pw가 존재한다면 앱에 로그인시키고 그렇지않다면 메세지를 출력한다(아이디 존재X, 비번 틀림).
     *  3. 입력파라미터
     *      1) id: 사용자가 입력한 id
     *      2) pw: 사용자가 입력한 pw
     *  4. 출력파라미터: resultNum 로그인 성공여부 반환
     *      1) 1: 로그인 성공 - HomeActivity로 이동
     *      2) 0: 로그인 실패 - 아이디 존재X or 비밀번호 틀림
     *
     * */
    public int loginCheck(String id, String inputPw, UserDto userInfo){
        int resultNum = 0;

        boolean isCorrectPw = checkIsCorrectPw(id, inputPw, userInfo);

        if(isCorrectPw){
            resultNum = 1;
        } else{
            resultNum = 0;
        }

        return resultNum;
    }

    public LoginChecker(Context context){
        this.context = context;
    }


    /**
     *  1. 메소드명: checkIsCorrectPw
     *  2. 메소드 역할: 사용자가 입력한 비밀번호와 서버에서 가져온 비밀번호를 비교해서 동일한지 여부 반환.
     *  3. 입력파라미터:
     *  4. 출력파라미터: tempPw - 임시비밀번호
     * */
    public boolean checkIsCorrectPw(String id,String inputPw, UserDto userInfo){
        // DB 구현
        /*DatabaseManager dbManager = new DatabaseManager();
        String dbPw = dbManager.getPw(id);
        boolean isCorrect = inputPw == dbPw;*/

        // Intent 구현
        boolean isCorrect = false;

        if(userInfo != null){
            isCorrect = (inputPw.equals(userInfo.getPw()));
        }

        return isCorrect;
    }

    /**
     *  1. 메소드명: findId
     *  2. 메소드 역할: 사용자가 입력한 이름과 전화번호에 해당하는 아이디가 존재할 경우 반환
     *  3. 입력파라미터
     *      1) name: 사용자가 입력한 이름
     *      2) phoneNum: 사용자가 입력한 전화번호
     *  4. 출력파라미터: id - 입력한 값에 해당하는 id
     * */
    public void findId(String name, String phoneNum){
        index = 0;
        idInstance = null;
        nameInstance = name;
        phoneNumInstance = phoneNum;

        // DB 사용
        // 해당 id의 db값 가져 옴
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users");

        Query q = dRef.orderByKey();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("debug_aaa", "1111111");
                UserDto userInfo = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userInfo = dataSnapshot.getValue(UserDto.class);

                    Log.d("debug_userInfo",userInfo.toString());

                    // 입력된 정보에 해당하는 유저정보가 존재한다면 for문 나가기
                    if(userInfo.getName() != null && userInfo.getPhoneNum() != null){
                        if (userInfo.getName().equals(nameInstance) && userInfo.getPhoneNum().equals(phoneNumInstance)) {
                            break;
                        } else {
                            index++;
                        }
                    } else{
                        index++;
                    }
                }

                Log.d("debug_index", "" + index);
                Log.d("debug_size", "" + snapshot.getChildrenCount());

                // 해당 아이디 존재
                if (index != snapshot.getChildrenCount()) {
                    idInstance = userInfo.getId();
                    // 다이얼로그 메시지 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("아이디 찾기").setMessage("해당하는 아이디는: " + idInstance + " 입니다");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else { //해당 아이디 없음
                    //로딩바 종료
                    Toast.makeText(context, "입력한 정보에 해당하는 유저 정보가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                }
                q.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        q.addListenerForSingleValueEvent(listener);
    }

    /**
     *  1. 메소드명: createTempPw
     *  2. 메소드 역할: 사용자가 입력한 정보에 해당하는 비밀번호가 존재하는 경우 임시비밀번호를 생성.
     *  3. 출력파라미터: tempPw - 임시비밀번호
     * */
    private String createTempPw(){
        String tempPw = null;
        int len = 16;
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        int idx = 0;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i++) {
            idx = (int) (charSet.length * Math.random()); // 36 * 생성된 난수를 Int로 추출 (소숫점제거)
            sb.append(charSet[idx]);
        }

        tempPw = sb.toString();

        return tempPw;
    }

    /**
     *  1. 메소드명: sendTempPw
     *  2. 메소드 역할: 임시비밀번호를 전송
     *  3. 출력파라미터: tempPw - 임시비밀번호
     * */
    public String sendSmsTempPw( String phoneNum){
        String tempPw = createTempPw();
        new SmsSender().sendSmsMsgTempPw(phoneNum,tempPw);
        return tempPw;
    }
}
