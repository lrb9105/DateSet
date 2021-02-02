package com.teamnova.dateset.util;

import android.graphics.Bitmap;

import com.teamnova.dateset.dto.CommentDto;
import com.teamnova.dateset.dto.PostDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 1. 클래스명: PostManager
 2. 역할
    1) 게시물 관련 작업을 수행하는 클래스
    2) 게시물 하나만/모두 가져오기, 게시물 게시 등의 작업을 한다.
 */
public class PostManager {
    /**
     *  1. 메소드명: writePost
     *  2. 메소드 역할: 게시물을 작성한다.
     *  3. 입력파라미터
     *      1) post: 게시물 객체
     * */
    public PostDto writePost(String id, String writer,ArrayList<String> imgList, String textContent, String place, String weather) {
        SimpleDateFormat format1 = new SimpleDateFormat ( "YYYY.MM.DD");
        String date = format1.format(new Date());

        PostDto post = new PostDto(id,writer,textContent,place,weather,date);
        //post.setimgList(imgList);
        return post;
    }

    /**
     *  1. 메소드명: readAllPosts
     *  2. 메소드 역할: 서버에 있는 모든게시물을 가져온다
     *  (onCreate에서 한번만 호출, 그 이후 갱신되는 것은, onStart() or onResume()에서 구현
     *    - 글을 나만 쓰는게 아니라 상대방도 쓸 수 있음)
     *  3. 출력파라미터
     *      1) postList - 모든 게시물 리스트
     * */
    public ArrayList<PostDto> readAllPosts() {
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<PostDto> postList = dbManager.readAllPosts();
        return postList;
    }

    /**
     *  1. 메소드명: readPost
     *  2. 메소드 역할: 서버에서 특정 게시물 가져오기
     *  3. 입력파라미터: postId - 해당 게시물의 id
     *  4. 출력파라미터
     *      1) post - 키값에 해당하는 게시물객체
     * */
    public PostDto readPost(String postId) {
        DatabaseManager dbManager = new DatabaseManager();
        PostDto post = dbManager.readPost(postId);
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
        DatabaseManager dbManager = new DatabaseManager();
        String[] filesUri = dbManager.readImagesOrVideo(postId);
        return filesUri;
    }

    /**
     *  1. 메소드명: writePost
     *  2. 메소드 역할: 게시물을 작성한다.
     *  3. 입력파라미터
     *      1) post: 게시물 객체
     * */
    public void writeComment(String postId, String nickName, String commentContents) {
        String commentId = null;
        SimpleDateFormat format1 = new SimpleDateFormat ( "YYYY.MM.DD");
        String date = format1.format(new Date());

        CommentDto comment = new CommentDto(postId,null,nickName,date,commentContents);
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.saveComment(comment);
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
        DatabaseManager dbManager = new DatabaseManager();
        ArrayList<CommentDto> commentList = dbManager.readAllComments();
        return commentList;
    }

    /**
     *  1. 메소드명: readComment
     *  2. 메소드 역할: 서버에서 특정 게시물의 댓글 가져오기
     *  3. 입력파라미터: commentId - 해당 댓글의 id
     *  4. 출력파라미터
     *      1) post - 키값에 해당하는 게시물객체
     * */
    public CommentDto readComment(String commentId) {
        DatabaseManager dbManager = new DatabaseManager();
        CommentDto comment = dbManager.readComment(commentId);
        return comment;
    }

    /**
     *  1. 메소드명: convertToStrList
     *  2. 메소드 역할: 비트맵리스트를 문자열리스트로 변환
     *  3. 입력파라미터: bitmapList - 직접 촬영하거나 갤러리에서 가져온 비트맵리스트
     *  4. 출력파라미터
     *      1) imgList - 비트맵을 문자열로 변환해서 저장한 리스트
     * */
    public ArrayList<String> convertToStrList(ArrayList<Bitmap> bitmapList){
        ArrayList<String> imgList = new ArrayList<>();
        for(Bitmap bitmap : bitmapList){
            String imgStr = BitmapConverter.BitmapToString(bitmap);
            imgList.add(imgStr);
        }
        return imgList;
    }
}
