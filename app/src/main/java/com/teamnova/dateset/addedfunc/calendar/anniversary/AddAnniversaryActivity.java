package com.teamnova.dateset.addedfunc.calendar.anniversary;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.calendar.AlarmReceiver;
import com.teamnova.dateset.addedfunc.calendar.CalendarActivity;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddAnniversaryActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    // 키값
    private String key = HomeActivity.userInfo.getSharedKey();

    // 뒤로가기 버튼
    private ImageButton btn_back;

    // 제목/위치
    private EditText editText_title;

    // 날짜
    private TextView textView_date;
    private TextView textView_repeat_yn;
    private TextView textView_repeat_end_date;

    // 반복 종료
    private LinearLayout layout_repeat;

    // 알림
    private TextView textView_alarm_yn;

    // 메모
    private EditText editText_memo;

    // 기념일추가 버튼
    private Button btn_add_anniversary;

    // 시작날짜
    private int startYear;
    private int startMonth;
    private int startDay;


    // 반복종료날짜
    private int repeatEndYear;
    private int repeatEndMonth;
    private int repeatEndDay;

    // 반복종류
    private String repeatType;

    // 알람종류
    private String alarmType;

    // db객체
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anniversary);

        // 뒤로가기 버튼
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_back.setOnTouchListener(this);

        // 제목,위치
        editText_title = findViewById(R.id.editText_title);

        // 날짜
        textView_date = findViewById(R.id.textView_date);
/*        textView_repeat_yn = findViewById(R.id.textView_repeat_yn);
        textView_repeat_end_date = findViewById(R.id.textView_repeat_end_date);*/

        textView_date.setOnClickListener(this);
/*        textView_repeat_yn.setOnClickListener(this);
        textView_repeat_end_date.setOnClickListener(this);*/

        textView_date.setOnTouchListener(this);
/*        textView_repeat_yn.setOnTouchListener(this);
        textView_repeat_end_date.setOnTouchListener(this);*/

        // 반복종료
