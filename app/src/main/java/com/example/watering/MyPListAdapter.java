package com.example.watering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPListAdapter extends BaseAdapter {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private Context context;
    private ArrayList<Plant> Ad_arrP = new ArrayList<Plant>();
    //private LayoutInflater layoutInflater;

    TextView plantName_textView;
    ImageView plantPhoto_imageView;
    Switch plantWater_switch;

    public MyPListAdapter(){}

    public MyPListAdapter(Context context, int resource, ArrayList<Plant> Ad_arrP){
        this.context = context;
        this.Ad_arrP = Ad_arrP;
        ////layoutInflater = LayoutInflater.from(context);
    }

    @Override // 이 리스트뷰가 몇개의 아이템을 가지고 있는지
    public int getCount() {
        return this.Ad_arrP.size();
    }

    @Override // 현재 어떤 아이템인지를 알려주는 부분,
    // arraylist에 저장되있는 객체중 position에 해당하는 것 가져옴
    public Plant getItem(int position) {
        return this.Ad_arrP.get(position);
    }

    @Override // 현재 어떤 position인지 알려 줌
    public long getItemId(int position) {
        return position;
    }

    @Override // item과 xml을 연결하여 화면에 표시해 주는 부분
    //getView부분에서 반복문 실행되는 것, 순차적으로 한칸씩 화면 구성
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layoutitem, parent,false);
        }
        //plantPhoto_imageView = (ImageView)convertView.findViewById(R.id.itemImageView);


        Bitmap bitmap = StringToBitmap(Ad_arrP.get(position).getPlantPhotoInfo());
        //plantPhoto_imageView.setImageBitmap(bitmap);


        plantName_textView = (TextView)convertView.findViewById(R.id.itemTextView);
        plantName_textView.setText(Ad_arrP.get(position).getPlantName());

        plantWater_switch = (Switch)convertView.findViewById(R.id.itemSwitch);
        plantWater_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }

            }
        });
  /*      plantWater_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){

                // Firebase DB - 해당 식물의 plantChecked Update
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            //IdCount_long = postSnapshot.getChildrenCount();
                            String plantNum = Ad_arrP.get(position).getPlantName();
                            if(isChecked){
                                Ad_arrP.get(position).setPlantWaterCheck("true");
                                databaseReference.child(plantNum).child("plantChecked").setValue("true");
                            }
                            else{
                                Ad_arrP.get(position).setPlantWaterCheck("false");
                                databaseReference.child(plantNum).child("plantChecked").setValue("false");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("MainActivity", "Failed Login", error.toException()); //log 로 실패 알림
                    }
                });

            }
        });*/




        /*LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layoutitem, null, true);

        plantName_textView = (TextView)convertView.findViewById(R.id.itemTextView);
        //plantName_textView.setText(Ad_arrP.get(position).toString());
        plantPhoto_imageView = (ImageView)convertView.findViewById(R.id.itemImageView);
        Context context = plantPhoto_imageView.getContext();
        int id = context.getResources().getIdentifier("icon" + position, "drawable",
                context.getPackageName());
        plantPhoto_imageView.setImageResource(id);
*/

/*
        if(convertView == null){ // convertView에 item.xml 뷰를 불러옴
            // Activity가 아니기 때문에 convertView.findViewById(R.id.xxx)앞에 inflate받은 뷰
            convertView = LayoutInflater.from(context).inflate(R.layout.layoutitem,null);
            plantPhoto_imageView = (ImageView)convertView.findViewById(R.id.itemImageView);
            plantName_textView = (TextView)convertView.findViewById(R.id.itemTextView);
        }

        String imageStr = Ad_arrP.get(position).getPlantPhotoInfo();
        Bitmap bitmap = StringToBitmap(imageStr);
        plantPhoto_imageView.setImageBitmap(bitmap);

       ///// plantName_textView.setText(Ad_arrP.get(position).getPlantName());
*/
        return convertView;
    }

    public static Bitmap StringToBitmap(String imgStr){
        try{
            ////byte[] encodeByte = imgStr.getBytes();
            byte[] encodeByte = Base64.decode(imgStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }
}
