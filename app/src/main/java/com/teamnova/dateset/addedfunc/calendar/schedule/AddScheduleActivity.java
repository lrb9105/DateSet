package com.teamnova.dateset.addedfunc.calendar.schedule;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.calendar.AlarmReceiver;
import com.teamnova.dateset.addedfunc.calendar.CalendarActivity;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddScheduleActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    // SP 에저장된 scheduleDto key값
    public static String SCHEDULE_DTO_KEY = "SCHEDULE_DTO";

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

    // 일정추가 버튼
    private Button btn_add_schedule;

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
    private int endHour;
    private int endMinute;
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
    private String myId;

    // 유저정보
    UserDto userDto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

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
/*        textView_repeat_yn.setOnClickListener(this);
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
        alarmType = "0";

        // 메모
        editText_memo = findViewById(R.id.editText_memo);

        // 일정추가 버튼
        btn_add_schedule = findViewById(R.id.btn_add_schedule);
        btn_add_schedule.setOnClickListener(this);

        // 화면생성 시 시작/종료 날짜 및 시간 세팅
        try {
            textView_sch_start_date.setText(getCurrentDate());
            textView_sch_start_time.setText(getCurrentDay(false));
            textView_sch_end_date.setText(getCurrentDate());
            textView_sch_end_time.setText(getCurrentDay(true));
            textView_repeat_end_date.setText(getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 화면 생성 시 date 초기화
        // 시작날짜
        startYear = Calendar.getInstance().get(Calendar.YEAR);
        startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        startDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // 종료날짜
        endYear = Calendar.getInstance().get(Calendar.YEAR);
        endMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        endDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // 반복종료날짜
        repeatEndYear = Calendar.getInstance().get(Calendar.YEAR);
        repeatEndMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        repeatEndDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        try {
            // 시작시간
            startTimeWithoutAmPm = getCurrentTime(false);
            Calendar calendar = Calendar.getInstance();
            startHour = calendar.get(Calendar.HOUR_OF_DAY);
            startMinute= calendar.get(Calendar.MINUTE);
            // 종료시간
            calendar.add(Calendar.HOUR,1);
            endTimeWithoutAmPm =  getCurrentTime(true);
            endHour = calendar.get(Calendar.HOUR_OF_DAY);
            endMinute= calendar.get(Calendar.MINUTE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // db 객체
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("schedule/"+key);

        // Sp Manager 객체
        sp = new SPManager(this,"com.teamnova.dateset.schedule",MODE_PRIVATE);

        // 내id
        userDto = ((UserDto)getIntent().getSerializableExtra("USER_INFO"));
        myId = userDto.getId();
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
                DatePickerDialog dialog = new DatePickerDialog(this);
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

                        String dateAndDay = date+" ("+day+")";
                        textView_sch_start_date.setText(dateAndDay);
                        textView_sch_end_date.setText(dateAndDay);
                        //Toast.makeText(getApplicationContext(), dateAndDay, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            case R.id.textView_sch_end_date:
                dialog = new DatePickerDialog(this);

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
                // 현재시간, 분

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        endTimeWithoutAmPm = startTimeWithoutAmPm;
                        textView_sch_start_time.setText(Util.getTime(hourOfDay,minute));
                        textView_sch_end_time.setText(Util.getTime(hourOfDay,minute));

                        Toast.makeText(AddScheduleActivity.this, Util.getTime(hourOfDay,minute), Toast.LENGTH_SHORT).show();
                    }
                },startHour, startMinute, false);

                timePickerDialog.show();
                break;
            case R.id.textView_sch_end_time:

                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay < startHour){
                            hourOfDay = startHour;
                        }
                        endTimeWithoutAmPm = ((hourOfDay < 10)? "0"+hourOfDay : hourOfDay) + ":" + minute;
                        textView_sch_end_time.setText(Util.getTime(hourOfDay,minute));
                        Toast.makeText(AddScheduleActivity.this, Util.getTime(hourOfDay,minute), Toast.LENGTH_SHORT).show();
                    }
                },endHour, endMinute, false);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);

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
            case R.id.btn_add_schedule:
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

                ScheduleDto schDto = new ScheduleDto(myId,schType,title,place,startDateWithDay,srtDate,startTime,endDate2,endDateWithDay,endTime,repeatType,repeatEndDateWithDay,repeatEndDate,alarmType,memo,startTimeWithoutAmPm,endTimeWithoutAmPm,startDateWithDay);

                if(!alarmType.equals("0")){
                    Intent alarmIntent;
                    PendingIntent pendingIntent;
                    AlarmManager alarmManager;

                    alarmIntent = new Intent(this, AlarmReceiver.class);

                    /*alarmIntent.putExtra("SCHEDULE_DTO",schDto);
                    alarmIntent.putExtra("TITLE",schDto.getStartTime() + "~" + schDto.getEndTime());
                    alarmIntent.putExtra("CONTENTS",schDto.getTitle());
*/
                    Bundle bundle = new Bundle();
                    bundle.putString("TITLE", schDto.getStartTime() + " ~ " + schDto.getEndTime());
                    bundle.putString("CONTENTS", schDto.getTitle());

                    alarmIntent.putExtras(bundle);

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), UUID.randomUUID().hashCode(), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    //Log.d("debug_dto", alarmIntent.getSerializableExtra("SCHEDULE_DTO").toString());

                    // 시작일

                    Log.d("debug_srtDate",srtDate);

                    String srtDateForAlarm = srtDate;
                    // 시작시간
                    String srtTimeForAlarm = startTime;
                    srtTimeForAlarm = get24Time(srtTimeForAlarm);

                    // 시작시간 mills
                    long triggerMills = 0L;

                    // 알람타입에 따라 조작한 시작일
                    String handledDate;
                    int type = 0;

                    // 일정 시작 시간
                    if(alarmType.equals("1")){
                        // 스케줄 시작일자의 시작시간
                        triggerMills = dateToMills(srtDateForAlarm + srtTimeForAlarm);
                        Log.d("debug_alarmTime1",srtDateForAlarm + srtTimeForAlarm);
                        Log.d("debug_alarmTime3","" + triggerMills);
                    } else if(alarmType.equals("2")){ //10분전
                        // 스케줄 시작일자의 시작시간-10분
                        type = 2;
                    } else if(alarmType.equals("3")){ //1시간전
                        // 스케줄 시작일자의 시작시간-1시간
                        type = 3;
                    } else if(alarmType.equals("4")){ //1일전
                        // 스케줄 시작일자-1일의 시작시간
                        type = 4;
                    } else if(alarmType.equals("5")){ //3일전
                        // 스케줄 시작일자-3일의 시작시간
                        type = 5;
                    } else if(alarmType.equals("6")){ //1주일전
                        // 스케줄 시작일자-7일의 시작시간
                        type = 6;
                    }

                    if(!alarmType.equals("1")){
                        handledDate = handleDate(srtDateForAlarm,srtTimeForAlarm,type);
                        triggerMills =  dateToMills(handledDate);
                    }

                    Log.d("debug_alarmTime", "" + triggerMills);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMills, pendingIntent);
                    }
                }

                // fb에 저장
                String fbKey = writeSchToFb(schDto);

                // 만약 본인이 작성한 일정이라면 sp에도 저장
                /*if(schType.equals("1")){
                    schDto.setKey(fbKey);
                    int size = sp.getCurrentSize() + 1;
                    sp.insertScheduleDto(SCHEDULE_DTO_KEY, schDto, size);
                }*/

                // 시작일 - 종료일까지 각각 데이터 만들어서 저장하는 로직

                int repeatCnt = Util.dateDiff(srtDate,endDate2) + 1;

                Log.d("debug_dot_srtDate",srtDate);
                Log.d("debug_dot_endDate",endDate2);
                Log.d("debug_dot_repeatCnt","" + repeatCnt);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                for(int i = 0; i < repeatCnt; i++){
                    Log.d("debug_for",""+i);
                    // 첫번째 데이터

                    if (i != 0) {
                        String day = null;
                        try {
                            c.add(Calendar.DATE, 1);  //하루를 더해준다.
                            Log.d("debug_dot"+i, c.getTime().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        startDateWithDay += " (" + day + ")";
                    } else{
                        try {
                            c.setTime(sdf.parse(srtDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    /*CalendarDay calendarDay = CalendarDay.from(c);
                    staticDates.add(calendarDay);*/
                }


                // 나중에 db추가 이벤트로 옮기기
                builder = new AlertDialog.Builder(AddScheduleActivity.this);
                builder.setTitle("일정 저장 완료");
                builder.setMessage("일정 저장이 완료되었습니다.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(AddScheduleActivity.this, CalendarActivity.class);
                        intent.putExtra("USER_INFO",userDto);
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

    private String getCurrentDate() throws Exception {
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy년 MM월 dd일");
        Date today = new Date();
        String date = format1.format(today);
        String day = Util.getDateDay(date,"yyyy년 MM월 dd일");
        return date+" ("+day+")";
    }

    private String getCurrentDay(boolean isLaterTime) throws Exception {
        SimpleDateFormat format2 = new SimpleDateFormat( "a h:mm");
        Date today = new Date();
        String time = format2.format(today);

        if(isLaterTime){
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(Calendar.HOUR, 1);

            time = format2.format(cal.getTime());
        }

        return time;
    }

    private String getCurrentTime(boolean isLaterTime) throws Exception {
        SimpleDateFormat format2 = new SimpleDateFormat( "hh:mm");
        Date today = new Date();
        String time = format2.format(today);

        if(isLaterTime){
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(Calendar.HOUR, 1);

            time = format2.format(cal.getTime());
        }

        return time;
    }

    private String writeSchToFb(ScheduleDto schDto) {
        Log.d("debug_for2",""+schDto.toString());
        // 시작일자 기준으로 저장한다.
        DatabaseReference mRef = mDatabaseRef.child(schDto.getStartDate()).push();
        mRef.setValue(schDto);
        return mRef.getKey();
    }

    private String get24Time(String srtTimeForAlarm){
        Log.d("debug_time",srtTimeForAlarm);

        if(srtTimeForAlarm.startsWith("오후")){
            srtTimeForAlarm = srtTimeForAlarm.substring(3);

            int hour = 0;
            int min = 0;
                    String strHour = srtTimeForAlarm.substring(0,2);

            if(strHour.contains(":")){
                hour = Integer.parseInt(srtTimeForAlarm.substring(0,1));
                min = Integer.parseInt(srtTimeForAlarm.substring(2,4));
            } else{
                hour = Integer.parseInt(srtTimeForAlarm.substring(0,2));
                min = Integer.parseInt(srtTimeForAlarm.substring(3,5));
            }

            if(hour == 12){
                srtTimeForAlarm = "00:" + min;
            } else{
                hour += 12;
                srtTimeForAlarm = hour + ":" + min;
            }
        } else{
            srtTimeForAlarm = srtTimeForAlarm.substring(3);
        }

        return srtTimeForAlarm;
    }

    // 일짜를 mills 형태로 변환해준다.
    private long dateToMills(String date){
        String startDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm");
        Date date2 = null;
        try {
            date2 = sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date2.getTime();
        return millis;
    }

    //
    private String handleDate(String currentDate, String currentTime, int type){
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMddHH:mm");
        Date date = null;
        String handledDate = null;

        currentDate = currentDate + currentTime;

        try {
            date = df.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 현재 시간
        cal.setTime(date);

        // type에 따라 현재날짜를 조정한다.
        switch(type){
          case  2: //10분전
              cal.add(Calendar.MINUTE, -10);
            break;
            case  3: //1시간전
                cal.add(Calendar.HOUR_OF_DAY, -1);
                break;
            case  4: //1일전
                cal.add(Calendar.DATE, - 1);
                break;
            case  5: //3일전
                cal.add(Calendar.DATE, - 3);
                break;
            case  6: //7일전
                cal.add(Calendar.DATE, - 7);
                break;
        }
        handledDate = df.format(cal.getTime());

        return handledDate;
    }
}

