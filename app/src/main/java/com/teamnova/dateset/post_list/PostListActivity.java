package com.teamnova.dateset.post_list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.AddedFunctionActivity;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.adapter.PostAdapter;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.login.mypage.MyPageActivity;
import com.teamnova.dateset.post_writing.PostWritingActivity;
import com.teamnova.dateset.util.ProgressLoadingDialog;

import java.util.ArrayList;

import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class PostListActivity extends AppCompatActivity implements View.OnClickListener {
    // key 값
    private String key;

    private LinearLayout layout_home;
    private LinearLayout layout_post_list;
    private LinearLayout layout_post_writing;
    private LinearLayout layout_chat;
    private LinearLayout layout_added_function;

    private ImageButton btn_home;
    private ImageButton btn_post_list;
    private ImageButton btn_post_writing;
    private ImageButton btn_chat;
    private ImageButton btn_added_function;

    //recyclerview
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private LinearLayoutManager layoutManager;

    // 데이터
    ArrayList<PostDto> itemList;

    // db참조 객체
    private DatabaseReference mDatabaseRef;
    private View[] viewArr;

    // 로딩바
    private ProgressLoadingDialog customProgressDialog;

    private UserDto userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        userInfo = (UserDto)getIntent().getSerializableExtra("USER_INFO");

        if(userInfo != null){
            this.key = userInfo.getSharedKey();
        } else{
            Toast.makeText(this, "userInfo가 널입니다...", Toast.LENGTH_SHORT).show();
        }

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressLoadingDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        /*Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");
        Log.d("debug_onCreate_Home","onCreate()호출");*/

        // 데이터 리스트
        itemList = new ArrayList<>();

        // db 참조객체
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("posts/" + key);

        // 리사이클러뷰
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_post_area);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // 게시글의 길이에 따라 리사이클러뷰의 크기가 변해야 한다.
        recyclerView.setHasFixedSize(false);

        // 리니어레이아웃 매니저
        layoutManager = new LinearLayoutManager(this);

        // 역순으로 출력
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);

        // 어댑터
        postAdapter = new PostAdapter(itemList, PostListActivity.this, userInfo);
        recyclerView.setAdapter(postAdapter);

       /*// 데이터 가져오기
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostDto postInfo = dataSnapshot.getValue(PostDto.class);
                    String keyOfPost = dataSnapshot.getKey();

                    postInfo.setKey(keyOfPost);

                    // key가 등록되어있지 않다면 등록
                    if(postInfo.getKey() == null){
                        mDatabaseRef.child(keyOfPost).setValue(postInfo);
                    }

                    postAdapter.addPostInfo(postInfo, keyOfPost);

                    // 댓글 추가 시 화면 갱신
                    DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("comments/"+key+"/"+keyOfPost);
                    mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int commentCnt = (int)snapshot.getChildrenCount();
                            postInfo.setCommentCount(commentCnt);

                            // key값으로 position값 찾는다.
                            int position = postAdapter.getPosition(keyOfPost);

                            postAdapter.modifyPostInfo(position,postInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Log.d("debug_postKey",keyOfPost);
                    //Log.d("debug_", "ValueEventListener : " + dataSnapshot.getValue(PostDto.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        // 하단 레이아웃 초기화
        layout_home = findViewById(R.id.layout_home);
        layout_post_list = findViewById(R.id.layout_post_list);
        layout_post_writing = findViewById(R.id.layout_post_writing);
        layout_chat = findViewById(R.id.layout_chat);
        //layout_added_function = findViewById(R.id.layout_added_function);

        layout_home.setOnClickListener(this);
        layout_post_list.setOnClickListener(this);
        layout_post_writing.setOnClickListener(this);
        layout_chat.setOnClickListener(this);
        //layout_added_function.setOnClickListener(this);


        //하단 버튼 초기화
        btn_home = findViewById(R.id.btn_home);
        btn_post_list = findViewById(R.id.btn_post_list);
        btn_post_writing = findViewById(R.id.btn_post_writing);
        btn_chat = findViewById(R.id.btn_chat);
        //btn_added_function = findViewById(R.id.btn_added_function);

        btn_home.setOnClickListener(this);
        btn_post_list.setOnClickListener(this);
        btn_post_writing.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        //btn_added_function.setOnClickListener(this);

        viewArr = initializeView();

        setBackGroundColor(R.id.layout_post_list);


         /* Intent 사용
         postInfo = (PostDto)intent.getSerializableExtra("POST_INFO");
        Log.d("debug_onCreate_Home","onCreate()호출");
        if(postInfo != null){
            Log.d("debug_USER_INFO",postInfo.getTextContent());
            Log.d("debug_USER_INFO",postInfo.getDate());
            Log.d("debug_USER_INFO",postInfo.getPlace());
            Log.d("debug_USER_INFO",postInfo.getPlace());

            for(String str : postInfo.getimgList()){
                Log.d("debug_USER_INFO",str);
            }
        }

        // 게시물 세팅
        if(postInfo != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.view_post,postContainer,true);

            TextView textViewNickName = (TextView) postContainer.findViewById(R.id.textView_nickName);
            TextView textViewDpw = (TextView)postContainer.findViewById(R.id.textView_DPW);
            TextView textViewPost = (TextView)postContainer.findViewById(R.id.textView_post);
            ImageButton btn_post_menu = (ImageButton)postContainer.findViewById(R.id.btn_post_menu);
            LinearLayout layout_comment_write = findViewById(R.id.layout_comment_write);

            layout_comment_write.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                    startActivity(intent);
                }
            });

            btn_post_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(HomeActivity.this, "11", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                    builder.setItems(R.array.postFunc, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            String[] items = getResources().getStringArray(R.array.postFunc);
                            if(pos == 0){ //게시물 수정
                            } else { //게시물 삭제
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            textViewNickName.setText(postInfo.getWriter());
            textViewDpw.setText(postInfo.getDate() + "/" + postInfo.getPlace() + "/" + postInfo.getWeather());
            textViewPost.setText(postInfo.getTextContent());
        }*/
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Class cls = null;

        switch(v.getId()){
            case  R.id.layout_home:
            case  R.id.btn_home:
                layout_home.setBackgroundColor(Color.GRAY);
                cls = HomeActivity.class;
                setBackGroundColor(R.id.layout_home);
                break;
            case  R.id.layout_post_list:
            case  R.id.btn_post_list:
                cls = PostListActivity.class;
                setBackGroundColor(R.id.layout_post_list);
                break;
            case  R.id.layout_post_writing:
            case  R.id.btn_post_writing:
                cls = PostWritingActivity.class;
                setBackGroundColor(R.id.layout_post_writing);
                break;
            case  R.id.layout_chat:
            case  R.id.btn_chat:
                cls = ChattingActivity.class;
                setBackGroundColor(R.id.layout_chat);
                break;
/*            case  R.id.layout_added_function:
            case  R.id.btn_added_function:
                cls = AddedFunctionActivity.class;
                setBackGroundColor(R.id.layout_added_function);
                break;
            default:
                break;*/
        }

        if((v.getId() != R.id.layout_post_list) && (v.getId() != R.id.btn_post_list)){
            intent = new Intent(PostListActivity.this, cls);
            if(userInfo != null){
                intent.putExtra("USER_INFO",userInfo);
            }

            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBackGroundColor(R.id.layout_post_list);

        // 모든 데이터 삭제
        postAdapter.clearAllItem();

        // 데이터 가져오기
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);

                if(snapshot.getChildrenCount() == 0){
                    customProgressDialog.dismiss();
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostDto postInfo = dataSnapshot.getValue(PostDto.class);
                    String keyOfPost = dataSnapshot.getKey();

                    postInfo.setKey(keyOfPost);

                    // key가 등록되어있지 않다면 등록
                    if(postInfo.getKey() == null){
                        mDatabaseRef.child(keyOfPost).setValue(postInfo);
                    }

                    postAdapter.addPostInfo(postInfo, keyOfPost);

                    // 댓글 추가 시 화면 갱신
                    DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("comments/"+key+"/"+keyOfPost);
                    mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int commentCnt = (int)snapshot.getChildrenCount();
                            postInfo.setCommentCount(commentCnt);

                            // key값으로 position값 찾는다.
                            int position = postAdapter.getPosition(keyOfPost);

                            postAdapter.modifyPostInfo(position,postInfo);
                            customProgressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            customProgressDialog.dismiss();
                        }
                    });
                    Log.d("debug_postKey",keyOfPost);
                    //Log.d("debug_", "ValueEventListener : " + dataSnapshot.getValue(PostDto.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customProgressDialog.dismiss();
            }
        });
    }

    public void setBackGroundColor(int id){
        int i = 0;
        for(View view : viewArr){
            if(view.getId() == id){
                Log.d("debug_nullCheck",""+ i);
                view.setBackgroundColor(Color.GRAY);
            } else{
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            i++;
        }
    }

    private View[] initializeView(){
        View[] viewArr = new View[4];
        viewArr[0] = layout_home;
        viewArr[1] = layout_post_list;
        viewArr[2] = layout_post_writing;
        viewArr[3] = layout_chat;
        return viewArr;
    }

    // 댓글 액티비티에서 뒤로가기 클릭한 경우
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "!!!1111", Toast.LENGTH_SHORT).show();
        Log.d("debug_onActivityResult","눌림");
        if(requestCode == 1000 && resultCode == RESULT_OK){
            int commentCount = data.getIntExtra("COMMENT_COUNT",0);
            int position = data.getIntExtra("POSITION",0);

            Log.d("debug_onActivityResult","눌림222");

            PostDto postInfo = postAdapter.getPostDto(position);
            postInfo.setCommentCount(commentCount);

            postAdapter.modifyPostInfo(position, postInfo);

        }
    }
}