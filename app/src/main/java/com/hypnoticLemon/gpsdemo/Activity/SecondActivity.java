package com.hypnoticLemon.gpsdemo.Activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypnoticLemon.gpsdemo.Receiver.LocationReceiver;
import com.hypnoticLemon.gpsdemo.Service.MyService;
import com.hypnoticLemon.gpsdemo.R;

public class SecondActivity extends AppCompatActivity implements LocationReceiver.Receiver {

    private Button btnStart, btnStop;
    private LocationReceiver mReceiver;
    public int STATUS_RUNNING = 0;
    private Context context;
    private final static int PERMISSION_CODE = 123;
    private final static int APPINFO_PERMISSION = 111;
    private final static int REASK_PERMISIION_CODE = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        context = SecondActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {

        }

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getBaseContext(), MyService.class));
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getBaseContext(), MyService.class).putExtra("mReceiver", mReceiver));
            }
        });

        mReceiver = new LocationReceiver(new Handler());
        mReceiver.setReceiver(this);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == STATUS_RUNNING) {

            String Latitude = resultData.getString("Latitude");
            String Longitude = resultData.getString("Longitude");
            String Address = resultData.getString("getLocation");

            Toast.makeText(context, "Latitude : " + Latitude + " |  Longitude : " + Longitude + "\n Address: " + Address, Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
            alertDialog.setMessage("Please Give Your Location Access");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION
                    }, REASK_PERMISIION_CODE);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            alertDialog.create();
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION,
            }, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REASK_PERMISIION_CODE: {
                if (checkPermissions()) {
                } else {
                    finish();
                }
                break;
            }
            case PERMISSION_CODE: {
                if (checkPermissions()) {
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(SecondActivity.this);
                            alert.setMessage("Please Give Your Location Access");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    openAppInfoPage();
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });
                            alert.create();
                            alert.show();
                        }
                    });
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void openAppInfoPage() {
        String packageName = getPackageName();
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivityForResult(intent, APPINFO_PERMISSION);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivityForResult(intent, APPINFO_PERMISSION);
        }
    }


    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
