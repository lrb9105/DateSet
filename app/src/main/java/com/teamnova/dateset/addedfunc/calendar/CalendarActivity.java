package com.teamnova.dateset.addedfunc.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.DateAdapter;
import com.teamnova.dateset.addedfunc.calendar.anniversary.AddAnniversaryActivity;
import com.teamnova.dateset.addedfunc.calendar.anniversary.AnniversaryListActivity;
import com.teamnova.dateset.addedfunc.calendar.decorator.DotDecorator;
import com.teamnova.dateset.addedfunc.calendar.decorator.SaturdayDecorator;
import com.teamnova.dateset.addedfunc.calendar.decorator.SundayDecorator;
import com.teamnova.dateset.addedfunc.calendar.decorator.TodayDecorator;
import com.teamnova.dateset.addedfunc.calendar.schedule.AddScheduleActivity;
import com.teamnova.dateset.addedfunc.calendar.schedule.CompareByDate;
import com.teamnova.dateset.addedfunc.calendar.schedule.ScheduleListActivity;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.dto.DateInterface;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class CalendarActivity extends AppCompatActivity implements View.OnTouchListener {
    private String key;

    // 뒤로가기 버튼
    private ImageButton btn_back;
    private MaterialCalendarView calendar;
    private TextView textView_date;

    // 컴포넌트
    private ImageButton btn_anniversary;
    private ImageButton btn_schedule;
    private ImageButton btn_add;

    // HashSet객체의 static변수 - 일정이나 기념일 추가 시 해당 변수에 값추가해주기!!
    public HashSet<CalendarDay> dateSet;

    // db 객체
    DatabaseReference dbRef;

    //유저정보
    private UserDto userDto;

    // 날짜에 해당하는 데이터의 fb key값 리스트를 가지고 있는 map
    // key: 날짜
    // value: 날짜에 해당하는 일정과 기념일의 fb key값 리스트트
   private HashMap<String, ArrayList<String>> dateListMap;

    private RecyclerView recyclerView;

    private TextView empty_shc_anniversary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        recyclerView = findViewById(R.id.recyclerView_sch_anniversary);
        empty_shc_anniversary = findViewById(R.id.empty_shc_anniversary);

        // 키값
        key = HomeActivity.userInfo.getSharedKey();


        dateSet = new HashSet<>();

        // 날짜를 key값으로 fb key값을 저장한 리스트를 저장하는 맵
        dateListMap = new HashMap<>();
        
        // 날짜데이터를 이용해서 달력에 점을 찍어주거나 
        // 날짜를 클릭했을 때 데이터가 출력되도록
        DatabaseReference dbRefAnniversary = FirebaseDatabase.getInstance().getReference("anniversary/"+key);
        dbRefAnniversary.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataByDay : snapshot.getChildren()){
                    // fb key값을 저장할 리스트
                    ArrayList<String> dateList = new ArrayList<>();

                    // HashSet에 날짜 저장.
                    // yyMMdd
                    String date = dataByDay.getKey();

                    int year = Integer.parseInt(date.substring(0,4));
                    // month는 0부터 시작!
                    int month = Integer.parseInt(date.substring(4,6)) - 1;
                    int dayOfWeek = Integer.parseInt(date.substring(6,8));

                    CalendarDay c = CalendarDay.from(year, month, dayOfWeek);
                    dateSet.add(c);

                    // 리스트에 데이터 저장
                    for(DataSnapshot dataByOrder : dataByDay.getChildren()){
                        String fbKey = dataByOrder.getKey();
                        dateList.add(fbKey);
                    }

                    // 맵에 데이터 저장(key: 날짜, value: fb key 리스트)
                    dateListMap.put(date,dateList);
                }

                DatabaseReference dbRefSchedule = FirebaseDatabase.getInstance().getReference("schedule/"+key);
                dbRefSchedule.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataByDay : snapshot.getChildren()){
                            for(DataSnapshot dataByOrder : dataByDay.getChildren()){
                                String fbKey = dataByOrder.getKey();
                                ScheduleDto dto = dataByOrder.getValue(ScheduleDto.class);
                                String srtDate = dto.getStartDate();
                                String endDate = dto.getEndDate();

                                // 반복횟수 일정 시작일 - 일정 종료일까지
                                int repeatCnt = Util.dateDiff(srtDate, endDate) + 1;

                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat  sdf = new SimpleDateFormat("yyyyMMdd");

                                // 시작일 - 종료일만큼 HashMap에 데이터 저장
                                for(int i = 0; i < repeatCnt; i++){
                                    // 반복문에서 현재 날짜
                                    String currentDate = null;

                                    // 첫번째 데이터가 아닌경우
                                    if (i != 0) {
                                        try {
                                            calendar.add(Calendar.DATE, 1);  //하루를 더해준다.
                                            currentDate = sdf.format(calendar.getTime());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else{ // 첫번째 데이터
                                        try {
                                            currentDate = srtDate;
                                            calendar.setTime(sdf.parse(srtDate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    // 점찍을 날짜 데이터 저장
                                    CalendarDay calendarDay = CalendarDay.from(calendar);
                                    dateSet.add(calendarDay);

                                    /* 날짜에 해당하는 리스트에 fb Key값 저장
                                     *  1. HashMap에서 해당 날짜의 리스트를 가져옴
                                     *  2. 리스트가 있다면 그곳에 fb값 넣어주고 HashMap에 다시 넣어줌
                                     *  3. 리스트가 없다면 리스트 생성 후 리스트에 fb값 넣어줌 => HashMap에 리스트 넣어줌
                                     * */
                                    ArrayList<String> dateList = dateListMap.get(currentDate);

                                    if(dateList == null){
                                        dateList = new ArrayList<>();
                                        dateList.add(fbKey);
                                    } else{
                                        dateList.add(fbKey);
                                    }

                                    dateListMap.put(currentDate,dateList);
                                }
                            }
                        }

                        // 처음엔 여기서 해줌
                        Log.d("debug_dot","" + dateSet.size());
                        Log.d("debug_dot_map",dateListMap.toString());
                        calendar.addDecorator(new DotDecorator(Color.RED, dateSet));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = null;
        intent = getIntent();
        userDto = (UserDto)intent.getSerializableExtra("USER_INFO");

        dbRef = FirebaseDatabase.getInstance().getReference("schedule/"+key);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataByDay : snapshot.getChildren()){
                    ArrayList<ScheduleDto> tempList = new ArrayList<>();
                    for(DataSnapshot dataByTime : dataByDay.getChildren()){
                        ScheduleDto dto = dataByTime.getValue(ScheduleDto.class);
                        tempList.add(dto);
                    }

                    // 시작 시간순으로 정렬
                    Collections.sort(tempList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 컴포넌트 초기화
        btn_anniversary = findViewById(R.id.btn_anniversary);
        btn_schedule = findViewById(R.id.btn_schedule);
        btn_add = findViewById(R.id.btn_add);
        textView_date = findViewById(R.id.date);
        calendar = findViewById(R.id.calendarView);

        calendar.addDecorator(new TodayDecorator(this));
        calendar.addDecorator(new SundayDecorator());
        calendar.addDecorator(new SaturdayDecorator());
        
        //calendar.addDecorator(new DotDecorator(Color.RED,));

        // 기념일 버튼 클릭
        btn_anniversary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                intent =  new Intent(CalendarActivity.this, AnniversaryListActivity.class);
                intent.putExtra("USER_INFO", userDto);
                startActivity(intent);
            }
        });

        // 일정 버튼 클릭
        btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                intent =  new Intent(CalendarActivity.this, ScheduleListActivity .class);
                intent.putExtra("USER_INFO", userDto);
                startActivity(intent);
            }
        });

        // 추가 버튼 클릭
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);

                builder.setItems(R.array.addCalendarFunc, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        Intent intent = null;
                        String[] items = new String[]{"일정추가", "기념일추가"};
                        intent = getIntent();
                        if(pos == 0){ // 일정추가
                            intent =  new Intent(CalendarActivity.this, AddScheduleActivity.class);
                        } else { // 기념일 추가
                            intent =  new Intent(CalendarActivity.this, AddAnniversaryActivity.class);
                        }
                        intent.putExtra("USER_INFO", userDto);

                        startActivity(intent);
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // 캘린더 들어왔을 떄 오늘날짜 세팅
        // 오늘의 일정이나 기념일 있을 경우 db에서 가져오기
        CalendarDay today = CalendarDay.today();
        textView_date.setText(today.getYear()+"년 " + (today.getMonth()+1) + "월 " + today.getDay() + "일");

        // 날짜 변경(날짜 클릭 시) 실행되는 콜백 메소드
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ArrayList<DateInterface> dateList = new ArrayList<>();

                textView_date.setText(date.getYear()+"년 " + (date.getMonth()+1) + "월 " + date.getDay() + "일");

                // 날짜 변경시 해당하는 데이터 하위 리사이클러뷰에 출력
                String clickedDate = date.getYear() + "" + (date.getMonth() < 10 ? "0" + (date.getMonth()+1) : (date.getMonth() + 1)) + "" +  (date.getDay() < 10 ? "0" + date.getDay() : date.getDay());

                Log.d("debug_map_date",clickedDate);

                // 해당 날짜에 들어있는 기념일과 일정의 fbKey
                ArrayList<String> fbKeyList = dateListMap.get(clickedDate);

                //기념일 부터 가져 옴
                if(fbKeyList != null){
                    empty_shc_anniversary.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    Log.d("debug_recycler_fbList","" + fbKeyList.size());
                    Log.d("debug_click",fbKeyList.get(0));

                    DatabaseReference dbRefAnniversary = FirebaseDatabase.getInstance().getReference("anniversary/" + key);
                    dbRefAnniversary.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataByDate : snapshot.getChildren()){
                                for(DataSnapshot dataByOrder : dataByDate.getChildren()){
                                    if(fbKeyList != null){
                                        for(String fbKey : fbKeyList){
                                            if(fbKey.equals(dataByOrder.getKey())){
                                                AnniversaryDto aDto = dataByOrder.getValue(AnniversaryDto.class);
                                                dateList.add(aDto);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            Log.d("debug_recycler_dateList1","" + dateList.size());

                            ArrayList<ScheduleDto> schList = new ArrayList<>();

                            DatabaseReference dbRefSchedule = FirebaseDatabase.getInstance().getReference("schedule/" + key);
                            dbRefSchedule.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataByDate : snapshot.getChildren()){
                                        for(DataSnapshot dataByOrder : dataByDate.getChildren()){
                                            if(fbKeyList != null){
                                                for(String fbKey : fbKeyList){
                                                    if(fbKey.equals(dataByOrder.getKey())){
                                                        ScheduleDto aDto = dataByOrder.getValue(ScheduleDto.class);
                                                        schList.add(aDto);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // 시간별로 sorting
                                    if(schList.size() > 0){
                                        Collections.sort(schList, new CompareByDate());
                                        for(ScheduleDto s : schList) {
                                            dateList.add(s);
                                        }

                                        Log.d("debug_recycler_dateList2","" + dateList.size());

                                    }

                                    // 리사이클러뷰에 데이터 넣기
                                    if(dateList.size() > 0){
                                        Log.d("debug_recycler_dateList3","" + dateList.size());

                                        DateAdapter adapter = new DateAdapter(dateList, CalendarActivity.this);
                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CalendarActivity.this);

                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else{
                    empty_shc_anniversary.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        calendar.setDateSelected(today,false);

        // 월 변경 시 실행되는 콜백 메소드
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });

        // 버튼 터치이벤트 추가

        btn_back.setOnTouchListener(this);
        btn_anniversary.setOnTouchListener(this);
        btn_schedule.setOnTouchListener(this);
        btn_add.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("_debug_onStart", "onResume");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()){
            case R.id.btn_back:
            case R.id.btn_anniversary:
            case R.id.btn_schedule:
            case R.id.btn_add:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.LTGRAY);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
          default:
            break;
        }
        return false;
    }
}