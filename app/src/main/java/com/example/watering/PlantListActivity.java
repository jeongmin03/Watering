package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class PlantListActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String Ids;

    ListView listView;
    MyPListAdapter myPListAdapter;
    ArrayList<Plant> PArrayList = new ArrayList<Plant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        // 이전 Activity(Main)로 부터 PArrayList와 Ids 정보 가져오기
        Ids = getIntent().getStringExtra("Ids");
        PArrayList = (ArrayList<Plant>)getIntent().getSerializableExtra("arr");

        //로그아웃 버튼
        Button buttonLogOut = (Button) findViewById(R.id.PL_logOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // 식물 추가 버튼
        Button buttonAddPlant = (Button)findViewById(R.id.PL_photo);
        buttonAddPlant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), PlantAddActivity.class);
                intent.putExtra("Ids", Ids);
                intent.putExtra("PListSize", PArrayList.size());
                startActivity(intent);
            }
        });



        // 리스트뷰
        listView = findViewById(R.id.listView_xml); //
        myPListAdapter = new MyPListAdapter(PlantListActivity.this, android.R.layout.simple_list_item_1, PArrayList);
        listView.setAdapter(myPListAdapter);
        myPListAdapter.setOnItemChanged(new MyPListAdapter.OnItemChanged() {
            @Override
            public void onItemChanged(ArrayList<Plant> arr, int position) {
                ArrayList<Plant> changedPlant = new ArrayList<Plant>();
                changedPlant = arr;  //Toast.makeText(getApplicationContext(), PArrayList.get(0).getPlantLastWater(), Toast.LENGTH_LONG).show();

                Toast.makeText(getApplicationContext(), changedPlant.get(position).getPlantName() + "에 물을 줬습니다.", Toast.LENGTH_LONG).show();
                databaseReference.child("Watering").child(Ids).child(changedPlant.get(position).getPlantNum()).child("plantLastWater").setValue(changedPlant.get(position).getPlantLastWater());
                setAlarmNotification(changedPlant.get(position).getPlantName());
            }
        });


    } //onCreate

    // 알림 설정
    private  void setAlarmNotification(String plantName){

        Intent receiverIntent = new Intent(PlantListActivity.this, AlarmReceiver.class);
        receiverIntent.putExtra("PlantName", plantName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PlantListActivity.this, 0, receiverIntent, 0);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, +1);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


        /* Activity에서 Adapter로 전달
            receiverIntent.putExtra("PlantName", plantName);
            sendBroadcast(receiverIntent);
        */
    }

} // main