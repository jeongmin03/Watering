package com.example.watering;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPListAdapter extends BaseAdapter {
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    private Context context;
    private ArrayList<Plant> Ad_arrP;

    public MyPListAdapter() {
    }

    public MyPListAdapter(Context context, int layout, ArrayList<Plant> Ad_arrP) {
        this.context = context;
        this.Ad_arrP = Ad_arrP;
    }

    ///// 물 주기 버튼 - DB에 LastWater 정보 갱신
    public interface OnItemChanged {
        public void onItemChanged(ArrayList<Plant> arr, int position);
    }

    private OnItemChanged onItemChanged;

    public void setOnItemChanged(OnItemChanged onItemChanged) {
        this.onItemChanged = onItemChanged;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override // item과 xml을 연결하여 화면에 표시해 주는 부분
    //getView부분에서 반복문 실행되는 것, 순차적으로 한칸씩 화면 구성
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layoutitem, parent, false);
        }

        // PlantPhoto - ImageView, Firebase Cloud Storage
        ImageView plantPhoto_imageView = (ImageView) convertView.findViewById(R.id.itemImageView);
        String imageStr = Ad_arrP.get(position).getPlantPhotoInfo();

        storageReference.child(imageStr).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(plantPhoto_imageView.getContext()).load(uri)
                        .override(plantPhoto_imageView.getWidth(), plantPhoto_imageView.getHeight())
                        .into(plantPhoto_imageView);
            }
        });

        // PlantName - TextView
        TextView plantName_textView = (TextView) convertView.findViewById(R.id.itemTextView);
        plantName_textView.setText(Ad_arrP.get(position).getPlantName());

        // LastWater - TextView
        TextView plantLastWater_textView = (TextView) convertView.findViewById(R.id.LastWaterTextView);
        plantLastWater_textView.setText("Latest Water : " + Ad_arrP.get(position).getPlantLastWater());

        // Watering - Button
        Button waterButton = (Button) convertView.findViewById(R.id.Waterbutton);
        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ad_arrP.get(position).setPlantWaterCheck("true");
                Date today = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Ad_arrP.get(position).setPlantLastWater(simpleDateFormat.format(today));
                plantLastWater_textView.setText("Latest Water : " + Ad_arrP.get(position).getPlantLastWater());
                if (onItemChanged != null) {
                    onItemChanged.onItemChanged(Ad_arrP, position);
                }
            }
        });

        convertView.setBackgroundResource(R.drawable.listview_designs);
        return convertView;
    }
}
