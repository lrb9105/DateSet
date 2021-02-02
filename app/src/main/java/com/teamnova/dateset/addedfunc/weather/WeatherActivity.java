package com.teamnova.dateset.addedfunc.weather;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.ChattingAdapter;
import com.teamnova.dateset.adapter.WeatherAdapter;
import com.teamnova.dateset.addedfunc.find_restaurant.FindRestaurantActivity;
import com.teamnova.dateset.dto.CityInfoDto;
import com.teamnova.dateset.dto.SearchInfoDto;
import com.teamnova.dateset.dto.WeatherDto;
import com.teamnova.dateset.util.NavaeSearchApi;
import com.teamnova.dateset.util.ProgressLoadingDialog;
import com.teamnova.dateset.util.WeatherApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.teamnova.dateset.home.HomeActivity.userInfo;

public class WeatherActivity extends AppCompatActivity {
    // 뒤로가기 버튼
    private ImageButton btn_back;

    // 검색
    private ImageButton btn_search;
    private EditText editText_search;

    // 오늘 날짜
    private TextView textView_date;

    // 현재날씨
    private TextView textView_region_name;
    private TextView textView_currentTemp_feels_like_temp;
    private TextView textView_feels_like_temp;
    private ImageView imageView_currnet;

    // 시간별 날씨
    private RecyclerView recyclerView_weekly_weather;
    private WeatherAdapter weatherAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // 주간날씨
    private TextView textView_day1;
    private TextView textView_day1_temp;
    private TextView textView_day1_percentOfRain;
    private ImageView imageView_day1;

    private TextView textView_day2;
    private TextView textView_day2_temp;
    private TextView textView_day2_percentOfRain;
    private ImageView imageView_day2;
    
    private TextView textView_day3;
    private TextView textView_day3_temp;
    private TextView textView_day3_percentOfRain;
    private ImageView imageView_day3;
    
    private TextView textView_day4;
    private TextView textView_day4_temp;
    private TextView textView_day4_percentOfRain;
    private ImageView imageView_day4;

    private TextView textView_day5;
    private TextView textView_day5_temp;
    private TextView textView_day5_percentOfRain;
    private ImageView imageView_day5;

    private TextView textView_day6;
    private TextView textView_day6_temp;
    private TextView textView_day6_percentOfRain;
    private ImageView imageView_day6;

    private TextView textView_day7;
    private TextView textView_day7_temp;
    private TextView textView_day7_percentOfRain;
    private ImageView imageView_day7;

    private TextView textView_day8;
    private TextView textView_day8_temp;
    private TextView textView_day8_percentOfRain;
    private ImageView imageView_day8;

    // 핸들러
    private Handler handler;

    // 도시정보 리스트(화면 생성 시 생성)
    private ArrayList<CityInfoDto> cityInfoList;

    // 받아온 날씨 데이터 json
    private JsonObject jsonWeather;

    // 오늘 요일
    private String dayOfWeek;

    // 시간별 데이터
    private ArrayList<WeatherDto> hourlyWeatherList;
    // 일별 데이터
    private ArrayList<WeatherDto> dailyWeatherList;

    // 로딩바
    private ProgressLoadingDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressLoadingDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        initialize();

