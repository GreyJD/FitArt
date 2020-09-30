package com.example.fitart;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class GetLocationService extends Service {


    // service code used from
    // https://stackoverflow.com/questions/34573109/how-to-make-an-android-app-to-always-run-in-background
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id"; // add once channel is in place

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        //do location stuff here

        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground(){
        Intent notificationIntent = new Intent(this, /**/MapsActivity.class/**/); //must alter if this moves to a new activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //NotificationCompat is for old versions, maybe update to Notification.builder? needs research
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,NOTIF_CHANNEL_ID)
                .setOngoing(true)
                //.setSmallIcon(R.drawable.ic_notification) //this is found in res/drawable... there is no icon so it gives an error
                .setContentTitle(getString(R.string.app_name))
                .setContentText("service running in background")
                .setContentIntent(pendingIntent)
                .build());
    }


}
