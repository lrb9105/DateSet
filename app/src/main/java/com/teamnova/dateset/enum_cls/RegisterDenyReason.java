package com.teamnova.dateset.enum_cls;

public enum RegisterDenyReason {
    ID_NOT_ENTER("아이디를 입력하세요")
    , PW_NOT_ENTER("비밀번호를 입력하세요")
    , PW_NOT_CORRECT("비밀번호를 확인하세요.")
    , PW_NOT_EQUAL("비밀번호가 일치하지 않습니다.")
    , NAME_NOT_ENTER("이름을 입력하세요")
    , NICKNAME_NOT_ENTER("닉네임을 입력하세요")
    , PHONE_NUM_NOT_ENTER("전화번호를 입력하세요")
    , CERTIFICATION_NUM_NOT_ENTER("인증번호를 입력하세요")
    , NOT_EXIST_ID("존재하지 않는 아이다입니다.")
    , EXIST_ID("이미 존재하는 아이다입니다.");


    final private String denyReason;

    RegisterDenyReason(String denyReason) { //enum에서 생성자 같은 역할
        this.denyReason = denyReason;
    }
    public String getDenyReason() { // 문자를 받아오는 함수
        return denyReason;
    }
}
