package com.teamnova.dateset.addedfunc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.calendar.CalendarActivity;
import com.teamnova.dateset.addedfunc.find_restaurant.FindRestaurantActivity;
import com.teamnova.dateset.addedfunc.weather.WeatherActivity;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.post_list.PostListActivity;
import com.teamnova.dateset.login.mypage.MyPageActivity;
import com.teamnova.dateset.post_writing.PostWritingActivity;

public class AddedFunctionActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_find_restaurant;
    private Button btn_calendar;
    private Button btn_weather;

    private LinearLayout layout_home;
    private LinearLayout layout_post_list;
    private LinearLayout layout_post_writing;
    private LinearLayout layout_chat;
    private LinearLayout layout_added_function;

    private ImageButton btn_home;
    private ImageButton btn_post_list;
    private ImageButton btn_post_writing;
    private ImageButton btn_chat;
    private ImageButton btn_added_function;

    private UserDto userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_function);

        btn_find_restaurant = findViewById(R.id.btn_find_restaurant);
        btn_calendar = findViewById(R.id.btn_calendar);
        btn_weather = findViewById(R.id.btn_weather);

        btn_find_restaurant.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        btn_weather.setOnClickListener(this);

        // 하단 레이아웃 초기화
        layout_home = findViewById(R.id.layout_home);
        layout_post_list = findViewById(R.id.layout_post_list);
        layout_post_writing = findViewById(R.id.layout_post_writing);
        layout_chat = findViewById(R.id.layout_chat);
        //layout_added_function = findViewById(R.id.layout_added_function);

        layout_home.setOnClickListener(this);
        layout_post_list.setOnClickListener(this);
        layout_post_writing.setOnClickListener(this);
        layout_chat.setOnClickListener(this);
        //layout_added_function.setOnClickListener(this);

        //하단 버튼 초기화
        btn_home = findViewById(R.id.btn_home);
        btn_post_list = findViewById(R.id.btn_post_list);
        btn_post_writing = findViewById(R.id.btn_post_writing);
        btn_chat = findViewById(R.id.btn_chat);
        //btn_added_function = findViewById(R.id.btn_added_function);

        btn_home.setOnClickListener(this);
        btn_post_list.setOnClickListener(this);
        btn_post_writing.setOnClickListener(this);
        btn_chat.setOnClickListener(this);
        //btn_added_function.setOnClickListener(this);

        userInfo = this.bringUserInfo();
    }

    @Override
    public void onClick(View v) {
        /*Intent intent = null;
        Class cls = null;

        switch(v.getId()){
            case  R.id.btn_find_restaurant:
                cls = FindRestaurantActivity.class;
                break;
            case  R.id.btn_calendar:
                cls = CalendarActivity.class;
                break;
            case  R.id.btn_weather:
                cls = WeatherActivity.class;
                break;
            case  R.id.layout_home:
            case  R.id.btn_home:
                layout_home.setBackgroundColor(Color.GRAY);
                cls = HomeActivity.class;
                setBackGroundColor(R.id.layout_home);
                break;
            case  R.id.layout_post_list:
            case  R.id.btn_post_list:
                cls = PostListActivity.class;
                setBackGroundColor(R.id.layout_post_list);
                break;
            case  R.id.layout_post_writing:
            case  R.id.btn_post_writing:
                cls = PostWritingActivity.class;
                setBackGroundColor(R.id.layout_post_writing);
                break;
            case  R.id.layout_chat:
            case  R.id.btn_chat:
                cls = ChattingActivity.class;
                setBackGroundColor(R.id.layout_chat);
                break;
            case  R.id.layout_added_function:
            case  R.id.btn_added_function:
                cls = AddedFunctionActivity.class;
                setBackGroundColor(R.id.layout_added_function);
                break;
            default:
                break;
        }

        if((v.getId() != R.id.layout_added_function) && (v.getId() != R.id.btn_added_function)){
            intent = new Intent(getApplicationContext(), cls);
            if(userInfo != null){
                intent.putExtra("USER_INFO",userInfo);
            }

            startActivity(intent);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setBackGroundColor(R.id.layout_added_function);
    }

    public void setBackGroundColor(int id){
        View[] viewArr = new View[5];
        viewArr[0] = layout_home;
        viewArr[1] = layout_post_list;
        viewArr[2] = layout_post_writing;
        viewArr[3] = layout_chat;
        viewArr[4] = layout_added_function;

        for(View view : viewArr){
            if(view.getId() == id){
                view.setBackgroundColor(Color.GRAY);
            } else{
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     *  1. 메소드명: bringUserInfo
     *  2. 메소드 역할: 서버에서 유저정보를 가져온다.
     *  3. 출력파라미터
     *      1) userInfo: 서버에서 가져온 유저정보 객체
     * */
    private UserDto bringUserInfo(){
        UserDto userInfo = null;

        // Intent 구현
        Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");

        return userInfo;
    }
}