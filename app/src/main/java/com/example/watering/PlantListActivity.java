package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

/*
        // Plant List Reset
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        Long PCount_long = postSnapshot.child(Ids).getChildrenCount();
                        int PCount_int = Long.valueOf(PCount_long).intValue() - 2;
                        //로그인한 해당 Ids의 식물 이름 목록 가져오기 + ArrList에 저장
                        for(int j = 0; j < PCount_int; j++){
                            String pStr = "plant" + String.valueOf(j+1);
                            String plantName = postSnapshot.child(Ids).child(pStr).child("plantName").getValue().toString();
                            String plantLastWater = postSnapshot.child(Ids).child(pStr).child("plantLastWater").getValue().toString();
                            int plantCycle =  Integer.parseInt(postSnapshot.child(Ids).child(pStr).child("plantCycle").getValue().toString());
                            //String plantPhotoInfo = "null";
                            String plantPhotoInfo = postSnapshot.child(Ids).child(pStr).child("plantPhotoInfo").getValue().toString();
                            String plantWaterCheck = postSnapshot.child(Ids).child(pStr).child("plantWaterCheck").getValue().toString();
                            Plant p = new Plant(plantName, plantCycle, plantLastWater, plantPhotoInfo, plantWaterCheck);
                            PArrayList.add(p);
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("PlantListActivity", "Failed", error.toException()); //log 로 실패 알림
            }
        });

*/

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
        listView = (ListView)findViewById(R.id.listView_xml);
        
        myPListAdapter = new MyPListAdapter(PlantListActivity.this, android.R.layout.simple_list_item_1, PArrayList);

        listView.setAdapter(myPListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast
                //Toast.makeText(getApplicationContext(), myPListAdapter.getItem(position).getPlantName(), Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(getApplicationContext(), plantInfo.class);
            }
        });



        /*       // 해당 아이디의 존재하는 식물 카운트
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    // 로그인 정보 받아오기
                    Intent intent = getIntent();
                    Ids = intent.getExtras().getString("Ids");
                    PCount_long = postSnapshot.child(Ids).getChildrenCount();//Plantstr = postSnapshot.child("Id1").child("plant1").child("name").getValue().toString();
                }
                PCount_int = Long.valueOf(PCount_long).intValue() - 2;

                //plList에 식물 이름 목록 저장
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    for(int i = 0; i < PCount_int; i++){
                        //로그인한 해당 Ids의 식물 이름 목록 가져오기
                        String pStr = "plant" + String.valueOf(i+1);
                        String plN = postSnapshot.child(Ids).child(pStr).child("name").getValue().toString();
                        //PList[i] = plN;
                        plant p = new plant();
                        p.setPlantName(plN);
                        p.setPlantPhoto(R.mipmap.ic_launcher);
                        PArrayList.add(p);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("plantListActivity", "Failed plantList", error.toException()); //log 로 실패 알림
            }
        });
*/


        /*
        // plant1의 선택 버튼
        Button buttonP1_sel = (Button) findViewById(R.id.P1_view);
        buttonP1_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            pc = (Long.valueOf(PlCount).intValue()) - 2;
                            for(int i = 0; i < pc; i++){
                                Plantstr = "plant" + String.valueOf(i+1);
                                String stName = postSnapshot.child(Ids).child(Plantstr).child("name").getValue().toString();
                                Intent intent = new Intent(getApplicationContext(), plantInfo.class);
                                intent.putExtra("plantName", stName);
                                startActivity(intent);

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("MainActivity", "Failed Login", error.toException()); //log 로 실패 알림
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
*/


    } //onCreate
} // main