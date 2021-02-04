package com.teamnova.dateset.addedfunc.calendar.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.calendar.CalendarActivity;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.teamnova.dateset.addedfunc.calendar.schedule.AddScheduleActivity.SCHEDULE_DTO_KEY;

public class ModifyScheduleActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    // 키값
    private String key = HomeActivity.userInfo.getSharedKey();

    // 뒤로가기 버튼
    private ImageButton btn_back;

    // 일정분류
    private TextView textView_common;
    private TextView textView_me;
    private TextView textView_opponent;

    // 제목/위치
    private EditText editText_title;
    private EditText editText_place;

    // 날짜
    private TextView textView_sch_start_date;
    private TextView textView_sch_start_time;
    private TextView textView_sch_end_date;
    private TextView textView_sch_end_time;
    private TextView textView_repeat_yn;
    private TextView textView_repeat_end_date;

    // 반복 종료
    private LinearLayout layout_repeat;

    // 알림
    private TextView textView_alarm_yn;

    // 메모
    private EditText editText_memo;

    // 수정 삭제 버튼
    private Button btn_modify_schedule;
    private Button btn_delete_schedule;

    // 일정분류값(0:공동, 1:본인, 2:상대방)
    private String scheduleType;

    // 시작날짜
    private int startYear;
    private int startMonth;
    private int startDay;

    // 종료날짜
    private int endYear;
    private int endMonth;
    private int endDay;

    // 반복종료날짜
    private int repeatEndYear;
    private int repeatEndMonth;
    private int repeatEndDay;

    // 시작시간
    private int startHour;
    private int startMinute;
    private String startTimeWithoutAmPm;

    //종료시간
    private String endTimeWithoutAmPm;

    // 반복종류(0으로 초기화)
    private String repeatType = "0";

    // 알람종류
    private String alarmType;

    // db객체
    private DatabaseReference mDatabaseRef;

    // SP(Shared Preferences) Manager 객체
    private SPManager sp;

    // 내 id
    private UserDto userInfo;

    // 일정정보
    private ScheduleDto scheduleDto;

    // 이전 일정타입 - 수정 및 삭제 시 비교를 위해 필요
    private String beforeSchType;

    // 이전 시작일짜
    private String beforeSrtDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mofity_schedule);
        setContentView(R.layout.activity_mofity_schedule);

        Intent intent = getIntent();
        // 일정정보
        scheduleDto = (ScheduleDto)intent.getSerializableExtra(SCHEDULE_DTO_KEY);
        Log.d("debug_Modify",scheduleDto.toString());
        // 유저정보
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");


        // 뒤로가기 버튼
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_back.setOnTouchListener(this);

        // 제목,위치
        editText_title = findViewById(R.id.editText_title);
        editText_place = findViewById(R.id.editText_place);

        // 일정분류
        textView_common = findViewById(R.id.textView_common);
        textView_me = findViewById(R.id.textView_me);
        textView_opponent = findViewById(R.id.textView_opponent);

        textView_common.setOnClickListener(this);
        textView_me.setOnClickListener(this);
        textView_opponent.setOnClickListener(this);

        // 공동버튼이 클릳되도록.
        textView_common.performClick();

        // 날짜
        textView_sch_start_date = findViewById(R.id.textView_sch_start_date);
        textView_sch_start_time = findViewById(R.id.textView_sch_start_time);
        textView_sch_end_date = findViewById(R.id.textView_sch_end_date);
        textView_sch_end_time = findViewById(R.id.textView_sch_end_time);
/*        textView_repeat_yn = findViewById(R.id.textView_repeat_yn);
        textView_repeat_end_date = findViewById(R.id.textView_repeat_end_date);*/

        textView_sch_start_date.setOnClickListener(this);
        textView_sch_start_time.setOnClickListener(this);
        textView_sch_end_date.setOnClickListener(this);
        textView_sch_end_time.setOnClickListener(this);
/*      textView_repeat_yn.setOnClickListener(this);
        textView_repeat_end_date.setOnClickListener(this);*/

        textView_sch_start_date.setOnTouchListener(this);
        textView_sch_start_time.setOnTouchListener(this);
        textView_sch_end_date.setOnTouchListener(this);
        textView_sch_end_time.setOnTouchListener(this);
/*        textView_repeat_yn.setOnTouchListener(this);
        textView_repeat_end_date.setOnTouchListener(this);*/

        // 반복종료
