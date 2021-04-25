package com.example.watering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.watering.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class JoinActivity extends AppCompatActivity  {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    public long idCount_long;
    public int idCount_int;
    public String Ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 존재하는 아이디 개수 카운트
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        //fdCount = postSnapshot.child("Watering").getChildrenCount();
                        idCount_long = postSnapshot.getChildrenCount();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("JoinActivity", "Failed Join", error.toException()); //log 로 실패 알림
                }
            });

        //아이디 중복확인 : DB와 비교
        Button buttonDup = (Button) findViewById(R.id.J_dupButton);
        buttonDup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Ji = (EditText) findViewById(R.id.J_textId);
                String JoinId = Ji.getText().toString();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int exist = 0;
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            idCount_int = (Long.valueOf(idCount_long).intValue());
                            for(int i = 0; i < idCount_int; i++){
                                Ids = "Id" + String.valueOf(i+1);
                                String st = postSnapshot.child(Ids).child("personId").getValue().toString();
                                if(st.equals(JoinId)){
                                    Toast.makeText(getApplicationContext(), "존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                                    exist++;
                                    //Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                                    //startActivity(intent);
                                }
                            } // for i
                            if(exist == 0) {
                                Toast.makeText(getApplicationContext(), "사용가능한 아이디입니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("JoinActivity", "Failed Join", error.toException()); //log 로 실패 알림
                    }
                });
            }
        });

        // 가입 버튼 : 아이디,비밀번호 DB 저장
        Button buttonJoin = (Button) findViewById(R.id.J_joinButton);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText i = (EditText) findViewById(R.id.J_textId);
                String JoinId = i.getText().toString();
                EditText p = (EditText) findViewById(R.id.J_textPwd);
                String JoinPwd = p.getText().toString();
                EditText p2 = (EditText) findViewById(R.id.J_textPwd2);
                String JoinPwd2 = p2.getText().toString();
                if(!JoinPwd.equals(JoinPwd2)){
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(JoinId.equals("") || JoinPwd.equals("") || JoinPwd2.equals("")){
                    Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    User u = new User(JoinId, JoinPwd);
                    Ids = Ids.substring(0, 2) + String.valueOf(idCount_int);
                    databaseReference.child("Watering").child(Ids).setValue(u);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        //취소 버튼 : Main(로그인창)으로 이동
        Button button = (Button) findViewById(R.id.J_cancelButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }


}