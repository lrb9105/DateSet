package com.teamnova.dateset.addedfunc.calendar.anniversary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.dto.DateInterface;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AnniversaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AnniversaryDto> anniversaryList;
    private UserDto userInfo;
    private Context context;

    // 날짜가 없는 일정 뷰홀더
    public class AnniversaryViewHolder extends RecyclerView.ViewHolder {
        TextView textView_date;
        TextView textView_title;
        TextView textView_dday;

        public AnniversaryViewHolder(View itemView) {
            super(itemView);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_dday = itemView.findViewById(R.id.textView_dday);
        }
    }

    // 날짜가 있는 일정 뷰홀더
    public class AnniversaryWithDateViewHolder extends RecyclerView.ViewHolder {
        TextView textView_month;
        TextView textView_date;
        TextView textView_title;
        TextView textView_dday;

        public AnniversaryWithDateViewHolder(View itemView) {
            super(itemView);
            textView_month = itemView.findViewById(R.id.textView_month);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_dday = itemView.findViewById(R.id.textView_dday);
        }
    }

    // 같은요일인 경우
    public class AnniversaryWithoutDateViewHolder extends RecyclerView.ViewHolder {
        TextView textView_title;
        TextView textView_dday;

        public AnniversaryWithoutDateViewHolder(View itemView) {
            super(itemView);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_dday = itemView.findViewById(R.id.textView_dday);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AnniversaryAdapter(ArrayList<AnniversaryDto> anniversaryList, UserDto userInfo, Context context) {
        this.anniversaryList = anniversaryList;
        this.userInfo = userInfo;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemViewSch = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anniversary, parent, false);
        View itemViewSchWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anniversary_with_date, parent, false);
        View itemViewSchWithoutDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anniversary_without_date, parent, false);

        AnniversaryAdapter.AnniversaryViewHolder vh = new AnniversaryAdapter.AnniversaryViewHolder(itemViewSch);
        AnniversaryAdapter.AnniversaryWithDateViewHolder vh2 = new AnniversaryAdapter.AnniversaryWithDateViewHolder(itemViewSchWithDate);
        AnniversaryAdapter.AnniversaryWithoutDateViewHolder vh3 = new AnniversaryAdapter.AnniversaryWithoutDateViewHolder(itemViewSchWithoutDate);

        if(viewType == 0){ // 날짜가 없는 일정
            return vh;
        } else if(viewType == 1){ // 날짜가 있는 일정(새로운 날짜의 첫번쨰 일정)
            return vh2;
        } else{
            return vh3;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        AnniversaryDto anniversaryDto = anniversaryList.get(position);

        TextView textView_month;
        TextView textView_date;
        TextView textView_srt_time;
        TextView textView_end_time;
        TextView textView_title = null;

        // 월의 첫번째 일정이 아닌경우
        if(holder instanceof AnniversaryViewHolder){
            textView_date = ((AnniversaryViewHolder)holder).textView_date;
            textView_title = ((AnniversaryViewHolder)holder).textView_title;

            textView_date.setText(anniversaryDto.getDateWithDay());
            textView_title.setText(anniversaryDto.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            int dDay = Util.dateDiff(anniversaryDto.getDate(), sdf.format(new Date()));
            ((AnniversaryViewHolder)holder).textView_dday.setText("D"+ (dDay < 0 ? dDay : "+" + dDay));
        } else if(holder instanceof AnniversaryWithDateViewHolder){ // 월의 첫번째 일정인 경우
            String month = anniversaryDto.getDateWithDay().substring(0,9);
            textView_month = ((AnniversaryWithDateViewHolder)holder).textView_month;
            textView_date = ((AnniversaryWithDateViewHolder)holder).textView_date;
            textView_title = ((AnniversaryWithDateViewHolder)holder).textView_title;

            textView_month.setText(month);
            textView_date.setText(anniversaryDto.getDateWithDay());

            textView_title.setText(anniversaryDto.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            int dDay = Util.dateDiff(anniversaryDto.getDate(), sdf.format(new Date()));
            ((AnniversaryWithDateViewHolder)holder).textView_dday.setText("D"+ (dDay < 0 ? dDay : "+" + dDay));
        } else if(holder instanceof AnniversaryWithoutDateViewHolder){ //해당 날짜의 첫번째 일정이 아닌경우
            textView_title = ((AnniversaryWithoutDateViewHolder)holder).textView_title;

            textView_title.setText(anniversaryDto.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            int dDay = Util.dateDiff(anniversaryDto.getDate(), sdf.format(new Date()));
            ((AnniversaryWithoutDateViewHolder)holder).textView_dday.setText("D"+ (dDay < 0 ? dDay : "+" + dDay));
        }
        textView_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.LTGRAY);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });

        textView_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ModifyAnniversaryActivity.class);
                intent.putExtra(Anniversary_DTO_KEY,AnniversaryList.get(position));

                Log.d("debug_SchAdapterKey",anniversaryList.get(position).getKey());

                intent.putExtra("USER_INFO",userInfo);

                Log.d("debug_SchAdapter",anniversaryList.get(position).toString());

                context.startActivity(intent);*/
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return anniversaryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;

        boolean isMonthShowed = false;
        boolean isDayNotShowed = false;

        if(position == 0){
            isMonthShowed = true;
        }

        if(position != 0){
            // 이전 일정과 현재일정의 달이 다르면
            if(!anniversaryList.get(position).getDateWithDay().substring(0,9).equals(anniversaryList.get(position - 1).getDateWithDay().substring(0,9))) {
                isMonthShowed = true;
            }
        }

        if(position != 0){
            // 이전 일정과 현재일정의 날짜가 같으면
            if(anniversaryList.get(position).getDateWithDay().equals(anniversaryList.get(position - 1).getDateWithDay())) {
                isDayNotShowed = true;
            }
        }
        if(isMonthShowed){ // 해당 달의 첫번째 데이터 일 떄
            viewType = 1;
        } else if(isDayNotShowed){
            viewType = 2;
        } else { // 달을 보여줄 필요가 없을때(해당 달의 첫번쨰 데이터가 아닐 떄)
            viewType = 0;
        }

        return viewType;
    }

    // 일정데이터를 리싸이클러뷰에 추가하기위한 메소드 => 해당 메소드 호출 시 자동갱신한다.
   public void addAnniversaryInfo(AnniversaryDto schInfo){
       anniversaryList.add(schInfo);
       notifyItemInserted(getItemCount());
    }

    // 일정 삭제
    private void deleteAnniversary(int position){
        anniversaryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, anniversaryList.size());
    }

    // 일정 수정
    public void modifyAnniversary(int position, AnniversaryDto schInfo){
        anniversaryList.set(position,schInfo);
        notifyItemChanged(position);
    }

    public void clearAllItem(){
        int size = anniversaryList.size();
        anniversaryList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
