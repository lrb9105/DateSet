package com.teamnova.dateset.addedfunc.calendar.schedule;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.ChattingAdapter;
import com.teamnova.dateset.dto.ChattingDto;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.Util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.teamnova.dateset.addedfunc.calendar.schedule.AddScheduleActivity.SCHEDULE_DTO_KEY;

public class ScheduleListActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private String key;

    private ImageButton btn_back;
    private TextView textView_all;
    private TextView textView_common;
    private TextView textView_me;
    private TextView textView_opponent;

    private ScheduleAdapter scheduleAdapter;
    private RecyclerView recyclerView_sch_list;
    private ArrayList<ScheduleDto> schList;
    private RecyclerView.LayoutManager layoutManager;

    private UserDto userInfo;

    // SP Manaager
    private SPManager spManager;

    // db참조 객체
    private DatabaseReference mDatabaseRef; // Firebase db에서 읽고 쓰기를 하기위한 db참조 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        key = HomeActivity.userInfo.getSharedKey();

        userInfo = (UserDto)getIntent().getSerializableExtra("USER_INFO");

        // 컴포넌트 초기화
        btn_back = findViewById(R.id.btn_back);
        textView_all = findViewById(R.id.textView_all);
        textView_common = findViewById(R.id.textView_common);
        textView_me = findViewById(R.id.textView_me);
        textView_opponent = findViewById(R.id.textView_opponent);
        recyclerView_sch_list = findViewById(R.id.recyclerView_sch_list);

        // 이벤트 등록
        btn_back.setOnClickListener(this);
        btn_back.setOnTouchListener(this);

        textView_all.setOnClickListener(this);
        textView_common.setOnClickListener(this);
        textView_me.setOnClickListener(this);
        textView_opponent.setOnClickListener(this);

        // 리싸이클러뷰
        // 일정 정보를 저장할 리스트, 일정 정보를 뿌려줄 리사이클러뷰
        schList = new ArrayList<>();
        recyclerView_sch_list = findViewById(R.id.recyclerView_sch_list);

        // 레이아웃 매니저
        layoutManager = new LinearLayoutManager(this);

        // 리사이클러뷰 세팅
        recyclerView_sch_list.setLayoutManager(layoutManager);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("schedule/"+key);
        Log.d("debug_db",mDatabaseRef.toString());

        textView_all.performClick();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()){
            case R.id.btn_back:
                onBackPressed();
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.textView_all:
                if(scheduleAdapter != null){
                    scheduleAdapter.clearAllItem();
                }

                // 모든 일정 데이터 가져오기
                v.setBackgroundColor(getResources().getColor(R.color.orange));
                textView_common.setBackgroundColor(Color.TRANSPARENT);
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ScheduleDto> schList = new ArrayList<>();

                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ArrayList<ScheduleDto> tempList = new ArrayList<>();
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                dto.setKey(dataByTime.getKey());
                                try {
                                    tempList = getSchListByDate(dto);
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                schList.addAll(tempList);
                            }

                            /*for(int i = 0; i < schList.size(); i++){
                                Log.d("debug_SchListKey",schList.get(i).getKey());
                            }*/
                        }

                        // 시작 날짜, 시간순으로 정렬
                        Collections.sort(schList, new CompareByDate());

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo,ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.textView_common:
                if(scheduleAdapter != null){
                    scheduleAdapter.clearAllItem();
                }

                // schType값이 "0"인 데이터 가져오기
                v.setBackgroundColor(getResources().getColor(R.color.light_blue));
                textView_all.setBackgroundColor(Color.TRANSPARENT);
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ScheduleDto> schList = new ArrayList<>();

                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ArrayList<ScheduleDto> tempList = new ArrayList<>();
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                dto.setKey(dataByTime.getKey());

                                try {
                                    Log.d("debug_schType",dto.getSchType());
                                    if(dto.getSchType().equals("0")){
                                        tempList = getSchListByDate(dto);
                                        Log.d("debug_SchListKey_common_temp",""+tempList.size());
                                    }
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }

                                for(ScheduleDto s : tempList){
                                    schList.add(s);
                                }
                            }

                            for(ScheduleDto s : schList){
                                Log.d("debug_SchListKey_common",""+schList.size());
                            }
                        }

                        // 시작 날짜, 시간순으로 정렬
                        Collections.sort(schList, new CompareByDate());

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo,ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            ArrayList<ScheduleDto> tempList = new ArrayList<>();
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                dto.setKey(dataByTime.getKey());
                                if(dto.getSchType().equals("0")){
                                    tempList.add(dto);
                                }
                            }

                            // 시작 날짜, 시간순으로 정렬
                            Collections.sort(schList, new CompareByDate());

                            for(ScheduleDto dto : tempList){
                                schList.add(dto);
                            }
                        }

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo.getId(), ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
                break;
            case R.id.textView_me:
                if(scheduleAdapter != null){
                    scheduleAdapter.clearAllItem();
                }

                // 작성자 id가 내 id와 동일한 경우 가져오기
                v.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                textView_all.setBackgroundColor(Color.TRANSPARENT);
                textView_common.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ScheduleDto> schList = new ArrayList<>();

                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ArrayList<ScheduleDto> tempList = new ArrayList<>();
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                dto.setKey(dataByTime.getKey());
                                try {
                                    if((dto.getSchType().equals("1")&&dto.getWriter().equals(userInfo.getId()))){
                                        tempList = getSchListByDate(dto);
                                    }
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                schList.addAll(tempList);
                            }
                        }

                        // 시작 날짜, 시간순으로 정렬
                        Collections.sort(schList, new CompareByDate());

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo,ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*if(scheduleAdapter != null){
                    scheduleAdapter.clearAllItem();
                }

                v.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                textView_all.setBackgroundColor(Color.TRANSPARENT);
                textView_common.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);

                // SP에서 데이터 가져오기
                // sp매니저
                spManager = new SPManager(this,"com.teamnova.dateset.schedule",MODE_PRIVATE);
                int maxLength = spManager.getCurrentSize();

                ArrayList<ScheduleDto> schList = new ArrayList<>();

                for(int i = 1; i <= maxLength; i++){
                    ScheduleDto dto = spManager.getDto(SCHEDULE_DTO_KEY,i);
                    if(dto != null){
                        ArrayList<ScheduleDto> tempList = null;
                        if(dto.getWriter().equals(userInfo.getId())){
                            try {
                                tempList = getSchListByDate(dto);
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }

                            schList.addAll(tempList);
                        }
                    }
                }

                Collections.sort(schList, new CompareByDate());

                *//*for(ScheduleDto s : schList){
                    Log.d("debug_List1",s.getStartDateWithDay());
                }*//*

                scheduleAdapter = new ScheduleAdapter(schList, userInfo, ScheduleListActivity.this);
                recyclerView_sch_list.setAdapter(scheduleAdapter);*/

                break;
            case R.id.textView_opponent:
                if(scheduleAdapter != null){
                    scheduleAdapter.clearAllItem();
                }

                // 작성자 id가 내 id와 다른 경우 가져오기
                v.setBackgroundColor(getResources().getColor(R.color.light_brown));
                textView_all.setBackgroundColor(Color.TRANSPARENT);
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_common.setBackgroundColor(Color.TRANSPARENT);

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ScheduleDto> schList = new ArrayList<>();

                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ArrayList<ScheduleDto> tempList = new ArrayList<>();
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                dto.setKey(dataByTime.getKey());
                                try {
                                    if((dto.getSchType().equals("2")&&dto.getWriter().equals(userInfo.getId())) || (dto.getSchType().equals("1")&&!dto.getWriter().equals(userInfo.getId()))){
                                        tempList = getSchListByDate(dto);
                                    }
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                schList.addAll(tempList);
                            }
                        }

                        // 시작 날짜, 시간순으로 정렬
                        Collections.sort(schList, new CompareByDate());

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo,ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            ArrayList<ScheduleDto> tempList = new ArrayList<>();
                            for(DataSnapshot dataByTime : dataByDay.getChildren()){
                                ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                                // 내가 상대방 것을 작성한 경우 혹은 상대방이 본인것을 작성한 경우
                                if((dto.getSchType().equals("2")&&dto.getWriter().equals(userInfo.getId())) || (dto.getSchType().equals("1")&&!dto.getWriter().equals(userInfo.getId()))){
                                    tempList.add(dto);
                                }
                            }

                            // 시작 시간순으로 정렬
                            Collections.sort(tempList);

                            for(ScheduleDto dto : tempList){
                                schList.add(dto);
                            }
                        }

                        scheduleAdapter = new ScheduleAdapter(schList, userInfo.getId(), ScheduleListActivity.this);
                        recyclerView_sch_list.setAdapter(scheduleAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // 작성자 id가 내 id와 다른 경우 가져오기
                v.setBackgroundColor(getResources().getColor(R.color.light_brown));
                textView_all.setBackgroundColor(Color.TRANSPARENT);
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_common.setBackgroundColor(Color.TRANSPARENT);*/
                break;
        }

