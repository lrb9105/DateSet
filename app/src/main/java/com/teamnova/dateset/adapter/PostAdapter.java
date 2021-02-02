package com.teamnova.dateset.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.post_writing.PostModifyActivity;
import com.teamnova.dateset.post_writing.comment.CommentActivity;

import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.prnd.readmore.ReadMoreTextView;

import static com.teamnova.dateset.home.HomeActivity.bitmapUser1;
import static com.teamnova.dateset.home.HomeActivity.bitmapUser2;
import static com.teamnova.dateset.home.HomeActivity.sharedKeyDto;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable {
    // key 값
    private String key;

    private ArrayList<PostDto> mDataList;
    private Context context;
    private UserDto userInfo;

    // db참조 객체
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseCommentRef;
    //private SharedKeyDto sharedKeyDto;

    // 이 객체
    PostAdapter postAdapter = this;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout layout_selected_photo;
        //ViewPager2 viewPager;
        TextView textViewNickName;
        TextView textViewDpw;
        TextView textView_comment;
        ReadMoreTextView textViewPost;
        ImageButton btn_post_menu;
        LinearLayout layout_comment_write;
        CircleImageView user_profile;
        ImageView postImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            layout_selected_photo = itemView.findViewById(R.id.layout_selected_photo);
            //viewPager = itemView.findViewById(R.id.viewPager);
            textViewNickName = itemView.findViewById(R.id.textView_nickName);
            textViewDpw = itemView.findViewById(R.id.textView_DPW);
            textView_comment = itemView.findViewById(R.id.textView_comment);
            textViewPost = itemView.findViewById(R.id.textView_post);
            btn_post_menu = itemView.findViewById(R.id.btn_post_menu);
            layout_comment_write = itemView.findViewById(R.id.layout_comment_write);
            user_profile = itemView.findViewById(R.id.user_profile);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }

    public class MyViewHolderOppo extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout layout_selected_photo;
        //ViewPager2 viewPager;
        TextView textViewNickName;
        TextView textViewDpw;
        TextView textView_comment;
        ReadMoreTextView textViewPost;
        LinearLayout layout_comment_write;
        CircleImageView user_profile;
        ImageView post_image;

        public MyViewHolderOppo(View itemView) {
            super(itemView);
            layout_selected_photo = itemView.findViewById(R.id.layout_selected_photo);
            //viewPager = itemView.findViewById(R.id.viewPager);
            textViewNickName = itemView.findViewById(R.id.textView_nickName);
            textViewDpw = itemView.findViewById(R.id.textView_DPW);
            textView_comment = itemView.findViewById(R.id.textView_comment);
            textViewPost = itemView.findViewById(R.id.textView_post);
            layout_comment_write = itemView.findViewById(R.id.layout_comment_write);
            user_profile = itemView.findViewById(R.id.user_profile);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(ArrayList<PostDto> mDataList, Context context, UserDto userInfo) {
        this.mDataList = mDataList;
        this.context = context;
        this.userInfo = userInfo;
        this.key =userInfo.getSharedKey();
        this.mDatabaseRef = FirebaseDatabase.getInstance().getReference("posts/" +key);
        this.mDatabaseCommentRef = FirebaseDatabase.getInstance().getReference("comments/" +key);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);
        View itemViewOppo = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post_oppo, parent, false);

        if(viewType == 0){
            MyViewHolder vh = new MyViewHolder(itemView);
            Log.d("debug_viewType1111","viewType: "+viewType);
            return vh;
        } else{
            MyViewHolderOppo vh2 = new MyViewHolderOppo(itemViewOppo);
            Log.d("debug_viewType2222","viewType: "+viewType);
            return vh2;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        PostDto postInfo = mDataList.get(position);

        // 내가 작성한 게시물
        if(holder instanceof MyViewHolder) {
            // 게시물 이미지 가져 옴.
            String path = "posts/"+key+"/"+postInfo.getKey()+".JPEG";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(path);

            Log.d("debug_PostImg",path);

            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("debug_PostImg",path + " 이미지 다운로드 성공");
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(context)
                                .load(task.getResult())
                                //.override(1024, 980)
                                .into(((MyViewHolder)holder).postImage);
                        Log.d("debug_postImg1111",((MyViewHolder)holder).postImage.toString());

                    } else {
                        // URL을 가져오지 못하면 토스트 메세지
                        Log.d("debug_PostImg","이미지 다운로드 실패");
                    }
                }
            });

            // 로그인한 사용자가 user1에 들어있다면
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolder)holder).user_profile.setImageBitmap(bitmapUser1);
                Log.d("debug_profile","1111" + bitmapUser1.toString());
            } else{ // 로그인한 사용자가 user2에 들어있다면
                ((MyViewHolder)holder).user_profile.setImageBitmap(bitmapUser2);
                Log.d("debug_profile","2222" + bitmapUser2.toString());
            }

            // 댓글작성 버튼
            ((MyViewHolder)holder).layout_comment_write.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("POST_INFO", postInfo);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("USER_INFO", userInfo);
                    context.startActivity(intent);
                }
            });

            ((MyViewHolder)holder).layout_comment_write.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ((MyViewHolder)holder).layout_comment_write.setBackgroundColor(Color.LTGRAY);
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        ((MyViewHolder)holder).layout_comment_write.setBackgroundColor(Color.TRANSPARENT);
                    }

                    return false;
                }
            });

            // 메뉴버튼
            ((MyViewHolder)holder).btn_post_menu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setItems(R.array.postFunc, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            String[] items = new String[]{"게시물 수정", "게시물 삭제"};
                            if(pos == 0){ //게시물 수정
                                Intent intent = new Intent(context, PostModifyActivity.class);
                                intent.putExtra("POST_INFO", postInfo);
                                intent.putExtra("USER_INFO", userInfo);
                                //intent.putExtra("POST_ADAPTER", (Parcelable) postAdapter);
                                intent.putExtra("position", position);

                                ((Activity)context).startActivityForResult(intent,1000);
                            } else { //게시물 삭제
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("정말 삭제하시겠습니까?");

                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        String keyOfPost = postInfo.getKey();
                                        // 댓글 먼저 지우기
                                        mDatabaseCommentRef.child(keyOfPost).removeValue();
                                        // 게시물 지우기
                                        mDatabaseRef.child(keyOfPost).removeValue();
                                        deleteItem(position);
                                        // 이미지 지우기
                                        StorageReference postStorageRef = FirebaseStorage.getInstance().getReference("posts/" + key + "/" + keyOfPost + ".JPEG");
                                        postStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                    }
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

            ((MyViewHolder)holder).btn_post_menu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ((MyViewHolder)holder).btn_post_menu.setBackgroundColor(Color.LTGRAY);
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        ((MyViewHolder)holder).btn_post_menu.setBackgroundColor(Color.TRANSPARENT);
                    }

                    return false;
                }
            });

            ((MyViewHolder)holder).textViewNickName.setText(postInfo.getWriter());
            String date = postInfo.getDate();
            String place =  postInfo.getPlace();
            String weather = postInfo.getWeather();

            ((MyViewHolder)holder).textViewDpw.setText( date + (place.equals("") ? "" : " \n " + "'" + place + "'" +"에서.. ") + (weather.equals("날씨")? "" : " 날씨는" + "'" + weather + "'"));
            ((MyViewHolder)holder).textViewPost.setText(postInfo.getTextContent());
            if(postInfo.getCommentCount() != 0){
                ((MyViewHolder)holder).textView_comment.setText("댓글 " + postInfo.getCommentCount() + "개 모두보기");
            }
        } else if(holder instanceof MyViewHolderOppo){ //상대방이 작성한 게시물
            // 게시물 이미지 가져 옴.
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("posts/"+key+"/"+postInfo.getKey()+".JPEG");
            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("debug_img","111111");
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(context)
                                .load(task.getResult())
                                .into(((MyViewHolderOppo)holder).post_image);
                                //.override(1024, 980)
                        Log.d("debug_postImgTask.getResult()", task.getResult().toString());
                        Log.d("debug_postImg2222",((MyViewHolderOppo)holder).post_image.toString());
                    } else {
                        // URL을 가져오지 못하면 토스트 메세지
                        Log.d("debug_postImg333333333333","33333333");
                    }
                }
            });

            // 로그인한 사용자가 user1에 들어있다면 상대방은 user2에 들어있음
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolderOppo)holder).user_profile.setImageBitmap(bitmapUser2);
                Log.d("debug_profile","3333" + bitmapUser2.toString());
            } else{ // 로그인한 사용자가 user2에 들어있다면 상대방은 user1에 들어있음
                ((MyViewHolderOppo)holder).user_profile.setImageBitmap(bitmapUser1);
                Log.d("debug_profile","4444" + bitmapUser1.toString());
            }

            // 댓글작성 버튼
            ((MyViewHolderOppo)holder).layout_comment_write.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("POST_INFO", postInfo);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("USER_INFO", userInfo);
                    context.startActivity(intent);
                }
            });

            ((MyViewHolderOppo)holder).layout_comment_write.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ((MyViewHolderOppo)holder).layout_comment_write.setBackgroundColor(Color.LTGRAY);
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        ((MyViewHolderOppo)holder).layout_comment_write.setBackgroundColor(Color.TRANSPARENT);
                    }

                    return false;
                }
            });

            ((MyViewHolderOppo)holder).textViewNickName.setText(postInfo.getWriter());
            String date = postInfo.getDate();
            String place =  postInfo.getPlace();
            String weather = postInfo.getWeather();

            ((MyViewHolderOppo)holder).textViewDpw.setText( date + (place.equals("") ? "" : " \n " + "'" + place + "'" +"에서.. ") + (weather.equals("")? "" : " 날씨는" + "'" + weather + "'"));
            ((MyViewHolderOppo)holder).textViewPost.setText(postInfo.getTextContent());
            if(postInfo.getCommentCount() != 0){
                ((MyViewHolderOppo)holder).textView_comment.setText("댓글 " + postInfo.getCommentCount() + "개 모두보기");
            }
        }

        /*// 이미지 세팅
        // 로그인한 사용자가 작성한 글
        if(postInfo.getId().equals(userInfo.getId())){
            // 로그인한 사용자가 user1에 들어있다면
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                holder.user_profile.setImageBitmap(bitmapUser1);
            } else{ // 로그인한 사용자가 user2에 들어있다면
                holder.user_profile.setImageBitmap(bitmapUser2);
            }
        } else{ // 상대방이 작성한글
            // 로그인한 사용자가 user1에 들어있다면 상대방은 user2에 들어있음
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                holder.user_profile.setImageBitmap(bitmapUser2);
            } else{ // 로그인한 사용자가 user2에 들어있다면 상대방은 user1에 들어있음
                holder.user_profile.setImageBitmap(bitmapUser1);
            }
        }*/

        /*// 이미지 없으면 안보이도록
        holder.layout_selected_photo.setVisibility(View.GONE);

        // 댓글작성 버튼
        holder.layout_comment_write.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("POST_INFO", postInfo);
                intent.putExtra("POSITION", position);
                intent.putExtra("USER_INFO", userInfo);
                context.startActivity(intent);
            }
        });

        holder.layout_comment_write.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.layout_comment_write.setBackgroundColor(Color.LTGRAY);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    holder.layout_comment_write.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });*/

        /*if(this.userInfo.getId().equals(postInfo.getId())){
            holder.btn_post_menu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setItems(R.array.postFunc, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            String[] items = new String[]{"게시물 수정", "게시물 삭제"};
                            if(pos == 0){ //게시물 수정
                                Intent intent = new Intent(context, PostModifyActivity.class);
                                intent.putExtra("POST_INFO", postInfo);
                                intent.putExtra("USER_INFO", userInfo);
                                //intent.putExtra("POST_ADAPTER", (Parcelable) postAdapter);
                                intent.putExtra("position", position);

                                ((Activity)context).startActivityForResult(intent,1000);
                            } else { //게시물 삭제
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("정말 삭제하시겠습니까?");

                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        String key = postInfo.getKey();
                                        // 댓글 먼저 지우기
                                        mDatabaseCommentRef.child(key).removeValue();
                                        // 게시물 지우기
                                        mDatabaseRef.child(key).removeValue();
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

            holder.btn_post_menu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        holder.btn_post_menu.setBackgroundColor(Color.LTGRAY);
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        holder.btn_post_menu.setBackgroundColor(Color.TRANSPARENT);
                    }

                    return false;
                }
            });
        } else{
            holder.btn_post_menu.setVisibility(View.GONE);
        }*/


        /*holder.textViewNickName.setText(postInfo.getWriter());
        String date = postInfo.getDate();
        String place =  postInfo.getPlace();
        String weather = postInfo.getWeather();

        holder.textViewDpw.setText( date + (place.equals("") ? "" : " \n " + "'" + place + "'" +"에서.. ") + (weather.equals("")? "" : " 날씨는" + "'" + weather + "'"));
        holder.textViewPost.setText(postInfo.getTextContent());
        if(postInfo.getCommentCount() != 0){
            holder.textView_comment.setText("댓글 " + postInfo.getCommentCount() + "개 모두보기");
        }*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private void deleteItem(int position){
        mDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
    }

    // 게시물을 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
    public void addPostInfo(PostDto postInfo, String key){
        postInfo.setKey(key);
        mDataList.add(postInfo);
        notifyItemInserted(getItemCount());
    }

    public void modifyPostInfo(int position, PostDto postInfo){
        Log.d("debug_modifyPostInfo","눌림");
        mDataList.set(position,postInfo);
        notifyItemChanged(position);
    }

    // 모든 아이템 삭제
    public void clearAllItem(){
        int size = mDataList.size();
        mDataList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public PostDto getPostDto(int position){
        return mDataList.get(position);
    }

    public int getPosition(String keyOfPost){
        int position = 0;
        for(position = 0; position < mDataList.size(); position++){
            if(mDataList.get(position).getKey() != null){
                if(mDataList.get(position).getKey().equals(keyOfPost)){
                    break;
                }
            }
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        // 내가 작성한 게시물
        if(this.userInfo.getId().equals(mDataList.get(position).getId())){
            viewType = 0;
        } else{ //상대방이 작성한 게시물
            viewType = 1;
        }

        return viewType;
    }
}