        editText_search.setText("서울");
        btn_search.performClick();
        editText_search.setText("");
    }

    public void initialize(){
        // 도시 정보 리스트
        this.cityInfoList = getCityList();

        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 0){
                    // 데이터를 받아서 해야할 일!!
                    // jsonObj를 받아서 여기서 처리해야겠지?? ㅇㅇ
                    JsonObject jsonObject = (JsonObject)msg.obj;

                    // 현재날씨
                    JsonObject jsonCurrentWeather = jsonObject.getAsJsonObject("current");

                    Long dateTime = jsonCurrentWeather.get("dt").getAsLong();
                    String dateWithDay = getDate(jsonCurrentWeather.get("dt").getAsLong());
                    String temp = "" + (jsonCurrentWeather.get("temp").getAsDouble() - 273);
                    String feelsLikeTemp = "" + (jsonCurrentWeather.get("feels_like").getAsDouble() - 273);
                    String humidity = "" + jsonCurrentWeather.get("humidity").getAsDouble();
                    String windSpeed = "" + jsonCurrentWeather.get("wind_speed").getAsDouble();
                    String weatherMain = (jsonCurrentWeather.getAsJsonArray("weather")).get(0).getAsJsonObject().get("main").getAsString();
                    String pop = null;
                    String minTemp = null;
                    String maxTemp = null;
                    String feelsLikeTempDay = null;
                    String weatherDescription = (jsonCurrentWeather.getAsJsonArray("weather")).get(0).getAsJsonObject().get("description").getAsString();

                    // 오늘 날짜
                    textView_date.setText(getDate(dateTime));

                    // 현재 날씨
                    WeatherDto currentWeather = new WeatherDto(dateTime,dateWithDay,temp,feelsLikeTemp,humidity,windSpeed,weatherMain,pop,minTemp,maxTemp,feelsLikeTempDay,weatherDescription);
                    Log.d("debug_json_current", currentWeather.toString());

                    // 초기값이 아니라면
                    if(!editText_search.getText().toString().equals("")){
                        textView_region_name.setText(editText_search.getText().toString());
                    } else{ // 초기값이면 서울 고정!
                        textView_region_name.setText("서울");
                    }

                    textView_currentTemp_feels_like_temp.setText(Math.round(Double.valueOf(currentWeather.getTemp())) + "°C");
                    textView_feels_like_temp.setText("체감온도: " + Math.round(Double.valueOf(currentWeather.getFeelsLikeTemp())) + "°C");

                    String time = WeatherActivity.getHour(currentWeather.getDateTime());
                    String weather = currentWeather.getWeatherMain();

                    Log.d("debug_weather_daily",time + ": " + weather);


                    boolean b = time.equals("오전 07시") || time.equals("오전 08시") || time.equals("오전 09시") || time.equals("오전 10시")
                            || time.equals("오전 11시") || time.equals("오후 12시") || time.equals("오후 01시") || time.equals("오후 02시")
                            || time.equals("오후 03시") || time.equals("오후 04시") || time.equals("오후 05시") || time.equals("오후 06시") || time.equals("오후 07시");

                    switch(weather){
                        case "Thunderstorm":
                            imageView_currnet.setImageDrawable(getDrawable(R.drawable.thunderstorm));
                            break;
                        case "Drizzle":
                        case "Rain":
                            imageView_currnet.setImageDrawable(getDrawable(R.drawable.rain));
                            break;
                        case "Snow":
                            imageView_currnet.setImageDrawable(getDrawable(R.drawable.snow));
                            break;
                        case "Mist":
                        case "Smoke":
                        case "Haze":
                        case "Fog":
                        case "Sand":
                        case "Dust":
                        case "Ash":
                        case "Squall":
                        case "Tornado":
                            imageView_currnet.setImageDrawable(getDrawable(R.drawable.mist));
                            break;
                        case "Clear":
                            if(b){
                                imageView_currnet.setImageDrawable(getDrawable(R.drawable.clear_sky_day));
                            } else{
                                imageView_currnet.setImageDrawable(getDrawable(R.drawable.clear_sky_night));
                            }
                            break;
                        case "Clouds":
                            if(weatherDescription.equals("clouds")){
                                if(b){
                                    imageView_currnet.setImageDrawable(getDrawable(R.drawable.few_clouds_day));
                                } else{
                                    imageView_currnet.setImageDrawable(getDrawable(R.drawable.few_clouds_night));
                                }
                            } else if(weatherDescription.equals("scattered clouds") || weatherDescription.equals("broken clouds") || weatherDescription.equals("overcast clouds")){
                                imageView_currnet.setImageDrawable(getDrawable(R.drawable.broken_clouds));
                                break;
                            }
                            break;
                    }

                    // 시간별 날씨
                    hourlyWeatherList = new ArrayList<>();
                    JsonArray jsonHourlyWeather = jsonObject.getAsJsonArray("hourly");
                    for(int i = 0; i < jsonHourlyWeather.size(); i++){
                        // 3시간마다 반복, 하루 반나절만 보여줌
                        if((i+1) < 37 && (i+1)%3 == 0){
                            JsonObject jsonHourly = jsonHourlyWeather.get(i).getAsJsonObject();
                            dateTime = jsonHourly.get("dt").getAsLong();
                            dateWithDay = getDate(jsonHourly.get("dt").getAsLong());
                            temp = "" + (jsonHourly.get("temp").getAsDouble() - 273);
                            feelsLikeTemp = "" + (jsonHourly.get("feels_like").getAsDouble() - 273);
                            humidity = "" + jsonHourly.get("humidity").getAsDouble();
                            windSpeed = "" + jsonHourly.get("wind_speed").getAsDouble();
                            weatherMain = (jsonHourly.getAsJsonArray("weather")).get(0).getAsJsonObject().get("main").getAsString();
                            weatherDescription = (jsonHourly.getAsJsonArray("weather")).get(0).getAsJsonObject().get("description").getAsString();
                            pop = "" + jsonHourly.get("pop").getAsDouble();
                            minTemp = null;
                            maxTemp = null;
                            feelsLikeTempDay = null;
                            WeatherDto hourlyWeather = new WeatherDto(dateTime,dateWithDay,temp,feelsLikeTemp,humidity,windSpeed,weatherMain,pop,minTemp,maxTemp,feelsLikeTempDay,weatherDescription);

                            Log.d("debug_weather_hourly",WeatherActivity.getHour((dateTime)) + ": " + weatherMain);

                            hourlyWeatherList.add(hourlyWeather);
                        }
                    }

                    recyclerView_weekly_weather.setLayoutManager(layoutManager);
                    weatherAdapter = new WeatherAdapter(hourlyWeatherList, WeatherActivity.this);
                    recyclerView_weekly_weather.setAdapter(weatherAdapter);

                    Log.d("debug_json_hourly", jsonHourlyWeather.toString());

                    // 일별 날씨
                    dailyWeatherList = new ArrayList<>();
                    JsonArray jsonDailyWeather = jsonObject.getAsJsonArray("daily");
                    for(int i = 0; i < jsonDailyWeather.size(); i++){
                        JsonObject jsonDaily = jsonDailyWeather.get(i).getAsJsonObject();
                        
                        dateTime = jsonDaily.get("dt").getAsLong();
                        dateWithDay = getDate(jsonDaily.get("dt").getAsLong());
                        temp = "" + (jsonDaily.get("temp").getAsJsonObject().get("day").getAsDouble() - 273);
                        feelsLikeTemp = "" + (jsonDaily.get("feels_like").getAsJsonObject().get("day").getAsDouble() - 273);
                        humidity = "" + jsonDaily.get("humidity").getAsDouble();
                        windSpeed = "" + jsonDaily.get("wind_speed").getAsDouble();
                        weatherMain = (jsonDaily.getAsJsonArray("weather")).get(0).getAsJsonObject().get("main").getAsString();
                        weatherDescription = (jsonDaily.getAsJsonArray("weather")).get(0).getAsJsonObject().get("description").getAsString();
                        pop = "" + jsonDaily.get("pop").getAsDouble();
                        minTemp = "" + (jsonDaily.get("temp").getAsJsonObject().get("min").getAsDouble() - 273);
                        maxTemp = "" + (jsonDaily.get("temp").getAsJsonObject().get("max").getAsDouble() - 273);
                        WeatherDto dailyWeather = new WeatherDto(dateTime,dateWithDay,temp,feelsLikeTemp,humidity,windSpeed,weatherMain,pop,minTemp,maxTemp,null,weatherDescription);

                        Log.d("debug_weather_daily",getDay(dateTime) + ": " + weatherMain);

                        dailyWeatherList.add(dailyWeather);
                    }

                    textView_day1.setText(getDay(dailyWeatherList.get(0).getDateTime()));
                    textView_day1_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(0).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(0).getMaxTemp()))+"°C");
                    textView_day1_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(0).getPop()))+"%");

                    textView_day2.setText(getDay(dailyWeatherList.get(1).getDateTime()));
                    textView_day2_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(1).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(1).getMaxTemp()))+"°C");
                    textView_day2_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(1).getPop()))+"%");

                    textView_day3.setText(getDay(dailyWeatherList.get(2).getDateTime()));
                    textView_day3_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(2).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(2).getMaxTemp()))+"°C");
                    textView_day3_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(2).getPop()))+"%");

                    textView_day4.setText(getDay(dailyWeatherList.get(3).getDateTime()));
                    textView_day4_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(3).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(3).getMaxTemp()))+"°C");
                    textView_day4_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(3).getPop()))+"%");

                    textView_day5.setText(getDay(dailyWeatherList.get(4).getDateTime()));
                    textView_day5_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(4).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(4).getMaxTemp()))+"°C");
                    textView_day5_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(4).getPop()))+"%");

                    textView_day6.setText(getDay(dailyWeatherList.get(5).getDateTime()));
                    textView_day6_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(5).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(5).getMaxTemp()))+"°C");
                    textView_day6_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(5).getPop()))+"%");

                    textView_day7.setText(getDay(dailyWeatherList.get(6).getDateTime()));
                    textView_day7_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(6).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(6).getMaxTemp()))+"°C");
                    textView_day7_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(6).getPop()))+"%");

                    textView_day8.setText(getDay(dailyWeatherList.get(7).getDateTime()));
                    textView_day8_temp.setText(Math.round(Double.parseDouble(dailyWeatherList.get(7).getMinTemp()))+"°C / "+Math.round(Double.parseDouble(dailyWeatherList.get(7).getMaxTemp()))+"°C");
                    textView_day8_percentOfRain.setText(Math.round(Double.parseDouble(dailyWeatherList.get(7).getPop()))+"%");

                    ImageView[] imgList = new ImageView[8];
                    imgList[0] = imageView_day1;
                    imgList[1] = imageView_day2;
                    imgList[2] = imageView_day3;
                    imgList[3] = imageView_day4;
                    imgList[4] = imageView_day5;
                    imgList[5] = imageView_day6;
                    imgList[6] = imageView_day7;
                    imgList[7] = imageView_day8;
                    
                    for(int i = 0; i < dailyWeatherList.size(); i++){
                        weather = dailyWeatherList.get(i).getWeatherMain();

                        switch(weather){
                            case "Thunderstorm":
                                imgList[i].setImageDrawable(getDrawable(R.drawable.thunderstorm));
                                break;
                            case "Drizzle":
                            case "Rain":
                                imgList[i].setImageDrawable(getDrawable(R.drawable.rain));
                                break;
                            case "Snow":
                                imgList[i].setImageDrawable(getDrawable(R.drawable.snow));
                                break;
                            case "Mist":
                            case "Smoke":
                            case "Haze":
                            case "Fog":
                            case "Sand":
                            case "Dust":
                            case "Ash":
                            case "Squall":
                            case "Tornado":
                                imgList[i].setImageDrawable(getDrawable(R.drawable.mist));
                                break;
                            case "Clear":
                                imgList[i].setImageDrawable(getDrawable(R.drawable.clear_sky_day));
                                break;
                            case "Clouds":
                                if(weatherDescription.equals("clouds")){
                                    imgList[i].setImageDrawable(getDrawable(R.drawable.few_clouds_day));
                                } else if(weatherDescription.equals("scattered clouds") || weatherDescription.equals("broken clouds") || weatherDescription.equals("overcast clouds")){
                                    imgList[i].setImageDrawable(getDrawable(R.drawable.broken_clouds));
                                    break;
                                }
                                break;
                        }
                    }
                    customProgressDialog.dismiss();

                    Log.d("debug_json_daily", jsonDailyWeather.toString());
                }
            }
        };

        // 뒤로가기 버튼
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundColor(Color.LTGRAY);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        // 검색 버튼
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editText_search.getText().toString();
                double[] latAndLog = getLatAndLog(searchText);
                if(latAndLog == null){
                    Toast.makeText(WeatherActivity.this, "검색결과가 없습니다. 지역명을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("debug_2","2222");
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);
                new SearchThread(handler,latAndLog).start();
            }
        });
        editText_search = findViewById(R.id.editText_search);
        editText_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        if(keyEvent.getAction() == KeyEvent.ACTION_UP){
                            // 엔터키 입력 시 검색
                            String searchText = editText_search.getText().toString();
                            double[] latAndLog = getLatAndLog(searchText);

                            if(latAndLog == null){
                                Toast.makeText(WeatherActivity.this, "검색결과가 없습니다. 지역명을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            Log.d("debug_1","111");
                            customProgressDialog.show();
                            customProgressDialog.setCancelable(false);
                            new SearchThread(handler,latAndLog).start();

                            return true;
                        }
                    default:
                        return false;
                }
            }
        });

        // 오늘 날짜
        textView_date = findViewById(R.id.textView_date);

        // 현재날씨
        textView_region_name = findViewById(R.id.textView_region_name);
        textView_currentTemp_feels_like_temp = findViewById(R.id.textView_currentTemp_feels_like_temp);
        textView_feels_like_temp = findViewById(R.id.textView_feels_like_temp);
        imageView_currnet = findViewById(R.id.imageView_currnet);

        // 시간별 날씨 리사이클러뷰
        recyclerView_weekly_weather = findViewById(R.id.recyclerView_weekly_weather);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);

        // 일별 날씨
        textView_day1 = findViewById(R.id.textView_day1);
        textView_day1_temp = findViewById(R.id.textView_day1_temp);
        textView_day1_percentOfRain = findViewById(R.id.textView_day1_percentOfRain);
        imageView_day1 = findViewById(R.id.imageView_day1);
        
        textView_day2 = findViewById(R.id.textView_day2);
        textView_day2_temp = findViewById(R.id.textView_day2_temp);
        textView_day2_percentOfRain = findViewById(R.id.textView_day2_percentOfRain);
        imageView_day2 = findViewById(R.id.imageView_day2);

        textView_day3 = findViewById(R.id.textView_day3);
        textView_day3_temp = findViewById(R.id.textView_day3_temp);
        textView_day3_percentOfRain = findViewById(R.id.textView_day3_percentOfRain);
        imageView_day3 = findViewById(R.id.imageView_day3);

        textView_day4 = findViewById(R.id.textView_day4);
        textView_day4_temp = findViewById(R.id.textView_day4_temp);
        textView_day4_percentOfRain = findViewById(R.id.textView_day4_percentOfRain);
        imageView_day4 = findViewById(R.id.imageView_day4);

        textView_day5 = findViewById(R.id.textView_day5);
        textView_day5_temp = findViewById(R.id.textView_day5_temp);
        textView_day5_percentOfRain = findViewById(R.id.textView_day5_percentOfRain);
        imageView_day5 = findViewById(R.id.imageView_day5);

        textView_day6 = findViewById(R.id.textView_day6);
        textView_day6_temp = findViewById(R.id.textView_day6_temp);
        textView_day6_percentOfRain = findViewById(R.id.textView_day6_percentOfRain);
        imageView_day6 = findViewById(R.id.imageView_day6);

        textView_day7 = findViewById(R.id.textView_day7);
        textView_day7_temp = findViewById(R.id.textView_day7_temp);
        textView_day7_percentOfRain = findViewById(R.id.textView_day7_percentOfRain);
        imageView_day7 = findViewById(R.id.imageView_day7);

        textView_day8 = findViewById(R.id.textView_day8);
        textView_day8_temp = findViewById(R.id.textView_day8_temp);
        textView_day8_percentOfRain = findViewById(R.id.textView_day8_percentOfRain);
        imageView_day8 = findViewById(R.id.imageView_day8);

    }


    private ArrayList getCityList(){
        ArrayList<CityInfoDto> list = new ArrayList<>();

        // 도시이름, 위경도 json데이터 파싱
        AssetManager assetManager= getAssets();

        //assets/json/city_lan_lng.json 파일 읽기 위한 InputStream
        try {
            InputStream is= assetManager.open("json/city_lan_lng.json");
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader reader= new BufferedReader(isr);

            StringBuffer buffer= new StringBuffer();
            String line= reader.readLine();
            while (line!=null){
                buffer.append(line+"\n");
                line=reader.readLine();
            }
            // json 데이터 얻어 옴
            String jsonData= buffer.toString();

            //json 데이터가 []로 시작하는 배열일때..
            JSONArray jsonArray= new JSONArray(jsonData);

            for(int i=0; i<jsonArray.length();i++){
                JSONObject jo=jsonArray.getJSONObject(i);

                // 한글 도시명, 영문 도시명, 위도, 경도
                String kr_city_name= jo.getString("kr_city_name");
                String en_city_name= jo.getString("en_city_name");
                double lat= jo.getDouble("lat");
                double lng= jo.getDouble("lng");

                CityInfoDto dto = new CityInfoDto(kr_city_name, en_city_name, lat, lng);
                list.add(dto);

                if(i == 0 || i == jsonArray.length() - 1){
                    Log.d("debug_city_info",list.get(i).getKr_city_name() + ", " + list.get(i).getKr_city_name() + ", " + list.get(i).getLat() + ", " + list.get(i).getLng());
                }

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double[] getLatAndLog(String searchText){
        double[] latAndLog = null;

        for(CityInfoDto cityInfo : cityInfoList) {
            if(cityInfo.getKr_city_name().equals(searchText)){
                latAndLog = new double[2];

                latAndLog[0] = cityInfo.getLat();
                latAndLog[1] = cityInfo.getLng();
                break;
            }
        }

        return latAndLog;
    }

    class SearchThread extends Thread{
        private Handler handler;
        private Message msg;
        private double[] latAndLog;

        public SearchThread(Handler handler, double[] latAndLog){
            this.handler = handler;
            this.msg = handler.obtainMessage();
            this.latAndLog = latAndLog;
        }

        @Override
        public void run() {

            jsonWeather = new JsonObject();

            String appId = "e3bceac5303b25ed6fbb27c0da5ad14f";
            double lat = latAndLog[0];
            double log = latAndLog[1];
            String apiURL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat +"&lon=" + log+ "&exclude=minutely,alerts&appid=" + appId;

            Map<String, String> requestHeaders = new HashMap<>();
            WeatherApi weatherApi = new WeatherApi();
            String responseBody = weatherApi.get(apiURL,requestHeaders);

            // 검색결과 json
            jsonWeather = new JsonParser().parse(responseBody).getAsJsonObject();

            msg.obj = jsonWeather;
            msg.what = 0;

            handler.sendMessage(msg);
        }
    }

    public String getDate(Long datetime){
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy년 MM월 dd일", Locale.KOREA);
        Long time = datetime  * 1000;
        Date date = new Date(time);

        int day =  Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch(day){
          case  1:
              dayOfWeek = "일";
            break;
            case  2:
                dayOfWeek = "월";
                break;
            case  3:
                dayOfWeek = "화";
                break;
            case  4:
                dayOfWeek = "수";
                break;
            case  5:
                dayOfWeek = "목";
                break;
            case  6:
                dayOfWeek = "금";
                break;
            case  7:
                dayOfWeek = "토";
                break;
          default:
            break;
        }
        String formattedDate = format1.format(date) + " (" + dayOfWeek + ")";

        return formattedDate;
    }

    public static String getHour(Long datetime){
        SimpleDateFormat format1 = new SimpleDateFormat ( "aa hh시", Locale.KOREA);
        Long time = datetime  * 1000;
        Date date = new Date(time);

        String formattedDate = format1.format(date);

        return formattedDate;
    }

    public String getDay(Long datetime){
        String day = "";
        
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy년 MM월 dd일", Locale.KOREA);
        Long time = datetime  * 1000;
        Date date = new Date(time);
        String formattedDate = format1.format(date);
        day = formattedDate;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        Date nDate = null;
        
        try {
            nDate = dateFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일요일";
                break;
            case 2:
                day = "월요일";
                break;
            case 3:
                day = "화요일";
                break;
            case 4:
                day = "수요일";
                break;
            case 5:
                day = "목요일";
                break;
            case 6:
                day = "금요일";
                break;
            case 7:
                day = "토요일";
                break;

        }

        return day;   
    }
}