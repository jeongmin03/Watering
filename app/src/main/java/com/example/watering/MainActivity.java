package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    public long IdCount_long;
    public int IdCount_int;
    public String Ids;
    public long PCount_long;
    public int PCount_int;
    ArrayList<Plant> PArrayList = new ArrayList<Plant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 존재하는 아이디 개수 카운트
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    IdCount_long = postSnapshot.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainActivity", "Failed Login", error.toException()); //log 로 실패 알림
            }
        });

        //회원가입 버튼
        Button buttonJ = (Button) findViewById(R.id.M_joinButton);
        buttonJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼 : Main(로그인창) 입력정보와 DB에 있는 계정정보 비교
        Button buttonL = (Button) findViewById(R.id.M_loginButton);
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력받은 값
                EditText Li = (EditText) findViewById(R.id.M_personId);
                String LoginId = Li.getText().toString();
                EditText Lp = (EditText) findViewById(R.id.M_personPwd);
                String LoginPwd = Lp.getText().toString();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            IdCount_int = (Long.valueOf(IdCount_long).intValue());
                            for(int i = 0; i < IdCount_int; i++){
                                Ids = "Id" + String.valueOf(i+1);
                                String stI = postSnapshot.child(Ids).child("personId").getValue().toString();
                                String stP = postSnapshot.child(Ids).child("personPasswd").getValue().toString();
                                if(LoginId.equals("") || LoginPwd.equals("")) {
                                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                                }
                                if((stI.equals(LoginId))&& (stP.equals(LoginPwd))){
                                    Intent intent = new Intent(getApplicationContext(), PlantListActivity.class);

                                    PCount_long = postSnapshot.child(Ids).getChildrenCount();
                                    PCount_int = Long.valueOf(PCount_long).intValue() - 2;
                                    //로그인한 해당 Ids의 식물 이름 목록 가져오기 + ArrList에 저장
                                    for(int j = 0; j < PCount_int; j++){
                                        String pStr = "plant" + String.valueOf(j+1);
                                        //String plN = postSnapshot.child(Ids).child(pStr).child("plantName").getValue().toString();
                                        //Plant p = new Plant(R.mipmap.ic_launcher, plN);
                                        String plantName = postSnapshot.child(Ids).child(pStr).child("plantName").getValue().toString();
                                        String plantLastWater = postSnapshot.child(Ids).child(pStr).child("plantLastWater").getValue().toString();
                                        int plantCycle =  Integer.parseInt(postSnapshot.child(Ids).child(pStr).child("plantCycle").getValue().toString());
                                        //String plantPhotoInfo = "null";
                                        String plantPhotoInfo = postSnapshot.child(Ids).child(pStr).child("plantPhotoInfo").getValue().toString();
                                        String plantWaterCheck = postSnapshot.child(Ids).child(pStr).child("plantWaterCheck").getValue().toString();
                                        Plant p = new Plant(plantName, plantCycle, plantLastWater, plantPhotoInfo, plantWaterCheck);
                                        PArrayList.add(p);
                                    }
                                    //해당 Ids의 식물 리스트와 Ids 값 plantListActivity로 전달
                                    intent.putExtra("arr", PArrayList);
                                    intent.putExtra("Ids", Ids);
                                    startActivity(intent);
                                } // 아이디 비밀번호 확인
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("MainActivity", "Failed Login", error.toException()); //log 로 실패 알림
                    }
                });
            }
        });
    }// onCreate
} // main


