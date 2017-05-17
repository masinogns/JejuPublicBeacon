package com.example.masinogns.beacon;

import android.app.Application;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    private BeaconManager beaconManager;
    private Region region;

    private TextView tvId;

    private boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvId = (TextView)findViewById(R.id.tvId);

        beaconManager = new BeaconManager(this);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()){
                    Beacon nearestBeacon = list.get(0);
                    Log.d("airport", "nearest plcase : "+nearestBeacon.getRssi());
                    tvId.setText(nearestBeacon.getRssi() + "");

                    if (!isConnected && nearestBeacon.getRssi() > -70){
                        isConnected = true;

                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog .setTitle("알림")
                                .setMessage("비콘 연결")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create().show();
                    }
                    else if(nearestBeacon.getRssi() < -70){
                        Toast.makeText(MainActivity.this, "연결종료", Toast.LENGTH_SHORT).show();
                        isConnected = false;
                    }
                }
            }
        });

        region = new Region("ranged region",
                UUID.fromString("e2558b02-4d84-ece3-9d4a-61e112a971ca"),
                0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(region);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
