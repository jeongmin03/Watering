package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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


import java.io.ByteArrayOutputStream;
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
    private String plantPhotoInfo;

    private int pListSize;

    ImageView plantImageView;
    String currentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    Uri photoURI;

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  //prefix
                ".jpg",         // suffix
                storageDir    // directory
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
            } catch (IOException ex) { // 파일 생성시 에러 발생
                //Log.w("PlantAddActivity", "사진 파일 생성 에러!", ex);
            }

            // 파일 성공적 생성시 계속 실행
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.watering.fileprovider", photoFile);
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
                Log.d("PlantAddActivity", "권한 설정 완료");
            } else {
                Log.d("PlantAddActivity", "권한 설정 요청");
                ActivityCompat.requestPermissions(PlantAddActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // 카메라 버튼 : 사진 촬영
        Button buttonCameraPhoto = (Button)findViewById(R.id.PA_Camera);
        buttonCameraPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch (v.getId()){
                    case R.id.PA_Camera:
                        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(cameraIntent, TAKE_PICTURE);
                        dispatchTakePictureIntent();
                        break;
                }
                //dispatchTakePictureIntent();
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

        // 촬영한 사진 이미지뷰
        plantImageView = (ImageView)findViewById(R.id.PA_IMAGE);



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
                //plantImageView.setImageURI(photoURI);
                /*Bitmap bitmap = ((BitmapDrawable)plantImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                plantPhoto = byteArrayToBinaryString(data);
*/
                BitmapDrawable drawable = (BitmapDrawable)plantImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                plantPhotoInfo = BitmapToString(bitmap);

                // 식물 생성
                plantPhotoInfo = "PlantPhoto";
                Plant p = new Plant(plantName, plantCycle, plantLastWater, plantPhotoInfo);
                String plantNum = "plant" + String.valueOf(pListSize+1);
                databaseReference.child("Watering").child(Ids).child(plantNum).setValue(p);






                Toast.makeText(getApplicationContext(), plantNum, Toast.LENGTH_LONG).show();

               // Intent intent = new Intent(getApplicationContext(), PlantListActivity.class);
               // startActivity(intent);

            } // buttonSavePlant - onClick
        }); // buttonSavePlant - onClickListener

    } //onCreate

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PlantAddActivity", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                == PackageManager.PERMISSION_GRANTED ) {
            Log.d("PlantAddActivity", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try{
            switch(requestCode){
                case REQUEST_TAKE_PHOTO:{
                    if(resultCode == RESULT_OK){
                        File file = new File(currentPhotoPath);
                        Bitmap bitmap;

                        if(Build.VERSION.SDK_INT >= 29){
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver()
                            , Uri.fromFile(file));
                            try{
                                bitmap = ImageDecoder.decodeBitmap(source);
                                if(bitmap != null){ //plantImageView.setImageBitmap(bitmap);
                                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);
                                    Bitmap rotatedBitmap = null;
                                    switch(orientation){
                                        case ExifInterface.ORIENTATION_ROTATE_90:
                                            rotatedBitmap = rotateImage(bitmap, 90);
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_180:
                                            rotatedBitmap = rotateImage(bitmap, 180);
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_270:
                                            rotatedBitmap = rotateImage(bitmap, 270);
                                            break;
                                        case ExifInterface.ORIENTATION_NORMAL:
                                        default:
                                            rotatedBitmap = bitmap;
                                    }
                                    plantImageView.setImageBitmap(rotatedBitmap);

                                }
                            }catch(IOException e){
                                //
                            }
                        }
                        else{
                            try{
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if(bitmap != null){ //plantImageView.setImageBitmap(bitmap);
                                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);
                                    Bitmap rotatedBitmap = null;
                                    switch(orientation){
                                        case ExifInterface.ORIENTATION_ROTATE_90:
                                            rotatedBitmap = rotateImage(bitmap, 90);
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_180:
                                            rotatedBitmap = rotateImage(bitmap, 180);
                                            break;
                                        case ExifInterface.ORIENTATION_ROTATE_270:
                                            rotatedBitmap = rotateImage(bitmap, 270);
                                            break;
                                        case ExifInterface.ORIENTATION_NORMAL:
                                        default:
                                            rotatedBitmap = bitmap;
                                    }
                                    plantImageView.setImageBitmap(rotatedBitmap);
                                }
                            }catch (IOException e){
                                //
                            }
                        }
                    }
                    break;
                }
            }
        }catch (Exception e){

        }
    } // onActivityResult

    public static Bitmap rotateImage(Bitmap source, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight()
                , matrix, true);
    }



    public String BitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();

        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        ////String temp = bytes.toString();
        return temp;
    }


    /*
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }
*/




} // PlantAddActivity
/*
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
*/
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