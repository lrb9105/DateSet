package com.teamnova.dateset.util;

import java.util.Random;

public class InvCodeManager {
    /**
     *  1. 메소드명: createInviteCode
     *  2. 메소드 역할: 사용자의 문자인증이 완료되면 초대코드를 생성한다.
     *  3. 출력파라미터
     *      1) invCode: 사용자의 초대코드
     * */
    public int createInviteCode(String userId){
        int invCode = new Random().nextInt(900000) + 100000;
        saveInvCode(userId,invCode);
        return invCode;
    }

    /**
     *  1. 메소드명: saveInvCode
     *  2. 메소드 역할: 사용자의 초대코드를 서버에 저장한다.
     *  3. 입력파라미터
     *      1) id: 사용자 id.
     *      2) invCode: 사용자의 초대코드.
     * */
    public void saveInvCode(String id, int invCode){
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.saveInvCode(id,invCode);
    }

    /**
     *  1. 메소드명: checkOpponentInvCode
     *  2. 메소드 역할: 상대방의 초대코드가 서버에 있는지 확인 하고 상대방의 id를 반환한다.
     *  3. 입력파라미터
     *      1) opponentInvCode: 상대방의 초대코드.
     *  4. 출력파라미터: opponentId - 상대방의 id
     * */
    public String checkOpponentInvCode(int opponentInvCode){
        String opponentId = null;
        DatabaseManager dbManager = new DatabaseManager();
        opponentId = dbManager.isExistedInvCodeAndReturnOppoId(opponentInvCode);

        return opponentId;
    }

    /**
     *  1. 메소드명: checkIsCoupled
     *  2. 메소드 역할: 내가 연결하려는 상대방이 다른 사람과 연결되어있거나 내가 다른사람과 연결되어있는지 확인.
     *  3. 입력파라미터
     *      1) myId: 사용자 id.
     *      2) opponentId: 상대방 id.
     *  4. 출력파라미터
     *      1) 1: 아무와도 연결되어있지 않음. 상대방과 연결하여 db에 저장 후 회원가입 완료.
     *      2) 2: 상대방과 이미 연결되어 따로 저장하지 않고 회원가입 완료.
     *      3) 3: 사용자가 다른 사람과 이미 연결되어있음(다른 사람이 사용자를 연결함) - 이미 연결된 사용자가 있다. 사용자 id 보여주기(bringCoupleI()한번 더 호출).
     *      4) 4: 상대방이 이미 다른 사람과 연결되어있음 - 이미 연결된 사용자가 있다. 사용자 id 보여주기(bringCoupleI()한번 더 호출).
     * */
    /*
    상황1: 아무와도 연결되어있지 않음
    상황2: 내가 연결하려는 상대와 연결되어있음
    상황3: 다른사람과 연결되어있음.
    상황4: 내가연결하려는 사람이 다른사람과 연결되어있음.
    */
    public int checkIsCoupled(String myId, String opponentId){
        String coupleId = bringCoupleId(myId);
        String opponentCoupleId = bringCoupleId(opponentId);

        if(coupleId == null){
            return 1;
        } else if(coupleId.equals(opponentId)) {
            return 2;
        } else if(!coupleId.equals(opponentId)) {
            return 3;
        } else if(!opponentCoupleId.equals(myId)){
            return 4;
        }

        return 0;
    }

    /**
     *  1. 메소드명: bringCoupleId
     *  2. 메소드 역할: 커플의 id를 조회한다.
     *  3. 입력파라미터
     *      1) id: 조회하려는 id.
     *  4. 출력파라미터: coupleId - 나와 커플인자의 id
     * */
    private String bringCoupleId(String id){
        String coupleId = null;
        DatabaseManager dbManager = new DatabaseManager();
        coupleId = dbManager.bringCoupleId(id);
        return coupleId;
    }


    /**
     *  1. 메소드명: connectIds
     *  2. 메소드 역할: 사용자와 상대방의 id를 연결하여 db에 저장한다.
     *  3. 입력파라미터
     *      1) id: 사용자 id.
     *      2) invCode: 사용자의 초대코드.
     * */
    public void connectIds(String myId, String opponentId){
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.saveIds(myId,opponentId);
    }
}
