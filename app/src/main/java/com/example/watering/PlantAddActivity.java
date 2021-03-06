package com.example.watering;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PlantAddActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();

    private String Ids;
    ArrayList<Plant> PArrayList = new ArrayList<Plant>();
    private int pListSize;

    private String plantNum;
    private String plantName;
    private int plantCycle;
    private String plantLastWater;
    private String plantPhotoInfo;

    ImageView plantImageView;
    String currentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    final static int PICK_FROM_ALBUM = 2;
    Uri photoURI;

    private String plantUrl = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_add);

        // ?????? Activity(PL)??? ?????? Ids ??????, PListSize ???(?????? ????????? #??????)  ????????????
        Ids = getIntent().getStringExtra("Ids");
        pListSize = getIntent().getIntExtra("PListSize", 1);

        // ????????? ?????? / ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PlantAddActivity", "?????? ?????? ??????");
            } else {
                Log.d("PlantAddActivity", "?????? ?????? ??????");
                ActivityCompat.requestPermissions(PlantAddActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // ????????? ?????? : ?????? ??????
        Button buttonCameraPhoto = (Button) findViewById(R.id.PA_Camera);
        buttonCameraPhoto.setBackgroundResource(R.drawable.ic_baseline_photo_camera_24);
        buttonCameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.PA_Camera:
                        dispatchTakePictureIntent();
                        break;
                }
            }
        });

        // ?????? ?????? : ???????????? ?????? ????????????
        Button buttonAlbumPhoto = (Button) findViewById(R.id.PA_Album);
        buttonAlbumPhoto.setBackgroundResource(R.drawable.ic_baseline_photo_24);
        buttonAlbumPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        // ?????? ???????????? ??????
        Button buttonSearchPPhoto = (Button) findViewById(R.id.PA_PhotoSearch);
        buttonSearchPPhoto.setBackgroundResource(R.drawable.ic_baseline_search_24);
        buttonSearchPPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                startActivity(intent);
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://images.google.com"));
                //startActivity(intent);
            }
        });

        // ????????? ?????? ????????????
        plantImageView = (ImageView) findViewById(R.id.PA_IMAGE);
        plantImageView.setClipToOutline(true);

        // ?????? ?????? : ?????? ?????? - firebase ??????
        Button buttonSavePlant = (Button) findViewById(R.id.PA_SavePlant);
        buttonSavePlant.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                plantNum = "plant" + String.valueOf(pListSize + 1);

                // ?????? ?????? ??????, ????????? ?????? ??????
                EditText PL = (EditText) findViewById(R.id.PA_plantName);
                plantName = PL.getText().toString();
                EditText PC = (EditText) findViewById(R.id.PA_plantCycle);
                plantCycle = Integer.parseInt(PC.getText().toString());

                // ?????? ????????? ?????? ?????? -> lastWater??? ??????
                Date today = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                plantLastWater = simpleDateFormat.format(today);

                // ?????? ?????? ??????
                Drawable drawable = plantImageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                // Cloud Storage??? ????????? Upload
                plantPhotoInfo = Ids + "/" + plantNum + "_" + plantName;
                StorageReference plantImageRef = storageReference.child(plantPhotoInfo);
                UploadTask uploadTask = plantImageRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? Cloud ????????? ??????.", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "?????? ?????? Cloud ????????? ??????.", Toast.LENGTH_LONG).show();
                    }
                });

                // ?????? ??????
                Plant p = new Plant(plantNum, plantName, plantCycle, plantLastWater, plantPhotoInfo);
                databaseReference.child("Watering").child(Ids).child(plantNum).setValue(p);

                // ?????? Setting
                setAlarmNotification(plantName, plantCycle);
                Toast.makeText(getApplicationContext(), "????????? ??????????????????.", Toast.LENGTH_LONG).show();

                // ????????? Plant List -> PlantListActivity??? ??????
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Intent intent = new Intent(getApplicationContext(), PlantListActivity.class);

                            Long PCount_long = postSnapshot.child(Ids).getChildrenCount();
                            int PCount_int = Long.valueOf(PCount_long).intValue() - 2;
                            //???????????? ?????? Ids??? ?????? ?????? ?????? ???????????? + ArrList??? ??????
                            for (int j = 0; j < PCount_int; j++) {
                                String pStr = "plant" + String.valueOf(j + 1);
                                String plantName = postSnapshot.child(Ids).child(pStr).child("plantName").getValue().toString();
                                String plantLastWater = postSnapshot.child(Ids).child(pStr).child("plantLastWater").getValue().toString();
                                int plantCycle = Integer.parseInt(postSnapshot.child(Ids).child(pStr).child("plantCycle").getValue().toString());
                                //String plantPhotoInfo = "null";
                                String plantPhotoInfo = postSnapshot.child(Ids).child(pStr).child("plantPhotoInfo").getValue().toString();
                                String plantWaterCheck = postSnapshot.child(Ids).child(pStr).child("plantWaterCheck").getValue().toString();
                                Plant p = new Plant(pStr, plantName, plantCycle, plantLastWater, plantPhotoInfo, plantWaterCheck);
                                PArrayList.add(p);
                            }
                            //?????? Ids??? ?????? ????????? plantListActivity??? ??????
                            intent.putExtra("arr", PArrayList);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("PlantListActivity", "Failed", error.toException()); //log ??? ?????? ??????
                    }
                });

            } // buttonSavePlant - onClick
        }); // buttonSavePlant - onClickListener


    } //onCreate

    // ?????? ??????
    private void setAlarmNotification(String plantName, int plantCycle) {
        Intent receiverIntent = new Intent(PlantAddActivity.this, AlarmReceiver.class);
        receiverIntent.putExtra("PlantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PlantAddActivity.this, 0, receiverIntent, 0);

        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.HOUR_OF_DAY, 12); //calendar.set(Calendar.MINUTE, 07);
        calendar.add(Calendar.DATE, +plantCycle);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

         /* Activity?????? Adapter??? ??????
            receiverIntent.putExtra("PlantName", plantName);
            sendBroadcast(receiverIntent); */
    }

    ///// ????????? ?????? /////
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  //prefix
                ".jpg",         // suffix
                storageDir    // directory
        );
        // ?????? ?????? : path for use with ACTION_VIEW intents
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
            } catch (IOException ex) { // ?????? ????????? ?????? ??????
                //Log.w("PlantAddActivity", "?????? ?????? ?????? ??????!", ex);
            }
            // ?????? ????????? ????????? ?????? ??????
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.watering.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    ///// ????????? ?????? /////

    // ?????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PlantAddActivity", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("PlantAddActivity", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(currentPhotoPath);
                        Bitmap bitmap;

                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver()
                                    , Uri.fromFile(file));
                            try {
                                bitmap = ImageDecoder.decodeBitmap(source);
                                if (bitmap != null) { //plantImageView.setImageBitmap(bitmap);
                                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);
                                    Bitmap rotatedBitmap = null;
                                    switch (orientation) {
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
                            } catch (IOException e) {
                                //
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) { //plantImageView.setImageBitmap(bitmap);
                                    ExifInterface ei = new ExifInterface(currentPhotoPath);
                                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);
                                    Bitmap rotatedBitmap = null;
                                    switch (orientation) {
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
                            } catch (IOException e) {
                                //
                            }
                        }
                    }
                    break;
                }
                case PICK_FROM_ALBUM: {
                    Uri selectedPhotoUri = intent.getData();
                    plantImageView.setImageURI(selectedPhotoUri);
                    break;
                }

            }
        } catch (Exception e) {

        }
    } // onActivityResult

    public static Bitmap rotateImage(Bitmap source, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight()
                , matrix, true);
    }

} // PlantAddActivity - main


