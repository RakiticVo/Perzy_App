package com.example.lvtn_app.View;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {
    public static final  String CHANNEL_1_ID = "channel1";
    public static final  String CHANNEL_2_ID = "channel2";
    public static final  String CHANNEL_3_ID = "channel3";
    @Override
    public void onCreate() {
        super.onCreate();

        this.createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Joining Group Chat Request",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Joining Project Request",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 2");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Message",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 2");

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}
