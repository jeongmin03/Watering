package com.example.watering;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    public AlarmReceiver(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        String plantName = intent.getStringExtra("PlantName");

        builder = null;
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(
                    new NotificationChannel("1", "기본채널", NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, "1");
        }
        else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setContentTitle("Watering Alarm");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(plantName + " 물 주기");
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);

    } // onReceive

} // AlarmReceiver
