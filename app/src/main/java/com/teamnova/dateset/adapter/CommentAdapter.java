package com.teamnova.dateset.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.CommentDto;
import com.teamnova.dateset.home.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // key값
    private final String key = userInfo.getSharedKey();

    private ArrayList<CommentDto> commentList;
    private String myId;
    private Context context;
    private String postId;

    // db참조 객체
    private DatabaseReference mDatabaseRef;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_nickName;
        TextView textView_date;
        TextView textView_comment_writing;
        ImageButton btn_comment_menu;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView_nickName = itemView.findViewById(R.id.textView_nickName);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_comment_writing = itemView.findViewById(R.id.textView_comment_writing);
            btn_comment_menu = itemView.findViewById(R.id.btn_comment_menu);
        }
    }

    // 상대방이 작성한 코멘트 - 메뉴버튼 없음
    public class MyViewHolderOppo extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_nickName;
        TextView textView_date;
        TextView textView_comment_writing;

        public MyViewHolderOppo(View itemView) {
            super(itemView);
            textView_nickName = itemView.findViewById(R.id.textView_nickName);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_comment_writing = itemView.findViewById(R.id.textView_comment_writing);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentAdapter(ArrayList<CommentDto> commentList, String myId, Context context, String postId) {
        this.commentList = commentList;
        this.myId = myId;
        this.context = context;
        this.postId = postId;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("comments/" + key + "/" + postId);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment_commentbox, parent, false);
        View itemViewOppo = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment_commentbox_oppo, parent, false);

        // 내가 작성한 댓글
        if(viewType == 0){
            CommentAdapter.MyViewHolder vh = new CommentAdapter.MyViewHolder(itemView);
            return vh;
        } else{ // 상대방이 작성한 댓글
            CommentAdapter.MyViewHolderOppo vh2 = new CommentAdapter.MyViewHolderOppo(itemViewOppo);
            return vh2;
        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        CommentDto commentInfo = commentList.get(position);

        // 내가 작성한 댓글
        if(holder instanceof CommentAdapter.MyViewHolder){
            Log.d("debug_onBindViewHolder","11111");

            ((CommentAdapter.MyViewHolder)holder).textView_nickName.setText(commentInfo.getNickName());
            ((CommentAdapter.MyViewHolder)holder).textView_date.setText(commentInfo.getDate());
            ((CommentAdapter.MyViewHolder)holder).textView_comment_writing.setText(commentInfo.getCommentContents());

            ((CommentAdapter.MyViewHolder)holder).btn_comment_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setItems(R.array.commentFunc, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            String[] items = new String[]{"댓글 수정", "댓글 삭제"};
                            if(pos == 0){ //게시물 수정 클릭
                                // 수정할 수 있는 다이얼로그 생성
                                View dialogView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_modify_comment, null);

                                final EditText comment_modify = (EditText)dialogView.findViewById(R.id.comment_modify);
                                //final Button btn_comment_modify_complete = (Button)dialogView.findViewById(R.id.btn_comment_modify_complete);

                                comment_modify.setText(commentInfo.getCommentContents());
                                comment_modify.requestFocus();

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);

                                builder.setTitle("댓글 수정");
                                builder.setPositiveButton("수정", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int pos)
                                    {
                                        // 수정 완료 시
                                        String key = commentInfo.getCommentId();

                                        commentInfo.setCommentContents(comment_modify.getText().toString());

                                        HashMap<String,Object> modifiedMapInfo = new HashMap<>();
                                        // 수정된 데이터 - 맵객체를 이용한다(key값, 수정된 객체)
                                        modifiedMapInfo.put(key,commentInfo);

                                        // db 업데이트
                                        mDatabaseRef.updateChildren(modifiedMapInfo);
                                        // 리사이클러뷰 업데이트
                                        modifyPostInfo(position,commentInfo);

                                        //postAdapter.modifyPostInfo(position,postInfo);

                                        // 다이얼로그 메시지 생성
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                        builder.setTitle("").setMessage("댓글 수정이 완료되었습니다!");

                                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {}
                                        });

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else { //게시물 삭제
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("정말 삭제하시겠습니까?");

                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        String commentId = commentInfo.getCommentId();

                                        Log.d("debug_postId",postId);
                                        Log.d("debug_commentId",commentId);

                                        mDatabaseRef.child(commentId).removeValue();
                                        deleteItem(position);                                }
                                });

                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {}
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            ((CommentAdapter.MyViewHolder)holder).btn_comment_menu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ((CommentAdapter.MyViewHolder)holder).btn_comment_menu.setBackgroundColor(Color.LTGRAY);
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        ((CommentAdapter.MyViewHolder)holder).btn_comment_menu.setBackgroundColor(Color.TRANSPARENT);
                    }

                    return false;
                }
            });
        } else if(holder instanceof CommentAdapter.MyViewHolderOppo){
            Log.d("debug_onBindViewHolder","22222");
            ((CommentAdapter.MyViewHolderOppo)holder).textView_nickName.setText(commentInfo.getNickName());
            ((CommentAdapter.MyViewHolderOppo)holder).textView_date.setText(commentInfo.getDate());
            ((CommentAdapter.MyViewHolderOppo)holder).textView_comment_writing.setText(commentInfo.getCommentContents());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // 댓글데이터를 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
   public void addChatInfo(CommentDto commentInfo, String commentId){
        commentInfo.setCommentId(commentId);
        commentList.add(commentInfo);
        notifyItemInserted(getItemCount());
    }

    // 댓글데이터 삭제
    private void deleteItem(int position){
        commentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, commentList.size());
    }


    // 댓글 수정
    public void modifyPostInfo(int position, CommentDto commentInfo){
        commentList.set(position,commentInfo);
        notifyItemChanged(position);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        // 내가 작성한 댓글
        if(userInfo.getId().equals(commentList.get(position).getId())){
            Log.d("debug_onBindViewHolder","11111");
            viewType = 0;
        } else{ //상대방이 작성한 댓글
            Log.d("debug_onBindViewHolder","2222");
            viewType = 1;
        }

        return viewType;
    }
}
