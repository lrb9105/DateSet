package com.teamnova.dateset.addedfunc.calendar.anniversary;

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
import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.calendar.schedule.CompareByDate;
import com.teamnova.dateset.addedfunc.calendar.schedule.ScheduleAdapter;
import com.teamnova.dateset.addedfunc.calendar.schedule.ScheduleListActivity;
import com.teamnova.dateset.dto.AnniversaryDto;
import com.teamnova.dateset.dto.ScheduleDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class AnniversaryListActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private String key;

    private ImageButton btn_back;
    // 리사이클러뷰
    private AnniversaryAdapter anniversaryAdapter;
    private RecyclerView recyclerView_anniversary_list;
    private ArrayList<AnniversaryDto> anniversaryList;
    private RecyclerView.LayoutManager layoutManager;

    // db참조 객체
    private DatabaseReference mDatabaseRef; // Firebase db에서 읽고 쓰기를 하기위한 db참조 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anniversary_list);

        key = userInfo.getSharedKey();

        // 컴포넌트 초기화
        btn_back = findViewById(R.id.btn_back);
        recyclerView_anniversary_list = findViewById(R.id.recyclerView_anniversary_list);

        // 이벤트 등록
        btn_back.setOnClickListener(this);
        btn_back.setOnTouchListener(this);

        // 모든 기념일 데이터 가져오기


        // 리사이클러뷰 세팅
        anniversaryList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView_anniversary_list.setLayoutManager(layoutManager);
        // 기념일 데이터

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("anniversary/"+key);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<AnniversaryDto> anniversaryList = new ArrayList<>();

                for(DataSnapshot dataByDay : snapshot.getChildren()){
                    for(DataSnapshot dataByOrder : dataByDay.getChildren()){
                        AnniversaryDto dto = dataByOrder.getValue(AnniversaryDto.class);
                        Log.d("debug_anniversary",dto.getDate() + ", " + dto.getDateWithDay() + ", " + dto.getTitle());
                        anniversaryList.add(dto);
                    }
                }

                anniversaryAdapter = new AnniversaryAdapter(anniversaryList, userInfo, AnniversaryListActivity.this);
                recyclerView_anniversary_list.setAdapter(anniversaryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        }
    }
}