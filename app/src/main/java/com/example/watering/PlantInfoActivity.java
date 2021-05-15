package com.example.watering;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlantInfoActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private String Ids;
    private String plantNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_info);
        ArrayList<Plant> selectedPlant = new ArrayList<Plant>();

        //로그아웃 버튼
        Button buttonLogOut = (Button)findViewById(R.id.P_LogOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        selectedPlant = (ArrayList<Plant>)getIntent().getSerializableExtra("selectedPlant");
        Ids = getIntent().getStringExtra("Ids");
        plantNum = selectedPlant.get(0).getPlantNum();

        Switch plantWater_switch = (Switch)findViewById(R.id.waterSwitch);
        plantWater_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // databaseReference.child("Watering").child(Ids).child(plantNum).child("plantWaterCheck").setValue();
                if(isChecked){
                    databaseReference.child("Watering").child(Ids).child(plantNum).child("plantWaterCheck").setValue("true");
                }
                else{
                    databaseReference.child("Watering").child(Ids).child(plantNum).child("plantWaterCheck").setValue("false");
                }
            }
        });


    }
}