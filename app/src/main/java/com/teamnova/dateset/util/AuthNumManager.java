package com.teamnova.dateset.util;

import java.util.Random;

public class AuthNumManager {
    /**
     *  1. 메소드명: createAuthNum
     *  2. 메소드 역할: 사용자에게 보낼 인증번호를 생성한다.
     *  3. 입력파라미터
     *      1) phoneNum: 사용자 전화번호
     * */
    public int createAuthNum(){
        int authNum = new Random().nextInt(900000) + 100000;
        return authNum;
    }

    /**
     *  1. 메소드명: isCorrectAuthNum
     *  2. 메소드 역할: 사용자가 입력한 인증번호가 올바른 인증번호인지 체크
     *  3. 입력파라미터
     *      1) inputAuthNum: 사용자가 입력한 인증번호
     *  4. 출력파라미터: boolean - 올바른 인증번호인지여부(true: 올바름, false: 틀렸음).
     * */
    public boolean isCorrectAuthNum(int inputAuthNum, int authNum){
        return inputAuthNum == authNum;
    }
}
