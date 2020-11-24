package com.example.fitart;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Interfaces.SimpleTempCallback;
import mad.location.manager.lib.Services.KalmanLocationService;
import mad.location.manager.lib.Services.ServicesHelper;


public class GetLocationService extends Service implements LocationServiceInterface {

        // service code used from
        // https://stackoverflow.com/questions/34573109/how-to-make-an-android-app-to-always-run-in-background
        private static final int NOTIF_ID = 1;
        private static final String NOTIF_CHANNEL_ID = "GetLocationServiceChannel"; // add once channel is in place

        LatLng newLocation = null;
        LatLng lastLocation = null;
        PolyLineData newLine = null;
        ArrayList<LatLng> locationList;
        MapStateManager currentsState;

        // boolean isActivityRunning = true;
        // IsActivityOnReceiver isActivityOnReceiver;


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();

            locationList = new ArrayList<LatLng>();
            ServicesHelper.addLocationServiceInterface(this);
            ServicesHelper.getLocationService(this, new SimpleTempCallback<KalmanLocationService>() {
                @Override
                public void onCall(KalmanLocationService value) {
                    if (value.IsRunning()) {
                        return;
                    }
                    value.stop();
                    KalmanLocationService.Settings settings = KalmanLocationService.defaultSettings;
                    value.reset(settings);
                    value.start();
                }
            });


        }

        @Override
        public void onDestroy() {

            broadcastLocation();
            ServicesHelper.getLocationService(this, new SimpleTempCallback<KalmanLocationService>() {
                @Override
                public void onCall(KalmanLocationService value) {
                    value.stop();
                }
            });
            super.onDestroy();
            // !! location manager/listener needs to be deallocated here to avoid mem leak !!

        }


        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            // This function should be used to return location data.
            startForeground(); // this could be moved to onCreate for optimization

            // broadcastLocationList();

            return START_NOT_STICKY;
        }

        private void startForeground() {
            Intent notificationIntent = new Intent(this, /**/MapRecordingActivity.class/**/); //must alter if this moves to a new activity
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            //NotificationCompat is for old versions, maybe update to Notification.builder? needs research
            startForeground(NOTIF_ID, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                    .setOngoing(true)
                    //.setSmallIcon(R.drawable.pototype_small_icon) //this is found in res/drawable... there is no icon so it gives an error
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("service running in background")
                    .setContentIntent(pendingIntent)
                    .build());
        }

        private void broadcastLocation() {
            if (!locationList.isEmpty()) {
                Intent sendLocation = new Intent();
                sendLocation.putParcelableArrayListExtra("LOCATION", locationList);
                sendLocation.setAction("GET_LOCATION_IN_BACKGROUND");
                sendBroadcast(sendLocation);
                //newLine = null;
                locationList = new ArrayList<LatLng>();


                // I believe reference to the old arraylist is attached to the broadcasted intent so no memory is
                // leaked here, but I'm more of a cpp guy so this needs a closer look
            }
        }

        @Override
        public void locationChanged(Location location) {
            currentsState = new MapStateManager(this, "CurrentSession");
            if (lastLocation == null) {
                lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                newLocation = new LatLng(0.0,0.0);
                locationList.add(lastLocation);
                locationList.add(newLocation);
                broadcastLocation();
            } else {
                newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                double temp = CalculateDistance(lastLocation.latitude, newLocation.latitude, lastLocation.longitude, newLocation.longitude);
                if(temp > 0.00075757576 ) {
                    locationList.add(lastLocation);
                    locationList.add(newLocation);
                    broadcastLocation();
                    lastLocation = newLocation;
               }

            }
        }
        public double CalculateDistance(double lat1, double lat2, double long1, double long2) {
            double theta = long1 - long2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return dist;
        }
    }
