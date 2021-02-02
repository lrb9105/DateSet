package com.teamnova.dateset.util;

import android.telephony.SmsManager;
import android.widget.Toast;

import com.teamnova.dateset.login.register.RegisterActivity;

public class SmsSender {
    /**
     *  1. 메소드명: sendSmsMsgAuthNum
     *  2. 메소드 역할: 사용자가 인증번호를 요청할 경우 사용자에게 인증번호 sms메세지를 보낸다.
     *  3. 입력파라미터
     *      1) phoneNum: 사용자 전화번호
     * */
    public void sendSmsMsgAuthNum(String phoneNum, int authNum){
        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, ""+authNum, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  1. 메소드명: sendSmsMsgTempPw
     *  2. 메소드 역할: 사용자에게 임시 비밀번호를 전송한다.
     *  3. 입력파라미터
     *      1) tempPw: 임시비밀번호
     *      2) phoneNum: 사용자 전화번호
     * */
    public void sendSmsMsgTempPw(String phoneNum, String tempPw){
        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, "임시비밀번호: "+tempPw, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
