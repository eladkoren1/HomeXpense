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
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koren.homexpense.Classes.ExpensePlace;
import com.koren.homexpense.Classes.User;

import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class StoreLocationService extends Service implements LocationListener {

    LocationManager locationManager;
    Context context = this;
    HashMap<Integer,ExpensePlace> expensePlaces = new HashMap<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference expensePlacesReference = database.getReference("ExpensePlaces");

    String userUID;
    boolean isInPlace = false;
    int placeKey=-1;

    public StoreLocationService() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 5000, 3,this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userUID = (String)intent.getExtras().get("userUID");
        return START_NOT_STICKY;
    }


    @SuppressLint("LongLogTag")
    private void checkStore(Location location){
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("current latlng:", currentLatLng.toString());

        if (!isInPlace) {
            for (Map.Entry<Integer, ExpensePlace> expensePlace : expensePlaces.entrySet()) {

                double expensePlaceLatitudeDelta = ((Math.abs(currentLatLng.latitude - expensePlace.getValue().getPlaceCoordinates().latitude)));
                double expensePlaceLongitudeDelta = ((Math.abs(currentLatLng.longitude - expensePlace.getValue().getPlaceCoordinates().longitude)));


                if ((expensePlaceLatitudeDelta < 0.0001) && (expensePlaceLongitudeDelta < 0.0001)) {
                    Log.d("in place:", "yes");
                    Log.d("expensePlaceLatitude", String.valueOf(expensePlace.getValue().getPlaceCoordinates().latitude));
                    Log.d("expensePlaceLongitue", String.valueOf(expensePlace.getValue().getPlaceCoordinates().longitude));
                    isInPlace = true;
                    placeKey = expensePlace.getKey();
                    Log.d("placeKey", String.valueOf(placeKey));
                }
            }
        }


        if (isInPlace) {
            double expensePlaceLatitudeDeltaInPlace = ((Math.abs(currentLatLng.latitude - expensePlaces.get(placeKey).getPlaceCoordinates().latitude)));
            double expensePlaceLongitudeDeltaInPlace = ((Math.abs(currentLatLng.longitude - expensePlaces.get(placeKey).getPlaceCoordinates().longitude)));
            if ((expensePlaceLatitudeDeltaInPlace > 0.0002) || (expensePlaceLongitudeDeltaInPlace > 0.0002)){
                Log.d("left the place", "yes");
                Log.d("expensePlaceLatitude", String.valueOf(expensePlaces.get(placeKey).getPlaceCoordinates().latitude));
                Log.d("expensePlaceLongitue", String.valueOf(expensePlaces.get(placeKey).getPlaceCoordinates().longitude));
                notifyUser(placeKey);
                isInPlace = false;
                placeKey=-1;
            }
        }
    }


    @SuppressLint("NewApi")
    private void notifyUser(int expensePlaceKey){


        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int NOTIFICATION_ID = 1;
        String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

        NotificationCompat.Builder notificationBuilder;
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
        addEntryIntent.putExtra("userUID",userUID);
        addEntryIntent.putExtra("expensePlaceKey",expensePlaceKey);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(addEntryIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent addEntryPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentIntent(addEntryPendingIntent)
                .setContentTitle("האם יצאת עכשיו מ"+expensePlaces.get(expensePlaceKey).getPlaceName()+"?")
                .setContentText("לחץ לתיעוד הוצאה")
                //.setSound(alarmSound)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setChannelId(NOTIFICATION_CHANNEL_ID);

        Notification addEntryNotification = notificationBuilder.build();

        StatusBarNotification[] statusBarNotification = notificationManager.getActiveNotifications();
        if ((statusBarNotification.length==0)){
            notificationManager.notify(NOTIFICATION_ID, addEntryNotification);
        }
        else if (!(notificationManager.getActiveNotifications()[0].getId()==NOTIFICATION_ID)){
            notificationManager.notify(NOTIFICATION_ID, addEntryNotification);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("Location","changed");
        if (expensePlaces.isEmpty()) {
            expensePlacesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot placeKey : dataSnapshot.getChildren()) {
                        ExpensePlace expensePlace = new ExpensePlace();
                        expensePlace.setPlaceName(placeKey.child("placeName").getValue().toString());
                        expensePlace.setPlaceAddress(placeKey.child("placeAddress").getValue().toString());
                        expensePlace.setExpenseType(placeKey.child("expenseType").getValue().toString());
                        double lat = (Double) placeKey.child("placeCoordinates").child("latitude").getValue();
                        double lon = (Double) placeKey.child("placeCoordinates").child("longitude").getValue();
                        expensePlace.setPlaceCoordinates(new LatLng(lat, lon));
                        expensePlaces.put(Integer.parseInt(placeKey.getKey()), expensePlace);
                        Log.d("expensePlace",expensePlace.getPlaceName());

                    }
                    checkStore(location);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else checkStore(location);

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
