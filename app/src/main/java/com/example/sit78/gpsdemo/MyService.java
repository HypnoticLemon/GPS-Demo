package com.example.sit78.gpsdemo;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MyService extends Service {

    private String TAG = "MyService";

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
}