/*        if(v.getId() != R.id.btn_back){
            scheduleAdapter = new ScheduleAdapter(schList,userInfo.getId(),ScheduleListActivity.this);
            recyclerView_sch_list.setAdapter(scheduleAdapter);
        }*/
    }
    public ArrayList<ScheduleDto> getSchListByDate(ScheduleDto schDto) throws CloneNotSupportedException {
        ArrayList<ScheduleDto> schList = new ArrayList<>();
        String srtDate = schDto.getStartDate();
        String endDate2 = schDto.getEndDate();

        /*Log.d("debug_srtDate",srtDate);
        Log.d("debug_endDate",endDate2);*/

        int repeatCnt = dateDiff(srtDate,endDate2) + 1;

        for(int i = 0; i < repeatCnt; i++){
            String startDate = schDto.getStartDate();
            String startDateWithDay = null;

            if (i != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");

                Calendar c = Calendar.getInstance();
                String day = null;

                try {
                    c.setTime(sdf.parse(startDate));
                    c.add(Calendar.DATE, 1);  //하루를 더해준다.

                    startDate = sdf.format(c.getTime());  // yyyymmdd
                    startDateWithDay = sdf2.format(c.getTime());  // yyyy년 mm월 dd일
                    day = Util.getDateDay(startDateWithDay, "yyyy년 MM월 dd일");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startDateWithDay += " (" + day + ")";

                schDto.setStartDate(startDate);
                schDto.setStartDateWithDay(startDateWithDay);

                /*Log.d("debug_date" + i+": ", startDateWithDay);
                Log.d("debug_date" + i+": ", startDate);*/
            }
            ScheduleDto tempDto = (ScheduleDto)schDto.clone();
            schList.add(tempDto);
        }
        return schList;
    }


    // 시작일, 종료일 차이 계산(반복 저장하기 위해서) - 동일한 날짜면 0반환
    private int dateDiff(String srtDate, String endDate){
        if(srtDate.equals(endDate)) return 0;

        String strFormat = "yyyyMMdd";    //strStartDate 와 strEndDate 의 format
        long diffDay = 0;

        //SimpleDateFormat 을 이용하여 startDate와 endDate의 Date 객체를 생성한다.
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        try{
            Date startDate = sdf.parse(srtDate);
            Date endDate2 = sdf.parse(endDate);

            //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
            diffDay = ( endDate2.getTime() - startDate.getTime()) / (24*60*60*1000);

        }catch(ParseException e){
            e.printStackTrace();
        }

        return (int)diffDay;
    }
}