/*        layout_repeat = findViewById(R.id.layout_repeat);*/

        // 알림
        textView_alarm_yn = findViewById(R.id.textView_alarm_yn);
        textView_alarm_yn.setOnClickListener(this);
        textView_alarm_yn.setOnTouchListener(this);

        // 메모
        editText_memo = findViewById(R.id.editText_memo);

        // 일정추가 버튼
        btn_modify_schedule = findViewById(R.id.btn_modify_schedule);
        btn_modify_schedule.setOnClickListener(this);

        btn_delete_schedule = findViewById(R.id.btn_delete_schedule);
        btn_delete_schedule.setOnClickListener(this);

        // 데이터 초기화
        String[] items = new String[]{"없음","일정 시작 시간","10분전","1시간전","1일전","3일전","1주일전"};
        int alarmType = Integer.parseInt(scheduleDto.getAlarmType());
        editText_title.setText(scheduleDto.getTitle());
        editText_place.setText(scheduleDto.getPlace());
        textView_sch_start_date.setText(scheduleDto.getFirstDate());
        textView_sch_start_time.setText(scheduleDto.getStartTime());
        textView_sch_end_date.setText(scheduleDto.getEndDateWithDay());
        textView_sch_end_time.setText(scheduleDto.getEndTime());
        textView_alarm_yn.setText(items[alarmType]);
        this.alarmType = ""+alarmType;
        editText_memo.setText(scheduleDto.getMemo());

        // 화면생성 시 시작/종료 날짜 및 시간 세팅
        /*try {
            textView_sch_start_date.setText(getCurrentDate());
            textView_sch_start_time.setText(getCurrentDay(false));
            textView_sch_end_date.setText(getCurrentDate());
            textView_sch_end_time.setText(getCurrentDay(true));
            textView_repeat_end_date.setText(getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // 화면 생성 시 date 초기화
        // 시작날짜
        String startDate = scheduleDto.getFirstDate(); //yyyy년 mm월 dd일
        startYear = Integer.parseInt(startDate.substring(0,4));
        startMonth = Integer.parseInt(startDate.substring(6,8));
        String srtDayStr = startDate.substring(10,12);
        if(srtDayStr.contains(":")){
            startDay = Integer.parseInt(startDate.substring(10,11));
        } else{
            startDay = Integer.parseInt(startDate.substring(10,12));
        }



        // 종료날짜
        String endDate = scheduleDto.getEndDate();
        endYear = Integer.parseInt(endDate.substring(0,4));
        endMonth = Integer.parseInt(endDate.substring(4,6));
        endDay = Integer.parseInt(endDate.substring(6,8));

        // 반복종료날짜
/*        String repeatEndDate = scheduleDto.getRepeatEndDate();
        repeatEndYear = Integer.parseInt(repeatEndDate.substring(0,4));
        repeatEndMonth = Integer.parseInt(repeatEndDate.substring(4,6));
        repeatEndDay = Integer.parseInt(repeatEndDate.substring(6,8));*/

        try {
            // 시작시간
            startTimeWithoutAmPm = scheduleDto.getStartTimeWithoutAmPm();
            // 종료시간
            endTimeWithoutAmPm = scheduleDto.getEndTimeWithoutAmPm();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 일정 타입 세팅
        beforeSchType = scheduleDto.getSchType();

        if(beforeSchType.equals("0")){ //공동
            textView_common.performClick();
        } else if(beforeSchType.equals("1")){ //본인
            textView_me.performClick();
        } else if(beforeSchType.equals("2")){ //상대방
            textView_opponent.performClick();
        }


        // db 객체
        beforeSrtDate = scheduleDto.getStartDate();
        String fbKey = scheduleDto.getKey();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("schedule/"+key);

        // Sp Manager 객체
        sp = new SPManager(this,"com.teamnova.dateset.schedule",MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
          case  R.id.btn_back:
              onBackPressed();
            break;
            case  R.id.textView_common:
                v.setBackgroundColor(getResources().getColor(R.color.light_blue));
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);
                scheduleType = "0";
                break;
            case  R.id.textView_me:
                v.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                textView_common.setBackgroundColor(Color.TRANSPARENT);
                textView_opponent.setBackgroundColor(Color.TRANSPARENT);
                scheduleType = "1";
                break;
            case  R.id.textView_opponent:
                v.setBackgroundColor(getResources().getColor(R.color.light_brown));
                textView_me.setBackgroundColor(Color.TRANSPARENT);
                textView_common.setBackgroundColor(Color.TRANSPARENT);
                scheduleType = "2";
                break;
            case R.id.textView_sch_start_date:
                DatePickerDialog dialog = new DatePickerDialog(this,null,startYear,(startMonth-1),startDay);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + dayOfMonth +"일";
                        String day = "";
                        month = month + 1;

                        try {
                            day = Util.getDateDay(date,"yyyy년 mm월 dd일");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        startYear = year;
                        startMonth = month;
                        startDay = dayOfMonth;

                        endYear = year;
                        endMonth = month;
                        endDay = dayOfMonth;

                        String dateAndDay = date+"("+day+")";
                        textView_sch_start_date.setText(dateAndDay);
                        textView_sch_end_date.setText(dateAndDay);
                        //Toast.makeText(getApplicationContext(), dateAndDay, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            case R.id.textView_sch_end_date:
                dialog = new DatePickerDialog(this,null,endYear,(endMonth-1),endDay);

                // 시작날짜 이후의 날짜만 선택할 수 있도록
                Calendar startDate = Calendar.getInstance();
                startDate.set(startYear,startMonth-1,startDay);

                dialog.getDatePicker().setMinDate(startDate.getTime().getTime());

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + dayOfMonth +"일";
                        String day = "";

                        month = month + 1;

                        try {
                            day = Util.getDateDay(date,"yyyy년 mm월 dd일");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        endYear = year;
                        endMonth = month;
                        endDay = dayOfMonth;

                        String dateAndDay = date+" ("+day+")";
                        textView_sch_end_date.setText(dateAndDay);
                    }
                });

                dialog.show();
                break;
            case R.id.textView_sch_start_time:
                // 시작시간, 분
                int hour = Integer.parseInt(startTimeWithoutAmPm.substring(0,2));
                int minute = Integer.parseInt(startTimeWithoutAmPm.substring(3,5));
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        endTimeWithoutAmPm = startTimeWithoutAmPm;
                        textView_sch_start_time.setText(Util.getTime(hourOfDay,minute));
                        textView_sch_end_time.setText(Util.getTime(hourOfDay,minute));
                        Toast.makeText(ModifyScheduleActivity.this, Util.getTime(hourOfDay,minute), Toast.LENGTH_SHORT).show();
                    }
                },hour, minute, false);

                timePickerDialog.show();
                break;
            case R.id.textView_sch_end_time:
                // 종료시간, 분
                hour = Integer.parseInt(endTimeWithoutAmPm.substring(0,2));
                minute = Integer.parseInt(endTimeWithoutAmPm.substring(3,5));

                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < startHour){
                            hourOfDay = startHour;
                        }
                        endTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        textView_sch_end_time.setText(Util.getTime(hourOfDay,minute));
                        Toast.makeText(ModifyScheduleActivity.this, Util.getTime(hourOfDay,minute), Toast.LENGTH_SHORT).show();
                    }
                },hour, minute, false);

                timePickerDialog.show();
                break;
            /*case R.id.textView_repeat_end_date:
                dialog = new DatePickerDialog(this);
                // 시작날짜 이후의 날짜만 선택할 수 있도록
                Calendar endDate = Calendar.getInstance();
                endDate.set(endYear,endMonth,endDay);

                dialog.getDatePicker().setMinDate(endDate.getTime().getTime());
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "년 " + (month <10 ? "0"+(month+1) : (month+1)) + "월 " + dayOfMonth +"일";
                        String day = "";

                        month = month + 1;

                        try {
                            day = Util.getDateDay(date,"yyyy년 mm월 dd일");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        repeatEndYear = year;
                        repeatEndMonth = month;
                        repeatEndDay = dayOfMonth;

                        String dateAndDay = date+" ("+day+")";
                        textView_repeat_end_date.setText(dateAndDay);
                    }
                });

                dialog.show();
                break;
            case R.id.textView_repeat_yn:
                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);

                builder.setItems(R.array.schRepeat, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        // 0, 1, 2, 3, 4, 5, 6
                        String[] items = new String[]{"안함","매일", "매주","매달","매년","평일","주말"};
                        switch(pos){
                          case  0:
                            case  1:
                            case  2:
                            case  3:
                            case  4:
                            case  5:
                            case  6:
                                if(pos != 0){
                                    layout_repeat.setVisibility(View.VISIBLE);
                                } else{
                                    layout_repeat.setVisibility(View.GONE);
                                }
                                textView_repeat_yn.setText(items[pos]);
                                repeatType = ""+pos;
                                break;

                            default:
                            break;
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;*/
            case R.id.textView_alarm_yn:
                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyScheduleActivity.this);

                builder.setItems(R.array.schAlarm, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        // 0, 1, 2, 3, 4, 5, 6
                        String[] items = new String[]{"없음","일정 시작 시간","10분전","1시간전","1일전","3일전","1주일전"};
                        switch(pos){
                            case  0:
                            case  1:
                            case  2:
                            case  3:
                            case  4:
                            case  5:
                            case  6:
                                textView_alarm_yn.setText(items[pos]);
                                alarmType = ""+pos;
                                break;

                            default:
                                break;
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.btn_modify_schedule:
                String schType = null;       // 스케줄 종류 0:공동, 1:본인, 2:상대방
                String title = null;         // 제목
                String place = null;         // 위치 null가능
                String srtDate = null;     // 시작날짜(YYYYMMDD)
                String startDateWithDay;     // 시작날짜(일 포함)
                String startTime = null;     // 시작시간
                String endDate2 = null;       // 종료날짜(YYYYMMDD)
                String endDateWithDay;     // 시작날짜(일 포함)
                String endTime = null;       // 종료시간
                String repeatType = null;    // 반복종류
                String repeatEndDate = null; // 반복종료날짜(YYYYMMDD)
                String repeatEndDateWithDay = null; // 반복종료날짜(일 포함)
                String alarmType = null;     // 알람종류
                String memo = null;          // 메모 null가능

                schType = scheduleType;

                Log.d("debug_",schType);

                //----- 유효성 검사------
                //제목
                if(editText_title.getText() == null){
                    title = "일정";
                }else{
                    title = editText_title.getText().toString();
                }

                //위치
                if(editText_place.getText() != null){
                    place = editText_place.getText().toString();
                }

                // 시작날짜
                srtDate = "" + startYear + (startMonth <10 ? "0"+startMonth : startMonth) + (startDay < 10?"0"+startDay:startDay);
                startDateWithDay = textView_sch_start_date.getText().toString();
                // 시작시간
                startTime = textView_sch_start_time.getText().toString();
                // 종료날짜
                endDate2 = "" + endYear + (endMonth <10 ? "0"+endMonth : endMonth) + (endDay < 10?"0"+endDay:endDay);
                endDateWithDay = textView_sch_end_date.getText().toString();
                // 종료시간
                endTime = textView_sch_end_time.getText().toString();
                //반복종류
                repeatType = this.repeatType;
                //반복종료날짜
                if(!repeatType.equals("0") && textView_repeat_end_date.getText() != null){
                    repeatEndDate = "" + repeatEndYear + (repeatEndMonth <10 ? "0"+repeatEndMonth : repeatEndMonth) + repeatEndDay;
                    repeatEndDateWithDay = textView_repeat_end_date.getText().toString();
                }
                //알람종류
                alarmType = this.alarmType;
                //메모
                if(editText_memo.getText() != null){
                    memo = editText_memo.getText().toString();
                }
                ScheduleDto schDto = new ScheduleDto(userInfo.getId(),schType,title,place,startDateWithDay,srtDate,startTime,endDate2,endDateWithDay,endTime,repeatType,repeatEndDateWithDay,repeatEndDate,alarmType,memo,startTimeWithoutAmPm,endTimeWithoutAmPm,startDateWithDay);
                schDto.setKey(scheduleDto.getKey());
                schDto.setSpKey(scheduleDto.getSpKey());

                // ========== 수정하기 ==========
                modifySchToFb(schDto);

                // 기존 시작일자와 수정된 시작일자가 다르면 새로운 노드가 생성되므로 기존 노드는 삭제한다.
                if(!beforeSrtDate.equals(schDto.getStartDate())){
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("schedule/"+key+"/"+beforeSrtDate);
                    mDatabaseRef.child(schDto.getKey()).removeValue();
                }

                /*//본인
                if(beforeSchType.equals("1")){
                    if(schType.equals("1")){ // 본인 -> 본인
                        String key = modifySchToFb(schDto);
                        schDto.setKey(key);
                        sp.modifyScheduleDto(schDto.getSpKey(), schDto, schDto.getId());
                    }else{// 본인 -> 공동/상대방
                        modifySchToFb(schDto);
                        sp.removeDto(schDto.getSpKey());
                    }
                } else{ //공동, 상대방
                    if(schType.equals("1")){ // 공동/상대방 -> 본인
                        String key = modifySchToFb(schDto);
                        schDto.setKey(key);
                        int size = sp.getCurrentSize() + 1;
                        sp.insertScheduleDto(SCHEDULE_DTO_KEY, schDto, size);
                    }else{// 공동/상대방 -> 공동/상대방
                        modifySchToFb(schDto);
                    }
                modifySchToFb(schDto);
                }*/

                // 기존 시작일자와 수정된 시작일자가 다르면 새로운 노드가 생성되므로 기존 노드는 삭제한다.
                /*if(!beforeSrtDate.equals(schDto.getStartDate())){
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("schedule/"+key+"/"+beforeSrtDate);
                    mDatabaseRef.child(schDto.getKey()).removeValue();
                }*/


                // 일정 데이터 생성
                /*int repeatCnt = dateDiff(srtDate,endDate2) + 1;

                for(int i = 0; i < repeatCnt; i++){
                    Log.d("debug_for",""+i);
                    ScheduleDto schDto = null;
                    // 첫번째 데이터
                    if (i != 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");

                        Calendar c = Calendar.getInstance();
                        String day = null;

                        try {
                            c.setTime(sdf.parse(srtDate));
                            c.add(Calendar.DATE, 1);  //하루를 더해준다.

                            srtDate = sdf.format(c.getTime());  // dt는 하루를 더한 날짜
                            startDateWithDay = sdf2.format(c.getTime());  // dt는 하루를 더한 날짜
                            day = Util.getDateDay(startDateWithDay, "yyyy년 MM월 dd일");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        startDateWithDay += " (" + day + ")";

                    }
                    schDto = new ScheduleDto(userInfo.getId(),schType,title,place,startDateWithDay,srtDate,startTime,endDate2,endDateWithDay,endTime,repeatType,repeatEndDateWithDay,repeatEndDate,alarmType,memo,startTimeWithoutAmPm,endTimeWithoutAmPm,startDateWithDay);

                    // fb에 저장
                    modifySchToFb(schDto);

                    // 만약 본인이 작성한 일정이라면 sp에도 저장
                    if(schType.equals("1")){
                        int size = sp.getCurrentSize() + 1;
                        sp.insertScheduleDto(SCHEDULE_DTO_KEY, schDto, size);
                    }
                }*/


                // 나중에 db추가 이벤트로 옮기기
                builder = new AlertDialog.Builder(ModifyScheduleActivity.this);
                builder.setTitle("수정 완료");
                builder.setMessage("수정이 완료되었습니다.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(ModifyScheduleActivity.this, ScheduleListActivity.class);
                        intent.putExtra("USER_INFO",userInfo);

                        startActivity(intent);
                        finish();
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();

                break;
            case R.id.btn_delete_schedule: //삭제하기
                // ========== 삭제하기 ==========
                builder = new AlertDialog.Builder(ModifyScheduleActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("일정을 삭제 하시겠습니까?");
                builder.setNegativeButton("취소",null);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mDatabaseRef.child(beforeSrtDate).child(scheduleDto.getKey()).removeValue();

                        Intent intent = new Intent(ModifyScheduleActivity.this, ScheduleListActivity.class);
                        intent.putExtra("USER_INFO",userInfo);

                        startActivity(intent);
                        finish();
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();
                break;
          default:
            break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()){
            case R.id.btn_back:
            case R.id.textView_sch_start_date:
            case R.id.textView_sch_start_time:
            case R.id.textView_sch_end_date:
            case R.id.textView_sch_end_time:
/*            case R.id.textView_repeat_yn:
            case R.id.textView_repeat_end_date:*/
            case R.id.textView_alarm_yn:
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

    private String modifySchToFb(ScheduleDto schDto) {
        // 기존데이터를 삭제하고
        mDatabaseRef.child(beforeSrtDate).child(scheduleDto.getKey()).removeValue();
        // 새로운 데이터를 삽입한다.
        DatabaseReference mRef = mDatabaseRef.child(schDto.getStartDate()).push();
        mRef.setValue(schDto);
        return mRef.getKey();
    }
}

