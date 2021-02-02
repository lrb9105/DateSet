package com.teamnova.dateset.post_writing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.teamnova.dateset.R;
import com.teamnova.dateset.adapter.PostAdapter;
import com.teamnova.dateset.adapter.ViewPagerAdapter;
import com.teamnova.dateset.dto.PostDto;
import com.teamnova.dateset.dto.UserDto;
import com.teamnova.dateset.home.HomeActivity;
import com.teamnova.dateset.post_list.PostListActivity;
import com.teamnova.dateset.util.PostManager;
import com.teamnova.dateset.util.ProgressLoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PostModifyActivity extends AppCompatActivity {
    // 데이터를 저장할 때 사용할 키값 - 나와 연결된 상대는 동일한 키값을 갖는다.
    private String key = HomeActivity.userInfo.getSharedKey();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ALBUM_PICK = 2;

    private Button btn_selection_photo;
    private EditText editTextText_write;
    private EditText editTextPlace;
    //private EditText editTextWeather;
    private Button registerComplete;
    private ImageButton btn_close;
    private PostManager postManager;
    private UserDto userInfo;
    private Spinner spinnerWeather;
    private String weatherFromSpinner;

    // 이미지
    private ImageView postImage;
    private Bitmap postImageBitmap;

    //private ViewPager2 viewPager2;
    private String mCurrentPhotoPath;
    //private ArrayList<Bitmap> imgList;

    // indicator
    //private WormDotsIndicator wormDotsIndicator;

    // db참조 객체
    private DatabaseReference mDatabaseRef;

    // 로딩바
    private ProgressLoadingDialog customProgressDialog;

    private PostDto postInfo;
    private PostAdapter postAdapter;
    private int position;

    private boolean isImgChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_modify);
        this.initialize();
    }


    /**
     1. 메소드명: initialize
     2. 역할: 컴포넌트 초기화
     */
    private void initialize(){
        // 로딩바
        //로딩창 객체 생성
        customProgressDialog = new ProgressLoadingDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        Intent intent = getIntent();
        postInfo = (PostDto)intent.getSerializableExtra("POST_INFO");
        //PostAdapter = (PostAdapter)intent.getSerializableExtra("POST_ADAPTER");
        position = intent.getIntExtra("position",0);

        // db 참조객체 가져오기
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("posts/"+key);

        postManager = new PostManager();
        //imgList = new ArrayList<>();

        //viewPager2 = findViewById(R.id.viewPager_modi);
        //wormDotsIndicator = (WormDotsIndicator) findViewById(R.id.worm_dots_indicator_modi);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_close.setBackgroundColor(Color.LTGRAY);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    btn_close.setBackgroundColor(Color.TRANSPARENT);
                }

                return false;
            }
        });

        btn_selection_photo = findViewById(R.id.btn_selection_photo_modi);
        btn_selection_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostModifyActivity.this);

                builder.setItems(R.array.photoOrImageWithoutRemove, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        if(pos == 0){ //직접촬영
                            dispatchTakePictureIntent();
                        } else { //갤러리에서 가져오기
                            doTakeMultiAlbumAction();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        editTextText_write = findViewById(R.id.editTextText_write_modi);
        editTextPlace = findViewById(R.id.editTextPlace_modi);
        //editTextWeather = findViewById(R.id.editTextWeather_modi);
        spinnerWeather = findViewById(R.id.spinnerWeather_modi);

        ArrayAdapter weatherAdapter = ArrayAdapter.createFromResource(this, R.array.weather, android.R.layout.simple_spinner_dropdown_item);
        weatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeather.setAdapter(weatherAdapter); //어댑터에 연결해줍니다.
        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weatherFromSpinner = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registerComplete = findViewById(R.id.registerComplete_modi);
        registerComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<String> imgStrList = postManager.convertToStrList(imgList);

                String textContent = editTextText_write.getText().toString();
                String place = editTextPlace.getText().toString();
                //String weather = editTextWeather.getText().toString();
                String weather = weatherFromSpinner;
                // 나중에 로그인 하고 수정하기
                userInfo = (UserDto)getIntent().getSerializableExtra("USER_INFO");
                String writer = userInfo.getNickName();
                String id = userInfo.getId();
                modify(id,writer,null, textContent, place, weather);
            }
        });
        editTextText_write.setText(postInfo.getTextContent());
        editTextPlace.setText(postInfo.getPlace());
        //editTextWeather.setText(postInfo.getWeather());

        // 이미지
        postImage = findViewById(R.id.postImage);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("posts/" + key + "/" + postInfo.getKey() + ".JPEG");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(PostModifyActivity.this)
                            .load(task.getResult())
                            //.override(1024, 980)
                            .into(postImage);
                } else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Toast.makeText(PostModifyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void modify(String id,String writer, ArrayList<String> imgList, String textContent, String place, String weather){
        if(textContent.equals("")){ // 게시글 내용이 없다면
            Toast.makeText(this, "글을 작성해주세요", Toast.LENGTH_LONG).show();
        } else{
            PostDto postInfo = postManager.writePost(id,writer,imgList,textContent,place,weather);

            HashMap<String,Object> modifiedMapInfo = new HashMap<>();
            modifiedMapInfo.put(this.postInfo.getKey(),postInfo);

            customProgressDialog.show();
            customProgressDialog.setCancelable(false);

            mDatabaseRef.updateChildren(modifiedMapInfo);

            // 이미지 저장
            if(isImgChanged){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                postImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference postStorageRef = FirebaseStorage.getInstance().getReference("posts/" + key + "/" + this.postInfo.getKey() + ".JPEG");
                UploadTask uploadTask =postStorageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        customProgressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        customProgressDialog.dismiss();

                        // 다이얼로그 메시지 생성
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostModifyActivity.this);

                        builder.setTitle("").setMessage("게시물 수정이 완료되었습니다!");

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                                intent.putExtra("USER_INFO",userInfo);
                                startActivity(intent);
                                finish();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        Log.d("debug_upload",postStorageRef.getDownloadUrl().toString());
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    }
                });
            } else{
                customProgressDialog.dismiss();

                // 다이얼로그 메시지 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(PostModifyActivity.this);

                builder.setTitle("").setMessage("게시물 수정이 완료되었습니다!");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                        intent.putExtra("USER_INFO",userInfo);
                        startActivity(intent);
                        finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
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
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,REQUEST_ALBUM_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 직접촬영
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        if (bitmap != null) {
                            //imgList.add(bitmap);
                            isImgChanged = true;
                            postImageBitmap = bitmap;
                            postImage.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 갤러리에서 가져오기
        } else if(requestCode == REQUEST_ALBUM_PICK && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            // 선택한 사진이 한장이라면 clipData존재 X
            if(clipData == null){
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    // 이미지 저장
                    isImgChanged = true;
                    postImageBitmap = bitmap;
                    postImage.setImageBitmap(bitmap);
                    //imgList.add(BitmapFactory.decodeStream(in));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } /*else {
                InputStream in = null;

                for(int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri =  clipData.getItemAt(i).getUri();
                    try {
                        in = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap img = BitmapFactory.decodeStream(in);
                    postImageBitmap = img;
                    //imgList.add(img);
                }
            }*/
        }

        //viewPager2.setAdapter(new ViewPagerAdapter(imgList));
        // wormDotsIndicator.setViewPager2(viewPager2);
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
}