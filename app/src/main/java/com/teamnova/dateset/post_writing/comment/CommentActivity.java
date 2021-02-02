package com.teamnova.dateset.post_writing.comment;
import android.app.Activity;
import android.app.ActivityManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.ChattingAdapter;
import com.teamnova.dateset.adapter.CommentAdapter;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.CommentDto;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.prnd.readmore.ReadMoreTextView;

public class CommentActivity extends AppCompatActivity {
    // key값
    private String key = HomeActivity.userInfo.getSharedKey();

    private String postId;

    //컴포넌트
    private ImageButton btn_back;
    private ReadMoreTextView textView_post_output;
    private EditText editText_comment_writing;
    private Button btn_write;
    private TextView textView_nickName;

    //리사이클러뷰
    private RecyclerView recyclerView_comment_area;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentDto> commentList;
    private RecyclerView.LayoutManager layoutManager;

    // user정보
    private UserDto userInfo;

    // POST 정보
    private PostDto postInfo;

    // db 참조객체
    private DatabaseReference mDatabaseRef; // Firebase db에서 읽고 쓰기를 하기위한 db참조 객체

    // context객체
    private Context context = this;

    // position
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // user정보
        Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");

        // 게시물 정보
        postInfo = (PostDto)getIntent().getSerializableExtra("POST_INFO");
        position = getIntent().getIntExtra("POSITION",0);

        Log.d("debug_position",""+position);

        String postId = postInfo.getKey();
        // db 참조객체 가져오기
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("comments/"+key+"/"+postId);

        // 컴포넌트 초기화
        btn_back = findViewById(R.id.btn_back);
        textView_post_output = findViewById(R.id.textView_post_output);
        textView_post_output.setText(postInfo.getTextContent());
        textView_nickName = findViewById(R.id.textView_nickName);
        textView_nickName.setText(userInfo.getNickName());
        editText_comment_writing = findViewById(R.id.editText_comment_writing);

 /*       // focus를 받았을 때 리사이클러뷰의 가장 마지막인덱스로 이동한다.
        editText_comment_writing.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    int lastPosition = commentAdapter.getItemCount()-1;
                    if(lastPosition >= 0){
                        recyclerView_comment_area.smoothScrollToPosition(lastPosition);
                    }
                }
            }
        });

        // 엔터키가 눌리면 마지막 포커스로 이동
        editText_comment_writing.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        int lastPosition = commentAdapter.getItemCount()-1;
                        if(lastPosition >= 0){
                            recyclerView_comment_area.smoothScrollToPosition(lastPosition);
                        }
                }
                return true;
            }
        });*/

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundColor(Color.LTGRAY);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = "";

                if(editText_comment_writing.getText() != null){
                    contents = editText_comment_writing.getText().toString();
                }
                if(!contents.equals("")){
                    writeComment(contents);
                }
            }
        });

        // 댓글을 저장할 리스트, 댓글을 뿌려줄 리사이클러뷰
        commentList = new ArrayList<>();
        recyclerView_comment_area = findViewById(R.id.recyclerView_comment_area);

        // 레이아웃 매니저
        layoutManager = new LinearLayoutManager(this);

        // 리사이클러뷰 세팅
        recyclerView_comment_area.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(commentList,userInfo.getId(),context,postId);
        recyclerView_comment_area.setAdapter(commentAdapter);

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentDto commentInfo = snapshot.getValue(CommentDto.class);
                String commentId = snapshot.getKey();
                commentAdapter.addChatInfo(commentInfo, commentId);
                int lastPosition = commentAdapter.getItemCount()-1;
                recyclerView_comment_area.smoothScrollToPosition(lastPosition);
                Log.d("debug_", "addChildEventListener : " + snapshot.getValue(ChattingDto.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("debug_",snapshot.getValue(ChattingDto.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("debug_",error.getMessage());
            }
        });
    }

    private void writeComment(String contents) {
        //날짜
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy.MM.dd HH:mm");
        Date today = new Date();
        String date = format1.format(today);

        String myId = userInfo.getId();
        String nickName = userInfo.getNickName();

        CommentDto commentDto = new CommentDto(myId,null,nickName,date,contents);
        mDatabaseRef.push().setValue(commentDto);
        editText_comment_writing.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("POSITION",position);
        resultIntent.putExtra("COMMENT_COUNT",commentAdapter.getItemCount());
        setResult(Activity.RESULT_OK,resultIntent);

        Log.d("debug_onBackPressed","눌림");
        finish();
        super.onBackPressed();
    }
}