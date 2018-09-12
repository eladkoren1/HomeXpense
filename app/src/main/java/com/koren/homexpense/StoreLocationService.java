package com.koren.homexpense;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.ExpensePlace;

import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class StoreLocationService extends Service implements LocationListener {

    LocationManager locationManager;
    Context context = this;
    boolean isInPlace = false;
    Uri alarmSound;
    NotificationCompat.Builder builder;
    HashMap<Integer,double[]> expensePlaceCoordinates = new HashMap<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int placeKey;

    public StoreLocationService() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 5000, 3,this);
    }

    @SuppressLint("LongLogTag")
    private void checkStore(Location location){

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("current latlng:", currentLatLng.toString());
        for (Map.Entry<Integer, double[]> expensePlace : expensePlaceCoordinates.entrySet()){

            double expensePlaceLatitudeDelta = ((Math.abs(currentLatLng.latitude - expensePlace.getValue()[0])));
            double expensePlaceLongitudeDelta = ((Math.abs(currentLatLng.longitude - expensePlace.getValue()[1])));

            /*if(expensePlace.getKey().equals(3)){
                Log.d("PLACE",expensePlace.getKey().toString());
                Log.d("expensePlaceLatitude",String.valueOf(expensePlace.getValue()[0]));
                Log.d("expensePlaceLongitue",String.valueOf(expensePlace.getValue()[1]));
            }*/

            if ((expensePlaceLatitudeDelta < 0.0001) && (expensePlaceLongitudeDelta < 0.0001)) {
                Log.d("in place:", "yes");
                Log.d("current latlng:", currentLatLng.toString());
                Log.d("expensePlaceLatitude",String.valueOf(expensePlace.getValue()[0]));
                Log.d("expensePlaceLongitue",String.valueOf(expensePlace.getValue()[1]));
                isInPlace = true;
                placeKey = expensePlace.getKey();

                Log.d("placeKey",String.valueOf(placeKey));

            }
            if (isInPlace) {
                double expensePlaceLatitudeDeltaInPlace = ((Math.abs(currentLatLng.latitude - expensePlaceCoordinates.get(placeKey)[0])));
                double expensePlaceLongitudeDeltaInPlace = ((Math.abs(currentLatLng.longitude - expensePlaceCoordinates.get(placeKey)[1])));
                if ((expensePlaceLatitudeDeltaInPlace > 0.0001) || (expensePlaceLongitudeDeltaInPlace > 0.0001)){
                    Log.d("left the place", "yes");
                    Log.d("current latlng", currentLatLng.toString());
                    Log.d("expensePlaceLatitude",String.valueOf(expensePlace.getValue()[0]));
                    Log.d("expensePlaceLongitue",String.valueOf(expensePlace.getValue()[1]));
                    notifyUser(placeKey);
                    isInPlace = false;
                }
            }
        }
    }


    private void notifyUser(int expensePlaceKey){

        Bitmap icon = BitmapFactory.decodeResource(getResources(),


                R.mipmap.ic_launcher_round);

        int NOTIFICATION_ID = 1;
        String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Configure the notification channel.



        // Create an Intent for the activity you want to start
        Intent addEntryIntent = new Intent(this, AddEntryActivity.class);
        addEntryIntent.putExtra("expensePlaceKey",expensePlaceKey);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(addEntryIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent addEntryPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(addEntryPendingIntent)
                .setContentTitle("HomeXpense content title")
                .setContentText("Store tracker content text")
                .setTicker("HomeXpense Ticker")
                //.setSound(alarmSound)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setChannelId(NOTIFICATION_CHANNEL_ID);

        //notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(final Location location) {
        DatabaseReference expensePlacesReference = database.getReference("ExpensePlaces");
        expensePlacesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot placeKey : dataSnapshot.getChildren()){
                    double lat = (Double) placeKey.child("placeCoordinates").child("latitude").getValue();
                    double lon = (Double) placeKey.child("placeCoordinates").child("longitude").getValue();
                    double latLong[] = {lat,lon};
                    expensePlaceCoordinates.put(Integer.parseInt(placeKey.getKey()),latLong);
                }
                checkStore(location);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.i("Status changed",provider.toString() +" "+ String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Log.i("enabled",provider.toString());
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Log.i("disabled",provider.toString());

    }
}
