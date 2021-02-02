package com.teamnova.dateset.util;

import com.teamnova.dateset.dto.CommentDto;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;

import java.util.ArrayList;

/**
 1. 클래스명: DatabaseManager
 2. 역할
     1) 데이터에 대한 CRUD작업을 한다(실제접근은 하지 않는 WRAPPER 클래스).
 */
public class DatabaseManager {
    /**
     *  1. 메소드명: getPw
     *  2. 메소드 역할: 사용자가 입력한 id에 해당하는 pw를 서버에서 가지고 온다.
     *  3. 입력파라미터
     *      1) id: 사용자가 입력한 id
     *  4. 출력파라미터: pw - db에서 가져온 pw
     * */
    public String getPw(String id){
        String pw = null;

        return pw;
    }

    /**
     *  1. 메소드명: getPw
     *  2. 메소드 역할: 사용자가 입력한 id에 해당하는 pw를 서버에서 가지고 온다.
     *  3. 입력파라미터
     *      1) id: 사용자가 입력한 id
     *  4. 출력파라미터: pw - db에서 가져온 pw
     * */
    public String getPw(String id, String phoneNum){
        String pw = null;

        return pw;
    }

    /**
     *  1. 메소드명: isNotExistedId
     *  2. 메소드 역할: 사용자가 입력한 id가 기존에 존재하는 id인지 db에서 검색.
     *  3. 입력파라미터
     *      1) inputId: 사용자가 입력한 id
     *  4. 출력파라미터: boolean - 입력id가 존재하지 않는지 여부(true: 존재하지 않음(사용가능), false: 존재함(사용불가))
     * */
    public boolean isNotExistedId(String inputId){
        String dbId = this.getId(inputId);
        return !(inputId == dbId);
    }

    /**
     *  1. 메소드명: saveInvCode
     *  2. 메소드 역할: 사용자의 초대코드를 서버에 저장한다.
     *  3. 입력파라미터
     *      1) phoneNum: 사용자 전화번호
     * */
    public void saveInvCode(String id, int invCode){

    }

    /**
     *  1. 메소드명: isExistedInvCode
     *  2. 메소드 역할: 상대방의 초대코드가 존재하는지 여부를 판단 후 존재한다면 상대방의 id를 반환한다.
     *  3. 입력파라미터
     *      1) opponentInvCode: 상대방의 초대코드
     *  4. 출력파라미터: 상대방 id
     * */
    public String isExistedInvCodeAndReturnOppoId(int opponentInvCode){
        String opponentId = null;
        return opponentId;
    }

    /**
     *  1. 메소드명: saveIds
     *  2. 메소드 역할: 사용자와 상대방의 id를 저장한다.
     *  3. 입력파라미터
     *      1) myId: 사용자 id
     *      2) opponentId: 상대방 id
     * */
    public void saveIds(String myId, String opponentId){

    }

    /**
     *  1. 메소드명: bringCoupleId
     *  2. 메소드 역할: 커플의 id를 가지고 온다.
     *  3. 입력파라미터
     *      1) id: 조회할 id
     *  4. 출력파라미터: coupleId - 조회할 id와 커플인자의 id
     * */
    public String bringCoupleId(String id){
        String coupleId = null;
        return coupleId;
    }

    /**
     *  1. 메소드명: bringUserInfo
     *  2. 메소드 역할: 서버에서 유저정보를 가져온다.
     *  3. 출력파라미터
     *      1) userInfo: 서버에서 가져온 유저정보 객체
     * */
    public UserDto bringUserInfo(){
        UserDto userInfo = null;

        return userInfo;
    }

    /**
     *  1. 메소드명: saveLoginInfo
     *  2. 메소드 역할: 사용자의 로그인 정보를 저장한다.
     *  3. 입력파라미터
     *      1) userInfo: 사용자정보
     * */
    public void saveUserInfo(UserDto userInfo){

    }


    /**
     *  1. 메소드명: getId
     *  2. 메소드 역할: 사용자가 입력한 id를 가지고 동일한 id가 있는지 조회
     *  3. 입력파라미터
     *      1) name: 사용자명
     *      2) phoneNum: 사용자 전화번호
     * */
    private String getId(String inputId) {
        String id = null;
        return id;
    }

