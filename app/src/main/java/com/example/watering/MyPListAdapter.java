package com.example.watering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyPListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Plant> Ad_arrP = new ArrayList<Plant>();
   // LayoutInflater layoutInflater;

    TextView plantName_textView;
    ImageView plantPhoto_imageView;

    public MyPListAdapter(){}

    public MyPListAdapter(Context context, ArrayList<Plant> Ad_arrP){
        this.context = context;
        this.Ad_arrP = Ad_arrP;
        //layoutInflater = LayoutInflater.from(context);
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

        if(convertView == null){ // convertView에 item.xml 뷰를 불러옴
            // Activity가 아니기 때문에 convertView.findViewById(R.id.xxx)앞에 inflate받은 뷰
            convertView = LayoutInflater.from(context).inflate(R.layout.layoutitem,null);
            plantPhoto_imageView = (ImageView)convertView.findViewById(R.id.itemImageView);
            plantName_textView = (TextView)convertView.findViewById(R.id.itemTextView);
        }

        String imageStr = Ad_arrP.get(position).getPlantPhotoInfo();
        Bitmap bitmap = StringToBitmap(imageStr);
        plantPhoto_imageView.setImageBitmap(bitmap);

        plantName_textView.setText(Ad_arrP.get(position).getPlantName());

        return convertView;
    }

    public static Bitmap StringToBitmap(String imgStr){
        try{
            byte[] encodeByte = Base64.decode(imgStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }
}
