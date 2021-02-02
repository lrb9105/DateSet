package com.teamnova.dateset.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.SharedKeyDto;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.teamnova.dateset.home.HomeActivity.bitmapUser1;
import static com.teamnova.dateset.home.HomeActivity.bitmapUser2;
import static com.teamnova.dateset.home.HomeActivity.sharedKeyDto;
import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChattingDto> chattingList;
    private String myId;
    private Context context;

    // 내가 작성한 채팅을 보여주는 뷰홀더
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;

        public MyViewHolder(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 상대방이 작성한 채팅을 보여주는 뷰홀더
    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;

        public MyViewHolder2(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 내가 작성한 채팅과 날짜를 보여주는 뷰홀더
    public class MyViewHolder3 extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;

        public MyViewHolder3(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }

    // 상대방이 작성한 채팅채팅과 날짜를 보여주는 뷰홀더
    public class MyViewHolder4 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;

        public MyViewHolder4(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ChattingAdapter(ArrayList<ChattingDto> chattingList, String myId, Context context) {
        this.chattingList = chattingList;
        this.myId = myId;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemViewMy = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_chatbox_my, parent, false);
        View itemViewOppo = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_chatbox_oppo, parent, false);
        View itemViewOppWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_chatbox_my_with_date, parent, false);
        View itemViewOppoWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_chatbox_oppo_with_date, parent, false);

        if(viewType == 0){ // 내가 작성한 채팅
            ChattingAdapter.MyViewHolder vh = new ChattingAdapter.MyViewHolder(itemViewMy);
            return vh;
        } else if(viewType == 1){ // 상대방이 작성한 채팅
            ChattingAdapter.MyViewHolder2 vh2 = new ChattingAdapter.MyViewHolder2(itemViewOppo);
            return vh2;
        } else if(viewType == 2){ // 내가 작성했고 날짜 보여줘야 할 때
            ChattingAdapter.MyViewHolder3 vh3 = new ChattingAdapter.MyViewHolder3(itemViewOppWithDate);
            return vh3;
        } else { // 상대방이 작성했고 날짜 보여줘야 할 때
            ChattingAdapter.MyViewHolder4 vh4 = new ChattingAdapter.MyViewHolder4(itemViewOppoWithDate);
            return vh4;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChattingDto chattingDto = chattingList.get(position);

/*        Log.d("debug_1",bitmapUser1.toString());
        Log.d("debug_2",bitmapUser2.toString());*/

        // 내가 작성한 메시지일 경우
        if(holder instanceof MyViewHolder){
            //holder.textView_my_nickName.setText(chattingDto.getFromNickName());
            // user1에 내 아이디가 들어있다면
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolder)holder).user_profile.setImageBitmap(bitmapUser1);
            } else{ //user2에 내아이디가 들어있다면
                ((MyViewHolder)holder).user_profile.setImageBitmap(bitmapUser2);
            }

            ((MyViewHolder)holder).editText_MyMsg.setText(chattingDto.getContents());
            ((MyViewHolder)holder).textViewMyTime.setText(chattingDto.getTime());
        } else if(holder instanceof MyViewHolder2){ //상대방이 작성한 메시지인 경우
            // user1에 상대방 아이디가 들어있다면
            if(!userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolder2)holder).user_profile.setImageBitmap(bitmapUser1);
            } else{ //user2에 상대방 아이디가 들어있다면
                ((MyViewHolder2)holder).user_profile.setImageBitmap(bitmapUser2);
            }

            ((MyViewHolder2)holder).textView_oppo_nickName.setText(chattingDto.getFromNickName());
            ((MyViewHolder2)holder).editText_OpponentMsg.setText(chattingDto.getContents());
            ((MyViewHolder2)holder).textView_oppo_time.setText(chattingDto.getTime());
        } else if(holder instanceof MyViewHolder3){ //내가 작성했고 날짜를 보여줘야 하는 경우
            // user1에 내 아이디가 들어있다면
            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolder3)holder).user_profile.setImageBitmap(bitmapUser1);
            } else{ //user2에 내아이디가 들어있다면
                ((MyViewHolder3)holder).user_profile.setImageBitmap(bitmapUser2);
            }
            ((MyViewHolder3)holder).textView_date.setText(chattingDto.getDate());
            ((MyViewHolder3)holder).editText_MyMsg.setText(chattingDto.getContents());
            ((MyViewHolder3)holder).textViewMyTime.setText(chattingDto.getTime());
        } else if(holder instanceof MyViewHolder4){ //상대방이 작성했고 날짜를 보여줘야 하는 경우
            // user1에 상대방 아이디가 들어있다면
            if(!userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                ((MyViewHolder4)holder).user_profile.setImageBitmap(bitmapUser1);
            } else{ //user2에 상대방 아이디가 들어있다면
                ((MyViewHolder4)holder).user_profile.setImageBitmap(bitmapUser2);
            }
            ((MyViewHolder4)holder).textView_date.setText(chattingDto.getDate());
            ((MyViewHolder4)holder).textView_oppo_nickName.setText(chattingDto.getFromNickName());
            ((MyViewHolder4)holder).editText_OpponentMsg.setText(chattingDto.getContents());
            ((MyViewHolder4)holder).textView_oppo_time.setText(chattingDto.getTime());
        }
    }


    // Replace the contents of a view (invoked by the layout manager)
    /*@Override
    public void onBindViewHolder(ChattingAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChattingDto chattingDto = chattingList.get(position);

        // 내가 작성한 메시지일 경우
        if(chattingDto.getFromId().equals(myId)){
            //holder.textView_my_nickName.setText(chattingDto.getFromNickName());
            holder.editText_MyMsg.setText(chattingDto.getContents());
            holder.textViewMyTime.setText(chattingDto.getTime());

            // 상대방 메시지는 안보이도록 처리
            holder.textView_oppo_nickName.setVisibility(View.GONE);
            holder.editText_OpponentMsg.setVisibility(View.GONE);
            holder.textView_oppo_time.setVisibility(View.GONE);
        } else{ //상대방이 작성한 메시지인 경우
            holder.textView_oppo_nickName.setText(chattingDto.getFromNickName());
            holder.editText_OpponentMsg.setText(chattingDto.getContents());
            holder.textView_oppo_time.setText(chattingDto.getTime());

            //내가 작성한 메시지는 안보이도록 처리
            holder.textView_my_nickName.setVisibility(View.GONE);
            holder.editText_MyMsg.setVisibility(View.GONE);
            holder.textViewMyTime.setVisibility(View.GONE);
        }
    }*/

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return chattingList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        boolean isMyChat = chattingList.get(position).getFromId().equals(myId);
        boolean isDateShowed = false;

        if(position != 0){
            // 이전 채팅과 현재 채팅의 날짜가 다르다면
            if(!chattingList.get(position).getDate().equals(chattingList.get(position - 1).getDate())) {
                isDateShowed = true;
            }
        }

        if(isMyChat && (position == 0 || isDateShowed)){ // 내가 작성했고 날짜를 보여줘야 할 때
            viewType = 2;
        } else if(!isMyChat && (position == 0 || isDateShowed)){ // 상대방이 작성했고 날짜를 보여줘야 할 때
            viewType = 3;
        } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 0;
        } else if(!isMyChat){ // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 1;
        }

        return viewType;
    }

    // 채팅데이터를 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
   public void addChatInfo(ChattingDto chatInfo){
        chattingList.add(chatInfo);
        notifyItemInserted(getItemCount());
    }

    /*private ArrayList<ChattingDto> chattingList;
    private String myId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        ConstraintLayout layout_my_chatBox;

        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;

        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
        }
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        ConstraintLayout layout_my_chatBox;

        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;

        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;

        public MyViewHolder2(View itemView) {
            super(itemView);
            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ChattingAdapter(ArrayList<ChattingDto> chattingList, String myId) {
        this.chattingList = chattingList;
        this.myId = myId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChattingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_chatbox, parent, false);
        ChattingAdapter.MyViewHolder vh = new ChattingAdapter.MyViewHolder(itemView);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ChattingAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ChattingDto chattingDto = chattingList.get(position);

        // 내가 작성한 메시지일 경우
        if(chattingDto.getFromId().equals(myId)){
            //holder.textView_my_nickName.setText(chattingDto.getFromNickName());
            holder.editText_MyMsg.setText(chattingDto.getContents());
            holder.textViewMyTime.setText(chattingDto.getTime());

            // 상대방 메시지는 안보이도록 처리
            holder.textView_oppo_nickName.setVisibility(View.GONE);
            holder.editText_OpponentMsg.setVisibility(View.GONE);
            holder.textView_oppo_time.setVisibility(View.GONE);
        } else{ //상대방이 작성한 메시지인 경우
            holder.textView_oppo_nickName.setText(chattingDto.getFromNickName());
            holder.editText_OpponentMsg.setText(chattingDto.getContents());
            holder.textView_oppo_time.setText(chattingDto.getTime());

            //내가 작성한 메시지는 안보이도록 처리
            holder.textView_my_nickName.setVisibility(View.GONE);
            holder.editText_MyMsg.setVisibility(View.GONE);
            holder.textViewMyTime.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return chattingList.size();
    }

    // 채팅데이터를 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
   public void addChatInfo(ChattingDto chatInfo){
        chattingList.add(chatInfo);
        notifyItemInserted(getItemCount());
    }*/
}
