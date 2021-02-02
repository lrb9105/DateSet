package com.teamnova.dateset.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.teamnova.dateset.MainActivity;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.ViewPagerAdapter;
import com.teamnova.dateset.addedfunc.AddedFunctionActivity;
import com.teamnova.dateset.addedfunc.calendar.CalendarActivity;
import com.teamnova.dateset.addedfunc.find_restaurant.FindRestaurantActivity;
import com.teamnova.dateset.addedfunc.weather.WeatherActivity;
import com.teamnova.dateset.chatting.chatting.ChattingActivity;
import com.teamnova.dateset.chatting.service.ChattingAlarmService;
import com.teamnova.dateset.dto.SharedKeyDto;
import com.teamnova.dateset.dto.TokenDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.post_list.PostListActivity;
import com.teamnova.dateset.post_writing.PostWritingActivity;
import com.teamnova.dateset.util.BitmapConverter;
import com.teamnova.dateset.util.ProgressDialog;
import com.teamnova.dateset.util.ProgressLoadingDialog;
import com.teamnova.dateset.util.SPManager;
import com.teamnova.dateset.util.UploadThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;
    static final int REQUEST_IMAGE_CAPTURE_BACK = 3;
    static final int REQUEST_ALBUM_PICK_BACK = 4;
    static final int PROFILE = 5;
    static final int BACK_IMG = 6;

    private final int REQUEST_WIDTH = 512;
    private final int REQUEST_HEIGHT = 512;

    private String mCurrentPhotoPath;

    // 하단 버튼
    private LinearLayout layout_home;
    private LinearLayout layout_post_list;
    private LinearLayout layout_post_writing;
    private LinearLayout layout_chat;
    private LinearLayout layout_added_function;
    private CircleImageView user1_profile;
    private CircleImageView user2_profile;

    private ImageButton btn_home;
    private ImageButton btn_post_list;
    private ImageButton btn_post_writing;
    private ImageButton btn_chat;
    private ImageButton btn_added_function;

   private Button btn_find_restaurant;
   private Button btn_calendar;
   private Button btn_weather;

   private TextView nickName;

   // 배경 이미지
   private ImageView back_img;

   // 객체
   // 유저정보
   public static UserDto userInfo;
   // sharedKey
   public static SharedKeyDto sharedKeyDto;

   // 처음사귄날
    private TextView textView_first_day;

   private View[] viewArr;

   // 배경사진
    private static Bitmap backBitmap;

    // 로딩바
    private ProgressLoadingDialog customProgressDialog;

    // 프로필사진 가져오기
    private StorageReference storageRef1;
    private StorageReference storageRef2;
    public static Bitmap bitmapUser1;
    public static  Bitmap bitmapUser2;

    // 파이어베이스 토큰
    private DatabaseReference dbRefToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressLoadingDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // 유저정보 가져오기 - 어떤 액티비티에서도 사용할 수 있음.
        Intent intent = getIntent();
        userInfo = (UserDto)intent.getSerializableExtra("USER_INFO");

        // SharedKey 객체 가져오기
        String sharedKeyId = userInfo.getSharedKey();
        DatabaseReference dbRefSharedKey = FirebaseDatabase.getInstance().getReference("sharedKey/"+sharedKeyId);
        dbRefSharedKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);

                sharedKeyDto = snapshot.getValue(SharedKeyDto.class);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                // user1에 들어가 있다면 user1프로필 사진 가져와서 user2_profile에 세팅
                if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                    String profileUrl = sharedKeyDto.getUser1ProfileUrl();
                    if(profileUrl != null){
                        storageRef.child(profileUrl).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩
                                    Glide.with(HomeActivity.this)
                                            .load(task.getResult())
                                            //.override(1024, 980)
                                            .into(user2_profile);
                                } else {
                                    // URL을 가져오지 못하면 토스트 메세지
                                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                    profileUrl = sharedKeyDto.getUser2ProfileUrl();
                    if(profileUrl != null){
                        storageRef.child(profileUrl).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩
                                    Glide.with(HomeActivity.this)
                                            .load(task.getResult())
                                            //.override(1024, 980)
                                            .into(user1_profile);
                                } else {
                                    // URL을 가져오지 못하면 토스트 메세지
                                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    // 닉네임 user2닉네임 ❤ user1닉네임
                    nickName.setText(sharedKeyDto.getUser2NickName() + " ❤ " + sharedKeyDto.getUser1NickName());
                } else{
                    //user2에 들어가 있다면 user2프로필 사진 가져와서 user2_profile에 세팅
                    //닉네임 user1닉네임 ❤ user2닉네임
                    String profileUrl = sharedKeyDto.getUser2ProfileUrl();
                    if(profileUrl != null){
                        storageRef.child(profileUrl).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩
                                    Glide.with(HomeActivity.this)
                                            .load(task.getResult())
                                            //.override(1024, 980)
                                            .into(user2_profile);
                                } else {
                                    // URL을 가져오지 못하면 토스트 메세지
                                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    profileUrl = sharedKeyDto.getUser1ProfileUrl();
                    if(profileUrl != null){
                        storageRef.child(profileUrl).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Glide 이용하여 이미지뷰에 로딩
                                    Glide.with(HomeActivity.this)
                                            .load(task.getResult())
                                            //.override(1024, 980)
                                            .into(user1_profile);
                                } else {
                                    // URL을 가져오지 못하면 토스트 메세지
                                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    // 닉네임 user1닉네임 ❤ user2닉네임
                    nickName.setText(sharedKeyDto.getUser1NickName() + " ❤ " + sharedKeyDto.getUser2NickName());
                }

                String backImgUrl = sharedKeyDto.getBackgroundUrl();

                // 배경 이미지가 존재한다면
                if(backBitmap != null){
                    back_img.setImageBitmap(backBitmap);
                    customProgressDialog.dismiss();
                } else if(backImgUrl != null){
                    /*storageRef.child(backImgUrl).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // Glide 이용하여 이미지뷰에 로딩
                                Glide.with(HomeActivity.this)
                                        .load(task.getResult())
                                        //.override(1024, 980)
                                        .into(back_img);
                            } else {
                                // URL을 가져오지 못하면 토스트 메세지
                                Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/

                    StorageReference islandRef = storageRef.child(backImgUrl);
                    final long size = 1024 * 1024 * 10;
                    islandRef.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length ) ;
                            backBitmap = bitmap;
                            back_img.setImageBitmap(bitmap);
                            customProgressDialog.dismiss();
                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            customProgressDialog.dismiss();
                            // Handle any errors
                        }
                    });
                }else{
                    customProgressDialog.dismiss();
                }


                // 프로필사진 가져오기
                //getProfileToBitmap();

                final long size = 1024 * 1024 * 5;

                if(sharedKeyDto.getUser1ProfileUrl() != null){
                    storageRef1 = FirebaseStorage.getInstance().getReference(sharedKeyDto.getUser1ProfileUrl());

                    storageRef1.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length ) ;
                            bitmapUser1 = bitmap;
                            Log.d("debug_bitmapUser1-4",bitmapUser1.toString());

                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("debug_bitmapUser1-4_failure",exception.getMessage());
                        }
                    });
                }

                if(sharedKeyDto.getUser2ProfileUrl() != null){
                    storageRef2 = FirebaseStorage.getInstance().getReference(sharedKeyDto.getUser2ProfileUrl());
                    storageRef2.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length ) ;
                            bitmapUser2 = bitmap;
                            Log.d("debug_bitmapUser2-4",bitmapUser2.toString());
                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

                // 처음사귄날
                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
                Date time = new Date();
                long diffDay = 0;

                String today = format1.format(time);
                String firstDay = sharedKeyDto.getDateOfFirstDay();

                try{
                    Date todayDate = format1.parse(today);
                    Date firstDate = format1.parse(firstDay);

                    diffDay = (todayDate.getTime() - firstDate.getTime()) / (24*60*60*1000);

                }catch(ParseException e){
                    e.printStackTrace();
                }


                textView_first_day.setText( (int)diffDay + "일째");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        // 부가기능 버튼 초기화
        btn_find_restaurant = findViewById(R.id.btn_find_restaurant);
        btn_calendar = findViewById(R.id.btn_calendar);
        btn_weather = findViewById(R.id.btn_weather);

        btn_find_restaurant.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        btn_weather.setOnClickListener(this);

        viewArr = initializeView();

        // 닉네임
        nickName = findViewById(R.id.nickName);

        // 처음 사귄날
        textView_first_day = findViewById(R.id.textView_first_day);
        // user프로필
        user1_profile = findViewById(R.id.user1_profile);
        user2_profile = findViewById(R.id.user2_profile);
        user2_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                builder.setItems( R.array.photoOrImageOrRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        String[] items = null;
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent(PROFILE);
                        } else if(pos == 1) { //갤러리에서 가져오기
                            doTakeMultiAlbumAction(PROFILE);
                        } else if(pos == 2){ //삭제
                            user2_profile.setImageDrawable(getDrawable(R.drawable.home_heart));

                            // 삭제 시 기본사진 비트맵으로 저장
                            Bitmap bitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.home_heart);

                            String profileUrl;

                            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                                profileUrl = sharedKeyDto.getUser1ProfileUrl();
                            } else{ //user2에 들어가 있다면
                                profileUrl = sharedKeyDto.getUser2ProfileUrl();
                            }
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference(profileUrl);

                            // static bitmap객체에 저장하기
                            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                                bitmapUser1 = bitmap;
                                Log.d("debug_bitmapUser1-1",bitmapUser1.toString());
                            } else{
                                bitmapUser2 = bitmap;
                            }
                            new UploadThread(bitmap, storageRef).start();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //배경 이미지
        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                builder.setItems( R.array.photoOrImageOrRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        String[] items = null;
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent(BACK_IMG);
                        } else if(pos == 1) { //갤러리에서 가져오기
                            doTakeMultiAlbumAction(BACK_IMG);
                        } else if(pos == 2){ //삭제
                            back_img.setImageDrawable(getDrawable(R.drawable.profile_back_img));

                            // 삭제 시 기본사진 비트맵으로 저장
                            Bitmap bitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.profile_back_img);
                            backBitmap = bitmap;

                            // 이미지 저장
                            String backImgUrl = "backImg/"+userInfo.getSharedKey()+".JPEG";

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference(backImgUrl);

                            // 배경사진을 첫번째로 저장하는 거라면
                            if(sharedKeyDto.getBackgroundUrl() == null){
                                DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("sharedKey/"+userInfo.getSharedKey());
                                sharedKeyDto.setBackgroundUrl(backImgUrl);
                                mDbRef.setValue(sharedKeyDto);
                            }

                            new UploadThread(bitmap, storageRef).start();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        /*spManager = new SPManager(this, "com.teamnova.dateset.profile",MODE_PRIVATE);
        // user1의 프로필이 존재한다면
        String user1ProfileStr = spManager.getProfileStr("user1");
        // 프로필 사진 출력
        if(!user1ProfileStr.equals("")){
            Bitmap user1ProfileBitmap = BitmapConverter.StringToBitmap(user1ProfileStr);
            user1_profile.setImageBitmap(user1ProfileBitmap);
        }*/

        /*Intent intentForService  = new Intent(this, ChattingAlarmService.class);
        intentForService.putExtra("ID",userInfo.getId());
        startService(intentForService);*/
        dbRefToken = FirebaseDatabase.getInstance().getReference("token/"+sharedKeyId);

        // 파이어베이스 토큰 생성
        // 토큰 저장
        dbRefToken.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TokenDto tokenDto =  snapshot.getValue(TokenDto.class);
                Log.d("debug_token","10101010");

                // 이 앱의 토큰을 가지고 온다.
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("debug_token","00000");
                            return;
                        }

                        // 이 앱의 토큰
                        String token = task.getResult();

                        Log.d("debug_token",token);

                        // 토큰이 저장되어있지 않다면
                        if(tokenDto == null){
                            Log.d("debug_token","11111");

                            TokenDto tokenDto2 = new TokenDto();
                            // token1에 저장
                            if(sharedKeyDto.getUser1Id().equals(userInfo.getId())){
                                Log.d("debug_token","2222");

                                tokenDto2.setUser1Token(token);
                            } else{ // token1에 저장
                                Log.d("debug_token","3333");

                                tokenDto2.setUser2Token(token);
                            }

                            dbRefToken.setValue(tokenDto2);
                        } else { //토큰이 저장되어 있다면
                            Log.d("debug_token","4444");

                            // 내가 user1이라면
                            if(sharedKeyDto.getUser1Id().equals(userInfo.getId())){
                                // user1의 토큰이 저장되어 있지 않다면
                                if(tokenDto.getUser1Token() == null){
                                    Log.d("debug_token","5555");

                                    tokenDto.setUser1Token(token);
                                }
                            } else{ // 내가 user2라면
                                // user2의 토큰이 저장되어 있지 않다면
                                if(tokenDto.getUser2Token() == null){
                                    Log.d("debug_token","6666");

                                    tokenDto.setUser2Token(token);
                                }
                            }

                            dbRefToken.setValue(tokenDto);
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Class cls = null;
        switch(v.getId()){
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
            /*case  R.id.layout_added_function:
            case  R.id.btn_added_function:
                cls = AddedFunctionActivity.class;
                setBackGroundColor(R.id.layout_added_function);
                break;*/
            case  R.id.btn_find_restaurant:
                cls = FindRestaurantActivity.class;
                break;
            case  R.id.btn_calendar:
                cls = CalendarActivity.class;
                break;
            case  R.id.btn_weather:
                cls = WeatherActivity.class;
                break;
            default:
                break;
        }

        if((v.getId() != R.id.layout_home) && (v.getId() != R.id.btn_home)){
            intent = new Intent(getApplicationContext(), cls);
            if(userInfo != null){
                intent.putExtra("USER_INFO",userInfo);
            }

            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBackGroundColor(R.id.layout_home);
    }

    public void setBackGroundColor(int id){
        int i = 0;
        for(View view : viewArr){
            if(view.getId() == id){
                Log.d("debug_nullCheck",""+ i);
                view.setBackgroundColor(Color.GRAY);
            } else{
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            i++;
        }
    }

    private View[] initializeView(){
        View[] viewArr = new View[4];
        viewArr[0] = layout_home;
        viewArr[1] = layout_post_list;
        viewArr[2] = layout_post_writing;
        viewArr[3] = layout_chat;
        return viewArr;
    }

    private void dispatchTakePictureIntent(int type) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //카메라 권한 없음
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},0);
        }else{ //카메라 권한 있음
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) { }
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.teamnova.dateset.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if(type == PROFILE) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } else{
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_BACK);
                    }
                }
            }
        }
    }

    private void doTakeMultiAlbumAction(int type){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        if(type == PROFILE){
            startActivityForResult(intent,REQUEST_ALBUM_PICK);
        } else{
            startActivityForResult(intent,REQUEST_ALBUM_PICK_BACK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 직접촬영 - 프로필
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            user2_profile.setImageBitmap(bitmap);

                            // static bitmap객체에 저장하기
                            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                                bitmapUser1 = bitmap;
                                Log.d("debug_bitmapUser1-2",bitmapUser1.toString());

                            } else{
                                bitmapUser2 = bitmap;
                            }

                            // 이미지 저장
                            // user1에 들어가 있다면
                            String profileUrl;

                            if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                                // 처음 저장이 아니라면
                                if(sharedKeyDto.getUser1ProfileUrl() != null){
                                    profileUrl = sharedKeyDto.getUser1ProfileUrl();
                                } else{ //처음 저장이라면
                                    profileUrl = "profile/"+userInfo.getId()+".JPEG";

                                    sharedKeyDto.setUser1ProfileUrl(profileUrl);
                                }
                            } else{ //user2에 들어가 있다면
                                if(sharedKeyDto.getUser2ProfileUrl() != null){
                                    profileUrl = sharedKeyDto.getUser2ProfileUrl();
                                } else{
                                    profileUrl = "profile/"+userInfo.getId()+".JPEG";
                                    sharedKeyDto.setUser2ProfileUrl(profileUrl);
                                }
                            }

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference(profileUrl);
                            new UploadThread(bitmap, storageRef).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 갤러리에서 가져오기 - 프로필
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    // 이미지 저장
                    /*Bitmap bitmap = BitmapFactory.decodeStream(in);*/
                    Bitmap bitmap = resizeBitmap(in);
                    user2_profile.setImageBitmap(bitmap);

                    // static bitmap객체에 저장하기
                    if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                        bitmapUser1 = bitmap;
                        Log.d("debug_bitmapUser1-3",bitmapUser1.toString());
                    } else{
                        bitmapUser2 = bitmap;
                    }

                    // 이미지 저장
                    // user1에 들어가 있다면
                    String profileUrl;

                    if(userInfo.getId().equals(sharedKeyDto.getUser1Id())){
                        if(sharedKeyDto.getUser1ProfileUrl() != null){ // 처음저장이 아니라면
                            profileUrl = sharedKeyDto.getUser1ProfileUrl();
                        } else{ // 처음 저장이라면
                            profileUrl = "profile/"+userInfo.getId()+".JPEG";
                            sharedKeyDto.setUser1ProfileUrl(profileUrl);
                        }
                    } else{ //user2에 들어가 있다면
                        if(sharedKeyDto.getUser2ProfileUrl() != null){ // 처음 저장이 아니라면
                            profileUrl = sharedKeyDto.getUser2ProfileUrl();
                        } else{ // 처음 저장이라면
                            profileUrl = "profile/"+userInfo.getId()+".JPEG";
                            sharedKeyDto.setUser2ProfileUrl(profileUrl);
                        }
                    }

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference(profileUrl);
                    new UploadThread(bitmap, storageRef).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 직접촬영 - 배경
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_BACK && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            back_img.setImageBitmap(bitmap);
                            backBitmap = bitmap;

                            // 이미지 저장
                            String backImgUrl = "backImg/"+userInfo.getSharedKey()+".JPEG";

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference(backImgUrl);

                            // 배경사진을 첫번째로 저장하는 거라면
                            if(sharedKeyDto.getBackgroundUrl() == null){
                                DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("sharedKey/"+userInfo.getSharedKey());
                                sharedKeyDto.setBackgroundUrl(backImgUrl);
                                mDbRef.setValue(sharedKeyDto);
                            }

                            new UploadThread(bitmap, storageRef).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 갤러리에서 가져오기 - 배경
        } else if(requestCode == REQUEST_ALBUM_PICK_BACK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    // 이미지 저장
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    //Bitmap bitmap = resizeBitmap(in);
                    back_img.setImageBitmap(bitmap);
                    backBitmap = bitmap;

                    // 이미지 저장
                    String backImgUrl = "backImg/"+userInfo.getSharedKey()+".JPEG";

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference(backImgUrl);

                    // 배경사진을 첫번째로 저장하는 거라면
                    if(sharedKeyDto.getBackgroundUrl() == null){
                        DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference("sharedKey/"+userInfo.getSharedKey());
                        sharedKeyDto.setBackgroundUrl(backImgUrl);
                        mDbRef.setValue(sharedKeyDto);
                    }

                    new UploadThread(bitmap, storageRef).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 촬영한 사진을 저장할 파일을 생성한다.
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap resizeBitmap(InputStream in){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap= BitmapFactory.decodeStream(in,null,options);

        int width = 300; // 축소시킬 너비
        int height = 300; // 축소시킬 높이
        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        if (bmpWidth > width) {
            // 원하는 너비보다 클 경우의 설정
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        } else if (bmpHeight > height) {
            // 원하는 높이보다 클 경우의 설정
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
        }

        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
        return resizedBmp;
    }

    public void getProfileToBitmap(){

    }
}