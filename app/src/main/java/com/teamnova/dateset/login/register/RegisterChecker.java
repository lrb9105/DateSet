package com.teamnova.dateset.login.register;

import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.util.AuthNumManager;
import com.teamnova.dateset.util.DatabaseManager;
import com.teamnova.dateset.util.InvCodeManager;
import com.teamnova.dateset.util.SmsSender;

import java.util.Random;

/**
 1. 클래스명: RegisterChecker
 2. 역할: 사용자가 회원가입을 할 때 필요한 기능들을 수행하는 클래스
 */
public class RegisterChecker {
    private int authNum;
    private AuthNumManager authNumManager = new AuthNumManager();
    private InvCodeManager invCodeManager = new InvCodeManager();

    /**
     *  1. 메소드명: isNotDupleId
     *  2. 메소드 역할: 사용자가 입력한 id가 존재하는 기존에 존재하는 id인지 체크.
     *  3. 입력파라미터
     *      1) inputId: 사용자가 입력한 id
     *  4. 출력파라미터: boolean - 기존에 존재하는 id인지 여부(true: 사용가능, false: 사용불가)
     * */
    public boolean isNotDupleId(String inputId){
        DatabaseManager dbManager = new DatabaseManager();
        return dbManager.isNotExistedId(inputId);
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
     *  1. 메소드명: saveLoginInfo
     *  2. 메소드 역할: 사용자와 회원정보를 저장한다.
     *  3. 입력파라미터
     *      1) id: 사용자 id.
     *      2) invCode: 사용자의 초대코드.
     * */
    public void saveUserInfo(String id, String pw,String name,String nickName,String phoneNum){
        UserDto userInfo = new UserDto(id,pw,name,nickName,phoneNum);

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.saveUserInfo(userInfo);
    }

    /**
     *  1. 메소드명: createInviteCode
     *  2. 메소드 역할: 사용자의 문자인증이 완료되면 초대코드를 생성한다.
     *  3. 출력파라미터
     *      1) invCode: 사용자의 초대코드
     * */
    public int createInviteCode(String userId){
        return invCodeManager.createInviteCode(userId);
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
}
