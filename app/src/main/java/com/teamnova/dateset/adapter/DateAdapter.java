package com.teamnova.dateset.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.weather.WeatherActivity;
import com.teamnova.dateset.dto.DateInterface;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.WeatherDto;
import com.teamnova.dateset.util.Util;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<DateInterface> dateList;
    private Context context;

    public DateAdapter(ArrayList<DateInterface> dateList, Context context) {
        this.dateList = dateList;
        this.context = context;
    }

    // sch정보를 담을 뷰홀더
    public class SchHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_srt_time;
        //ImageView imageView_weatherImg;
        TextView textView_end_time;
        TextView textView_title;

        public SchHolder(View itemView) {
            super(itemView);
            textView_srt_time = itemView.findViewById(R.id.textView_srt_time);
            textView_end_time = itemView.findViewById(R.id.textView_end_time);
            textView_title = itemView.findViewById(R.id.textView_title);
        }
    }

    // anniversary정보를 담을 뷰홀더
    public class AnniversaryHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_title;
        TextView textView_dday;

        public AnniversaryHolder(View itemView) {
            super(itemView);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_dday = itemView.findViewById(R.id.textView_dday);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemViewSch = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_schedule_without_date, parent, false);
        View itemViewAnniversary = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anniversary_without_date, parent, false);

        // Schedule
        if(viewType == 0){
            DateAdapter.SchHolder vh = new DateAdapter.SchHolder(itemViewSch);
            return vh;
        } else { // Anniversary
            DateAdapter.AnniversaryHolder vh2 = new DateAdapter.AnniversaryHolder(itemViewAnniversary);
            return vh2;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // schedule
        if(holder instanceof  DateAdapter.SchHolder){
            DateInterface dto = dateList.get(position);
            ((DateAdapter.SchHolder)holder).textView_srt_time.setText(dto.startTime());
            ((DateAdapter.SchHolder)holder).textView_end_time.setText(dto.endTime());
            ((DateAdapter.SchHolder)holder).textView_title.setText(dto.title());
        } else { //anniversary
            DateInterface dto = dateList.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            int dDay = Util.dateDiff(dto.startDate(), sdf.format(new Date()));
            ((AnniversaryHolder)holder).textView_dday.setText("D"+ (dDay < 0 ? dDay : (dDay == 0 ? "-Day" : "+" + dDay)));
            ((DateAdapter.AnniversaryHolder)holder).textView_title.setText(dto.title());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(dateList.get(position) instanceof ScheduleDto){
            return 0;
        } else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

}
