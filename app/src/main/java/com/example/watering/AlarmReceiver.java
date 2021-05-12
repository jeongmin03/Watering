package com.example.watering;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver(){}

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        builder = null;
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }
        else {
            builder = new NotificationCompat.Builder(context);
        }

        // 알림창 눌렀을 때 나오는 Activity 화면
        Intent intentActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알림창 제목
        builder.setContentTitle("Watering Alarm");
        // 알림창 아이콘
        // builder.setSmallIcon(R.drawable.ic_launcher_background);
        // 알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(1, notification);

    }
}
