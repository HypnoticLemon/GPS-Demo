package com.hypnoticLemon.gpsdemo.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MyService extends Service {

    private ResultReceiver receiver;
    private String TAG = "MyService";
    public int STATUS_RUNNING = 0;
    public double Latitude, Longitude;
    CountDownTimer x;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            receiver = intent.getParcelableExtra("mReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        timer();
        return START_STICKY;
    }


    private void timer() {
        x = new CountDownTimer(30000, 10000) {

            public void onTick(long millisUntilFinished) {
                getLocation();
            }

            public void onFinish() {
                x.start();
            }
        }.start();
    }

    private void getLocation() {
        GPSTracker gpsTracker = new GPSTracker(getBaseContext());
        Location gpsLocation = gpsTracker.getLocation();
        if (gpsLocation != null) {


            Log.e(TAG, "getLocation: " + getCompleteAddressString(gpsLocation.getLatitude(), gpsLocation.getLongitude()));
            Log.e(TAG, "Latitude: " + gpsLocation.getLatitude());
            Log.e(TAG, "Longitude: " + gpsLocation.getLongitude());

            Bundle bundle = new Bundle();
            bundle.putString("Latitude", "" + gpsLocation.getLatitude());
            bundle.putString("getLocation", "" + getCompleteAddressString(gpsLocation.getLatitude(), gpsLocation.getLongitude()));
            bundle.putString("Longitude", "" + gpsLocation.getLongitude());

            if (receiver != null) {
                receiver.send(STATUS_RUNNING, bundle);
            }

        } else {
            Log.e(TAG, "getLocation: gpsLocation null");
        }
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                strAdd = strReturnedAddress.toString();

            } else {
                Log.e(TAG, "No Address returned!");
                Toast.makeText(this, "No Address returned!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot get Address!");
        } finally {
        }
        return strAdd;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        x.cancel();
        Toast.makeText(this, "Service Finished", Toast.LENGTH_LONG).show();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
