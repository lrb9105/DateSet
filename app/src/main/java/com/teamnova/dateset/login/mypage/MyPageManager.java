package com.teamnova.dateset.login.mypage;

import android.content.Intent;

import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.util.AuthNumManager;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.SmsSender;

/**
 1. 클래스명: MyPageManager
 2. 역할
 1) 마이페이지 관련 기능을 제공하는 객체
 2) 프로필사진 교체, 유저정보 가져오기/세팅, 인증번호 보내기, 인증번호 확인하기 등의 기능을 제공한다.
 */
public class MyPageManager {
    DatabaseManager dbManager = new DatabaseManager();
    AuthNumManager authNumManager = new AuthNumManager();
    int authNum;
    /**
     *  1. 메소드명: bringUserInfo
     *  2. 메소드 역할: 서버에서 유저정보를 가져온다.
     *  3. 출력파라미터
     *      1) userInfo: 서버에서 가져온 유저정보 객체
     * */
    public UserDto bringUserInfo(){
        UserDto userInfo = null;
        // db사용
        // userInfo = dbManager.bringUserInfo();
        return userInfo;
    }



    /**
     *  1. 메소드명: sendSmsMsg
     *  2. 메소드 역할: 사용자가 인증번호를 요청할 경우 사용자에게 인증번호 sms메세지를 보낸다.
     *  3. 입력파라미터
     *      1) phoneNum: 사용자 전화번호
     * */
    public int sendSmsAuthNum(String phoneNum){
        authNum = authNumManager.createAuthNum();
        SmsSender sender = new SmsSender();

        sender.sendSmsMsgAuthNum(phoneNum, authNum);
        return authNum;
    }

    /**
     *  1. 메소드명: isCorrectAuthNum
     *  2. 메소드 역할: 사용자가 입력한 인증번호가 올바른 인증번호인지 체크
     *  3. 입력파라미터
     *      1) inputAuthNum: 사용자가 입력한 인증번호
     *  4. 출력파라미터: boolean - 올바른 인증번호인지여부(true: 올바름, false: 틀렸음).
     * */
    public boolean isCorrectAuthNum(int inputAuthNum){
        return authNumManager.isCorrectAuthNum(inputAuthNum,authNum);
    }

    /**
     *  1. 메소드명: saveLoginInfo
     *  2. 메소드 역할: 사용자의 로그인 정보를 저장한다.
     *  3. 입력파라미터
     *      1) userInfo: 사용자정보
     * */
    public void saveUserInfo(UserDto userInfo){
        dbManager.saveUserInfo(userInfo);
    }
}
