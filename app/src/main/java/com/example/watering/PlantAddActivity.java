package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PlantAddActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String Ids;
    private long PlCount_long;
    private int PlCount_int;
    private String plantName;
    private int plantCycle;
    private String plantLastWater;
    private String plantPhoto;

    private int pListSize;

    //final static int TAKE_PICTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    Uri photoURI;


    private File createImageFile() throws IOException {
        // 이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // 파일 저장 : path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // 파일 생성시 에러 발생
            }
            // 파일 성공적 생성시 계속 실행
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.watering", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    ///// 카메라 촬영 /////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_add);

        // 이전 Activity(PL)로 부터 Ids 정보, PListSize 값(식물 목록의 #식물)  가져오기
        Ids = getIntent().getStringExtra("Ids");
        pListSize = getIntent().getIntExtra("PListSize", 1);

        // 식물 이름으로 검색

        // 식물 사진으로 검색
        Button buttonSearchPlant = (Button)findViewById(R.id.PA_Search);
        buttonSearchPlant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://images.google.com"));
                startActivity(intent);
            }
        });

        // 카메라 권한 / 허가
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(PlantAddActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // 카메라 버튼 : 사진 촬영
        Button buttonCameraPhoto = (Button)findViewById(R.id.PA_Camera);
        buttonCameraPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dispatchTakePictureIntent();
            }
        });

        // 앨범 버튼 : 앨범에서 사진 가져오기
        Button buttonAlbumPhoto = (Button)findViewById(R.id.PA_Album);
        buttonAlbumPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                startActivity(intent);
            }
        });

        // 저장 버튼 : 식물 생성 - firebase 저장
        Button buttonSavePlant = (Button)findViewById(R.id.PA_SavePlant);
        buttonSavePlant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // 식물 이름 정보, 물주는 주기 정보
                EditText PL = (EditText) findViewById(R.id.PA_plantName);
                plantName = PL.getText().toString();
                EditText PC = (EditText) findViewById(R.id.PA_plantCycle);
                plantCycle = Integer.parseInt(PC.getText().toString());

                // 식물 생성시 시간 정보 -> lastWater로 저장
                Date today = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                plantLastWater = simpleDateFormat.format(today);

                // 식물 사진 정보
                plantPhoto = "not yet";

                // 식물 생성
                Plant p = new Plant(plantName, plantCycle, plantLastWater, plantPhoto);
                String plantNum = "plant" + String.valueOf(pListSize+1);
                databaseReference.child("Watering").child(Ids).child(plantNum).setValue(p);

                Toast.makeText(getApplicationContext(), plantNum, Toast.LENGTH_LONG).show();

               // Intent intent = new Intent(getApplicationContext(), PlantListActivity.class);
               // startActivity(intent);

            } // buttonSavePlant - onClick
        }); // buttonSavePlant - onClickListener



/*
        // 검색 버튼
        Button buttonSearchPlant = (Button)findViewById(R.id.PA_Search);
        buttonSearchPlant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String plantGroup = "과과과";
                // EditText 입력 값 : 식물 이름
                EditText PN = (EditText) findViewById(R.id.PA_textbox);
                String plantName = PN.getText().toString();
                String plantUrl = "https://ko.wikipedia.org/wiki/" + plantName;
                
                try {
                    Document document = Jsoup.connect(plantUrl).get();
                    Elements elems = document.select("table.infobox tbody tr:eq(3)").select("td table tbody tr:eq(5)");
                    for(Element element : elems){
                        String st = element.select("td:eq(1) a").text();
                        if(st.substring(st.length()-1).equals("과")){
                            plantGroup = st;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), plantGroup, Toast.LENGTH_LONG).show();

            }
        });
*/

// WebView
        WebView webView = (WebView) findViewById(R.id.PA_WebView);
        webView.getSettings().setJavaScriptEnabled(true); // 자바 스크립트 허용
        String plantUrl = "https://www.google.co.kr/imghp?hl=ko";
        //String plantUrl = "https://ko.wikipedia.org/wiki/" + "장미";
        webView.loadUrl(plantUrl);
        //webView.loadUrl("https://images.google.com"); // 웹뷰 실행
        webView.setWebChromeClient(new WebChromeClient()); // 웹뷰에서 크롬 실행가능하도록
        class WebViewClientClass extends WebViewClient { // 페이지 이동
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url){
                Log.d("check URL", url);
                webView.loadUrl(url);
                return true;
            }
        }
        webView.setWebViewClient(new WebViewClientClass ());
/*
        // WebView
        WebView webView = (WebView) findViewById(R.id.PA_WebView);
        webView.getSettings().setJavaScriptEnabled(true); // 자바 스크립트 허용
        String plantUrl = "https://ko.wikipedia.org/wiki/" + "장미";
        webView.loadUrl(plantUrl);
        //webView.loadUrl("https://images.google.com"); // 웹뷰 실행
        webView.setWebChromeClient(new WebChromeClient()); // 웹뷰에서 크롬 실행가능하도록
        class WebViewClientClass extends WebViewClient { // 페이지 이동
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url){
                Log.d("check URL", url);
                webView.loadUrl(url);
                return true;
            }
        }
        webView.setWebViewClient(new WebViewClientClass ());
*/




    } //onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = intent.getExtras();
            //Bitmap imgBitmap = (Bitmap) extras.get("data");
            //intent.getData(); // 찍은 사진의 data
            //imageView.setImageURI(photoURI);
            ((ImageView)findViewById(R.id.PA_IMAGE)).setImageURI(photoURI);
        }

    } // onActivityResult
} // PlantAddActivity

  /*
                // 해당 아이디의 존재하는 식물 목록 수 카운트
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 로그인 정보 받아오기 + 해당 아이디의 식물 목록 식물 개수 구하기
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            //Intent intent = getIntent();
                            //Ids = intent.getExtras().getString("Ids");
                            PlCount_long = postSnapshot.child(Ids).getChildrenCount();
                        }
                        PlCount_int = Long.valueOf(PlCount_long).intValue() - 2;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("PlantAddActivity", "Failed", error.toException()); //log 로 실패 알림
                    }
                }); // dataReference

                 */