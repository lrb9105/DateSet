package com.teamnova.dateset.addedfunc.calendar.schedule;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.CommentDto;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.UserDto;

import java.util.ArrayList;

import static com.teamnova.dateset.addedfunc.calendar.schedule.AddScheduleActivity.SCHEDULE_DTO_KEY;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ScheduleDto> scheduleList;
    private UserDto userInfo;
    private Context context;

    // 날짜가 없는 일정 뷰홀더
    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView textView_date;
        TextView textView_srt_time;
        TextView textView_end_time;
        TextView textView_title;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_srt_time = itemView.findViewById(R.id.textView_srt_time);
            textView_end_time = itemView.findViewById(R.id.textView_end_time);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }

    // 날짜가 있는 일정 뷰홀더
    public class ScheduleWithDateViewHolder extends RecyclerView.ViewHolder {
        TextView textView_month;
        TextView textView_date;
        TextView textView_srt_time;
        TextView textView_end_time;
        TextView textView_title;

        public ScheduleWithDateViewHolder(View itemView) {
            super(itemView);
            textView_month = itemView.findViewById(R.id.textView_month);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_srt_time = itemView.findViewById(R.id.textView_srt_time);
            textView_end_time = itemView.findViewById(R.id.textView_end_time);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }

    // 같은요일인 경우
    public class ScheduleWithoutDateViewHolder extends RecyclerView.ViewHolder {
        TextView textView_srt_time;
        TextView textView_end_time;
        TextView textView_title;

        public ScheduleWithoutDateViewHolder(View itemView) {
            super(itemView);
            textView_srt_time = itemView.findViewById(R.id.textView_srt_time);
            textView_end_time = itemView.findViewById(R.id.textView_end_time);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScheduleAdapter(ArrayList<ScheduleDto> scheduleList, UserDto userInfo, Context context) {
        this.scheduleList = scheduleList;
        this.userInfo = userInfo;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemViewSch = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule, parent, false);
        View itemViewSchWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule_with_date, parent, false);
        View itemViewSchWithoutDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule_without_date, parent, false);

        ScheduleAdapter.ScheduleViewHolder vh = new ScheduleAdapter.ScheduleViewHolder(itemViewSch);
        ScheduleAdapter.ScheduleWithDateViewHolder vh2 = new ScheduleAdapter.ScheduleWithDateViewHolder(itemViewSchWithDate);
        ScheduleAdapter.ScheduleWithoutDateViewHolder vh3 = new ScheduleAdapter.ScheduleWithoutDateViewHolder(itemViewSchWithoutDate);

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
        ScheduleDto scheduleDto = scheduleList.get(position);

        TextView textView_month;
        TextView textView_date;
        TextView textView_srt_time;
        TextView textView_end_time;
        TextView textView_title = null;

        // 월의 첫번째 일정이 아닌경우
        if(holder instanceof ScheduleViewHolder){
            textView_date = ((ScheduleViewHolder)holder).textView_date;
            textView_srt_time = ((ScheduleViewHolder)holder).textView_srt_time;
            textView_end_time = ((ScheduleViewHolder)holder).textView_end_time;
            textView_title = ((ScheduleViewHolder)holder).textView_title;

            textView_date.setText(scheduleDto.getStartDateWithDay());
            textView_srt_time.setText(scheduleDto.getStartTime());
            textView_end_time.setText(scheduleDto.getEndTime());
            textView_title.setText(scheduleDto.getTitle());
        } else if(holder instanceof ScheduleWithDateViewHolder){ // 월의 첫번째 일정인 경우
            String month = scheduleDto.getStartDateWithDay().substring(0,9);
            textView_month = ((ScheduleWithDateViewHolder)holder).textView_month;
            textView_date = ((ScheduleWithDateViewHolder)holder).textView_date;
            textView_srt_time = ((ScheduleWithDateViewHolder)holder).textView_srt_time;
            textView_end_time = ((ScheduleWithDateViewHolder)holder).textView_end_time;
            textView_title = ((ScheduleWithDateViewHolder)holder).textView_title;

            textView_month.setText(month);
            textView_date.setText(scheduleDto.getStartDateWithDay());
            textView_srt_time.setText(scheduleDto.getStartTime());
            textView_end_time.setText(scheduleDto.getEndTime());
            textView_title.setText(scheduleDto.getTitle());
        } else if(holder instanceof ScheduleWithoutDateViewHolder){ //해당 날짜의 첫번째 일정이 아닌경우
            textView_srt_time = ((ScheduleWithoutDateViewHolder)holder).textView_srt_time;
            textView_end_time = ((ScheduleWithoutDateViewHolder)holder).textView_end_time;
            textView_title = ((ScheduleWithoutDateViewHolder)holder).textView_title;

            textView_srt_time.setText(scheduleDto.getStartTime());
            textView_end_time.setText(scheduleDto.getEndTime());
            textView_title.setText(scheduleDto.getTitle());
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
                Intent intent = new Intent(context,ModifyScheduleActivity.class);
                intent.putExtra(SCHEDULE_DTO_KEY,scheduleList.get(position));

                Log.d("debug_SchAdapterKey",scheduleList.get(position).getKey());

                intent.putExtra("USER_INFO",userInfo);

                Log.d("debug_SchAdapter",scheduleList.get(position).toString());

                context.startActivity(intent);
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return scheduleList.size();
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
            if(!scheduleList.get(position).getStartDateWithDay().substring(0,9).equals(scheduleList.get(position - 1).getStartDateWithDay().substring(0,9))) {
                isMonthShowed = true;
            }
        }

        if(position != 0){
            // 이전 일정과 현재일정의 날짜가 같으면
            if(scheduleList.get(position).getStartDateWithDay().equals(scheduleList.get(position - 1).getStartDateWithDay())) {
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
   public void addScheduleInfo(ScheduleDto schInfo){
       scheduleList.add(schInfo);
       notifyItemInserted(getItemCount());
    }

    // 일정 삭제
    private void deleteSchedule(int position){
        scheduleList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, scheduleList.size());
    }

    // 일정 수정
    public void modifySchedule(int position, ScheduleDto schInfo){
        scheduleList.set(position,schInfo);
        notifyItemChanged(position);
    }

    public void clearAllItem(){
        int size = scheduleList.size();
        scheduleList.clear();
        notifyItemRangeRemoved(0, size);
    }
}
