package com.teamnova.dateset.addedfunc.find_restaurant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.teamnova.dateset.R;
import com.teamnova.dateset.dto.SearchInfoDto;
import com.teamnova.dateset.login.register.PrivateInfoActivity;
import com.teamnova.dateset.login.register.RegisterActivity3;
import com.teamnova.dateset.util.NavaeSearchApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindRestaurantActivity extends FragmentActivity implements OnMapReadyCallback {
    // 뒤로가기 버튼
    private ImageButton btn_back;

    // 네이버 지도
    private FragmentManager fm;
    private MapFragment mapFragment;

    // 검색에 입력 텍스트
    private EditText editText_search;

    // 핸들러
    private Handler handler;
    // 검색정보 저장 리스트
    private ArrayList<SearchInfoDto> searchList;

    // 검색 버튼
    private ImageButton btn_search;

    // 마커 리스트
    private ArrayList<Marker> markserList;

    // 현재위치
    private FusedLocationSource mLocationSource;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int PERMISSION_REQUEST_CODE = 1000;

    // 네이버맵
    private NaverMap mNavermap;

    // 정보
    private InfoWindow infoWindow;

    // 현재 선택된 마커
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_restaurant);

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

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_back.setBackgroundColor(Color.LTGRAY);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_back.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 0){
                    mapFragment.getMapAsync(FindRestaurantActivity.this::onMapReady);
                }
            }
        };

        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editText_search.getText().toString();
                new SearchThread(handler,searchText).start();
            }
        });

        editText_search = findViewById(R.id.editText_search);
        // 엔터키 입력 이벤트
        editText_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i){
                    case KeyEvent.KEYCODE_ENTER:
                        if(keyEvent.getAction() == KeyEvent.ACTION_UP){
                            // 엔터키 입력 시 검색
                            String searchText = editText_search.getText().toString();
                            new SearchThread(handler,searchText).start();
                            return true;
                        }
                }
                return false;
            }
        });
        searchList = new ArrayList<>();

        // 지도 객체 생성
        fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.naver_map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.naver_map, mapFragment).commit();
        }

        // 이 메소드를 호출하면 onMapReady() callback 메소드가 호출된다.
        // 콜백에서 NaverMap객체를 이용해서 네이버지도와 사용자간의 인터페이스 기능을 수행할 수 있다.
        mapFragment.getMapAsync(this);

        // 마커리스트
        markserList = new ArrayList<>();

        mLocationSource = new FusedLocationSource(this,PERMISSION_REQUEST_CODE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNavermap = naverMap;

        // 현재위치 지정
        naverMap.setLocationSource(mLocationSource);
        ActivityCompat.requestPermissions(this,PERMISSIONS, PERMISSION_REQUEST_CODE);

        // UI 컨트롤 재배치
        UiSettings uiSettings = mNavermap.getUiSettings();
        uiSettings.setCompassEnabled(true); // 기본값 : true
        uiSettings.setScaleBarEnabled(true); // 기본값 : true
        uiSettings.setZoomControlEnabled(true); // 기본값 : true
        uiSettings.setLocationButtonEnabled(true); // 기본값 : false

        // 검색어를 입력했다면
        if(!editText_search.getText().toString().equals("")){

            // 정보창 생성
            infoWindow = new InfoWindow();
            infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
                @NonNull
                @Override
                protected View getContentView(@NonNull InfoWindow infoWindow) {
                    Marker marker = infoWindow.getMarker();
                    String titleOfDto = null;
                    String addressOfDto = null;
                    String link = null;

                    for(SearchInfoDto s : searchList){
                        if(s.getTitle().equals(marker.getCaptionText())){
                            titleOfDto = s.getTitle();
                            addressOfDto = s.getAddress();
                            link = s.getLink();
                        }
                    }

                    ViewGroup viewGroup = findViewById(R.id.layout);
                    View restaurant_info = LayoutInflater.from(FindRestaurantActivity.this).inflate(R.layout.view_restaurant_info,viewGroup,false);
                    TextView title = restaurant_info.findViewById(R.id.title);
                    TextView address = restaurant_info.findViewById(R.id.address);

                    SpannableString content = new SpannableString(titleOfDto);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    title.setText(content);

                    address.setText(addressOfDto);

                    return restaurant_info;
                }
            });

            infoWindow.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    Marker marker = infoWindow.getMarker();
                    String link = null;

                    Log.d("debug_markerClick","클릭됨");

                    for(SearchInfoDto s : searchList){
                        if(s.getTitle().equals(marker.getCaptionText())){
                            link = s.getLink();

                            Intent intent = new Intent(FindRestaurantActivity.this, RestaurantInfoWebview.class);

                            // instagram 링크의 경우 http가 들어가면 웹뷰로 바로 뜨지않고 어떤 앱으로 실행할 것인지 선택하는 스낵바가 나온다.
                            // 따라서 https로 변경해줘야 한다.
                            if(!link.equals("")){
                                if(link.contains("instagram")){
                                    link = link.replaceAll("http://","https://");

                                    if(!link.contains("www")){
                                        link = link.replaceAll("instagram","www.instagram");
                                    }
                                }

                                /*if(link.contains("blog.naver.com")){
                                    String title = s.getTitle();

                                    try {
                                        title = URLEncoder.encode(title, "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    link = "https://search.naver.com/search.naver?query="+title;
                                }*/
                            } else{ // 링크에 아무값도 넘어오지 않는경우 네이버에 해당 상호명을 검색하여 보여준다.
                                String title = s.getTitle();

                                try {
                                    title = URLEncoder.encode(title, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                link = "https://search.naver.com/search.naver?query="+title;
                            }


                            intent.putExtra("LINK",link);

                            Log.d("debug_address", link);

                            startActivity(intent);
                            break;
                        }
                    }
                    return true;
                }
            });

            // 지도를 클릭하면 정보 창을 닫음
            naverMap.setOnMapClickListener((coord, point) -> {
                if(currentMarker != null && currentMarker.getWidth() == 120){
                    currentMarker.setWidth(70);
                    currentMarker.setHeight(70);
                }
                Log.d("debug_marker","222222클릭");

                infoWindow.close();
            });

            /*// 마커를 클릭하면:
            Overlay.OnClickListener listener = overlay -> {
                Marker marker = (Marker)overlay;

                if (marker.getInfoWindow() == null) {
                    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                    infoWindow.open(marker);
                } else {
                    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                    infoWindow.close();
                }

                return true;
            };*/


            // 검색 시 마커 정보 등록
           if(searchList.size() > 0){
                /*CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(Double.valueOf(searchList.get(0).getMapY()), Double.valueOf(searchList.get(0).getMapX()))).zoomTo(13);
                naverMap.moveCamera(cameraUpdate);*/

                // 기존에 등록되어 있는 마커가 있다면 삭제
                if(markserList.size() > 0){
                    for(Marker marker : markserList){
                        marker.setMap(null);
                    }
                    markserList.clear();
                }
                Log.d("debug_markerList", ""+markserList.size());

                // 리스트에 새로운 마커 등록
                for(int i = 0; i < 5; i++){
                    Marker marker = new Marker();
                    markserList.add(marker);
                }

               // 마커정보 등록
                for(int i = 0; i < markserList.size(); i++){
                    SearchInfoDto dto = searchList.get(i);

                    String title = dto.getTitle();
                    String address = dto.getAddress();
                    String link = dto.getLink();
                    String latitude = dto.getMapY();
                    String longitude = dto.getMapX();

                    Marker marker = markserList.get(i);
                    //Marker marker = new Marker();

                    marker.setPosition(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
                    marker.setCaptionText(title);
                    marker.setWidth(70);
                    marker.setHeight(70);
                    marker.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            if (overlay instanceof Marker) {
                                Marker marker = (Marker) overlay;

                                if (marker.getInfoWindow() != null) {
                                    marker.setWidth(70);
                                    marker.setHeight(70);
                                    infoWindow.close();
                                }
                                else {
                                    // 기존에 열려있던 마커가있다면 닫아줌
                                    if(currentMarker != null && currentMarker.getWidth() == 120){
                                        currentMarker.setWidth(70);
                                        currentMarker.setHeight(70);
                                    }
                                    marker.setWidth(120);
                                    marker.setHeight(120);
                                    infoWindow.open(marker);
                                    currentMarker = marker;
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    /*switch(i){
                      case  0:
                          marker.setIcon(MarkerIcons.BLUE);
                        break;
                        case  1:
                            marker.setIcon(MarkerIcons.GREEN);
                            break;
                        case  2:
                            marker.setIcon(MarkerIcons.YELLOW);
                            break;
                        case  3:
                            marker.setIcon(MarkerIcons.GRAY);
                            break;
                        case  4:
                            marker.setIcon(MarkerIcons.BLACK);
                            break;

                      default:
                        break;
                    }*/
                    marker.setMap(naverMap);
                }
            }else{
                Toast.makeText(this, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class SearchThread extends Thread{
        private Handler handler;
        private Message msg;
        private String searchText;

        public SearchThread(Handler handler, String searchText){
            this.handler = handler;
            this.msg = handler.obtainMessage();
            this.searchText = searchText;
        }

        /*public SearchThread(String searchText){
            this.searchText = searchText;
        }*/

        @Override
        public void run() {
            String clientId = "6J5xu8CVXFRNSFbn7sj0"; //애플리케이션 클라이언트 아이디값"
            String clientSecret = "laJymQyHJJ"; //애플리케이션 클라이언트 시크릿값"

            searchList = new ArrayList<>();

            String text = null;
            if(searchText.equals("")){
                Toast.makeText(FindRestaurantActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                text = URLEncoder.encode(searchText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패",e);
            }

            String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text + "&&display=5";    // json 결과
            //String apiURL = "https://openapi.naver.com/v1/search/local.xml?query="+ text; // xml 결과

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);
            String responseBody = new NavaeSearchApi().get(apiURL,requestHeaders);
            
            // 검색결과 json
            JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            // 검색결과 jsonArray
            JsonArray jsonArray = jsonObject.getAsJsonArray("items");

            // 검색결과 to DTO
            if(jsonArray.size() > 0){
                for(int i = 0; i < jsonArray.size(); i++){
                    JsonObject j = jsonArray.get(i).getAsJsonObject();
                    String title = j.get("title").toString();
                    String link = j.get("link").toString();
                    String address = j.get("address").toString();
                    String mapX = j.get("mapx").toString();
                    String mapY = j.get("mapy").toString();

                    // 문자열에 ""이 포함되어있어서 제거해 줌
                    title = title.substring(1,title.length()-1).replaceAll("<b>","").replaceAll("</b>","");
                    link = link.substring(1,link.length()-1);
                    address = address.substring(1,address.length()-1);
                    mapX = mapX.substring(1,mapX.length()-1);
                    mapY = mapY.substring(1,mapY.length()-1);

                    Tm128 tm1238 = new Tm128(Double.parseDouble(mapX), Double.parseDouble(mapY));
                    LatLng latLng = tm1238.toLatLng();
                    // 경도 x좌표
                    String longitude = String.valueOf(latLng.longitude);
                    // 위도 y좌표
                    String latitude = String.valueOf(latLng.latitude);
                    SearchInfoDto dto = new SearchInfoDto(title,link,address,longitude,latitude);

                    searchList.add(dto);
                }
            }
            
            for(SearchInfoDto dto : searchList){
                Log.d("debug_searchList", dto.getTitle() + ", " + dto.getLink() + ", " + dto.getAddress() + ", " + dto.getMapX() + ", " + dto.getMapY());
            }

            msg.obj = searchList;
            msg.what = 0;

            handler.sendMessage(msg);

            //Log.d("debug_json",jsonObject.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(searchList.size() > 0){
                    mNavermap.setLocationTrackingMode(LocationTrackingMode.Follow);
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(Double.valueOf(searchList.get(0).getMapY()), Double.valueOf(searchList.get(0).getMapX())));
                    mNavermap.moveCamera(cameraUpdate);
                    cameraUpdate = CameraUpdate.zoomTo(13);
                    mNavermap.moveCamera(cameraUpdate);
                } else{
                    mNavermap.setLocationTrackingMode(LocationTrackingMode.Follow);
                }
            }
        }
    }

    /*@Override
    public boolean onClick(@NonNull Overlay overlay) {
        Toast.makeText(this, "11123", Toast.LENGTH_SHORT).show();
        if (overlay instanceof Marker) {
            Marker marker = (Marker) overlay;
            if (marker.getInfoWindow() != null) {
                infoWindow.close();
                Toast.makeText(this.getApplicationContext(), "InfoWindow Close.", Toast.LENGTH_LONG).show();
            }
            else {
                infoWindow.open(marker);
                Toast.makeText(this.getApplicationContext(), "InfoWindow Open.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }*/
}