    /**
     *  1. 메소드명: getId
     *  2. 메소드 역할: 사용자의 이름과 전화번호를 이용해서 id를 조회한다.
     *  3. 입력파라미터
     *      1) name: 사용자명
     *      2) phoneNum: 사용자 전화번호
     * */
    public String getId(String name, String phoneNum) {
        String id = null;
        return id;
    }

    /**
     *  1. 메소드명: savePw
     *  2. 메소드 역할: 사용자의 비밀번호를 서버에 저장한다.
     *  3. 입력파라미터
     *      1) tempPw: 임시비밀번호
     * */
    public void savePw(String tempPw) {
        try {
            String encPw = Util.EncBySha256(tempPw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  1. 메소드명: updateUserInfo
     *  2. 메소드 역할: 사용자가 변경한 비밀번호와 전화번호 정보를 수정한다.
     *  3. 입력파라미터
     *      1) tempPw: 임시비밀번호
     * */
    public void updateUserInfo(String id, String pw, String phoneNum) {
        try {
            String encPw = Util.EncBySha256(pw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  1. 메소드명: savePost
     *  2. 메소드 역할: 게시물 작성 시 서버에 저장한다.
     *  3. 입력파라미터
     *      1) post: 게시물 객체
     * */
    public String savePost(PostDto post) {
        String postId = null;
        return postId;
    }

    /**
     *  1. 메소드명: savePost
     *  2. 메소드 역할: 게시물 작성 시 서버에 저장한다.
     *  3. 입력파라미터
     *      1) post: 게시물 객체
     * */
    public void saveImagesOrVideo(String postId, String[] imagesOrVideoUri) {

    }

    /**
     *  1. 메소드명: readAllPosts
     *  2. 메소드 역할: 서버에 있는 모든게시물을 가져온다
     *  (onCreate에서 한번만 호출, 그 이후 갱신되는 것은, onStart() or onResume()에서 구현
     *    - 글을 나만 쓰는게 아니라 상대방도 쓸 수 있음
     *    - 현재 HomeActivity에 있는대 게시물이 업데이트 된다면 onResume()에서 되야될 것 같다.)
     *  3. 출력파라미터
     *      1) postList - 모든 게시물 리스트
     * */
    public ArrayList<PostDto> readAllPosts() {
        ArrayList<PostDto> postList = new ArrayList<>();
        return postList;
    }

    /**
     *  1. 메소드명: readPost
     *  2. 메소드 역할: 특정 게시물 가져오기
     *  3. 입력파라미터: key - 해당 게시물의 키값
     *  4. 출력파라미터
     *      1) post - 키값에 해당하는 게시물객체
     * */
    public PostDto readPost(String key) {
        PostDto post = null;
        return post;
    }

    /**
     *  1. 메소드명: readImagesOrVideo
     *  2. 메소드 역할: 서버에서 해당 게시물의 파일데이터 가져오기
     *  3. 입력파라미터: postId - 해당 게시물의 id
     *  4. 출력파라미터
     *      1) fileUriArr - 해당 게시물의 id 해당하는 파일경로
     * */
    public String[] readImagesOrVideo(String postId) {
        String[] fileUriArr = null;
        return fileUriArr;
    }

    /**
     *  1. 메소드명: savePost
     *  2. 메소드 역할: 게시물 작성 시 서버에 저장한다.
     *  3. 입력파라미터
     *      1) post: 게시물 객체
     * */
    public void saveComment(CommentDto comment) {

    }

    /**
     *  1. 메소드명: readAllComments
     *  2. 메소드 역할: 서버에 있는 특정 게시물에 해당하는 모든 댓글을 가지고 온다.
     *  (onCreate에서 한번만 호출, 그 이후 갱신되는 것은, onStart() or onResume()에서 구현
     *    - 글을 나만 쓰는게 아니라 상대방도 쓸 수 있음)
     *  3. 출력파라미터
     *      1) commentList - 특정 게시물의 모든 댓글 리스트
     * */
    public ArrayList<CommentDto> readAllComments() {
        ArrayList<CommentDto> commentList = new ArrayList<>();
        return commentList;
    }

    /**
     *  1. 메소드명: readComment
     *  2. 메소드 역할: 특정 게시물 가져오기
     *  3. 입력파라미터: key - 해당 게시물의 키값
     *  4. 출력파라미터
     *      1) post - 키값에 해당하는 게시물객체
     * */
    public CommentDto readComment(String commentId) {
        CommentDto comment = null;
        return comment;
    }
}
