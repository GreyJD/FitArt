package com.example.fitart;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID = "GetLocationServiceChannel";

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    // this creates the notification channel required by the OS for foreground services
    // in GetLocationService.java
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel service_Channel = new NotificationChannel(
                    CHANNEL_ID,
                    "FitArt Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(service_Channel);
        }
    }
}
