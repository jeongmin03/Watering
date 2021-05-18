package com.example.watering;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLES30;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPListAdapter extends BaseAdapter {
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    private Context context;
    private ArrayList<Plant> Ad_arrP;


    public MyPListAdapter(){}

    public MyPListAdapter(Context context, int layout, ArrayList<Plant> Ad_arrP){
        this.context = context;
        this.Ad_arrP = Ad_arrP;
    }

    ///// 물 주기 버튼 - DB에 LastWater 갱신
    public interface OnItemChanged {
        public void onItemChanged(ArrayList<Plant> arr, int position);
    }
    private OnItemChanged onItemChanged;
    public void setOnItemChanged(OnItemChanged onItemChanged){
        this.onItemChanged= onItemChanged;
    }
    /////

    @Override // 이 리스트뷰가 몇개의 아이템을 가지고 있는지
    public int getCount() {
        return Ad_arrP.size();
    }

    @Override // 현재 어떤 아이템인지를 알려주는 부분,
    // arraylist에 저장되있는 객체중 position에 해당하는 것 가져옴
    public Plant getItem(int position) {
        return Ad_arrP.get(position);
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

        ImageView plantPhoto_imageView = (ImageView)convertView.findViewById(R.id.itemImageView);
        String imageStr = Ad_arrP.get(position).getPlantPhotoInfo();

        storageReference.child(imageStr).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(plantPhoto_imageView.getContext()).load(uri)
                        .override(plantPhoto_imageView.getWidth(), plantPhoto_imageView.getHeight())
                        .into(plantPhoto_imageView);
            }
        });

        TextView plantName_textView = (TextView)convertView.findViewById(R.id.itemTextView);
        plantName_textView.setText(Ad_arrP.get(position).getPlantName());


        TextView plantLastWater_textView = (TextView)convertView.findViewById(R.id.LastWaterTextView);
        plantLastWater_textView.setText("Latest Water : " + Ad_arrP.get(position).getPlantLastWater());

        Button waterButton = (Button)convertView.findViewById(R.id.Waterbutton);
        waterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Ad_arrP.get(position).setPlantWaterCheck("true");
                Date today = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Ad_arrP.get(position).setPlantLastWater(simpleDateFormat.format(today));
                plantLastWater_textView.setText("Latest Water : " + Ad_arrP.get(position).getPlantLastWater());
                if(onItemChanged != null){
                    onItemChanged.onItemChanged(Ad_arrP, position);
                }
            }
        });



        return convertView;
    }
}
/*  byte[] bytes = imageStr.getBytes();
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            plantPhoto_imageView.setImageBitmap(bitmapImage);
        */