/*
        layout_repeat = findViewById(R.id.layout_repeat);
*/

        // 알림
        textView_alarm_yn = findViewById(R.id.textView_alarm_yn);
        textView_alarm_yn.setOnClickListener(this);
        textView_alarm_yn.setOnTouchListener(this);

        // 메모
        editText_memo = findViewById(R.id.editText_memo);

        // 기념일 추가 버튼
        btn_add_anniversary = findViewById(R.id.btn_add_anniversary);
        btn_add_anniversary.setOnClickListener(this);

        // 화면생성 시 시작/종료 날짜 및 시간 세팅
        try {
            textView_date.setText(getCurrentDate());
            textView_repeat_end_date.setText(getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 화면 생성 시 date 초기화
        // 시작날짜
        startYear = Calendar.getInstance().get(Calendar.YEAR);
        startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        startDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // 반복종료날짜
        repeatEndYear = Calendar.getInstance().get(Calendar.YEAR);
        repeatEndMonth = Calendar.getInstance().get(Calendar.MONTH);
        repeatEndDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // db객체
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("anniversary/"+key);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.btn_back:
                onBackPressed();
                break;
            case R.id.textView_date:
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

                        String dateAndDay = date+"("+day+")";
                        textView_date.setText(dateAndDay);
                        //Toast.makeText(getApplicationContext(), dateAndDay, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
                break;
            /*case R.id.textView_repeat_end_date:
                dialog = new DatePickerDialog(this);
                // 시작날짜 이후의 날짜만 선택할 수 있도록
                Calendar startDate = Calendar.getInstance();
                startDate.set(startYear,startMonth,startDay);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAnniversaryActivity.this);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAnniversaryActivity.this);

                builder.setItems(R.array.anniversaryAlarm, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        // 0, 1, 2, 3, 4, 5, 6
                        String[] items = new String[]{"없음","당일[오전 09:00]","1일전[오전 09:00]","3일전[오전 09:00]","1주일전[오전 09:00]","10일전[오전 09:00]","2주일전[오전 09:00]"};
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
            case R.id.btn_add_anniversary:
                String title = null;         // 제목
                String date = null;     // 시작날짜(YYYYMMDD)
                String dateWithDay;     // 시작날짜(일 포함)
                String repeatType = null;    // 반복종류
                String repeatEndDate = null; // 반복종료날짜(YYYYMMDD)
                String repeatEndDateWithDay = null; // 반복종료날짜(일 포함)
                String alarmType = null;     // 알람종류
                String memo = null;          // 메모 null가능

                //----- 유효성 검사------
                //제목
                if(editText_title.getText() == null){
                    title = "기념일";
                }else{
                    title = editText_title.getText().toString();
                }


                // 시작날짜
                date = "" + startYear + (startMonth <10 ? "0"+startMonth : startMonth) + (startDay <10 ? "0"+startDay : startDay);
                dateWithDay = textView_date.getText().toString();

                /*Log.d("debug_dates","" + staticDates.size());
                CalendarDay c = CalendarDay.from(startYear, startMonth-1, startDay);
                staticDates.add(c);*/
                Log.d("debug_startDate","" + startYear + "." + startMonth + "." + startDay);


                //반복종류
                /*repeatType = this.repeatType;
                //반복종료날짜
                if(!repeatType.equals("0") && textView_repeat_end_date.getText() != null){
                    repeatEndDate = "" + repeatEndYear + (repeatEndMonth <10 ? "0"+(repeatEndMonth) : (repeatEndMonth)) + repeatEndDay;
                    repeatEndDateWithDay = textView_repeat_end_date.getText().toString();
                }*/
                //알람종류
                alarmType = this.alarmType;
                //메모
                if(editText_memo.getText() != null){
                    memo = editText_memo.getText().toString();
                }

                // 일정 데이터 생성
                AnniversaryDto anniversaryDto = new AnniversaryDto(title,date,dateWithDay,repeatType,repeatEndDateWithDay,repeatEndDate,alarmType,memo);

                if(!alarmType.equals("0")){
                    Intent alarmIntent;
                    PendingIntent pendingIntent;
                    AlarmManager alarmManager;

                    alarmIntent = new Intent(this, AlarmReceiver.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("TITLE", anniversaryDto.getDateWithDay());
                    bundle.putString("CONTENTS", anniversaryDto.getTitle());

                    alarmIntent.putExtras(bundle);

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), UUID.randomUUID().hashCode(), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    //Log.d("debug_dto", alarmIntent.getSerializableExtra("SCHEDULE_DTO").toString());

                    // 시작일
                    String srtDateForAlarm = date;
                    // 시작시간
                    String srtTimeForAlarm = "09:00";

                    // 시작시간 mills
                    long triggerMills = 0L;

                    // 알람타입에 따라 조작한 시작일
                    String handledDate;
                    int type = 0;

                    // 일정 시작 시간
                    if(alarmType.equals("1")){
                        // 기념일 시작일자의 시작시간
                        triggerMills = dateToMills(srtDateForAlarm + srtTimeForAlarm);
                        Log.d("debug_alarmTime1",srtDateForAlarm + srtTimeForAlarm);
                        Log.d("debug_alarmTime3","" + triggerMills);
                    } else if(alarmType.equals("2")){ //10분전
                        // 기념일 시작일자-1일의 시작시간
                        type = 2;
                    } else if(alarmType.equals("3")){ //1시간전
                        // 기념일 시작일자-3일의 시작시간
                        type = 3;
                    } else if(alarmType.equals("4")){ //1일전
                        // 기념일 시작일자-7일의 시작시간
                        type = 4;
                    } else if(alarmType.equals("5")){ //3일전
                        // 기념일 시작일자-10일의 시작시간
                        type = 5;
                    } else if(alarmType.equals("6")){ //1주일전
                        // 기념일 시작일자-14일의 시작시간
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
                writeSchToFb(anniversaryDto);

                // 나중에 db추가 이벤트로 옮기기
                builder = new AlertDialog.Builder(AddAnniversaryActivity.this);
                builder.setTitle("기념일 저장 완료");
                builder.setMessage("기념일 저장이 완료되었습니다.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(AddAnniversaryActivity.this, CalendarActivity.class);
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
            case R.id.textView_date:
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

    private void writeSchToFb(AnniversaryDto anniversaryDto) {
        // 시작일자 기준으로 저장한다.
        mDatabaseRef.child(anniversaryDto.getDate()).push().setValue(anniversaryDto);
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
            case  2: //1일전 오전 09:00
                cal.add(Calendar.DATE, -1);
                break;
            case  3: //3일전 오전 09:00
                cal.add(Calendar.DATE, -3);
                break;
            case  4: //7일전 오전 09:00
                cal.add(Calendar.DATE, - 7);
                break;
            case  5: //10일전 오전 09:00
                cal.add(Calendar.DATE, - 10);
                break;
            case  6: //14일전 오전 09:00
                cal.add(Calendar.DATE, - 14);
                break;
        }
        handledDate = df.format(cal.getTime());

        return handledDate;
    }
}